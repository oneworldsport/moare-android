package com.moare.android.features.search.models.responsemodels.kbo

import com.moare.android.features.search.models.models.kbo.KBOGame
import com.moare.android.features.search.models.models.kbo.KBOGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KBOGameScheduleResponseModel(
    @SerialName("scheduleType") val scheduleType: ScheduleType? = null,
    @SerialName("scheduledMonths") val scheduledMonths: List<String> = emptyList(),
    val schedule: List<KBOGameForSchedule> = emptyList()
)