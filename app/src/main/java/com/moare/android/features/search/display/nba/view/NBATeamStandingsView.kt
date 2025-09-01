package com.moare.android.features.search.display.nba.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.NBAUtil
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.StandingsViewContainer
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStandingsIntent
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStandingsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplayModel
import com.moare.android.ui.common.components.NBATitle

@Composable
fun NBATeamStandingsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaTeamStandingsViewModel: NBATeamStandingsViewModel = hiltViewModel(),
    data: NBATeamStandingsDisplayModel
) {
    val headerCategories = listOf("서부 컨퍼런스", "동부 컨퍼런스")

    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by nbaTeamStandingsViewModel.displayModel.collectAsState()
    val selectedConferenceIndex by nbaTeamStandingsViewModel.selectedConferenceIndex.collectAsState()
    val selectedCategoryIndex by nbaTeamStandingsViewModel.selectedCategoryIndex.collectAsState()
    val standings by nbaTeamStandingsViewModel.standings.collectAsState()
    val teamNameDic = nbaTeamStandingsViewModel.teamNameDictionary

    val season = displayModel?.standings?.firstOrNull()?.stats?.groupValue

    val poppedView by searchViewModel.poppedView.collectAsState()

    val teamStandings: List<StandingsItemState> = standings.map {
        val stats = it.stats
        StandingsItemState(
            id = it.team.id,
            imageUrl = NBAUtil.teamLogoUrl(it.team.id),
            name = teamNameDic["short_${it.team.id}"] ?: it.team.fullName,
            dataList = listOf(
                nbaTeamStandingsViewModel.calculateGamesBack(stats).toString(),
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

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.NBATeamStandings) {
            nbaTeamStandingsViewModel.send(NBATeamStandingsIntent.InitData(data))
        }
    }

    StandingsViewContainer(
        state = NewStandingsContainerState(
            headerCategories = headerCategories,
            secondCategories = StringConstants.NBA.TEAM_STANDINGS_CATEGORIES,
            standings = teamStandings,
            headerCategorySelectedIndex = selectedConferenceIndex,
            secondCategorySelectedIndex = selectedCategoryIndex,
            columnWidthList = columnWidthList
        ),
        actions = StandingsContainerActions(
            headerCategoryButtonAction = { index ->
                nbaTeamStandingsViewModel.send(NBATeamStandingsIntent.SelectConference(index))
            },
            secondCategoryButtonAction = { index, _ ->
                nbaTeamStandingsViewModel.send(NBATeamStandingsIntent.SelectCategory(index))
            },
            itemButtonAction = { id ->
                searchViewModel.send(SearchViewModel.Intent.ShowTeamStats(teamId = id))
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

























