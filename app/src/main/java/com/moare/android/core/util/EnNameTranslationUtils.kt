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

    private val shortTeamNameTranslationDic = mapOf(
        "Manchester United" to "맨유", "Newcastle" to "뉴캐슬", "Bournemouth" to "본머스", "Fulham" to "풀럼", "Wolves" to "울버햄튼",
        "Liverpool" to "리버풀", "Southampton" to "새우샘프턴", "Arsenal" to "아스널", "Everton" to "에버턴", "Leicester" to "레스터 시티",
        "Tottenham" to "토트넘", "West Ham" to "웨스트 햄", "Chelsea" to "첼시", "Manchester City" to "맨시티", "Brighton" to "브라이턴",
        "Crystal Palace" to "크리스털 팰리스", "Brentford" to "브렌트퍼드", "Ipswich" to "입스위치", "Nottingham Forest" to "노팅엄 포레스트", "Aston Villa" to "애스턴 빌라",

        "Barcelona" to "바르셀로나", "Atletico Madrid" to "아틀레티코", "Athletic Club" to "빌바오", "Valencia" to "발렌시아", "Villarreal" to "비야레알",
        "Las Palmas" to "라스팔마스", "Sevilla" to "세비야", "Leganes" to "레가네스", "Celta Vigo" to "셀타 데 비고", "Espanyol" to "에스파뇰",
        "Real Madrid" to "레알 마드리드", "Alaves" to "알라베스", "Real Betis" to "레알 베티스", "Getafe" to "헤타페", "Girona" to "지로나",
        "Real Sociedad" to "레알 소시에다드", "Valladolid" to "레알 바야돌리드", "Osasuna" to "오사수나", "Rayo Vallecano" to "바예카노", "Mallorca" to "마요르카",

        "Bayern München" to "바이에른 뮌헨", "SC Freiburg" to "SC 프라이부르크", "VfL Wolfsburg" to "볼프스부르크", "Werder Bremen" to "브레멘", "Borussia Mönchengladbach" to "묀헨글라트바흐",
        "FSV Mainz 05" to "마인츠 05", "Borussia Dortmund" to "도르트문트", "1899 Hoffenheim" to "호펜하임", "Bayer Leverkusen" to "레버쿠젠", "Eintracht Frankfurt" to "프랑크푸르트",
        "FC Augsburg" to "아우크스부르크", "VfB Stuttgart" to "슈투트가르트", "RB Leipzig" to "라이프치히", "VfL Bochum" to "보훔", "1. FC Heidenheim" to "하이덴하임",
        "Union Berlin" to "우니온 베를린", "FC St. Pauli" to "FC 장 파울리", "Holstein Kiel" to "홀슈타인 킬",

        "Angers" to "앙제", "Lille" to "LOSC 릴", "Lyon" to "리옹", "Marseille" to "마르세유", "Montpellier" to "몽펠리에",
        "Nantes" to "낭트", "Nice" to "OGC 니스", "Paris Saint Germain" to "PSG", "Monaco" to "AS 모나코", "Reims" to "랭스",
        "Rennes" to "렌", "Strasbourg" to "스트라스부르", "Toulouse" to "툴루즈", "Stade Brestois 29" to "브레스트", "Auxerre" to "오세르",
        "Le Havre" to "르아브르 AC", "Lens" to "랭스", "Saint Etienne" to "생테티엔"
    )

    private val fullTeamNameTranslationDic = mapOf(
        "Manchester United" to "맨체스터 유나이티드", "Newcastle" to "뉴캐슬 유나이티드", "Bournemouth" to "AFC 본머스", "Fulham" to "풀럼", "Wolves" to "울버햄프턴 원더러스",
        "Liverpool" to "리버풀", "Southampton" to "사우샘프턴", "Arsenal" to "아스널", "Everton" to "에버턴", "Leicester" to "레스터 시티",
        "Tottenham" to "토트넘 홋스퍼", "West Ham" to "웨스트햄 유나이티드", "Chelsea" to "첼시", "Manchester City" to "맨체스터 시티", "Brighton" to "브라이턴 & 호브 앨비언",
        "Crystal Palace" to "크리스털 팰리스", "Brentford" to "브렌트퍼드", "Ipswich" to "입스위치 타운", "Nottingham Forest" to "노팅엄 포레스트", "Aston Villa" to "애스턴 빌라",

        "Barcelona" to "FC 바르셀로나", "Atletico Madrid" to "아틀레티코 마드리드", "Athletic Club" to "아틀레틱 빌바오", "Valencia" to "발렌시아 CF", "Villarreal" to "비야레알 CF",
        "Las Palmas" to "UD 라스팔마스", "Sevilla" to "세비야 FC", "Leganes" to "CD 레가네스", "Celta Vigo" to "셀타 데 비고", "Espanyol" to "RCD 에스파뇰",
        "Real Madrid" to "레알 마드리드 CF", "Alaves" to "데포르티보 알라베스", "Real Betis" to "레알 베티스", "Getafe" to "헤타페 CF", "Girona" to "지로나 FC",
        "Real Sociedad" to "레알 소시에다드", "Valladolid" to "레알 바야돌리드 CF", "Osasuna" to "CA 오사수나", "Rayo Vallecano" to "라요 바예카노", "Mallorca" to "RCD 마요르카",

        "Bayern München" to "FC 바이에른 뮌헨", "SC Freiburg" to "SC 프라이부르크", "VfL Wolfsburg" to "VfL 볼프스부르크", "Werder Bremen" to "SV 베르더 브레멘", "Borussia Mönchengladbach" to "보루시아 묀헨글라트바흐",
        "FSV Mainz 05" to "1. FSV 마인츠 05", "Borussia Dortmund" to "보루시아 도르트문트", "1899 Hoffenheim" to "TSG 1899 호펜하임", "Bayer Leverkusen" to "바이어 04 레버쿠젠", "Eintracht Frankfurt" to "아인트라흐트 프랑크푸르트",
        "FC Augsburg" to "FC 아우크스부르크", "VfB Stuttgart" to "VfB 슈투트가르트", "RB Leipzig" to "RB 라이프치히", "VfL Bochum" to "VfL 보훔", "1. FC Heidenheim" to "1. FC 하이덴하임",
        "Union Berlin" to "1. FC 우니온 베를린", "FC St. Pauli" to "FC 장크트파울리", "Holstein Kiel" to "홀슈타인 킬",

        "Angers" to "앙제 SCO", "Lille" to "LOSC 릴", "Lyon" to "올랭피크 리옹", "Marseille" to "올랭피크 드 마르세유", "Montpellier" to "몽펠리에 HSC",
        "Nantes" to "FC 낭트", "Nice" to "OGC 니스", "Paris Saint Germain" to "파리 생제르맹 FC", "Monaco" to "AS 모나코 FC", "Reims" to "스타드 드 랭스",
        "Rennes" to "스타드 렌 FC", "Strasbourg" to "RC 스트라스부르", "Toulouse" to "툴루즈 FC", "Stade Brestois 29" to "스타드 브레스투아 29", "Auxerre" to "AJ 오세르",
        "Le Havre" to "르아브르 AC", "Lens" to "스타드 드 랭스", "Saint Etienne" to "AS 생테티엔"
    )

    fun init(context: Context) {
        val entryPoint = EntryPointAccessors.fromApplication(context, EntryPoint::class.java)
        translateClient = entryPoint.getTranslateClient()
    }

    fun translateByDic(type: TranslationType, isShort: Boolean = true, input: String): String {
        val map = when (type) {
            TranslationType.COUNTRY -> countryTranslationDic
            TranslationType.PLAYER -> countryTranslationDic
            TranslationType.TEAM -> if (isShort) shortTeamNameTranslationDic else fullTeamNameTranslationDic
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