package com.moare.android.features.search.models.responsemodels.nba

import com.moare.android.features.search.models.models.nba.NBAPlayerInfo
import kotlinx.serialization.Serializable

@Serializable
data class NBAPlayerStandingsResponseModel(
    val playerList: List<NBAPlayerInfo>
)