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
import com.moare.android.features.search.display.mlb.viewmodel.MLBLeagueScheduleIntent
import com.moare.android.features.search.display.mlb.viewmodel.MLBLeagueScheduleViewModel
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamScheduleIntent
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamScheduleViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamScheduleDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBGameForSchedule

@Composable
fun MLBTeamScheduleView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbTeamScheduleViewModel: MLBTeamScheduleViewModel = hiltViewModel(),
    data: MLBTeamScheduleDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val isAllResultOpened by mlbTeamScheduleViewModel.isAllResultOpened.collectAsState()

    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       etc
       --------------------- */

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.MLBTeamSchedule) {
            mlbTeamScheduleViewModel.send(MLBTeamScheduleIntent.InitData(data))
        }
    }

    ScheduleViewContainer(
        state = ScheduleContainerState(
            shouldShowCalendar = false,
            shouldFetchSchedule = false,
            isAllResultOpened = isAllResultOpened
        ),
        actions = ScheduleContainerActions(
            allResultButtonAction = {
                mlbTeamScheduleViewModel.send(MLBTeamScheduleIntent.ToggleAllResult)
            }
        ),
        titleContent = {},
        gameListContent = {
            MLBTeamScheduleList()
        }
    )
}

@Composable
fun MLBTeamScheduleList(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbTeamScheduleViewModel: MLBTeamScheduleViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val games by mlbTeamScheduleViewModel.games.collectAsState()

    LazyColumn {
        items(games) { item ->
            MLBTeamScheduleListItem(data = item)
        }
    }
}

@Composable
fun MLBTeamScheduleListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbTeamScheduleViewModel: MLBTeamScheduleViewModel = hiltViewModel(),
    data: MLBGameForSchedule,
) {
    val gameId = data.gameId
    val homeTeamId = data.homeTeamId
    val awayTeamId = data.awayTeamId
    val gameStatus = data.gameStatus
    val teamNameDic = mlbTeamScheduleViewModel.teamNameDictionary

    /* ---------------------
       ui state
       --------------------- */
    var isResultOpened by remember { mutableStateOf(false) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val gameResultOpenedStateList by mlbTeamScheduleViewModel.gameResultOpenedStateList.collectAsState()

    /* ---------------------
       constants
       --------------------- */
    val gameStatusText = when (gameStatus) {
        StringConstants.MLB.GAME_SCHEDULED -> StringConstants.GAME_NOT_STARTED_STR
        StringConstants.MLB.GAME_LIVE -> StringConstants.GAME_LIVE_STR
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

    ScheduleGameItem(
        state = ScheduleGameItemState(
            homeTeamLogo = MLBUtil.teamLogoUrl(homeTeamId),
            homeTeamName = teamNameDic["short_${homeTeamId}"] ?: "",
            homeTeamScore = data.homeTeamScore,
            awayTeamLogo = MLBUtil.teamLogoUrl(awayTeamId),
            awayTeamName = teamNameDic["short_${awayTeamId}"] ?: "",
            awayTeamScore = data.awayTeamScore,
            isResultOpened = isResultOpened,
            gameStatusText = gameStatusText,
            gameStatusColor = gameStatusColor,
            isCapsuleButtonDisabled = !StringConstants.MLB.GAME_FINISHED_LIST.contains(gameStatus),
            date = data.date,
            venue = teamNameDic["venue_${homeTeamId}"] ?: "",
            shouldShowOnlyDateTime = false,
            isSvgLogo = true
        ),
        actions = ScheduleGameItemActions(
            onGameItemClick = {
                searchViewModel.send(SearchViewModel.Intent.SelectMLBGame(data))

                // set selected game's isOpened true
                mlbTeamScheduleViewModel.send(MLBTeamScheduleIntent.UpdateResultOpenedState(gameId, true))
            },
            onCapsuleButtonClick = {
                mlbTeamScheduleViewModel.send(MLBTeamScheduleIntent.UpdateResultOpenedState(gameId, !isResultOpened))
            }
        )
    )
}