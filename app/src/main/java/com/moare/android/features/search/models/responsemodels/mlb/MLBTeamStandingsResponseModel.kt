package com.moare.android.features.search.models.responsemodels.mlb

import com.moare.android.features.search.models.models.kbo.KBOTeam
import com.moare.android.features.search.models.models.mlb.MLBGame
import com.moare.android.features.search.models.models.mlb.MLBPlayer
import com.moare.android.features.search.models.models.mlb.MLBTeam
import kotlinx.serialization.Serializable

@Serializable
data class MLBTeamStandingsResponseModel(
    val standings: List<MLBTeam> = emptyList()
)
