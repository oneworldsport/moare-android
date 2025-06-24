package com.moare.android.features.search.display.football.view

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.constants.UIConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.EnNameTranslationUtils
import com.moare.android.core.util.MatchDescriptionConverter
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.display.common.container.component.ScheduleGameItemContainer
import com.moare.android.features.search.display.common.container.state.CalendarUiActions
import com.moare.android.features.search.display.common.container.state.CalendarUiState
import com.moare.android.features.search.display.common.container.state.ScheduleContainerActions
import com.moare.android.features.search.display.common.container.state.ScheduleContainerState
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemActions
import com.moare.android.features.search.display.common.container.state.ScheduleGameItemState
import com.moare.android.features.search.display.common.container.view.ScheduleViewContainer
import com.moare.android.features.search.display.football.viewmodel.FBLeagueScheduleIntent
import com.moare.android.features.search.display.football.viewmodel.FBLeagueScheduleViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.ui.common.components.CalendarList
import com.moare.android.ui.common.components.CalendarType
import com.moare.android.ui.common.components.CapsuleButton
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.ProgressIndicator
import com.moare.android.ui.common.components.RoundedBorderText
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.theme.Moare

@Composable
fun FBLeagueScheduleView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbLeagueScheduleViewModel: FBLeagueScheduleViewModel = hiltViewModel(),
    data: FBLeagueScheduleDisplayModel,
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
    val yearMonthList by fbLeagueScheduleViewModel.yearMonthList.collectAsState()
    val days by fbLeagueScheduleViewModel.days.collectAsState()
    val selectedYearMonthIndex by fbLeagueScheduleViewModel.selectedYearMonthIndex.collectAsState()
    val selectedDayIndex by fbLeagueScheduleViewModel.selectedDayIndex.collectAsState()
    val yearMonthCalendarScrollTrigger by fbLeagueScheduleViewModel.yearMonthCalendarScrollTrigger.collectAsState()
    val dayCalendarScrollTrigger by fbLeagueScheduleViewModel.dayCalendarScrollTrigger.collectAsState()
    val isAllResultOpened by fbLeagueScheduleViewModel.isAllResultOpened.collectAsState()
    val displayDataState by fbLeagueScheduleViewModel.displayDataState.collectAsState()

    val displayModels by searchViewModel.displayModels.collectAsState()
    val fbGameStatsModel = displayModels[SportDisplayType.FB_GAME_STATS] as? FBGameStatsDisplayModel

    val viewStack by searchViewModel.viewStack.collectAsState()
    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       etc
       --------------------- */

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.FBLeagueSchedule) {
            fbLeagueScheduleViewModel.send(FBLeagueScheduleIntent.InitData(data))
        }
    }

    LaunchedEffect(viewStack) {
        // update games data after refreshing in FBGameStatsView
        if (viewStack.isNotEmpty() && viewStack.last() is SportDecodableModel.FBLeagueSchedule) {
            val fbLeagueSchedule = viewStack.last() as SportDecodableModel.FBLeagueSchedule

            poppedView?.let {
                if (it is SportDecodableModel.FBGameStats) {
                    fbLeagueScheduleViewModel.send(FBLeagueScheduleIntent.UpdateGamesData(fbLeagueSchedule, it) { data ->
                        searchViewModel.send(SearchViewModel.Intent.UpdateLastViewStack(data))
                    })
                }
            }
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
                dayCalendarScrollTrigger
            ),
            isAllResultOpened = isAllResultOpened
        ),
        actions = ScheduleContainerActions(
            calendarUiActions = CalendarUiActions(
                onSelectYearMonth = { yearMonth, index ->
                    fbLeagueScheduleViewModel.send(FBLeagueScheduleIntent.SelectYearMonth(yearMonth, index) { data ->
                        // 현재 구조 콜백 수정 필요?
                        searchViewModel.send(SearchViewModel.Intent.UpdateLastViewStack(data))
                    })
                },
                onSelectDay = { day, index ->
                    fbLeagueScheduleViewModel.send(FBLeagueScheduleIntent.SelectDay(day, index))
                }
            ),
            allResultButtonAction = {
                fbLeagueScheduleViewModel.send(FBLeagueScheduleIntent.ToggleAllResult)
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
            FBLeagueScheduleList()
        }
    )
}

@Composable
fun FBLeagueScheduleList(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbLeagueScheduleViewModel: FBLeagueScheduleViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val filteredGames by fbLeagueScheduleViewModel.filteredGames.collectAsState()
    val selectedDayIndex by fbLeagueScheduleViewModel.selectedDayIndex.collectAsState()

    val displayModels by searchViewModel.displayModels.collectAsState()
    val fbGameStatsModel = displayModels[SportDisplayType.FB_GAME_STATS] as? FBGameStatsDisplayModel

    val gameListToDisplay = if (fbGameStatsModel == null) filteredGames[selectedDayIndex] ?: emptyList() else listOf(fbGameStatsModel.game)

    LazyColumn {
        items(gameListToDisplay) { item ->
            FBLeagueScheduleListItem(data = item)
        }
//        for (value in gameListToDisplay) {
//            FBLeagueScheduleItem(data = value)
//        }
    }
}

@Composable
fun FBLeagueScheduleListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbLeagueScheduleViewModel: FBLeagueScheduleViewModel = hiltViewModel(),
    data: FBGame,
) {
    /* ---------------------
       ui state
       --------------------- */
//    var isResultOpened by remember(data.fixture.id) { mutableStateOf(false) }
    var isResultOpened by remember { mutableStateOf(false) }
    val noRippleInteractionSource = remember { MutableInteractionSource() }
    var refereeKrName by remember { mutableStateOf("") }

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by fbLeagueScheduleViewModel.displayModel.collectAsState()
    val gameResultOpenedStateList by fbLeagueScheduleViewModel.gameResultOpenedStateList.collectAsState()

    val displayModels by searchViewModel.displayModels.collectAsState()
    val fbGameStatsModel = displayModels[SportDisplayType.FB_GAME_STATS] as? FBGameStatsDisplayModel

    /* ---------------------
       animation
       --------------------- */
    val scoreAlpha by animateFloatAsState(
        targetValue = if (StringConstants.Football.GAME_LIVE_LIST.contains(data.fixture.status.short) ||
            (StringConstants.Football.GAME_FINISHED_LIST.contains(data.fixture.status.short) && isResultOpened)) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearOutSlowInEasing
        )
    )

    /* ---------------------
       constants
       --------------------- */
    val gameStatusText = if (isResultOpened) {
        when (data.fixture.status.short) {
            StringConstants.Football.GAME_NOT_STARTED -> StringConstants.GAME_NOT_STARTED_STR
            StringConstants.Football.GAME_FIRST_HALF -> StringConstants.Football.GAME_FIRST_HALF_STR
            StringConstants.Football.GAME_HALF_TIME -> StringConstants.Football.GAME_HALF_TIME_STR
            StringConstants.Football.GAME_SECOND_HALF -> StringConstants.Football.GAME_SECOND_HALF_STR
            in StringConstants.Football.GAME_FINISHED_LIST -> StringConstants.GAME_FINISHED_STR
            else -> ""
        }
    } else {
        StringConstants.RESULT_OPEN
    }

    val gameStatusColor = if (isResultOpened) {
        when (data.fixture.status.short) {
            in StringConstants.Football.GAME_LIVE_LIST -> MaterialTheme.colors.primary
            else -> Color.Gray
        }
    } else {
        Color.Gray
    }

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (StringConstants.Football.GAME_FINISHED_LIST.contains(data.fixture.status.short)) {
            isResultOpened = gameResultOpenedStateList[data.fixture.id] ?: false
        } else {
            isResultOpened = true
        }
    }
    LaunchedEffect(gameResultOpenedStateList) {
        if (StringConstants.Football.GAME_FINISHED_LIST.contains(data.fixture.status.short)) {
            isResultOpened = gameResultOpenedStateList[data.fixture.id] ?: false
        }
    }
    LaunchedEffect(fbGameStatsModel) {
        fbGameStatsModel?.let {
            isResultOpened = true

            refereeKrName = EnNameTranslationUtils.translateByAWS(it.game.fixture.referee)
        }
    }

    ScheduleGameItemContainer(
        state = ScheduleGameItemState(
            isClickEnabled = fbGameStatsModel == null,
            homeTeamLogo = data.teams.home.logo,
            homeTeamName = fbLeagueScheduleViewModel.teamNameDictionary["short_${data.teams.home.id}"] ?: data.teams.home.name,
            homeTeamScore = data.goals.home,
            awayTeamLogo = data.teams.away.logo,
            awayTeamName = fbLeagueScheduleViewModel.teamNameDictionary["short_${data.teams.away.id}"] ?: data.teams.away.name,
            awayTeamScore = data.goals.away,
            scoreAlpha = scoreAlpha,
            isResultOpened = isResultOpened,
            gameStatusText = gameStatusText,
            gameStatusColor = gameStatusColor,
            isCapsuleButtonDisabled = fbGameStatsModel != null || !StringConstants.Football.GAME_FINISHED_LIST.contains(data.fixture.status.short),
            date = CalendarUtil.formatDate(data.fixture.date).split(" ").firstOrNull() ?: "",
            dateTime = CalendarUtil.formatDate(data.fixture.date, TimeFormatType.AMPM),
            venue = fbLeagueScheduleViewModel.teamNameDictionary["venue_${data.teams.home.id}"] ?: (fbGameStatsModel?.game?.fixture?.venue?.name ?: ""),
            gameType = MatchDescriptionConverter.convert(input = data.league.round),
            referee = refereeKrName,
            shouldShowOnlyDateTime = fbGameStatsModel == null,
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
                fbLeagueScheduleViewModel.send(FBLeagueScheduleIntent.UpdateResultOpenedState(data.fixture.id, true))
            },
            onCapsuleButtonClick = {
                fbLeagueScheduleViewModel.send(FBLeagueScheduleIntent.UpdateResultOpenedState(data.fixture.id, !isResultOpened))
            }
        )
    )
}






















