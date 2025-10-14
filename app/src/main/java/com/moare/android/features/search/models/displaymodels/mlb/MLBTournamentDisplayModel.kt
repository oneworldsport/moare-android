package com.moare.android.features.search.models.displaymodels.mlb

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import kotlinx.serialization.Serializable

@Serializable
data class MLBTournamentDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    override val season: Int,
    val scheduleType: ScheduleType,
    val games: List<MLBGameForSchedule>
) : SportDisplayModel