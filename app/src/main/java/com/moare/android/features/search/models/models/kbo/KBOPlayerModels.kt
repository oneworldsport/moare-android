package com.moare.android.features.search.models.models.kbo

import com.moare.android.core.util.rounded
import com.moare.android.features.search.models.models.football.FBPlayerInfo
import com.moare.android.features.search.models.models.football.FBPlayerStats
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KBOPlayer(
    val player: KBOPlayerInfo,
    val statistics: List<KBOPlayerStats>
)

@Serializable
data class KBOPlayerInfo(
    @SerialName("birthdate") private val _birthdate: String? = null,
    @SerialName("career") private val _career: String? = null,
    @SerialName("draftRound") private val _draftRound: String? = null,
    @SerialName("fromYear") private val _fromYear: String? = null,
    @SerialName("height") private val _height: String? = null,
    @SerialName("id") private val _id: Int? = null,
    @SerialName("jersey") private val _jersey: String? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("position") private val _position: String? = null,
    @SerialName("salary") private val _salary: String? = null,
    @SerialName("signingBonus") private val _signingBonus: String? = null,
    @SerialName("teamId") private val _teamId: Int? = null,
    @SerialName("weight") private val _weight: String? = null,
) {
    val birthdate: String get() = _birthdate ?: ""
    val career: String get() = _career ?: ""
    val draftRound: String get() = _draftRound ?: ""
    val fromYear: String get() = _fromYear ?: ""
    val height: String get() = _height ?: ""
    val id: Int get() = _id ?: 0
    val jersey: String get() = _jersey ?: ""
    val name: String get() = _name ?: ""
    val position: String get() = _position ?: ""
    val salary: String get() = _salary ?: ""
    val signingBonus: String get() = _signingBonus ?: ""
    val teamId: Int get() = _teamId ?: 0
    val weight: String get() = _weight ?: ""
}

@Serializable
data class KBOPlayerStats(
    val hitter: KBOPlayerHitterStats?,
    val pitcher: KBOPlayerPitcherStats?,
    @SerialName("season") private val _season: Int? = null,
    @SerialName("seasonType") private val _seasonType: String? = null,
) {
    val season: Int get() = _season ?: 0
    val seasonType: String get() = _seasonType ?: ""
}


@Serializable
data class KBOPlayerHitterStats(
    @SerialName("2b") private val _double: String? = null,
    @SerialName("3b") private val _triple: String? = null,
    @SerialName("ab") private val _ab: String? = null,
    @SerialName("avg") private val _avg: String? = null,
    @SerialName("bb") private val _bb: String? = null,
    @SerialName("cs") private val _cs: String? = null,
    @SerialName("e") private val _e: String? = null,
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
    @SerialName("sb") private val _sb: String? = null,
    @SerialName("sb%") private val _sbPercent: String? = null,
    @SerialName("sf") private val _sf: String? = null,
    @SerialName("slg") private val _slg: String? = null,
    @SerialName("so") private val _so: String? = null,
    @SerialName("tb") private val _tb: String? = null
) {
    val double: String get() = _double ?: "" // 2루타
    val triple: String get() = _triple ?: "" // 3루타
    val ab: String get() = _ab ?: "" // 타수
    val avg: String get() = _avg ?: "" // 타율
    val bb: String get() = _bb ?: "" // 볼넷
    val cs: String get() = _cs ?: "" // 도루실패
    val e: String get() = _e ?: "" // 실책
    val g: String get() = _g ?: "" // 경기수
    val gdp: String get() = _gdp ?: "" // 병살타
    val h: String get() = _h ?: "" // 안타
    val hbp: String get() = _hbp ?: "" // 사구
    val hr: String get() = _hr ?: "" // 홈런
    val ibb: String get() = _ibb ?: "" // 고의4구
    val mh: String get() = _mh ?: "" // 멀티히트
    val obp: String get() = _obp ?: "" // 출루율
    val ops: String get() = _ops ?: "" // 출루율+장타율
    val pa: String get() = _pa ?: "" // 타석
    val phBa: String get() = _phBa ?: "" // 대타 타율
    val r: String get() = _r ?: "" // 득점
    val rbi: String get() = _rbi ?: "" // 타점
    val risp: String get() = _risp ?: "" // 득점권 타율
    val sac: String get() = _sac ?: "" // 희생번트
    val sb: String get() = _sb ?: "" // 도루
    val sbPercent: String get() = _sbPercent ?: "" // 도루성골률
    val sf: String get() = _sf ?: "" // 희생플라이
    val slg: String get() = _slg ?: "" // 장타율
    val so: String get() = _so ?: "" // 삼진
    val tb: String get() = _tb ?: "" // 루타
}

@Serializable
data class KBOPlayerPitcherStats(
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
    @SerialName("wpct") private val _wpct: String? = null
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
    val g: String get() = _g ?: "" // 경기수
    val h: String get() = _h ?: "" // 피안타
    val hld: String get() = _hld ?: "" // 홀드
    val hr: String get() = _hr ?: "" // 피홈런
    val ibb: String get() = _ibb ?: "" // 고의4구
    val ip: String get() = _ip ?: "" // 이닝
    val l: String get() = _l ?: "" // 패배
    val np: String get() = _np ?: "" // 투구수
    val qs: String get() = _qs ?: "" // 퀄리티 스타트
    val r: String get() = _r ?: "" // 실점
    val sac: String get() = _sac ?: "" // 피희생번트
    val sf: String get() = _sf ?: "" // 피희생플라이
    val sho: String get() = _sho ?: "" // 완봉
    val so: String get() = _so ?: "" // 삼진
    val sv: String get() = _sv ?: "" // 세이브
    val tbf: String get() = _tbf ?: "" // 타자수
    val w: String get() = _w ?: "" // 승리
    val whip: String get() = _whip ?: "" // 이닝당 출루허용률
    val wp: String get() = _wp ?: "" // 폭투
    val wpct: String get() = _wpct ?: "" // 승률(투수 개인 승률. 출전 시 팀 승률 아님)

    // 경기당 볼넷
    // 경기당 평균 투구수
    val npsPG: Double get() = if (g.toInt() != 0) (np.toDouble() / g.toInt()).rounded(1) else 0.0
    // 경기당 평균 이닝수
//    val npsPG: Double get() = if (g.toInt() != 0) (np.toDouble() / g.toInt()).rounded(1) else 0.0
}

