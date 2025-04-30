package com.moare.android.features.search.models.displaymodels.football

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.LeagueIdentifiable
import com.moare.android.features.search.models.models.football.FBPlayerInfo
import com.moare.android.features.search.models.models.football.FBPlayerStats
import kotlinx.serialization.Serializable

@Serializable
data class FBPlayerStandingsDisplayModel(
    override val leagueId: Int,
    val keywords: List<Keyword>,
    val entityInfo: List<EntityInfo>,
    var standings: List<FBPlayerStandingsDisplay>
) : LeagueIdentifiable

@Serializable
data class FBPlayerStandingsDisplay(
    val player: FBPlayerInfo,
    val stats: FBPlayerStats
)
