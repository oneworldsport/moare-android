package com.moare.android.features.search.display.nba.view

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.NBAUtil
import com.moare.android.features.search.display.common.container.component.ScheduleGameItem
import com.moare.android.features.search.display.common.container.state.CalendarUiActions
import com.moare.android.features.search.display.common.container.state.CalendarUiState
import com.moare.android.features.search.display.common.container.state.ScheduleContainerActions
import com.moare.android.features.search.display.common.container.state.ScheduleContainerState
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemActions
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemState
import com.moare.android.features.search.display.common.container.view.ScheduleViewContainer
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueScheduleAction
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueScheduleStore
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBALeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.nba.NBAGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType

@Composable
fun NBALeagueScheduleView(
    searchStore: SearchViewModel,
    store: NBALeagueScheduleStore
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by store.displayModel.collectAsState()
    val yearMonthList by store.yearMonthList.collectAsState()
    val days by store.days.collectAsState()
    val selectedYearMonthIndex by store.selectedYearMonthIndex.collectAsState()
    val selectedDayIndex by store.selectedDayIndex.collectAsState()
    val yearMonthCalendarScrollTrigger by store.yearMonthCalendarScrollTrigger.collectAsState()
    val dayCalendarScrollTrigger by store.dayCalendarScrollTrigger.collectAsState()
    val isAllResultOpened by store.isAllResultOpened.collectAsState()
    val displayDataState by store.displayDataState.collectAsState()

    val displayModels by searchStore.displayModels.collectAsState()
    val viewStack by searchStore.viewStack.collectAsState()

    LaunchedEffect(viewStack) {
        // update games data after refreshing in NBAGameStatsView
        if (viewStack.isNotEmpty() && viewStack.last() is SportDecodableModel.NBALeagueSchedule) {
            val nbaLeagueSchedule = viewStack.last() as SportDecodableModel.NBALeagueSchedule

//            poppedView?.let {
//                if (it is SportDecodableModel.NBAGameStats) {
//                    store.send(NBALeagueScheduleAction.UpdateGamesData(nbaLeagueSchedule, it) { data ->
//                        searchStore.send(SearchViewModel.Intent.UpdateLastViewStack(data))
//                    })
//                }
//            }
        }
    }

    ScheduleViewContainer(
        state = ScheduleContainerState(
            shouldShowCalendar = displayModel.scheduleType != ScheduleType.TEAM_FLAT,
            shouldFetchSchedule = displayModel.scheduleType == ScheduleType.LEAGUE,
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
                    store.send(NBALeagueScheduleAction.SelectYearMonth(yearMonth, index) { data ->
                        // 현재 구조 콜백 수정 필요?
                        searchStore.send(SearchViewModel.Intent.UpdateLastViewStack(data))
                    })
                },
                onSelectDay = { day, index ->
                    store.send(NBALeagueScheduleAction.SelectDay(day, index))
                }
            ),
            allResultButtonAction = {
                store.send(NBALeagueScheduleAction.ToggleAllResult)
            }
        ),
        titleContent = {},
        gameListContent = {
            NBALeagueScheduleList(searchStore = searchStore, store = store)
        }
    )
}

@Composable
fun NBALeagueScheduleList(
    searchStore: SearchViewModel,
    store: NBALeagueScheduleStore
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val filteredGames by store.filteredGames.collectAsState()
    val selectedDayIndex by store.selectedDayIndex.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val gameListToDisplay = filteredGames[selectedDayIndex] ?: emptyList()

    LazyColumn {
        items(gameListToDisplay) { item ->
            NBALeagueScheduleListItem(
                searchStore = searchStore,
                store = store,
                data = item,
                teamNameDic = teamNameDic
            )
        }
    }
}

@Composable
fun NBALeagueScheduleListItem(
    searchStore: SearchViewModel,
    store: NBALeagueScheduleStore,
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
    val gameResultOpenedStateList by store.gameResultOpenedStateList.collectAsState()
    val displayModel by store.displayModel.collectAsState()

    val displayModels by searchStore.displayModels.collectAsState()
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
                searchStore.send(SearchViewModel.Intent.SelectNBAGame(data, displayModel.season))

                // set selected game's isOpened true
                store.send(NBALeagueScheduleAction.UpdateResultOpenedState(gameId, true))
            },
            onCapsuleButtonClick = {
                store.send(NBALeagueScheduleAction.UpdateResultOpenedState(gameId, !isResultOpened))
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




















