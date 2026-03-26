package com.moare.android.features.search.models.models.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameForSchedule<T>(
    @SerialName("itemKey") private val _itemKey: String? = null,
    @SerialName("homeTeamId") private val _homeTeamId: Int? = null,
    @SerialName("awayTeamId") private val _awayTeamId: Int? = null,
    @SerialName("homeTeamScore") private val _homeTeamScore: Int? = null,
    @SerialName("awayTeamScore") private val _awayTeamScore: Int? = null,
    @SerialName("gameStatus") private val _gameStatus: String? = null,
    val isHomeTopSeed: Boolean? = null,
    val gameInfo: T? = null
) {
    val itemKey: String get() = _itemKey ?: ""
    val homeTeamId: Int get() = _homeTeamId ?: 0
    val awayTeamId: Int get() = _awayTeamId ?: 0
    val homeTeamScore: Int get() = _homeTeamScore ?: 0
    val awayTeamScore: Int get() = _awayTeamScore ?: 0
    val gameStatus: String get() = _gameStatus ?: ""

    val gameId: String get() = _itemKey?.split("#")?.lastOrNull() ?: ""
    val date: String get() = (_itemKey?.split("#")?.firstOrNull() ?: "") + "+09:00"
}
