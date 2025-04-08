package com.moare.android.features.search.display.nba.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import com.moare.android.core.util.NBAUtil
import com.moare.android.core.util.TimeFormatType
import com.moare.android.core.util.TranslationType
import com.moare.android.features.search.display.football.view.FBTeamScheduleList
import com.moare.android.features.search.display.football.view.FBTeamScheduleListItem
import com.moare.android.features.search.display.football.viewmodel.FBTeamScheduleViewModel
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueScheduleViewModel
import com.moare.android.features.search.display.nba.viewmodel.NBATeamScheduleViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamScheduleDisplayModel
import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.ui.common.components.CapsuleButton
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.RoundedBorderText
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.theme.Moare

@Composable
fun NBATeamScheduleView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaTeamScheduleViewModel: NBATeamScheduleViewModel = hiltViewModel(),
    data: NBATeamScheduleDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by nbaTeamScheduleViewModel.displayModel.collectAsState()
    val isAllResultOpened by nbaTeamScheduleViewModel.isAllResultOpened.collectAsState()

    val season = displayModel?.games?.firstOrNull()?.gameSummary?.season

    val nbaGameStatsData by searchViewModel.nbaGameStatsData.collectAsState()
    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.NBATeamSchedule) {
            nbaTeamScheduleViewModel.send(NBATeamScheduleViewModel.Intent.InitData(data))
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
        nbaGameStatsData?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                NBATitle(
                    leagueName = "NBA",
                    leagueSeason = season?.split("-")?.firstOrNull()?.toIntOrNull() ?: 2024
                )

                Text(
                    text = " - 정규시즌",
                    fontSize = 14.sp
                )
            }
        }

        /* ---------------------
           all result open button
           - hides when game selected
           --------------------- */
        if (nbaGameStatsData == null) {
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
                    nbaTeamScheduleViewModel.send(NBATeamScheduleViewModel.Intent.ToggleAllResult)
                }
            }
        }

        /* ---------------------
           schedule
           --------------------- */
        NBATeamScheduleList()

        /* ---------------------
           bottom empty space
           - hides when game selected
           --------------------- */
        if (nbaGameStatsData == null) {
            Spacer(Modifier.fillMaxSize())
        }
    }
}

@Composable
fun NBATeamScheduleList(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaTeamScheduleViewModel: NBATeamScheduleViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val games by nbaTeamScheduleViewModel.games.collectAsState()

    val nbaGameStatsData by searchViewModel.nbaGameStatsData.collectAsState()

    val gameListToDisplay = if (nbaGameStatsData == null) games else listOf(nbaGameStatsData!!.game)

    LazyColumn {
        items(gameListToDisplay) { item ->
            NBATeamScheduleListItem(data = item)
        }
    }
}

@Composable
fun NBATeamScheduleListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaTeamScheduleViewModel: NBATeamScheduleViewModel = hiltViewModel(),
    data: NBAGame
) {
    /* ---------------------
       ui state
       --------------------- */
    var isResultOpened by remember { mutableStateOf(false) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val gameResultOpenedStateList by nbaTeamScheduleViewModel.gameResultOpenedStateList.collectAsState()

    val homeTeamId = data.gameSummary?.homeTeamId
    val awayTeamId = data.gameSummary?.visitorTeamId
    val homeTeamScore = data.lineScore.find { it.teamId == homeTeamId }?.pts ?: 0
    val awayTeamScore = data.lineScore.find { it.teamId == awayTeamId }?.pts ?: 0

    val nbaGameStatsData by searchViewModel.nbaGameStatsData.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val scoreAlpha by animateFloatAsState(
        targetValue = if (data.gameSummary?.gameStatusId == 2 || (data.gameSummary?.gameStatusId == 3 && isResultOpened)) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearOutSlowInEasing
        )
    )

    /* ---------------------
       constants
       --------------------- */
    val gameStatusText = if (isResultOpened) {
        when (data.gameSummary?.gameStatusId) {
            1 -> StringConstants.gameNotStartedStr
            2 -> if (data.lineScore.firstOrNull()?.ptsOt3 != null) {
                StringConstants.NBA.gameOt3
            } else if (data.lineScore.firstOrNull()?.ptsOt2 != null) {
                StringConstants.NBA.gameOt2
            } else if (data.lineScore.firstOrNull()?.ptsOt1 != null) {
                StringConstants.NBA.gameOt1
            } else if (data.lineScore.firstOrNull()?.ptsQtr4 != null) {
                StringConstants.NBA.gameQtr4
            } else if (data.lineScore.firstOrNull()?.ptsQtr3 != null) {
                StringConstants.NBA.gameQtr3
            } else if (data.lineScore.firstOrNull()?.ptsQtr2 != null) {
                StringConstants.NBA.gameQtr2
            } else if (data.lineScore.firstOrNull()?.ptsQtr1 != null) {
                StringConstants.NBA.gameQtr1
            } else {
                ""
            }
            3 -> StringConstants.gameFinishedStr
            else -> ""
        }
    } else {
        StringConstants.resultOpen
    }

    val gameStatusColor = if (isResultOpened) {
        if (data.gameSummary?.gameStatusId == 2) {
            MaterialTheme.colors.primary
        } else {
            Color.Gray
        }
    } else {
        Color.Gray
    }

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (data.gameSummary?.gameStatusId == 3) {
            isResultOpened = gameResultOpenedStateList[data.gameSummary.gameCode] ?: false
        } else {
            isResultOpened = true
        }
    }
    LaunchedEffect(gameResultOpenedStateList) {
        if (data.gameSummary?.gameStatusId == 3) {
            isResultOpened = gameResultOpenedStateList[data.gameSummary.gameCode] ?: false
        }
    }
    LaunchedEffect(nbaGameStatsData) {
        nbaGameStatsData?.let {
            isResultOpened = true
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
            .clickable(enabled = nbaGameStatsData == null) {
                searchViewModel.send(SearchViewModel.Intent.SelectNBAGame(data))

                // set selected game's isOpened true
                data.gameSummary?.let {
                    nbaTeamScheduleViewModel.send(NBATeamScheduleViewModel.Intent.UpdateResultOpenedState(it.gameCode, true))
                }
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
                url = if (homeTeamId != null) NBAUtil.teamLogoUrl(homeTeamId) else "",
                size = URLImageSize.SMALL,
                isSvg = true
            )

            Text(
                text = "",
                fontSize = 13.sp,
                maxLines = 2
            )

            nbaGameStatsData?.let {
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
            text = homeTeamScore.toString(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(20.dp)
                .alpha(scoreAlpha),
            color = if (homeTeamScore >= awayTeamScore) MaterialTheme.colors.primary else Color.Black
        )

        // Add space to both sides of each score to place the score in the middle
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
                isDisabled = nbaGameStatsData != null || data.gameSummary?.gameStatusId != 3
            ) {
                data.gameSummary?.let {
                    nbaTeamScheduleViewModel.send(NBATeamScheduleViewModel.Intent.UpdateResultOpenedState(it.gameCode, !isResultOpened))
                }
            }

            // game date
            if (nbaGameStatsData != null) {
                Text(
                    text = CalendarUtil.formatDate(data.gameSummary?.date, TimeFormatType.AMPM),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            } else {
                Text(
                    text = CalendarUtil.formatDate(data.gameSummary?.date).split(" ")[0],
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = CalendarUtil.formatDate(data.gameSummary?.date, TimeFormatType.AMPM),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            // venue
            nbaGameStatsData?.let {
                Text(
                    text = "장소: ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            // game type or referee
//            Text(
//                text = if (nbaGameStatsData != null) {
//                    "심판: "
//                } else {
//                    MatchDescriptionConverter.convert(input = data.league.round)
//                },
//                fontSize = 12.sp,
//                fontWeight = FontWeight.Light,
//                maxLines = 1,
//            )
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
                .width(20.dp)
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
                url = if (awayTeamId != null) NBAUtil.teamLogoUrl(awayTeamId) else "",
                size = URLImageSize.SMALL,
                isSvg = true
            )

            Text(
                text = "",
                fontSize = 13.sp,
                maxLines = 2
            )

            nbaGameStatsData?.let {
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







































