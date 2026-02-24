package com.moare.android.features.search.models.responsemodels.tennis

import com.moare.android.features.search.models.models.football.FBGameForSchedule
import com.moare.android.features.search.models.models.tennis.TennisGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TennisGameScheduleResponseModel(
    val scheduleType: ScheduleType = ScheduleType.LEAGUE,
    val scheduledMonths: List<String> = emptyList(),
    val startDate: String? = null,
    val endDate: String? = null,
    val relatedLeagues: List<Int>? = null,
    val schedule: List<TennisGameForSchedule> = emptyList()
)