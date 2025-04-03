package com.moare.android.features.search.models.models.nba

import com.moare.android.core.util.rounded
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NBATeam(
    val team: NBATeamInfo,
    val venue: NBAVenue,
    val statistics: List<NBATeamStats> = emptyList(),
)

@Serializable
data class NBATeamInfo(
    @SerialName("abbreviation") private val _abbreviation: String? = null,
    @SerialName("city") private val _city: String? = null,
    @SerialName("confRank") private val _confRank: Int? = null,
    @SerialName("divRank") private val _divRank: Int? = null,
    @SerialName("fullName") private val _fullName: String? = null,
    @SerialName("id") private val _id: Int? = null,
    @SerialName("l") private val _l: Int? = null,
    @SerialName("maxYear") private val _maxYear: String? = null,
    @SerialName("minYear") private val _minYear: String? = null,
    @SerialName("nickname") private val _nickname: String? = null,
    @SerialName("pct") private val _pct: Double? = null,
    @SerialName("seasonYear") private val _seasonYear: String? = null,
    @SerialName("state") private val _state: String? = null,
    @SerialName("teamCode") private val _teamCode: String? = null,
    @SerialName("teamConference") private val _teamConference: String? = null,
    @SerialName("teamDivision") private val _teamDivision: String? = null,
    @SerialName("teamLogo") private val _teamLogo: String? = null,
    @SerialName("w") private val _w: Int? = null,
    @SerialName("yearFounded") private val _yearFounded: Int? = null,
) {
    val abbreviation: String get() = _abbreviation ?: ""
    val city: String get() = _city ?: ""
    val confRank: Int get() = _confRank ?: 0
    val divRank: Int get() = _divRank ?: 0
    val fullName: String get() = _fullName ?: ""
    val id: Int get() = _id ?: 0
    val losses: Int get() = _l ?: 0
    val maxYear: String get() = _maxYear ?: ""
    val minYear: String get() = _minYear ?: ""
    val nickname: String get() = _nickname ?: ""
    val pct: Double get() = _pct ?: 0.0
    val seasonYear: String get() = _seasonYear ?: ""
    val state: String get() = _state ?: ""
    val teamCode: String get() = _teamCode ?: ""
    val teamConference: String get() = _teamConference ?: ""
    val teamDivision: String get() = _teamDivision ?: ""
    val teamLogo: String get() = _teamLogo ?: ""
    val wins: Int get() = _w ?: 0
    val yearFounded: Int get() = _yearFounded ?: 0
}

@Serializable
data class NBAVenue(
    @SerialName("capacity") private val _capacity: Int? = null,
    @SerialName("krname") private val _krname: String? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("opened") private val _opened: Int? = null,
) {
    val capacity: Int get() = _capacity ?: 0
    val krname: String get() = _krname ?: ""
    val name: String get() = _name ?: ""
    val opened: Int get() = _opened ?: 0
}

@Serializable
data class NBATeamStats(
    @SerialName("ast") private val _ast: Int? = null,
    @SerialName("blk") private val _blk: Int? = null,
    @SerialName("blka") private val _blka: Int? = null,
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
    @SerialName("min") private val _min: Int? = null,
    @SerialName("oreb") private val _oreb: Int? = null,
    @SerialName("pf") private val _pf: Int? = null,
    @SerialName("pfd") private val _pfd: Int? = null,
    @SerialName("plusMinus") private val _plusMinus: Int? = null,
    @SerialName("pts") private val _pts: Int? = null,
    @SerialName("reb") private val _reb: Int? = null,
    @SerialName("seasonType") private val _seasonType: String? = null,
    @SerialName("stl") private val _stl: Int? = null,
    @SerialName("tov") private val _tov: Int? = null,
    @SerialName("w") private val _w: Int? = null,
    @SerialName("wPct") private val _wPct: Double? = null,
) {
    val ast: Int get() = _ast ?: 0
    val blk: Int get() = _blk ?: 0
    val blka: Int get() = _blka ?: 0
    val dreb: Int get() = _dreb ?: 0
    val fg3a: Int get() = _fg3a ?: 0
    val fg3m: Int get() = _fg3m ?: 0
    val fg3Pct: Double get() = _fg3Pct ?: 0.0
    val fga: Int get() = _fga ?: 0
    val fgm: Int get() = _fgm ?: 0
    val fgPct: Double get() = _fgPct ?: 0.0
    val fta: Int get() = _fta ?: 0
    val ftm: Int get() = _ftm ?: 0
    val ftPct: Double get() = _ftPct ?: 0.0
    val gp: Int get() = _gp ?: 0
    val groupValue: String get() = _groupValue ?: ""
    val losses: Int get() = _l ?: 0
    val min: Int get() = _min ?: 0
    val oreb: Int get() = _oreb ?: 0
    val pf: Int get() = _pf ?: 0
    val pfd: Int get() = _pfd ?: 0
    val plusMinus: Int get() = _plusMinus ?: 0
    val pts: Int get() = _pts ?: 0
    val reb: Int get() = _reb ?: 0
    val seasonType: String get() = _seasonType ?: ""
    val stl: Int get() = _stl ?: 0
    val tov: Int get() = _tov ?: 0
    val wins: Int get() = _w ?: 0
    val winsPct: Double get() = _wPct ?: 0.0

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
    val minPG: Double get() = if (gp != 0) (min.toDouble() /gp).rounded(1) else 0.0
}
