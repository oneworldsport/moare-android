package com.moare.android.features.search.display.football.view

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
import com.moare.android.core.util.MatchDescriptionConverter
import com.moare.android.features.search.display.common.container.component.ScheduleGameItem
import com.moare.android.features.search.display.common.container.state.CalendarUiActions
import com.moare.android.features.search.display.common.container.state.CalendarUiState
import com.moare.android.features.search.display.common.container.state.ScheduleContainerActions
import com.moare.android.features.search.display.common.container.state.ScheduleContainerState
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemActions
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemState
import com.moare.android.features.search.display.common.container.view.ScheduleViewContainer
import com.moare.android.features.search.display.football.store.FBLeagueScheduleAction
import com.moare.android.features.search.display.football.store.FBLeagueScheduleStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.models.models.football.FBGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import com.moare.android.ui.common.components.FBLeagueTitleForGameStats
import com.moare.android.ui.common.components.GameStatusContext
import com.moare.android.ui.util.Refreshable
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun FBLeagueScheduleView(
    searchStore: SearchStore,
    store: FBLeagueScheduleStore,
    didPop: Boolean,
    isCombinedView: Boolean = false
) {
    var shouldAnimateScroll by remember { mutableStateOf(true) }

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
    val selectedGame by store.selectedGame.collectAsState()
    val selectedMonth by store.selectedMonth.collectAsState()
    val league by store.league.collectAsState()
    val leagueId = displayModel.leagueId

    LaunchedEffect(didPop) {
        // 뒤로가서 일정화면으로 돌아왔을때 filteredGames update
        if (!isCombinedView && didPop) {
            // TODO: FBGameStatsView에서 뒤로왔을때만 실행하게 개선 필요
            store.send(FBLeagueScheduleAction.UpdateFilteredGames)
        }
    }

    ScheduleViewContainer(
        state = ScheduleContainerState(
            leagueId = leagueId,
            shouldShowCalendar = (displayModel.scheduleType != ScheduleType.TEAM_FLAT) && ( selectedGame == null),
            shouldShowAllResultToggleButton = selectedGame == null,
            shouldFetchSchedule = displayModel.scheduleType == ScheduleType.LEAGUE,
            displayDataState = displayDataState,
            shouldFillBelow = selectedGame == null,
            calendarUiState = CalendarUiState(
                yearMonthList,
                days,
                selectedYearMonthIndex,
                selectedDayIndex,
                yearMonthCalendarScrollTrigger,
                dayCalendarScrollTrigger,
                shouldAnimateScroll
            ),
            isAllResultOpened = isAllResultOpened,
            shouldShowTournamentButton = (leagueId == Constants.Ids.MLS) && (selectedMonth >= 10),
        ),
        actions = ScheduleContainerActions(
            calendarUiActions = CalendarUiActions(
                onSelectYearMonth = { yearMonth, index ->
                    shouldAnimateScroll = true

                    store.send(FBLeagueScheduleAction.SelectYearMonth(yearMonth, index))
                },
                onSelectDay = { day, index ->
                    store.send(FBLeagueScheduleAction.SelectDay(day, index))
                }
            ),
            allResultButtonAction = {
                store.send(FBLeagueScheduleAction.ToggleAllResult)
            },
            tournamentOrteamStandingsButtonAction = {
                if (Constants.Ids.FOOTBALL_DRAW_TOURNAMENT_LEAGUES.contains(leagueId)) {
                    store.send(FBLeagueScheduleAction.ShowTournament)
                } else {
                    store.send(FBLeagueScheduleAction.ShowTeamStandings)
                }
            },
            tournamentButtonAction = {
                store.send(FBLeagueScheduleAction.ShowTournament)
            }
        ),
        titleContent = {
            selectedGame?.let {
                league?.let { league ->
                    FBLeagueTitleForGameStats(
                        url = league.logo,
                        leagueName = league.name,
                        leagueSeason = league.season,
                        description = league.round
                    )
                }
            }
        },
        gameListContent = {
            FBLeagueScheduleList(searchStore = searchStore, store = store)
        }
    )
}

@Composable
fun FBLeagueScheduleList(
    searchStore: SearchStore,
    store: FBLeagueScheduleStore
) {
    val filteredGames by store.filteredGames.collectAsState()
    val displayModel by store.displayModel.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()
    val isRefreshing by store.isRefreshing.collectAsState()
    val days by store.days.collectAsState()
    val selectedDay by store.selectedDay.collectAsState()
    val selectedGame by store.selectedGame.collectAsState()

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
                if (inProgress) return@collect

                val dayToSelect = validDays.getOrNull(page) ?: return@collect
                val dayIndexToSelect = dayToSelect.day - 1
                if (selectedDay?.day != dayToSelect.day) {
                    store.send(FBLeagueScheduleAction.SelectDay(dayToSelect, dayIndexToSelect))
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
        modifier = if (selectedGame == null) {
            Modifier.fillMaxSize()
        } else {
            Modifier
        },
        verticalAlignment = Alignment.Top,
        userScrollEnabled = selectedGame == null
    ) { page ->
        val dayIndex = (validDays.getOrNull(page)?.day ?: 1) - 1
        val gameListToDisplay = filteredGames[dayIndex] ?: emptyList()
        val hasLive = gameListToDisplay.any { game ->
            game.gameStatus in Constants.GameStatus.Football.LIVE_LIST
        }

        Refreshable(
            enabled = hasLive,
            isRefreshing = isRefreshing,
            onRefresh = {
                store.send(FBLeagueScheduleAction.RefreshGames)
            }
        ) {
            LazyColumn {
                items(gameListToDisplay) { item ->
                    FBLeagueScheduleListItem(
                        searchStore = searchStore,
                        store = store,
                        data = item,
                        leagueId = displayModel.leagueId,
                        teamNameDic = teamNameDic
                    )
                }
            }
        }
    }
}

@Composable
fun FBLeagueScheduleListItem(
    searchStore: SearchStore,
    store: FBLeagueScheduleStore?,
    leagueId: Int,
    // FBLeagueScheduleViewModel이 한번도 초기화 된적 없이 FBGameStatsView에서 함수가 호출될때 teamNameDictionary를 store에서 가져올수가 없어 추가.
    teamNameDic: Map<String, String>,
    data: FBGameForSchedule,
) {
    val gameId = data.gameId
    val gameStatus = data.gameStatus
    val gameInfo = data.gameInfo

    /* ---------------------
       ui state
       --------------------- */
    var isResultOpened by remember { mutableStateOf(false) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel = store?.displayModel?.collectAsState()?.value
    val gameResultOpenedStateList = store?.gameResultOpenedStateList?.collectAsState()?.value
    val selectedGame = store?.selectedGame?.collectAsState()?.value

    val isFromSchedule = store != null

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (store == null) {
            isResultOpened = true
        } else {
            if (Constants.GameStatus.Football.FINISHED_LIST.contains(gameStatus)) {
                gameResultOpenedStateList?.let {
                    isResultOpened = gameResultOpenedStateList[gameId] ?: false
                }
            } else if (gameStatus == Constants.GameStatus.Football.NOT_STARTED ||
                gameStatus == Constants.GameStatus.Football.CANCELLED ||
                gameStatus == Constants.GameStatus.Football.POSTPONED
            ) {
                isResultOpened = false
            } else {
                isResultOpened = true
            }
        }
    }
    LaunchedEffect(gameResultOpenedStateList) {
        gameResultOpenedStateList?.let {
            if (Constants.GameStatus.Football.FINISHED_LIST.contains(gameStatus)) {
                isResultOpened = gameResultOpenedStateList[gameId] ?: false
            }
        }
    }

    ScheduleGameItem(
        state = ScheduleGameItemState(
            leagueId = leagueId,
            game = data,
            teamNameDic = teamNameDic,
            isClickEnabled = if (isFromSchedule) selectedGame == null else false,
            isResultOpened = isResultOpened,
            gameStatusContext = GameStatusContext.Football(status = data.gameStatus, elapsed = data.gameInfo?.status?.elapsed, extra = data.gameInfo?.status?._extra, isResultOpened = isResultOpened),
            isCapsuleButtonDisabled = (if (isFromSchedule) selectedGame != null else true) || !Constants.GameStatus.Football.FINISHED_LIST.contains(gameStatus),
            gameType = MatchDescriptionConverter.convert(input = data.gameInfo?.round ?: ""),
            shouldShowOnlyDateTime = if (isFromSchedule) {
                (displayModel?.scheduleType != ScheduleType.TEAM_FLAT) && (selectedGame == null)
            } else false,
            shouldShowGameType = if (isFromSchedule) selectedGame == null else false,
            shouldShowHomeLabel = if (isFromSchedule) selectedGame != null else true,
            shouldShowAwayLabel = if (isFromSchedule) selectedGame != null else true
        ),
        actions = ScheduleGameItemActions(
            onGameItemClick = {
                store?.send(FBLeagueScheduleAction.SelectGame(data))
            },
            onCapsuleButtonClick = {
                store?.send(FBLeagueScheduleAction.UpdateResultOpenedState(gameId, !isResultOpened))
            }
        )
    )
}






















