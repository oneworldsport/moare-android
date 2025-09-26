package com.moare.android.features.moat.models

import kotlinx.serialization.Serializable

@Serializable
data class MoatCreateRequest(
    val content: String,
    val sportType: List<String>,
    val parentMoatId: String? = null
)

@Serializable
data class MoatUpdateRequest(
    val content: String? = null,
    val sportType: List<String>? = null
)

@Serializable
data class MoatListRequest(
    val sportType: List<String>? = null,
    val parentMoatId: String? = null,
    val limit: Int = 10,
    val nextToken: Map<String, String>? = null
)