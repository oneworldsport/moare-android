package com.moare.android.core.util

import android.content.Context
import com.amazonaws.services.translate.AmazonTranslateClient
import com.amazonaws.services.translate.model.TranslateTextRequest
import com.moare.android.core.di.EntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class TranslationType {
    COUNTRY, PLAYER, TEAM
}

object EnNameTranslationUtils {
    private lateinit var translateClient: AmazonTranslateClient

    private val countryTranslationDic = mapOf(
        "Korea Republic" to "대한민국",
        "England" to "영국"
    )

    private val teamNameTranslationDic = mapOf(
        "Manchester United" to "맨체스터 유나이티드",
        "Newcastle" to "뉴캐슬 유나이티드",
        "Bournemouth" to "AFC 본머스",
        "Fulham" to "풀럼",
        "Wolves" to "울버햄프턴 원더러스",
        "Liverpool" to "리버풀",
        "Southampton" to "사우샘프턴",
        "Arsenal" to "아스널",
        "Everton" to "에버턴",
        "Leicester" to "레스터 시티",
        "Tottenham" to "토트넘 홋스퍼",
        "West Ham" to "웨스트햄 유나이티드",
        "Chelsea" to "첼시",
        "Manchester City" to "맨체스터 시티",
        "Brighton" to "브라이턴 & 호브 앨비언",
        "Crystal Palace" to "크리스털 팰리스",
        "Brentford" to "브렌트퍼드",
        "Ipswich" to "입스위치 타운",
        "Nottingham Forest" to "노팅엄 포레스트",
        "Aston Villa" to "애스턴 빌라"
    )

    fun init(context: Context) {
        val entryPoint = EntryPointAccessors.fromApplication(context, EntryPoint::class.java)
        translateClient = entryPoint.getTranslateClient()
    }

    fun translateByDic(type: TranslationType, input: String): String {
        val map = when (type) {
            TranslationType.COUNTRY -> countryTranslationDic
            TranslationType.PLAYER -> countryTranslationDic
            TranslationType.TEAM -> teamNameTranslationDic
        }

        var result = input

        for ((english, korean) in map) {
            if (result.contains(english, ignoreCase = true)) {
                result = result.replace(english, korean, ignoreCase = true)
                break
            }
        }

        return result
    }

    suspend fun translateByAWS(text: String?): String {
        try {
            if (!text.isNullOrBlank()) {
                val translatedText = withContext(Dispatchers.IO) {
                    val translateRequest = TranslateTextRequest()
                        .withText(text)
                        .withSourceLanguageCode("en")
                        .withTargetLanguageCode("ko")

                    val translateResult = translateClient.translateText(translateRequest)
                    translateResult.translatedText
                }

                return translatedText
            }

            return text ?: ""
        } catch (e: Exception) {
            return text ?: ""
        }
    }
}