package com.moare.android.features.search.models.displaymodels.football

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.football.FBGame
import kotlinx.serialization.Serializable

@Serializable
data class FBGameStatsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword> = emptyList(),
    override val entityInfo: List<EntityInfo> = emptyList(),
    override val season: Int,
    val game: FBGame
) : SportDisplayModel
