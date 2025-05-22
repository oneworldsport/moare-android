package com.moare.android.features.search.models.models.kbo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KBOGame(
    val gameInfo: KBOGameInfo?,
    val lineScore: KBOGameLineScoreInfo,
    val lineup: KBOGameLineupInfo
)

@Serializable
data class KBOGameInfo(
    @SerialName("awayTeamId") private val _awayTeamId: String? = null,
    @SerialName("date") private val _date: String? = null,
    @SerialName("gameId") private val _gameId: String? = null,
    @SerialName("homeTeamId") private val _homeTeamId: String? = null,
    @SerialName("remark") private val _remark: String? = null,
) {
    val awayTeamId: String get() = _awayTeamId ?: ""
    val date: String get() = _date ?: ""
    val gameId: String get() = _gameId ?: ""
    val homeTeamId: String get() = _homeTeamId ?: ""
    val remark: String get() = _remark ?: ""
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
    @SerialName("13") private val _inning13: String? = null,
    @SerialName("14") private val _inning14: String? = null,
    @SerialName("15") private val _inning15: String? = null,
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
    val inning13: String get() = _inning13 ?: ""
    val inning14: String get() = _inning14 ?: ""
    val inning15: String get() = _inning15 ?: ""
    val b: String get() = _b ?: ""
    val e: String get() = _e ?: ""
    val h: String get() = _h ?: ""
    val r: String get() = _r ?: ""
    val teamName: String get() = _teamName ?: ""
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
    @SerialName("ab") private val _ab: String? = null,
    @SerialName("bb") private val _bb: String? = null,
    @SerialName("e") private val _e: String? = null,
    @SerialName("gdp") private val _gdp: String? = null,
    @SerialName("h") private val _h: String? = null,
    @SerialName("hr") private val _hr: String? = null,
    @SerialName("playerName") private val _playerName: String? = null,
    @SerialName("r") private val _r: String? = null,
    @SerialName("rbi") private val _rbi: String? = null,
    @SerialName("sb") private val _sb: String? = null,
    @SerialName("sf") private val _sf: String? = null,
    @SerialName("so") private val _so: String? = null,
) {
    val ab: String get() = _ab ?: "" // 타수
    val bb: String get() = _bb ?: "" // 볼넷
    val e: String get() = _e ?: "" // 실책
    val gdp: String get() = _gdp ?: "" // 병살타
    val h: String get() = _h ?: "" // 안타
    val hr: String get() = _hr ?: "" // 홈런
    val playerName: String get() = _playerName ?: ""
    val r: String get() = _r ?: "" // 득점
    val rbi: String get() = _rbi ?: "" // 타점
    val sb: String get() = _sb ?: "" // 도루
    val sf: String get() = _sf ?: "" // 희생플라이
    val so: String get() = _so ?: "" // 삼진
}

@Serializable
data class KBOGamePitcherStats(
    @SerialName("ab") private val _ab: String? = null,
    @SerialName("bb") private val _bb: String? = null,
    @SerialName("er") private val _er: String? = null,
    @SerialName("h") private val _h: String? = null,
    @SerialName("hr") private val _hr: String? = null,
    @SerialName("ip") private val _ip: String? = null,
    @SerialName("np") private val _np: String? = null,
    @SerialName("playerName") private val _playerName: String? = null,
    @SerialName("r") private val _r: String? = null,
    @SerialName("sf") private val _sf: String? = null,
    @SerialName("so") private val _so: String? = null,
    @SerialName("tbf") private val _tbf: String? = null,
) {
    val ab: String get() = _ab ?: "" // 타수
    val bb: String get() = _bb ?: "" // 볼넷
    val er: String get() = _er ?: "" // 자책
    val h: String get() = _h ?: "" // 피안타
    val hr: String get() = _hr ?: "" // 피홈런
    val ip: String get() = _ip ?: "" // 이닝
    val np: String get() = _np ?: "" // 투구수
    val playerName: String get() = _playerName ?: ""
    val r: String get() = _r ?: "" // 실점
    val sf: String get() = _sf ?: "" // 희생타
    val so: String get() = _so ?: "" // 삼진
    val tbf: String get() = _tbf ?: "" // 타자수
}

