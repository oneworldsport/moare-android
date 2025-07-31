package com.moare.android.features.sign.networking

import com.moare.android.core.networking.ApiHelper
import com.moare.android.features.sign.models.AuthMethod
import com.moare.android.features.sign.models.AuthResponse
import com.moare.android.features.sign.models.AuthResponseType
import com.moare.android.features.sign.models.AuthTokenData
import com.moare.android.features.sign.models.ConfirmAuthRequest
import com.moare.android.features.sign.models.SignUpCompleteRequest
import com.moare.android.features.sign.models.SignUpInitiateRequest
import com.moare.android.features.sign.models.SignUpVerificationRequest
import com.moare.android.features.sign.models.StartAuthRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

class SignClient(
    private val apiHelper: ApiHelper
) {
    suspend fun startLoginAuth(body: StartAuthRequest) {
        apiHelper.authApi.startLoginAuth(body)
    }

    suspend fun confirmLoginAuth(body: ConfirmAuthRequest): Any? {
        val response = apiHelper.authApi.confirmLoginAuth(body)

        if (response.isSuccessful) {
            val responseBody = response.body()

            responseBody?.type?.let { type ->
                val json = responseBody.data

                when (type) {
                    AuthResponseType.SUCCESS -> {
                        json?.let {
                            return Json.decodeFromJsonElement<AuthTokenData>(json)
                        }
                    }
                    AuthResponseType.RETRY -> {
                        json?.let {
                            return Json.decodeFromJsonElement<Map<String, String>>(json)
                        }
                    }
                    else -> return type
                }
            }
        }

        return  null
    }

    suspend fun initiateSignUp(body: SignUpInitiateRequest) {
        apiHelper.authApi.initiateSignUp(body)
    }

    suspend fun verifySignUpOtp(body: SignUpVerificationRequest) {
        apiHelper.authApi.verifySignUpOtp(body)
    }

    suspend fun completeSignUp(body: SignUpCompleteRequest) {
        apiHelper.authApi.completeSignUp(body)
    }
}