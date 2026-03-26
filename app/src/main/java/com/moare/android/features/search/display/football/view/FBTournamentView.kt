package com.moare.android.features.search.display.football.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.moare.android.core.constants.Constants
import com.moare.android.features.search.display.common.container.state.TournamentBracketContainerState
import com.moare.android.features.search.display.common.container.state.TournamentContainerAction
import com.moare.android.features.search.display.common.container.state.TournamentDrawContainerState
import com.moare.android.features.search.display.common.container.view.TournamentBracketViewContainer
import com.moare.android.features.search.display.common.container.view.TournamentDrawViewContainer
import com.moare.android.features.search.display.football.store.FBTournamentAction
import com.moare.android.features.search.display.football.store.FBTournamentStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.models.responsemodels.football.ScheduleType

@Composable
fun FBTournamentView(
    searchStore: SearchStore,
    store: FBTournamentStore
) {
    val displayModel by store.displayModel.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()
    val gameListTuple by store.gameListTuple.collectAsState()

    val leagueId = displayModel.leagueId

    if (displayModel.scheduleType == ScheduleType.TOURNAMENT_BRACKET) {
        TournamentBracketViewContainer(
            state = TournamentBracketContainerState(
                leagueId = leagueId,
                teamNameDic = teamNameDic,
                gameListTuple = gameListTuple,
                isConference = leagueId == Constants.Ids.MLS,
                isSeries = leagueId != Constants.Ids.MLS
            ),
            action = TournamentContainerAction(
                selectSeries = { gameList ->
                    store.send(FBTournamentAction.SelectSeries(gameList))
                },
                selectGame = { game ->
                    store.send(FBTournamentAction.SelectGame(game))
                }
            )
        )
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