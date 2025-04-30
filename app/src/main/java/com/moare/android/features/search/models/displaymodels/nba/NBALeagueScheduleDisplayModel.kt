package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.displaymodels.LeagueIdentifiable
import com.moare.android.features.search.models.models.nba.NBAGame
import kotlinx.serialization.Serializable

@Serializable
data class NBALeagueScheduleDisplayModel(
    override val leagueId: Int,
    val yearMonthList: List<String>,
    var games: List<NBAGame>,
    val entityInfo: List<EntityInfo>
) : LeagueIdentifiable