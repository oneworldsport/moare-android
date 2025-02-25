package com.moare.android.core.networking.apiendpoint

import com.moare.android.features.search.models.DataModel
import com.moare.android.features.search.models.TrendingKeyword
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
        @Body request: TrendingKeyword
    ): ResponseBody

    @GET("search/schedule")
    suspend fun getLeagueSchedule(
        @Query("leagueId") leagueId: Int,
        @Query("yearMonth") yearMonth: String
    ): ResponseBody

    @GET("search/game")
    suspend fun fetchGameInfo(
        @Query("category") category: String,
        @Query("date") date: String,
        @Query("leagueId") leagueId: Int,
        @Query("fixtureId") fixtureId: Int,
    ): ResponseBody
}