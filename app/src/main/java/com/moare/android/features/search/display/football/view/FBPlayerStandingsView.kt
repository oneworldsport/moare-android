package com.moare.android.features.search.display.football.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.dropFirstWord
import com.moare.android.core.util.rounded
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.display.common.container.state.StandingsHighlightItemState
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.StandingsViewContainer
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStandingsIntent
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStandingsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplayModel
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.util.convertDpToPx
import kotlinx.coroutines.delay

@Composable
fun FBPlayerStandingsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbPlayerStandingsViewModel: FBPlayerStandingsViewModel = hiltViewModel(),
    data: FBPlayerStandingsDisplayModel
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
    val displayModel by fbPlayerStandingsViewModel.displayModel.collectAsState()
    val displayDataState by fbPlayerStandingsViewModel.displayDataState.collectAsState()
    val firstSelectedIndex by fbPlayerStandingsViewModel.firstSelectedIndex.collectAsState()
    val secondCategorySelectedIndex by fbPlayerStandingsViewModel.secondCategorySelectedIndex.collectAsState()
    val filteredStandings by fbPlayerStandingsViewModel.filteredStandings.collectAsState()
    val entityIndex by fbPlayerStandingsViewModel.entityIndex.collectAsState()
    val filteredStandingsStartIndex by fbPlayerStandingsViewModel.filteredStandingsStartIndex.collectAsState()
    val playerNameDic = fbPlayerStandingsViewModel.playerNameDictionary
    val teamNameDic = fbPlayerStandingsViewModel.teamNameDictionary

    val league = displayModel?.standings?.first()?.stats?.league

    val poppedView by searchViewModel.poppedView.collectAsState()

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

    val previousScrollPosition = convertDpToPx(fbPlayerStandingsViewModel.dataItemHeight * 10).toInt()
    val firstScrollPosition = convertDpToPx(fbPlayerStandingsViewModel.dataItemHeight).toInt()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.FBPlayerStandings) {
            fbPlayerStandingsViewModel.send(FBPlayerStandingsIntent.InitData(data))
        }
    }

    LaunchedEffect(verticalScrollState.value) {
        // prevent executing at first open
        if (isFirstOpen) {
            isFirstOpen = false
            return@LaunchedEffect
        }

        when (verticalScrollState.value) {
            0 -> {
                fbPlayerStandingsViewModel.send(FBPlayerStandingsIntent.ShowMoreStandings(true))
            }
            verticalScrollState.maxValue -> {
                fbPlayerStandingsViewModel.send(FBPlayerStandingsIntent.ShowMoreStandings(false))
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
            secondCategorySelectedIndex = secondCategorySelectedIndex,
            highlightState = StandingsHighlightItemState(
                itemIndex = entityIndex,
                standingsStartIndex = filteredStandingsStartIndex
            ),
            displayDataState = displayDataState,
            columnWidthList = columnWidthList
        ),
        actions = StandingsContainerActions(
            secondCategoryButtonAction = { index, category ->
                fbPlayerStandingsViewModel.send(FBPlayerStandingsIntent.SelectSecondCategory(index, category))
            },
            itemButtonAction = { id ->
                searchViewModel.send(SearchViewModel.Intent.ShowPlayerStats(season = displayModel?.season, category = "football", playerId = id))
            }
        ),
        verticalScrollState = verticalScrollState,
        titleContent = {
            league?.let {
                LeagueTitle(
                    url = league.logo,
                    leagueName = league.name,
                    leagueSeason = league.season,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    )
}