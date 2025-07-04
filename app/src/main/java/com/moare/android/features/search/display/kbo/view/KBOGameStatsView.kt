package com.moare.android.features.search.display.kbo.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.R
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.constants.UIConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.KBOUtil
import com.moare.android.core.util.MLBUtil
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.NewStandingsViewContainer
import com.moare.android.features.search.display.kbo.viewmodel.KBOGameStatsIntent
import com.moare.android.features.search.display.kbo.viewmodel.KBOGameStatsViewModel
import com.moare.android.features.search.display.mlb.viewmodel.MLBGameStatsIntent
import com.moare.android.features.search.display.mlb.viewmodel.MLBGameStatsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBGameStatsDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOGameLineScore
import com.moare.android.features.search.models.models.mlb.MLBGameLineScoreInning
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.CapsuleButton
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.HCapsuleBarSize
import com.moare.android.ui.common.components.RoundedBorderText
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar

@Composable
fun KBOGameStatsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboGameStatsViewModel: KBOGameStatsViewModel = hiltViewModel(),
    data: KBOGameStatsDisplayModel
) {
    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by kboGameStatsViewModel.displayModel.collectAsState()
    val firstCategorySelectedIndex by kboGameStatsViewModel.firstCategorySelectedIndex.collectAsState()
    val secondCategorySelectedIndex by kboGameStatsViewModel.secondCategorySelectedIndex.collectAsState()
    val teamHitters by kboGameStatsViewModel.teamHitters.collectAsState()
    val teamPitchers by kboGameStatsViewModel.teamPitchers.collectAsState()
    val playerNameDic = kboGameStatsViewModel.playerNameDictionary

    val game = displayModel?.game

    val poppedView by searchViewModel.poppedView.collectAsState()

    val hitterStandings: List<StandingsItemState> = teamHitters.map {
        StandingsItemState(
            isGameStats = true,
            imageUrl = KBOUtil.playerPhotoUrl(it.id),
            name = it.name,
            dataList = listOf(
                it.ab, it.h, it.hr, it.rbi, it.r, it.sb, it.bb, it.so
            )
        )
    }
    val pitcherStandings: List<StandingsItemState> = teamPitchers.map {
        StandingsItemState(
            isGameStats = true,
            imageUrl = KBOUtil.playerPhotoUrl(it.id),
            name = it.name,
            dataList = listOf(
                it.ip, it.r, it.er, it.bb, it.so, it.h
            )
        )
    }

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.KBOGameStats) {
            kboGameStatsViewModel.send(KBOGameStatsIntent.InitData(data))
        }
    }

    /* ---------------------
       ui
       --------------------- */
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        /* ---------------------
           game title
           --------------------- */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = UIConstants.Padding.DEFAULT_H_PADDING)
        ) {
            BaseballLeagueTitle(
                url = KBOUtil.kboLogoUrl,
                leagueName = "KBO",
                leagueSeason = 2025
            )

            Spacer(Modifier.weight(1f))
        }

        KBOGameStatsScoreInfoItem()

        Box(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .height(1.dp)
                .clip(RoundedCornerShape(10.dp))
                .padding(horizontal = UIConstants.Padding.DEFAULT_H_PADDING)
                .background(MaterialTheme.colors.primary)
        )

        if (game?.gameInfo?.gameStatus?.toIntOrNull() != StringConstants.KBO.GAME_SCHEDULED) {
            /* ---------------------
               team select button
               --------------------- */
            KBOGameStatsTeamButtonAdditionalInfoContainer()

            /* ---------------------
               players stats
               --------------------- */
            // hitter stats
            Row {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(100.dp)
                ) {
                    Text(
                        text = "타자",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )

                    HCapsuleBar()
                }

                Spacer(Modifier.weight(1f))
            }

            NewStandingsViewContainer(
                state = NewStandingsContainerState(
                    firstCategoryText = StringConstants.GAME_STATS_FIRST_CATEGORY,
                    secondCategories = StringConstants.KBO.GAME_STATS_HITTING_CATEGORIES,
                    standings = hitterStandings,
                    secondCategorySelectedIndex = firstCategorySelectedIndex
                ),
                actions = StandingsContainerActions(
                    secondCategoryButtonAction = { index, _ ->
                        kboGameStatsViewModel.send(KBOGameStatsIntent.SelectFirstCategory(index))
                    },
                    itemButtonAction = {
                    }
                ),
                modifier = Modifier.weight(0.5f)
            )

            // pitcher stats
            Row {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(100.dp)
                ) {
                    Text(
                        text = "투수",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )

                    HCapsuleBar()
                }

                Spacer(Modifier.weight(1f))
            }

            NewStandingsViewContainer(
                state = NewStandingsContainerState(
                    firstCategoryText = StringConstants.GAME_STATS_FIRST_CATEGORY,
                    secondCategories = StringConstants.KBO.GAME_STATS_PITCHING_CATEGORIES,
                    standings = pitcherStandings,
                    secondCategorySelectedIndex = secondCategorySelectedIndex
                ),
                actions = StandingsContainerActions(
                    secondCategoryButtonAction = { index, _ ->
                        kboGameStatsViewModel.send(KBOGameStatsIntent.SelectSecondCategory(index))
                    },
                    itemButtonAction = {
                    }
                ),
                modifier = Modifier.weight(0.5f)
            )
        } else {
            Text(
                text = "경기 시작 후 데이터가 업데이트됩니다.",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )

            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun KBOGameStatsScoreInfoItem(
    kboGameStatsViewModel: KBOGameStatsViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by kboGameStatsViewModel.displayModel.collectAsState()
    val game = displayModel?.game
    val homeTeamId = game?.gameInfo?.homeTeamId
    val awayTeamId = game?.gameInfo?.awayTeamId
    val gameStatus = game?.gameInfo?.gameStatus?.toIntOrNull() ?: 0
    val teamNameDic = kboGameStatsViewModel.teamNameDictionary

    /* ---------------------
       constants
       --------------------- */
    val gameStatusText = when (gameStatus) {
        StringConstants.KBO.GAME_SCHEDULED -> StringConstants.GAME_NOT_STARTED_STR
        StringConstants.KBO.GAME_LIVE -> StringConstants.GAME_LIVE_STR
        StringConstants.KBO.GAME_FINAL -> StringConstants.GAME_FINISHED_STR
        StringConstants.KBO.GAME_CANCELED -> StringConstants.GAME_CANCELED_STR
        else -> ""
    }

    val gameStatusColor = if (gameStatus == StringConstants.KBO.GAME_LIVE) {
        MaterialTheme.colors.primary
    } else {
        Color.Gray
    }

    /* ---------------------
       ui
       --------------------- */
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = UIConstants.Padding.DEFAULT_H_PADDING)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(0.4f)
                .padding(top = 26.dp) // for KBOGameStatsLineScoreTitle. TODO: KBOTitle 과의 간격 줄이고 싶음
        ) {
            URLImage(
                url = KBOUtil.teamLogoUrl(homeTeamId),
                size = URLImageSize.SMALL,
                isSvg = true
            )

            Row {
                // TODO: RoundedBorderText 는 왼쪽 정렬, 팀 이름은 가운데 정렬 하고 싶음
                RoundedBorderText(
                    text = "홈",
                    fontSize = 11.sp,
                    radius = 4.dp,
                    textColor = Moare,
                    borderColor = Moare
                )
                Text(
                    text = teamNameDic["short_$homeTeamId"] ?: "",
                    fontSize = 13.sp,
                    maxLines = 2,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            CapsuleButton(
                text = gameStatusText,
                color = gameStatusColor,
                isDisabled = true,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {}

            Row {
                RoundedBorderText(
                    text = "원정",
                    fontSize = 11.sp,
                    radius = 4.dp,
                    textColor = Color.Gray,
                    borderColor = Color.Gray
                )
                Text(
                    text = teamNameDic["short_$awayTeamId"] ?: "",
                    fontSize = 13.sp,
                    maxLines = 2,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            URLImage(
                url = KBOUtil.teamLogoUrl(awayTeamId),
                size = URLImageSize.SMALL,
                isSvg = true
            )
        }

        KBOGameStatsLineScoreContainer(
            modifier = Modifier
                .height(127.dp) // 25 + 1 + 50 + 1 + 50
                .weight(1f)
        )
    }
}

@Composable
fun KBOGameStatsLineScoreContainer(
    kboGameStatsViewModel: KBOGameStatsViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val displayModel by kboGameStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val game = it.game

        game.lineScore?.let { lineScore ->
            val homeTeamLineScore = lineScore.home.r.toIntOrNull() ?: 0
            val awayTeamLineScore = lineScore.away.r.toIntOrNull() ?: 0

            Column(
                modifier = modifier
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Box(Modifier.height(26.dp)) // Empty space to position pts to same line with linescore

                        Box(
                            modifier = Modifier.height(kboGameStatsViewModel.lineScoreItemHeight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = homeTeamLineScore.toString(),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .padding(start = 4.dp, end = 8.dp)
                                    .width(30.dp),
                                color = if (homeTeamLineScore >= awayTeamLineScore) MaterialTheme.colors.primary else Color.Black
                            )
                        }
                    }


                    Column(
                        Modifier.weight(1f)
                    ) {
                        KBOGameStatsLineScoreTitle(lineScore.away)

                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Gray)
                                .alpha(0.5f)
                        )

                        KBOGameStatsLineScoreItem(lineScore = lineScore.home)
                    }
                }

                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Gray)
                        .alpha(0.5f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.height(kboGameStatsViewModel.lineScoreItemHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = awayTeamLineScore.toString(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(start = 4.dp, end = 8.dp)
                                .width(30.dp),
                            color = if (awayTeamLineScore >= homeTeamLineScore) MaterialTheme.colors.primary else Color.Black
                        )
                    }

                    KBOGameStatsLineScoreItem(lineScore = lineScore.away)
                }
            }
        }
    }
}

@Composable
fun KBOGameStatsLineScoreTitle(
    lineScore: KBOGameLineScore
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(25.dp)
    ) {
        for (index in 1..12) {
            if (index < 10 ||
                (index == 10 && lineScore.inning10 != "-") ||
                (index == 11 && lineScore.inning11 != "-")
            ) {
                VCapsuleBar(modifier = Modifier.alpha(0.5f))
                Text(
                    text = "${index}회",
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun KBOGameStatsLineScoreItem(
    kboGameStatsViewModel: KBOGameStatsViewModel = hiltViewModel(),
    lineScore: KBOGameLineScore
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(kboGameStatsViewModel.lineScoreItemHeight)
    ) {
        for (index in 0 until 11) {
            if (index < 9 ||
                (index == 9 && lineScore.inning10 != "-") ||
                (index == 10 && lineScore.inning11 != "-")
            ) {
                VCapsuleBar(modifier = Modifier.alpha(0.5f))
                Text(
                    text = lineScore.innings[index],
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun KBOGameStatsTeamButtonAdditionalInfoContainer(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboGameStatsViewModel: KBOGameStatsViewModel = hiltViewModel()
) {
    val density = LocalDensity.current
    var teamButtonWidth by remember { mutableStateOf(0.dp) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by kboGameStatsViewModel.displayModel.collectAsState()
    val selectedIndex by kboGameStatsViewModel.selectedTeamIndex.collectAsState()
    val teamNameDic = kboGameStatsViewModel.teamNameDictionary

    /* ---------------------
       animation
       --------------------- */
    val barOffset by animateDpAsState(
        targetValue = if (selectedIndex == 0) {
            getOffsetOfAniCapsuleBar(itemWidth = teamButtonWidth, barWidth = 50.dp)
        } else {
            2.dp + getOffsetOfAniCapsuleBar(itemWidth = teamButtonWidth, barWidth = 50.dp, index = selectedIndex)
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    displayModel?.let { displayModel ->
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(50.dp)
                ) {
                    // home
                    KBOGameStatsTeamButton(
                        team = teamNameDic["short_${displayModel.game.gameInfo?.homeTeamId}"] ?: "",
                        index = 0,
                        modifier = Modifier
                            .weight(1f)
                            .onGloballyPositioned { layoutCoordinates ->
                                with(density) {
                                    teamButtonWidth = layoutCoordinates.size.width.toDp()
                                }
                            }
                    )

                    VCapsuleBar(modifier = Modifier.alpha(0.5f))

                    // away
                    KBOGameStatsTeamButton(
                        team = teamNameDic["short_${displayModel.game.gameInfo?.awayTeamId}"] ?: "",
                        index = 1,
                        modifier = Modifier
                            .weight(1f)
                    )
                }

                HCapsuleBar(
                    modifier = Modifier.offset(x = barOffset),
                    size = HCapsuleBarSize.MEDIUM
                )
            }

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.weight(0.4f)
            ) {
                // refresh button
                if (displayModel.game.gameInfo?.gameStatus?.toIntOrNull() == StringConstants.KBO.GAME_LIVE) {
                    Box(
                        Modifier
                            .padding(end = 4.dp)
                            .alpha(0.6f)
                            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(10.dp))
                            .padding(2.dp)
                            .clickable {
                                searchViewModel.send(SearchViewModel.Intent.RefreshGame(category = "basketball"))
                            }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_refresh_24),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = "날짜: ${CalendarUtil.formatDate(displayModel.game.gameInfo?.date).split(" ").firstOrNull() ?: ""}",
                        fontSize = 12.sp,
                    )

                    Text(
                        text = CalendarUtil.formatDate(displayModel.game.gameInfo?.date, TimeFormatType.AMPM),
                        fontSize = 12.sp
                    )

                    Text(
                        text = "장소: ${teamNameDic["venue_${displayModel.game.gameInfo?.homeTeamId}"] ?: ""}",
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}

// TODO: Make it Component
@Composable
fun KBOGameStatsTeamButton(
    kboGameStatsViewModel: KBOGameStatsViewModel = hiltViewModel(),
    team: String,
    index: Int,
    modifier: Modifier
) {
    Text(
        text = team,
        textAlign = TextAlign.Center,
        maxLines = 2,
        modifier = modifier
            .clickable {
                kboGameStatsViewModel.send(KBOGameStatsIntent.SelectTeam(index))
            }
//            .width(kboGameStatsViewModel.teamButtonWidth)
    )
}