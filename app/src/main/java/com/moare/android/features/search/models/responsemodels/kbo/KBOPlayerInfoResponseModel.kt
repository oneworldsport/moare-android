package com.moare.android.features.search.models.responsemodels.kbo

import com.moare.android.features.search.models.models.kbo.KBOGame
import com.moare.android.features.search.models.models.kbo.KBOPlayer
import kotlinx.serialization.Serializable

@Serializable
data class KBOPlayerInfoResponseModel(
    val info: KBOPlayer? = null,
    val lastGame: KBOGame? = null,
    val nextGame: KBOGame? = null
)
