package com.moare.android.core.util

import com.moare.android.core.constants.Constants
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplay
import com.moare.android.features.search.models.models.mlb.MLBTeamStats
import com.moare.android.features.search.models.models.nba.NBAGameSummary

object MLBUtil {
    val mlbLogoUrl = "https://www.mlbstatic.com/team-logos/league-on-dark/1.svg"

    val positionCodeMap = mapOf(
        "P" to "투수",
//        "H" to "타자"
    )

    val leagueDivisionMap = mapOf(
        Constants.Ids.AMERICAN_LEAGUE to "아메리칸리그",
        Constants.Ids.NATIONAL_LEAGUE to "내셔널리그",
        Constants.Ids.AMERICAN_LEAGUE_WEST to "아메리칸 서부",
        Constants.Ids.AMERICAN_LEAGUE_EAST to "아메리칸 동부",
        Constants.Ids.AMERICAN_LEAGUE_CENTRAL to "아메리칸 중부",
        Constants.Ids.NATIONAL_LEAGUE_WEST to "내셔널 서부",
        Constants.Ids.NATIONAL_LEAGUE_EAST to "내셔널 동부",
        Constants.Ids.NATIONAL_LEAGUE_CENTRAL to "내셔널 중부",
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

    // NOTE: divisionGamesBack(게임차) 값이 이상해서 만듬
    fun calculateGamesBack(team: MLBTeamStats, standings: List<MLBTeamStandingsDisplay>): Double {
        val leader = standings.maxByOrNull {
            it.stats.recordData?.winningPercentage?.toDoubleOrNull() ?: 0.0
        } ?: return 0.0

        val leaderRecord = leader.stats.recordData ?: return 0.0
        val teamRecord = team.recordData ?: return 0.0

        val gamesBack = ((leaderRecord.wins - teamRecord.wins) + (teamRecord.losses - leaderRecord.losses)).toDouble() / 2.0

        return gamesBack
    }
}