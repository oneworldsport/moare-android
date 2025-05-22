package com.moare.android.features.search.models.displaymodels.mlb

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.DisplayModelBase
import com.moare.android.features.search.models.models.mlb.MLBGame
import com.moare.android.features.search.models.models.mlb.MLBGameBoxscoreTeamPlayer
import com.moare.android.features.search.models.models.mlb.MLBPlayerInfo
import com.moare.android.features.search.models.models.mlb.MLBPlayerStats
import kotlinx.serialization.Serializable

@Serializable
data class MLBPlayerInfoDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val info: MLBPlayerInfo,
    val teamId: Int?,
    val stats: MLBPlayerStats?,
    val lastGame: MLBGame?,
    val lastGamePlayerStats: MLBGameBoxscoreTeamPlayer?,
    val nextGame: MLBGame?
) : DisplayModelBase
