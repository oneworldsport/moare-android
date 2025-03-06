package com.moare.android.features.search.models

import kotlinx.serialization.Serializable

@Serializable
data class KeywordInfo(
    var keyword: String, // searching keyword
    var weight: Int? = null,
    var keywords: List<Keyword>, // keyword list that are in searching keyword
    var entities: List<EntityInfo>
)
