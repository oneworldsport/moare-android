package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.displaymodels.LeagueIdentifiable
import com.moare.android.features.search.models.models.nba.NBAGame
import kotlinx.serialization.Serializable

@Serializable
data class NBATeamScheduleDisplayModel(
    override val leagueId: Int,
    val games: List<NBAGame>
) : LeagueIdentifiable