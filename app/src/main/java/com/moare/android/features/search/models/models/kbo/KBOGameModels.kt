package com.moare.android.features.search.models.models.kbo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KBOGame(
    val gameInfo: KBOGameInfo?,
    val lineScore: KBOGameLineScoreInfo?,
    val lineup: KBOGameLineupInfo?
)

@Serializable
data class KBOGameInfo(
    @SerialName("awayTeamId") private val _awayTeamId: Int? = null,
    @SerialName("date") private val _date: String? = null,
    @SerialName("gameId") private val _gameId: String? = null,
    @SerialName("homeTeamId") private val _homeTeamId: Int? = null,
    @SerialName("remark") private val _remark: String? = null,
    @SerialName("gameStatus") private val _gameStatus: String? = null,
) {
    val awayTeamId: Int get() = _awayTeamId ?: 0
    val date: String get() = _date ?: ""
    val gameId: String get() = _gameId ?: ""
    val homeTeamId: Int get() = _homeTeamId ?: 0
    val remark: String get() = _remark ?: ""
    val gameStatus: String get() = _gameStatus ?: ""
}

@Serializable
data class KBOGameLineScoreInfo(
    val away: KBOGameLineScore,
    val home: KBOGameLineScore,
)

@Serializable
data class KBOGameLineScore(
    @SerialName("1") private val _inning1: String? = null,
    @SerialName("2") private val _inning2: String? = null,
    @SerialName("3") private val _inning3: String? = null,
    @SerialName("4") private val _inning4: String? = null,
    @SerialName("5") private val _inning5: String? = null,
    @SerialName("6") private val _inning6: String? = null,
    @SerialName("7") private val _inning7: String? = null,
    @SerialName("8") private val _inning8: String? = null,
    @SerialName("9") private val _inning9: String? = null,
    @SerialName("10") private val _inning10: String? = null,
    @SerialName("11") private val _inning11: String? = null,
    @SerialName("12") private val _inning12: String? = null,
    @SerialName("b") private val _b: String? = null,
    @SerialName("e") private val _e: String? = null,
    @SerialName("h") private val _h: String? = null,
    @SerialName("r") private val _r: String? = null,
    @SerialName("teamName") private val _teamName: String? = null,
) {
    val inning1: String get() = _inning1 ?: ""
    val inning2: String get() = _inning2 ?: ""
    val inning3: String get() = _inning3 ?: ""
    val inning4: String get() = _inning4 ?: ""
    val inning5: String get() = _inning5 ?: ""
    val inning6: String get() = _inning6 ?: ""
    val inning7: String get() = _inning7 ?: ""
    val inning8: String get() = _inning8 ?: ""
    val inning9: String get() = _inning9 ?: ""
    val inning10: String get() = _inning10 ?: ""
    val inning11: String get() = _inning11 ?: ""
    val inning12: String get() = _inning12 ?: ""
    val b: String get() = _b ?: ""
    val e: String get() = _e ?: ""
    val h: String get() = _h ?: ""
    val r: String get() = _r ?: ""
    val teamName: String get() = _teamName ?: ""
    val innings: List<String> get() = listOf(inning1, inning2, inning3, inning4, inning5, inning6, inning7, inning8, inning9, inning10, inning11, inning12)
}

@Serializable
data class KBOGameLineupInfo(
    val away: KBOGameLineup,
    val home: KBOGameLineup,
)

@Serializable
data class KBOGameLineup(
    val hitters: List<KBOGameHitterStats>,
    val pitchers: List<KBOGamePitcherStats>,
)

@Serializable
data class KBOGameHitterStats(
    @SerialName("id") private val _id: Int? = null,
    @SerialName("ab") private val _ab: String? = null,
    @SerialName("bb") private val _bb: String? = null,
    @SerialName("e") private val _e: String? = null,
    @SerialName("gdp") private val _gdp: String? = null,
    @SerialName("h") private val _h: String? = null,
    @SerialName("hr") private val _hr: String? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("r") private val _r: String? = null,
    @SerialName("rbi") private val _rbi: String? = null,
    @SerialName("sb") private val _sb: String? = null,
    @SerialName("sf") private val _sf: String? = null,
    @SerialName("so") private val _so: String? = null,
    @SerialName("avg") private val _avg: String? = null,
    @SerialName("batting_number") private val _battingNumber: Int? = null,
    @SerialName("position") private val _position: String? = null,
    @SerialName("inningStats") private val _inningStats: List<KBOGameHitterInningStat>? = null,
) {
    val id: Int get() = _id ?: 0
    val ab: String get() = _ab ?: "0" // 타수
    val bb: String get() = _bb ?: "0" // 볼넷
    val e: String get() = _e ?: "0" // 실책
    val gdp: String get() = _gdp ?: "0" // 병살타
    val h: String get() = _h ?: "0" // 안타
    val hr: String get() = _hr ?: "0" // 홈런
    val name: String get() = _name ?: ""
    val r: String get() = _r ?: "0" // 득점
    val rbi: String get() = _rbi ?: "0" // 타점
    val sb: String get() = _sb ?: "0" // 도루
    val sf: String get() = _sf ?: "0" // 희생플라이
    val so: String get() = _so ?: "0" // 삼진
    val avg: String get() = _avg ?: "0.000" // 타율
    val battingNumber: Int get() = _battingNumber ?: 0
    val position: String get() = _position ?: ""
}

@Serializable
data class KBOGameHitterInningStat(
    @SerialName("num") private val _num: Int? = null,
    @SerialName("info") private val _info: String? = null
) {
    val num: Int get() = _num ?: 0
    val info: String get() = _info ?: ""
}

@Serializable
data class KBOGamePitcherStats(
    @SerialName("id") private val _id: Int? = null,
    @SerialName("ab") private val _ab: String? = null,
    @SerialName("bb") private val _bb: String? = null,
    @SerialName("er") private val _er: String? = null,
    @SerialName("h") private val _h: String? = null,
    @SerialName("hr") private val _hr: String? = null,
    @SerialName("ip") private val _ip: String? = null,
    @SerialName("np") private val _np: String? = null,
    @SerialName("name") private val _name: String? = null,
    @SerialName("r") private val _r: String? = null,
    @SerialName("so") private val _so: String? = null,
    @SerialName("tbf") private val _tbf: String? = null,
    @SerialName("appearance") private val _appearance: String? = null,
    @SerialName("result") private val _result: String? = null,
    @SerialName("w") private val _w: String? = null,
    @SerialName("l") private val _l: String? = null,
    @SerialName("sv") private val _sv: String? = null,
    @SerialName("era") private val _era: String? = null,
) {
    val id: Int get() = _id ?: 0
    val ab: String get() = _ab ?: "0" // 타수
    val bb: String get() = _bb ?: "0" // 볼넷
    val er: String get() = _er ?: "0" // 자책
    val h: String get() = _h ?: "0" // 피안타
    val hr: String get() = _hr ?: "0" // 피홈런
    val ip: String get() = _ip ?: "0.0" // 이닝
    val np: String get() = _np ?: "0" // 투구수
    val name: String get() = _name ?: ""
    val r: String get() = _r ?: "0" // 실점
    val so: String get() = _so ?: "0" // 삼진
    val tbf: String get() = _tbf ?: "0" // 타자수
    val appearance: String get() = _appearance ?: "" // 등판
    val result: String get() = _result ?: "" // 결과
    val w: String get() = _w ?: "0" // 승
    val l: String get() = _l ?: "0" // 패
    val sv: String get() = _sv ?: "0" // 세이브
    val era: String get() = _era ?: "0.0" // 평균자책점
}

