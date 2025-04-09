package com.moare.android.features.search.models.responsemodels.nba

import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBAPlayer
import kotlinx.serialization.Serializable

@Serializable
data class NBAPlayerInfoResponseModel(
    val info: NBAPlayer? = null,
    val lastGame: NBAGame? = null,
    val nextGame: NBAGame? = null
)