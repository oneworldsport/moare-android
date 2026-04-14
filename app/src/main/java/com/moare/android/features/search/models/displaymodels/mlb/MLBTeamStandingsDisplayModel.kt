package com.moare.android.features.search.models.displaymodels.mlb

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.Rankable
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBTeamInfo
import com.moare.android.features.search.models.models.mlb.MLBTeamStats
import kotlinx.serialization.Serializable

@Serializable
data class MLBTeamStandingsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    override val season: Int,
    val standings: List<MLBTeamStandingsDisplay>
) : SportDisplayModel

@Serializable
data class MLBTeamStandingsDisplay(
    val team: MLBTeamInfo,
    val stats: MLBTeamStats,
    override val displayRank: Int = 0 // 화면에서 순위 표시에 쓰이는 값
) : Rankable<MLBTeamStandingsDisplay> {
    override fun withDisplayRank(rank: Int): MLBTeamStandingsDisplay {
        return copy(displayRank = rank)
    }
}
