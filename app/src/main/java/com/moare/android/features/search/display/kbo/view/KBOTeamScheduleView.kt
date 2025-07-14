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
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamScheduleIntent
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamScheduleViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamScheduleDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOGameForSchedule

@Composable
fun KBOTeamScheduleView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboTeamScheduleViewModel: KBOLeagueScheduleViewModel = hiltViewModel(),
    data: KBOLeagueScheduleDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val isAllResultOpened by kboTeamScheduleViewModel.isAllResultOpened.collectAsState()

    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       etc
       --------------------- */

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.KBOLeagueSchedule) {
            kboTeamScheduleViewModel.send(KBOLeagueScheduleIntent.InitData(data))
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
                kboTeamScheduleViewModel.send(KBOLeagueScheduleIntent.ToggleAllResult)
            }
        ),
        titleContent = {},
        gameListContent = {
            KBOTeamScheduleList()
        }
    )
}

@Composable
fun KBOTeamScheduleList(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboTeamScheduleViewModel: KBOTeamScheduleViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val games by kboTeamScheduleViewModel.games.collectAsState()

    LazyColumn {
        items(games) { item ->
            KBOTeamScheduleListItem(data = item)
        }
    }
}

@Composable
fun KBOTeamScheduleListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboTeamScheduleViewModel: KBOLeagueScheduleViewModel = hiltViewModel(),
    data: KBOGameForSchedule,
) {
    val itemKey = data.itemKey
    val homeTeamId = data.homeTeamId
    val awayTeamId = data.awayTeamId
    val gameStatus = data.gameStatus.toIntOrNull() ?: 0
    val teamNameDic = kboTeamScheduleViewModel.teamNameDictionary

    /* ---------------------
       ui state
       --------------------- */
    var isResultOpened by remember { mutableStateOf(false) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val gameResultOpenedStateList by kboTeamScheduleViewModel.gameResultOpenedStateList.collectAsState()
    val displayModel by kboTeamScheduleViewModel.displayModel.collectAsState()

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

    ScheduleGameItem(
        state = ScheduleGameItemState(
            homeTeamLogo = KBOUtil.teamLogoUrl(homeTeamId),
            homeTeamName = teamNameDic["short_${homeTeamId}"] ?: "",
            homeTeamScore = data.homeTeamScore,
            awayTeamLogo = KBOUtil.teamLogoUrl(awayTeamId),
            awayTeamName = teamNameDic["short_${awayTeamId}"] ?: "",
            awayTeamScore = data.awayTeamScore,
            isResultOpened = isResultOpened,
            gameStatusText = gameStatusText,
            gameStatusColor = gameStatusColor,
            isCapsuleButtonDisabled = gameStatus != StringConstants.KBO.GAME_FINAL,
            date = data.date,
            venue = teamNameDic["venue_${homeTeamId}"] ?: "",
            shouldShowOnlyDateTime = false,
        ),
        actions = ScheduleGameItemActions(
            onGameItemClick = {
                displayModel?.let {
                    searchViewModel.send(SearchViewModel.Intent.SelectKBOGame(data, it.season))
                }

                // set selected game's isOpened true
                kboTeamScheduleViewModel.send(KBOLeagueScheduleIntent.UpdateResultOpenedState(itemKey, true))
            },
            onCapsuleButtonClick = {
                kboTeamScheduleViewModel.send(KBOLeagueScheduleIntent.UpdateResultOpenedState(itemKey, !isResultOpened))
            }
        )
    )
}