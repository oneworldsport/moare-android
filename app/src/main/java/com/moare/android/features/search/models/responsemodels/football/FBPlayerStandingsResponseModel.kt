package com.moare.android.features.search.models.responsemodels.football

import com.moare.android.features.search.models.models.football.FBPlayer
import kotlinx.serialization.Serializable

@Serializable
data class FBPlayerStandingsResponseModel(
    val standings: List<FBPlayer> = emptyList()
)
