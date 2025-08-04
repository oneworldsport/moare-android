package com.moare.android.features.sign.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AuthResponse(
    val type: AuthResponseType,
    val message: String,
    val data: JsonElement? = null
)

@Serializable
enum class AuthResponseType {
    SUCCESS, RETRY, EXPIRED, LIMIT_EXCEEDED, ERROR
}

@Serializable
data class AuthTokenData(
    val idToken: String,
    val accessToken: String,
    val refreshToken: String
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