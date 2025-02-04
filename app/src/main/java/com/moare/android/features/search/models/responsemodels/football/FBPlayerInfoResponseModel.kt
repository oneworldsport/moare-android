package com.moare.android.features.search.models.responsemodels.football

import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.models.models.football.FBPlayer
import kotlinx.serialization.Serializable

@Serializable
data class FBPlayerInfoResponseModel(
    val info: FBPlayer? = null,
    val lastGame: FBGame? = null,
    val nextGame: FBGame? = null
)
