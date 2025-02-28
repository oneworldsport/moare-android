package com.moare.android.features.search.models

import kotlinx.serialization.Serializable

@Serializable
data class KeywordInfo(
    val keyword: String,
    var weight: Int? = null,
    val keywords: List<Keyword>,
    val entities: List<EntityInfo>
)
