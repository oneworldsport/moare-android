package com.moare.android.core.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val ACCESS_TOKEN = stringPreferencesKey("accessToken")
    private val REFRESH_TOKEN = stringPreferencesKey("refreshToken")
    private val ID_TOKEN = stringPreferencesKey("idToken")

    val accessTokenFlow: Flow<String?> = dataStore.data.map { it[ACCESS_TOKEN] }

    suspend fun getAccessToken(): String? = dataStore.data.map { it[ACCESS_TOKEN] }.firstOrNull()

    suspend fun getRefreshToken(): String? = dataStore.data.map { it[REFRESH_TOKEN] }.firstOrNull()

    suspend fun getIdToken(): String? = dataStore.data.map { it[ID_TOKEN] }.firstOrNull()

    suspend fun updateTokens(accessToken: String, refreshToken: String?, idToken: String?) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
            refreshToken?.let { prefs[REFRESH_TOKEN] = it }
            idToken?.let { prefs[ID_TOKEN] = it }
        }
    }

    suspend fun clearTokens() {
        dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN)
            prefs.remove(REFRESH_TOKEN)
            prefs.remove(ID_TOKEN)
        }
    }
}