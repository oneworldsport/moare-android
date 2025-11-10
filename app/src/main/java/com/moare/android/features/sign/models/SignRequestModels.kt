package com.moare.android.features.sign.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartAuthRequest(
    val id: String,
    val method: AuthMethod
)

@Serializable
enum class AuthMethod {
    @SerialName("email")
    EMAIL,
    @SerialName("phone_number")
    PHONE_NUMBER
}

@Serializable
data class ConfirmAuthRequest(
    val id: String,
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

// TODO: User 도메인으로 옮기기
@Serializable
data class UserProfileCreateRequest(
    val userHandle: String,
    val profileImageUrl: String? = null,
    val bio: String? = null,
    val sportsInterests: List<String>? = null
)

@Serializable
data class UserHandleReserveRequest(
    val userHandle: String
)