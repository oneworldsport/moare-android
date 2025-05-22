package com.moare.android.features.search.models.responsemodels.kbo

import com.moare.android.features.search.models.models.kbo.KBOTeam
import kotlinx.serialization.Serializable

@Serializable
data class KBOTeamStandingsResponseModel(
    val standings: List<KBOTeam> = emptyList()
)
