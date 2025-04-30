package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.displaymodels.LeagueIdentifiable
import com.moare.android.features.search.models.models.nba.NBAPlayerInfo
import com.moare.android.features.search.models.models.nba.NBAPlayerStats
import kotlinx.serialization.Serializable

@Serializable
data class NBAPlayerStatsDisplayModel(
    override val leagueId: Int,
    val player: NBAPlayerInfo,
    val stats: List<NBAPlayerStats>
) : LeagueIdentifiable