package com.moare.android.features.search.models.responsemodels.nba

import com.moare.android.features.search.models.models.nba.NBATeamInfo
import kotlinx.serialization.Serializable

@Serializable
data class NBATeamStandingsResponseModel(
    val teamList: List<NBATeamInfo>
)