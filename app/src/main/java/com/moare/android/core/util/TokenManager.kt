package com.moare.android.core.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val crypto: CryptoManager
) {
    private val ACCESS_TOKEN_ENC = stringPreferencesKey("accessToken_enc")
    private val ACCESS_TOKEN_IV = stringPreferencesKey("accessToken_iv")

    private val REFRESH_TOKEN_ENC = stringPreferencesKey("refreshToken_enc")
    private val REFRESH_TOKEN_IV = stringPreferencesKey("refreshToken_iv")

    private val ID_TOKEN_ENC = stringPreferencesKey("idToken_enc")
    private val ID_TOKEN_IV = stringPreferencesKey("idToken_iv")

    // Flow는 "평문 토큰"을 내보내도록 유지 (상위 코드 유지 목적)
    val accessTokenFlow: Flow<String?> = dataStore.data.map { prefs ->
        decryptOrNull(prefs[ACCESS_TOKEN_ENC], prefs[ACCESS_TOKEN_IV])
    }

    suspend fun getAccessToken(): String? =
        dataStore.data.map { prefs -> decryptOrNull(prefs[ACCESS_TOKEN_ENC], prefs[ACCESS_TOKEN_IV]) }.first()

    suspend fun getRefreshToken(): String? =
        dataStore.data.map { prefs -> decryptOrNull(prefs[REFRESH_TOKEN_ENC], prefs[REFRESH_TOKEN_IV]) }.first()

    suspend fun getIdToken(): String? =
        dataStore.data.map { prefs -> decryptOrNull(prefs[ID_TOKEN_ENC], prefs[ID_TOKEN_IV]) }.first()

    suspend fun updateTokens(accessToken: String, refreshToken: String?, idToken: String?) {
        dataStore.edit { prefs ->
            putEncrypted(prefs, accessToken, ACCESS_TOKEN_ENC, ACCESS_TOKEN_IV)

            refreshToken?.let { putEncrypted(prefs, it, REFRESH_TOKEN_ENC, REFRESH_TOKEN_IV) }
                ?: run {
                    // null이면 삭제할지 유지할지 정책 결정. 보통은 삭제 권장.
                    prefs.remove(REFRESH_TOKEN_ENC); prefs.remove(REFRESH_TOKEN_IV)
                }

            idToken?.let { putEncrypted(prefs, it, ID_TOKEN_ENC, ID_TOKEN_IV) }
                ?: run {
                    prefs.remove(ID_TOKEN_ENC); prefs.remove(ID_TOKEN_IV)
                }
        }
    }

    suspend fun clearTokens() {
        dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN_ENC); prefs.remove(ACCESS_TOKEN_IV)
            prefs.remove(REFRESH_TOKEN_ENC); prefs.remove(REFRESH_TOKEN_IV)
            prefs.remove(ID_TOKEN_ENC); prefs.remove(ID_TOKEN_IV)
        }
    }

    private fun putEncrypted(
        prefs: MutablePreferences,
        token: String,
        encKey: Preferences.Key<String>,
        ivKey: Preferences.Key<String>
    ) {
        val encrypted = crypto.encrypt(token)
        prefs[encKey] = encrypted.cipherTextB64
        prefs[ivKey] = encrypted.ivB64
    }

    private fun decryptOrNull(enc: String?, iv: String?): String? {
        if (enc.isNullOrBlank() || iv.isNullOrBlank()) return null
        return try {
            crypto.decrypt(enc, iv)
        } catch (e: Exception) {
            // 키 유실/복구/변조 등으로 복호화 실패 시
            null
        }
    }
}