package com.moare.android.features.moat.networking

import com.moare.android.features.moat.models.MoatCreateRequest
import com.moare.android.features.moat.models.MoatDetailResponse
import com.moare.android.features.moat.models.MoatListRequest
import com.moare.android.features.moat.models.MoatListResponse
import com.moare.android.features.moat.models.MoatResponse
import com.moare.android.features.moat.models.MoatUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface MoatApi {
    @POST("moats")
    suspend fun createMoat(
        @Header("Authorization") token: String? = null,
        @Body body: MoatCreateRequest
    ): MoatResponse

    @PATCH("moats/{moatId}")
    suspend fun updateMoat(
        @Header("Authorization") token: String? = null,
        @Path("moatId") moatId: String,
        @Body body: MoatUpdateRequest
    ): MoatResponse

    @DELETE("moats/{moatId}")
    suspend fun deleteMoat(
        @Header("Authorization") token: String? = null,
        @Path("moatId") moatId: String
    ): MoatResponse

    @GET("moats/{moatId}")
    suspend fun getMoatDetail(
        @Header("Authorization") token: String? = null,
        @Path("moatId") moatId: String
    ): MoatDetailResponse

    @POST("moats/timeline")
    suspend fun getTimelineMoats(
        @Header("Authorization") token: String? = null,
        @Body body: MoatListRequest
    ): MoatListResponse

    @POST("moats/user")
    suspend fun getUserMoats(
        @Header("Authorization") token: String? = null,
        @Body body: MoatListRequest
    ): MoatListResponse
}