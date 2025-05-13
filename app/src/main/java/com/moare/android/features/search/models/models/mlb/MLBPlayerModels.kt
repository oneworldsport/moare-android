package com.moare.android.features.search.models.models.mlb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MLBPlayer(
    val player: MLBPlayerInfo,
    val statistics: List<MLBPlayerStats>
)

@Serializable
data class MLBPlayerInfo(
    @SerialName("active") private val _active: Boolean? = null,
    val batSide: MLBCodeObj,
    @SerialName("birthCity") private val _birthCity: String? = null,
    @SerialName("birthCountry") private val _birthCountry: String? = null,
    @SerialName("birthDate") private val _birthDate: String? = null,
    @SerialName("birthStateProvince") private val _birthStateProvince: String? = null,
    @SerialName("boxscoreName") private val _boxscoreName: String? = null,
    @SerialName("currentAge") private val _currentAge: Int? = null,
    @SerialName("draftYear") private val _draftYear: Int? = null,
    @SerialName("firstLastName") private val _firstLastName: String? = null,
    @SerialName("firstName") private val _firstName: String? = null,
    @SerialName("fullFMLName") private val _fullFMLName: String? = null,
    @SerialName("fullLFMName") private val _fullLFMName: String? = null,
    @SerialName("fullName") private val _fullName: String? = null,
    @SerialName("height") private val _height: String? = null,
    @SerialName("id") private val _id: Int? = null,
    @SerialName("initLastName") private val _initLastName: String? = null,
    @SerialName("isPlayer") private val _isPlayer: Boolean? = null,
    @SerialName("isVerified") private val _isVerified: Boolean? = null,
    @SerialName("lastFirstName") private val _lastFirstName: String? = null,
    @SerialName("lastInitName") private val _lastInitName: String? = null,
    @SerialName("lastName") private val _lastName: String? = null,
    @SerialName("link") private val _link: String? = null,
    @SerialName("middleName") private val _middleName: String? = null,
    @SerialName("mlbDebutDate") private val _mlbDebutDate: String? = null,
    @SerialName("nameFirstLast") private val _nameFirstLast: String? = null,
    val pitchHand: MLBCodeObj,
    @SerialName("primaryNumber") private val _primaryNumber: String? = null,
    val primaryPosition: MLBAbbreviationCodeObj,
    @SerialName("strikeZoneBottom") private val _strikeZoneBottom: Double? = null,
    @SerialName("strikeZoneTop") private val _strikeZoneTop: Double? = null,
    @SerialName("useLastName") private val _useLastName: String? = null,
    @SerialName("useName") private val _useName: String? = null,
    @SerialName("weight") private val _weight: Int? = null,
) {
    val active: Boolean get() = _active ?: false
    val birthCity: String get() = _birthCity ?: ""
    val birthCountry: String get() = _birthCountry ?: ""
    val birthDate: String get() = _birthDate ?: ""
    val birthStateProvince: String get() = _birthStateProvince ?: ""
    val boxscoreName: String get() = _boxscoreName ?: ""
    val currentAge: Int get() = _currentAge ?: 0
    val draftYear: Int get() = _draftYear ?: 0
    val firstLastName: String get() = _firstLastName ?: ""
    val firstName: String get() = _firstName ?: ""
    val fullFMLName: String get() = _fullFMLName ?: ""
    val fullLFMName: String get() = _fullLFMName ?: ""
    val fullName: String get() = _fullName ?: ""
    val height: String get() = _height ?: ""
    val id: Int get() = _id ?: 0
    val initLastName: String get() = _initLastName ?: ""
    val isPlayer: Boolean get() = _isPlayer ?: false
    val isVerified: Boolean get() = _isVerified ?: false
    val lastFirstName: String get() = _lastFirstName ?: ""
    val lastInitName: String get() = _lastInitName ?: ""
    val lastName: String get() = _lastName ?: ""
    val link: String get() = _link ?: ""
    val middleName: String get() = _middleName ?: ""
    val mlbDebutDate: String get() = _mlbDebutDate ?: ""
    val nameFirstLast: String get() = _nameFirstLast ?: ""
    val primaryNumber: String get() = _primaryNumber ?: ""
    val strikeZoneBottom: Double get() = _strikeZoneBottom ?: 0.0
    val strikeZoneTop: Double get() = _strikeZoneTop ?: 0.0
    val useLastName: String get() = _useLastName ?: ""
    val useName: String get() = _useName ?: ""
    val weight: Int get() = _weight ?: 0
}

@Serializable
data class MLBPlayerStats(
    @SerialName("type") private val _type: String? = null,
    val fielding: MLBPlayerFieldingData? = null,
    val hitting: MLBPlayerHittingData? = null,
    val pitching: MLBPlayerPitchingData? = null,
    val catching: MLBPlayerCatchingData? = null
) {
    val type: String get() = _type ?: ""
}

@Serializable
data class MLBPlayerFieldingData(
    @SerialName("gameType") private val _gameType: String? = null,
    val league: MLBNameObj,
    val position: MLBPlayerPosition,
    @SerialName("season") private val _season: String? = null,
    val sport: MLBAbbreviationIdObj,
    val stat: MLBPlayerFieldingStats,
    val team: MLBNameObj,
) {
    val gameType: String get() = _gameType ?: ""
    val season: String get() = _season ?: ""
}

@Serializable
data class MLBPlayerPosition(
    @SerialName("abbreviation") private val _abbreviation: String? = null,
    @SerialName("code") private val _code: String? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("type") private val _type: String? = null,
) {
    val abbreviation: String get() = _abbreviation ?: ""
    val code: String get() = _code ?: ""
    val name: String get() = _name ?: ""
    val type: String get() = _type ?: ""
}

@Serializable
data class MLBPlayerFieldingStats(
    @SerialName("assists") private val _assists: Int? = null,
    @SerialName("chances") private val _chances: Int? = null,
    @SerialName("doublePlays") private val _doublePlays: Int? = null,
    @SerialName("errors") private val _errors: Int? = null,
    @SerialName("fielding") private val _fielding: String? = null,
    @SerialName("games") private val _games: Int? = null,
    @SerialName("gamesPlayed") private val _gamesPlayed: Int? = null,
    @SerialName("gamesStarted") private val _gamesStarted: Int? = null,
    @SerialName("innings") private val _innings: String? = null,
    val position: MLBPlayerPosition,
    @SerialName("putOuts") private val _putOuts: Int? = null,
    @SerialName("rangeFactorPer9Inn") private val _rangeFactorPer9Inn: String? = null,
    @SerialName("rangeFactorPerGame") private val _rangeFactorPerGame: String? = null,
    @SerialName("throwingErrors") private val _throwingErrors: Int? = null,
    @SerialName("triplePlays") private val _triplePlays: Int? = null,
) {
    val assists: Int get() = _assists ?: 0
    val chances: Int get() = _chances ?: 0
    val doublePlays: Int get() = _doublePlays ?: 0
    val errors: Int get() = _errors ?: 0
    val fielding: String get() = _fielding ?: ""
    val games: Int get() = _games ?: 0
    val gamesPlayed: Int get() = _gamesPlayed ?: 0
    val gamesStarted: Int get() = _gamesStarted ?: 0
    val innings: String get() = _innings ?: ""
    val putOuts: Int get() = _putOuts ?: 0
    val rangeFactorPer9Inn: String get() = _rangeFactorPer9Inn ?: ""
    val rangeFactorPerGame: String get() = _rangeFactorPerGame ?: ""
    val throwingErrors: Int get() = _throwingErrors ?: 0
    val triplePlays: Int get() = _triplePlays ?: 0
}

@Serializable
data class MLBPlayerHittingData(
    @SerialName("gameType") private val _gameType: String? = null,
    val league: MLBNameObj,
    @SerialName("season") private val _season: String? = null,
    val sport: MLBAbbreviationIdObj,
    val stat: MLBPlayerHittingStats,
    val team: MLBNameObj
) {
    val gameType: String get() = _gameType ?: ""
    val season: String get() = _season ?: ""
}

@Serializable
data class MLBPlayerHittingStats(
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
data class MLBPlayerPitchingData(
    @SerialName("gameType") private val _gameType: String? = null,
    val league: MLBNameObj,
    @SerialName("season") private val _season: String? = null,
    val sport: MLBAbbreviationIdObj,
    val stat: MLBPlayerPitchingStats,
    val team: MLBNameObj
) {
    val gameType: String get() = _gameType ?: ""
    val season: String get() = _season ?: ""
}

@Serializable
data class MLBPlayerPitchingStats(
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
    @SerialName("inheritedRunners") private val _inheritedRunners: Int? = null,
    @SerialName("inheritedRunnersScored") private val _inheritedRunnersScored: Int? = null,
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
    val inheritedRunners: Int get() = _inheritedRunners ?: 0
    val inheritedRunnersScored: Int get() = _inheritedRunnersScored ?: 0
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
data class MLBPlayerCatchingData(
    @SerialName("gameType") private val _gameType: String? = null,
    val league: MLBNameObj,
    @SerialName("season") private val _season: String? = null,
    val sport: MLBAbbreviationIdObj,
    val stat: MLBPlayerCatchingStats,
    val team: MLBNameObj
) {
    val gameType: String get() = _gameType ?: ""
    val season: String get() = _season ?: ""
}

@Serializable
data class MLBPlayerCatchingStats(
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












