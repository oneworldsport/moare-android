package com.moare.android.features.userprofile.models

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileUpdateRequest(
    var userHandle: String? = null,
    var profileImageUrl: String? = null,
    var bio: String? = null,
    var sportsInterests: List<String>? = null
)