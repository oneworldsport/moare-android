package com.moare.android.features.search.display.football.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.moare.android.features.search.display.common.container.state.TournamentDrawContainerState
import com.moare.android.features.search.display.common.container.view.TournamentDrawViewContainer
import com.moare.android.features.search.display.football.viewmodel.FBTournamentStore
import com.moare.android.features.search.display.search.viewmodel.SearchStore
import com.moare.android.features.search.models.responsemodels.football.ScheduleType

@Composable
fun FBTournamentView(
    searchStore: SearchStore,
    store: FBTournamentStore
) {
    val displayModel by store.displayModel.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()
    val gameListTuple by store.gameListTuple.collectAsState()

    if (displayModel.scheduleType == ScheduleType.TOURNAMENT_BRACKET) {
        // TODO: 축구에서 Bracket생기면 작업
    } else {
        TournamentDrawViewContainer(
            state = TournamentDrawContainerState(
                leagueId = displayModel.leagueId,
                teamNameDic = teamNameDic,
                gameListTuple = gameListTuple,
                isSeries = false
            )
        )
    }
}