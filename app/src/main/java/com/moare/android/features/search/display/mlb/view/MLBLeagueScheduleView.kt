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
import com.moare.android.core.util.MLBUtil
import com.moare.android.core.util.NBAUtil
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.display.common.container.state.CalendarUiActions
import com.moare.android.features.search.display.common.container.state.CalendarUiState
import com.moare.android.features.search.display.common.container.state.ScheduleContainerActions
import com.moare.android.features.search.display.common.container.state.ScheduleContainerState
import com.moare.android.features.search.display.common.container.view.ScheduleViewContainer
import com.moare.android.features.search.display.mlb.viewmodel.MLBLeagueScheduleIntent
import com.moare.android.features.search.display.mlb.viewmodel.MLBLeagueScheduleViewModel
import com.moare.android.features.search.display.nba.view.NBALeagueScheduleList
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueScheduleIntent
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueScheduleViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBALeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBGame
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
    val displayModel by mlbLeagueScheduleViewModel.displayModel.collectAsState()
    val yearMonthList by mlbLeagueScheduleViewModel.yearMonthList.collectAsState()
    val days by mlbLeagueScheduleViewModel.days.collectAsState()
    val selectedYearMonthIndex by mlbLeagueScheduleViewModel.selectedYearMonthIndex.collectAsState()
    val selectedDayIndex by mlbLeagueScheduleViewModel.selectedDayIndex.collectAsState()
    val yearMonthCalendarScrollTrigger by mlbLeagueScheduleViewModel.yearMonthCalendarScrollTrigger.collectAsState()
    val dayCalendarScrollTrigger by mlbLeagueScheduleViewModel.dayCalendarScrollTrigger.collectAsState()
    val isAllResultOpened by mlbLeagueScheduleViewModel.isAllResultOpened.collectAsState()
    val displayDataState by mlbLeagueScheduleViewModel.displayDataState.collectAsState()

    val season = displayModel?.games?.firstOrNull()?.game?.season

    val mlbGameStatsData by searchViewModel.mlbGameStatsData.collectAsState()

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
            shouldShowCalendar = mlbGameStatsData == null,
            shouldShowAllResultToggleButton = mlbGameStatsData == null,
            displayDataState = displayDataState,
            shouldFillBelow = mlbGameStatsData == null,
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

    val mlbGameStatsData by searchViewModel.mlbGameStatsData.collectAsState()

    val gameListToDisplay = filteredGames[selectedDayIndex] ?: emptyList()

    LazyColumn {
        items(gameListToDisplay) { item ->
            MLBLeagueScheduleListItem(data = item)
        }
    }
}

@Composable
fun MLBLeagueScheduleListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbLeagueScheduleViewModel: MLBLeagueScheduleViewModel = hiltViewModel(),
    data: MLBGame,
) {
    /* ---------------------
       ui state
       --------------------- */
    var isResultOpened by remember { mutableStateOf(false) }
    val noRippleInteractionSource = remember { MutableInteractionSource() }

    /* ---------------------
       viewmodel state
       --------------------- */
    val gameResultOpenedStateList by mlbLeagueScheduleViewModel.gameResultOpenedStateList.collectAsState()

    val homeTeamId = data.teams.home.id
    val awayTeamId = data.teams.away.id
    val homeTeamScore = data.linescore.teams.home.runs
    val awayTeamScore = data.linescore.teams.away.runs

//    val mlbGameStatsData by searchViewModel.mlbGameStatsData.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val scoreAlpha by animateFloatAsState(
        targetValue = if (StringConstants.MLB.GAME_LIVE_LIST.contains(data.status.codedGameState) || (StringConstants.MLB.GAME_FINISHED_LIST.contains(data.status.codedGameState) && isResultOpened)) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearOutSlowInEasing
        )
    )

    /* ---------------------
       constants
       --------------------- */
    val gameStatusText = if (isResultOpened) {
        when (data.status.codedGameState) {
            "S" -> StringConstants.GAME_NOT_STARTED_STR
            in StringConstants.MLB.GAME_LIVE_LIST -> "${data.linescore.currentInning}${
                if (data.linescore.inningState == "Top") {
                    "초"
                } else {
                    "말"
                }
            }"
            in StringConstants.MLB.GAME_FINISHED_LIST -> StringConstants.GAME_FINISHED_STR
            else -> ""
        }
    } else {
        StringConstants.RESULT_OPEN
    }

    val gameStatusColor = if (isResultOpened) {
        when (data.status.codedGameState) {
            in StringConstants.MLB.GAME_LIVE_LIST -> MaterialTheme.colors.primary
            else -> Color.Gray
        }
    } else {
        Color.Gray
    }

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (StringConstants.MLB.GAME_FINISHED_LIST.contains(data.status.codedGameState)) {
            isResultOpened = gameResultOpenedStateList[data.game.id] ?: false
        } else {
            isResultOpened = true
        }
    }
    LaunchedEffect(gameResultOpenedStateList) {
        if (StringConstants.MLB.GAME_FINISHED_LIST.contains(data.status.codedGameState)) {
            isResultOpened = gameResultOpenedStateList[data.game.id] ?: false
        }
    }

    /* ---------------------
       ui
       --------------------- */
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
//            .clickable(enabled = mlbGameStatsData == null) {
            .clickable {
                searchViewModel.send(SearchViewModel.Intent.SelectMLBGame(data))

                // set selected game's isOpened true
                mlbLeagueScheduleViewModel.send(MLBLeagueScheduleIntent.UpdateResultOpenedState(data.game.id, true))
            }
            .padding(vertical = 8.dp)
            .padding(horizontal = UIConstants.Padding.DEFAULT_H_PADDING)
    ) {
        /* ---------------------
           home
           --------------------- */
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
//                .clickable(enabled = fbGameStatsData != null) {
//                    searchViewModel.send(SearchViewModel.Intent.UpdateTextField(newValue = TextFieldValue(text = "토트넘")))
//                    searchViewModel.send(SearchViewModel.Intent.PerformSearch())
//                }
        ) {
            URLImage(
                url = MLBUtil.teamLogoUrl(homeTeamId),
                size = URLImageSize.SMALL,
                isSvg = true
            )

            Text(
                text = mlbLeagueScheduleViewModel.teamNameDictionary["short_$homeTeamId"] ?: "",
                fontSize = 13.sp,
                maxLines = 2
            )
        }

        // Add space to both sides of each score to place the score in the middle
        Spacer(Modifier.weight(0.3f))

        // score
        Text(
            text = homeTeamScore.toString(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(30.dp)
                .alpha(scoreAlpha),
            color = if (homeTeamScore >= awayTeamScore) MaterialTheme.colors.primary else Color.Black
        )

        Spacer(Modifier.weight(0.3f))

        /* ---------------------
           game info
           --------------------- */
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // game status
            CapsuleButton(
                text = gameStatusText,
                color = gameStatusColor,
                isDisabled = !StringConstants.MLB.GAME_FINISHED_LIST.contains(data.status.codedGameState)
            ) {
                mlbLeagueScheduleViewModel.send(MLBLeagueScheduleIntent.UpdateResultOpenedState(data.game.id, !isResultOpened))
            }

            // game date
            Text(
                text = CalendarUtil.formatDate(data.gameInfo.gameDate, TimeFormatType.AMPM),
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        /* ---------------------
           away
           --------------------- */
        Spacer(Modifier.weight(0.3f))

        // score
        Text(
            text = awayTeamScore.toString(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(30.dp)
                .alpha(scoreAlpha),
            color = if (awayTeamScore >= homeTeamScore) MaterialTheme.colors.primary else Color.Black
        )

        Spacer(Modifier.weight(0.3f))

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
        ) {
            URLImage(
                url = MLBUtil.teamLogoUrl(awayTeamId),
                size = URLImageSize.SMALL,
                isSvg = true
            )

            Text(
                text = mlbLeagueScheduleViewModel.teamNameDictionary["short_$awayTeamId"] ?: "",
                fontSize = 13.sp,
                maxLines = 2
            )
        }
    }
}