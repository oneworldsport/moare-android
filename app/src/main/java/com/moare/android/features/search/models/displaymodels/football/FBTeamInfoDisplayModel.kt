package com.moare.android.features.search.models.displaymodels.football

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.DisplayModelBase
import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.models.models.football.FBTeamInfo
import com.moare.android.features.search.models.models.football.FBVenue
import com.moare.android.features.search.models.models.football.FBTeamStats
import kotlinx.serialization.Serializable

@Serializable
data class FBTeamInfoDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val team: FBTeamInfo,
    val venue: FBVenue,
    val stats: FBTeamStats?,
    val lastGame: FBGame?,
    val nextGame: FBGame?
) : DisplayModelBase
