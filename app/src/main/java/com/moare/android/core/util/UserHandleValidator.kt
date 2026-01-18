package com.moare.android.core.util

sealed class UserHandleValidationError {
    data object Empty : UserHandleValidationError()
    data class TooShort(val min: Int) : UserHandleValidationError()
    data class TooLong(val max: Int) : UserHandleValidationError()
    data object InvalidCharacters : UserHandleValidationError()
    data object StartsWithUnderscore : UserHandleValidationError()
    data object EndsWithUnderscore : UserHandleValidationError()
    data object ContainsDoubleUnderscore : UserHandleValidationError()
}

object UserHandleValidator {
    private const val MIN_LENGTH = 3
    private const val MAX_LENGTH = 20

    fun validate(input: String): UserHandleValidationError? {
        // 0) trim
        val handle = input.trim()

        // 1) empty check
        if (handle.isBlank()) return UserHandleValidationError.Empty

        // 2) length check
        val count = handle.length
        if (count < MIN_LENGTH) return UserHandleValidationError.TooShort(MIN_LENGTH)
        if (count > MAX_LENGTH) return UserHandleValidationError.TooLong(MAX_LENGTH)

        // 3) allowed characters check: lowercase a-z, digits 0-9, underscore _
        val allowedRegex = Regex("^[a-z0-9_]+$")
        if (!allowedRegex.matches(handle)) return UserHandleValidationError.InvalidCharacters

        // 4) starts/ends with underscore check
        if (handle.startsWith("_")) return UserHandleValidationError.StartsWithUnderscore
        if (handle.endsWith("_")) return UserHandleValidationError.EndsWithUnderscore

        // 5) no double underscore
        if (handle.contains("__")) return UserHandleValidationError.ContainsDoubleUnderscore

        return null
    }

    fun isValid(input: String): Boolean = validate(input) == null
}