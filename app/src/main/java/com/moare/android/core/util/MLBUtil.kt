package com.moare.android.core.util

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.Constants
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplay
import com.moare.android.features.search.models.models.mlb.MLBTeamStats
import com.moare.android.features.search.models.models.nba.NBAGameSummary
import com.moare.android.ui.theme.Moare

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

    val teamMap: Map<Int, String> = mapOf(
        133 to "ATH", 134 to "PIT", 135 to "SD", 136 to "SEA", 137 to "SF", 138 to "STL", 139 to "TB", 140 to "TEX",
        141 to "TOR", 142 to "MIN", 143 to "PHI", 144 to "ATL", 145 to "CWS", 146 to "MIA", 147 to "NYY", 158 to "MIL",
        108 to "LAA", 109 to "AZ", 110 to "BAL", 111 to "BOS", 112 to "CHC", 113 to "CIN", 114 to "CLE", 115 to "COL",
        116 to "DET", 117 to "HOU", 118 to "KC", 119 to "LAD", 120 to "WSH", 121 to "NYM"
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

// TODO: 다른곳으로 옮겨야함
@Composable
fun FormatSeriesResult(
    seriesStatus: String,
    homeTeamId: Int,
    awayTeamId: Int,
    teamNameDic: Map<String, String>
) {
    val homeTeamName = teamNameDic["short_${homeTeamId}"] ?: ""
    val awayTeamName = teamNameDic["short_${awayTeamId}"] ?: ""

    // seriesStatus 예시: "STL wins 3-0", "Series tied 2-2"
    val parts = seriesStatus.split(" ")
    if (parts.size != 3) return

    val scoreParts = parts[2].split("-")

    val text = buildAnnotatedString {
        append("시리즈 스코어: ")

        if (parts[0].lowercase() == "series") {
            // "Series tied 2-2" 같은 케이스
            append("$awayTeamName ")
            withStyle(SpanStyle(color = Moare)) {
                append(scoreParts[0])
            }
            append(" - ")
            withStyle(SpanStyle(color = Moare)) {
                append(scoreParts[1] + " ")
            }
            append(homeTeamName)
        } else {
            // "STL wins 3-0" 같은 케이스
            val winnerCode = parts[0]
            val homeCode = MLBUtil.teamMap[homeTeamId] ?: ""

            val homeScore: Int
            val awayScore: Int

            if (homeCode == winnerCode) {
                homeScore = scoreParts.getOrNull(0)?.toIntOrNull() ?: 0
                awayScore = scoreParts.getOrNull(1)?.toIntOrNull() ?: 0
            } else {
                homeScore = scoreParts.getOrNull(1)?.toIntOrNull() ?: 0
                awayScore = scoreParts.getOrNull(0)?.toIntOrNull() ?: 0
            }

            append("$awayTeamName ")
            withStyle(SpanStyle(color = if (awayScore >= homeScore) Moare else Color.Black)) {
                append(awayScore.toString())
            }
            append(" - ")
            withStyle(SpanStyle(color = if (homeScore >= awayScore) Moare else Color.Black)) {
                append("$homeScore ")
            }
            append(homeTeamName)
        }
    }

    Text(
        text = text,
        fontSize = 14.sp,
        modifier = Modifier.padding(start = 8.dp)
    )
}