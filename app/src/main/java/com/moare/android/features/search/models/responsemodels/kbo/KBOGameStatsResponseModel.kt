package com.moare.android.features.search.models.responsemodels.kbo

import com.moare.android.features.search.models.models.kbo.KBOGame
import kotlinx.serialization.Serializable

@Serializable
data class KBOGameStatsResponseModel(
    val game: KBOGame? = null
)
