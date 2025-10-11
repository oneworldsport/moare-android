package com.moare.android.features.search.display.nba.view

import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.NBAUtil
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.StandingsViewContainer
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStandingsAction
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStandingsStore
import com.moare.android.features.search.display.search.viewmodel.SearchAction
import com.moare.android.features.search.display.search.viewmodel.SearchStore
import com.moare.android.ui.common.components.NBATitle

@Composable
fun NBATeamStandingsView(
    searchStore: SearchStore,
    store: NBATeamStandingsStore
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
    val headerCategorySelectedIndex by store.headerCategorySelectedIndex.collectAsState()
    val selectedCategoryIndex by store.categorySelectedIndex.collectAsState()
    val standings by store.standings.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val season = displayModel.standings.firstOrNull()?.stats?.groupValue

    val teamStandings: List<StandingsItemState> = standings.map {
        val stats = it.stats
        StandingsItemState(
            id = it.team.id,
            imageUrl = NBAUtil.teamLogoUrl(it.team.id),
            name = teamNameDic["short_${it.team.id}"] ?: it.team.fullName,
            dataList = listOf(
                store.calculateGamesBack(stats).toString(),
                stats.winsPct.toString(),
                stats.wins.toString(),
                stats.losses.toString(),
                stats.gp.toString(),
                stats.ptsPG.toString(),
                stats.plusMinusPG.toString(),
                stats.astPG.toString(),
                stats.rebPG.toString(),
                stats.fgPct.toString(),
                stats.fg3Pct.toString(),
                stats.ftPct.toString(),
                stats.blkPG.toString(),
                stats.stlPG.toString(),
                stats.tovPG.toString(),
                stats.pfPG.toString()
            )
        )
    }
    val columnWidthList = listOf(50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp)

    StandingsViewContainer(
        state = NewStandingsContainerState(
            headerCategories = headerCategories,
            secondCategories = StringConstants.NBA.TEAM_STANDINGS_CATEGORIES,
            standings = teamStandings,
            headerCategorySelectedIndex = headerCategorySelectedIndex,
            secondCategorySelectedIndex = selectedCategoryIndex,
            columnWidthList = columnWidthList
        ),
        actions = StandingsContainerActions(
            headerCategoryButtonAction = { index ->
                store.send(NBATeamStandingsAction.SelectHeaderCategory(index))
            },
            secondCategoryButtonAction = { index, _ ->
                store.send(NBATeamStandingsAction.SelectCategory(index))
            },
            itemButtonAction = { id ->
                searchStore.send(SearchAction.ShowTeamStats(teamId = id))
            }
        ),
        titleContent = {
            NBATitle(
                leagueName = "NBA 정규시즌",
                leagueSeason = season?.split("-")?.firstOrNull()?.toIntOrNull() ?: CalendarUtil.currentYear
            )
        }
    )
}

























