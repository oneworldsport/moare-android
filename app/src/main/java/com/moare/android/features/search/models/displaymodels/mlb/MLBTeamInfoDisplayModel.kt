package com.moare.android.features.search.models.displaymodels.mlb

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.DisplayModelBase
import com.moare.android.features.search.models.models.kbo.KBOGame
import com.moare.android.features.search.models.models.kbo.KBOTeamInfo
import com.moare.android.features.search.models.models.kbo.KBOTeamStats
import com.moare.android.features.search.models.models.kbo.KBOTeamVenue
import com.moare.android.features.search.models.models.mlb.MLBGame
import com.moare.android.features.search.models.models.mlb.MLBNameObj
import com.moare.android.features.search.models.models.mlb.MLBPlayerInfo
import com.moare.android.features.search.models.models.mlb.MLBPlayerStats
import com.moare.android.features.search.models.models.mlb.MLBTeamInfo
import com.moare.android.features.search.models.models.mlb.MLBTeamStats
import kotlinx.serialization.Serializable

@Serializable
data class MLBTeamInfoDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val team: MLBTeamInfo,
    val venue: MLBNameObj,
    val stats: MLBTeamStats?,
    val lastGame: MLBGame?,
    val nextGame: MLBGame?
) : DisplayModelBase
