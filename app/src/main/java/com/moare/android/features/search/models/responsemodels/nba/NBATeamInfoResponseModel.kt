package com.moare.android.features.search.models.responsemodels.nba

import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBATeamInfo
import kotlinx.serialization.Serializable

@Serializable
data class NBATeamInfoResponseModel(
    val info: NBATeamInfo,
    val lastGame: NBAGame,
    val nextGame: NBAGame
)