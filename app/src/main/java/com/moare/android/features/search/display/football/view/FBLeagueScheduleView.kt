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
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.football.FBGameForSchedule
import com.moare.android.ui.common.components.LeagueTitle

@Composable
fun FBLeagueScheduleView(
    searchStore: SearchViewModel,
    store: FBLeagueScheduleStore
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

    val displayModels by searchStore.displayModels.collectAsState()
    val fbGameStatsModel = displayModels[SportDisplayType.FB_GAME_STATS] as? FBGameStatsDisplayModel

    val viewStack by searchStore.viewStack.collectAsState()

    LaunchedEffect(viewStack) {
        // update games data after refreshing in FBGameStatsView
        if (viewStack.isNotEmpty() && viewStack.last() is SportDecodableModel.FBLeagueSchedule) {
            val fbLeagueSchedule = viewStack.last() as SportDecodableModel.FBLeagueSchedule

//            poppedView?.let {
//                if (it is SportDecodableModel.FBGameStats) {
//                    store.send(FBLeagueScheduleAction.UpdateGamesData(fbLeagueSchedule, it) { data ->
//                        searchStore.send(SearchViewModel.Intent.UpdateLastViewStack(data))
//                    })
//                }
//
//                // 현재 뷰로 뒤로가기를 통해서 왔을때는 달력을 애니메이션 없이 이동
//                // NOTE: - 가장 처음에만 적용 안되는 버그 있음. 그 이후부터는 잘됨.
//                // - NBALeagueScheduleView에서는 작동안해서 일단 다른 뷰에는 추가 안함.
//                shouldAnimateScroll = false
//            }
        }
    }

    ScheduleViewContainer(
        state = ScheduleContainerState(
            shouldShowCalendar = fbGameStatsModel == null,
            shouldShowAllResultToggleButton = fbGameStatsModel == null,
            displayDataState = displayDataState,
            shouldFillBelow = fbGameStatsModel == null,
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

                    store.send(FBLeagueScheduleAction.SelectYearMonth(yearMonth, index) { data ->
                        // 현재 구조 콜백 수정 필요?
                        searchStore.send(SearchViewModel.Intent.UpdateLastViewStack(data))
                    })
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
            fbGameStatsModel?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LeagueTitle(
                        url = it.game.league.logo,
                        leagueName = it.game.league.name,
                        leagueSeason = it.game.league.season
                    )

                    Text(
                        text = " - " + MatchDescriptionConverter.convert(descriptionType = MatchDescriptionConverter.DescriptionType.ROUND_WITHOUT_DASH, input = it.game.league.round),
                        fontSize = 14.sp
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
    searchStore: SearchViewModel,
    store: FBLeagueScheduleStore
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val filteredGames by store.filteredGames.collectAsState()
    val selectedDayIndex by store.selectedDayIndex.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val displayModels by searchStore.displayModels.collectAsState()
    val fbGameStatsModel = displayModels[SportDisplayType.FB_GAME_STATS] as? FBGameStatsDisplayModel

    val gameListToDisplay = if (fbGameStatsModel == null) filteredGames[selectedDayIndex] ?: emptyList() else listOf(ModelConverter().fbGameToGameScheduleConverter(fbGameStatsModel.game))

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
    searchStore: SearchViewModel,
    store: FBLeagueScheduleStore,
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
    val displayModel by store.displayModel.collectAsState()
    val gameResultOpenedStateList by store.gameResultOpenedStateList.collectAsState()

    val displayModels by searchStore.displayModels.collectAsState()
    val fbGameStatsModel = displayModels[SportDisplayType.FB_GAME_STATS] as? FBGameStatsDisplayModel

    /* ---------------------
       constants
       --------------------- */
    val gameStatusText = when (gameStatus) {
        StringConstants.Football.GAME_NOT_STARTED -> StringConstants.GAME_NOT_STARTED_STR
        StringConstants.Football.GAME_FIRST_HALF -> {
            if (gameInfo != null) {
                "전반${gameInfo.status.elapsed}'"
            } else {
                StringConstants.Football.GAME_FIRST_HALF_STR
            }
        }
        StringConstants.Football.GAME_HALF_TIME -> StringConstants.Football.GAME_HALF_TIME_STR
        StringConstants.Football.GAME_SECOND_HALF -> {
            if (gameInfo != null) {
                "후반${gameInfo.status.elapsed}'"
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
            isResultOpened = gameResultOpenedStateList[gameId] ?: false
        } else if (gameStatus == StringConstants.Football.GAME_NOT_STARTED) {
            isResultOpened = false
        } else {
            isResultOpened = true
        }
    }
    LaunchedEffect(gameResultOpenedStateList) {
        if (StringConstants.Football.GAME_FINISHED_LIST.contains(gameStatus)) {
            isResultOpened = gameResultOpenedStateList[gameId] ?: false
        }
    }
    LaunchedEffect(fbGameStatsModel) {
        fbGameStatsModel?.let {
            if (gameStatus != StringConstants.Football.GAME_NOT_STARTED) {
                isResultOpened = true
            }
        }
    }

    ScheduleGameItem(
        state = ScheduleGameItemState(
            isClickEnabled = fbGameStatsModel == null,
            homeTeamLogo = FBUtil.teamLogoUrl(homeTeamId),
            homeTeamName = teamNameDic["short_${homeTeamId}"] ?: "",
            homeTeamScore = data.homeTeamScore,
            awayTeamLogo = FBUtil.teamLogoUrl(awayTeamId),
            awayTeamName = teamNameDic["short_${awayTeamId}"] ?: "",
            awayTeamScore = data.awayTeamScore,
            isResultOpened = isResultOpened,
            gameStatusText = gameStatusText,
            gameStatusColor = gameStatusColor,
            isCapsuleButtonDisabled = fbGameStatsModel != null || !StringConstants.Football.GAME_FINISHED_LIST.contains(gameStatus),
            date = data.date,
            venue = teamNameDic["venue_${homeTeamId}"] ?: (fbGameStatsModel?.game?.fixture?.venue?.name ?: ""),
            gameType = MatchDescriptionConverter.convert(input = data.gameInfo?.round ?: ""),
            shouldShowOnlyDateTime = fbGameStatsModel == null,
            shouldShowVenue = fbGameStatsModel != null,
            shouldShowGameType = fbGameStatsModel == null,
            shouldShowHomeLabel = fbGameStatsModel != null,
            shouldShowAwayLabel = fbGameStatsModel != null
        ),
        actions = ScheduleGameItemActions(
            onGameItemClick = {
                searchStore.send(SearchViewModel.Intent.SelectFBGame(data, displayModel.season, displayModel.leagueId))

                // set selected game's isOpened true
                store.send(FBLeagueScheduleAction.UpdateResultOpenedState(gameId, true))
            },
            onCapsuleButtonClick = {
                store.send(FBLeagueScheduleAction.UpdateResultOpenedState(gameId, !isResultOpened))
            }
        )
    )
}






















