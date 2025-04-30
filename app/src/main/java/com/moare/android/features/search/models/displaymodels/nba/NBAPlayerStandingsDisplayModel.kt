package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.LeagueIdentifiable
import com.moare.android.features.search.models.models.nba.NBAPlayerInfo
import com.moare.android.features.search.models.models.nba.NBAPlayerStats
import kotlinx.serialization.Serializable

@Serializable
data class NBAPlayerStandingsDisplayModel(
    override val leagueId: Int,
    val keywords: List<Keyword>,
    val entityInfo: List<EntityInfo>,
    val standings: List<NBAPlayerStandingsDisplay>
) : LeagueIdentifiable

@Serializable
data class NBAPlayerStandingsDisplay(
    val player: NBAPlayerInfo,
    val stats: NBAPlayerStats
)