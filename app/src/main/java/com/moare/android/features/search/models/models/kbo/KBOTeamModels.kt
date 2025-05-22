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
    @SerialName("season") private val _season: Int? = null,
    @SerialName("seasonType") private val _seasonType: String? = null,
) {
    val season: Int get() = _season ?: 0
    val seasonType: String get() = _seasonType ?: ""
}

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
    @SerialName("rank") private val _rank: String? = null,
    @SerialName("teamName") private val _teamName: String? = null
) {
    val a: String get() = _a ?: "" // 어시스트
    val cs: String get() = _cs ?: "" // 도루 실패
    val csPercent: String get() = _csPercent ?: "" // 도루 저지율
    val dp: String get() = _dp ?: "" // 병살
    val e: String get() = _e ?: "" // 실책
    val fpct: String get() = _fpct ?: "" // 수비율
    val g: String get() = _g ?: "" // 경기
    val pb: String get() = _pb ?: "" // 포일
    val pko: String get() = _pko ?: "" // 견제사
    val po: String get() = _po ?: "" // 풋아웃
    val sb: String get() = _sb ?: "" // 도류 허용
    val rank: String get() = _rank ?: ""
    val teamName: String get() = _teamName ?: ""
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
    @SerialName("rank") private val _rank: String? = null,
    @SerialName("teamName") private val _teamName: String? = null
) {
    val double: String get() = _double ?: "" // 2루타
    val triple: String get() = _triple ?: "" // 3루타
    val ab: String get() = _ab ?: "" // 타수
    val avg: String get() = _avg ?: "" // 타율
    val bb: String get() = _bb ?: "" // 볼넷
    val g: String get() = _g ?: "" // 경기수
    val gdp: String get() = _gdp ?: "" // 병살타
    val h: String get() = _h ?: "" // 안타
    val hbp: String get() = _hbp ?: "" // 사구
    val hr: String get() = _hr ?: "" // 홈런
    val ibb: String get() = _ibb ?: "" // 고의4구
    val mh: String get() = _mh ?: "" // 멀티히트
    val obp: String get() = _obp ?: "" // 출루율
    val ops: String get() = _ops ?: "" // 출루율 + 장타율
    val pa: String get() = _pa ?: "" // 타석
    val phBa: String get() = _phBa ?: "" // 대타타율
    val r: String get() = _r ?: "" // 득점
    val rbi: String get() = _rbi ?: "" // 타점
    val risp: String get() = _risp ?: "" // 득점권타율
    val sac: String get() = _sac ?: "" // 희생번트
    val sf: String get() = _sf ?: "" // 희생플라이
    val slg: String get() = _slg ?: "" // 장타율
    val so: String get() = _so ?: "" // 삼진
    val tb: String get() = _tb ?: "" // 루타
    val rank: String get() = _rank ?: ""
    val teamName: String get() = _teamName ?: ""
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
    @SerialName("rank") private val _rank: String? = null,
    @SerialName("teamName") private val _teamName: String? = null
) {
    val double: String get() = _double ?: "" // 피2루타
    val triple: String get() = _triple ?: "" // 피3루타
    val avg: String get() = _avg ?: "" // 피안타율
    val bb: String get() = _bb ?: "" // 볼넷
    val bk: String get() = _bk ?: "" // 보크
    val bsv: String get() = _bsv ?: "" // 블론세이브
    val cg: String get() = _cg ?: "" // 완투
    val er: String get() = _er ?: "" // 자책점
    val era: String get() = _era ?: "" // 평균자책점
    val g: String get() = _g ?: "" // 경기
    val h: String get() = _h ?: "" // 피안타
    val hbp: String get() = _hbp ?: "" // 사구
    val hld: String get() = _hld ?: "" // 홀드
    val hr: String get() = _hr ?: "" // 피홈런
    val ibb: String get() = _ibb ?: "" // 고의4구
    val ip: String get() = _ip ?: "" // 이닝
    val l: String get() = _l ?: "" // 패
    val np: String get() = _np ?: "" // 투구수
    val qs: String get() = _qs ?: "" // 퀄리티스타트
    val r: String get() = _r ?: "" // 실점
    val sac: String get() = _sac ?: "" // 희생번트
    val sf: String get() = _sf ?: "" // 희생플라이
    val sho: String get() = _sho ?: "" // 완봉
    val so: String get() = _so ?: "" // 삼진
    val sv: String get() = _sv ?: "" // 세이브
    val tbf: String get() = _tbf ?: "" // 타자수
    val w: String get() = _w ?: "" // 승리
    val whip: String get() = _whip ?: "" // 이닝당 출루허용률
    val wp: String get() = _wp ?: "" // 폭투
    val wpct: String get() = _wpct ?: "" // 승률
    val rank: String get() = _rank ?: ""
    val teamName: String get() = _teamName ?: ""
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
    @SerialName("rank") private val _rank: String? = null,
    @SerialName("teamName") private val _teamName: String? = null
) {
    val cs: String get() = _cs ?: "" // 도루실패
    val g: String get() = _g ?: "" // 경기
    val oob: String get() = _oob ?: "" // 주루사
    val pko: String get() = _pko ?: "" // 견제사
    val sb: String get() = _sb ?: "" // 도루성공
    val sbPercent: String get() = _sbPercent ?: "" // 도루성공률
    val sba: String get() = _sba ?: "" // 도루시도
    val rank: String get() = _rank ?: ""
    val teamName: String get() = _teamName ?: ""
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
    @SerialName("rank") private val _rank: String? = null,
    @SerialName("teamName") private val _teamName: String? = null
) {
    val awayrecord: String get() = _awayrecord ?: ""
    val draws: String get() = _draws ?: ""
    val gb: String get() = _gb ?: "" // 게임차
    val gp: String get() = _gp ?: ""
    val homerecord: String get() = _homerecord ?: ""
    val last10game: String get() = _last10game ?: ""
    val losses: String get() = _losses ?: ""
    val streak: String get() = _streak ?: ""
    val winpct: String get() = _winpct ?: ""
    val wins: String get() = _wins ?: ""
    val rank: String get() = _rank ?: ""
    val teamName: String get() = _teamName ?: ""
}

