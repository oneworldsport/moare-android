package com.moare.android.features.search.display.mlb.view

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
import com.moare.android.core.util.MLBUtil
import com.moare.android.features.search.display.common.container.component.ScheduleGameItem
import com.moare.android.features.search.display.common.container.state.CalendarUiActions
import com.moare.android.features.search.display.common.container.state.CalendarUiState
import com.moare.android.features.search.display.common.container.state.ScheduleContainerActions
import com.moare.android.features.search.display.common.container.state.ScheduleContainerState
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemActions
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemState
import com.moare.android.features.search.display.common.container.view.ScheduleViewContainer
import com.moare.android.features.search.display.mlb.viewmodel.MLBLeagueScheduleAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBLeagueScheduleStore
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.search.models.displaymodels.mlb.MLBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBGameForSchedule

@Composable
fun MLBLeagueScheduleView(
    searchStore: SearchViewModel,
    store: MLBLeagueScheduleStore
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
        // update games data after refreshing in MLBGameStatsView
        if (viewStack.isNotEmpty() && viewStack.last() is SportDecodableModel.MLBLeagueSchedule) {
            val mlbLeagueSchedule = viewStack.last() as SportDecodableModel.MLBLeagueSchedule

//            poppedView?.let {
//                if (it is SportDecodableModel.MLBGameStats) {
//                    store.send(MLBLeagueScheduleAction.UpdateGamesData(mlbLeagueSchedule, it) { data ->
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
                    store.send(MLBLeagueScheduleAction.SelectYearMonth(yearMonth, index) { data ->
                        searchStore.send(SearchViewModel.Intent.UpdateLastViewStack(data))
                    })
                },
                onSelectDay = { day, index ->
                    store.send(MLBLeagueScheduleAction.SelectDay(day, index))
                }
            ),
            allResultButtonAction = {
                store.send(MLBLeagueScheduleAction.ToggleAllResult)
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
    searchStore: SearchViewModel,
    store: MLBLeagueScheduleStore
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
            MLBLeagueScheduleListItem(
                searchStore = searchStore,
                store = store,
                data = item,
                teamNameDic = teamNameDic
            )
        }
    }
}

// NOTE: 경기 시작 전 게임의 경우 MLBGameStatsView에서 MLBGameStatsScoreInfoItem를 사용하지 않고 MLBLeagueScheduleListItem를 사용함에 따라
// FBLeagueScheduleListItem에서처럼 nbaGameStatsModel을 가져와 사용하는 로직이 일부 추가됨. KBO, NBA에서도 마찬가지.
// 계속 MLBGameStatsScoreInfoItem를 사용할거는 아니기때문에(축구처럼 애니메이션 적용을 위해) 나중에 바뀔 여지 있음.
@Composable
fun MLBLeagueScheduleListItem(
    searchStore: SearchViewModel,
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

    val displayModels by searchStore.displayModels.collectAsState()
    val mlbGameStatsModel = displayModels[SportDisplayType.MLB_GAME_STATS] as? MLBGameStatsDisplayModel

    /* ---------------------
       constants
       --------------------- */
    val gameStatusText = when (gameStatus) {
        StringConstants.MLB.GAME_SCHEDULED -> StringConstants.GAME_NOT_STARTED_STR
        StringConstants.MLB.GAME_LIVE -> data.gameInfo?.currentInning ?: StringConstants.GAME_LIVE_STR
        StringConstants.MLB.GAME_POSTPONED -> StringConstants.GAME_POSTPONED_STR
        in StringConstants.MLB.GAME_FINISHED_LIST -> if (isResultOpened) StringConstants.GAME_FINISHED_STR else StringConstants.RESULT_OPEN
        else -> ""
    }

    val gameStatusColor = if (gameStatus == StringConstants.MLB.GAME_LIVE) {
        MaterialTheme.colors.primary
    } else {
        Color.Gray
    }

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
    LaunchedEffect(mlbGameStatsModel) {
        mlbGameStatsModel?.let {
            if (gameStatus != StringConstants.MLB.GAME_SCHEDULED) {
                isResultOpened = true
            }
        }
    }

    ScheduleGameItem(
        state = ScheduleGameItemState(
            isClickEnabled = mlbGameStatsModel == null,
            homeTeamLogo = MLBUtil.teamLogoUrl(homeTeamId),
            homeTeamName = teamNameDic["short_${homeTeamId}"] ?: "",
            homeTeamScore = data.homeTeamScore,
            awayTeamLogo = MLBUtil.teamLogoUrl(awayTeamId),
            awayTeamName = teamNameDic["short_${awayTeamId}"] ?: "",
            awayTeamScore = data.awayTeamScore,
            isResultOpened = isResultOpened,
            gameStatusText = gameStatusText,
            gameStatusColor = gameStatusColor,
            isCapsuleButtonDisabled = mlbGameStatsModel != null || !StringConstants.MLB.GAME_FINISHED_LIST.contains(gameStatus),
            date = data.date,
            venue = teamNameDic["venue_${homeTeamId}"] ?: "",
            shouldShowOnlyDateTime = mlbGameStatsModel == null,
            shouldShowVenue = mlbGameStatsModel != null,
            shouldShowHomeLabel = mlbGameStatsModel != null,
            shouldShowAwayLabel = mlbGameStatsModel != null,
            isSvgLogo = true
        ),
        actions = ScheduleGameItemActions(
            onGameItemClick = {
                searchStore.send(SearchViewModel.Intent.SelectMLBGame(data, displayModel.season))

                // set selected game's isOpened true
                store.send(MLBLeagueScheduleAction.UpdateResultOpenedState(gameId, true))
            },
            onCapsuleButtonClick = {
                store.send(MLBLeagueScheduleAction.UpdateResultOpenedState(gameId, !isResultOpened))
            }
        )
    )
}