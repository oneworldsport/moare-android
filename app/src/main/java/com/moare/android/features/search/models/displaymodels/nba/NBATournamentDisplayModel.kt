package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.nba.NBAGame
import kotlinx.serialization.Serializable

@Serializable
data class NBATournamentDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val yearMonthList: List<String>,
    var games: List<NBAGame>
) : SportDisplayModel
