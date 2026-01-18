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
    val loginId: String,
    val method: AuthMethod
)

@Serializable
data class SignUpVerificationRequest(
    val sessionId: String,
    val otp: String
)

@Serializable
data class SignUpCompleteRequest(
    val sessionId: String,
    val loginId: String,
    val method: AuthMethod,
    val profile: UserProfileCreateRequest
)

@Serializable
data class UserProfileCreateRequest(
    val userHandle: String,
    val profileImageUrl: String? = null,
    val bio: String? = null,
    val sportsInterests: List<String>,
    val termsAgreements: List<TermsAgreementRequest>
)

@Serializable
data class UserHandleReserveRequest(
    val signupSessionId: String?,
    val userHandle: String
)

@Serializable
data class TermsAgreementRequest(
    val termType: TermType,
    val version: String,
    val isAgreed: Boolean
)