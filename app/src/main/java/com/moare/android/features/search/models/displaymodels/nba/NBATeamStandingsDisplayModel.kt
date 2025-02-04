package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.models.nba.NBATeamInfo
import kotlinx.serialization.Serializable

@Serializable
data class NBATeamStandingsDisplayModel(
    val teamList: List<NBATeamInfo>
)