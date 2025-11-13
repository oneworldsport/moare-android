package com.moare.android.features.moat.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoatResponse(
    val moatId: String,
    val userId: String,
    val userHandle: String,
    val profileImageUrl: String?,
    val content: String,
    val sportTags: List<String>,
    val parentMoatId:String?,
    val targetType: String,
    val createdAt: String,
    val updatedAt: String?,
    val fireCount: Int,
    val commentCount: Int
)

@Serializable
data class MoatDetailResponse(
    val moat: MoatResponse,
    var commentListResponse: MoatListResponse?
)

@Serializable
data class MoatListResponse(
    var moats: List<MoatResponse>,
    val nextToken:  Map<String, String>?
)

@Serializable
data class FireResponse(
    val targetId: String,
    val userId: String,
    val targetType: String,
    val createdAt: String
)