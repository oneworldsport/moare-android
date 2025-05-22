package com.moare.android.features.search.models.responsemodels.mlb

import com.moare.android.features.search.models.models.mlb.MLBGame
import com.moare.android.features.search.models.models.mlb.MLBPlayer
import kotlinx.serialization.Serializable

@Serializable
data class MLBGameStatsResponseModel(
    val game: MLBGame? = null
)
