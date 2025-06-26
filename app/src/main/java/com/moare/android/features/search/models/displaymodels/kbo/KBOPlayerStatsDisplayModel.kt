package com.moare.android.features.search.models.displaymodels.kbo

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOPlayerInfo
import com.moare.android.features.search.models.models.kbo.KBOPlayerStats
import kotlinx.serialization.Serializable

@Serializable
data class KBOPlayerStatsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val player: KBOPlayerInfo,
    val stats: List<KBOPlayerStats>
) : SportDisplayModel
