package com.moare.android.features.search.display.common.container.state

import com.moare.android.features.search.models.models.common.GameForSchedule

data class TournamentBracketContainerState<T>(
    val leagueId: Int,
    val teamNameDic: Map<String, String>,
    val gameListTuple: List<GameListEntry<T>>,
    val isConference: Boolean,
    val isSeries: Boolean
)

data class TournamentDrawContainerState<T>(
    val leagueId: Int,
    val teamNameDic: Map<String, String>,
    val gameListTuple: List<GameListEntry<T>>,
    val isSeries: Boolean
)

data class GameListEntry<T>(
    val title: String,
    val gameList: List<List<GameForSchedule<T>>>
)