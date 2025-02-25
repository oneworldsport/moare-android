package com.moare.android.features.search.models.responsemodels.football

import com.moare.android.features.search.models.models.football.FBGame
import kotlinx.serialization.Serializable

@Serializable
data class FBGameStatsResponseModel(
    val game: FBGame? = null
)
