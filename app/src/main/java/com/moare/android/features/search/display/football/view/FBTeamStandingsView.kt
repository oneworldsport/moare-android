package com.moare.android.features.search.display.football.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.constants.StringConstants
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.StandingsViewContainer
import com.moare.android.features.search.display.football.viewmodel.FBTeamStandingsIntent
import com.moare.android.features.search.display.football.viewmodel.FBTeamStandingsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplayModel
import com.moare.android.ui.common.components.LeagueTitle

@Composable
fun FBTeamStandingsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbTeamStandingsViewModel: FBTeamStandingsViewModel = hiltViewModel(),
    data: FBTeamStandingsDisplayModel
) {
    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by fbTeamStandingsViewModel.displayModel.collectAsState()
    val selectedCategoryIndex by fbTeamStandingsViewModel.selectedCategoryIndex.collectAsState()
    val standings by fbTeamStandingsViewModel.standings.collectAsState()
    val teamNameDic = fbTeamStandingsViewModel.teamNameDictionary

    val league = displayModel?.league

    val poppedView by searchViewModel.poppedView.collectAsState()

    val teamStandings: List<StandingsItemState> = standings.map {
        StandingsItemState(
            id = it.team.id,
            imageUrl = it.team.logo,
            name = teamNameDic["short_${it.team.id}"] ?: it.team.name,
            dataList = listOf(
                fbTeamStandingsViewModel.calculatePoints(it.homeAwayStats).toString(),
                it.homeAwayStats.wins.total.toString(),
                it.homeAwayStats.draws.total.toString(),
                it.homeAwayStats.loses.total.toString(),
                it.homeAwayStats.played.total.toString(),
                it.goalsFor.total.toString(),
                it.goalsAgainst.total.toString(),
                (it.goalsFor.total - it.goalsAgainst.total).toString(),
                fbTeamStandingsViewModel.getRecordString(it.homeAwayStats),
                fbTeamStandingsViewModel.getRecordString(it.homeAwayStats, false)
            )
        )
    }
    val columnWidthList = listOf(50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 100.dp, 100.dp)

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.FBTeamStandings) {
            fbTeamStandingsViewModel.send(FBTeamStandingsIntent.InitData(data))
        }
    }

    StandingsViewContainer(
        state = NewStandingsContainerState(
            secondCategories = StringConstants.Football.TEAM_STANDINGS_CATEGORIES,
            standings = teamStandings,
            secondCategorySelectedIndex = selectedCategoryIndex,
            columnWidthList = columnWidthList
        ),
        actions = StandingsContainerActions(
            secondCategoryButtonAction = { index, _ ->
                fbTeamStandingsViewModel.send(FBTeamStandingsIntent.SelectCategory(index))
            },
            itemButtonAction = { id ->
                searchViewModel.send(SearchViewModel.Intent.ShowTeamStats(teamId = id))
            }
        ),
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
























