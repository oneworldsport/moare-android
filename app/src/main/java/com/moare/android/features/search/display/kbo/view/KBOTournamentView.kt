package com.moare.android.features.search.display.kbo.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.moare.android.features.search.display.common.container.state.TournamentBracketContainerState
import com.moare.android.features.search.display.common.container.state.TournamentContainerAction
import com.moare.android.features.search.display.common.container.view.TournamentBracketViewContainer
import com.moare.android.features.search.display.kbo.viewmodel.KBOTournamentAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOTournamentStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBTournamentAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBTournamentStore
import com.moare.android.features.search.display.search.viewmodel.SearchStore

@Composable
fun KBOTournamentView(
    searchStore: SearchStore,
    store: KBOTournamentStore
) {
    val displayModel by store.displayModel.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()
    val gameListTuple by store.gameListTuple.collectAsState()
    val seedIdPairList by store.seedIdPairList.collectAsState()

    TournamentBracketViewContainer(
        state = TournamentBracketContainerState(
            leagueId = displayModel.leagueId,
            teamNameDic = teamNameDic,
            gameListTuple = gameListTuple,
            seedIdPairList = seedIdPairList,
            isConference = false,
            isSeries = true
        ),
        action = TournamentContainerAction(
            selectSeries = { gameList ->
                store.send(KBOTournamentAction.SelectSeries(gameList))
            }
        )
    )
}