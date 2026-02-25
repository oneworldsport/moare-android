package com.moare.android.features.search.models.responsemodels.tennis

import com.moare.android.features.search.models.models.football.FBPlayer
import kotlinx.serialization.Serializable

@Serializable
data class TennisPlayerStandingsResponseModel(
    val standings: List<FBPlayer> = emptyList()
)