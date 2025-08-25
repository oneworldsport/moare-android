package com.moare.android.features.search.display.kbo.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.moare.android.core.util.KBOUtil
import com.moare.android.core.util.MLBUtil
import com.moare.android.core.util.NBAUtil
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.display.common.container.state.StandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.NewStandingsViewContainer
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStandingsIntent
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStandingsViewModel
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStandingsIntent
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStandingsViewModel
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStandingsIntent
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplayModel
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar
import com.moare.android.ui.util.screenWidthDp

@Composable
fun KBOTeamStandingsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
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
    val isKeyword by kboTeamStandingsViewModel.isKeyword.collectAsState()
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
       etc
       --------------------- */
    val selectedCategoryPosition = with(LocalDensity.current) {
        val position = kboTeamStandingsViewModel.dataItemWidth * selectedCategoryIndex
        position.toPx()
    }.toInt()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.KBOTeamStandings) {
            kboTeamStandingsViewModel.send(KBOTeamStandingsIntent.InitData(data))
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