package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.models.nba.NBAPlayerInfo
import com.moare.android.features.search.models.models.nba.NBAPlayerStats
import kotlinx.serialization.Serializable

@Serializable
data class NBAPlayerStatsDisplayModel(
    val player: NBAPlayerInfo,
    val stats: List<NBAPlayerStats>
)