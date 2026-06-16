package com.moare.android.features.search.models.displaymodels.football

import com.moare.android.core.constants.Constants
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.Rankable
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.football.FBLeague
import com.moare.android.features.search.models.models.football.FBTeamInfo
import com.moare.android.features.search.models.models.football.FBHomeAwayIntStats
import com.moare.android.features.search.models.models.football.FBTeamStatsFixtures
import kotlinx.serialization.Serializable

@Serializable
data class FBTeamStandingsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    override val season: Int,
    val league: FBLeague?,
    val standings: List<FBTeamStandingsDisplay>
) : SportDisplayModel {
    val groupStandings: Map<String, List<FBTeamStandingsDisplay>>
        get() = if (leagueId == Constants.Ids.WORLD_CUP) {
            standings.groupBy { standing ->
                standing.group.replace("Group ", "")
            }
        } else {
            emptyMap()
        }
}

@Serializable
data class FBTeamStandingsDisplay(
    val team: FBTeamInfo,
    val homeAwayStats: FBTeamStatsFixtures,
    val goalsFor: FBHomeAwayIntStats,
    val goalsAgainst: FBHomeAwayIntStats,
    val rank: Int,
    val points: Int,
    val group: String,
    override val displayRank: Int = 0 // 화면에서 순위 표시에 쓰이는 값
) : Rankable<FBTeamStandingsDisplay> {
    override fun withDisplayRank(rank: Int): FBTeamStandingsDisplay {
        return copy(displayRank = rank)
    }
}