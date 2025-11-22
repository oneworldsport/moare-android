package com.moare.android.features.userprofile.networking

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.moare.android.core.di.Authenticated
import com.moare.android.core.networking.ApiHelper
import com.moare.android.core.networking.apiCall
import com.moare.android.core.util.TokenManager
import com.moare.android.features.userprofile.models.UserProfileResponse
import com.moare.android.features.userprofile.models.UserProfileUpdateRequest
import com.moare.android.features.userprofile.models.UserProfileWithMoatsResponse
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileClient @Inject constructor(
    private val tokenManager: TokenManager,
    @Authenticated private val protectedUserProfileApi: UserProfileApi
) {
    suspend fun fetchUserProfile(): UserProfileWithMoatsResponse =
        apiCall {
            val accessToken = tokenManager.getAccessToken()
            protectedUserProfileApi.getUserProfile(accessToken)
        }

    suspend fun updateUserProfile(body: UserProfileUpdateRequest): UserProfileResponse =
        apiCall {
            val accessToken = tokenManager.getAccessToken()
            protectedUserProfileApi.updateUserProfile(accessToken, body)
        }
}