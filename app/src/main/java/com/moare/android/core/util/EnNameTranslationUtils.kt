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

    // NOTE: if the structure changes to translating teamName in the app, this dictionary should change to english-korean dictionary.
    // now is temporary dictionary.
    private val teamNameTranslationDic = mapOf(
        "늑대" to "울버햄튼"
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

        return ""
    }
}