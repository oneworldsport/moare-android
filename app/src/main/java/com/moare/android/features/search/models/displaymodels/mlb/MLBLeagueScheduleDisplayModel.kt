package com.moare.android.features.search.models.displaymodels.mlb

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBGame
import com.moare.android.features.search.models.models.mlb.MLBGameForSchedule
import kotlinx.serialization.Serializable

@Serializable
data class MLBLeagueScheduleDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    override val season: Int,
    val yearMonthList: List<String>,
    var games: List<MLBGameForSchedule>
) : SportDisplayModel
