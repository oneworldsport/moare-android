package com.moare.android.features.search.display.football.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.constants.UIConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.EnNameTranslationUtils
import com.moare.android.core.util.MatchDescriptionConverter
import com.moare.android.core.util.TimeFormatType
import com.moare.android.core.util.TranslationType
import com.moare.android.features.search.display.football.viewmodel.FBLeagueScheduleViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.SportDecodableModel
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

    val fbGameStatsData by searchViewModel.fbGameStatsData.collectAsState()

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
            fbLeagueScheduleViewModel.send(FBLeagueScheduleViewModel.Intent.InitData(data))
        }
    }

    LaunchedEffect(viewStack) {
        // update games data after refreshing in FBGameStatsView
        if (viewStack.isNotEmpty() && viewStack.last() is SportDecodableModel.FBLeagueSchedule) {
            val fbLeagueSchedule = viewStack.last() as SportDecodableModel.FBLeagueSchedule

            poppedView?.let {
                if (it is SportDecodableModel.FBGameStats) {
                    fbLeagueScheduleViewModel.send(FBLeagueScheduleViewModel.Intent.UpdateGamesData(fbLeagueSchedule, it) { data ->
                        searchViewModel.send(SearchViewModel.Intent.UpdateLastViewStack(data))
                    })
                }
            }
        }
    }

    /* ---------------------
       ui
       --------------------- */
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        /* ---------------------
           game title
           - shows when game selected
           --------------------- */
        fbGameStatsData?.let {
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

        /* ---------------------
           calendar
           - hides when game selected
           --------------------- */
        if (fbGameStatsData == null) {
            CalendarList(yearMonthList, CalendarType.YEARMONTH, selectedYearMonthIndex, yearMonthCalendarScrollTrigger) { yearMonth, index ->
                fbLeagueScheduleViewModel.send(FBLeagueScheduleViewModel.Intent.SelectYearMonth(yearMonth, index) { data ->
                    // 현재 구조 콜백 수정 필요?
                    searchViewModel.send(SearchViewModel.Intent.UpdateLastViewStack(data))
                })
            }

            CalendarList(days, CalendarType.DAY, selectedDayIndex, dayCalendarScrollTrigger) { day, index ->
                fbLeagueScheduleViewModel.send(FBLeagueScheduleViewModel.Intent.SelectDay(day, index))
            }
        }


        /* ---------------------
           all result open button
           - hides when game selected
           --------------------- */
        if (fbGameStatsData == null) {
            Row {
                Spacer(Modifier.weight(1f))

                CapsuleButton(
                    text = if (isAllResultOpened) {
                        StringConstants.resultHide
                    } else {
                        StringConstants.resultOpen
                    },
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    fbLeagueScheduleViewModel.send(FBLeagueScheduleViewModel.Intent.ToggleAllResult)
                }
            }
        }

        // NOTE: In most situations, loading should be used in Box for smooth animation.
        Box {
            // loading
            this@Column.AnimatedVisibility(
                visible = displayDataState == ApiFetchState.Fetching,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProgressIndicator()
                }
            }

            /* ---------------------
               schedule
               --------------------- */
            this@Column.AnimatedVisibility(
                visible = displayDataState == ApiFetchState.Success
            ) {
                FBLeagueScheduleList()
            }
        }

        // no result / error
        AnimatedVisibility(
            visible = displayDataState is ApiFetchState.Error,
//            enter = fadeIn()
        ) {
            val error = displayDataState as? ApiFetchState.Error
            error?.let {
                Text(error.message)
            }
        }

        /* ---------------------
           bottom empty space
           - hides when game selected
           --------------------- */
        if (fbGameStatsData == null) {
            Spacer(Modifier.fillMaxSize())
        }
    }
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

    val fbGameStatsData by searchViewModel.fbGameStatsData.collectAsState()

    val gameListToDisplay = if (fbGameStatsData == null) filteredGames[selectedDayIndex] ?: emptyList() else listOf(fbGameStatsData!!.game)

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
    val gameResultOpenedStateList by fbLeagueScheduleViewModel.gameResultOpenedStateList.collectAsState()

    val fbGameStatsData by searchViewModel.fbGameStatsData.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val scoreAlpha by animateFloatAsState(
        targetValue = if (StringConstants.Football.gameLiveList.contains(data.fixture.status.short) ||
            (StringConstants.Football.gameFinishedList.contains(data.fixture.status.short) && isResultOpened)) 1f else 0f,
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
            StringConstants.Football.gameNotStarted -> StringConstants.gameNotStartedStr
            StringConstants.Football.gameFirstHalf -> StringConstants.Football.gameFirstHalfStr
            StringConstants.Football.gameHalftime -> StringConstants.Football.gameHalftimeStr
            StringConstants.Football.gameSecondHalf -> StringConstants.Football.gameSecondHalfStr
            in StringConstants.Football.gameFinishedList -> StringConstants.gameFinishedStr
            else -> ""
        }
    } else {
        StringConstants.resultOpen
    }

    val gameStatusColor = if (isResultOpened) {
        when (data.fixture.status.short) {
            in StringConstants.Football.gameLiveList -> MaterialTheme.colors.primary
            else -> Color.Gray
        }
    } else {
        Color.Gray
    }

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (StringConstants.Football.gameFinishedList.contains(data.fixture.status.short)) {
            isResultOpened = gameResultOpenedStateList[data.fixture.id] ?: false
        } else {
            isResultOpened = true
        }
    }
    LaunchedEffect(gameResultOpenedStateList) {
        if (StringConstants.Football.gameFinishedList.contains(data.fixture.status.short)) {
            isResultOpened = gameResultOpenedStateList[data.fixture.id] ?: false
        }
    }
    LaunchedEffect(fbGameStatsData) {
        fbGameStatsData?.let {
            isResultOpened = true

            refereeKrName = EnNameTranslationUtils.translateByAWS(it.game.fixture.referee)
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
            .clickable(enabled = fbGameStatsData == null) {
                searchViewModel.send(SearchViewModel.Intent.SelectFBGame(data))

                // set selected game's isOpened true
                fbLeagueScheduleViewModel.send(FBLeagueScheduleViewModel.Intent.UpdateResultOpenedState(data.fixture.id, true))
            }
            .padding(vertical = 8.dp)
            .padding(horizontal = UIConstants.Padding.defaultHPadding)
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
                url = data.teams.home.logo,
                size = URLImageSize.SMALL
            )

            Text(
                text = fbLeagueScheduleViewModel.teamNameDictionary["short_${data.teams.home.id}"] ?: data.teams.home.name,
                fontSize = 13.sp,
                maxLines = 2
            )

            fbGameStatsData?.let {
                RoundedBorderText(
                    text = "홈",
                    fontSize = 11.sp,
                    radius = 4.dp,
                    textColor = Moare,
                    borderColor = Moare
                )
            }
        }

        // Add space to both sides of each score to place the score in the middle
        Spacer(Modifier.weight(0.3f))

        // score
        Text(
            text = data.goals.home.toString(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(20.dp)
                .alpha(scoreAlpha),
            color = if (data.goals.home >= data.goals.away) MaterialTheme.colors.primary else Color.Black
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
                isDisabled = fbGameStatsData != null || !StringConstants.Football.gameFinishedList.contains(data.fixture.status.short)
            ) {
                fbLeagueScheduleViewModel.send(FBLeagueScheduleViewModel.Intent.UpdateResultOpenedState(data.fixture.id, !isResultOpened))
            }

            // game date
            if (fbGameStatsData == null) {
                Text(
                    text = CalendarUtil.formatDate(data.fixture.date, TimeFormatType.AMPM),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            } else {
                Text(
                    text = CalendarUtil.formatDate(data.fixture.date).split(" ")[0],
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = CalendarUtil.formatDate(data.fixture.date, TimeFormatType.AMPM),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            // venue
            fbGameStatsData?.let {
                Text(
                    text = "장소: ${fbLeagueScheduleViewModel.teamNameDictionary["venue_${data.teams.home.id}"] ?: it.game.fixture.venue.name}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            // game type or referee
            Text(
                text = if (fbGameStatsData != null) {
                    "심판: $refereeKrName"
                } else {
                    MatchDescriptionConverter.convert(input = data.league.round)
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                maxLines = 1,
            )
        }

        /* ---------------------
           away
           --------------------- */
        Spacer(Modifier.weight(0.3f))

        // score
        Text(
            text = data.goals.away.toString(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(20.dp)
                .alpha(scoreAlpha),
            color = if (data.goals.away >= data.goals.home) MaterialTheme.colors.primary else Color.Black
        )

        Spacer(Modifier.weight(0.3f))

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
        ) {
            URLImage(
                url = data.teams.away.logo,
                size = URLImageSize.SMALL
            )

            Text(
                text = fbLeagueScheduleViewModel.teamNameDictionary["short_${data.teams.away.id}"] ?: data.teams.away.name,
                fontSize = 13.sp,
                maxLines = 2
            )

            fbGameStatsData?.let {
                RoundedBorderText(
                    text = "원정",
                    fontSize = 11.sp,
                    radius = 4.dp,
                    textColor = Color.Gray,
                    borderColor = Color.Gray
                )
            }
        }
    }
}






















