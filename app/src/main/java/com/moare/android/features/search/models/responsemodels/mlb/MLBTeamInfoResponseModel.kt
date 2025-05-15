package com.moare.android.features.search.models.responsemodels.mlb

import com.moare.android.features.search.models.models.mlb.MLBGame
import com.moare.android.features.search.models.models.mlb.MLBPlayer
import com.moare.android.features.search.models.models.mlb.MLBTeam
import kotlinx.serialization.Serializable

@Serializable
data class MLBTeamInfoResponseModel(
    val info: MLBTeam? = null,
    val lastGame: MLBGame? = null,
    val nextGame: MLBGame? = null
)
