package com.moare.android.features.search.models.models.football

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FBPlayer(
    val player: FBPlayerInfo,
    val statistics: List<FBPlayerStats> = emptyList()
)

@Serializable
data class FBPlayerInfo(
    @SerialName("id") private val _id: Int? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("krname") private val _krname: String? = null,
    @SerialName("firstname") private val _firstname: String? = null,
    @SerialName("lastname") private val _lastname: String? = null,
    @SerialName("age") private val _age: Int? = null,
    val birth: FBPlayerBirth,
    @SerialName("nationality") private val _nationality: String? = null,
    @SerialName("height") private val _height: String? = null,
    @SerialName("weight") private val _weight: String? = null,
    @SerialName("injured") private val _injured: Boolean? = null,
    @SerialName("photo") private val _photo: String? = null
) {
    val id: Int
        get() = _id ?: 0

    val name: String
        get() = _name ?: ""

    val krname: String
        get() = _krname ?: ""

    val firstname: String
        get() = _firstname ?: ""

    val lastname: String
        get() = _lastname ?: ""

    val age: Int
        get() = _age ?: 0

    val nationality: String
        get() = _nationality ?: ""

    val height: String
        get() = _height ?: ""

    val weight: String
        get() = _weight ?: ""

    val injured: Boolean
        get() = _injured ?: false

    val photo: String
        get() = _photo ?: ""
}

@Serializable
data class FBPlayerBirth(
    @SerialName("date") private val _date: String? = null,
    @SerialName("place") private val _place: String? = null,
    @SerialName("country") private val _country: String? = null
) {
    val date: String
        get() = _date ?: ""

    val place: String
        get() = _place ?: ""

    val country: String
        get() = _country ?: ""
}

@Serializable
data class FBPlayerStats(
    val team : FBTeamInfo,
    val league: FBLeague,
    val games: FBPlayerStatsGames,
    val substitutes: FBPlayerStatsSubstitutes,
    val shots: FBPlayerStatsShots,
    val goals: FBPlayerStatsGoals,
    val passes: FBPlayerStatsPasses,
    val tackles: FBPlayerStatsTackles,
    val duels: FBPlayerStatsDuels,
    val dribbles: FBPlayerStatsDribbles,
    val fouls: FBPlayerStatsFouls,
    val cards: FBPlayerStatsCards,
    val penalty: FBPlayerStatsPenalty
)

@Serializable
data class FBPlayerStatsGames(
    @SerialName("appearences") private val _appearences: Int? = null,
    @SerialName("lineups") private val _lineups: Int? = null,
    @SerialName("minutes") private val _minutes: Int? = null,
    @SerialName("number") private val _number: Int? = null,
    @SerialName("position") private val _position: String? = null,
    @SerialName("rating") private val _rating: String? = null,
    @SerialName("captain") private val _captain: Boolean? = null
) {
    val appearences: Int
        get() = _appearences ?: 0 // 경기 출전 수

    val lineups: Int
        get() = _lineups ?: 0 // 선발 출전 수

    val minutes: Int
        get() = _minutes ?: 0

    val number: Int
        get() = _number ?: 0 // 등번호. if data is not available, it is 0

    val position: String
        get() = _position ?: ""

    val rating: String
        get() = _rating ?: "0" // 평균 평점

    val captain: Boolean
        get() = _captain ?: false // NOTE: fb_player_info 에서는 captain 정보가 맞지 않음
}

@Serializable
data class FBPlayerStatsSubstitutes(
    @SerialName("in") private val _substituteIn: Int? = null,
    @SerialName("out") private val _substituteOut: Int? = null,
    @SerialName("bench") private val _bench: Int? = null
) {
    val substituteIn: Int
        get() = _substituteIn ?: 0

    val substituteOut: Int
        get() = _substituteOut ?: 0

    val bench: Int
        get() = _bench ?: 0
}

@Serializable
data class FBPlayerStatsShots(
    @SerialName("total") private val _total: Int? = null,
    @SerialName("on") private val _on: Int? = null
) {
    val total: Int
        get() = _total ?: 0

    val on: Int
        get() = _on ?: 0 // 유효슈팅
}

@Serializable
data class FBPlayerStatsGoals(
    @SerialName("total") private val _total: Int? = null,
    @SerialName("conceded") private val _conceded: Int? = null,
    @SerialName("assists") private val _assists: Int? = null,
    @SerialName("saves") private val _saves: Int? = null
) {
    val total: Int
        get() = _total ?: 0

    val conceded: Int
        get() = _conceded ?: 0

    val assists: Int
        get() = _assists ?: 0

    val saves: Int
        get() = _saves ?: 0
}

@Serializable
data class FBPlayerStatsPasses(
    @SerialName("total") private val _total: Int? = null,
    @SerialName("key") private val _key: Int? = null,
    @SerialName("accuracy") private val _accuracy: Int? = null
) {
    val total: Int
        get() = _total ?: 0

    val key: Int
        get() = _key ?: 0

    val accuracy: Int
        get() = _accuracy ?: 0
}

@Serializable
data class FBPlayerStatsTackles(
    @SerialName("total") private val _total: Int? = null,
    @SerialName("blocks") private val _blocks: Int? = null,
    @SerialName("interceptions") private val _interceptions: Int? = null
) {
    val total: Int
        get() = _total ?: 0

    val blocks: Int
        get() = _blocks ?: 0

    val interceptions: Int
        get() = _interceptions ?: 0
}

// 볼 경합(땅볼 + 공중볼)
@Serializable
data class FBPlayerStatsDuels(
    @SerialName("total") private val _total: Int? = null,
    @SerialName("won") private val _won: Int? = null
) {
    val total: Int
        get() = _total ?: 0

    val won: Int
        get() = _won ?: 0
}

@Serializable
data class FBPlayerStatsDribbles(
    @SerialName("attempts") private val _attempts: Int? = null,
    @SerialName("success") private val _success: Int? = null,
    @SerialName("past") private val _past: Int? = null
) {
    val attempts: Int
        get() = _attempts ?: 0

    val success: Int
        get() = _success ?: 0

    val past: Int
        get() = _past ?: 0
}

@Serializable
data class FBPlayerStatsFouls(
    @SerialName("drawn") private val _drawn: Int? = null,
    @SerialName("committed") private val _committed: Int? = null
) {
    val drawn: Int
        get() = _drawn ?: 0

    val committed: Int
        get() = _committed ?: 0
}

@Serializable
data class FBPlayerStatsCards(
    @SerialName("yellow") private val _yellow: Int? = null,
    @SerialName("yellowred") private val _yellowred: Int? = null,
    @SerialName("red") private val _red: Int? = null
) {
    val yellow: Int
        get() = _yellow ?: 0

    val yellowred: Int
        get() = _yellowred ?: 0

    val red: Int
        get() = _red ?: 0
}

@Serializable
data class FBPlayerStatsPenalty(
    @SerialName("won") private val _won: Int? = null,
    @SerialName("commited") private val _commited: Int? = null,
    @SerialName("scored") private val _scored: Int? = null,
    @SerialName("missed") private val _missed: Int? = null,
    @SerialName("saved") private val _saved: Int? = null
) {
    val won: Int
        get() = _won ?: 0

    val commited: Int
        get() = _commited ?: 0

    val scored: Int
        get() = _scored ?: 0

    val missed: Int
        get() = _missed ?: 0

    val saved: Int
        get() = _saved ?: 0
}
