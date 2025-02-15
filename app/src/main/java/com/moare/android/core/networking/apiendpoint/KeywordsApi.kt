package com.moare.android.core.networking.apiendpoint

import com.moare.android.features.search.models.TrendingKeyword
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface KeywordsApi {
    @GET("keywords/trending")
    suspend fun fetchTrendingKeywords(
    ): List<TrendingKeyword>
}