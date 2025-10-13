package com.moare.android.features.search.models.displaymodels.football

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.football.FBGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import kotlinx.serialization.Serializable

@Serializable
data class FBTournamentDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    override val season: Int,
    val scheduleType: ScheduleType,
    val games: List<FBGameForSchedule>
) : SportDisplayModel