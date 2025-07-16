package com.moare.android.features.search.display.nba.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.moare.android.core.util.MatchDescriptionConverter
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
import com.moare.android.features.search.display.football.view.FBLeagueScheduleList
import com.moare.android.features.search.display.football.viewmodel.FBLeagueScheduleIntent
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueScheduleIntent
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueScheduleViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBALeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBAGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import com.moare.android.ui.common.components.CalendarList
import com.moare.android.ui.common.components.CalendarType
import com.moare.android.ui.common.components.CapsuleButton
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.ProgressIndicator
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterRow

@Composable
fun NBALeagueScheduleView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaLeagueScheduleViewModel: NBALeagueScheduleViewModel = hiltViewModel(),
    data: NBALeagueScheduleDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by nbaLeagueScheduleViewModel.displayModel.collectAsState()
    val yearMonthList by nbaLeagueScheduleViewModel.yearMonthList.collectAsState()
    val days by nbaLeagueScheduleViewModel.days.collectAsState()
    val selectedYearMonthIndex by nbaLeagueScheduleViewModel.selectedYearMonthIndex.collectAsState()
    val selectedDayIndex by nbaLeagueScheduleViewModel.selectedDayIndex.collectAsState()
    val yearMonthCalendarScrollTrigger by nbaLeagueScheduleViewModel.yearMonthCalendarScrollTrigger.collectAsState()
    val dayCalendarScrollTrigger by nbaLeagueScheduleViewModel.dayCalendarScrollTrigger.collectAsState()
    val isAllResultOpened by nbaLeagueScheduleViewModel.isAllResultOpened.collectAsState()
    val displayDataState by nbaLeagueScheduleViewModel.displayDataState.collectAsState()

    val displayModels by searchViewModel.displayModels.collectAsState()
    val viewStack by searchViewModel.viewStack.collectAsState()
    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       etc
       --------------------- */

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.NBALeagueSchedule) {
            nbaLeagueScheduleViewModel.send(NBALeagueScheduleIntent.InitData(data))
        }
    }

    LaunchedEffect(viewStack) {
        // update games data after refreshing in NBAGameStatsView
        if (viewStack.isNotEmpty() && viewStack.last() is SportDecodableModel.NBALeagueSchedule) {
            val nbaLeagueSchedule = viewStack.last() as SportDecodableModel.NBALeagueSchedule

            poppedView?.let {
                if (it is SportDecodableModel.NBAGameStats) {
                    nbaLeagueScheduleViewModel.send(NBALeagueScheduleIntent.UpdateGamesData(nbaLeagueSchedule, it) { data ->
                        searchViewModel.send(SearchViewModel.Intent.UpdateLastViewStack(data))
                    })
                }
            }
        }
    }

    ScheduleViewContainer(
        state = ScheduleContainerState(
            shouldShowCalendar = displayModel?.scheduleType != ScheduleType.TEAM_FLAT,
            shouldFetchSchedule = displayModel?.scheduleType == ScheduleType.LEAGUE,
            displayDataState = displayDataState,
            calendarUiState = CalendarUiState(
                yearMonthList,
                days,
                selectedYearMonthIndex,
                selectedDayIndex,
                yearMonthCalendarScrollTrigger,
                dayCalendarScrollTrigger
            ),
            isAllResultOpened = isAllResultOpened
        ),
        actions = ScheduleContainerActions(
            calendarUiActions = CalendarUiActions(
                onSelectYearMonth = { yearMonth, index ->
                    nbaLeagueScheduleViewModel.send(NBALeagueScheduleIntent.SelectYearMonth(yearMonth, index) { data ->
                        // 현재 구조 콜백 수정 필요?
                        searchViewModel.send(SearchViewModel.Intent.UpdateLastViewStack(data))
                    })
                },
                onSelectDay = { day, index ->
                    nbaLeagueScheduleViewModel.send(NBALeagueScheduleIntent.SelectDay(day, index))
                }
            ),
            allResultButtonAction = {
                nbaLeagueScheduleViewModel.send(NBALeagueScheduleIntent.ToggleAllResult)
            }
        ),
        titleContent = {},
        gameListContent = {
            NBALeagueScheduleList()
        }
    )
}

@Composable
fun NBALeagueScheduleList(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaLeagueScheduleViewModel: NBALeagueScheduleViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val filteredGames by nbaLeagueScheduleViewModel.filteredGames.collectAsState()
    val selectedDayIndex by nbaLeagueScheduleViewModel.selectedDayIndex.collectAsState()
    val teamNameDic = nbaLeagueScheduleViewModel.teamNameDictionary

    val gameListToDisplay = filteredGames[selectedDayIndex] ?: emptyList()

    LazyColumn {
        items(gameListToDisplay) { item ->
            NBALeagueScheduleListItem(data = item, teamNameDic = teamNameDic)
        }
    }
}

@Composable
fun NBALeagueScheduleListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaLeagueScheduleViewModel: NBALeagueScheduleViewModel = hiltViewModel(),
    teamNameDic: Map<String, String>,
    data: NBAGameForSchedule,
) {
    val gameId = data.gameId
    val homeTeamId = data.homeTeamId
    val awayTeamId = data.awayTeamId
    val gameStatus = data.gameStatus.toIntOrNull() ?: 0

    /* ---------------------
       ui state
       --------------------- */
    var isResultOpened by remember { mutableStateOf(false) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val gameResultOpenedStateList by nbaLeagueScheduleViewModel.gameResultOpenedStateList.collectAsState()
    val displayModel by nbaLeagueScheduleViewModel.displayModel.collectAsState()

    val displayModels by searchViewModel.displayModels.collectAsState()
    val nbaGameStatsModel = displayModels[SportDisplayType.NBA_GAME_STATS] as? NBAGameStatsDisplayModel

    /* ---------------------
       constants
       --------------------- */
    val gameStatusText = when (gameStatus) {
        StringConstants.NBA.GAME_SCHEDULED -> StringConstants.GAME_NOT_STARTED_STR
        StringConstants.NBA.GAME_LIVE -> StringConstants.GAME_LIVE_STR
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
        StringConstants.NBA.GAME_FINAL -> if (isResultOpened) StringConstants.GAME_FINISHED_STR else StringConstants.RESULT_OPEN
        else -> ""
    }

    val gameStatusColor = if (gameStatus == StringConstants.NBA.GAME_LIVE) {
        MaterialTheme.colors.primary
    } else {
        Color.Gray
    }

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (gameStatus == StringConstants.NBA.GAME_FINAL) {
            isResultOpened = gameResultOpenedStateList[gameId] ?: false
        } else if (gameStatus == StringConstants.NBA.GAME_SCHEDULED) {
            isResultOpened = false
        } else {
            isResultOpened = true
        }
    }
    LaunchedEffect(gameResultOpenedStateList) {
        if (gameStatus == StringConstants.NBA.GAME_FINAL) {
            isResultOpened = gameResultOpenedStateList[gameId] ?: false
        }
    }
    LaunchedEffect(nbaGameStatsModel) {
        nbaGameStatsModel?.let {
            if (gameStatus != StringConstants.NBA.GAME_SCHEDULED) {
                isResultOpened = true
            }
        }
    }

    ScheduleGameItem(
        state = ScheduleGameItemState(
            isClickEnabled = nbaGameStatsModel == null,
            homeTeamLogo = NBAUtil.teamLogoUrl(homeTeamId),
            homeTeamName = teamNameDic["short_${homeTeamId}"] ?: "",
            homeTeamScore = data.homeTeamScore,
            awayTeamLogo = NBAUtil.teamLogoUrl(awayTeamId),
            awayTeamName = teamNameDic["short_${awayTeamId}"] ?: "",
            awayTeamScore = data.awayTeamScore,
            isResultOpened = isResultOpened,
            gameStatusText = gameStatusText,
            gameStatusColor = gameStatusColor,
            isCapsuleButtonDisabled = nbaGameStatsModel != null || gameStatus != StringConstants.NBA.GAME_FINAL,
            date = data.date,
            venue = teamNameDic["venue_${homeTeamId}"] ?: "",
//            gameType = "", // TODO: 아래 playoffs info 주석 참고해서 ScheduleGameItem에 만들어야함
            shouldShowOnlyDateTime = (displayModel?.scheduleType != ScheduleType.TEAM_FLAT && nbaGameStatsModel == null), // (리그, 팀)일정 화면에서만 true
            shouldShowVenue = nbaGameStatsModel != null,
            shouldShowHomeLabel = nbaGameStatsModel != null,
            shouldShowAwayLabel = nbaGameStatsModel != null,
            isSvgLogo = true
        ),
        actions = ScheduleGameItemActions(
            onGameItemClick = {
                displayModel?.let {
                    searchViewModel.send(SearchViewModel.Intent.SelectNBAGame(data, it.season))
                }

                // set selected game's isOpened true
                nbaLeagueScheduleViewModel.send(NBALeagueScheduleIntent.UpdateResultOpenedState(gameId, true))
            },
            onCapsuleButtonClick = {
                nbaLeagueScheduleViewModel.send(NBALeagueScheduleIntent.UpdateResultOpenedState(gameId, !isResultOpened))
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




















