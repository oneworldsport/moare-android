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
import com.moare.android.features.search.display.kbo.viewmodel.KBOLeagueScheduleIntent
import com.moare.android.features.search.display.kbo.viewmodel.KBOLeagueScheduleViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.search.models.displaymodels.kbo.KBOGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBGameStatsDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOGameForSchedule

@Composable
fun KBOLeagueScheduleView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboLeagueScheduleViewModel: KBOLeagueScheduleViewModel = hiltViewModel(),
    data: KBOLeagueScheduleDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val yearMonthList by kboLeagueScheduleViewModel.yearMonthList.collectAsState()
    val days by kboLeagueScheduleViewModel.days.collectAsState()
    val selectedYearMonthIndex by kboLeagueScheduleViewModel.selectedYearMonthIndex.collectAsState()
    val selectedDayIndex by kboLeagueScheduleViewModel.selectedDayIndex.collectAsState()
    val yearMonthCalendarScrollTrigger by kboLeagueScheduleViewModel.yearMonthCalendarScrollTrigger.collectAsState()
    val dayCalendarScrollTrigger by kboLeagueScheduleViewModel.dayCalendarScrollTrigger.collectAsState()
    val isAllResultOpened by kboLeagueScheduleViewModel.isAllResultOpened.collectAsState()
    val displayDataState by kboLeagueScheduleViewModel.displayDataState.collectAsState()

    val viewStack by searchViewModel.viewStack.collectAsState()
    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       etc
       --------------------- */

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.KBOLeagueSchedule) {
            kboLeagueScheduleViewModel.send(KBOLeagueScheduleIntent.InitData(data))
        }
    }

    LaunchedEffect(viewStack) {
        // update games data after refreshing in KBOGameStatsView
        if (viewStack.isNotEmpty() && viewStack.last() is SportDecodableModel.KBOLeagueSchedule) {
            val kboLeagueSchedule = viewStack.last() as SportDecodableModel.KBOLeagueSchedule

            poppedView?.let {
                if (it is SportDecodableModel.KBOGameStats) {
                    kboLeagueScheduleViewModel.send(KBOLeagueScheduleIntent.UpdateGamesData(kboLeagueSchedule, it) { data ->
                        searchViewModel.send(SearchViewModel.Intent.UpdateLastViewStack(data))
                    })
                }
            }
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
                    kboLeagueScheduleViewModel.send(KBOLeagueScheduleIntent.SelectYearMonth(yearMonth, index) { data ->
                        searchViewModel.send(SearchViewModel.Intent.UpdateLastViewStack(data))
                    })
                },
                onSelectDay = { day, index ->
                    kboLeagueScheduleViewModel.send(KBOLeagueScheduleIntent.SelectDay(day, index))
                }
            ),
            allResultButtonAction = {
                kboLeagueScheduleViewModel.send(KBOLeagueScheduleIntent.ToggleAllResult)
            }
        ),
        titleContent = {},
        gameListContent = {
            KBOLeagueScheduleList()
        }
    )
}

@Composable
fun KBOLeagueScheduleList(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboLeagueScheduleViewModel: KBOLeagueScheduleViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val filteredGames by kboLeagueScheduleViewModel.filteredGames.collectAsState()
    val selectedDayIndex by kboLeagueScheduleViewModel.selectedDayIndex.collectAsState()
    val teamNameDic = kboLeagueScheduleViewModel.teamNameDictionary

    val gameListToDisplay = filteredGames[selectedDayIndex] ?: emptyList()

    LazyColumn {
        items(gameListToDisplay) { item ->
            KBOLeagueScheduleListItem(data = item, teamNameDic = teamNameDic)
        }
    }
}

@Composable
fun KBOLeagueScheduleListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboLeagueScheduleViewModel: KBOLeagueScheduleViewModel = hiltViewModel(),
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
    val gameResultOpenedStateList by kboLeagueScheduleViewModel.gameResultOpenedStateList.collectAsState()
    val displayModel by kboLeagueScheduleViewModel.displayModel.collectAsState()

    val displayModels by searchViewModel.displayModels.collectAsState()
    val kboGameStatsModel = displayModels[SportDisplayType.KBO_GAME_STATS] as? KBOGameStatsDisplayModel

    /* ---------------------
       constants
       --------------------- */
    val gameStatusText = when (gameStatus) {
        StringConstants.KBO.GAME_SCHEDULED -> StringConstants.GAME_NOT_STARTED_STR
        StringConstants.KBO.GAME_LIVE -> StringConstants.GAME_LIVE_STR
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
                displayModel?.let {
                    searchViewModel.send(SearchViewModel.Intent.SelectKBOGame(data, it.season))
                }

                // set selected game's isOpened true
                kboLeagueScheduleViewModel.send(KBOLeagueScheduleIntent.UpdateResultOpenedState(itemKey, true))
            },
            onCapsuleButtonClick = {
                kboLeagueScheduleViewModel.send(KBOLeagueScheduleIntent.UpdateResultOpenedState(itemKey, !isResultOpened))
            }
        )
    )
}