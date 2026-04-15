package com.moare.android.core.networking.apiendpoint

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.KeywordInfo
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SearchApi {
    @GET("search")
    suspend fun saveQuery(
        @Query("word") query: String
    ): Any

    @GET("search")
    suspend fun searchByQuery(
        @Query("query") query: String
    ): ResponseBody

    @POST("search/keyword")
    suspend fun searchByKeyword(
        @Body request: KeywordInfo,
        @Query("season") season: Int?
    ): ResponseBody

    @POST("search/schedule")
    suspend fun getLeagueSchedule(
        @Body entity: EntityInfo,
        @Query("season") season: Int,
        @Query("yearMonth") yearMonth: String?,
        @Query("day") day: Int?
    ): ResponseBody

    @GET("search/id")
    suspend fun searchById(
        @Query("season") season: Int,
        @Query("category") category: String,
        @Query("date") date: String?,
        @Query("dataType") dataType: String,
        @Query("leagueId") leagueId: Int,
        @Query("id") id: String,
    ): ResponseBody
}