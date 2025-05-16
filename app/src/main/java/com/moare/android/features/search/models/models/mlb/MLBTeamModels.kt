package com.moare.android.features.search.models.models.mlb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MLBTeam(
    val team: MLBTeamInfo,
    val venue: MLBNameObj,
    val statistics: List<MLBTeamStats>
)

@Serializable
data class MLBTeamInfo(
    @SerialName("abbreviation") private val _abbreviation: String? = null,
    @SerialName("active") private val _active: Boolean? = null,
    @SerialName("allStarStatus") private val _allStarStatus: String? = null,
    @SerialName("clubName") private val _clubName: String? = null,
    val division: MLBNameObj,
    @SerialName("fileCode") private val _fileCode: String? = null,
    @SerialName("firstYearOfPlay") private val _firstYearOfPlay: String? = null,
    @SerialName("franchiseName") private val _franchiseName: String? = null,
    @SerialName("id") private val _id: Int? = null,
    val league: MLBNameObj,
    @SerialName("link") private val _link: String? = null,
    @SerialName("locationName") private val _locationName: String? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("season") private val _season: Int? = null,
    @SerialName("shortName") private val _shortName: String? = null,
    @SerialName("teamCode") private val _teamCode: String? = null,
    @SerialName("teamName") private val _teamName: String? = null,
) {
    val abbreviation: String get() = _abbreviation ?: ""
    val active: Boolean get() = _active ?: false
    val allStarStatus: String get() = _allStarStatus ?: ""
    val clubName: String get() = _clubName ?: ""
    val fileCode: String get() = _fileCode ?: ""
    val firstYearOfPlay: String get() = _firstYearOfPlay ?: ""
    val franchiseName: String get() = _franchiseName ?: ""
    val id: Int get() = _id ?: 0
    val link: String get() = _link ?: ""
    val locationName: String get() = _locationName ?: ""
    val name: String get() = _name ?: ""
    val season: Int get() = _season ?: 0
    val shortName: String get() = _shortName ?: ""
    val teamCode: String get() = _teamCode ?: ""
    val teamName: String get() = _teamName ?: ""
}

@Serializable
data class MLBTeamStats(
    val catching: MLBTeamCatchingStats?,
    val fielding: MLBTeamFieldingStats?,
    val hitting: MLBTeamHittingStats?,
    val pitching: MLBTeamPitchingStats?,
    val recordData: MLBTeamRecordData?
)

@Serializable
data class MLBTeamCatchingStats(
    @SerialName("atBats") private val _atBats: Int? = null,
    @SerialName("avg") private val _avg: String? = null,
    @SerialName("baseOnBalls") private val _baseOnBalls: Int? = null,
    @SerialName("battersFaced") private val _battersFaced: Int? = null,
    @SerialName("catchersInterference") private val _catchersInterference: Int? = null,
    @SerialName("caughtStealing") private val _caughtStealing: Int? = null,
    @SerialName("earnedRuns") private val _earnedRuns: Int? = null,
    @SerialName("gamesPitched") private val _gamesPitched: Int? = null,
    @SerialName("gamesPlayed") private val _gamesPlayed: Int? = null,
    @SerialName("hitBatsmen") private val _hitBatsmen: Int? = null,
    @SerialName("hitByPitch") private val _hitByPitch: Int? = null,
    @SerialName("hits") private val _hits: Int? = null,
    @SerialName("homeRuns") private val _homeRuns: Int? = null,
    @SerialName("intentionalWalks") private val _intentionalWalks: Int? = null,
    @SerialName("obp") private val _obp: String? = null,
    @SerialName("ops") private val _ops: String? = null,
    @SerialName("passedBall") private val _passedBall: Int? = null,
    @SerialName("pickoffAttempts") private val _pickoffAttempts: Int? = null,
    @SerialName("pickoffs") private val _pickoffs: Int? = null,
    @SerialName("runs") private val _runs: Int? = null,
    @SerialName("sacBunts") private val _sacBunts: Int? = null,
    @SerialName("sacFlies") private val _sacFlies: Int? = null,
    @SerialName("slg") private val _slg: String? = null,
    @SerialName("stolenBasePercentage") private val _stolenBasePercentage: String? = null,
    @SerialName("stolenBases") private val _stolenBases: Int? = null,
    @SerialName("strikeOuts") private val _strikeOuts: Int? = null,
    @SerialName("strikeoutWalkRatio") private val _strikeoutWalkRatio: String? = null,
    @SerialName("totalBases") private val _totalBases: Int? = null,
    @SerialName("wildPitches") private val _wildPitches: Int? = null,
) {
    val atBats: Int get() = _atBats ?: 0
    val avg: String get() = _avg ?: ""
    val baseOnBalls: Int get() = _baseOnBalls ?: 0
    val battersFaced: Int get() = _battersFaced ?: 0
    val catchersInterference: Int get() = _catchersInterference ?: 0
    val caughtStealing: Int get() = _caughtStealing ?: 0
    val earnedRuns: Int get() = _earnedRuns ?: 0
    val gamesPitched: Int get() = _gamesPitched ?: 0
    val gamesPlayed: Int get() = _gamesPlayed ?: 0
    val hitBatsmen: Int get() = _hitBatsmen ?: 0
    val hitByPitch: Int get() = _hitByPitch ?: 0
    val hits: Int get() = _hits ?: 0
    val homeRuns: Int get() = _homeRuns ?: 0
    val intentionalWalks: Int get() = _intentionalWalks ?: 0
    val obp: String get() = _obp ?: ""
    val ops: String get() = _ops ?: ""
    val passedBall: Int get() = _passedBall ?: 0
    val pickoffAttempts: Int get() = _pickoffAttempts ?: 0
    val pickoffs: Int get() = _pickoffs ?: 0
    val runs: Int get() = _runs ?: 0
    val sacBunts: Int get() = _sacBunts ?: 0
    val sacFlies: Int get() = _sacFlies ?: 0
    val slg: String get() = _slg ?: ""
    val stolenBasePercentage: String get() = _stolenBasePercentage ?: ""
    val stolenBases: Int get() = _stolenBases ?: 0
    val strikeOuts: Int get() = _strikeOuts ?: 0
    val strikeoutWalkRatio: String get() = _strikeoutWalkRatio ?: ""
    val totalBases: Int get() = _totalBases ?: 0
    val wildPitches: Int get() = _wildPitches ?: 0
}

@Serializable
data class MLBTeamFieldingStats(
    @SerialName("assists") private val _assists: Int? = null,
    @SerialName("catchersInterference") private val _catchersInterference: Int? = null,
    @SerialName("caughtStealing") private val _caughtStealing: Int? = null,
    @SerialName("chances") private val _chances: Int? = null,
    @SerialName("doublePlays") private val _doublePlays: Int? = null,
    @SerialName("errors") private val _errors: Int? = null,
    @SerialName("fielding") private val _fielding: String? = null,
    @SerialName("games") private val _games: Int? = null,
    @SerialName("gamesPlayed") private val _gamesPlayed: Int? = null,
    @SerialName("gamesStarted") private val _gamesStarted: Int? = null,
    @SerialName("innings") private val _innings: String? = null,
    @SerialName("passedBall") private val _passedBall: Int? = null,
    @SerialName("pickoffs") private val _pickoffs: Int? = null,
    @SerialName("putOuts") private val _putOuts: Int? = null,
    @SerialName("rangeFactorPer9Inn") private val _rangeFactorPer9Inn: String? = null,
    @SerialName("rangeFactorPerGame") private val _rangeFactorPerGame: String? = null,
    @SerialName("stolenBasePercentage") private val _stolenBasePercentage: String? = null,
    @SerialName("stolenBases") private val _stolenBases: Int? = null,
    @SerialName("throwingErrors") private val _throwingErrors: Int? = null,
    @SerialName("triplePlays") private val _triplePlays: Int? = null,
    @SerialName("wildPitches") private val _wildPitches: Int? = null,
) {
    val assists: Int get() = _assists ?: 0
    val catchersInterference: Int get() = _catchersInterference ?: 0
    val caughtStealing: Int get() = _caughtStealing ?: 0
    val chances: Int get() = _chances ?: 0
    val doublePlays: Int get() = _doublePlays ?: 0
    val errors: Int get() = _errors ?: 0
    val fielding: String get() = _fielding ?: ""
    val games: Int get() = _games ?: 0
    val gamesPlayed: Int get() = _gamesPlayed ?: 0
    val gamesStarted: Int get() = _gamesStarted ?: 0
    val innings: String get() = _innings ?: ""
    val passedBall: Int get() = _passedBall ?: 0
    val pickoffs: Int get() = _pickoffs ?: 0
    val putOuts: Int get() = _putOuts ?: 0
    val rangeFactorPer9Inn: String get() = _rangeFactorPer9Inn ?: ""
    val rangeFactorPerGame: String get() = _rangeFactorPerGame ?: ""
    val stolenBasePercentage: String get() = _stolenBasePercentage ?: ""
    val stolenBases: Int get() = _stolenBases ?: 0
    val throwingErrors: Int get() = _throwingErrors ?: 0
    val triplePlays: Int get() = _triplePlays ?: 0
    val wildPitches: Int get() = _wildPitches ?: 0
}

@Serializable
data class MLBTeamHittingStats(
    @SerialName("airOuts") private val _airOuts: Int? = null,
    @SerialName("atBats") private val _atBats: Int? = null,
    @SerialName("atBatsPerHomeRun") private val _atBatsPerHomeRun: String? = null,
    @SerialName("avg") private val _avg: String? = null,
    @SerialName("babip") private val _babip: String? = null,
    @SerialName("baseOnBalls") private val _baseOnBalls: Int? = null,
    @SerialName("catchersInterference") private val _catchersInterference: Int? = null,
    @SerialName("caughtStealing") private val _caughtStealing: Int? = null,
    @SerialName("doubles") private val _doubles: Int? = null,
    @SerialName("gamesPlayed") private val _gamesPlayed: Int? = null,
    @SerialName("groundIntoDoublePlay") private val _groundIntoDoublePlay: Int? = null,
    @SerialName("groundOuts") private val _groundOuts: Int? = null,
    @SerialName("groundOutsToAirouts") private val _groundOutsToAirouts: String? = null,
    @SerialName("hitByPitch") private val _hitByPitch: Int? = null,
    @SerialName("hits") private val _hits: Int? = null,
    @SerialName("homeRuns") private val _homeRuns: Int? = null,
    @SerialName("intentionalWalks") private val _intentionalWalks: Int? = null,
    @SerialName("leftOnBase") private val _leftOnBase: Int? = null,
    @SerialName("numberOfPitches") private val _numberOfPitches: Int? = null,
    @SerialName("obp") private val _obp: String? = null,
    @SerialName("ops") private val _ops: String? = null,
    @SerialName("plateAppearances") private val _plateAppearances: Int? = null,
    @SerialName("rbi") private val _rbi: Int? = null,
    @SerialName("runs") private val _runs: Int? = null,
    @SerialName("sacBunts") private val _sacBunts: Int? = null,
    @SerialName("sacFlies") private val _sacFlies: Int? = null,
    @SerialName("slg") private val _slg: String? = null,
    @SerialName("stolenBasePercentage") private val _stolenBasePercentage: String? = null,
    @SerialName("stolenBases") private val _stolenBases: Int? = null,
    @SerialName("strikeOuts") private val _strikeOuts: Int? = null,
    @SerialName("totalBases") private val _totalBases: Int? = null,
    @SerialName("triples") private val _triples: Int? = null,
) {
    val airOuts: Int get() = _airOuts ?: 0
    val atBats: Int get() = _atBats ?: 0
    val atBatsPerHomeRun: String get() = _atBatsPerHomeRun ?: ""
    val avg: String get() = _avg ?: ""
    val babip: String get() = _babip ?: ""
    val baseOnBalls: Int get() = _baseOnBalls ?: 0
    val catchersInterference: Int get() = _catchersInterference ?: 0
    val caughtStealing: Int get() = _caughtStealing ?: 0
    val doubles: Int get() = _doubles ?: 0
    val gamesPlayed: Int get() = _gamesPlayed ?: 0
    val groundIntoDoublePlay: Int get() = _groundIntoDoublePlay ?: 0
    val groundOuts: Int get() = _groundOuts ?: 0
    val groundOutsToAirouts: String get() = _groundOutsToAirouts ?: ""
    val hitByPitch: Int get() = _hitByPitch ?: 0
    val hits: Int get() = _hits ?: 0
    val homeRuns: Int get() = _homeRuns ?: 0
    val intentionalWalks: Int get() = _intentionalWalks ?: 0
    val leftOnBase: Int get() = _leftOnBase ?: 0
    val numberOfPitches: Int get() = _numberOfPitches ?: 0
    val obp: String get() = _obp ?: ""
    val ops: String get() = _ops ?: ""
    val plateAppearances: Int get() = _plateAppearances ?: 0
    val rbi: Int get() = _rbi ?: 0
    val runs: Int get() = _runs ?: 0
    val sacBunts: Int get() = _sacBunts ?: 0
    val sacFlies: Int get() = _sacFlies ?: 0
    val slg: String get() = _slg ?: ""
    val stolenBasePercentage: String get() = _stolenBasePercentage ?: ""
    val stolenBases: Int get() = _stolenBases ?: 0
    val strikeOuts: Int get() = _strikeOuts ?: 0
    val totalBases: Int get() = _totalBases ?: 0
    val triples: Int get() = _triples ?: 0
}

@Serializable
data class MLBTeamPitchingStats(
    @SerialName("airOuts") private val _airOuts: Int? = null,
    @SerialName("atBats") private val _atBats: Int? = null,
    @SerialName("avg") private val _avg: String? = null,
    @SerialName("balks") private val _balks: Int? = null,
    @SerialName("baseOnBalls") private val _baseOnBalls: Int? = null,
    @SerialName("battersFaced") private val _battersFaced: Int? = null,
    @SerialName("blownSaves") private val _blownSaves: Int? = null,
    @SerialName("catchersInterference") private val _catchersInterference: Int? = null,
    @SerialName("caughtStealing") private val _caughtStealing: Int? = null,
    @SerialName("completeGames") private val _completeGames: Int? = null,
    @SerialName("doubles") private val _doubles: Int? = null,
    @SerialName("earnedRuns") private val _earnedRuns: Int? = null,
    @SerialName("era") private val _era: String? = null,
    @SerialName("gamesFinished") private val _gamesFinished: Int? = null,
    @SerialName("gamesPitched") private val _gamesPitched: Int? = null,
    @SerialName("gamesPlayed") private val _gamesPlayed: Int? = null,
    @SerialName("gamesStarted") private val _gamesStarted: Int? = null,
    @SerialName("groundIntoDoublePlay") private val _groundIntoDoublePlay: Int? = null,
    @SerialName("groundOuts") private val _groundOuts: Int? = null,
    @SerialName("groundOutsToAirouts") private val _groundOutsToAirouts: String? = null,
    @SerialName("hitBatsmen") private val _hitBatsmen: Int? = null,
    @SerialName("hitByPitch") private val _hitByPitch: Int? = null,
    @SerialName("hits") private val _hits: Int? = null,
    @SerialName("hitsPer9Inn") private val _hitsPer9Inn: String? = null,
    @SerialName("holds") private val _holds: Int? = null,
    @SerialName("homeRuns") private val _homeRuns: Int? = null,
    @SerialName("homeRunsPer9") private val _homeRunsPer9: String? = null,
    @SerialName("inningsPitched") private val _inningsPitched: String? = null,
    @SerialName("intentionalWalks") private val _intentionalWalks: Int? = null,
    @SerialName("losses") private val _losses: Int? = null,
    @SerialName("numberOfPitches") private val _numberOfPitches: Int? = null,
    @SerialName("obp") private val _obp: String? = null,
    @SerialName("ops") private val _ops: String? = null,
    @SerialName("outs") private val _outs: Int? = null,
    @SerialName("pickoffs") private val _pickoffs: Int? = null,
    @SerialName("pitchesPerInning") private val _pitchesPerInning: String? = null,
    @SerialName("runs") private val _runs: Int? = null,
    @SerialName("runsScoredPer9") private val _runsScoredPer9: String? = null,
    @SerialName("sacBunts") private val _sacBunts: Int? = null,
    @SerialName("sacFlies") private val _sacFlies: Int? = null,
    @SerialName("saveOpportunities") private val _saveOpportunities: Int? = null,
    @SerialName("saves") private val _saves: Int? = null,
    @SerialName("shutouts") private val _shutouts: Int? = null,
    @SerialName("slg") private val _slg: String? = null,
    @SerialName("stolenBasePercentage") private val _stolenBasePercentage: String? = null,
    @SerialName("stolenBases") private val _stolenBases: Int? = null,
    @SerialName("strikeOuts") private val _strikeOuts: Int? = null,
    @SerialName("strikeoutsPer9Inn") private val _strikeoutsPer9Inn: String? = null,
    @SerialName("strikeoutWalkRatio") private val _strikeoutWalkRatio: String? = null,
    @SerialName("strikePercentage") private val _strikePercentage: String? = null,
    @SerialName("strikes") private val _strikes: Int? = null,
    @SerialName("totalBases") private val _totalBases: Int? = null,
    @SerialName("triples") private val _triples: Int? = null,
    @SerialName("walksPer9Inn") private val _walksPer9Inn: String? = null,
    @SerialName("whip") private val _whip: String? = null,
    @SerialName("wildPitches") private val _wildPitches: Int? = null,
    @SerialName("winPercentage") private val _winPercentage: String? = null,
    @SerialName("wins") private val _wins: Int? = null,
) {
    val airOuts: Int get() = _airOuts ?: 0
    val atBats: Int get() = _atBats ?: 0
    val avg: String get() = _avg ?: ""
    val balks: Int get() = _balks ?: 0
    val baseOnBalls: Int get() = _baseOnBalls ?: 0
    val battersFaced: Int get() = _battersFaced ?: 0
    val blownSaves: Int get() = _blownSaves ?: 0
    val catchersInterference: Int get() = _catchersInterference ?: 0
    val caughtStealing: Int get() = _caughtStealing ?: 0
    val completeGames: Int get() = _completeGames ?: 0
    val doubles: Int get() = _doubles ?: 0
    val earnedRuns: Int get() = _earnedRuns ?: 0
    val era: String get() = _era ?: ""
    val gamesFinished: Int get() = _gamesFinished ?: 0
    val gamesPitched: Int get() = _gamesPitched ?: 0
    val gamesPlayed: Int get() = _gamesPlayed ?: 0
    val gamesStarted: Int get() = _gamesStarted ?: 0
    val groundIntoDoublePlay: Int get() = _groundIntoDoublePlay ?: 0
    val groundOuts: Int get() = _groundOuts ?: 0
    val groundOutsToAirouts: String get() = _groundOutsToAirouts ?: ""
    val hitBatsmen: Int get() = _hitBatsmen ?: 0
    val hitByPitch: Int get() = _hitByPitch ?: 0
    val hits: Int get() = _hits ?: 0
    val hitsPer9Inn: String get() = _hitsPer9Inn ?: ""
    val holds: Int get() = _holds ?: 0
    val homeRuns: Int get() = _homeRuns ?: 0
    val homeRunsPer9: String get() = _homeRunsPer9 ?: ""
    val inningsPitched: String get() = _inningsPitched ?: ""
    val intentionalWalks: Int get() = _intentionalWalks ?: 0
    val losses: Int get() = _losses ?: 0
    val numberOfPitches: Int get() = _numberOfPitches ?: 0
    val obp: String get() = _obp ?: ""
    val ops: String get() = _ops ?: ""
    val outs: Int get() = _outs ?: 0
    val pickoffs: Int get() = _pickoffs ?: 0
    val pitchesPerInning: String get() = _pitchesPerInning ?: ""
    val runs: Int get() = _runs ?: 0
    val runsScoredPer9: String get() = _runsScoredPer9 ?: ""
    val sacBunts: Int get() = _sacBunts ?: 0
    val sacFlies: Int get() = _sacFlies ?: 0
    val saveOpportunities: Int get() = _saveOpportunities ?: 0
    val saves: Int get() = _saves ?: 0
    val shutouts: Int get() = _shutouts ?: 0
    val slg: String get() = _slg ?: ""
    val stolenBasePercentage: String get() = _stolenBasePercentage ?: ""
    val stolenBases: Int get() = _stolenBases ?: 0
    val strikeOuts: Int get() = _strikeOuts ?: 0
    val strikeoutsPer9Inn: String get() = _strikeoutsPer9Inn ?: ""
    val strikeoutWalkRatio: String get() = _strikeoutWalkRatio ?: ""
    val strikePercentage: String get() = _strikePercentage ?: ""
    val strikes: Int get() = _strikes ?: 0
    val totalBases: Int get() = _totalBases ?: 0
    val triples: Int get() = _triples ?: 0
    val walksPer9Inn: String get() = _walksPer9Inn ?: ""
    val whip: String get() = _whip ?: ""
    val wildPitches: Int get() = _wildPitches ?: 0
    val winPercentage: String get() = _winPercentage ?: ""
    val wins: Int get() = _wins ?: 0
}

@Serializable
data class MLBTeamRecordData(
    @SerialName("conferenceGamesBack") private val _conferenceGamesBack: String? = null,
    @SerialName("divisionGamesBack") private val _divisionGamesBack: String? = null,
    @SerialName("divisionRank") private val _divisionRank: String? = null,
    @SerialName("gamesBack") private val _gamesBack: String? = null,
    @SerialName("gamesPlayed") private val _gamesPlayed: Int? = null,
    @SerialName("lastUpdated") private val _lastUpdated: String? = null,
    @SerialName("leagueGamesBack") private val _leagueGamesBack: String? = null,
    @SerialName("leagueRank") private val _leagueRank: String? = null,
    val leagueRecord: MLBGameTeamLeagueRecord,
    @SerialName("losses") private val _losses: Int? = null,
    @SerialName("runDifferential") private val _runDifferential: Int? = null,
    @SerialName("runsAllowed") private val _runsAllowed: Int? = null,
    @SerialName("runsScored") private val _runsScored: Int? = null,
    @SerialName("season") private val _season: String? = null,
    @SerialName("sportGamesBack") private val _sportGamesBack: String? = null,
    @SerialName("sportRank") private val _sportRank: String? = null,
    val streak: MLBTeamRecordStreak,
    val team: MLBNameObj,
    @SerialName("wildCardGamesBack") private val _wildCardGamesBack: String? = null,
    @SerialName("wildCardRank") private val _wildCardRank: String? = null,
    @SerialName("winningPercentage") private val _winningPercentage: String? = null,
    @SerialName("wins") private val _wins: Int? = null,
) {
    val conferenceGamesBack: String get() = _conferenceGamesBack ?: "-"
    val divisionGamesBack: String get() = _divisionGamesBack ?: "-"
    val divisionRank: String get() = _divisionRank ?: ""
    val gamesBack: String get() = _gamesBack ?: "-"
    val gamesPlayed: Int get() = _gamesPlayed ?: 0
    val lastUpdated: String get() = _lastUpdated ?: ""
    val leagueGamesBack: String get() = _leagueGamesBack ?: "-"
    val leagueRank: String get() = _leagueRank ?: ""
    val losses: Int get() = _losses ?: 0
    val runDifferential: Int get() = _runDifferential ?: 0
    val runsAllowed: Int get() = _runsAllowed ?: 0
    val runsScored: Int get() = _runsScored ?: 0
    val season: String get() = _season ?: ""
    val sportGamesBack: String get() = _sportGamesBack ?: "-"
    val sportRank: String get() = _sportRank ?: ""
    val wildCardGamesBack: String get() = _wildCardGamesBack ?: "-"
    val wildCardRank: String get() = _wildCardRank ?: ""
    val winningPercentage: String get() = _winningPercentage ?: ""
    val wins: Int get() = _wins ?: 0
}

@Serializable
data class MLBTeamRecordStreak(
    @SerialName("streakCode") private val _streakCode: String? = null,
    @SerialName("streakNumber") private val _streakNumber: Int? = null,
    @SerialName("streakType") private val _streakType: String? = null,
) {
    val streakCode: String get() = _streakCode ?: ""
    val streakNumber: Int get() = _streakNumber ?: 0
    val streakType: String get() = _streakType ?: ""
}
