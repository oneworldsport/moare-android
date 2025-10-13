package com.moare.android.core.util

import com.moare.android.core.constants.Constants

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
}