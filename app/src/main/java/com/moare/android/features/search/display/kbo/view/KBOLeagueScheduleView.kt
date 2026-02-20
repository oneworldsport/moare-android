package com.moare.android.features.search.display.kbo.view

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
import com.moare.android.core.util.KBOUtil
import com.moare.android.features.search.display.common.container.component.ScheduleGameItem
import com.moare.android.features.search.display.common.container.state.CalendarUiActions
import com.moare.android.features.search.display.common.container.state.CalendarUiState
import com.moare.android.features.search.display.common.container.state.ScheduleContainerActions
import com.moare.android.features.search.display.common.container.state.ScheduleContainerState
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemActions
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemState
import com.moare.android.features.search.display.common.container.view.ScheduleViewContainer
import com.moare.android.features.search.display.football.viewmodel.FBLeagueScheduleAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOLeagueScheduleAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOLeagueScheduleStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBLeagueScheduleAction
import com.moare.android.features.search.display.search.viewmodel.SearchStore
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.search.models.displaymodels.kbo.KBOGameStatsDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import com.moare.android.ui.util.Refreshable

@Composable
fun KBOLeagueScheduleView(
    searchStore: SearchStore,
    store: KBOLeagueScheduleStore,
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
            // TODO: KBOGameStatsView에서 뒤로왔을때만 실행하게 개선 필요
            store.send(KBOLeagueScheduleAction.UpdateFilteredGames)
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
                    store.send(KBOLeagueScheduleAction.SelectYearMonth(yearMonth, index))
                },
                onSelectDay = { day, index ->
                    store.send(KBOLeagueScheduleAction.SelectDay(day, index))
                }
            ),
            allResultButtonAction = {
                store.send(KBOLeagueScheduleAction.ToggleAllResult)
            },
            tournamentOrteamStandingsButtonAction = {
                store.send(KBOLeagueScheduleAction.ShowTeamStandings)
            },
            tournamentButtonAction = {
                store.send(KBOLeagueScheduleAction.ShowTournament)
            }
        ),
        titleContent = {},
        gameListContent = {
            KBOLeagueScheduleList(searchStore = searchStore, store = store)
        }
    )
}

@Composable
fun KBOLeagueScheduleList(
    searchStore: SearchStore,
    store: KBOLeagueScheduleStore
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val filteredGames by store.filteredGames.collectAsState()
    val selectedDayIndex by store.selectedDayIndex.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()
    val isRefreshing by store.isRefreshing.collectAsState()

    val gameListToDisplay = filteredGames[selectedDayIndex] ?: emptyList()
    val hasLive = gameListToDisplay.any { game ->
        game.gameStatus == Constants.GameStatus.KBO.LIVE
    }

    Refreshable(
        enabled = hasLive,
        isRefreshing = isRefreshing,
        onRefresh = {
            store.send(KBOLeagueScheduleAction.RefreshGames)
        }
    ) {
        LazyColumn {
            items(gameListToDisplay) { item ->
                KBOLeagueScheduleListItem(
                    searchStore = searchStore,
                    store = store,
                    data = item,
                    teamNameDic = teamNameDic
                )
            }
        }
    }
}

@Composable
fun KBOLeagueScheduleListItem(
    searchStore: SearchStore,
    store: KBOLeagueScheduleStore,
    teamNameDic: Map<String, String>,
    data: KBOGameForSchedule,
) {
    val itemKey = data.itemKey
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
        StringConstants.KBO.GAME_SCHEDULED -> StringConstants.GAME_NOT_STARTED_STR
        StringConstants.KBO.GAME_LIVE -> data.gameInfo?.currentInning ?: StringConstants.GAME_LIVE_STR
        StringConstants.KBO.GAME_FINAL -> if (isResultOpened) StringConstants.GAME_FINISHED_STR else StringConstants.RESULT_OPEN
        StringConstants.KBO.GAME_CANCELED -> StringConstants.GAME_CANCELED_STR
        else -> ""
    }

    val gameStatusColor = if (gameStatus == StringConstants.KBO.GAME_LIVE) {
        MaterialTheme.colors.primary
    } else {
        Color.Gray
    }

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (gameStatus == StringConstants.KBO.GAME_FINAL) {
            isResultOpened = gameResultOpenedStateList[itemKey] ?: false
        } else if (gameStatus == StringConstants.KBO.GAME_SCHEDULED || gameStatus == StringConstants.KBO.GAME_CANCELED) {
            isResultOpened = false
        } else {
            isResultOpened = true
        }
    }
    LaunchedEffect(gameResultOpenedStateList) {
        if (gameStatus == StringConstants.KBO.GAME_FINAL) {
            isResultOpened = gameResultOpenedStateList[itemKey] ?: false
        }
    }

    ScheduleGameItem(
        state = ScheduleGameItemState(
            leagueId = Constants.Ids.KBO,
            game = data,
            teamNameDic = teamNameDic,
            isClickEnabled = data.gameStatus != Constants.GameStatus.KBO.CANCELED, // 취소된 경기는 클릭 안되게
            isResultOpened = isResultOpened,
            gameStatusText = gameStatusText,
            gameStatusColor = gameStatusColor,
            isCapsuleButtonDisabled = gameStatus != StringConstants.KBO.GAME_FINAL,
            gameType = data.gameInfo?.seriesDescription,
            shouldShowOnlyDateTime = displayModel.scheduleType != ScheduleType.TEAM_FLAT, // (리그, 팀)일정 화면에서만 true
        ),
        actions = ScheduleGameItemActions(
            onGameItemClick = {
                store.send(KBOLeagueScheduleAction.SelectGame(data))
            },
            onCapsuleButtonClick = {
                store.send(KBOLeagueScheduleAction.UpdateResultOpenedState(itemKey, !isResultOpened))
            }
        )
    )
}