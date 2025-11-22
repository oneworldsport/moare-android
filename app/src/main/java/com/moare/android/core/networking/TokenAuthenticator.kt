package com.moare.android.core.networking

import android.util.Log
import com.moare.android.core.di.NetworkJson
import com.moare.android.core.util.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val cognitoTokenClient: CognitoTokenClient
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // 401이면서 "리프레시 가능한" 인증오류면 토큰 재발급 후 1회 재시도
        if (response.code != 401) return null

        if (responseCount(response) >= 2) return null

        val isTokenExpired = isRefreshableAuthError(response)
        if (!isTokenExpired) {
            return null
        }

        synchronized(this) {
            // 이미 누가 refresh해서 새 토큰 생겼는지 한 번 더 확인
            val latestToken = runBlocking { tokenManager.getAccessToken() }
            val oldToken = response.request.header("Authorization")?.removePrefix("Bearer ")?.trim()
            if (!latestToken.isNullOrBlank() && latestToken != oldToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $latestToken")
                    .build()
            }

            val newAccessToken = try {
                runBlocking {
                    cognitoTokenClient.refreshToken()
                }
            } catch (e: Exception) {
                Log.d("token_exception", "failed refreshing token. Deleting tokens...")
                // token 갱신 실패 시 기존 토큰 삭제
                runBlocking { tokenManager.clearTokens() }
                return null
            }

            Log.d("token_exception", "trying again...")
            return response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private fun isRefreshableAuthError(response: Response): Boolean {
        val body = response.peekBody(Long.MAX_VALUE).string()
        if (body.isBlank()) return false

        return try {
            val envelope = NetworkJson.instance.decodeFromString<ApiErrorEnvelope>(body)
            envelope.error.code == "TOKEN_EXPIRED"
        } catch (_: Exception) {
            false
        }
    }
}






















