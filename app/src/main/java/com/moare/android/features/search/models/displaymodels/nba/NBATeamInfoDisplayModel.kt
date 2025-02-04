package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBATeam
import com.moare.android.features.search.models.models.nba.NBATeamStats
import kotlinx.serialization.Serializable

@Serializable
data class NBATeamInfoDisplayModel(
    val team: NBATeam?,
    val stats: NBATeamStats?,
    val lastGame: NBAGame?,
    val nextGame: NBAGame?
)