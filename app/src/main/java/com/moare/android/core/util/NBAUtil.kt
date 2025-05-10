package com.moare.android.core.util

import com.moare.android.features.search.models.models.nba.NBAGameSummary

object NBAUtil {
//    val nbaLogoUrl = "https://cdn.nba.com/logos/leagues/logo-nba.svg"

    fun translateEastWest(input: String): String {
        return if (input.lowercase() == "east") {
            "동부"
        } else if (input.lowercase() == "west") {
            "서부"
        } else {
            input
        }
    }

    fun playerPhotoUrl(id: Int?): String? {
        return id?.let { "https://cdn.nba.com/headshots/nba/latest/1040x760/$id.png" }
    }

    fun teamLogoUrl(id: Int?): String? {
        return id?.let { "https://cdn.nba.com/logos/nba/$id/primary/L/logo.svg" }
    }

    fun gameType(gameSummary: NBAGameSummary?, isShort: Boolean = false): String {
        if (gameSummary == null) return ""

        return if (gameSummary.seriesGameNumber.isEmpty()) {
            "정규시즌"
        } else if (gameSummary.gameLabel.lowercase().contains("play-in")) {
            "플레이인 토너먼트"
        } else if (!gameSummary.seriesGameNumber.isEmpty()) {
            val label = gameSummary.gameLabel.lowercase()
            val subLabel = gameSummary.gameSubLabel
            val conference = if (label.contains("west")) "서부" else "동부"

            if (label.contains("round")) {
                if (isShort) "플레이오프-${conference} 1R ${subLabel}" else "플레이오프 - ${conference} 컨퍼런스 1라운드 ${subLabel}"
            } else if (label.contains("semifinals")) {
                if (isShort) "플레이오프-${conference} 준결승 ${subLabel}" else "플레이오프 - ${conference} 컨퍼런스 준결승 ${subLabel}"
            } else if (label.contains("finals")) {
                if (label.contains("nba")) {
                    if (isShort) "플레이오프-NBA 결승 ${subLabel}" else "플레이오프 - NBA 결승 ${subLabel}"
                } else {
                    if (isShort) "플레이오프-${conference} 결승 ${subLabel}" else "플레이오프 - ${conference} 컨퍼런스 결승 ${subLabel}"
                }
            } else {
                "정규시즌"
            }
        } else {
            "정규시즌"
        }
    }
}