package com.moare.android.core.util

import com.moare.android.features.search.models.models.nba.NBAGameSummary

object MLBUtil {
    val mlbLogoUrl = "https://www.mlbstatic.com/team-logos/league-on-dark/1.svg"

    val positionCodeMap = mapOf(
        "P" to "투수",
//        "H" to "타자"
    )

    val leagueDivisionMap = mapOf(
        0 to ""
    )

    fun playerPhotoUrl(id: Int?): String? {
        return id?.let { "https://img.mlbstatic.com/mlb-photos/image/upload/v1/people/$it/headshot/67/current.png" }
    }

    fun teamLogoUrl(id: Int?): String? {
        return id?.let { "https://www.mlbstatic.com/team-logos/$it.svg" }
    }

    fun getPositionName(input: String): String {
        return positionCodeMap[input] ?: "타자"
    }

    fun changeToCm(input: String): Int {
        val regex = Regex("""(\d+)'\s*(\d+)\"""")
        val matchResult = regex.find(input.trim())
        val (feet, inches) = matchResult?.destructured ?: return 0
        return toCm(feet.toInt(), inches.toInt()).toInt()
    }
}