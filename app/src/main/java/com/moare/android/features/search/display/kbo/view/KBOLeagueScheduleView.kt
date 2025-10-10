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
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.moare.android.features.search.display.kbo.viewmodel.KBOLeagueScheduleAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOLeagueScheduleStore
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.search.models.displaymodels.kbo.KBOGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOLeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOGameForSchedule

@Composable
fun KBOLeagueScheduleView(
    searchStore: SearchViewModel,
    store: KBOLeagueScheduleStore
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val yearMonthList by store.yearMonthList.collectAsState()
    val days by store.days.collectAsState()
    val selectedYearMonthIndex by store.selectedYearMonthIndex.collectAsState()
    val selectedDayIndex by store.selectedDayIndex.collectAsState()
    val yearMonthCalendarScrollTrigger by store.yearMonthCalendarScrollTrigger.collectAsState()
    val dayCalendarScrollTrigger by store.dayCalendarScrollTrigger.collectAsState()
    val isAllResultOpened by store.isAllResultOpened.collectAsState()
    val displayDataState by store.displayDataState.collectAsState()

    val viewStack by searchStore.viewStack.collectAsState()

    LaunchedEffect(viewStack) {
        // update games data after refreshing in KBOGameStatsView
        if (viewStack.isNotEmpty() && viewStack.last() is SportDecodableModel.KBOLeagueSchedule) {
            val kboLeagueSchedule = viewStack.last() as SportDecodableModel.KBOLeagueSchedule

//            poppedView?.let {
//                if (it is SportDecodableModel.KBOGameStats) {
//                    store.send(KBOLeagueScheduleAction.UpdateGamesData(kboLeagueSchedule, it) { data ->
//                        searchStore.send(SearchViewModel.Intent.UpdateLastViewStack(data))
//                    })
//                }
//            }
        }
    }

    ScheduleViewContainer(
        state = ScheduleContainerState(
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
                    store.send(KBOLeagueScheduleAction.SelectYearMonth(yearMonth, index) { data ->
                        searchStore.send(SearchViewModel.Intent.UpdateLastViewStack(data))
                    })
                },
                onSelectDay = { day, index ->
                    store.send(KBOLeagueScheduleAction.SelectDay(day, index))
                }
            ),
            allResultButtonAction = {
                store.send(KBOLeagueScheduleAction.ToggleAllResult)
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
    searchStore: SearchViewModel,
    store: KBOLeagueScheduleStore
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
            KBOLeagueScheduleListItem(
                searchStore = searchStore,
                store = store,
                data = item,
                teamNameDic = teamNameDic
            )
        }
    }
}

@Composable
fun KBOLeagueScheduleListItem(
    searchStore: SearchViewModel,
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

    val displayModels by searchStore.displayModels.collectAsState()
    val kboGameStatsModel = displayModels[SportDisplayType.KBO_GAME_STATS] as? KBOGameStatsDisplayModel

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
    LaunchedEffect(kboGameStatsModel) {
        kboGameStatsModel?.let {
            if (gameStatus == StringConstants.KBO.GAME_LIVE || gameStatus == StringConstants.KBO.GAME_FINAL) {
                isResultOpened = true
            }
        }
    }

    ScheduleGameItem(
        state = ScheduleGameItemState(
            isClickEnabled = kboGameStatsModel == null,
            homeTeamLogo = KBOUtil.teamLogoUrl(homeTeamId),
            homeTeamName = teamNameDic["short_${homeTeamId}"] ?: "",
            homeTeamScore = data.homeTeamScore,
            awayTeamLogo = KBOUtil.teamLogoUrl(awayTeamId),
            awayTeamName = teamNameDic["short_${awayTeamId}"] ?: "",
            awayTeamScore = data.awayTeamScore,
            isResultOpened = isResultOpened,
            gameStatusText = gameStatusText,
            gameStatusColor = gameStatusColor,
            isCapsuleButtonDisabled = kboGameStatsModel != null || gameStatus != StringConstants.KBO.GAME_FINAL,
            date = data.date,
            venue = teamNameDic["venue_${homeTeamId}"] ?: "",
            shouldShowOnlyDateTime = kboGameStatsModel == null,
            shouldShowVenue = kboGameStatsModel != null,
            shouldShowHomeLabel = kboGameStatsModel != null,
            shouldShowAwayLabel = kboGameStatsModel != null,
        ),
        actions = ScheduleGameItemActions(
            onGameItemClick = {
                searchStore.send(SearchViewModel.Intent.SelectKBOGame(data, displayModel.season))

                // set selected game's isOpened true
                store.send(KBOLeagueScheduleAction.UpdateResultOpenedState(itemKey, true))
            },
            onCapsuleButtonClick = {
                store.send(KBOLeagueScheduleAction.UpdateResultOpenedState(itemKey, !isResultOpened))
            }
        )
    )
}