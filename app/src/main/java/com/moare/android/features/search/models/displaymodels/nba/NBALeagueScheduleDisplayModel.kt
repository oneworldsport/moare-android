package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.models.nba.NBAGame
import kotlinx.serialization.Serializable

@Serializable
data class NBALeagueScheduleDisplayModel(
    val yearMonthList: List<String>,
    var games: List<NBAGame>,
    val entityInfo: List<EntityInfo>,
)