package com.moare.android.features.search.models.models.nba

import kotlinx.serialization.Serializable

@Serializable
data class NBAPlayerInfo(
    val player: NBAPlayer,
    val statistics: List<NBAPlayerStatistic>?
)

@Serializable
data class NBAPlayer(
    val affiliation: String?,
    val birth: NBAPlayerBirth?,
    val college: String?,
    val firstname: String?,
    val height: NBAPlayerHeight?,
    val id: Int?,
    val lastname: String?,
    val leagues: NBAPlayerLeagues?,
    val nba: NBAPlayerNBAInfo?,
    val weight: NBAPlayerWeight?
)

@Serializable
data class NBAPlayerBirth(
    val country: String?,
    val date: String?
)

@Serializable
data class NBAPlayerHeight(
    val feets: String?,
    val inches: String?,
    val meters: String?
)

@Serializable
data class NBAPlayerLeagues(
    val standard: NBAPlayerLeagueStandard?
)

@Serializable
data class NBAPlayerLeagueStandard(
    val active: Boolean?,
    val jersey: Int?,
    val pos: String?
)

@Serializable
data class NBAPlayerNBAInfo(
    val pro: Int?,
    val start: Int?
)

@Serializable
data class NBAPlayerWeight(
    val kilograms: String?,
    val pounds: String?
)

@Serializable
data class NBAPlayerStatistic(
    val assists: Int?,
    val blocks: Int?,
    val comment: String?,
    val defReb: Int?,
    val fga: Int?,
    val fgm: Int?,
    val fgp: String?,
    val fta: Int?,
    val ftm: Int?,
    val ftp: String?,
    // TODO: CommonModel로
    val game: NBAGameGamdId?,
    val min: String?,
    val offReb: Int?,
    val pFouls: Int?,
    val player: NBAPlayerStatisticPlayerInfo?,
    val plusMinus: String?,
    val points: Int?,
    val pos: String?,
    val steals: Int?,
    // TODO: CommonModel로
    val team: NBAGameTeam?,
    val totReb: Int?,
    val tpa: Int?,
    val tpm: Int?,
    val tpp: String?,
    val turnovers: Int?
)

@Serializable
data class NBAPlayerStatisticPlayerInfo(
    val firstname: String?,
    val id: Int?,
    val lastname: String?
)