package com.moare.android.features.search.models.responsemodels.nba

import com.moare.android.features.search.models.models.nba.NBAPlayer
import kotlinx.serialization.Serializable

@Serializable
data class NBAPlayerStandingsResponseModel(
    val standings: List<NBAPlayer> = emptyList()
)