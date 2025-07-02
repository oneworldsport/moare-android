package com.moare.android.features.search.display.football.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.FBUtil
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.display.common.container.state.StandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.NewStandingsViewContainer
import com.moare.android.features.search.display.common.container.view.StandingsViewContainer
import com.moare.android.features.search.display.football.viewmodel.FBTeamStandingsIntent
import com.moare.android.features.search.display.football.viewmodel.FBTeamStandingsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplayModel
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar

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
    val isKeyword by fbTeamStandingsViewModel.isKeyword.collectAsState()
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
       etc
       --------------------- */
    val selectedCategoryPosition = with(LocalDensity.current) {
        val position = if (selectedCategoryIndex == 9) {
            (fbTeamStandingsViewModel.intDataItemWidth * 8) + fbTeamStandingsViewModel.stringDataItemWidth
        } else {
            fbTeamStandingsViewModel.intDataItemWidth * selectedCategoryIndex
        }

        position.toPx()
    }.toInt()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.FBTeamStandings) {
            fbTeamStandingsViewModel.send(FBTeamStandingsIntent.InitData(data))
        }
    }

    // scroll to category that matches with the keyword
    LaunchedEffect(isKeyword) {
        if (isKeyword) {
            horizontalScrollState.animateScrollTo(
                value = selectedCategoryPosition,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearOutSlowInEasing
                )
            )
        }
    }

    NewStandingsViewContainer(
        state = NewStandingsContainerState(
            secondCategories = StringConstants.Football.TEAM_STANDINGS_CATEGORIES,
            standings = teamStandings,
            secondCategorySelectedIndex = selectedCategoryIndex,
            columnWidthList = columnWidthList
        ),
        actions = StandingsContainerActions(
            secondCategoryButtonAction = { index ->
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
























