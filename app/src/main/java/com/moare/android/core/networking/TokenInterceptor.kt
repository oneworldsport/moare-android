package com.moare.android.core.networking

import com.moare.android.core.util.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // 토큰 불필요한 요청은 스킵
//        val path = original.url.encodedPath
//        if (path.startsWith("/auth/login") || path.startsWith("/auth/refresh")) {
//            return chain.proceed(original)
//        }

        val accessToken = runBlocking { tokenManager.getAccessToken() }

        val newRequest = if (!accessToken.isNullOrBlank()) {
            original.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            original
        }

        return chain.proceed(newRequest)
    }
}










