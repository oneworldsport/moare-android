package com.moare.android.core.networking

import kotlinx.serialization.Serializable

@Serializable
data class CognitoTokenResponse(
    val access_token: String,
    val refresh_token: String? = null,
    val id_token: String? = null,
    val expires_in: Long? = null,
    val token_type: String
)
