package com.moare.android.features.search.models.responsemodels.nba

import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBAGameForSchedule
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NBAGameScheduleResponseModel(
    @SerialName("scheduledMonths") val scheduledMonths: List<String> = emptyList(),
    val schedule: List<NBAGameForSchedule> = emptyList()
)