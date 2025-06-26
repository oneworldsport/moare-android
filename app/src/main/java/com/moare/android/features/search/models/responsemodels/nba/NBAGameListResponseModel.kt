package com.moare.android.features.search.models.responsemodels.nba

import com.moare.android.features.search.models.models.nba.NBAGame
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NBAGameListResponseModel(
    @SerialName("scheduledMonths") val scheduledMonths: List<String> = emptyList(),
    val schedule: List<NBAGame> = emptyList()
)
