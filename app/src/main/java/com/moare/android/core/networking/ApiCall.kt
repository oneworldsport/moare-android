package com.moare.android.core.networking

import okio.IOException
import retrofit2.HttpException

suspend inline fun <T> apiCall(
    crossinline block: suspend () -> T
): T {
    try {
        return block()
    } catch (e: HttpException) {
        val apiError = e.toApiHttpError()

        if (isSessionInvalidating(apiError)) {
            // 리프레시도 안되고, 세션 자체가 무효인 경우
            // (401인데 리프레시 불가 / 403 등)
            // -> 토큰 삭제 + 세션 만료 예외 던지기
            // (TokenManager는 주입받은 쪽에서 사용)
            throw SessionExpiredException()
        }

        throw apiError
    } catch (e: IOException) {
        // 네트워크 오류 (타임아웃, 인터넷 끊김 등)
        throw e
    }
}

class SessionExpiredException(message: String? = null) : Exception(message)