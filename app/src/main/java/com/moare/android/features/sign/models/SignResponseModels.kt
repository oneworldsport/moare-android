package com.moare.android.features.sign.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

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
data class SignUpInitiateResponse(
    val sessionId: String
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

@Serializable
enum class TermStatus {
    ACTIVE, DEPRECATED
}

@Serializable
enum class TermType {
    PRIVACY, SERVICE
}

@Serializable
data class TermsResponse(
    val isRequired: Boolean,
    val status: TermStatus,
    val termType: TermType,
    val title: String,
    val url: String,
    val version: String
) {
    val selfKey: TermKey
        get() = TermKey(termType = termType, version = version)
}

data class TermKey(
    val termType: TermType,
    val version: String
)