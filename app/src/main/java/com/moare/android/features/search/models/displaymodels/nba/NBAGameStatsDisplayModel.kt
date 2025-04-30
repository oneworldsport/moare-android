package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.DisplayModelBase
import com.moare.android.features.search.models.models.nba.NBAGame
import kotlinx.serialization.Serializable

@Serializable
data class NBAGameStatsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val game: NBAGame
) : DisplayModelBase