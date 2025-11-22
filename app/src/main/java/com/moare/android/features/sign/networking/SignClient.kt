package com.moare.android.features.sign.networking

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.moare.android.core.di.Authenticated
import com.moare.android.core.di.NoAuth
import com.moare.android.core.networking.ApiHelper
import com.moare.android.core.networking.apiCall
import com.moare.android.core.networking.apiendpoint.AuthApi
import com.moare.android.core.util.TokenManager
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
    private val tokenManager: TokenManager,
    @NoAuth private val authApi: AuthApi,
    @Authenticated private val protectedAuthApi: AuthApi
) {
    suspend fun bootstrapSession(): SimpleResponse =
        apiCall {
            val accessToken = tokenManager.getAccessToken()
            protectedAuthApi.bootstrapSession(accessToken)
        }

    suspend fun startLoginAuth(body: StartAuthRequest): AuthSessionResponse {
        return authApi.startLoginAuth(body)
    }

    suspend fun confirmLoginAuth(body: ConfirmAuthRequest): AuthTokenResponse {
        return authApi.confirmLoginAuth(body)
    }

    suspend fun initiateSignUp(body: SignUpInitiateRequest): SimpleResponse {
        return authApi.initiateSignUp(body)
    }

    suspend fun verifySignUpOtp(body: SignUpVerificationRequest): SimpleResponse {
        return authApi.verifySignUpOtp(body)
    }

    suspend fun completeSignUp(body: SignUpCompleteRequest): SimpleResponse {
        return authApi.completeSignUp(body)
    }

    suspend fun checkUserHandle(userHandle: String): SimpleResponse {
        return authApi.checkUserHandle(userHandle)
    }

    suspend fun reserveUserHandle(userHandle: String): SimpleResponse {
        val body = UserHandleReserveRequest(userHandle)
        return authApi.reserveUserHandle(body)
    }
}