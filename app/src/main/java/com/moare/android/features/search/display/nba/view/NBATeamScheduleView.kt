package com.moare.android.features.search.display.nba.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.constants.UIConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.FBUtil
import com.moare.android.core.util.NBAUtil
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.display.common.container.component.ScheduleGameItem
import com.moare.android.features.search.display.common.container.state.CalendarUiActions
import com.moare.android.features.search.display.common.container.state.CalendarUiState
import com.moare.android.features.search.display.common.container.state.ScheduleContainerActions
import com.moare.android.features.search.display.common.container.state.ScheduleContainerState
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemActions
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemState
import com.moare.android.features.search.display.common.container.view.ScheduleViewContainer
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueScheduleIntent
import com.moare.android.features.search.display.nba.viewmodel.NBATeamScheduleIntent
import com.moare.android.features.search.display.nba.viewmodel.NBATeamScheduleViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamScheduleDisplayModel
import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBAGameForSchedule
import com.moare.android.ui.common.components.CapsuleButton
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterRow

@Composable
fun NBATeamScheduleView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaTeamScheduleViewModel: NBATeamScheduleViewModel = hiltViewModel(),
    data: NBATeamScheduleDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by nbaTeamScheduleViewModel.displayModel.collectAsState()
    val isAllResultOpened by nbaTeamScheduleViewModel.isAllResultOpened.collectAsState()

    val displayModels by searchViewModel.displayModels.collectAsState()
    val nbaGameStatsModel = displayModels[SportDisplayType.NBA_GAME_STATS] as? NBAGameStatsDisplayModel
    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.NBATeamSchedule) {
            nbaTeamScheduleViewModel.send(NBATeamScheduleIntent.InitData(data))
        }
    }

    ScheduleViewContainer(
        state = ScheduleContainerState(
            shouldShowCalendar = false,
            shouldFetchSchedule = false,
            isAllResultOpened = isAllResultOpened
        ),
        actions = ScheduleContainerActions(
            allResultButtonAction = {
                nbaTeamScheduleViewModel.send(NBATeamScheduleIntent.ToggleAllResult)
            }
        ),
        titleContent = {},
        gameListContent = {
            NBATeamScheduleList()
        }
    )
}

@Composable
fun NBATeamScheduleList(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaTeamScheduleViewModel: NBATeamScheduleViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val games by nbaTeamScheduleViewModel.games.collectAsState()

    val displayModels by searchViewModel.displayModels.collectAsState()
    val nbaGameStatsModel = displayModels[SportDisplayType.NBA_GAME_STATS] as? NBAGameStatsDisplayModel

    LazyColumn {
        items(games) { item ->
            NBATeamScheduleListItem(data = item)
        }
    }
}

@Composable
fun NBATeamScheduleListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaTeamScheduleViewModel: NBATeamScheduleViewModel = hiltViewModel(),
    data: NBAGameForSchedule
) {
    val gameId = data.gameId
    val homeTeamId = data.homeTeamId
    val awayTeamId = data.awayTeamId
    val gameStatus = data.gameStatus.toIntOrNull() ?: 0
    val teamNameDic = nbaTeamScheduleViewModel.teamNameDictionary

    /* ---------------------
       ui state
       --------------------- */
    var isResultOpened by remember { mutableStateOf(false) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val gameResultOpenedStateList by nbaTeamScheduleViewModel.gameResultOpenedStateList.collectAsState()

    /* ---------------------
       constants
       --------------------- */
    val gameStatusText = when (gameStatus) {
        1 -> StringConstants.GAME_NOT_STARTED_STR
        2 -> StringConstants.GAME_LIVE_STR
//            if (data.lineScore.firstOrNull()?.ptsOt3 != null) {
//            StringConstants.NBA.GAME_OT_3
//        } else if (data.lineScore.firstOrNull()?.ptsOt2 != null) {
//            StringConstants.NBA.GAME_OT_2
//        } else if (data.lineScore.firstOrNull()?.ptsOt1 != null) {
//            StringConstants.NBA.GAME_OT_1
//        } else if (data.lineScore.firstOrNull()?.ptsQtr4 != null) {
//            StringConstants.NBA.GAME_QTR_4
//        } else if (data.lineScore.firstOrNull()?.ptsQtr3 != null) {
//            StringConstants.NBA.GAME_QTR_3
//        } else if (data.lineScore.firstOrNull()?.ptsQtr2 != null) {
//            StringConstants.NBA.GAME_QTR_2
//        } else if (data.lineScore.firstOrNull()?.ptsQtr1 != null) {
//            StringConstants.NBA.GAME_QTR_1
//        } else {
//            ""
//        }
        3 -> if (isResultOpened) StringConstants.GAME_FINISHED_STR else StringConstants.RESULT_OPEN
        else -> ""
    }

    val gameStatusColor = if (gameStatus == 2) {
        MaterialTheme.colors.primary
    } else {
        Color.Gray
    }

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (gameStatus == 3) {
            isResultOpened = gameResultOpenedStateList[gameId] ?: false
        } else if (gameStatus == 1) {
            isResultOpened = false
        } else {
            isResultOpened = true
        }
    }
    LaunchedEffect(gameResultOpenedStateList) {
        if (gameStatus == 3) {
            isResultOpened = gameResultOpenedStateList[gameId] ?: false
        }
    }

    ScheduleGameItem(
        state = ScheduleGameItemState(
            homeTeamLogo = NBAUtil.teamLogoUrl(homeTeamId),
            homeTeamName = teamNameDic["short_${homeTeamId}"] ?: "",
            homeTeamScore = data.homeTeamScore,
            awayTeamLogo = FBUtil.teamLogoUrl(awayTeamId),
            awayTeamName = teamNameDic["short_${awayTeamId}"] ?: "",
            awayTeamScore = data.awayTeamScore,
            isResultOpened = isResultOpened,
            gameStatusText = gameStatusText,
            gameStatusColor = gameStatusColor,
            isCapsuleButtonDisabled = gameStatus != 3,
            date = data.date,
            venue = teamNameDic["venue_${homeTeamId}"] ?: "",
            gameType = "", // TODO: 아래 playoffs info 주석 참고해서 ScheduleGameItem에 만들어야함
            shouldShowOnlyDateTime = false,
            shouldShowGameType = true,
            isSvgLogo = true
        ),
        actions = ScheduleGameItemActions(
            onGameItemClick = {
                searchViewModel.send(SearchViewModel.Intent.SelectNBAGame(data))

                // set selected game's isOpened true
                nbaTeamScheduleViewModel.send(NBATeamScheduleIntent.UpdateResultOpenedState(gameId, true))
            },
            onCapsuleButtonClick = {
                nbaTeamScheduleViewModel.send(NBATeamScheduleIntent.UpdateResultOpenedState(gameId, !isResultOpened))
            }
        )
    )

    // playoffs info
//            if (data.gameSummary != null && data.gameSummary.seriesText.isNotEmpty()) {
//                val gameSummary = data.gameSummary
//                Text(
//                    text = NBAUtil.gameType(gameSummary, true),
//                    fontSize = 11.sp
//                )
//
//                if (data.seasonSeries != null && gameSummary.seriesGameNumber.isNotEmpty()) {
//                    val seasonSeries = data.seasonSeries
//                    CenterRow {
//                        Text(
//                            text = "시리즈 스코어: ",
//                            fontSize = 11.sp
//                        )
//
//                        Text(
//                            text = "${seasonSeries.homeTeamWins}",
//                            fontSize = 11.sp,
//                            color = if (seasonSeries.homeTeamWins >= seasonSeries.homeTeamLosses) Moare else Color.Black
//                        )
//
//                        Text(
//                            text = " - ",
//                            fontSize = 11.sp
//                        )
//
//                        Text(
//                            text = "${seasonSeries.homeTeamLosses}",
//                            fontSize = 11.sp,
//                            color = if (seasonSeries.homeTeamLosses >= seasonSeries.homeTeamWins) Moare else Color.Black
//                        )
//                    }
//                }
//            }
}







































