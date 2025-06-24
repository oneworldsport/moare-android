package com.moare.android.features.search.models.displaymodels.mlb

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBGame
import kotlinx.serialization.Serializable

@Serializable
data class MLBLeagueScheduleDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val yearMonthList: List<String>,
    var games: List<MLBGame>
) : SportDisplayModel
