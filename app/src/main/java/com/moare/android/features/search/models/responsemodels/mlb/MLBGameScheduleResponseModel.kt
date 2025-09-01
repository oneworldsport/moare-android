package com.moare.android.features.search.models.responsemodels.mlb

import com.moare.android.features.search.models.models.kbo.KBOGame
import com.moare.android.features.search.models.models.mlb.MLBGame
import com.moare.android.features.search.models.models.mlb.MLBGameForSchedule
import com.moare.android.features.search.models.models.mlb.MLBPlayer
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MLBGameScheduleResponseModel(
    @SerialName("scheduleType") val scheduleType: ScheduleType = ScheduleType.LEAGUE,
    @SerialName("scheduledMonths") val scheduledMonths: List<String> = emptyList(),
    val schedule: List<MLBGameForSchedule> = emptyList()
)
