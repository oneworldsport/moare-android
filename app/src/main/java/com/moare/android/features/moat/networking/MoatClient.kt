package com.moare.android.features.moat.networking

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.moare.android.core.networking.ApiHelper
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

    suspend fun createMoat(body: MoatCreateRequest): MoatResponse {
        return apiHelper.moatApi.createMoat(accessTokenHeader() ,body)
    }

    suspend fun updateMoat(moatId: String, body: MoatUpdateRequest): MoatResponse {
        return apiHelper.moatApi.updateMoat(accessTokenHeader() ,moatId, body)
    }

    suspend fun deleteMoat(moatId: String): MoatResponse {
        return apiHelper.moatApi.deleteMoat(accessTokenHeader() ,moatId)
    }

    suspend fun fetchMoatDetail(moatId: String): MoatDetailResponse {
        return apiHelper.moatApi.getMoatDetail(accessTokenHeader() ,moatId)
    }

    suspend fun fetchTrendingMoats(body: MoatListRequest): MoatListResponse {
        return apiHelper.moatApi.getTrendingMoats(accessTokenHeader() ,body)
    }

    suspend fun fetchUserMoats(body: MoatListRequest): MoatListResponse {
        return apiHelper.moatApi.getUserMoats(accessTokenHeader() ,body)
    }

    suspend fun createFire(body: FireCreateRequest): FireResponse {
        return apiHelper.moatApi.createFire(accessTokenHeader() ,body)
    }

    suspend fun deleteFire(moatId: String): SimpleResponse {
        return apiHelper.moatApi.deleteFire(accessTokenHeader() ,moatId)
    }

    suspend fun checkFire(moatId: String): Boolean {
        return apiHelper.moatApi.checkFire(accessTokenHeader() ,moatId)
    }
}