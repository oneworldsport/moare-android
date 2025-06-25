package com.moare.android.features.search.display.football.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.EnNameTranslationUtils
import com.moare.android.core.util.FBUtil
import com.moare.android.core.util.MatchDescriptionConverter
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.display.common.container.component.ScheduleGameItem
import com.moare.android.features.search.display.common.container.state.ScheduleContainerActions
import com.moare.android.features.search.display.common.container.state.ScheduleContainerState
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemActions
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemState
import com.moare.android.features.search.display.common.container.view.ScheduleViewContainer
import com.moare.android.features.search.display.football.viewmodel.FBTeamScheduleIntent
import com.moare.android.features.search.display.football.viewmodel.FBTeamScheduleViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamScheduleDisplayModel
import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.models.models.football.FBGameForSchedule
import com.moare.android.ui.common.components.LeagueTitle

@Composable
fun FBTeamScheduleView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbTeamScheduleViewModel: FBTeamScheduleViewModel = hiltViewModel(),
    data: FBTeamScheduleDisplayModel
) {
    /* ---------------------
       constants
       --------------------- */

    /* ---------------------
       ui state
       --------------------- */

    /* ---------------------
       viewmodel state
       --------------------- */
    val isAllResultOpened by fbTeamScheduleViewModel.isAllResultOpened.collectAsState()

    val displayModels by searchViewModel.displayModels.collectAsState()
    val fbGameStatsModel = displayModels[SportDisplayType.FB_GAME_STATS] as? FBGameStatsDisplayModel

    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       etc
       --------------------- */

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.FBTeamSchedule) {
            fbTeamScheduleViewModel.send(FBTeamScheduleIntent.InitData(data))
        }
    }

    ScheduleViewContainer(
        state = ScheduleContainerState(
            shouldShowCalendar = false,
            shouldShowAllResultToggleButton = fbGameStatsModel == null,
            shouldFetchSchedule = false,
            shouldFillBelow = fbGameStatsModel == null,
            isAllResultOpened = isAllResultOpened
        ),
        actions = ScheduleContainerActions(
            allResultButtonAction = {
                fbTeamScheduleViewModel.send(FBTeamScheduleIntent.ToggleAllResult)
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
            FBTeamScheduleList()
        }
    )
}

@Composable
fun FBTeamScheduleList(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbTeamScheduleViewModel: FBTeamScheduleViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val games by fbTeamScheduleViewModel.games.collectAsState()

    val displayModels by searchViewModel.displayModels.collectAsState()
    val fbGameStatsModel = displayModels[SportDisplayType.FB_GAME_STATS] as? FBGameStatsDisplayModel

    val gameListToDisplay = if (fbGameStatsModel == null) games else listOf(ModelConverter().fbGameToGameScheduleConverter(fbGameStatsModel.game))

    LazyColumn {
        items(gameListToDisplay) { item ->
            FBTeamScheduleListItem(data = item)
        }
    }
}

@Composable
fun FBTeamScheduleListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbTeamScheduleViewModel: FBTeamScheduleViewModel = hiltViewModel(),
    data: FBGameForSchedule,
) {
    val gameId = data.gameId
    val homeTeamId = data.homeTeamId
    val awayTeamId = data.awayTeamId
    val gameStatus = data.gameStatus
    val teamNameDic = fbTeamScheduleViewModel.teamNameDictionary

    /* ---------------------
       ui state
       --------------------- */
    var isResultOpened by remember { mutableStateOf(false) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by fbTeamScheduleViewModel.displayModel.collectAsState()
    val gameResultOpenedStateList by fbTeamScheduleViewModel.gameResultOpenedStateList.collectAsState()

    val displayModels by searchViewModel.displayModels.collectAsState()
    val fbGameStatsModel = displayModels[SportDisplayType.FB_GAME_STATS] as? FBGameStatsDisplayModel

    /* ---------------------
       constants
       --------------------- */
    val gameStatusText = when (gameStatus) {
        StringConstants.Football.GAME_NOT_STARTED -> StringConstants.GAME_NOT_STARTED_STR
        StringConstants.Football.GAME_FIRST_HALF -> StringConstants.Football.GAME_FIRST_HALF_STR
        StringConstants.Football.GAME_HALF_TIME -> StringConstants.Football.GAME_HALF_TIME_STR
        StringConstants.Football.GAME_SECOND_HALF -> StringConstants.Football.GAME_SECOND_HALF_STR
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
            isResultOpened = true
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
            referee = fbGameStatsModel?.game?.fixture?.referee,
            shouldShowOnlyDateTime = false,
            shouldShowVenue = fbGameStatsModel != null,
            shouldShowGameType = fbGameStatsModel == null,
            shouldShowReferee = fbGameStatsModel != null,
            shouldShowHomeLabel = fbGameStatsModel != null,
            shouldShowAwayLabel = fbGameStatsModel != null
        ),
        actions = ScheduleGameItemActions(
            onGameItemClick = {
                displayModel?.let {
                    searchViewModel.send(SearchViewModel.Intent.SelectFBGame(data, it.leagueId))
                }

                // set selected game's isOpened true
                fbTeamScheduleViewModel.send(
                    FBTeamScheduleIntent.UpdateResultOpenedState(
                        data.gameId,
                        true
                    )
                )
            },
            onCapsuleButtonClick = {
                fbTeamScheduleViewModel.send(FBTeamScheduleIntent.UpdateResultOpenedState(data.gameId, !isResultOpened))
            }
        )
    )
}