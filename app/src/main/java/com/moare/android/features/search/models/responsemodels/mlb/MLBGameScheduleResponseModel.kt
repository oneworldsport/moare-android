package com.moare.android.features.search.models.responsemodels.mlb

import com.moare.android.features.search.models.models.kbo.KBOGame
import com.moare.android.features.search.models.models.mlb.MLBGame
import com.moare.android.features.search.models.models.mlb.MLBPlayer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MLBGameScheduleResponseModel(
    @SerialName("scheduledMonths") val scheduledMonths: List<String> = emptyList(),
    val schedule: List<MLBGame> = emptyList()
)
