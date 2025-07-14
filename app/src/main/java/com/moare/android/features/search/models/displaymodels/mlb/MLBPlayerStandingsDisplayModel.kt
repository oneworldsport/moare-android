package com.moare.android.features.search.models.displaymodels.mlb

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBPlayerInfo
import com.moare.android.features.search.models.models.mlb.MLBPlayerStats
import kotlinx.serialization.Serializable

@Serializable
data class MLBPlayerStandingsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    override val season: Int,
    val standings: List<MLBPlayerStandingsDisplay>
) : SportDisplayModel

@Serializable
data class MLBPlayerStandingsDisplay(
    val player: MLBPlayerInfo,
    val stats: MLBPlayerStats
)