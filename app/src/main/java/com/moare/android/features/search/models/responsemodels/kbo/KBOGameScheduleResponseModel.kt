package com.moare.android.features.search.models.responsemodels.kbo

import com.moare.android.features.search.models.models.kbo.KBOGame
import com.moare.android.features.search.models.models.kbo.KBOGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KBOGameScheduleResponseModel(
    val scheduleType: ScheduleType? = null,
    val scheduledMonths: List<String> = emptyList(),
    val startDate: String? = null,
    val endDate: String? = null,
    val relatedLeagueIds: List<Int>? = null,
    val schedule: List<KBOGameForSchedule> = emptyList()
)