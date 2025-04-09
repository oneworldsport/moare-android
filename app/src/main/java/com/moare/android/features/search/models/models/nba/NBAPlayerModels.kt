package com.moare.android.features.search.models.models.nba

import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.rounded
import com.moare.android.features.search.models.models.football.FBPlayerBirth
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NBAPlayer(
    val player: NBAPlayerInfo,
    val statistics: List<NBAPlayerStats> = emptyList()
)

@Serializable
data class NBAPlayerInfo(
    @SerialName("birthdate") private val _birthdate: String? = null,
    @SerialName("country") private val _country: String? = null,
    @SerialName("displayFirstLast") private val _displayFirstLast: String? = null,
    @SerialName("dleagueFlag") private val _dleagueFlag: String? = null,
    @SerialName("draftNumber") private val _draftNumber: String? = null,
    @SerialName("draftRound") private val _draftRound: String? = null,
    @SerialName("draftYear") private val _draftYear: String? = null,
    @SerialName("firstName") private val _firstName: String? = null,
    @SerialName("fromYear") private val _fromYear: Int? = null,
    @SerialName("gamesPlayedCurrentSeasonFlag") private val _gamesPlayedCurrentSeasonFlag: String? = null,
    @SerialName("gamesPlayedFlag") private val _gamesPlayedFlag: String? = null,
    @SerialName("height") private val _height: String? = null,
    @SerialName("jersey") private val _jersey: String? = null,
    @SerialName("lastAffiliation") private val _lastAffiliation: String? = null,
    @SerialName("lastName") private val _lastName: String? = null,
    @SerialName("nbaFlag") private val _nbaFlag: String? = null,
    @SerialName("personId") private val _personId: Int? = null,
    @SerialName("position") private val _position: String? = null,
    @SerialName("rosterstatus") private val _rosterStatus: String? = null,
    @SerialName("school") private val _school: String? = null,
    @SerialName("seasonExp") private val _seasonExp: Int? = null,
    @SerialName("teamAbbreviation") private val _teamAbbreviation: String? = null,
    @SerialName("teamCity") private val _teamCity: String? = null,
    @SerialName("teamCode") private val _teamCode: String? = null,
    @SerialName("teamId") private val _teamId: Int? = null,
    @SerialName("teamName") private val _teamName: String? = null,
    @SerialName("toYear") private val _toYear: Int? = null,
    @SerialName("weight") private val _weight: String? = null,
) {
    val birthdate: String get() = _birthdate ?: ""
    val country: String get() = _country ?: ""
    val displayFirstLast: String get() = _displayFirstLast ?: "" // full name
    val dleagueFlag: String get() = _dleagueFlag ?: "" // G리그에서 뛴 경험 여부
    val draftNumber: String get() = _draftNumber ?: ""
    val draftRound: String get() = _draftRound ?: ""
    val draftYear: String get() = _draftYear ?: ""
    val firstName: String get() = _firstName ?: ""
    val fromYear: Int get() = _fromYear ?: 0
    val gamesPlayedCurrentSeasonFlag: String get() = _gamesPlayedCurrentSeasonFlag ?: ""
    val gamesPlayedFlag: String get() = _gamesPlayedFlag ?: "" // NBA 정규 시즌에서 경기를 뛴 적이 있는지 여부
    val height: String get() = _height ?: ""
    val jersey: String get() = _jersey ?: ""
    val lastAffiliation: String get() = _lastAffiliation ?: "" // 출신 학교 및 국가 정보
    val lastName: String get() = _lastName ?: ""
    val nbaFlag: String get() = _nbaFlag ?: "" // NBA에서 뛰었는지 여부
    val personId: Int get() = _personId ?: 0
    val position: String get() = _position ?: ""
    val rosterStatus: String get() = _rosterStatus ?: ""
    val school: String get() = _school ?: ""
    val seasonExp: Int get() = _seasonExp ?: 0
    val teamAbbreviation: String get() = _teamAbbreviation ?: ""
    val teamCity: String get() = _teamCity ?: ""
    val teamCode: String get() = _teamCode ?: ""
    val teamId: Int get() = _teamId ?: 0
    val teamName: String get() = _teamName ?: ""
    val toYear: Int get() = _toYear ?: 0
    val weight: String get() = _weight ?: ""
}

@Serializable
data class NBAPlayerStats(
    @SerialName("ast") private val _ast: Int? = null,
    @SerialName("blk") private val _blk: Int? = null,
    @SerialName("blka") private val _blka: Int? = null,
    @SerialName("dd2") private val _dd2: Int? = null,
    @SerialName("dreb") private val _dreb: Int? = null,
    @SerialName("fg3a") private val _fg3a: Int? = null,
    @SerialName("fg3m") private val _fg3m: Int? = null,
    @SerialName("fg3Pct") private val _fg3Pct: Double? = null,
    @SerialName("fga") private val _fga: Int? = null,
    @SerialName("fgm") private val _fgm: Int? = null,
    @SerialName("fgPct") private val _fgPct: Double? = null,
    @SerialName("fta") private val _fta: Int? = null,
    @SerialName("ftm") private val _ftm: Int? = null,
    @SerialName("ftPct") private val _ftPct: Double? = null,
    @SerialName("gp") private val _gp: Int? = null,
    @SerialName("groupValue") private val _groupValue: String? = null,
    @SerialName("l") private val _l: Int? = null,
    @SerialName("maxGameDate") private val _maxGameDate: String? = null,
    @SerialName("min") private val _min: Double? = null,
    @SerialName("oreb") private val _oreb: Int? = null,
    @SerialName("pf") private val _pf: Int? = null,
    @SerialName("pfd") private val _pfd: Int? = null,
    @SerialName("plusMinus") private val _plusMinus: Int? = null,
    @SerialName("pts") private val _pts: Int? = null,
    @SerialName("reb") private val _reb: Int? = null,
    @SerialName("seasonType") private val _seasonType: String? = null,
    @SerialName("stl") private val _stl: Int? = null,
    @SerialName("td3") private val _td3: Int? = null,
    @SerialName("teamAbbreviation") private val _teamAbbreviation: String? = null,
    @SerialName("teamId") private val _teamId: Int? = null,
    @SerialName("tov") private val _tov: Int? = null,
    @SerialName("w") private val _w: Int? = null,
    @SerialName("wPct") private val _wPct: Double? = null,
    @SerialName("teamGp") private val _teamGp: Int? = null
) {
    val ast: Int get() = _ast ?: 0
    val blk: Int get() = _blk ?: 0
    val blka: Int get() = _blka ?: 0 // 블록 당한 횟수
    val dd2: Int get() = _dd2 ?: 0 // 더블더블 횟수
    val dreb: Int get() = _dreb ?: 0
    val fg3a: Int get() = _fg3a ?: 0 // 3점 슛 시도 횟수
    val fg3m: Int get() = _fg3m ?: 0 // 3점 슛 성공 횟수
    val fg3Pct: Double get() = _fg3Pct ?: 0.0 // 3점 슛 성공률
    val fga: Int get() = _fga ?: 0 // 야투 시도 횟수
    val fgm: Int get() = _fgm ?: 0 // 야투 성공 횟수
    val fgPct: Double get() = _fgPct ?: 0.0 // 야투 성공률
    val fta: Int get() = _fta ?: 0 // 자유투 시도 횟수
    val ftm: Int get() = _ftm ?: 0 // 자유투 성공 횟수
    val ftPct: Double get() = _ftPct ?: 0.0 // 자유투 성공률
    val gp: Int get() = _gp ?: 0
    val groupValue: String get() = _groupValue ?: ""
    val losses: Int get() = _l ?: 0
    val maxGameDate: String get() = _maxGameDate ?: "" // 가장 최근 경기
    val min: Int get() = (_min ?: 0.0).rounded(0).toInt() // 분단위
    val oreb: Int get() = _oreb ?: 0
    val pf: Int get() = _pf ?: 0 // 파울 횟수
    val pfd: Int get() = _pfd ?: 0 // 파울 유도 횟수
    val plusMinus: Int get() = _plusMinus ?: 0
    val pts: Int get() = _pts ?: 0
    val reb: Int get() = _reb ?: 0
    val seasonType: String get() = _seasonType ?: ""
    val stl: Int get() = _stl ?: 0
    val td3: Int get() = _td3 ?: 0 // 트리플더블 횟수
    val teamAbbreviation: String get() = _teamAbbreviation ?: ""
    val teamId: Int get() = _teamId ?: 0
    val tov: Int get() = _tov ?: 0
    val wins: Int get() = _w ?: 0
    val winsPct: Double get() = _wPct ?: 0.0
    val teamGp: Int get() = _teamGp ?: 0

    val ptsPG: Double get() = if (gp != 0) (pts.toDouble() / gp).rounded(1) else 0.0
    val astPG: Double get() = if (gp != 0) (ast.toDouble() /gp).rounded(1) else 0.0
    val rebPG: Double get() = if (gp != 0) (reb.toDouble() /gp).rounded(1) else 0.0
    val drebPG: Double get() = if (gp != 0) (dreb.toDouble() /gp).rounded(1) else 0.0
    val orebPG: Double get() = if (gp != 0) (oreb.toDouble() /gp).rounded(1) else 0.0
    val blkPG: Double get() = if (gp != 0) (blk.toDouble() /gp).rounded(1) else 0.0
    val blkaPG: Double get() = if (gp != 0) (blka.toDouble() /gp).rounded(1) else 0.0
    val stlPG: Double get() = if (gp != 0) (stl.toDouble() /gp).rounded(1) else 0.0
    val tovPG: Double get() = if (gp != 0) (tov.toDouble() /gp).rounded(1) else 0.0
    val fg3aPG: Double get() = if (gp != 0) (fg3a.toDouble() /gp).rounded(1) else 0.0
    val fg3mPG: Double get() = if (gp != 0) (fg3m.toDouble() /gp).rounded(1) else 0.0
    val fgaPG: Double get() = if (gp != 0) (fga.toDouble() /gp).rounded(1) else 0.0
    val fgmPG: Double get() = if (gp != 0) (fgm.toDouble() /gp).rounded(1) else 0.0
    val ftaPG: Double get() = if (gp != 0) (fta.toDouble() /gp).rounded(1) else 0.0
    val ftmPG: Double get() = if (gp != 0) (ftm.toDouble() /gp).rounded(1) else 0.0
    val pfPG: Double get() = if (gp != 0) (pf.toDouble() /gp).rounded(1) else 0.0
    val pfdPG: Double get() = if (gp != 0) (pfd.toDouble() /gp).rounded(1) else 0.0
    val minPG: String get() = if (gp != 0) CalendarUtil.formatMinutesToHourMinute(min) else "0:0"
    val plusMinusPG: Double get() = if (gp != 0) (plusMinus.toDouble() /gp).rounded(1) else 0.0
}