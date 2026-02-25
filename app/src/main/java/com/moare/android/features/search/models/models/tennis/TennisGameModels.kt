package com.moare.android.features.search.models.models.tennis

import com.moare.android.core.constants.StringConstants
import com.moare.android.features.search.models.models.common.GameForSchedule
import com.moare.android.features.search.models.models.football.FBGameStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// NOTE: homeTeam은 1, awayTeam은 2로 표시되는 필드들이 있음. ex) winnerCode, scoring, serving
@Serializable
data class TennisGame(
    val gameInfo: TennisGameInfo,
    val pointByPoint: List<TennisPointByPoint>? = null,
    val statistics: List<TennisGameStats>? = null,
)

@Serializable
data class TennisGameInfo(
    val status: TennisGameStatus? = null,
    val homeTeam: TennisGameTeam? = null,
    val awayTeam: TennisGameTeam? = null,
    val homeScore: TennisGameScore? = null,
    val awayScore: TennisGameScore? = null,
    val roundInfo: TennisGameRoundInfo? = null,
    val venue: TennisGameVenue? = null,
    val tournament: TennisGameTournament? = null,
    val season: TennisGameSeason? = null,
    val time: TennisGameTime? = null,
    @SerialName("id") private val _id: Int? = null,
    @SerialName("gameDate") private val _gameDate: String? = null,
    @SerialName("winnerCode") private val _winnerCode: Int? = null,
    @SerialName("defaultPeriodCount") private val _defaultPeriodCount: Int? = null,
    @SerialName("groundType") private val _groundType: String? = null,
) {
    val id: Int get() = _id ?: 0
    val gameDate: String get() = _gameDate ?: ""
    val winnerCode: Int get() = _winnerCode ?: -1
    val defaultPeriodCount: Int get() = _defaultPeriodCount ?: 3
    val groundType: String get() = _groundType ?: ""

    val isGameFinished: Boolean get() = winnerCode != -1 // CHECK: status로 판단하는게 맞을려나?
    val isHomeWinner: Boolean get() = winnerCode == 1
}

@Serializable
data class TennisGameStatus(
    @SerialName("code") private val _code: Int? = null,
    @SerialName("description") private val _description: String? = null,
    @SerialName("type") private val _type: String? = null,
) {
    val code: Int get() = _code ?: 0
    val description: String get() = _description ?: ""
    val type: String get() = _type ?: ""
}

@Serializable
data class TennisGameTeam(
    val country: TennisCountry? = null,
    @SerialName("fullName") private val _fullName: String? = null,
    @SerialName("shortName") private val _shortName: String? = null,
    @SerialName("gender") private val _gender: String? = null,
    @SerialName("id") private val _id: Int? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("nameCode") private val _nameCode: String? = null,
    @SerialName("national") private val _national: Boolean? = null,
    @SerialName("slug") private val _slug: String? = null,
) {
    val fullName: String get() = _fullName ?: ""
    val shortName: String get() = _shortName ?: ""
    val gender: String get() = _gender ?: "M"
    val id: Int get() = _id ?: 0
    val name: String get() = _name ?: ""
    val nameCode: String get() = _nameCode ?: ""
    val national: Boolean get() = _national ?: false
    val slug: String get() = _slug ?: ""
}

@Serializable
data class TennisGameScore(
    @SerialName("current") private val _current: Int? = null,
    @SerialName("display") private val _display: Int? = null,
    @SerialName("normaltime") private val _normaltime: Int? = null,
    // NOTE: null 값이 필요한 프로퍼티는 _ 네이밍 사용없이 그냥 사용.
    val period1: Int? = null,
    val period2: Int? = null,
    val period3: Int? = null,
    val period4: Int? = null,
    val period5: Int? = null,
    val period1TieBreak: Int? = null,
    val period2TieBreak: Int? = null,
    val period3TieBreak: Int? = null,
    val period4TieBreak: Int? = null,
    val period5TieBreak: Int? = null,
    @SerialName("point") private val _point: String? = null,
) {
    val current: Int get() = _current ?: 0
    val display: Int get() = _display ?: 0
    val normaltime: Int get() = _normaltime ?: 0
    val point: String get() = _point ?: ""

    val periods: List<Int?> get() = listOf(period1, period2, period3, period4, period5)
    val periodsTieBreak: List<Int?> get() = listOf(period1TieBreak, period2TieBreak, period3TieBreak, period4TieBreak, period5TieBreak)
}

@Serializable
data class TennisGameRoundInfo(
    @SerialName("name") private val _name: String? = null,
    @SerialName("round") private val _round: Int? = null,
    @SerialName("slug") private val _slug: String? = null,
) {
    val name: String get() = _name ?: ""
    val round: Int get() = _round ?: 0
    val slug: String get() = _slug ?: ""
}

@Serializable
data class TennisGameVenue(
    val city: TennisName? = null,
    val country: TennisCountry? = null,
    val stadium: TennisName? = null,
    @SerialName("hidden") private val _hidden: Boolean? = null,
    @SerialName("id") private val _id: Int? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("slug") private val _slug: String? = null,
) {
    val hidden: Boolean get() = _hidden ?: true
    val id: Int get() = _id ?: 0
    val name: String get() = _name ?: ""
    val slug: String get() = _slug ?: ""
}

@Serializable
data class TennisGameTournament(
    val category: TennisTournamentCategory? = null,
    val uniqueTournament: TennisUniqueTournament? = null,
    @SerialName("id") private val _id: Int? = null,
    @SerialName("competitionType") private val _competitionType: Int? = null,
    @SerialName("startTimestamp") private val _startTimestamp: Int? = null,
    @SerialName("endTimestamp") private val _endTimestamp: Int? = null,
    @SerialName("isGroup") private val _isGroup: Boolean? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("slug") private val _slug: String? = null,
) {
    val id: Int get() = _id ?: 0
    val competitionType: Int get() = _competitionType ?: 0
    val startTimestamp: Int get() = _startTimestamp ?: 0
    val endTimestamp: Int get() = _endTimestamp ?: 0
    val isGroup: Boolean get() = _isGroup ?: false
    val name: String get() = _name ?: ""
    val slug: String get() = _slug ?: ""
}

@Serializable
data class TennisTournamentCategory(
    val country: TennisCountry? = null,
    @SerialName("id") private val _id: Int? = null,
    @SerialName("flag") private val _flag: String? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("slug") private val _slug: String? = null,
) {
    val id: Int get() = _id ?: 0
    val flag: String get() = _flag ?: ""
    val name: String get() = _name ?: ""
    val slug: String get() = _slug ?: ""
}

@Serializable
data class TennisUniqueTournament(
    val category: TennisTournamentCategory? = null,
    @SerialName("id") private val _id: Int? = null,
    @SerialName("displayInverseHomeAwayTeams") private val _displayInverseHomeAwayTeams: Boolean? = null,
    @SerialName("hasRounds") private val _hasRounds: Boolean? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("slug") private val _slug: String? = null,
    @SerialName("tennisPoints") private val _tennisPoints: Int? = null,
) {
    val id: Int get() = _id ?: 0
    val displayInverseHomeAwayTeams: Boolean get() = _displayInverseHomeAwayTeams ?: false
    val hasRounds: Boolean get() = _hasRounds ?: true
    val name: String get() = _name ?: ""
    val slug: String get() = _slug ?: ""
    val tennisPoints: Int get() = _tennisPoints ?: 0
}

@Serializable
data class TennisGameSeason(
    @SerialName("id") private val _id: Int? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("year") private val _year: String? = null,
) {
    val id: Int get() = _id ?: 0
    val name: String get() = _name ?: ""
    val year: String get() = _year ?: ""
}

@Serializable
data class TennisGameTime(
    @SerialName("currentPeriodStartTimestamp") private val _currentPeriodStartTimestamp: Int? = null,
    val period1: Int? = null,
    val period2: Int? = null,
    val period3: Int? = null,
    val period4: Int? = null,
    val period5: Int? = null,
) {
    val currentPeriodStartTimestamp: Int get() = _currentPeriodStartTimestamp ?: 0
}

@Serializable
data class TennisPointByPoint(
    val games: List<TennisPointByPointGame>? = null,
    @SerialName("set") private val _set: Int? = null,
) {
    val set: Int get() = _set ?: 0
}

@Serializable
data class TennisPointByPointGame(
    val points: List<TennisGamePoint>? = null,
    val score: TennisPointByPointGameScore? = null,
    @SerialName("game") private val _game: Int? = null,
) {
    val game: Int get() = _game ?: 0
}

@Serializable
data class TennisGamePoint(
    @SerialName("homePoint") private val _homePoint: String? = null,
    @SerialName("homePointType") private val _homePointType: Int? = null,
    @SerialName("awayPoint") private val _awayPoint: String? = null,
    @SerialName("awayPointType") private val _awayPointType: Int? = null,
    @SerialName("pointDescription") private val _pointDescription: Int? = null, // 1: Ace(에이스), 2: Double fault(더블 폴트)
) {
    val homePoint: String get() = _homePoint ?: "0"
    val homePointType: Int get() = _homePointType ?: 0
    val awayPoint: String get() = _awayPoint ?: "0"
    val awayPointType: Int get() = _awayPointType ?: 0
    val pointDescription: Int get() = _pointDescription ?: 0
}

@Serializable
data class TennisPointByPointGameScore(
    @SerialName("homeScore") private val _homeScore: Int? = null,
    @SerialName("awayScore") private val _awayScore: Int? = null,
    @SerialName("scoring") private val _scoring: Int? = null, // 아직 게임이 안끝났으면 -1
    @SerialName("serving") private val _serving: Int? = null,
) {
    val homeScore: Int get() = _homeScore ?: 0
    val awayScore: Int get() = _awayScore ?: 0
    val scoring: Int get() = _scoring ?: 0
    val serving: Int get() = _serving ?: 0

    val isHomeWinner: Boolean get() = scoring == 1
    val isAwayWinner: Boolean get() = scoring == 2
    val isGameFinished: Boolean get() = scoring != -1
    val isHomeServing: Boolean get() = serving == 1
    val isTieBreak: Boolean
        get() = if (isGameFinished) {
            homeScore == 7 || awayScore == 7
        } else {
            homeScore == 6 && awayScore == 6
        }
}

@Serializable
data class TennisGameStats(
    val statisticsItems: List<TennisGameStatsItem>? = null,
    @SerialName("groupName") private val _groupName: String? = null,
) {
    val groupName: String get() = _groupName ?: ""

    fun itemsForDisplay(): List<TennisGameStatsItem> {
        val items = statisticsItems ?: return emptyList()
        return StringConstants.Tennis.playerStatKeyList.mapNotNull { key ->
            items.firstOrNull { it.key == key }
        }
    }
}

@Serializable
data class TennisGameStatsItem(
    @SerialName("key") private val _key: String? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("home") private val _home: String? = null,
    @SerialName("homeValue") private val _homeValue: Int? = null,
    @SerialName("homeTotal") private val _homeTotal: Int? = null,
    @SerialName("away") private val _away: String? = null,
    @SerialName("awayValue") private val _awayValue: Int? = null,
    @SerialName("awayTotal") private val _awayTotal: Int? = null,
    @SerialName("compareCode") private val _compareCode: Int? = null,
    @SerialName("renderType") private val _renderType: Int? = null,
    @SerialName("statisticsType") private val _statisticsType: String? = null,
    @SerialName("valueType") private val _valueType: String? = null,
) {
    val key: String get() = _key ?: ""
    val name: String get() = _name ?: ""
    val home: String get() = _home ?: ""
    val homeValue: Int get() = _homeValue ?: 0
    val homeTotal: Int get() = _homeTotal ?: 0
    val away: String get() = _away ?: ""
    val awayValue: Int get() = _awayValue ?: 0
    val awayTotal: Int get() = _awayTotal ?: 0
    val compareCode: Int get() = _compareCode ?: 0
    val renderType: Int get() = _renderType ?: 0
    val statisticsType: String get() = _statisticsType ?: ""
    val valueType: String get() = _valueType ?: ""

    val krname: String get() = StringConstants.Tennis.playerStatKrnameMap[key] ?: ""
}

@Serializable
data class TennisGameInfoForSchedule(
    val roundInfo: TennisGameRoundInfo? = null,
    val homeTeam: TennisGameTeam? = null,
    val awayTeam: TennisGameTeam? = null,
    @SerialName("winnerCode") private val _winnerCode: Int? = null
) {
    val winnerCode: Int get() = _winnerCode ?: -1

    val isGameFinished: Boolean get() = _winnerCode != -1
    val isHomeWinner: Boolean get() = _winnerCode == 1
}

typealias TennisGameForSchedule = GameForSchedule<TennisGameInfoForSchedule>