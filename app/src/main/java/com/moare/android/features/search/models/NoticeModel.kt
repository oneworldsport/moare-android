package com.moare.android.features.search.models

import kotlinx.serialization.Serializable

@Serializable
data class NoticeModel(
    val title: String,
    val content: String
)
