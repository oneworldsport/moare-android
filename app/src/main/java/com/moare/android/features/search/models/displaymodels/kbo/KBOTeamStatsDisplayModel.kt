package com.moare.android.features.search.models.displaymodels.kbo

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOTeamInfo
import com.moare.android.features.search.models.models.kbo.KBOTeamStats
import com.moare.android.features.search.models.models.kbo.KBOTeamVenue
import kotlinx.serialization.Serializable

@Serializable
data class KBOTeamStatsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val team: KBOTeamInfo,
    val venue: KBOTeamVenue,
    val stats: List<KBOTeamStats>
) : SportDisplayModel