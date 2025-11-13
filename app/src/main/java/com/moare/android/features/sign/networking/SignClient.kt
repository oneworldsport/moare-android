package com.moare.android.features.sign.networking

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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

class SignClient(
    private val apiHelper: ApiHelper
) {
    suspend fun startLoginAuth(body: StartAuthRequest): AuthSessionResponse? {
        val response = apiHelper.authApi.startLoginAuth(body)
        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }

    suspend fun confirmLoginAuth(body: ConfirmAuthRequest): AuthTokenResponse? {
        val response = apiHelper.authApi.confirmLoginAuth(body)
        if (response.isSuccessful) {
            return response.body()
        }

        return  null
    }

    suspend fun initiateSignUp(body: SignUpInitiateRequest): SimpleResponse? {
        val response = apiHelper.authApi.initiateSignUp(body)
        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }

    suspend fun verifySignUpOtp(body: SignUpVerificationRequest): SimpleResponse? {
        val response = apiHelper.authApi.verifySignUpOtp(body)
        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }

    suspend fun completeSignUp(body: SignUpCompleteRequest): SimpleResponse? {
        val response = apiHelper.authApi.completeSignUp(body)
        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }

    suspend fun checkUserHandle(userHandle: String): SimpleResponse? {
        val response = apiHelper.authApi.checkUserHandle(userHandle)
        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }

    suspend fun reserveUserHandle(userHandle: String): SimpleResponse? {
        // TODO: request model은 어느 레이어에서 만드는게 나을까? 중간에 레이어가 하나 더 있어야하나?
        val body = UserHandleReserveRequest(userHandle)
        val response = apiHelper.authApi.reserveUserHandle(body)
        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }
}