package com.moare.android.features.search.models.responsemodels.mlb

import com.moare.android.features.search.models.models.mlb.MLBGame
import com.moare.android.features.search.models.models.mlb.MLBPlayer
import kotlinx.serialization.Serializable

@Serializable
data class MLBPlayerInfoResponseModel(
    val info: MLBPlayer? = null,
    val lastGame: MLBGame? = null,
    val nextGame: MLBGame? = null
)
