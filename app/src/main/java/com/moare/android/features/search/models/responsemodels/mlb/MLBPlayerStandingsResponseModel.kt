package com.moare.android.features.search.models.responsemodels.mlb

import com.moare.android.features.search.models.models.kbo.KBOPlayer
import com.moare.android.features.search.models.models.mlb.MLBGame
import com.moare.android.features.search.models.models.mlb.MLBPlayer
import kotlinx.serialization.Serializable

@Serializable
data class MLBPlayerStandingsResponseModel(
    val standings: List<MLBPlayer> = emptyList()
)
