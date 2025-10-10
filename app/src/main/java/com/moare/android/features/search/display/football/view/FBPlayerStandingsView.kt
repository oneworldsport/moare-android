package com.moare.android.features.search.display.football.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.dropFirstWord
import com.moare.android.core.util.rounded
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.display.common.container.state.StandingsHighlightItemState
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.StandingsViewContainer
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStandingsAction
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStandingsStore
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplayModel
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.util.convertDpToPx
import kotlinx.coroutines.delay

@Composable
fun FBPlayerStandingsView(
    searchStore: SearchViewModel,
    store: FBPlayerStandingsStore
) {
    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()
    var isFirstOpen by remember { mutableStateOf(true) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by store.displayModel.collectAsState()
    val displayDataState by store.displayDataState.collectAsState()
    val categorySelectedIndex by store.categorySelectedIndex.collectAsState()
    val filteredStandings by store.filteredStandings.collectAsState()
    val entityIndex by store.entityIndex.collectAsState()
    val filteredStandingsStartIndex by store.filteredStandingsStartIndex.collectAsState()
    val playerNameDic by store.playerNameDic.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val league = displayModel.standings.first().stats.league

    val playerStandings: List<StandingsItemState> = filteredStandings.map {
        val stats = it.stats
        StandingsItemState(
            id = it.player.id,
            imageUrl = it.player.photo,
            name = playerNameDic["${it.player.id}"]?.dropFirstWord() ?: it.player.name.dropFirstWord(),
            subName = teamNameDic["short_${stats.team.id}"] ?: stats.team.name,
            dataList = listOf(
                stats.goals.total.toString(),
                stats.goals.assists.toString(),
                (stats.goals.total + stats.goals.assists).toString(),
                stats.shots.total.toString(),
                stats.shots.on.toString(),
                stats.passes.key.toString(),
                stats.dribbles.success.toString(),
                stats.penalty.scored.toString(),
                stats.tackles.total.toString(),
                stats.duels.won.toString(),
                stats.passes.total.toString(),
                stats.fouls.committed.toString(),
                stats.cards.yellow.toString(),
                stats.cards.red.toString(),
                stats.games.appearences.toString(),
                stats.games.lineups.toString(),
                stats.substitutes.substituteIn.toString(),
                stats.games.minutes.toString(),
                (stats.games.rating.toDoubleOrNull()?.rounded(2) ?: 0.0).toString()
            )
        )
    }
    val columnWidthList = listOf(50.dp, 50.dp, 70.dp, 50.dp, 70.dp, 50.dp, 70.dp, 50.dp, 70.dp, 80.dp, 70.dp, 50.dp, 50.dp, 50.dp, 50.dp, 70.dp, 70.dp, 80.dp, 70.dp)

    val previousScrollPosition = convertDpToPx(store.dataItemHeight * 10).toInt()
    val firstScrollPosition = convertDpToPx(store.dataItemHeight).toInt()

    LaunchedEffect(verticalScrollState.value) {
        // prevent executing at first open
        if (isFirstOpen) {
            isFirstOpen = false
            return@LaunchedEffect
        }

        when (verticalScrollState.value) {
            0 -> {
                store.send(FBPlayerStandingsAction.ShowMoreStandings(true))
            }
            verticalScrollState.maxValue -> {
                store.send(FBPlayerStandingsAction.ShowMoreStandings(false))
            }
        }
    }

    LaunchedEffect(filteredStandings) {
        if (filteredStandings.size == 20) {
            verticalScrollState.scrollTo(firstScrollPosition)
        } else if (filteredStandings.size > 20 && verticalScrollState.value == 0) {
            delay(100)
            verticalScrollState.scrollTo(previousScrollPosition)
        }
    }

    StandingsViewContainer(
        state = NewStandingsContainerState(
            secondCategories = StringConstants.Football.PLAYER_STANDINGS_SECOND_CATEGORIES,
            standings = playerStandings,
            secondCategorySelectedIndex = categorySelectedIndex,
            highlightState = StandingsHighlightItemState(
                itemIndex = entityIndex,
                standingsStartIndex = filteredStandingsStartIndex
            ),
            displayDataState = displayDataState,
            columnWidthList = columnWidthList
        ),
        actions = StandingsContainerActions(
            secondCategoryButtonAction = { index, category ->
                store.send(FBPlayerStandingsAction.SelectCategory(index, category))
            },
            itemButtonAction = { id ->
                searchStore.send(SearchViewModel.Intent.ShowPlayerStats(season = displayModel.season, category = "football", playerId = id))
            }
        ),
        verticalScrollState = verticalScrollState,
        titleContent = {
            LeagueTitle(
                url = league.logo,
                leagueName = league.name,
                leagueSeason = league.season,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    )
}