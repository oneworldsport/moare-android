package com.moare.android.core.networking.apiendpoint

import com.moare.android.features.sign.models.AuthResponse
import com.moare.android.features.sign.models.ConfirmAuthRequest
import com.moare.android.features.sign.models.SignUpCompleteRequest
import com.moare.android.features.sign.models.SignUpInitiateRequest
import com.moare.android.features.sign.models.SignUpVerificationRequest
import com.moare.android.features.sign.models.StartAuthRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login/start")
    suspend fun startLoginAuth(
        @Body body: StartAuthRequest
    ) : Response<Map<String, String>>

    @POST("auth/login/confirm")
    suspend fun confirmLoginAuth(
        @Body body: ConfirmAuthRequest
    ) : Response<AuthResponse>

    @POST("auth/signup/initiate")
    suspend fun initiateSignUp(
        @Body body: SignUpInitiateRequest
    ) : Response<Map<String, String>>

    @POST("auth/signup/verify")
    suspend fun verifySignUpOtp(
        @Body body: SignUpVerificationRequest
    ) : Response<Map<String, String>>

    @POST("auth/signup/complete")
    suspend fun completeSignUp(
        @Body body: SignUpCompleteRequest
    ) : Response<Map<String, String>>
}