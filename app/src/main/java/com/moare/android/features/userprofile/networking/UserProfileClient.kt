package com.moare.android.features.userprofile.networking

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.moare.android.core.networking.ApiHelper
import com.moare.android.features.userprofile.models.UserProfileResponse
import com.moare.android.features.userprofile.models.UserProfileUpdateRequest
import com.moare.android.features.userprofile.models.UserProfileWithMoatsResponse
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserProfileClient(
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

    suspend fun fetchUserProfile(): UserProfileWithMoatsResponse {
        return apiHelper.userProfileApi.getUserProfile(accessTokenHeader())
    }

    suspend fun updateUserProfile(body: UserProfileUpdateRequest): UserProfileResponse {
        return apiHelper.userProfileApi.updateUserProfile(accessTokenHeader(), body)
    }
}