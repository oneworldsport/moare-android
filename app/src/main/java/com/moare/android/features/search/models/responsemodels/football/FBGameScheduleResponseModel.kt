package com.moare.android.features.search.models.responsemodels.football

import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.models.models.football.FBGameForSchedule
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FBGameScheduleResponseModel(
    @SerialName("scheduledMonths") val scheduledMonths: List<String> = emptyList(),
    val schedule: List<FBGameForSchedule> = emptyList()
)
