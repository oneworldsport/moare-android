package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.Rankable
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.nba.NBATeamInfo
import com.moare.android.features.search.models.models.nba.NBATeamStats
import kotlinx.serialization.Serializable

@Serializable
data class NBATeamStandingsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    override val season: Int,
    val standings: List<NBATeamStandingsDisplay>
) : SportDisplayModel

@Serializable
data class NBATeamStandingsDisplay(
    val team: NBATeamInfo,
    val stats: NBATeamStats,
    override var displayRank: Int = 0 // 화면에서 순위 표시에 쓰이는 값
) : Rankable