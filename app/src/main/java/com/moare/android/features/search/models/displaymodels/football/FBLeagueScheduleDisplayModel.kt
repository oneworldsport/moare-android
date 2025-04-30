package com.moare.android.features.search.models.displaymodels.football

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.displaymodels.LeagueIdentifiable
import com.moare.android.features.search.models.models.football.FBGame
import kotlinx.serialization.Serializable

@Serializable
data class FBLeagueScheduleDisplayModel(
    override val leagueId: Int,
    val yearMonthList: List<String>,
    var games: List<FBGame>,
    val entityInfo: List<EntityInfo>
) : LeagueIdentifiable
