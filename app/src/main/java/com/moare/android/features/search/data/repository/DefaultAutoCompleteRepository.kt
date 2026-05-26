package com.moare.android.features.search.data.repository

import com.moare.android.core.util.Trie
import com.moare.android.features.search.domain.repository.AutoCompleteRepository
import com.moare.android.features.search.models.KeywordInfo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class DefaultAutoCompleteRepository @Inject constructor(
    private val trieDeferred: CompletableDeferred<Pair<Trie, List<KeywordInfo>>>
) : AutoCompleteRepository {
    // TODO: DefaultTrendingKeywordsRepository처럼 cached를 사용하는 방식으로 refactoring 필요
    private val trie: Trie by lazy {
        runBlocking { trieDeferred.await().first }
    }

    private val keywordInfoMap: Map<String, KeywordInfo> by lazy {
        runBlocking {
            trieDeferred.await().second.associateBy { it.keyword }
        }
    }

    override fun search(query: String): List<String> {
        return trie.search(query)
    }

    override fun keywordInfo(keyword: String): KeywordInfo? {
        return keywordInfoMap[keyword]
    }
}