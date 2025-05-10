package com.moare.android.features.search.models.displaymodels.kbo

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.DisplayModelBase
import com.moare.android.features.search.models.models.kbo.KBOGame
import com.moare.android.features.search.models.models.kbo.KBOTeamInfo
import com.moare.android.features.search.models.models.kbo.KBOTeamStats
import com.moare.android.features.search.models.models.kbo.KBOVenue
import kotlinx.serialization.Serializable

@Serializable
data class KBOTeamInfoDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val team: KBOTeamInfo,
    val venue: KBOVenue,
    val stats: KBOTeamStats?,
    val lastGame: KBOGame?,
    val nextGame: KBOGame?
) : DisplayModelBase
