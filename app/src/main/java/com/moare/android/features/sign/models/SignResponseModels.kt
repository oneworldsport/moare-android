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
data class AuthTokenData(
    val idToken: String,
    val accessToken: String,
    val refreshToken: String
)

@Serializable
enum class AuthResponseType {
    SUCCESS, RETRY, EXPIRED, LIMIT_EXCEEDED, ERROR
}