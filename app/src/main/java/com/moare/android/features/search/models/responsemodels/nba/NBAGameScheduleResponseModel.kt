package com.moare.android.features.search.models.responsemodels.nba

import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBAGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NBAGameScheduleResponseModel(
    @SerialName("scheduleType") val scheduleType: ScheduleType = ScheduleType.LEAGUE,
    @SerialName("scheduledMonths") val scheduledMonths: List<String> = emptyList(),
    val schedule: List<NBAGameForSchedule> = emptyList()
)