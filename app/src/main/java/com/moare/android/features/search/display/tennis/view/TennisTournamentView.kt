package com.moare.android.features.search.display.tennis.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.moare.android.features.search.display.common.container.state.TournamentBracketContainerState
import com.moare.android.features.search.display.common.container.state.TournamentContainerAction
import com.moare.android.features.search.display.common.container.view.TournamentBracketViewContainer
import com.moare.android.features.search.display.nba.store.NBATournamentAction
import com.moare.android.features.search.display.nba.store.NBATournamentStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.display.tennis.store.TennisTournamentStore

@Composable
fun TennisTournamentView(
    searchStore: SearchStore,
    store: TennisTournamentStore
) {
}