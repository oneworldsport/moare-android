package com.moare.android.core.networking.apiendpoint

import com.moare.android.features.search.models.KeywordInfo
import com.moare.android.features.search.models.LeagueKeywords
import retrofit2.http.GET

interface KeywordsApi {
    @GET("keywords/trending")
    suspend fun fetchTrendingKeywords(
    ): List<KeywordInfo>

    @GET("keywords/league")
    suspend fun fetchLeagueKeywords(
    ): LeagueKeywords
}