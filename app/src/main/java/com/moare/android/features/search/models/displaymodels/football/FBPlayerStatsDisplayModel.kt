package com.moare.android.features.search.models.displaymodels.football

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.football.FBPlayerInfo
import com.moare.android.features.search.models.models.football.FBPlayerStats
import com.moare.android.features.search.models.models.football.FBTeamInfo
import kotlinx.serialization.Serializable

@Serializable
data class FBPlayerStatsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val player: FBPlayerInfo,
    val team: FBTeamInfo?,
    val stats: List<FBPlayerStats>
) : SportDisplayModel
