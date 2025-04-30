package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.displaymodels.LeagueIdentifiable
import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBATeamInfo
import com.moare.android.features.search.models.models.nba.NBATeamStats
import com.moare.android.features.search.models.models.nba.NBAVenue
import kotlinx.serialization.Serializable

@Serializable
data class NBATeamInfoDisplayModel(
    override val leagueId: Int,
    val team: NBATeamInfo,
    val venue: NBAVenue,
    val stats: NBATeamStats?,
    val lastGame: NBAGame?,
    val nextGame: NBAGame?
) : LeagueIdentifiable