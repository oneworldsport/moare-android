package com.moare.android.core.networking

import com.moare.android.core.di.NetworkJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import retrofit2.HttpException

@Serializable
data class ApiErrorEnvelope(
    val error: ApiErrorBody
)

@Serializable
data class ApiErrorBody(
    val code: String,
    val message: String,
    val details: Map<String, String>? = null
)

data class ApiHttpError(
    val status: Int,
    val apiCode: String?,
    override val message: String?,
    val details: Map<String, String>?
) : Exception(message)

fun HttpException.toApiHttpError(): ApiHttpError {
    val status = code()
    val raw = response()?.errorBody()?.string()

    if (raw.isNullOrBlank()) {
        // 바디가 없으면 그냥 상태코드만 가진 에러
        return ApiHttpError(
            status = status,
            apiCode = null,
            message = null,
            details = null
        )
    }

    return try {
        val envelope = NetworkJson.instance.decodeFromString<ApiErrorEnvelope>(raw)
        val body = envelope.error
        ApiHttpError(
            status = status,
            apiCode = body.code,
            message = body.message,
            details = body.details
        )
    } catch (e: Exception) {
        ApiHttpError(
            status = status,
            apiCode = null,
            message = raw,
            details = null
        )
    }
}

fun isSessionInvalidating(e: ApiHttpError): Boolean = when (e.status) {
    401 -> !e.isRefreshableAuthError()
    403 -> true
    400 -> false
    else -> false
}

fun ApiHttpError.isRefreshableAuthError(): Boolean {
    return status == 401 && apiCode == "TOKEN_EXPIRED"
}