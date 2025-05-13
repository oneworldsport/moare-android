package com.moare.android.features.search.models.models.kbo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KBOTeam(
    val team: KBOTeamInfo,
    val venue: KBOTeamVenue,
    val statistics: List<KBOTeamStats>
)

@Serializable
data class KBOTeamInfo(
    @SerialName("city") private val _city: String? = null,
    @SerialName("coach") private val _coach: String? = null,
    @SerialName("id") private val _id: Int? = null,
    @SerialName("teamCode") private val _teamCode: String? = null,
    @SerialName("teamName") private val _teamName: String? = null,
    @SerialName("yearFounded") private val _yearFounded: Int? = null,
) {
    val city: String get() = _city ?: ""
    val coach: String get() = _coach ?: ""
    val id: Int get() = _id ?: 0
    val teamCode: String get() = _teamCode ?: ""
    val teamName: String get() = _teamName ?: ""
    val yearFounded: Int get() = _yearFounded ?: 0
}

@Serializable
data class KBOTeamVenue(
    @SerialName("capacity") private val _capacity: Int? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("opened") private val _opened: Int? = null,
) {
    val capacity: Int get() = _capacity ?: 0
    val name: String get() = _name ?: ""
    val opened: Int get() = _opened ?: 0
}

@Serializable
data class KBOTeamStats(
    val defenseData: KBOTeamDefenseData,
    val hitterData: KBOTeamHitterData,
    val pitcherData: KBOTeamPitcherData,
    val rankData: KBOTeamRankData,
    val runnerData: KBOTeamRunnerData,
)

@Serializable
data class KBOTeamDefenseData(
    @SerialName("a") private val _a: String? = null,
    @SerialName("cs") private val _cs: String? = null,
    @SerialName("cs%") private val _csPercent: String? = null,
    @SerialName("dp") private val _dp: String? = null,
    @SerialName("e") private val _e: String? = null,
    @SerialName("fpct") private val _fpct: String? = null,
    @SerialName("g") private val _g: String? = null,
    @SerialName("pb") private val _pb: String? = null,
    @SerialName("pko") private val _pko: String? = null,
    @SerialName("po") private val _po: String? = null,
    @SerialName("sb") private val _sb: String? = null,
) {
    val a: String get() = _a ?: ""
    val cs: String get() = _cs ?: ""
    val csPercent: String get() = _csPercent ?: ""
    val dp: String get() = _dp ?: ""
    val e: String get() = _e ?: ""
    val fpct: String get() = _fpct ?: ""
    val g: String get() = _g ?: ""
    val pb: String get() = _pb ?: ""
    val pko: String get() = _pko ?: ""
    val po: String get() = _po ?: ""
    val sb: String get() = _sb ?: ""
}

@Serializable
data class KBOTeamHitterData(
    @SerialName("2b") private val _double: String? = null,
    @SerialName("3b") private val _triple: String? = null,
    @SerialName("ab") private val _ab: String? = null,
    @SerialName("avg") private val _avg: String? = null,
    @SerialName("bb") private val _bb: String? = null,
    @SerialName("g") private val _g: String? = null,
    @SerialName("gdp") private val _gdp: String? = null,
    @SerialName("h") private val _h: String? = null,
    @SerialName("hbp") private val _hbp: String? = null,
    @SerialName("hr") private val _hr: String? = null,
    @SerialName("ibb") private val _ibb: String? = null,
    @SerialName("mh") private val _mh: String? = null,
    @SerialName("obp") private val _obp: String? = null,
    @SerialName("ops") private val _ops: String? = null,
    @SerialName("pa") private val _pa: String? = null,
    @SerialName("ph-ba") private val _phBa: String? = null,
    @SerialName("r") private val _r: String? = null,
    @SerialName("rbi") private val _rbi: String? = null,
    @SerialName("risp") private val _risp: String? = null,
    @SerialName("sac") private val _sac: String? = null,
    @SerialName("sf") private val _sf: String? = null,
    @SerialName("slg") private val _slg: String? = null,
    @SerialName("so") private val _so: String? = null,
    @SerialName("tb") private val _tb: String? = null,
) {
    val double: String get() = _double ?: ""
    val triple: String get() = _triple ?: ""
    val ab: String get() = _ab ?: ""
    val avg: String get() = _avg ?: ""
    val bb: String get() = _bb ?: ""
    val g: String get() = _g ?: ""
    val gdp: String get() = _gdp ?: ""
    val h: String get() = _h ?: ""
    val hbp: String get() = _hbp ?: ""
    val hr: String get() = _hr ?: ""
    val ibb: String get() = _ibb ?: ""
    val mh: String get() = _mh ?: ""
    val obp: String get() = _obp ?: ""
    val ops: String get() = _ops ?: ""
    val pa: String get() = _pa ?: ""
    val phBa: String get() = _phBa ?: ""
    val r: String get() = _r ?: ""
    val rbi: String get() = _rbi ?: ""
    val risp: String get() = _risp ?: ""
    val sac: String get() = _sac ?: ""
    val sf: String get() = _sf ?: ""
    val slg: String get() = _slg ?: ""
    val so: String get() = _so ?: ""
    val tb: String get() = _tb ?: ""
}

@Serializable
data class KBOTeamPitcherData(
    @SerialName("2b") private val _double: String? = null,
    @SerialName("3b") private val _triple: String? = null,
    @SerialName("avg") private val _avg: String? = null,
    @SerialName("bb") private val _bb: String? = null,
    @SerialName("bk") private val _bk: String? = null,
    @SerialName("bsv") private val _bsv: String? = null,
    @SerialName("cg") private val _cg: String? = null,
    @SerialName("er") private val _er: String? = null,
    @SerialName("era") private val _era: String? = null,
    @SerialName("g") private val _g: String? = null,
    @SerialName("h") private val _h: String? = null,
    @SerialName("hbp") private val _hbp: String? = null,
    @SerialName("hld") private val _hld: String? = null,
    @SerialName("hr") private val _hr: String? = null,
    @SerialName("ibb") private val _ibb: String? = null,
    @SerialName("ip") private val _ip: String? = null,
    @SerialName("l") private val _l: String? = null,
    @SerialName("np") private val _np: String? = null,
    @SerialName("qs") private val _qs: String? = null,
    @SerialName("r") private val _r: String? = null,
    @SerialName("sac") private val _sac: String? = null,
    @SerialName("sf") private val _sf: String? = null,
    @SerialName("sho") private val _sho: String? = null,
    @SerialName("so") private val _so: String? = null,
    @SerialName("sv") private val _sv: String? = null,
    @SerialName("tbf") private val _tbf: String? = null,
    @SerialName("w") private val _w: String? = null,
    @SerialName("whip") private val _whip: String? = null,
    @SerialName("wp") private val _wp: String? = null,
    @SerialName("wpct") private val _wpct: String? = null,
) {
    val double: String get() = _double ?: ""
    val triple: String get() = _triple ?: ""
    val avg: String get() = _avg ?: ""
    val bb: String get() = _bb ?: ""
    val bk: String get() = _bk ?: ""
    val bsv: String get() = _bsv ?: ""
    val cg: String get() = _cg ?: ""
    val er: String get() = _er ?: ""
    val era: String get() = _era ?: ""
    val g: String get() = _g ?: ""
    val h: String get() = _h ?: ""
    val hbp: String get() = _hbp ?: ""
    val hld: String get() = _hld ?: ""
    val hr: String get() = _hr ?: ""
    val ibb: String get() = _ibb ?: ""
    val ip: String get() = _ip ?: ""
    val l: String get() = _l ?: ""
    val np: String get() = _np ?: ""
    val qs: String get() = _qs ?: ""
    val r: String get() = _r ?: ""
    val sac: String get() = _sac ?: ""
    val sf: String get() = _sf ?: ""
    val sho: String get() = _sho ?: ""
    val so: String get() = _so ?: ""
    val sv: String get() = _sv ?: ""
    val tbf: String get() = _tbf ?: ""
    val w: String get() = _w ?: ""
    val whip: String get() = _whip ?: ""
    val wp: String get() = _wp ?: ""
    val wpct: String get() = _wpct ?: ""
}

@Serializable
data class KBOTeamRunnerData(
    @SerialName("cs") private val _cs: String? = null,
    @SerialName("g") private val _g: String? = null,
    @SerialName("oob") private val _oob: String? = null,
    @SerialName("pko") private val _pko: String? = null,
    @SerialName("sb") private val _sb: String? = null,
    @SerialName("sb%") private val _sbPercent: String? = null,
    @SerialName("sba") private val _sba: String? = null,
) {
    val cs: String get() = _cs ?: ""
    val g: String get() = _g ?: ""
    val oob: String get() = _oob ?: ""
    val pko: String get() = _pko ?: ""
    val sb: String get() = _sb ?: ""
    val sbPercent: String get() = _sbPercent ?: ""
    val sba: String get() = _sba ?: ""
}

@Serializable
data class KBOTeamRankData(
    @SerialName("awayrecord") private val _awayrecord: String? = null,
    @SerialName("draws") private val _draws: String? = null,
    @SerialName("gb") private val _gb: String? = null,
    @SerialName("gp") private val _gp: String? = null,
    @SerialName("homerecord") private val _homerecord: String? = null,
    @SerialName("last10game") private val _last10game: String? = null,
    @SerialName("losses") private val _losses: String? = null,
    @SerialName("streak") private val _streak: String? = null,
    @SerialName("winpct") private val _winpct: String? = null,
    @SerialName("wins") private val _wins: String? = null,
) {
    val awayrecord: String get() = _awayrecord ?: ""
    val draws: String get() = _draws ?: ""
    val gb: String get() = _gb ?: ""
    val gp: String get() = _gp ?: ""
    val homerecord: String get() = _homerecord ?: ""
    val last10game: String get() = _last10game ?: ""
    val losses: String get() = _losses ?: ""
    val streak: String get() = _streak ?: ""
    val winpct: String get() = _winpct ?: ""
    val wins: String get() = _wins ?: ""
}

