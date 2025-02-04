package com.moare.android.features.search.models.displaymodels.football

import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.models.football.FBPlayerInfo
import com.moare.android.features.search.models.models.football.FBPlayerStats
import kotlinx.serialization.Serializable

@Serializable
data class FBPlayerStandingsDisplayModel(
    val keywords: List<Keyword>,
    var standings: List<FBPlayerStandingsDisplay>
)

@Serializable
data class FBPlayerStandingsDisplay(
    val player: FBPlayerInfo,
    val stats: FBPlayerStats
)
