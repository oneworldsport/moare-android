package com.moare.android.features.search.models.displaymodels.mlb

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBTeamInfo
import com.moare.android.features.search.models.models.mlb.MLBTeamStats
import kotlinx.serialization.Serializable

@Serializable
data class MLBTeamStandingsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    override val season: Int,
    val standings: List<MLBTeamStandingsDisplay>
) : SportDisplayModel

@Serializable
data class MLBTeamStandingsDisplay(
    val team: MLBTeamInfo,
    val stats: MLBTeamStats
)
