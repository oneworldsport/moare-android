package com.moare.android.features.search.models.responsemodels.nba

import com.moare.android.features.search.models.models.nba.NBATeam
import kotlinx.serialization.Serializable

@Serializable
data class NBATeamStandingsResponseModel(
    val standings: List<NBATeam> = emptyList()
)