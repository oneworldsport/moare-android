package com.moare.android.features.search.display.mlb.view

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
import com.moare.android.features.search.display.common.container.component.ScheduleGameItem
import com.moare.android.features.search.display.common.container.state.CalendarUiActions
import com.moare.android.features.search.display.common.container.state.CalendarUiState
import com.moare.android.features.search.display.common.container.state.ScheduleContainerActions
import com.moare.android.features.search.display.common.container.state.ScheduleContainerState
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemActions
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemState
import com.moare.android.features.search.display.common.container.view.ScheduleViewContainer
import com.moare.android.features.search.display.mlb.store.MLBLeagueScheduleAction
import com.moare.android.features.search.display.mlb.store.MLBLeagueScheduleStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.models.models.mlb.MLBGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import com.moare.android.ui.common.components.GameStatusContext
import com.moare.android.ui.util.Refreshable
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun MLBLeagueScheduleView(
    searchStore: SearchStore,
    store: MLBLeagueScheduleStore,
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

    LaunchedEffect(didPop) {
        // 뒤로가서 일정화면으로 돌아왔을때 filteredGames update
        if (didPop) {
            // TODO: MLBGameStatsView에서 뒤로왔을때만 실행하게 개선 필요
            store.send(MLBLeagueScheduleAction.UpdateFilteredGames)
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
            shouldShowTournamentButton = selectedMonth >= 10
        ),
        actions = ScheduleContainerActions(
            calendarUiActions = CalendarUiActions(
                onSelectYearMonth = { yearMonth, index ->
                    store.send(MLBLeagueScheduleAction.SelectYearMonth(yearMonth, index))
                },
                onSelectDay = { day, index ->
                    store.send(MLBLeagueScheduleAction.SelectDay(day, index))
                }
            ),
            allResultButtonAction = {
                store.send(MLBLeagueScheduleAction.ToggleAllResult)
            },
            tournamentOrteamStandingsButtonAction = {
                store.send(MLBLeagueScheduleAction.ShowTeamStandings)
            },
            tournamentButtonAction = {
                store.send(MLBLeagueScheduleAction.ShowTournament)
            }
        ),
        titleContent = {},
        gameListContent = {
            MLBLeagueScheduleList(searchStore = searchStore, store = store)
        }
    )
}

@Composable
fun MLBLeagueScheduleList(
    searchStore: SearchStore,
    store: MLBLeagueScheduleStore
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
                    store.send(MLBLeagueScheduleAction.SelectDay(dayToSelect, dayIndexToSelect))
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
            game.gameStatus == Constants.GameStatus.MLB.LIVE
        }

        Refreshable(
            enabled = hasLive,
            isRefreshing = isRefreshing,
            onRefresh = {
                store.send(MLBLeagueScheduleAction.RefreshGames)
            }
        ) {
            LazyColumn {
                items(gameListToDisplay) { item ->
                    MLBLeagueScheduleListItem(
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

// NOTE: 경기 시작 전 게임의 경우 MLBGameStatsView에서 MLBGameStatsScoreInfoItem를 사용하지 않고 MLBLeagueScheduleListItem를 사용함에 따라
// FBLeagueScheduleListItem에서처럼 nbaGameStatsModel을 가져와 사용하는 로직이 일부 추가됨. KBO, NBA에서도 마찬가지.
// 계속 MLBGameStatsScoreInfoItem를 사용할거는 아니기때문에(축구처럼 애니메이션 적용을 위해) 나중에 바뀔 여지 있음.
@Composable
fun MLBLeagueScheduleListItem(
    searchStore: SearchStore,
    store: MLBLeagueScheduleStore,
    teamNameDic: Map<String, String>,
    data: MLBGameForSchedule,
) {
    val gameId = data.gameId
    val homeTeamId = data.homeTeamId
    val awayTeamId = data.awayTeamId
    val gameStatus = data.gameStatus

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
        if (StringConstants.MLB.GAME_FINISHED_LIST.contains(gameStatus)) {
            isResultOpened = gameResultOpenedStateList[gameId] ?: false
        } else if (gameStatus == StringConstants.MLB.GAME_SCHEDULED || gameStatus == StringConstants.MLB.GAME_POSTPONED) {
            isResultOpened = false
        } else {
            isResultOpened = true
        }
    }
    LaunchedEffect(gameResultOpenedStateList) {
        if (StringConstants.MLB.GAME_FINISHED_LIST.contains(gameStatus)) {
            isResultOpened = gameResultOpenedStateList[gameId] ?: false
        }
    }

    ScheduleGameItem(
        state = ScheduleGameItemState(
            leagueId = Constants.Ids.MLB,
            game = data,
            teamNameDic = teamNameDic,
            isClickEnabled = gameStatus != Constants.GameStatus.MLB.POSTPONED, // 연기된 경기는 클릭 안되게
            isResultOpened = isResultOpened,
            gameStatusContext = GameStatusContext.Mlb(status = gameStatus, currentInning = data.gameInfo?.currentInning, isResultOpened = isResultOpened),
            isCapsuleButtonDisabled = !StringConstants.MLB.GAME_FINISHED_LIST.contains(gameStatus),
            gameType = data.gameInfo?.seriesDescription,
            shouldShowOnlyDateTime = displayModel.scheduleType != ScheduleType.TEAM_FLAT, // (리그, 팀)일정 화면에서만 true
        ),
        actions = ScheduleGameItemActions(
            onGameItemClick = {
                store.send(MLBLeagueScheduleAction.SelectGame(data))
            },
            onCapsuleButtonClick = {
                store.send(MLBLeagueScheduleAction.UpdateResultOpenedState(gameId, !isResultOpened))
            }
        )
    )
}













