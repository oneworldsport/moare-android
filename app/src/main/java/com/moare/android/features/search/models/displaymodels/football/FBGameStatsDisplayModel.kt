package com.moare.android.features.search.models.displaymodels.football

import com.moare.android.features.search.models.displaymodels.LeagueIdentifiable
import com.moare.android.features.search.models.models.football.FBGame
import kotlinx.serialization.Serializable

@Serializable
data class FBGameStatsDisplayModel(
    override val leagueId: Int,
    val game: FBGame
) : LeagueIdentifiable
