package com.moare.android.features.search.models.displaymodels.mlb

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.DisplayModelBase
import com.moare.android.features.search.models.models.mlb.MLBGame
import com.moare.android.features.search.models.models.mlb.MLBPlayerInfo
import com.moare.android.features.search.models.models.mlb.MLBPlayerStats
import com.moare.android.features.search.models.models.mlb.MLBTeamInfo
import com.moare.android.features.search.models.models.mlb.MLBTeamStats
import kotlinx.serialization.Serializable

@Serializable
data class MLBTeamStandingsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val standings: List<MLBTeamStandingsDisplay>
) : DisplayModelBase

@Serializable
data class MLBTeamStandingsDisplay(
    val team: MLBTeamInfo,
    val stats: MLBTeamStats
)
