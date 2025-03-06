package com.moare.android.features.search.display.football.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
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
import com.moare.android.core.util.TranslationType
import com.moare.android.features.search.display.football.viewmodel.FBLeagueScheduleViewModel
import com.moare.android.features.search.display.football.viewmodel.FBTeamScheduleViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamScheduleDisplayModel
import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.ui.common.components.CapsuleButton
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize

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

    val fbGameStatsData by searchViewModel.fbGameStatsData.collectAsState()

    /* ---------------------
       etc
       --------------------- */

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        fbTeamScheduleViewModel.initData(data)
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
                    text = " - " + if (fbGameStatsData?.game?.league?.round != null) {
                        MatchDescriptionConverter.convert(descriptionType = MatchDescriptionConverter.DescriptionType.ROUND_WITHOUT_DASH, input = fbGameStatsData?.game?.league?.round!!)
                    } else { "" },
                    fontSize = 14.sp
                )
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
                        StringConstants.Football.resultHide
                    } else {
                        StringConstants.Football.resultOpen
                    },
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    fbTeamScheduleViewModel.send(FBTeamScheduleViewModel.Intent.ToggleAllResult)
                }
            }
        }

        /* ---------------------
           schedule
           --------------------- */
        FBTeamScheduleList()

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
fun FBTeamScheduleList(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbTeamScheduleViewModel: FBTeamScheduleViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val games by fbTeamScheduleViewModel.games.collectAsState()

    val fbGameStatsData by searchViewModel.fbGameStatsData.collectAsState()

    val gameListToDisplay = if (fbGameStatsData == null) games else listOf(fbGameStatsData!!.game)

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
    data: FBGame,
) {
    /* ---------------------
       ui state
       --------------------- */
    var isResultOpened by remember { mutableStateOf(false) }
    var venueKrName by remember { mutableStateOf("") }
    var refereeKrName by remember { mutableStateOf("") }

    /* ---------------------
       viewmodel state
       --------------------- */
    val gameResultOpenedStateList by fbTeamScheduleViewModel.gameResultOpenedStateList.collectAsState()

    val fbGameStatsData by searchViewModel.fbGameStatsData.collectAsState()

    /* ---------------------
       constants
       --------------------- */
    val gameStatusText = if (isResultOpened) {
        when (data.fixture.status.short) {
            StringConstants.Football.gameNotStarted -> StringConstants.Football.gameNotStartedStr
            StringConstants.Football.gameFirstHalf -> StringConstants.Football.gameFirstHalfStr
            StringConstants.Football.gameHalftime -> StringConstants.Football.gameHalftimeStr
            StringConstants.Football.gameSecondHalf -> StringConstants.Football.gameSecondHalfStr
            in StringConstants.Football.gameFinishedList -> StringConstants.Football.gameFinishedStr
            else -> ""
        }
    } else {
        StringConstants.Football.resultOpen
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

            venueKrName = EnNameTranslationUtils.translateByAWS(it.game.fixture.venue.name)
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
                fbTeamScheduleViewModel.send(FBTeamScheduleViewModel.Intent.UpdateResultOpenedState(data.fixture.id, true))
            }
            .padding(vertical = 8.dp)
            .padding(horizontal = UIConstants.Padding.defaultHPadding)
    ) {

        /* ---------------------
           home
           --------------------- */
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(110.dp)
                .clickable(enabled = fbGameStatsData != null) {
                    searchViewModel.send(SearchViewModel.Intent.UpdateTextField(newValue = TextFieldValue(text = "토트넘")))
                    searchViewModel.send(SearchViewModel.Intent.PerformSearch())
                }
        ) {
            URLImage(
                url = data.teams.home.logo,
                size = URLImageSize.SMALL
            )

            Text(
                text = EnNameTranslationUtils.translateByDic(TranslationType.TEAM, data.teams.home.name),
                fontSize = 13.sp,
                maxLines = 2
            )
        }

        Spacer(Modifier.weight(1f))

        // score
        if (StringConstants.Football.gameLiveList.contains(data.fixture.status.short) ||
            StringConstants.Football.gameFinishedList.contains(data.fixture.status.short) && isResultOpened) {
            Text(
                text = data.goals.home.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(20.dp),
                color = if (data.goals.home >= data.goals.away) MaterialTheme.colors.primary else Color.Black
            )
        }

        Spacer(Modifier.weight(1f))

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
                fbTeamScheduleViewModel.send(FBTeamScheduleViewModel.Intent.UpdateResultOpenedState(data.fixture.id, !isResultOpened))
            }

            // game date
            if (fbGameStatsData != null) {
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
                    text = "장소: $venueKrName",
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
        Spacer(Modifier.weight(1f))

        // score
        if (StringConstants.Football.gameLiveList.contains(data.fixture.status.short) ||
            StringConstants.Football.gameFinishedList.contains(data.fixture.status.short) && isResultOpened) {
            Text(
                text = data.goals.away.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(20.dp),
                color = if (data.goals.away >= data.goals.home) MaterialTheme.colors.primary else Color.Black
            )
        }

        Spacer(Modifier.weight(1f))

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(110.dp)
        ) {
            URLImage(
                url = data.teams.away.logo,
                size = URLImageSize.SMALL
            )

            Text(
                text = EnNameTranslationUtils.translateByDic(TranslationType.TEAM, data.teams.away.name),
                fontSize = 13.sp,
                maxLines = 2
            )
        }
    }
}