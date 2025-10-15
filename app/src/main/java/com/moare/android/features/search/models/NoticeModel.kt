package com.moare.android.features.search.models

import kotlinx.serialization.Serializable

@Serializable
data class NoticeModel(
    val title: String,
    val sports: List<SportSection>? = null,
    val content: String? = null
)

@Serializable
data class SportSection(
    val category: String,
    val content: String
)
