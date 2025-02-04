package com.moare.android.features.search.models.responsemodels.football

import com.moare.android.features.search.models.models.football.FBTeam
import kotlinx.serialization.Serializable

@Serializable
data class FBTeamStandingsResponseModel(
    val standings: List<FBTeam> = emptyList()
)
