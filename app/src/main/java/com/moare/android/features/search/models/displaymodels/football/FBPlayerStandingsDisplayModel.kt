package com.moare.android.features.search.models.displaymodels.football

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.football.FBPlayerInfo
import com.moare.android.features.search.models.models.football.FBPlayerStats
import kotlinx.serialization.Serializable

@Serializable
data class FBPlayerStandingsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    var standings: List<FBPlayerStandingsDisplay>
) : SportDisplayModel

@Serializable
data class FBPlayerStandingsDisplay(
    val player: FBPlayerInfo,
    val stats: FBPlayerStats
)
