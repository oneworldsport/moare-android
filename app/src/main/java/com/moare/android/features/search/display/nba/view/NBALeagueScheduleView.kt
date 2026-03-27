package com.moare.android.features.search.display.nba.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.InputTimeFormatType
import com.moare.android.core.util.NBAUtil
import com.moare.android.core.util.OutputTimeFormatType
import com.moare.android.features.search.display.common.container.component.ScheduleGameItem
import com.moare.android.features.search.display.common.container.state.CalendarUiActions
import com.moare.android.features.search.display.common.container.state.CalendarUiState
import com.moare.android.features.search.display.common.container.state.ScheduleContainerActions
import com.moare.android.features.search.display.common.container.state.ScheduleContainerState
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemActions
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemState
import com.moare.android.features.search.display.common.container.view.ScheduleViewContainer
import com.moare.android.features.search.display.nba.store.NBALeagueScheduleAction
import com.moare.android.features.search.display.nba.store.NBALeagueScheduleStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.models.models.nba.NBAGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import com.moare.android.ui.common.components.GameStatusContext
import com.moare.android.ui.util.Refreshable
import kotlinx.coroutines.flow.distinctUntilChanged

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
    val selectedMonth by store.selectedMonth.collectAsState()
    val days by store.days.collectAsState()
    val selectedYearMonthIndex by store.selectedYearMonthIndex.collectAsState()
    val selectedDayIndex by store.selectedDayIndex.collectAsState()
    val yearMonthCalendarScrollTrigger by store.yearMonthCalendarScrollTrigger.collectAsState()
    val dayCalendarScrollTrigger by store.dayCalendarScrollTrigger.collectAsState()
    val isAllResultOpened by store.isAllResultOpened.collectAsState()
    val displayDataState by store.displayDataState.collectAsState()
    val selectedYearMonth by store.selectedYearMonth.collectAsState()

    val tournamentStartDateYearMonth = CalendarUtil.formatDate(
        date = displayModel.tournamentStartDate,
        inputFormatType = InputTimeFormatType.DATE_ONLY,
        outputFormatType = OutputTimeFormatType.YEAR_MONTH
    )

    val tournamentStartDateYearMonthInt =
        tournamentStartDateYearMonth.replace("/", "").toIntOrNull() ?: 0

    val selectedYearMonthInt = selectedYearMonth.replace("/", "").toIntOrNull() ?: 0

    LaunchedEffect(didPop) {
        // 뒤로가서 일정화면으로 돌아왔을때 filteredGames update
        if (didPop) {
            // TODO: NBAGameStatsView에서 뒤로왔을때만 실행하게 개선 필요
            store.send(NBALeagueScheduleAction.UpdateFilteredGames)
        }
    }

    ScheduleViewContainer(
        state = ScheduleContainerState(
            leagueId = displayModel.leagueId,
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
            isAllResultOpened = isAllResultOpened,
            shouldShowTournamentButton = (displayModel.tournamentStartDate != null) && (tournamentStartDateYearMonthInt <= selectedYearMonthInt)
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
            },
            tournamentOrteamStandingsButtonAction = {
                store.send(NBALeagueScheduleAction.ShowTeamStandings)
            },
            tournamentButtonAction = {
                store.send(NBALeagueScheduleAction.ShowTournament)
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
    val filteredGames by store.filteredGames.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()
    val isRefreshing by store.isRefreshing.collectAsState()
    val days by store.days.collectAsState()
    val selectedDay by store.selectedDay.collectAsState()

    val validDays = remember(days) { days.filter { !it.isDataEmpty } }
    val validIndexByDay = remember(validDays) {
        validDays.mapIndexed { index, info -> info.day to index }.toMap()
    }
    val pagerState = rememberPagerState(
        initialPage = validIndexByDay[selectedDay?.day] ?: 0,
        pageCount = { validDays.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        snapshotFlow { pagerState.currentPage to pagerState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { (page, inProgress) ->
                // 스크롤 중이면 무시, 끝났을 때만 확정
                // animateScrollToPage로 해당 함수가 실행될때, 한번에 여러 페이지를 이동하는 경우에 설정된 page까지 이동하기 전에 해당 함수가 실행되는 경우가 있어서 추가.
                if (inProgress) return@collect

                val dayToSelect = validDays.getOrNull(page) ?: return@collect
                val dayIndexToSelect = dayToSelect.day - 1
                if (selectedDay?.day != dayToSelect.day) {
                    store.send(NBALeagueScheduleAction.SelectDay(dayToSelect, dayIndexToSelect))
                }
            }
    }

    LaunchedEffect(selectedDay) {
        val targetIndex = validIndexByDay[selectedDay?.day] ?: return@LaunchedEffect
        if (targetIndex != pagerState.currentPage) {
            pagerState.animateScrollToPage(targetIndex)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Top
    ) { page ->
        val dayIndex = (validDays.getOrNull(page)?.day ?: 1) - 1
        val gameListToDisplay = filteredGames[dayIndex] ?: emptyList()
        val hasLive = gameListToDisplay.any { game ->
            game.gameStatus == Constants.GameStatus.NBA.LIVE.toString()
        }

        Refreshable(
            enabled = hasLive,
            isRefreshing = isRefreshing,
            onRefresh = {
                store.send(NBALeagueScheduleAction.RefreshGames)
            }
        ) {
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
    val gameStatus = data.gameStatus.toIntOrNull() ?: 1

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
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (gameStatus == Constants.GameStatus.NBA.FINISHED) {
            isResultOpened = gameResultOpenedStateList[gameId] ?: false
        } else if (gameStatus == Constants.GameStatus.NBA.NOT_STARTED) {
            isResultOpened = false
        } else {
            isResultOpened = true
        }
    }
    LaunchedEffect(gameResultOpenedStateList) {
        if (gameStatus == Constants.GameStatus.NBA.FINISHED) {
            isResultOpened = gameResultOpenedStateList[gameId] ?: false
        }
    }

    ScheduleGameItem(
        state = ScheduleGameItemState(
            leagueId = Constants.Ids.NBA,
            game = data,
            teamNameDic = teamNameDic,
            isResultOpened = isResultOpened,
            gameStatusContext = GameStatusContext.Nba(status = gameStatus, period = data.gameInfo?.period, isResultOpened = isResultOpened),
            isCapsuleButtonDisabled = gameStatus != Constants.GameStatus.NBA.FINISHED,
            gameType = NBAUtil.gameType(data.gameInfo), // TODO: 아래 playoffs info 주석 참고해서 ScheduleGameItem에 만들어야함
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




















