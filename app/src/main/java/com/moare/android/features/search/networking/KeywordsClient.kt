package com.moare.android.features.search.networking

import com.moare.android.core.networking.ApiHelper
import com.moare.android.features.search.models.KeywordInfo

class KeywordsClient(
    private val apiHelper: ApiHelper
) {
    suspend fun fetchTrendingKeywords(): List<KeywordInfo> {
        return apiHelper.keywordsApi.fetchTrendingKeywords()
    }
}