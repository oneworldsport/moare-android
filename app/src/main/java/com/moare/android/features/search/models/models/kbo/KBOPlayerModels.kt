package com.moare.android.features.search.models.models.kbo

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
    @SerialName("season") private val _season: Int? = null,
    @SerialName("seasonType") private val _seasonType: String? = null,
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
    val hld: String get() = _hld ?: ""
    val hr: String get() = _hr ?: ""
    val ibb: String get() = _ibb ?: ""
    val ip: String get() = _ip ?: ""
    val l: String get() = _l ?: ""
    val np: String get() = _np ?: ""
    val qs: String get() = _qs ?: ""
    val r: String get() = _r ?: ""
    val sac: String get() = _sac ?: ""
    val season: Int get() = _season ?: 0
    val seasonType: String get() = _seasonType ?: ""
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
