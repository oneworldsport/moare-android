package com.moare.android.features.search.models.displaymodels.football

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.football.FBLeague
import com.moare.android.features.search.models.models.football.FBTeamInfo
import com.moare.android.features.search.models.models.football.FBHomeAwayIntStats
import com.moare.android.features.search.models.models.football.FBTeamStatsFixtures
import kotlinx.serialization.Serializable

@Serializable
data class FBTeamStandingsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val league: FBLeague?,
    val standings: List<FBTeamStandingsDisplay>
) : SportDisplayModel

@Serializable
data class FBTeamStandingsDisplay(
    val team: FBTeamInfo,
    val homeAwayStats: FBTeamStatsFixtures,
    val goalsFor: FBHomeAwayIntStats,
    val goalsAgainst: FBHomeAwayIntStats
)