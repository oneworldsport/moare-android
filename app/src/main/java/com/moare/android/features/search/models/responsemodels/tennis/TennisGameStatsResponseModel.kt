package com.moare.android.features.search.models.responsemodels.tennis

import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.models.models.tennis.TennisGame
import kotlinx.serialization.Serializable

@Serializable
data class TennisGameStatsResponseModel(
    val game: TennisGame? = null
)