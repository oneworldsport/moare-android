package com.moare.android.features.userprofile.models

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileUpdateRequest(
    val nickname: String?,
    val profileImageUrl: String?,
    val bio: String?,
    val sportInterest: List<String>?
)