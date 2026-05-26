package com.moare.android.features.search.domain.repository

import com.moare.android.features.search.models.KeywordInfo

interface AutoCompleteRepository {
    fun search(query: String): List<String>
    fun keywordInfo(keyword: String): KeywordInfo?
}