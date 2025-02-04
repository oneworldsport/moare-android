package com.moare.android.features.search.models.models.football

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class FBGame(
    val fixture: FBGameFixture,
    val league: FBLeague,
    val teams: FBGameTeams,
    val goals: FBHomeAwayIntStats,
    val score: FBGameScore,
    val lineups: List<FBGameLineups> = emptyList(),
    val statistics: List<FBGameStats> = emptyList(),
    val players: List<FBGamePlayers> = emptyList()
)

@Serializable
data class FBGameFixture(
    @SerialName("id") private val _id: Int?,
    @SerialName("referee") private val _referee: String?,
    @SerialName("timezone") private val _timezone: String?,
    @SerialName("date") private val _date: String?,
    @SerialName("timestamp") private val _timestamp: Int?,
    val periods: FBGamePeriods,
    val venue: FBVenue,
    val status: FBGameStatus
) {
    val id: Int
        get() = _id ?: 0

    val referee: String
        get() = _referee ?: ""

    val timezone: String
        get() = _timezone ?: ""

    val date: String
        get() = _date ?: ""

    val timestamp: Int
        get() = _timestamp ?: 0
}

@Serializable
data class FBGamePeriods(
    @SerialName("first") private val _first: Int?,
    @SerialName("second") private val _second: Int?,
) {
    val first: Int
        get() = _first ?: 0

    val second: Int
        get() = _second ?: 0
}

@Serializable
data class FBGameStatus(
    @SerialName("long") private val _long: String?,
    @SerialName("short") private val _short: String?,
    @SerialName("elapsed") private val _elapsed: Int?,
    @SerialName("extra") private val _extra: Int?,
) {
    val long: String
        get() = _long ?: ""

    val short: String
        get() = _short ?: ""

    val elapsed: Int
        get() = _elapsed ?: 0

    val extra: Int
        get() = _extra ?: 0
}

@Serializable
data class FBGameTeams(
    val home: FBTeamInfo,
    val away: FBTeamInfo
)

@Serializable
data class FBGameScore(
    val halftime: FBHomeAwayIntStats,
    val fulltime: FBHomeAwayIntStats,
    val extratime: FBHomeAwayIntStats,
    val penalty: FBHomeAwayIntStats
)

@Serializable
data class FBGameLineups(
    val team: FBTeamInfo,
    val coach: FBPerson,
    private val formation: String,
    val startXI: List<FBGameStartXI> = emptyList(),
    val substitutes: List<FBGameStartXI> = emptyList()
)

@Serializable
data class FBGameColors(
    val player: FBGameColorDetail,
    val goalkeeper: FBGameColorDetail
)

@Serializable
data class FBGameColorDetail(
    @SerialName("primary") private val _primary: String?,
    @SerialName("number") private val _number: String?,
    @SerialName("border") private val _border: String?,
) {
    val primary: String
        get() = _primary ?: ""

    val number: String
        get() = _number ?: ""

    val border: String
        get() = _border ?: ""
}

@Serializable
data class FBGameStartXI(
    val player: FBGamePlayer
)

@Serializable
data class FBGamePlayer(
    @SerialName("id") private val _id: Int?,
    @SerialName("name") private val _name: String?,
    @SerialName("number") private val _number: Int?,
    @SerialName("pos") private val _pos: String?,
    @SerialName("grid") private val _grid: String?,
) {
    val id: Int
        get() = _id ?: 0

    val name: String
        get() = _name ?: ""

    val number: Int
        get() = _number ?: 0

    val pos: String
        get() = _pos ?: ""

    val grid: String
        get() = _grid ?: ""
}

@Serializable
data class FBGameStats(
    val team: FBTeamInfo,
    val statistics: List<FBGameTeamStats> = emptyList()
)

@Serializable
data class FBGameTeamStats(
    @SerialName("type") private val _type: String? = null,
    val value: JsonElement? = null
) {
    val type: String
        get() = _type ?: ""
}

@Serializable
data class FBGamePlayers(
    val team: FBTeamInfo,
    val players: List<FBGamePlayerStats> = emptyList()
)

@Serializable
data class FBGamePlayerStats(
    val player: FBPerson,
    val statistics: List<FBGamePlayerStatsDetail> = emptyList()
)

@Serializable
data class FBGamePlayerStatsDetail(
    val games: FBGamePlayerStatsGames,
    @SerialName("offsides") private val _offsides: Int? = null,
    val shots: FBPlayerStatsShots,
    val goals: FBPlayerStatsGoals,
    val passes: FBPlayerStatsPasses,
    val tackles: FBPlayerStatsTackles,
    val duels: FBPlayerStatsDuels,
    val dribbles: FBPlayerStatsDribbles,
    val fouls: FBPlayerStatsFouls,
    val cards: FBPlayerStatsCards,
    val penalty: FBPlayerStatsPenalty
) {
    val offsides: Int
        get() = _offsides ?: 0
}

@Serializable
data class FBGamePlayerStatsGames(
    @SerialName("minutes") private val _minutes: Int? = null,
    @SerialName("number") private val _number: Int? = null,
    @SerialName("position") private val _position: String? = null,
    @SerialName("rating") private val _rating: String? = null,
    @SerialName("captain") private val _captain: Boolean? = null,
    @SerialName("substitute") private val _substitute: Boolean? = null
) {
    val minutes: Int
        get() = _minutes ?: 0

    val number: Int
        get() = _number ?: 0

    val position: String
        get() = _position ?: ""

    val rating: String
        get() = _rating ?: ""

    val captain: Boolean
        get() = _captain ?: false

    val substitute: Boolean
        get() = _substitute ?: false
}

