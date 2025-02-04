package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.models.nba.NBATeam
import com.moare.android.features.search.models.models.nba.NBATeamStats
import kotlinx.serialization.Serializable

@Serializable
data class NBATeamStatsDisplayModel(
    val team: NBATeam?,
    val stats: NBATeamStats?,
)