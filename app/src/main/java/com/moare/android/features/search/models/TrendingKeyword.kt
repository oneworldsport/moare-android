package com.moare.android.features.search.models

import kotlinx.serialization.Serializable

@Serializable
data class TrendingKeyword(
    val keyword: String,
    val keywords: List<Keyword>,
    val entities: List<EntityInfo>
)
