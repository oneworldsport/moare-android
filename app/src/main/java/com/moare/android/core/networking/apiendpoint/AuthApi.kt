package com.moare.android.core.networking.apiendpoint

import com.moare.android.features.sign.models.AuthResponse
import com.moare.android.features.sign.models.AuthSessionResponse
import com.moare.android.features.sign.models.ConfirmAuthRequest
import com.moare.android.features.sign.models.UserHandleReserveRequest
import com.moare.android.features.sign.models.SignUpCompleteRequest
import com.moare.android.features.sign.models.SignUpInitiateRequest
import com.moare.android.features.sign.models.SignUpVerificationRequest
import com.moare.android.features.sign.models.SimpleResponse
import com.moare.android.features.sign.models.StartAuthRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface AuthApi {
    @POST("auth/login/start")
    suspend fun startLoginAuth(
        @Body body: StartAuthRequest
    ) : Response<AuthSessionResponse>

    @POST("auth/login/confirm")
    suspend fun confirmLoginAuth(
        @Body body: ConfirmAuthRequest
    ) : Response<AuthResponse>

    @POST("auth/signup/initiate")
    suspend fun initiateSignUp(
        @Body body: SignUpInitiateRequest
    ) : Response<SimpleResponse>

    @POST("auth/signup/verify")
    suspend fun verifySignUpOtp(
        @Body body: SignUpVerificationRequest
    ) : Response<AuthResponse>

    @POST("auth/signup/complete")
    suspend fun completeSignUp(
        @Body body: SignUpCompleteRequest
    ) : Response<SimpleResponse>

    @GET("auth/user-handle/check")
    suspend fun checkUserHandle(
        @Query("userHandle") userHandle: String
    ) : Response<SimpleResponse>

    @PUT("auth/userHandle/reserve")
    suspend fun reserveUserHandle(
        @Body body:UserHandleReserveRequest
    ) : Response<SimpleResponse>
}