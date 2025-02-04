package com.moare.android.core.networking.apiendpoint

import com.moare.android.features.search.models.DataModel
import okhttp3.ResponseBody
import retrofit2.http.GET
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

    @GET("search/schedule")
    suspend fun getLeagueSchedule(
        @Query("leagueId") leagueId: String,
        @Query("yearMonth") yearMonth: String
    ): ResponseBody
}