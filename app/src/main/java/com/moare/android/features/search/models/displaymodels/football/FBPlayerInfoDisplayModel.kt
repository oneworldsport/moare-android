package com.moare.android.features.search.models.displaymodels.football

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.models.models.football.FBGamePlayerStatsDetail
import com.moare.android.features.search.models.models.football.FBPlayerInfo
import com.moare.android.features.search.models.models.football.FBPlayerStats
import kotlinx.serialization.Serializable

@Serializable
data class FBPlayerInfoDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val info: FBPlayerInfo,
    val stats: FBPlayerStats?,
    val lastGame: FBGame?,
    val lastGamePlayerStats: FBGamePlayerStatsDetail?,
    val nextGame: FBGame?
) : SportDisplayModel
