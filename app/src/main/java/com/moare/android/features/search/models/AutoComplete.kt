package com.moare.android.features.search.models

import kotlinx.serialization.Serializable

@Serializable
data class AutoComplete(
    val word: String,
    val weight: Int
)