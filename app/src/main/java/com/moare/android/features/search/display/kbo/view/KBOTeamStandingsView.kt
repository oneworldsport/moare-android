package com.moare.android.features.search.display.kbo.view

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
import com.moare.android.core.util.KBOUtil
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.StandingsViewContainer
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStandingsIntent
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStandingsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStandingsDisplayModel
import com.moare.android.ui.common.components.BaseballLeagueTitle

@Composable
fun KBOTeamStandingsView(
    searchViewModel: SearchViewModel,
    kboTeamStandingsViewModel: KBOTeamStandingsViewModel = hiltViewModel(),
    data: KBOTeamStandingsDisplayModel
) {
    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by kboTeamStandingsViewModel.displayModel.collectAsState()
    val selectedCategoryIndex by kboTeamStandingsViewModel.selectedCategoryIndex.collectAsState()
    val standings by kboTeamStandingsViewModel.standings.collectAsState()
    val teamNameDic = kboTeamStandingsViewModel.teamNameDictionary

    val season = displayModel?.standings?.firstOrNull()?.stats?.season

    val poppedView by searchViewModel.poppedView.collectAsState()

    val teamStandings: List<StandingsItemState> = standings.map {
        val rankData = it.stats.rankData
        val hitterData = it.stats.hitterData
        val pitcherData = it.stats.pitcherData
        StandingsItemState(
            id = it.team.id,
            imageUrl = KBOUtil.teamLogoUrl(it.team.id),
            name = teamNameDic["short_${it.team.id}"] ?: it.team.teamName,
            dataList = listOf(
                rankData.gb,
                rankData.winpct,
                rankData.wins,
                rankData.losses,
                rankData.gp,
                rankData.streak,
                hitterData.avg,
                hitterData.h,
                hitterData.hr,
                hitterData.slg,
                hitterData.r,
                pitcherData.er,
                pitcherData.avg,
                pitcherData.h,
                pitcherData.hr,
                pitcherData.r,
                it.stats.runnerData.sbPercent
            )
        )
    }
    val columnWidthList = listOf(50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 60.dp, 60.dp, 50.dp, 50.dp, 50.dp, 70.dp)

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.KBOTeamStandings) {
            kboTeamStandingsViewModel.send(KBOTeamStandingsIntent.InitData(data))
        }
    }

    StandingsViewContainer(
        state = NewStandingsContainerState(
            secondCategories = StringConstants.KBO.TEAM_STANDINGS_CATEGORIES,
            standings = teamStandings,
            secondCategorySelectedIndex = selectedCategoryIndex,
            firstColumnWidth = 100.dp,
            columnWidthList = columnWidthList,
        ),
        actions = StandingsContainerActions(
            secondCategoryButtonAction = { index, _ ->
                kboTeamStandingsViewModel.send(KBOTeamStandingsIntent.SelectCategory(index))
            },
            itemButtonAction = { id ->
                searchViewModel.send(SearchViewModel.Intent.ShowTeamStats(teamId = id))
            }
        ),
        titleContent = {
            BaseballLeagueTitle(
                url = KBOUtil.kboLogoUrl,
                leagueName = "KBO",
                leagueSeason = season,
            )
        }
    )
}