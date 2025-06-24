package com.moare.android.features.search.models.displaymodels.kbo

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOPlayerInfo
import com.moare.android.features.search.models.models.kbo.KBOPlayerStats
import kotlinx.serialization.Serializable

@Serializable
data class KBOPlayerStandingsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val standings: List<KBOPlayerStandingsDisplay>
) : SportDisplayModel

@Serializable
data class KBOPlayerStandingsDisplay(
    val player: KBOPlayerInfo,
    val stats: KBOPlayerStats
)