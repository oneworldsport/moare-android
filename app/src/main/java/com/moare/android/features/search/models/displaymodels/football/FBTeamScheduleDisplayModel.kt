package com.moare.android.features.search.models.displaymodels.football

import com.moare.android.features.search.models.displaymodels.LeagueIdentifiable
import com.moare.android.features.search.models.models.football.FBGame
import kotlinx.serialization.Serializable

@Serializable
data class FBTeamScheduleDisplayModel(
    override val leagueId: Int,
    val games: List<FBGame>
) : LeagueIdentifiable
