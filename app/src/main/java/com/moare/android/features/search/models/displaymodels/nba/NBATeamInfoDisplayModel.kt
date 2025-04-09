package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBATeamInfo
import com.moare.android.features.search.models.models.nba.NBATeamStats
import com.moare.android.features.search.models.models.nba.NBAVenue
import kotlinx.serialization.Serializable

@Serializable
data class NBATeamInfoDisplayModel(
    val team: NBATeamInfo,
    val venue: NBAVenue,
    val stats: NBATeamStats?,
    val lastGame: NBAGame?,
    val nextGame: NBAGame?
)