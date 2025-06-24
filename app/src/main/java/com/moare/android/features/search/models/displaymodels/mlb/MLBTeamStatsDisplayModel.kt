package com.moare.android.features.search.models.displaymodels.mlb

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBNameObj
import com.moare.android.features.search.models.models.mlb.MLBTeamInfo
import com.moare.android.features.search.models.models.mlb.MLBTeamStats
import kotlinx.serialization.Serializable

@Serializable
data class MLBTeamStatsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val team: MLBTeamInfo,
    val venue: MLBNameObj,
    val stats: List<MLBTeamStats>
) : SportDisplayModel
