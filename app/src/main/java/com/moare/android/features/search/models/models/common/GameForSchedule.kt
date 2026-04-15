package com.moare.android.features.search.models.models.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

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

    // NOTE: null 값이 필요한 프로퍼티는 따로 nullable getter를 추가. private을 제거하고 _property를 그대로 사용하는건 비추.
    // TODO: 다른곳도 해당 방식으로 리팩토링 필요
    val homeTeamIdOrNull: Int? get() = _homeTeamId
    val awayTeamIdOrNull: Int? get() = _awayTeamId

    val gameId: String get() = _itemKey?.split("#")?.lastOrNull() ?: ""
    val date: String get() = (_itemKey?.split("#")?.firstOrNull() ?: "") + "+09:00"
    val parsedDate get() = OffsetDateTime.parse(date).toInstant()
}
