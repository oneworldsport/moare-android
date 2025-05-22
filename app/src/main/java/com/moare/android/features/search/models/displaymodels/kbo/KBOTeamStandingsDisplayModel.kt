package com.moare.android.features.search.models.displaymodels.kbo

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.DisplayModelBase
import com.moare.android.features.search.models.models.kbo.KBOTeamInfo
import com.moare.android.features.search.models.models.kbo.KBOTeamStats
import kotlinx.serialization.Serializable

@Serializable
data class KBOTeamStandingsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val standings: List<KBOTeamStandingsDisplay>
) : DisplayModelBase

@Serializable
data class KBOTeamStandingsDisplay(
    val team: KBOTeamInfo,
    val stats: KBOTeamStats
)