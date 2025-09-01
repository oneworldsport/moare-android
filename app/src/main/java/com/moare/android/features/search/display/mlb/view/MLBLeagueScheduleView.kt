package com.moare.android.features.search.display.mlb.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.constants.UIConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.FBUtil
import com.moare.android.core.util.MLBUtil
import com.moare.android.core.util.NBAUtil
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.display.common.container.component.ScheduleGameItem
import com.moare.android.features.search.display.common.container.state.CalendarUiActions
import com.moare.android.features.search.display.common.container.state.CalendarUiState
import com.moare.android.features.search.display.common.container.state.ScheduleContainerActions
import com.moare.android.features.search.display.common.container.state.ScheduleContainerState
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemActions
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemState
import com.moare.android.features.search.display.common.container.view.ScheduleViewContainer
import com.moare.android.features.search.display.mlb.viewmodel.MLBLeagueScheduleIntent
import com.moare.android.features.search.display.mlb.viewmodel.MLBLeagueScheduleViewModel
import com.moare.android.features.search.display.nba.view.NBALeagueScheduleList
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueScheduleIntent
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueScheduleViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBALeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBGame
import com.moare.android.features.search.models.models.mlb.MLBGameForSchedule
import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.ui.common.components.CalendarList
import com.moare.android.ui.common.components.CalendarType
import com.moare.android.ui.common.components.CapsuleButton
import com.moare.android.ui.common.components.ProgressIndicator
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterRow

@Composable
fun MLBLeagueScheduleView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbLeagueScheduleViewModel: MLBLeagueScheduleViewModel = hiltViewModel(),
    data: MLBLeagueScheduleDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val yearMonthList by mlbLeagueScheduleViewModel.yearMonthList.collectAsState()
    val days by mlbLeagueScheduleViewModel.days.collectAsState()
    val selectedYearMonthIndex by mlbLeagueScheduleViewModel.selectedYearMonthIndex.collectAsState()
    val selectedDayIndex by mlbLeagueScheduleViewModel.selectedDayIndex.collectAsState()
    val yearMonthCalendarScrollTrigger by mlbLeagueScheduleViewModel.yearMonthCalendarScrollTrigger.collectAsState()
    val dayCalendarScrollTrigger by mlbLeagueScheduleViewModel.dayCalendarScrollTrigger.collectAsState()
    val isAllResultOpened by mlbLeagueScheduleViewModel.isAllResultOpened.collectAsState()
    val displayDataState by mlbLeagueScheduleViewModel.displayDataState.collectAsState()

    val viewStack by searchViewModel.viewStack.collectAsState()
    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       etc
       --------------------- */

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.MLBLeagueSchedule) {
            mlbLeagueScheduleViewModel.send(MLBLeagueScheduleIntent.InitData(data))
        }
    }

    LaunchedEffect(viewStack) {
        // update games data after refreshing in MLBGameStatsView
        if (viewStack.isNotEmpty() && viewStack.last() is SportDecodableModel.MLBLeagueSchedule) {
            val mlbLeagueSchedule = viewStack.last() as SportDecodableModel.MLBLeagueSchedule

            poppedView?.let {
                if (it is SportDecodableModel.MLBGameStats) {
                    mlbLeagueScheduleViewModel.send(MLBLeagueScheduleIntent.UpdateGamesData(mlbLeagueSchedule, it) { data ->
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
                    mlbLeagueScheduleViewModel.send(MLBLeagueScheduleIntent.SelectYearMonth(yearMonth, index) { data ->
                        searchViewModel.send(SearchViewModel.Intent.UpdateLastViewStack(data))
                    })
                },
                onSelectDay = { day, index ->
                    mlbLeagueScheduleViewModel.send(MLBLeagueScheduleIntent.SelectDay(day, index))
                }
            ),
            allResultButtonAction = {
                mlbLeagueScheduleViewModel.send(MLBLeagueScheduleIntent.ToggleAllResult)
            }
        ),
        titleContent = {},
        gameListContent = {
            MLBLeagueScheduleList()
        }
    )
}

@Composable
fun MLBLeagueScheduleList(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbLeagueScheduleViewModel: MLBLeagueScheduleViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val filteredGames by mlbLeagueScheduleViewModel.filteredGames.collectAsState()
    val selectedDayIndex by mlbLeagueScheduleViewModel.selectedDayIndex.collectAsState()
    val teamNameDic = mlbLeagueScheduleViewModel.teamNameDictionary

    val gameListToDisplay = filteredGames[selectedDayIndex] ?: emptyList()

    LazyColumn {
        items(gameListToDisplay) { item ->
            MLBLeagueScheduleListItem(data = item, teamNameDic = teamNameDic)
        }
    }
}

// NOTE: 경기 시작 전 게임의 경우 MLBGameStatsView에서 MLBGameStatsScoreInfoItem를 사용하지 않고 MLBLeagueScheduleListItem를 사용함에 따라
// FBLeagueScheduleListItem에서처럼 nbaGameStatsModel을 가져와 사용하는 로직이 일부 추가됨. KBO, NBA에서도 마찬가지.
// 계속 MLBGameStatsScoreInfoItem를 사용할거는 아니기때문에(축구처럼 애니메이션 적용을 위해) 나중에 바뀔 여지 있음.
@Composable
fun MLBLeagueScheduleListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbLeagueScheduleViewModel: MLBLeagueScheduleViewModel = hiltViewModel(),
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
    val gameResultOpenedStateList by mlbLeagueScheduleViewModel.gameResultOpenedStateList.collectAsState()
    val displayModel by mlbLeagueScheduleViewModel.displayModel.collectAsState()

    val displayModels by searchViewModel.displayModels.collectAsState()
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
                displayModel?.let {
                    searchViewModel.send(SearchViewModel.Intent.SelectMLBGame(data, it.season))
                }

                // set selected game's isOpened true
                mlbLeagueScheduleViewModel.send(MLBLeagueScheduleIntent.UpdateResultOpenedState(gameId, true))
            },
            onCapsuleButtonClick = {
                mlbLeagueScheduleViewModel.send(MLBLeagueScheduleIntent.UpdateResultOpenedState(gameId, !isResultOpened))
            }
        )
    )
}