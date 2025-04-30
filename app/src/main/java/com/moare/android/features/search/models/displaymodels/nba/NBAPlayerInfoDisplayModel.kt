package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.displaymodels.LeagueIdentifiable
import com.moare.android.features.search.models.models.nba.NBABoxScoreTeamPlayer
import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBAPlayerInfo
import com.moare.android.features.search.models.models.nba.NBAPlayerStats
import kotlinx.serialization.Serializable

@Serializable
data class NBAPlayerInfoDisplayModel(
    override val leagueId: Int,
    val info: NBAPlayerInfo,
    val stats: NBAPlayerStats?,
    val lastGame: NBAGame?,
    val lastGamePlayerStats: NBABoxScoreTeamPlayer?,
    val nextGame: NBAGame?
) : LeagueIdentifiable