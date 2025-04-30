package com.moare.android.features.search.models.displaymodels.football

import com.moare.android.features.search.models.displaymodels.LeagueIdentifiable
import com.moare.android.features.search.models.models.football.FBPlayerInfo
import com.moare.android.features.search.models.models.football.FBPlayerStats
import com.moare.android.features.search.models.models.football.FBTeamInfo
import kotlinx.serialization.Serializable

@Serializable
data class FBPlayerStatsDisplayModel(
    override val leagueId: Int,
    val player: FBPlayerInfo,
    val team: FBTeamInfo?,
    val stats: List<FBPlayerStats>
) : LeagueIdentifiable
