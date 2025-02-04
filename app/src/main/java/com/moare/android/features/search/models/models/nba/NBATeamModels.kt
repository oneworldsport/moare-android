package com.moare.android.features.search.models.models.nba

import kotlinx.serialization.Serializable

@Serializable
data class NBATeamInfo(
    val team: NBATeam,
    val statistics: NBATeamStats?,
)

@Serializable
data class NBATeam(
    val allStar: Boolean?,
    val city: String?,
    val code: String?,
    val id: Int?,
    val leagues: NBATeamLeagues?,
    val logo: String?,
    val name: String?,
    val nbaFranchise: Boolean?,
    val nickname: String?
)

@Serializable
data class NBATeamStats(
    val assists: Int?,
    val biggestLead: Int?,
    val blocks: Int?,
    val defReb: Int?,
    val fastBreakPoints: Int?,
    val fga: Int?,
    val fgm: Int?,
    val fgp: String?,
    val fta: Int?,
    val ftm: Int?,
    val ftp: String?,
    val games: Int?,
    val longestRun: Int?,
    val offReb: Int?,
    val pFouls: Int?,
    val plusMinus: Int?,
    val points: Int?,
    val pointsInPaint: Int?,
    val pointsOffTurnovers: Int?,
    val secondChancePoints: Int?,
    val steals: Int?,
    val totReb: Int?,
    val tpa: Int?,
    val tpm: Int?,
    val tpp: String?,
    val turnovers: Int?
)


@Serializable
data class NBATeamLeagues(
    val sacramento: NBATeamLeaguesDetails?,
    val standard: NBATeamLeaguesDetails?,
    val utah: NBATeamLeaguesDetails?,
    val vegas: NBATeamLeaguesDetails?
)

@Serializable
data class NBATeamLeaguesDetails(
    val conference: String?,
    val division: String?
)