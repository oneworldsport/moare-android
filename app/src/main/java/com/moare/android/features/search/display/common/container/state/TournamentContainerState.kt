package com.moare.android.features.search.display.common.container.state

import com.moare.android.features.search.models.models.common.GameForSchedule

data class TournamentBracketContainerState<T>(
    val leagueId: Int,
    val teamNameDic: Map<String, String>,
    val gameListTuple: List<GameListEntry<T>>,
    // TODO: seedIdTupleList사용 안되므로 다른 사용하는곳 리팩토링 필요. DB에 isHomeTopSeed작업이 되어있어야함.
    var seedIdPairList: List<List<Pair<Int?, Int?>>> = listOf(), // Bracket에 한 시리즈 아이템에서 위에 표시되는 팀과 아래 표시되는 팀 id 정보. gameListTuple의 gameList와 동일한 인덱스에 일치하는 정보가 있음.
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
    val gameList: List<List<GameForSchedule<T>>?>
)

data class TournamentContainerAction<T>(
    val selectSeries: ((List<GameForSchedule<T>>) -> Unit)? = null,
    val selectGame: ((GameForSchedule<T>) -> Unit)? = null
)