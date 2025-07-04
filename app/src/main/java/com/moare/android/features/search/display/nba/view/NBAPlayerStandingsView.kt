package com.moare.android.features.search.display.nba.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.NBAUtil
import com.moare.android.core.util.rounded
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.display.common.container.state.StandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsHighlightItemState
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.NewStandingsViewContainer
import com.moare.android.features.search.display.common.container.view.StandingsViewContainer
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStandingsIntent
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerStandingsIntent
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerStandingsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStandingsDisplay
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStandingsDisplayModel
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.HCapsuleBarSize
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.convertDpToPx
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar
import kotlinx.coroutines.delay

@Composable
fun NBAPlayerStandingsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaPlayerStandingsViewModel: NBAPlayerStandingsViewModel = hiltViewModel(),
    data: NBAPlayerStandingsDisplayModel
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
    val displayModel by nbaPlayerStandingsViewModel.displayModel.collectAsState()
    val displayDataState by nbaPlayerStandingsViewModel.displayDataState.collectAsState()
    val firstSelectedIndex by nbaPlayerStandingsViewModel.firstSelectedIndex.collectAsState()
    val secondCategorySelectedIndex by nbaPlayerStandingsViewModel.secondCategorySelectedIndex.collectAsState()
    val isKeyword by nbaPlayerStandingsViewModel.isKeyword.collectAsState()
    val filteredStandings by nbaPlayerStandingsViewModel.filteredStandings.collectAsState()
    val entityIndex by nbaPlayerStandingsViewModel.entityIndex.collectAsState()
    val filteredStandingsStartIndex by nbaPlayerStandingsViewModel.filteredStandingsStartIndex.collectAsState()
    val playerNameDic = nbaPlayerStandingsViewModel.playerNameDictionary
    val teamNameDic = nbaPlayerStandingsViewModel.teamNameDictionary

    val season = displayModel?.standings?.firstOrNull()?.stats?.groupValue

    val poppedView by searchViewModel.poppedView.collectAsState()

    val playerStandings: List<StandingsItemState> = filteredStandings.map {
        val stats = it.stats
        val id = it.player.personId
        StandingsItemState(
            id = id,
            imageUrl = NBAUtil.playerPhotoUrl(id),
            name = playerNameDic["${id}"] ?: it.player.displayFirstLast,
            subName = teamNameDic["short_${it.player.teamId}"] ?: it.player.teamCity,
            dataList = listOf(
                stats.ptsPG.toString(),
                stats.astPG.toString(),
                stats.orebPG.toString(),
                stats.fgaPG.toString(),
                stats.fgmPG.toString(),
                stats.fgPct.toString(),
                stats.fg3aPG.toString(),
                stats.fg3mPG.toString(),
                stats.fg3Pct.toString(),
                stats.ftaPG.toString(),
                stats.ftmPG.toString(),
                stats.ftPct.toString(),
                stats.drebPG.toString(),
                stats.blkPG.toString(),
                stats.stlPG.toString(),
                stats.rebPG.toString(),
                stats.tovPG.toString(),
                stats.pfPG.toString(),
                stats.pfdPG.toString(),
                stats.blkaPG.toString(),
                stats.plusMinusPG.toString(),
                stats.gp.toString(),
                stats.minPG,
                stats.wins.toString(),
                stats.losses.toString(),
                stats.winsPct.toString(),
                stats.td3.toString(),
                stats.dd2.toString()
            )
        )
    }
    val columnWidthList = listOf(80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp,
        80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 50.dp,
        80.dp, 80.dp, 80.dp, 80.dp, 80.dp, 80.dp)

    /* ---------------------
       etc
       --------------------- */
    val secondSelectedCategoryPosition = with(LocalDensity.current) {
        val attackCategoriesSize = StringConstants.NBA.PLAYER_STANDINGS_ATTACK_CATEGORIES.size
        val defendCategoriesSize = StringConstants.NBA.PLAYER_STANDINGS_DEFEND_CATEGORIES.size

        if (secondCategorySelectedIndex in 0 until attackCategoriesSize) {
            (nbaPlayerStandingsViewModel.itemWidth * secondCategorySelectedIndex).toPx()
        } else if (secondCategorySelectedIndex in attackCategoriesSize until attackCategoriesSize + defendCategoriesSize) {
            ((nbaPlayerStandingsViewModel.itemWidth * secondCategorySelectedIndex) + nbaPlayerStandingsViewModel.barWidth).toPx()
        } else {
            ((nbaPlayerStandingsViewModel.itemWidth * secondCategorySelectedIndex) + (nbaPlayerStandingsViewModel.barWidth * 2)).toPx()
        }
    }.toInt()

    val previousScrollPosition = convertDpToPx(nbaPlayerStandingsViewModel.dataItemHeight * 10).toInt()
    val firstScrollPosition = convertDpToPx(nbaPlayerStandingsViewModel.dataItemHeight).toInt()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.NBAPlayerStandings) {
            nbaPlayerStandingsViewModel.send(NBAPlayerStandingsIntent.InitData(data))
        }
    }

    // Scroll to category that matches with the keyword,
    // or when first category list's item is selected by click.
    LaunchedEffect(isKeyword, firstSelectedIndex) {
        if (nbaPlayerStandingsViewModel.shouldScrollCategory) {
            horizontalScrollState.animateScrollTo(
                value = secondSelectedCategoryPosition,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearOutSlowInEasing
                )
            )
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
                nbaPlayerStandingsViewModel.send(NBAPlayerStandingsIntent.ShowMoreStandings(true))
            }
            verticalScrollState.maxValue -> {
                nbaPlayerStandingsViewModel.send(NBAPlayerStandingsIntent.ShowMoreStandings(false))
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

    NewStandingsViewContainer(
        state = NewStandingsContainerState(
            secondCategories = StringConstants.NBA.PLAYER_STANDINGS_SECOND_CATEGORIES,
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
                nbaPlayerStandingsViewModel.send(NBAPlayerStandingsIntent.SelectSecondCategory(index, category))
            },
            itemButtonAction = { id ->
                searchViewModel.send(SearchViewModel.Intent.ShowPlayerStats(category = "basketball", playerId = id))
            }
        ),
        verticalScrollState = verticalScrollState,
        titleContent = {
            NBATitle(
                leagueName = "NBA 정규시즌",
                leagueSeason = season?.split("-")?.firstOrNull()?.toIntOrNull() ?: 2024,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    )
}
















