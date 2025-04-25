package com.moare.android.features.search.models.displaymodels.football

import com.moare.android.features.search.models.models.football.FBGame
import kotlinx.serialization.Serializable

@Serializable
data class FBTeamScheduleDisplayModel(
    val games: List<FBGame>,
    val leagueId: Int?
)
