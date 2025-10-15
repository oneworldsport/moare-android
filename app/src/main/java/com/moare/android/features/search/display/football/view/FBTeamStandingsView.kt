package com.moare.android.features.search.display.football.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moare.android.core.constants.StringConstants
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.StandingsViewContainer
import com.moare.android.features.search.display.football.viewmodel.FBTeamStandingsAction
import com.moare.android.features.search.display.football.viewmodel.FBTeamStandingsStore
import com.moare.android.features.search.display.search.viewmodel.SearchStore
import com.moare.android.ui.common.components.FBLeagueTitle

@Composable
fun FBTeamStandingsView(
    searchStore: SearchStore,
    store: FBTeamStandingsStore
) {
    val headerCategories = listOf("서부 컨퍼런스", "동부 컨퍼런스")

    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by store.displayModel.collectAsState()
    val isMLS by store.isMLS.collectAsState()
    val headerCategorySelectedIndex by store.headerCategorySelectedIndex.collectAsState()
    val selectedCategoryIndex by store.categorySelectedIndex.collectAsState()
    val standings by store.standings.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val league = displayModel.league

    val teamStandings: List<StandingsItemState> = standings.map {
        StandingsItemState(
            id = it.team.id,
            imageUrl = it.team.logo,
            name = teamNameDic["short_${it.team.id}"] ?: it.team.name,
            dataList = listOf(
                store.calculatePoints(it.homeAwayStats).toString(),
                it.homeAwayStats.wins.total.toString(),
                it.homeAwayStats.draws.total.toString(),
                it.homeAwayStats.loses.total.toString(),
                it.homeAwayStats.played.total.toString(),
                it.goalsFor.total.toString(),
                it.goalsAgainst.total.toString(),
                (it.goalsFor.total - it.goalsAgainst.total).toString(),
                store.getRecordString(it.homeAwayStats),
                store.getRecordString(it.homeAwayStats, false)
            )
        )
    }
    val columnWidthList = listOf(50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 100.dp, 100.dp)

    StandingsViewContainer(
        state = NewStandingsContainerState(
            headerCategories = if (isMLS) headerCategories else null,
            secondCategories = StringConstants.Football.TEAM_STANDINGS_CATEGORIES,
            standings = teamStandings,
            headerCategorySelectedIndex = headerCategorySelectedIndex,
            secondCategorySelectedIndex = selectedCategoryIndex,
            columnWidthList = columnWidthList
        ),
        actions = StandingsContainerActions(
            headerCategoryButtonAction = { index ->
                store.send(FBTeamStandingsAction.SelectHeaderCategory(index))
            },
            secondCategoryButtonAction = { index, _ ->
                store.send(FBTeamStandingsAction.SelectCategory(index))
            },
            itemButtonAction = { id ->
                // TODO: UEFA 리그는 showTeamStats가 안돼서 Button disabled 처리 해야함.
                store.send(FBTeamStandingsAction.ShowTeamStats(id))
            }
        ),
        titleContent = {
            league?.let {
                FBLeagueTitle(
                    url = league.logo,
                    leagueName = league.name,
                    leagueSeason = league.season,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    )
}
























