package com.moare.android.features.search.models.displaymodels.tennis

import com.moare.android.core.constants.StringConstants
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.football.FBGameForSchedule
import com.moare.android.features.search.models.models.tennis.TennisGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import kotlinx.serialization.Serializable

@Serializable
data class TennisLeagueScheduleDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    override val season: Int,
    val scheduleType: ScheduleType,
    val yearMonthList: List<String>,
    val startDate: String? = null,
    val endDate: String? = null,
    val relatedLeagueIds: List<Int>? = null,
    var games: List<TennisGameForSchedule>
) : SportDisplayModel {
    val sortedRelatedLeagues: List<Int>?
        get() = relatedLeagueIds?.sortedBy {
            StringConstants.Tennis.relatedLeagueRank(leagueId = it)
        }
    val relatedLeagueKrname: List<String>
        get() = (sortedRelatedLeagues ?: emptyList())
            .mapNotNull { leagueId ->
                StringConstants.Tennis.relatedLeaguesKrName(leagueId = leagueId)
            }
}