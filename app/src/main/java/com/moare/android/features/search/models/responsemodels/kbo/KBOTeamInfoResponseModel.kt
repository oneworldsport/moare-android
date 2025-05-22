package com.moare.android.features.search.models.responsemodels.kbo

import com.moare.android.features.search.models.models.kbo.KBOGame
import com.moare.android.features.search.models.models.kbo.KBOTeam
import kotlinx.serialization.Serializable

@Serializable
data class KBOTeamInfoResponseModel(
    val info: KBOTeam? = null,
    val lastGame: KBOGame? = null,
    val nextGame: KBOGame? = null
)
