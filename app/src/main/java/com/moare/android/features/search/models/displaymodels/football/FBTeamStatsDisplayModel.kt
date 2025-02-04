package com.moare.android.features.search.models.displaymodels.football

import com.moare.android.features.search.models.models.football.FBTeamInfo
import com.moare.android.features.search.models.models.football.FBVenue
import com.moare.android.features.search.models.models.football.FBTeamStats
import kotlinx.serialization.Serializable

@Serializable
data class FBTeamStatsDisplayModel(
    val team: FBTeamInfo,
    val venue: FBVenue,
    val stats: List<FBTeamStats>
)
