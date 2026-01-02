package com.moare.android.features.sign.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AuthResponse(
    val type: AuthResponseType = AuthResponseType.SUCCESS,
    val message: String = "",
    val data: JsonElement? = null
)

@Serializable
enum class AuthResponseType {
    SUCCESS, RETRY, EXPIRED, LIMIT_EXCEEDED, ERROR
}

@Serializable
data class AuthTokenResponse(
    val idToken: String,
    val accessToken: String,
    val refreshToken: String,
    val userId: String
)

@Serializable
data class AuthSessionResponse(
    val session: String
)

@Serializable
data class SimpleResponse(
    val success: Boolean,
    val message: String
)

@Serializable
data class BootstrapSessionResponse(
    val userId: String
)