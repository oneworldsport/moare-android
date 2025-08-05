package com.moare.android.features.search.models.models.mlb

import com.moare.android.features.search.models.models.common.GameForSchedule
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MLBGame(
    val boxscore: MLBGameBoxscore? = null,
    val decisions: MLBGameDecisions? = null,
    val game: MLBGameData,
    val gameInfo: MLBGameInfo,
    val linescore: MLBGameLineScore,
    val moundVisits: MLBGameMoundVisits,
    val probablePitchers: MLBGameProbablePitchers,
    val review: MLBGameReview,
    val status: MLBGameStatus,
    val teams: MLBGameTeams,
    val weather: MLBGameWeather
)

@Serializable
data class MLBGameBoxscore(
    val info: List<MLBLabelObj>? = null,
    val officials: List<MBLGameBoxscoreOfficial>,
    val teams: MLBGameBoxscoreTeams
)

@Serializable
data class MBLGameBoxscoreOfficial(
    val official: MLBFullNameObj,
    @SerialName("officialType") private val _officialType: String? = null,
) {
    val officialType: String get() = _officialType ?: ""
}

@Serializable
data class MLBGameBoxscoreTeams(
    val away: MLBGameBoxscoreTeamData,
    val home: MLBGameBoxscoreTeamData
)

@Serializable
data class MLBGameBoxscoreTeamData(
    @SerialName("batters") private val _batters: List<Int>? = null,
    @SerialName("battingOrder") private val _battingOrder: List<Int>? = null,
    @SerialName("bench") private val _bench: List<Int>? = null,
    @SerialName("bullpen") private val _bullpen: List<Int>? = null,
    @SerialName("info") private val _info: List<MLBGameBoxscoreTeamInfo>? = null,
//    @SerialName("note") private val _note: List<String>? = null,
    @SerialName("pitchers") private val _pitchers: List<Int>? = null,
    @SerialName("players") private val _players: Map<String, MLBGameBoxscoreTeamPlayer>? = null,
    val team: MLBGameBoxsocreTeamInfo,
    val teamStats: MLBGameBoxscoreStats
) {
    val batters: List<Int> get() = _batters ?: emptyList()
    val battingOrder: List<Int> get() = _battingOrder ?: emptyList()
    val bench: List<Int> get() = _bench ?: emptyList()
    val bullpen: List<Int> get() = _bullpen ?: emptyList()
    val info: List<MLBGameBoxscoreTeamInfo> get() = _info ?: emptyList()
//    val note: List<String> get() = _note ?: emptyList()
    val pitchers: List<Int> get() = _pitchers ?: emptyList()
    val players: Map<String, MLBGameBoxscoreTeamPlayer> get() = _players ?: emptyMap()
}

@Serializable
data class MLBGameBoxscoreTeamInfo(
    @SerialName("fieldList") private val _fieldList: List<MLBLabelObj>? = null,
    @SerialName("title") private val _title: String? = null,
) {
    val fieldList: List<MLBLabelObj> get() = _fieldList ?: emptyList()
    val title: String get() = _title ?: ""
}

@Serializable
data class MLBGameBoxscoreTeamPlayer(
    val gameStatus: MLBGameBoxscorePlayerStatus,
    @SerialName("jerseyNumber") private val _jerseyNumber: String? = null,
    @SerialName("parentTeamId") private val _parentTeamId: Int? = null,
    val person: MLBFullNameObj? = null,
    val position: MLBAbbreviationCodeObj? = null,
    val seasonStats: MLBGameBoxscoreStats? = null,
    val stats: MLBGameBoxscoreStats? = null,
    val status: MLBCodeObj? = null,
    @SerialName("battingOrder") private val _battingOrder: String? = null,
    @SerialName("allPositions") private val _allPositions: List<MLBAbbreviationCodeObj>? = null,
) {
    val jerseyNumber: String get() = _jerseyNumber ?: ""
    val parentTeamId: Int get() = _parentTeamId ?: 0
    val battingOrder: String get() = _battingOrder ?: ""
    val allPositions: List<MLBAbbreviationCodeObj> get() = _allPositions ?: emptyList()
}

@Serializable
data class MLBGameBoxscorePlayerStatus(
    @SerialName("isCurrentBatter") private val _isCurrentBatter: Boolean? = null,
    @SerialName("isCurrentPitcher") private val _isCurrentPitcher: Boolean? = null,
    @SerialName("isOnBench") private val _isOnBench: Boolean? = null,
    @SerialName("isSubstitute") private val _isSubstitute: Boolean? = null,
) {
    val isCurrentBatter: Boolean get() = _isCurrentBatter ?: false
    val isCurrentPitcher: Boolean get() = _isCurrentPitcher ?: false
    val isOnBench: Boolean get() = _isOnBench ?: false
    val isSubstitute: Boolean get() = _isSubstitute ?: false
}

@Serializable
data class MLBGameBoxscoreStats(
    val batting: MLBPlayerHittingStats?,
    val fielding: MLBPlayerFieldingStats?,
    val pitching: MLBPlayerPitchingStats?, // 데이터 없으면 null이 아니라 {}으로 옴. 위 다른 필드도 마찬가지일듯.
)

@Serializable
data class MLBGameBoxsocreTeamInfo(
    @SerialName("allStarStatus") private val _allStarStatus: String? = null,
    @SerialName("id") private val _id: Int? = null,
    @SerialName("link") private val _link: String? = null,
    @SerialName("name") private val _name: String? = null,
    val springLeague: MLBAbbreviationIdObj? = null
) {
    val allStarStatus: String get() = _allStarStatus ?: ""
    val id: Int get() = _id ?: 0
    val link: String get() = _link ?: ""
    val name: String get() = _name ?: ""
}

@Serializable
data class MLBGameDecisions(
    val loser: MLBFullNameObj,
    val save: MLBFullNameObj? = null,
    val winner: MLBFullNameObj
)

@Serializable
data class MLBGameData(
    @SerialName("calendarEventID") private val _calendarEventID: String? = null,
    @SerialName("doubleHeader") private val _doubleHeader: String? = null,
    @SerialName("gamedayType") private val _gamedayType: String? = null,
    @SerialName("gameNumber") private val _gameNumber: Int? = null,
    @SerialName("id") private val _id: String? = null,
    @SerialName("pk") private val _pk: Int? = null,
    @SerialName("season") private val _season: String? = null,
    @SerialName("seasonDisplay") private val _seasonDisplay: String? = null,
    @SerialName("tiebreaker") private val _tiebreaker: String? = null,
    @SerialName("type") private val _type: String? = null,
) {
    val calendarEventID: String get() = _calendarEventID ?: ""
    val doubleHeader: String get() = _doubleHeader ?: ""
    val gamedayType: String get() = _gamedayType ?: ""
    val gameNumber: Int get() = _gameNumber ?: 0
    val id: String get() = _id ?: ""
    val pk: Int get() = _pk ?: 0
    val season: String get() = _season ?: ""
    val seasonDisplay: String get() = _seasonDisplay ?: ""
    val tiebreaker: String get() = _tiebreaker ?: ""
    val type: String get() = _type ?: ""
}

@Serializable
data class MLBGameInfo(
    @SerialName("attendance") private val _attendance: Int? = null,
    @SerialName("firstPitch") private val _firstPitch: String? = null,
    @SerialName("gameDurationMinutes") private val _gameDurationMinutes: Int? = null,
    @SerialName("gameDate") private val _gameDate: String? = null
) {
    val attendance: Int get() = _attendance ?: 0
    val firstPitch: String get() = _firstPitch ?: ""
    val gameDurationMinutes: Int get() = _gameDurationMinutes ?: 0
    val gameDate: String get() = _gameDate ?: ""
}

@Serializable
data class MLBGameLineScore(
    @SerialName("balls") private val _balls: Int? = null,
    @SerialName("currentInning") private val _currentInning: Int? = null,
    @SerialName("currentInningOrdinal") private val _currentInningOrdinal: String? = null,
    val defense: MLBGameLineScoreDefense? = null,
    @SerialName("inningHalf") private val _inningHalf: String? = null,
    val innings: List<MLBGameLineScoreInning>,
    @SerialName("inningState") private val _inningState: String? = null,
    @SerialName("isTopInning") private val _isTopInning: Boolean? = null,
    val offense: MLBGameLineScoreDefense? = null,
    @SerialName("outs") private val _outs: Int? = null,
    @SerialName("scheduledInnings") private val _scheduledInnings: Int? = null,
    @SerialName("strikes") private val _strikes: Int? = null,
    val teams: MLBGameLineScoreTeams
) {
    val balls: Int get() = _balls ?: 0
    val currentInning: Int get() = _currentInning ?: 0
    val currentInningOrdinal: String get() = _currentInningOrdinal ?: ""
    val inningHalf: String get() = _inningHalf ?: ""
    val inningState: String get() = _inningState ?: ""
    val isTopInning: Boolean get() = _isTopInning ?: false
    val outs: Int get() = _outs ?: 0
    val scheduledInnings: Int get() = _scheduledInnings ?: 0
    val strikes: Int get() = _strikes ?: 0
}

@Serializable
data class MLBGameLineScoreDefense(
    val batter: MLBFullNameObj? = null,
    @SerialName("battingOrder") private val _battingOrder: Int? = null,
    val onDeck: MLBFullNameObj? = null,
    val inHole: MLBFullNameObj? = null,
    val pitcher: MLBFullNameObj? = null,
    val team: MLBNameObj,
    val catcher: MLBFullNameObj? = null,
    val center: MLBFullNameObj? = null,
    val first: MLBFullNameObj? = null,
    val left: MLBFullNameObj? = null,
    val right: MLBFullNameObj? = null,
    val second: MLBFullNameObj? = null,
    val shortstop: MLBFullNameObj? = null,
    val third: MLBFullNameObj? = null
) {
    val battingOrder: Int get() = _battingOrder ?: 0
}

@Serializable
data class MLBGameLineScoreInning(
    val away: MLBGameLineScoreStats,
    val home: MLBGameLineScoreStats,
    @SerialName("num") private val _num: Int? = null,
    @SerialName("ordinalNum") private val _ordinalNum: String? = null
) {
    val num: Int get() = _num ?: 0
    val ordinalNum: String get() = _ordinalNum ?: ""
}

@Serializable
data class MLBGameLineScoreStats(
    @SerialName("errors") private val _errors: Int? = null,
    @SerialName("hits") private val _hits: Int? = null,
    @SerialName("leftOnBase") private val _leftOnBase: Int? = null,
    @SerialName("runs") private val _runs: Int? = null
) {
    val errors: Int get() = _errors ?: 0
    val hits: Int get() = _hits ?: 0
    val leftOnBase: Int get() = _leftOnBase ?: 0
    val runs: Int get() = _runs ?: 0
}

@Serializable
data class MLBGameLineScoreTeams(
    val away: MLBGameLineScoreStats,
    val home: MLBGameLineScoreStats
)

@Serializable
data class MLBGameMoundVisits(
    val away: MLBGameRemainingUsed,
    val home: MLBGameRemainingUsed
)

@Serializable
data class MLBGameRemainingUsed(
    @SerialName("remaining") private val _remaining: Int? = null,
    @SerialName("used") private val _used: Int? = null,
) {
    val remaining: Int get() = _remaining ?: 0
    val used: Int get() = _used ?: 0
}

@Serializable
data class MLBGameProbablePitchers(
    val away: MLBFullNameObj? = null,
    val home: MLBFullNameObj? = null
)

@Serializable
data class MLBGameReview(
    val away: MLBGameRemainingUsed,
    val home: MLBGameRemainingUsed,
    @SerialName("hasChallenges") private val _hasChallenges: Boolean? = null
) {
    val hasChallenges: Boolean get() = _hasChallenges ?: false
}

@Serializable
data class MLBGameStatus(
    @SerialName("abstractGameCode") private val _abstractGameCode: String? = null,
    @SerialName("abstractGameState") private val _abstractGameState: String? = null,
    @SerialName("codedGameState") private val _codedGameState: String? = null,
    @SerialName("detailedState") private val _detailedState: String? = null,
    @SerialName("startTimeTBD") private val _startTimeTBD: Boolean? = null,
    @SerialName("statusCode") private val _statusCode: String? = null,
) {
    val abstractGameCode: String get() = _abstractGameCode ?: ""
    val abstractGameState: String get() = _abstractGameState ?: ""
    val codedGameState: String get() = _codedGameState ?: ""
    val detailedState: String get() = _detailedState ?: ""
    val startTimeTBD: Boolean get() = _startTimeTBD ?: false
    val statusCode: String get() = _statusCode ?: ""
}

@Serializable
data class MLBGameTeams(
    val away: MLBGameTeamDetail,
    val home: MLBGameTeamDetail
)

@Serializable
data class MLBGameTeamDetail(
    @SerialName("abbreviation") private val _abbreviation: String? = null,
    @SerialName("allStarStatus") private val _allStarStatus: String? = null,
    @SerialName("clubName") private val _clubName: String? = null,
    val division: MLBNameObj,
    @SerialName("franchiseName") private val _franchiseName: String? = null,
    @SerialName("id") private val _id: Int? = null,
    val league: MLBNameObj,
    @SerialName("locationName") private val _locationName: String? = null,
    @SerialName("name") private val _name: String? = null,
    val record: MLBGameTeamRecord? = null,
    @SerialName("season") private val _season: Int? = null,
    @SerialName("shortName") private val _shortName: String? = null,
    @SerialName("teamCode") private val _teamCode: String? = null,
    @SerialName("teamName") private val _teamName: String? = null,
) {
    val abbreviation: String get() = _abbreviation ?: ""
    val allStarStatus: String get() = _allStarStatus ?: ""
    val clubName: String get() = _clubName ?: ""
    val franchiseName: String get() = _franchiseName ?: ""
    val id: Int get() = _id ?: 0
    val locationName: String get() = _locationName ?: ""
    val name: String get() = _name ?: ""
    val season: Int get() = _season ?: 0
    val shortName: String get() = _shortName ?: ""
    val teamCode: String get() = _teamCode ?: ""
    val teamName: String get() = _teamName ?: ""
}

@Serializable
data class MLBGameTeamRecord(
    @SerialName("conferenceGamesBack") private val _conferenceGamesBack: String? = null,
    @SerialName("divisionGamesBack") private val _divisionGamesBack: String? = null,
    @SerialName("divisionLeader") private val _divisionLeader: Boolean? = null,
    @SerialName("gamesPlayed") private val _gamesPlayed: Int? = null,
    @SerialName("leagueGamesBack") private val _leagueGamesBack: String? = null,
    val leagueRecord: MLBGameTeamLeagueRecord,
    @SerialName("losses") private val _losses: Int? = null,
//    @SerialName("records") private val _records: Map<String, String>? = null,
    @SerialName("sportGamesBack") private val _sportGamesBack: String? = null,
    @SerialName("springLeagueGamesBack") private val _springLeagueGamesBack: String? = null,
    @SerialName("wildCardGamesBack") private val _wildCardGamesBack: String? = null,
    @SerialName("winningPercentage") private val _winningPercentage: String? = null,
    @SerialName("wins") private val _wins: Int? = null,
) {
    val conferenceGamesBack: String get() = _conferenceGamesBack ?: "-"
    val divisionGamesBack: String get() = _divisionGamesBack ?: "-"
    val divisionLeader: Boolean get() = _divisionLeader ?: false
    val gamesPlayed: Int get() = _gamesPlayed ?: 0
    val leagueGamesBack: String get() = _leagueGamesBack ?: "-"
    val losses: Int get() = _losses ?: 0
//    val records: Map<String, String> get() = _records ?: emptyMap()
    val sportGamesBack: String get() = _sportGamesBack ?: "-"
    val springLeagueGamesBack: String get() = _springLeagueGamesBack ?: "-"
    val wildCardGamesBack: String get() = _wildCardGamesBack ?: "-"
    val winningPercentage: String get() = _winningPercentage ?: ""
    val wins: Int get() = _wins ?: 0
}

@Serializable
data class MLBGameTeamLeagueRecord(
    @SerialName("losses") private val _losses: Int? = null,
    @SerialName("pct") private val _pct: String? = null,
    @SerialName("ties") private val _ties: Int? = null,
    @SerialName("wins") private val _wins: Int? = null,
) {
    val losses: Int get() = _losses ?: 0
    val pct: String get() = _pct ?: ""
    val ties: Int get() = _ties ?: 0
    val wins: Int get() = _wins ?: 0
}

@Serializable
data class MLBGameWeather(
    @SerialName("condition") private val _condition: String? = null,
    @SerialName("temp") private val _temp: String? = null,
    @SerialName("wind") private val _wind: String? = null,
) {
    val condition: String get() = _condition ?: ""
    val temp: String get() = _temp ?: ""
    val wind: String get() = _wind ?: ""
}

@Serializable
data class MLBGameInfoForSchedule(
    @SerialName("currentInning") private val _currentInning: String? = null
) {
    val currentInning: String get() = _currentInning ?: ""
}

typealias MLBGameForSchedule = GameForSchedule<MLBGameInfoForSchedule>










