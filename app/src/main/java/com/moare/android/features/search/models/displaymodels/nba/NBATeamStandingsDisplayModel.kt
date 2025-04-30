package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.LeagueIdentifiable
import com.moare.android.features.search.models.models.nba.NBATeam
import com.moare.android.features.search.models.models.nba.NBATeamInfo
import com.moare.android.features.search.models.models.nba.NBATeamStats
import kotlinx.serialization.Serializable

@Serializable
data class NBATeamStandingsDisplayModel(
    override val leagueId: Int,
    val keywords: List<Keyword>,
    val entityInfo: List<EntityInfo>,
    val standings: List<NBATeamStandingsDisplay>
) : LeagueIdentifiable

@Serializable
data class NBATeamStandingsDisplay(
    val team: NBATeamInfo,
    val stats: NBATeamStats,
)