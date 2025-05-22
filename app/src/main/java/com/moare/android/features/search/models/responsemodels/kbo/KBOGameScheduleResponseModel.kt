package com.moare.android.features.search.models.responsemodels.kbo

import com.moare.android.features.search.models.models.kbo.KBOGame
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KBOGameScheduleResponseModel(
    @SerialName("scheduledMonths") val scheduledMonths: List<String> = emptyList(),
    val schedule: List<KBOGame> = emptyList()
)