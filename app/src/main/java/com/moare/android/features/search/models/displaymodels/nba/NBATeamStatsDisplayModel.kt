package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.nba.NBATeamInfo
import com.moare.android.features.search.models.models.nba.NBATeamStats
import com.moare.android.features.search.models.models.nba.NBAVenue
import kotlinx.serialization.Serializable

@Serializable
data class NBATeamStatsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val team: NBATeamInfo,
    val venue: NBAVenue,
    val stats: List<NBATeamStats>,
) : SportDisplayModel