package com.moare.android.features.search.models.responsemodels.football

import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.models.models.football.FBTeam
import kotlinx.serialization.Serializable

@Serializable
data class FBTeamInfoResponseModel(
    val info: FBTeam? = null,
    val lastGame: FBGame? = null,
    val nextGame: FBGame? = null
)
