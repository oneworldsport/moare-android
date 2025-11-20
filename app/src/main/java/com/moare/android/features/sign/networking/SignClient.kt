package com.moare.android.features.sign.networking

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.moare.android.core.networking.ApiHelper
import com.moare.android.features.sign.models.AuthResponse
import com.moare.android.features.sign.models.AuthResponseType
import com.moare.android.features.sign.models.AuthSessionResponse
import com.moare.android.features.sign.models.AuthTokenResponse
import com.moare.android.features.sign.models.ConfirmAuthRequest
import com.moare.android.features.sign.models.UserHandleReserveRequest
import com.moare.android.features.sign.models.SignUpCompleteRequest
import com.moare.android.features.sign.models.SignUpInitiateRequest
import com.moare.android.features.sign.models.SignUpVerificationRequest
import com.moare.android.features.sign.models.SimpleResponse
import com.moare.android.features.sign.models.StartAuthRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignClient @Inject constructor(
    private val apiHelper: ApiHelper,
    private val dataStore: DataStore<Preferences>
) {
    suspend fun accessTokenHeader(): String? =
        try {
            dataStore.data
                .map { it[stringPreferencesKey("accessToken")] }
                .firstOrNull()
                ?.takeIf { it.isNotBlank() }
                ?.let { "Bearer $it" }
        } catch (_: IOException) { null }

    suspend fun bootstrapSession(): SimpleResponse {
        return apiHelper.authApi.bootstrapSession(accessTokenHeader())
    }

    suspend fun startLoginAuth(body: StartAuthRequest): AuthSessionResponse {
        return apiHelper.authApi.startLoginAuth(body)
    }

    suspend fun confirmLoginAuth(body: ConfirmAuthRequest): AuthTokenResponse {
        return apiHelper.authApi.confirmLoginAuth(body)
    }

    suspend fun initiateSignUp(body: SignUpInitiateRequest): SimpleResponse {
        return apiHelper.authApi.initiateSignUp(body)
    }

    suspend fun verifySignUpOtp(body: SignUpVerificationRequest): SimpleResponse {
        return apiHelper.authApi.verifySignUpOtp(body)
    }

    suspend fun completeSignUp(body: SignUpCompleteRequest): SimpleResponse {
        return apiHelper.authApi.completeSignUp(body)
    }

    suspend fun checkUserHandle(userHandle: String): SimpleResponse {
        return apiHelper.authApi.checkUserHandle(userHandle)
    }

    suspend fun reserveUserHandle(userHandle: String): SimpleResponse {
        val body = UserHandleReserveRequest(userHandle)
        return apiHelper.authApi.reserveUserHandle(body)
    }
}