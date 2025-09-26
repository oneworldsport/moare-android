package com.moare.android.features.userprofile.networking

import com.moare.android.features.userprofile.models.UserProfileResponse
import com.moare.android.features.userprofile.models.UserProfileUpdateRequest
import com.moare.android.features.userprofile.models.UserProfileWithMoatsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH

interface UserProfileApi {
    @GET("users/me")
    suspend fun getUserProfile(
        @Header("Authorization") token: String? = null
    ): UserProfileWithMoatsResponse

    @PATCH("users/me")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String? = null,
        @Body body: UserProfileUpdateRequest
    ): UserProfileResponse
}