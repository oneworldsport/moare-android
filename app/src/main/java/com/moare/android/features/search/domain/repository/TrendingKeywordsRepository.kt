package com.moare.android.features.search.domain.repository

import com.moare.android.features.search.models.KeywordInfo

interface TrendingKeywordsRepository {
    suspend fun keywords(): List<String>
    suspend fun keywordInfo(keyword: String): KeywordInfo?
}