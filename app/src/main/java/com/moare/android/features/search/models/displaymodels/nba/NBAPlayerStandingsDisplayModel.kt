package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.models.nba.NBAPlayer
import com.moare.android.features.search.models.models.nba.NBAPlayerStatistic
import kotlinx.serialization.Serializable

@Serializable
data class NBAPlayerStandingsDisplayModel(
    val keywords: List<String>,
    val playerList: List<NBAPlayerStandingsDisplay>
)

@Serializable
data class NBAPlayerStandingsDisplay(
    val player: NBAPlayer?,
    val stats: NBAPlayerStatistic?
)