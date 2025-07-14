package com.moare.android.features.search.models.displaymodels.kbo

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOTeamInfo
import com.moare.android.features.search.models.models.kbo.KBOTeamStats
import kotlinx.serialization.Serializable

@Serializable
data class KBOTeamStandingsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    override val season: Int,
    val standings: List<KBOTeamStandingsDisplay>
) : SportDisplayModel

@Serializable
data class KBOTeamStandingsDisplay(
    val team: KBOTeamInfo,
    val stats: KBOTeamStats
)