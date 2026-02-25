package com.moare.android.features.search.models.responsemodels.nba

import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBAGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NBAGameScheduleResponseModel(
    val scheduleType: ScheduleType? = null,
    val scheduledMonths: List<String> = emptyList(),
    val startDate: String? = null,
    val endDate: String? = null,
    val relatedLeagueIds: List<Int>? = null,
    val schedule: List<NBAGameForSchedule> = emptyList()
)