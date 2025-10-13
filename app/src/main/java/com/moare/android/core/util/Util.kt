package com.moare.android.core.util

import com.moare.android.core.constants.Constants
import com.moare.android.features.search.models.models.common.GameForSchedule

object Util {
    fun teamLogoUrl(leagueId: Int, teamId: Int?): String? {
        return teamId?.let {
            when (leagueId) {
                in Constants.Ids.FOOTBALL_ALL -> "https://media.api-sports.io/football/teams/$teamId.png"
                Constants.Ids.NBA -> "https://cdn.nba.com/logos/nba/$teamId/primary/L/logo.svg"
                Constants.Ids.MLB -> "https://www.mlbstatic.com/team-logos/$teamId.svg"
                Constants.Ids.KBO -> KBOUtil.codeMap[teamId]?.let { "https://6ptotvmi5753.edge.naverncp.com/KBO_IMAGE/emblem/regular/fixed/emblem_$it.png" }
                else -> null
            }
        }
    }

    fun <T> collectRound(
        pairs: List<List<Int?>>,
        games: MutableList<GameForSchedule<T>>,
        allowPartial: Boolean = true
    ): Pair<List<Pair<Int?, Int?>>, List<List<GameForSchedule<T>>>> {

        fun matches(g: GameForSchedule<T>, pair: List<Int?>): Boolean {
            val a = pair.getOrNull(0)
            val b = pair.getOrNull(1)

            return when {
                // 두 팀 다 확정: 순서 무시하고 같은 두 팀이면 매치
                a != null && b != null ->
                    (g.homeTeamId == a && g.awayTeamId == b) || (g.homeTeamId == b && g.awayTeamId == a)

                // 부분 매치 허용: 한 팀만 맞아도 매치
                allowPartial && (a != null || b != null) -> {
                    val x = a ?: b
                    g.homeTeamId == x || g.awayTeamId == x
                }

                else -> false
            }
        }

        val seedIdPair = ArrayList<Pair<Int?, Int?>>(pairs.size)
        val result = ArrayList<List<GameForSchedule<T>>>(pairs.size)

        for (pair in pairs) {
            val a = pair.getOrNull(0)
            val b = pair.getOrNull(1)
            seedIdPair += (a to b)

            // 페어 매칭
            val filtered = games.filter { matches(it, pair) }
            result += filtered

            // 매칭된 게임은 원본에서 제거 (중복 매칭 방지)
            val toRemove = filtered.map { it.gameId }.toHashSet()
            games.removeAll { it.gameId in toRemove }
        }

        return seedIdPair to result
    }
}