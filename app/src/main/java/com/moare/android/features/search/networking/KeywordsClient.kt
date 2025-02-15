package com.moare.android.features.search.networking

import com.moare.android.core.networking.ApiHelper
import com.moare.android.features.search.models.TrendingKeyword

class KeywordsClient(
    private val apiHelper: ApiHelper
) {
    suspend fun fetchTrendingKeywords(): List<TrendingKeyword> {
        return apiHelper.keywordsApi.fetchTrendingKeywords()
    }
}