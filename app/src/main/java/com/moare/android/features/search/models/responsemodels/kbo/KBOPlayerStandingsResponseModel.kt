package com.moare.android.features.search.models.responsemodels.kbo

import com.moare.android.features.search.models.models.kbo.KBOPlayer
import kotlinx.serialization.Serializable

@Serializable
data class KBOPlayerStandingsResponseModel(
    val standings: List<KBOPlayer> = emptyList()
)
