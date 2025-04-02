package com.moare.android.features.search.models.responsemodels.nba

import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBATeam
import kotlinx.serialization.Serializable

@Serializable
data class NBATeamInfoResponseModel(
    val info: NBATeam? = null,
    val lastGame: NBAGame? = null,
    val nextGame: NBAGame? = null
)