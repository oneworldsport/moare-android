package com.moare.android.features.search.models.models.nba

import kotlinx.serialization.Serializable

@Serializable
data class NBAGame(
    val id: Int?,
    val league: String?,
    val season: Int?,
    val date: NBAGameDate?,
    val stage: Int?,
    val status: NBAGameStatus?,
    val periods: NBAGamePeriods?,
    val arena: NBAGameArena?,
    val teams: List<NBAGameTeamWrapper>?,
    val scores: NBAGameScores?,
//    val officials:
    val timesTied: Int?, // TODO: 자료형 확인 필요
    val leadChanges: Int?, // TODO: 자료형 확인 필요
    val nugget: Int?, // TODO: 자료형 확인 필요
    val players: List<NBAGamePlayerStats>
)

@Serializable
data class NBAGameDate(
    val start: String?,
    val end: String?,
    val duration: String?
)

@Serializable
data class NBAGameStatus(
    val clock: String?,
    val halftime: Boolean?,
    val short: Int?,
    val long: String?
)

@Serializable
data class NBAGamePeriods(
    val current: Int?,
    val total: Int?,
    val endOfPeriod: Boolean?
)

@Serializable
data class NBAGameArena(
    val name: String?,
    val city: String?,
    val state: String?,
    val country: String?
)

@Serializable
data class NBAGameTeamWrapper(
    val team: NBAGameTeam?,
    val statistics: List<NBAGameStats>?
)

@Serializable
data class NBAGameStats(
    val fastBreakPoints: String?,
    val pointsInPaint: String?,
    val biggestLead: String?,
    val secondChancePoints: String?,
    val pointsOffTurnovers: String?,
    val longestRun: String?,
    val points: Int?,
    val fgm: Int?,
    val fga: Int?,
    val fgp: String?,
    val ftm: Int?,
    val fta: Int?,
    val ftp: String?,
    val tpm: Int?,
    val tpa: Int?,
    val tpp: String?,
    val offReb: Int?,
    val defReb: Int?,
    val totReb: Int?,
    val assists: Int?,
    val pFouls: Int?,
    val steals: Int?,
    val turnovers: Int?,
    val blocks: Int?,
    val plusMinus: String?,
    val min: String?
)

@Serializable
data class NBAGameTeam(
    val id: Int?,
    val name: String?,
    val nickname: String?,
    val code: String?,
    val logo: String?
)

@Serializable
data class NBAGameScores(
    val visitors: NBAGameTeamScores?,
    val home: NBAGameTeamScores?
)

@Serializable
data class NBAGameTeamScores(
    val win: Int?,
    val loss: Int?,
    val series: NBAGameSeriesScores?,
    val linescore: List<String>?,
    val points: Int?
)

@Serializable
data class NBAGameSeriesScores(
    val win: Int?,
    val loss: Int?
)

@Serializable
data class NBAGamePlayerStats(
    val player: NBAGamePlayer?,
    val team: NBAGameTeam?,
    val game: NBAGameGamdId?,
    val points: Int?,
    val pos: String?,
    val min: String?,
    val fgm: Int?,
    val fga: Int?,
    val fgp: String?,
    val ftm: Int?,
    val fta: Int?,
    val ftp: String?,
    val tpm: Int?,
    val tpa: Int?,
    val tpp: String?,
    val offReb: Int?,
    val defReb: Int?,
    val totReb: Int?,
    val assists: Int?,
    val pFouls: Int?,
    val steals: Int?,
    val turnovers: Int?,
    val blocks: Int?,
    val plusMinus: String?,
    val comment: String?
)

@Serializable
data class NBAGamePlayer(
    val id: Int?,
    val firstname: String?,
    val lastname: String?
)

@Serializable
data class NBAGameGamdId(
    val id: Int?
)