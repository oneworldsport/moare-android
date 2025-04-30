package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.DisplayModelBase
import com.moare.android.features.search.models.models.nba.NBABoxScoreTeamPlayer
import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBAPlayerInfo
import com.moare.android.features.search.models.models.nba.NBAPlayerStats
import kotlinx.serialization.Serializable

@Serializable
data class NBAPlayerInfoDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val info: NBAPlayerInfo,
    val stats: NBAPlayerStats?,
    val lastGame: NBAGame?,
    val lastGamePlayerStats: NBABoxScoreTeamPlayer?,
    val nextGame: NBAGame?
) : DisplayModelBase