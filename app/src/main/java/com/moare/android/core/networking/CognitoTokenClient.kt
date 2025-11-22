package com.moare.android.core.networking

import android.util.Log
import com.moare.android.core.networking.apiendpoint.CognitoAuthApi
import com.moare.android.core.util.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CognitoTokenClient @Inject constructor(
    private val tokenManager: TokenManager,
    private val cognitoAuthApi: CognitoAuthApi
) {
    private val clientId = "1uccmt8d43rqqel0hn69hscj2t"

    suspend fun refreshToken(): String {
        Log.d("token_exception", "getting new token with refresh token...")

        val refreshToken = tokenManager.getRefreshToken() ?: throw IllegalStateException("No refresh token")

        val response = cognitoAuthApi.refreshToken(
            clientId = clientId,
            refreshToken = refreshToken
        )

        if (response.access_token.isNotBlank()) {
            Log.d("token_exception", "new tokens received! updating tokens...")
            tokenManager.updateTokens(
                accessToken = response.access_token,
                refreshToken = response.refresh_token ?: refreshToken,
                idToken = null
            )
        }

        return response.access_token
    }
}