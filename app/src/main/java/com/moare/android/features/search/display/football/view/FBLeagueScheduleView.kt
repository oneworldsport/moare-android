package com.moare.android.features.search.display.football.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.MatchDescriptionConverter
import com.moare.android.features.search.display.common.container.component.ScheduleGameItem
import com.moare.android.features.search.display.common.container.state.CalendarUiActions
import com.moare.android.features.search.display.common.container.state.CalendarUiState
import com.moare.android.features.search.display.common.container.state.ScheduleContainerActions
import com.moare.android.features.search.display.common.container.state.ScheduleContainerState
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemActions
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemState
import com.moare.android.features.search.display.common.container.view.ScheduleViewContainer
import com.moare.android.features.search.display.football.viewmodel.FBLeagueScheduleAction
import com.moare.android.features.search.display.football.viewmodel.FBLeagueScheduleStore
import com.moare.android.features.search.display.search.viewmodel.SearchStore
import com.moare.android.features.search.models.models.football.FBGameForSchedule
import com.moare.android.ui.common.components.FBLeagueTitle
import com.moare.android.ui.common.components.FBLeagueTitleForGameStats

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
    val league by store.league.collectAsState()

    LaunchedEffect(didPop) {
        // 뒤로가서 일정화면으로 돌아왔을때 filteredGames update
        if (!isCombinedView && didPop) {
            // TODO: FBGameStatsView에서 뒤로왔을때만 실행하게 개선 필요
            store.send(FBLeagueScheduleAction.UpdateFilteredGames)
        }
    }

    ScheduleViewContainer(
        state = ScheduleContainerState(
            leagueId = displayModel.leagueId,
            shouldShowCalendar = selectedGame == null,
            shouldShowAllResultToggleButton = selectedGame == null,
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
            isAllResultOpened = isAllResultOpened
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
    /* ---------------------
       viewmodel state
       --------------------- */
    val filteredGames by store.filteredGames.collectAsState()
    val selectedDayIndex by store.selectedDayIndex.collectAsState()
    val displayModel by store.displayModel.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val gameListToDisplay = filteredGames[selectedDayIndex] ?: emptyList()

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
//        for (value in gameListToDisplay) {
//            FBLeagueScheduleItem(data = value)
//        }
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
            } else if (gameStatus == Constants.GameStatus.Football.NOT_STARTED) {
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
            gameStatusText = Constants.GameStatus.fbGameStatusText(data.gameStatus, gameInfo?.elapsed, isResultOpened),
            gameStatusColor = Constants.GameStatus.gameStatusColor(leagueId, data.gameStatus),
            isCapsuleButtonDisabled = (if (isFromSchedule) selectedGame != null else true) || !StringConstants.Football.GAME_FINISHED_LIST.contains(gameStatus),
            gameType = MatchDescriptionConverter.convert(input = data.gameInfo?.round ?: ""),
            shouldShowOnlyDateTime = if (isFromSchedule) selectedGame == null else false,
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






















