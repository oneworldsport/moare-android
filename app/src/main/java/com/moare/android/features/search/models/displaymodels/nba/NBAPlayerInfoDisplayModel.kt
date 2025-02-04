package com.moare.android.features.search.models.displaymodels.nba

import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBAGamePlayerStats
import com.moare.android.features.search.models.models.nba.NBAPlayer
import com.moare.android.features.search.models.models.nba.NBAPlayerStatistic
import kotlinx.serialization.Serializable

@Serializable
data class NBAPlayerInfoDisplayModel(
    val player: NBAPlayer?,
    // TODO: 모든 게임의 기록을 합친 데이터를 가지고 있는 DisplayModel 이 필요함,
    val stats: NBAPlayerStatistic?,
    val lastGame: NBAGame?,
    val lastGamePlayerStats: NBAGamePlayerStats?,
    val nextGame: NBAGame?
)