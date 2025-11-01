package com.moare.android.features.search.models.models.nba

import com.moare.android.features.search.models.models.common.GameForSchedule
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NBAGame(
    val boxScoreTraditional: NBABoxScoreTraditional? = null,
    val gameInfo: NBAGameInfo? = null,
    val gameSummary: NBAGameSummary? = null,
    val inativePlayers: List<NBAPlayerForInactive>? = null,
    val lastMeeting: NBALastMeeting? = null,
    val lineScore: List<NBALineScore>? = null,
    val officials: List<NBAOfficial>? = null,
    val otherStats: List<NBAOtherStats>? = null,
    val seasonSeries: NBASeasonSeries? = null
)

@Serializable
data class NBABoxScoreTraditional(
    val awayTeam: NBABoxScoreTeam,
    val homeTeam: NBABoxScoreTeam,
    @SerialName("awayTeamId") private val _awayTeamId: Int? = null,
    @SerialName("gameId") private val _gameId: String? = null,
    @SerialName("homeTeamId") private val _homeTeamId: Int? = null,
) {
    val awayTeamId: Int get() = _awayTeamId ?: 0
    val gameId: String get() = _gameId ?: ""
    val homeTeamId: Int get() = _homeTeamId ?: 0
}

@Serializable
data class NBABoxScoreTeam(
    val bench: NBAGameBoxScoreStats,
    val players: List<NBABoxScoreTeamPlayer> = emptyList(),
    val starters: NBAGameBoxScoreStats,
    val statistics: NBAGameBoxScoreStats,
    @SerialName("teamCity") private val _teamCity: String? = null,
    @SerialName("teamId") private val _teamId: Int? = null,
    @SerialName("teamName") private val _teamName: String? = null,
    @SerialName("teamSlug") private val _teamSlug: String? = null,
    @SerialName("teamTricode") private val _teamTricode: String? = null,
) {
    val teamCity: String get() = _teamCity ?: ""
    val teamId: Int get() = _teamId ?: 0
    val teamName: String get() = _teamName ?: ""
    val teamSlug: String get() = _teamSlug ?: ""
    val teamTricode: String get() = _teamTricode ?: ""
}

@Serializable
data class NBABoxScoreTeamPlayer(
    @SerialName("comment") private val _comment: String? = null,
    @SerialName("familyName") private val _familyName: String? = null,
    @SerialName("firstName") private val _firstName: String? = null,
    @SerialName("jerseyNum") private val _jerseyNum: String? = null,
    @SerialName("nameI") private val _nameI: String? = null,
    @SerialName("personId") private val _personId: Int? = null,
    @SerialName("playerSlug") private val _playerSlug: String? = null,
    @SerialName("position") private val _position: String? = null,
    val statistics: NBAGameBoxScoreStats
) {
    val comment: String get() = _comment ?: ""
    val familyName: String get() = _familyName ?: ""
    val firstName: String get() = _firstName ?: ""
    val jerseyNum: String get() = _jerseyNum?.trim() ?: ""
    val nameI: String get() = _nameI ?: ""
    val personId: Int get() = _personId ?: 0
    val playerSlug: String get() = _playerSlug ?: ""
    val position: String get() = _position ?: ""
}

@Serializable
data class NBAGameBoxScoreStats(
    @SerialName("assists") private val _assists: Int? = null,
    @SerialName("blocks") private val _blocks: Int? = null,
    @SerialName("fieldGoalsAttempted") private val _fieldGoalsAttempted: Int? = null,
    @SerialName("fieldGoalsMade") private val _fieldGoalsMade: Int? = null,
    @SerialName("fieldGoalsPercentage") private var _fieldGoalsPercentage: Double? = null,
    @SerialName("foulsPersonal") private val _foulsPersonal: Int? = null,
    @SerialName("freeThrowsAttempted") private val _freeThrowsAttempted: Int? = null,
    @SerialName("freeThrowsMade") private val _freeThrowsMade: Int? = null,
    @SerialName("freeThrowsPercentage") private var _freeThrowsPercentage: Double? = null,
    @SerialName("minutes") private val _minutes: String? = null,
    @SerialName("plusMinusPoints") private var _plusMinusPoints: Int? = null,
    @SerialName("points") private val _points: Int? = null,
    @SerialName("reboundsDefensive") private val _reboundsDefensive: Int? = null,
    @SerialName("reboundsOffensive") private val _reboundsOffensive: Int? = null,
    @SerialName("reboundsTotal") private val _reboundsTotal: Int? = null,
    @SerialName("steals") private val _steals: Int? = null,
    @SerialName("threePointersAttempted") private val _threePointersAttempted: Int? = null,
    @SerialName("threePointersMade") private val _threePointersMade: Int? = null,
    @SerialName("threePointersPercentage") private var _threePointersPercentage: Double? = null,
    @SerialName("turnovers") private val _turnovers: Int? = null,
) {
    val assists: Int get() = _assists ?: 0
    val blocks: Int get() = _blocks ?: 0
    val fieldGoalsAttempted: Int get() = _fieldGoalsAttempted ?: 0
    val fieldGoalsMade: Int get() = _fieldGoalsMade ?: 0
    var fieldGoalsPercentage: Double
        get() = _fieldGoalsPercentage ?: 0.0
        set(value) { _fieldGoalsPercentage = value }
    val foulsPersonal: Int get() = _foulsPersonal ?: 0
    val freeThrowsAttempted: Int get() = _freeThrowsAttempted ?: 0
    val freeThrowsMade: Int get() = _freeThrowsMade ?: 0
    var freeThrowsPercentage: Double
        get() = _freeThrowsPercentage ?: 0.0
        set(value) { _freeThrowsPercentage = value }
    val minutes: String get() = if (_minutes.isNullOrBlank()) "0:0" else _minutes
    var plusMinusPoints: Int
        get() = _plusMinusPoints ?: 0
        set(value) { _plusMinusPoints = value }
    val points: Int get() = _points ?: 0
    val reboundsDefensive: Int get() = _reboundsDefensive ?: 0
    val reboundsOffensive: Int get() = _reboundsOffensive ?: 0
    val reboundsTotal: Int get() = _reboundsTotal ?: 0
    val steals: Int get() = _steals ?: 0
    val threePointersAttempted: Int get() = _threePointersAttempted ?: 0
    val threePointersMade: Int get() = _threePointersMade ?: 0
    var threePointersPercentage: Double
        get() = _threePointersPercentage ?: 0.0
        set(value) { _threePointersPercentage = value }
    val turnovers: Int get() = _turnovers ?: 0
}

@Serializable
data class NBAGameInfo(
    @SerialName("attendance") private val _attendance: Int? = null,
    @SerialName("gameTime") private val _gameTime: String? = null,
) {
    val attendance: Int get() = _attendance ?: 0
    val gameTime: String get() = _gameTime ?: ""
}

@Serializable
data class NBAGameSummary(
    @SerialName("gameId") private val _gameId: String? = null,
    @SerialName("gameDate") private val _gameDate: String? = null,
    @SerialName("homeTeamId") private val _homeTeamId: Int? = null,
    @SerialName("awayTeamId") private val _awayTeamId: Int? = null,
    @SerialName("gameStatus") private val _gameStatus: Int? = null,
    @SerialName("weekNumber") private val _weekNumber: Int? = null,
    @SerialName("weekName") private val _weekName: String? = null,
    @SerialName("seriesGameNumber") private val _seriesGameNumber: String? = null,
    @SerialName("gameLabel") private val _gameLabel: String? = null,
    @SerialName("gameSubLabel") private val _gameSubLabel: String? = null,
    @SerialName("seriesText") private val _seriesText: String? = null,
    @SerialName("duration") private val _duration: Int? = null,
    @SerialName("gameCode") private val _gameCode: String? = null,
    @SerialName("gameStatusText") private val _gameStatusText: String? = null,
    @SerialName("regulationPeriods") private val _regulationPeriods: Int? = null,
    @SerialName("period") private val _period: Int? = null,
    @SerialName("gameClock") private val _gameClock: String? = null,
    @SerialName("attendance") private val _attendance: Int? = null,
) {
    val gameId: String get() = _gameId ?: ""
    val gameDate: String get() = _gameDate ?: ""
    val homeTeamId: Int get() = _homeTeamId ?: 0
    val awayTeamId: Int get() = _awayTeamId ?: 0
    val gameStatus: Int get() = _gameStatus ?: 0
    val weekNumber: Int get() = _weekNumber ?: 0
    val weekName: String get() = _weekName ?: ""
    val seriesGameNumber: String get() = _seriesGameNumber ?: ""
    val gameLabel: String get() = _gameLabel ?: ""
    val gameSubLabel: String get() = _gameSubLabel ?: ""
    val seriesText: String get() = _seriesText ?: ""
    val duration: Int get() = _duration ?: 0
    val gameCode: String get() = _gameCode ?: ""
    val gameStatusText: String get() = _gameStatusText ?: ""
    val regulationPeriods: Int get() = _regulationPeriods ?: 0
    val period: Int get() = _period ?: 0
    val gameClock: String get() = _gameClock ?: ""
    val attendance: Int get() = _attendance ?: 0
}

@Serializable
data class NBAPlayerForInactive(
    @SerialName("firstName") private val _firstName: String? = null,
    @SerialName("jerseyNum") private val _jerseyNum: String? = null,
    @SerialName("lastName") private val _lastName: String? = null,
    @SerialName("playerId") private val _playerId: Int? = null,
    @SerialName("teamAbbreviation") private val _teamAbbreviation: String? = null,
    @SerialName("teamCity") private val _teamCity: String? = null,
    @SerialName("teamId") private val _teamId: Int? = null,
    @SerialName("teamName") private val _teamName: String? = null,
) {
    val firstName: String get() = _firstName ?: ""
    val jerseyNum: String get() = _jerseyNum?.trim() ?: "" // jerseyNum 데이터에 공백값이 들어가 있음
    val lastName: String get() = _lastName ?: ""
    val playerId: Int get() = _playerId ?: 0
    val teamAbbreviation: String get() = _teamAbbreviation ?: ""
    val teamCity: String get() = _teamCity ?: ""
    val teamId: Int get() = _teamId ?: 0
    val teamName: String get() = _teamName ?: ""
}

@Serializable
data class NBALastMeeting(
    @SerialName("lastGameDateEst") private val _lastGameDateEst: String? = null,
    @SerialName("lastGameHomeTeamAbbreviation") private val _lastGameHomeTeamAbbreviation: String? = null,
    @SerialName("lastGameHomeTeamCity") private val _lastGameHomeTeamCity: String? = null,
    @SerialName("lastGameHomeTeamId") private val _lastGameHomeTeamId: Int? = null,
    @SerialName("lastGameHomeTeamName") private val _lastGameHomeTeamName: String? = null,
    @SerialName("lastGameHomeTeamPoints") private val _lastGameHomeTeamPoints: Int? = null,
    @SerialName("lastGameId") private val _lastGameId: String? = null,
    @SerialName("lastGameVisitorTeamCity") private val _lastGameVisitorTeamCity: String? = null,
    @SerialName("lastGameVisitorTeamCity1") private val _lastGameVisitorTeamCity1: String? = null,
    @SerialName("lastGameVisitorTeamId") private val _lastGameVisitorTeamId: Int? = null,
    @SerialName("lastGameVisitorTeamName") private val _lastGameVisitorTeamName: String? = null,
    @SerialName("lastGameVisitorTeamPoints") private val _lastGameVisitorTeamPoints: Int? = null,
) {
    val lastGameDateEst: String get() = _lastGameDateEst ?: ""
    val lastGameHomeTeamAbbreviation: String get() = _lastGameHomeTeamAbbreviation ?: ""
    val lastGameHomeTeamCity: String get() = _lastGameHomeTeamCity ?: ""
    val lastGameHomeTeamId: Int get() = _lastGameHomeTeamId ?: 0
    val lastGameHomeTeamName: String get() = _lastGameHomeTeamName ?: ""
    val lastGameHomeTeamPoints: Int get() = _lastGameHomeTeamPoints ?: 0
    val lastGameId: String get() = _lastGameId ?: ""
    val lastGameVisitorTeamCity: String get() = _lastGameVisitorTeamCity ?: ""
    val lastGameVisitorTeamCity1: String get() = _lastGameVisitorTeamCity1 ?: ""
    val lastGameVisitorTeamId: Int get() = _lastGameVisitorTeamId ?: 0
    val lastGameVisitorTeamName: String get() = _lastGameVisitorTeamName ?: ""
    val lastGameVisitorTeamPoints: Int get() = _lastGameVisitorTeamPoints ?: 0
}

@Serializable
data class NBALineScore(
    @SerialName("pts") private val _pts: Int? = null,
    @SerialName("ptsOt1") private val _ptsOt1: Int? = null,
    @SerialName("ptsOt2") private val _ptsOt2: Int? = null,
    @SerialName("ptsOt3") private val _ptsOt3: Int? = null,
    @SerialName("ptsOt4") private val _ptsOt4: Int? = null,
    @SerialName("ptsOt5") private val _ptsOt5: Int? = null,
    @SerialName("ptsOt6") private val _ptsOt6: Int? = null,
    @SerialName("ptsOt7") private val _ptsOt7: Int? = null,
    @SerialName("ptsOt8") private val _ptsOt8: Int? = null,
    @SerialName("ptsOt9") private val _ptsOt9: Int? = null,
    @SerialName("ptsOt10") private val _ptsOt10: Int? = null,
    @SerialName("ptsQtr1") private val _ptsQtr1: Int? = null,
    @SerialName("ptsQtr2") private val _ptsQtr2: Int? = null,
    @SerialName("ptsQtr3") private val _ptsQtr3: Int? = null,
    @SerialName("ptsQtr4") private val _ptsQtr4: Int? = null,
    @SerialName("teamAbbreviation") private val _teamAbbreviation: String? = null,
    @SerialName("teamCityName") private val _teamCityName: String? = null,
    @SerialName("teamId") private val _teamId: Int? = null,
    @SerialName("teamNickname") private val _teamNickname: String? = null,
    @SerialName("teamWinsLosses") private val _teamWinsLosses: String? = null,
) {
    val pts: Int? get() = _pts
    val ptsOt1: Int? get() = _ptsOt1
    val ptsOt2: Int? get() = _ptsOt2
    val ptsOt3: Int? get() = _ptsOt3
    val ptsOt4: Int? get() = _ptsOt4
    val ptsOt5: Int? get() = _ptsOt5
    val ptsOt6: Int? get() = _ptsOt6
    val ptsOt7: Int? get() = _ptsOt7
    val ptsOt8: Int? get() = _ptsOt8
    val ptsOt9: Int? get() = _ptsOt9
    val ptsOt10: Int? get() = _ptsOt10
    val ptsQtr1: Int? get() = _ptsQtr1
    val ptsQtr2: Int? get() = _ptsQtr2
    val ptsQtr3: Int? get() = _ptsQtr3
    val ptsQtr4: Int? get() = _ptsQtr4
    val teamAbbreviation: String get() = _teamAbbreviation ?: ""
    val teamCityName: String get() = _teamCityName ?: ""
    val teamId: Int get() = _teamId ?: 0
    val teamNickname: String get() = _teamNickname ?: ""
    val teamWinsLosses: String get() = _teamWinsLosses ?: ""
}

@Serializable
data class NBAOfficial(
    @SerialName("personId") private val _personId: Int? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("jerseyNum") private val _jerseyNum: String? = null,
    @SerialName("assignment") private val _assignment: String? = null,
) {
    val personId: Int get() = _personId ?: 0
    val name: String get() = _name ?: ""
    val jerseyNum: String get() = _jerseyNum ?: ""
    val assignment: String get() = _assignment ?: ""
}

@Serializable
data class NBAOtherStats(
    @SerialName("largestLead") private val _largestLead: Int? = null,
    @SerialName("leadChanges") private val _leadChanges: Int? = null,
    @SerialName("ptsFb") private val _ptsFb: Int? = null,
    @SerialName("ptsOffTo") private val _ptsOffTo: Int? = null,
    @SerialName("ptsPaint") private val _ptsPaint: Int? = null,
    @SerialName("pts2ndChance") private val _pts2ndChance: Int? = null,
    @SerialName("teamAbbreviation") private val _teamAbbreviation: String? = null,
    @SerialName("teamCity") private val _teamCity: String? = null,
    @SerialName("teamId") private val _teamId: Int? = null,
    @SerialName("teamRebounds") private val _teamRebounds: Int? = null,
    @SerialName("teamTurnovers") private val _teamTurnovers: Int? = null,
    @SerialName("timesTied") private val _timesTied: Int? = null,
    @SerialName("totalTurnovers") private val _totalTurnovers: Int? = null,
) {
    val largestLead: Int get() = _largestLead ?: 0
    val leadChanges: Int get() = _leadChanges ?: 0
    val ptsFb: Int get() = _ptsFb ?: 0
    val ptsOffTo: Int get() = _ptsOffTo ?: 0
    val ptsPaint: Int get() = _ptsPaint ?: 0
    val pts2ndChance: Int get() = _pts2ndChance ?: 0
    val teamAbbreviation: String get() = _teamAbbreviation ?: ""
    val teamCity: String get() = _teamCity ?: ""
    val teamId: Int get() = _teamId ?: 0
    val teamRebounds: Int get() = _teamRebounds ?: 0
    val teamTurnovers: Int get() = _teamTurnovers ?: 0
    val timesTied: Int get() = _timesTied ?: 0
    val totalTurnovers: Int get() = _totalTurnovers ?: 0
}

@Serializable
data class NBASeasonSeries(
    @SerialName("homeTeamId") private val _homeTeamId: Int? = null,
    @SerialName("homeTeamLosses") private val _homeTeamLosses: Int? = null,
    @SerialName("homeTeamWins") private val _homeTeamWins: Int? = null,
    @SerialName("seriesLeader") private val _seriesLeader: String? = null,
    @SerialName("visitorTeamId") private val _visitorTeamId: Int? = null,
) {
    val homeTeamId: Int get() = _homeTeamId ?: 0
    val homeTeamLosses: Int get() = _homeTeamLosses ?: 0
    val homeTeamWins: Int get() = _homeTeamWins ?: 0
    val seriesLeader: String get() = _seriesLeader ?: ""
    val visitorTeamId: Int get() = _visitorTeamId ?: 0
}

typealias NBAGameForSchedule = GameForSchedule<NBAGameSummary>













