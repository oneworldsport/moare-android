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
import com.moare.android.core.constants.Constants
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
import com.moare.android.features.search.display.football.viewmodel.FBLeagueScheduleAction
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueScheduleAction
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueScheduleStore
import com.moare.android.features.search.display.search.viewmodel.SearchStore
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
import com.moare.android.features.search.models.models.nba.NBAGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType

@Composable
fun NBALeagueScheduleView(
    searchStore: SearchStore,
    store: NBALeagueScheduleStore,
    didPop: Boolean,
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

    LaunchedEffect(didPop) {
        // 뒤로가서 일정화면으로 돌아왔을때 filteredGames update
        if (didPop) {
            // TODO: NBAGameStatsView에서 뒤로왔을때만 실행하게 개선 필요
            store.send(NBALeagueScheduleAction.UpdateFilteredGames)
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
                    store.send(NBALeagueScheduleAction.SelectYearMonth(yearMonth, index))
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
    searchStore: SearchStore,
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
    searchStore: SearchStore,
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

    ScheduleGameItem(
        state = ScheduleGameItemState(
            leagueId = Constants.Ids.NBA,
            game = data,
            teamNameDic = teamNameDic,
            isResultOpened = isResultOpened,
            gameStatusText = gameStatusText,
            gameStatusColor = gameStatusColor,
            isCapsuleButtonDisabled = gameStatus != StringConstants.NBA.GAME_FINAL,
//            gameType = "", // TODO: 아래 playoffs info 주석 참고해서 ScheduleGameItem에 만들어야함
            shouldShowOnlyDateTime = displayModel.scheduleType != ScheduleType.TEAM_FLAT, // (리그, 팀)일정 화면에서만 true
        ),
        actions = ScheduleGameItemActions(
            onGameItemClick = {
                store.send(NBALeagueScheduleAction.SelectGame(data))
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




















