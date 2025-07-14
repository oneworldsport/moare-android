package com.moare.android.features.search.models.displaymodels.kbo

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOGameForSchedule
import kotlinx.serialization.Serializable

@Serializable
data class KBOTeamScheduleDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    override val season: Int,
    var games: List<KBOGameForSchedule>
) : SportDisplayModel
