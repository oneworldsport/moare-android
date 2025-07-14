package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.nba.NBAGame
import kotlinx.serialization.Serializable

@Serializable
data class NBAGameStatsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword> = emptyList(), // NOTE: default value is added for usage in SearchViewModel
    override val entityInfo: List<EntityInfo> = emptyList(),
    override val season: Int,
    val game: NBAGame
) : SportDisplayModel