package com.moare.android.features.search.data.repository

import com.moare.android.features.search.domain.repository.TrendingKeywordsRepository
import com.moare.android.features.search.models.KeywordInfo
import com.moare.android.features.search.models.TrendingKeywords
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Inject

class DefaultTrendingKeywordsRepository @Inject constructor(
    private val trendingKeywordsDeferred: CompletableDeferred<TrendingKeywords>
) : TrendingKeywordsRepository {
    private var cachedKeywords: List<KeywordInfo>? = null
    private var cachedKeywordInfoMap: Map<String, KeywordInfo>? = null

    private suspend fun getKeywordInfos(): List<KeywordInfo> {
        return cachedKeywords ?: trendingKeywordsDeferred
            .await()
            .keywords
            .also { keywords ->
                cachedKeywords = keywords
                cachedKeywordInfoMap = keywords.associateBy { it.keyword }
            }
    }

    override suspend fun keywords(): List<String> {
        return getKeywordInfos().map { it.keyword }
    }

    override suspend fun keywordInfo(keyword: String): KeywordInfo? {
        val map = cachedKeywordInfoMap ?: getKeywordInfos()
            .associateBy { it.keyword }
            .also { cachedKeywordInfoMap = it }

        return map[keyword]
    }
}