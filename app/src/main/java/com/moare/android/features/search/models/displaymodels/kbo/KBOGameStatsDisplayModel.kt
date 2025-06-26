package com.moare.android.features.search.models.displaymodels.kbo

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOGame
import kotlinx.serialization.Serializable

@Serializable
data class KBOGameStatsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword> = emptyList(),
    override val entityInfo: List<EntityInfo> = emptyList(),
    val game: KBOGame
) : SportDisplayModel
