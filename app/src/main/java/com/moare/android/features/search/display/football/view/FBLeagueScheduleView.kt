package com.moare.android.features.search.display.football.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.FBUtil
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
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.models.football.FBGameForSchedule
import com.moare.android.ui.common.components.LeagueTitle

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LeagueTitle(
                            url = league.logo,
                            leagueName = league.name,
                            leagueSeason = league.season
                        )

                        Text(
                            text = " - " + MatchDescriptionConverter.convert(descriptionType = MatchDescriptionConverter.DescriptionType.ROUND_WITHOUT_DASH, input = league.round),
                            fontSize = 14.sp
                        )
                    }
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
    val teamNameDic by store.teamNameDic.collectAsState()

    val gameListToDisplay = filteredGames[selectedDayIndex] ?: emptyList()

    LazyColumn {
        items(gameListToDisplay) { item ->
            FBLeagueScheduleListItem(
                searchStore = searchStore,
                store = store,
                data = item,
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
    // FBLeagueScheduleViewModel이 한번도 초기화 된적 없이 FBGameStatsView에서 함수가 호출될때 teamNameDictionary를 store에서 가져올수가 없어 추가.
    // TODO: 그러면 결국 store는 nullable이어도 된다는건데..?
    teamNameDic: Map<String, String>,
    data: FBGameForSchedule,
) {
    val gameId = data.gameId
    val homeTeamId = data.homeTeamId
    val awayTeamId = data.awayTeamId
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
       constants
       --------------------- */
    val gameStatusText = when (gameStatus) {
        StringConstants.Football.GAME_NOT_STARTED -> StringConstants.GAME_NOT_STARTED_STR
        StringConstants.Football.GAME_FIRST_HALF -> {
            if (gameInfo != null) {
                "전반${gameInfo.elapsed}'"
            } else {
                StringConstants.Football.GAME_FIRST_HALF_STR
            }
        }
        StringConstants.Football.GAME_HALF_TIME -> StringConstants.Football.GAME_HALF_TIME_STR
        StringConstants.Football.GAME_SECOND_HALF -> {
            if (gameInfo != null) {
                "후반${gameInfo.elapsed}'"
            } else {
                StringConstants.Football.GAME_SECOND_HALF_STR
            }
        }
        in StringConstants.Football.GAME_FINISHED_LIST -> if (isResultOpened) StringConstants.GAME_FINISHED_STR else StringConstants.RESULT_OPEN
        else -> ""
    }

    val gameStatusColor = when (gameStatus) {
        in StringConstants.Football.GAME_LIVE_LIST -> MaterialTheme.colors.primary
        else -> Color.Gray
    }

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (StringConstants.Football.GAME_FINISHED_LIST.contains(gameStatus)) {
            gameResultOpenedStateList?.let {
                isResultOpened = gameResultOpenedStateList[gameId] ?: false
            }
        } else if (gameStatus == StringConstants.Football.GAME_NOT_STARTED) {
            isResultOpened = false
        } else {
            isResultOpened = true
        }
    }
    LaunchedEffect(gameResultOpenedStateList) {
        gameResultOpenedStateList?.let {
            if (StringConstants.Football.GAME_FINISHED_LIST.contains(gameStatus)) {
                isResultOpened = gameResultOpenedStateList[gameId] ?: false
            }
        }
    }
//    LaunchedEffect(fbGameStatsModel) {
//        fbGameStatsModel?.let {
//            if (gameStatus != StringConstants.Football.GAME_NOT_STARTED) {
//                isResultOpened = true
//            }
//        }
//    }

    ScheduleGameItem(
        state = ScheduleGameItemState(
            isClickEnabled = if (isFromSchedule) selectedGame == null else false,
            homeTeamLogo = FBUtil.teamLogoUrl(homeTeamId),
            homeTeamName = teamNameDic["short_${homeTeamId}"] ?: "",
            homeTeamScore = data.homeTeamScore,
            awayTeamLogo = FBUtil.teamLogoUrl(awayTeamId),
            awayTeamName = teamNameDic["short_${awayTeamId}"] ?: "",
            awayTeamScore = data.awayTeamScore,
            isResultOpened = isResultOpened,
            gameStatusText = gameStatusText,
            gameStatusColor = gameStatusColor,
            isCapsuleButtonDisabled = (if (isFromSchedule) selectedGame != null else true) || !StringConstants.Football.GAME_FINISHED_LIST.contains(gameStatus),
            date = data.date,
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






















