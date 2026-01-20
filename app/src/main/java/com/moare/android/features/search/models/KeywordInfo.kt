package com.moare.android.features.search.models

import kotlinx.serialization.Serializable

@Serializable
data class KeywordInfo(
    var keyword: String, // searching keyword
    var weight: Int? = null,
    var keywords: List<Keyword>? = null, // keyword list that are in searching keyword
    var entities: List<EntityInfo>
)

@Serializable
data class TrendingKeywords(
    val date: String,
    val keywords: List<KeywordInfo>
)

@Serializable
data class LeagueKeywords(
    val live: List<KeywordInfo>,
    val recent: List<KeywordInfo>
)
