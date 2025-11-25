package com.moare.android.features.userprofile.models

import kotlinx.serialization.Serializable
import com.moare.android.features.moat.models.MoatListResponse

@Serializable
data class UserProfileResponse(
    val userId: String,
    val userHandle: String,
    val profileImageUrl: String?,
    val bio: String?,
    val sportsInterests: List<String>,
    val joinedAt: String
)

@Serializable
data class UserProfileWithMoatsResponse(
    val userProfile: UserProfileResponse,
    val moatListResponse: MoatListResponse?
)

@Serializable
data class UserSummaryResponse(
    val userId: String,
    val userHandle: String,
    val profileImageUrl: String?
)