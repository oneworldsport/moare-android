package com.moare.android.features.search.models.responsemodels.nba

import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBAPlayerInfo
import kotlinx.serialization.Serializable

@Serializable
data class NBAPlayerInfoResponseModel(
    val info: NBAPlayerInfo?,
    val lastGame: NBAGame? = null,
    val nextGame: NBAGame? = null
)