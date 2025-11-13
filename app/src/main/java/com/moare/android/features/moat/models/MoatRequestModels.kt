package com.moare.android.features.moat.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoatCreateRequest(
    val content: String,
    val sportTags: List<String>,
    val parentMoatId: String? = null
)

@Serializable
data class MoatUpdateRequest(
    val content: String? = null,
    val sportTags: List<String>? = null
)

@Serializable
data class MoatListRequest(
    val sportTags: List<String>? = null,
    val parentMoatId: String? = null,
    val limit: Int = 10,
    val nextToken: Map<String, String>? = null
)

@Serializable
data class FireCreateRequest(
    val targetId: String,
    val targetType: TargetType
)

@Serializable
enum class TargetType {
    @SerialName("moat")
    MOAT,
    @SerialName("comment")
    COMMENT
}

@Serializable
data class FireCancelRequest(
    val targetId: String,
    val targetType: TargetType
)