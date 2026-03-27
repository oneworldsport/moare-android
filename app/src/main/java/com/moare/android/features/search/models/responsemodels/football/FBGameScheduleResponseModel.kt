package com.moare.android.features.search.models.responsemodels.football

import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.models.models.football.FBGameForSchedule
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FBGameScheduleResponseModel(
    val scheduleType: ScheduleType = ScheduleType.LEAGUE,
    val scheduledMonths: List<String> = emptyList(),
    val startDate: String? = null,
    val endDate: String? = null,
    val relatedLeagueIds: List<Int>? = null,
    val schedule: List<FBGameForSchedule> = emptyList(),
    val tournamentStartDate: String? = null
)

@Serializable
enum class ScheduleType {
    @SerialName("team")
    TEAM,
    @SerialName("league")
    LEAGUE,
    @SerialName("team_flat")
    TEAM_FLAT,
    @SerialName("tournament_bracket")
    TOURNAMENT_BRACKET,
    @SerialName("tournament_draw")
    TOURNAMENT_DRAW
}
