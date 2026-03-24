package com.moare.android.features.search.display.tennis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.moare.android.core.constants.Constants
import com.moare.android.core.util.NBAUtil
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
import com.moare.android.features.search.display.tennis.store.TennisLeagueScheduleAction
import com.moare.android.features.search.display.tennis.store.TennisLeagueScheduleStore
import com.moare.android.features.search.display.tennis.store.TennisTournamentStore
import com.moare.android.features.search.models.models.nba.NBAGameForSchedule
import com.moare.android.features.search.models.models.tennis.TennisGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import com.moare.android.ui.common.components.GameStatusContext
import com.moare.android.ui.common.components.TennisTournamentTitle
import com.moare.android.ui.util.Refreshable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.collections.get

@Composable
fun TennisLeagueScheduleView(
    searchStore: SearchStore,
    store: TennisLeagueScheduleStore,
    didPop: Boolean
) {
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
    val selectedRelatedLeagueIndex by store.selectedRelatedLeagueIndex.collectAsState()

    LaunchedEffect(didPop) {
        // 뒤로가서 일정화면으로 돌아왔을때 filteredGames update
//        if (didPop) {
//            store.send(NBALeagueScheduleAction.UpdateFilteredGames)
//        }
    }

    ScheduleViewContainer(
        state = ScheduleContainerState(
            leagueId = displayModel.leagueId,
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
            shouldShowTournamentOrTeamStandingsButton = false,
            startDate = displayModel.startDate,
            endDate = displayModel.endDate,
            relatedLeagues = displayModel.relatedLeagueKrname,
            selectedRelatedLeagueIndex = selectedRelatedLeagueIndex
        ),
        actions = ScheduleContainerActions(
            calendarUiActions = CalendarUiActions(
                onSelectYearMonth = { yearMonth, index ->
                    store.send(TennisLeagueScheduleAction.SelectYearMonth(yearMonth, index))
                },
                onSelectDay = { day, index ->
                    store.send(TennisLeagueScheduleAction.SelectDay(day, index))
                }
            ),
            allResultButtonAction = {
                store.send(TennisLeagueScheduleAction.ToggleAllResult)
            },
            tournamentOrteamStandingsButtonAction = {
                store.send(TennisLeagueScheduleAction.ShowTournament)
            },
            relatedLeagueButtonAction = { index ->
                store.send(TennisLeagueScheduleAction.SelectRelatedLeague(index))
            }
        ),
        titleContent = {
            TennisTournamentTitle(displayModel.leagueId, displayModel.season)
        },
        gameListContent = {
            TennisLeagueScheduleList(searchStore = searchStore, store = store)
        }
    )
}

@Composable
fun TennisLeagueScheduleList(
    searchStore: SearchStore,
    store: TennisLeagueScheduleStore
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
                    store.send(TennisLeagueScheduleAction.SelectDay(dayToSelect, dayIndexToSelect))
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
            Constants.GameStatus.Tennis.LIVE_LIST.contains(game.gameStatus.toIntOrNull() ?: 0)
        }

        Refreshable(
            enabled = hasLive,
            isRefreshing = isRefreshing,
            onRefresh = {
                store.send(TennisLeagueScheduleAction.RefreshGames)
            }
        ) {
            LazyColumn {
                items(gameListToDisplay) { item ->
                    TennisLeagueScheduleListItem(
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
fun TennisLeagueScheduleListItem(
    searchStore: SearchStore,
    store: TennisLeagueScheduleStore,
    teamNameDic: Map<String, String>,
    data: TennisGameForSchedule,
) {
    val gameResultOpenedStateList by store.gameResultOpenedStateList.collectAsState()
    val displayModel by store.displayModel.collectAsState()

    val gameId = data.gameId
    val gameStatus = data.gameStatus.toIntOrNull() ?: 0
    val leagueId = displayModel.leagueId

    var isResultOpened by remember { mutableStateOf(false) }

    LaunchedEffect(data) {
        if (gameStatus in Constants.GameStatus.Tennis.FINISHED_LIST) {
            isResultOpened = gameResultOpenedStateList[gameId] ?: false
        } else if (gameStatus != Constants.GameStatus.Tennis.NOT_STARTED) {
            isResultOpened = false
        } else {
            isResultOpened = true
        }
    }
    LaunchedEffect(gameResultOpenedStateList) {
        if (gameStatus in Constants.GameStatus.Tennis.FINISHED_LIST) {
            isResultOpened = gameResultOpenedStateList[gameId] ?: false
        }
    }

    ScheduleGameItem(
        state = ScheduleGameItemState(
            leagueId = leagueId,
            game = data,
            teamNameDic = teamNameDic,
            isResultOpened = isResultOpened,
            gameStatusContext = GameStatusContext.Tennis(status = gameStatus, isResultOpened = isResultOpened),
            isCapsuleButtonDisabled = !Constants.GameStatus.Tennis.FINISHED_LIST.contains(gameStatus),
            gameType = data.gameInfo?.roundInfo?.name,
            shouldShowWinner = data.gameInfo?.isGameFinished ?: false,
            isHomeWinner = data.gameInfo?.isHomeWinner ?: true
        ),
        actions = ScheduleGameItemActions(
            onGameItemClick = {
                store.send(TennisLeagueScheduleAction.SelectGame(data))
            },
            onCapsuleButtonClick = {
                store.send(TennisLeagueScheduleAction.UpdateResultOpenedState(gameId, !isResultOpened))
            }
        )
    )
}