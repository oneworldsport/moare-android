package com.moare.android.features.search.models.displaymodels.kbo

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOGame
import com.moare.android.features.search.models.models.kbo.KBOGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import kotlinx.serialization.Serializable

@Serializable
data class KBOLeagueScheduleDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val scheduleType: ScheduleType,
    val yearMonthList: List<String>,
    var games: List<KBOGameForSchedule>
) : SportDisplayModel
