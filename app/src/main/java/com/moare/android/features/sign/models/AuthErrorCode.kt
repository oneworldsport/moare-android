package com.moare.android.features.sign.models

enum class AuthErrorCode(val code: String) {
    OTP_INVALID("OTP_INVALID"),
    OTP_EXPIRED("OTP_EXPIRED"),
    OTP_ATTEMPT_LIMIT_EXCEEDED("OTP_ATTEMPT_LIMIT_EXCEEDED"),
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS"),
    USER_NOT_FOUND("USER_NOT_FOUND"),
    AUTH_SESSION_NOT_FOUND("AUTH_SESSION_NOT_FOUND"),
    USER_HANDLE_ALREADY_EXISTS("USER_HANDLE_ALREADY_EXISTS"),
    UNKNOWN("unknown");

    companion object {
        fun fromCode(raw: String?): AuthErrorCode {
            if (raw == null) return UNKNOWN
            return entries.firstOrNull { it.code == raw } ?: UNKNOWN
        }
    }
}