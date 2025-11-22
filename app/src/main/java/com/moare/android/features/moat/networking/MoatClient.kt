package com.moare.android.features.moat.networking

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.moare.android.core.di.Authenticated
import com.moare.android.core.networking.ApiHelper
import com.moare.android.core.networking.apiCall
import com.moare.android.core.util.TokenManager
import com.moare.android.features.moat.models.FireCreateRequest
import com.moare.android.features.moat.models.FireResponse
import com.moare.android.features.moat.models.MoatCreateRequest
import com.moare.android.features.moat.models.MoatDetailResponse
import com.moare.android.features.moat.models.MoatListRequest
import com.moare.android.features.moat.models.MoatListResponse
import com.moare.android.features.moat.models.MoatResponse
import com.moare.android.features.moat.models.MoatUpdateRequest
import com.moare.android.features.sign.models.SimpleResponse
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoatClient @Inject constructor(
    private val tokenManager: TokenManager,
    @Authenticated private val protectedMoatApi: MoatApi
) {
    suspend fun createMoat(body: MoatCreateRequest): MoatResponse =
        apiCall {
            val accessToken = tokenManager.getAccessToken()
            protectedMoatApi.createMoat(accessToken ,body)
        }

    suspend fun updateMoat(moatId: String, body: MoatUpdateRequest): MoatResponse =
        apiCall {
            val accessToken = tokenManager.getAccessToken()
            protectedMoatApi.updateMoat(accessToken ,moatId, body)
        }

    suspend fun deleteMoat(moatId: String): MoatResponse =
        apiCall {
            val accessToken = tokenManager.getAccessToken()
            protectedMoatApi.deleteMoat(accessToken, moatId)
        }

    suspend fun fetchMoatDetail(moatId: String): MoatDetailResponse =
        apiCall {
            val accessToken = tokenManager.getAccessToken()
            protectedMoatApi.getMoatDetail(accessToken, moatId)
        }

    suspend fun fetchTrendingMoats(body: MoatListRequest): MoatListResponse =
        apiCall {
            val accessToken = tokenManager.getAccessToken()
            protectedMoatApi.getTrendingMoats(accessToken, body)
        }

    suspend fun fetchUserMoats(body: MoatListRequest): MoatListResponse =
        apiCall {
            val accessToken = tokenManager.getAccessToken()
            protectedMoatApi.getUserMoats(accessToken, body)
        }

    suspend fun createFire(body: FireCreateRequest): FireResponse =
        apiCall {
            val accessToken = tokenManager.getAccessToken()
            protectedMoatApi.createFire(accessToken, body)
        }

    suspend fun deleteFire(moatId: String): SimpleResponse =
        apiCall {
            val accessToken = tokenManager.getAccessToken()
            protectedMoatApi.deleteFire(accessToken, moatId)
        }

    suspend fun checkFire(moatId: String): Boolean =
        apiCall {
            val accessToken = tokenManager.getAccessToken()
            protectedMoatApi.checkFire(accessToken, moatId)
        }
}