package com.moare.android.features.sign.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartAuthRequest(
    val loginId: String,
    val method: AuthMethod
)

@Serializable
enum class AuthMethod {
    EMAIL, PHONE_NUMBER
}

@Serializable
data class ConfirmAuthRequest(
    val loginId: String,
    val otp: String,
    val session: String
)

@Serializable
data class SignUpInitiateRequest(
    val id: String,
    val method: AuthMethod
)

@Serializable
data class SignUpVerificationRequest(
    val id: String,
    val otp: String
)

@Serializable
data class SignUpCompleteRequest(
    val id: String,
    val method: AuthMethod,
    val profile: UserProfileCreateRequest
)

@Serializable
data class UserProfileCreateRequest(
    val userHandle: String,
    val profileImageUrl: String? = null,
    val bio: String? = null,
    val sportsInterests: List<String>
)

@Serializable
data class UserHandleReserveRequest(
    val userHandle: String
)