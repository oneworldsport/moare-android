package com.moare.android.features.search.models.displaymodels.kbo

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.DisplayModelBase
import com.moare.android.features.search.models.models.kbo.KBOGame
import com.moare.android.features.search.models.models.kbo.KBOGameHitterStats
import com.moare.android.features.search.models.models.kbo.KBOGamePitcherStats
import com.moare.android.features.search.models.models.kbo.KBOPlayerHitterStats
import com.moare.android.features.search.models.models.kbo.KBOPlayerInfo
import com.moare.android.features.search.models.models.kbo.KBOPlayerStats
import kotlinx.serialization.Serializable

@Serializable
data class KBOPlayerInfoDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword>,
    override val entityInfo: List<EntityInfo>,
    val info: KBOPlayerInfo,
    val stats: KBOPlayerStats?,
    val lastGame: KBOGame?,
    val lastGamePlayerHitterStats: KBOGameHitterStats?,
    val lastGamePlayerPitcherStats: KBOGamePitcherStats?,
    val nextGame: KBOGame?
) : DisplayModelBase
