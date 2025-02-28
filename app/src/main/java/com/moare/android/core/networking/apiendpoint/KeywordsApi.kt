package com.moare.android.core.networking.apiendpoint

import com.moare.android.features.search.models.KeywordInfo
import retrofit2.http.GET

interface KeywordsApi {
    @GET("keywords/trending")
    suspend fun fetchTrendingKeywords(
    ): List<KeywordInfo>
}