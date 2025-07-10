package com.moare.android.features.search.display.mlb.view

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
import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.constants.UIConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.MLBUtil
import com.moare.android.core.util.TimeFormatType
import com.moare.android.core.util.displayOrDash
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.NewStandingsViewContainer
import com.moare.android.features.search.display.mlb.viewmodel.MLBGameStatsIntent
import com.moare.android.features.search.display.mlb.viewmodel.MLBGameStatsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBGameStatsDisplayModel
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
import com.moare.android.ui.util.CenterRow
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar

@Composable
fun MLBGameStatsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbGameStatsViewModel: MLBGameStatsViewModel = hiltViewModel(),
    data: MLBGameStatsDisplayModel
) {
    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by mlbGameStatsViewModel.displayModel.collectAsState()
    val firstCategorySelectedIndex by mlbGameStatsViewModel.firstCategorySelectedIndex.collectAsState()
    val secondCategorySelectedIndex by mlbGameStatsViewModel.secondCategorySelectedIndex.collectAsState()
    val teamHitters by mlbGameStatsViewModel.teamHitters.collectAsState()
    val teamPitchers by mlbGameStatsViewModel.teamPitchers.collectAsState()
    val playerNameDic = mlbGameStatsViewModel.playerNameDictionary

    val game = displayModel?.game
    val season = game?.game?.season?.toIntOrNull() ?: 2025

    val poppedView by searchViewModel.poppedView.collectAsState()

    val hitterStandings: List<StandingsItemState> = teamHitters.map {
        val playerData = it.second
        val playerBatting = playerData.stats?.batting
        val playerSeasonBatting = playerData.seasonStats?.batting

        StandingsItemState(
            isGameStats = true,
            imageUrl = MLBUtil.playerPhotoUrl(it.first.removePrefix("ID").toIntOrNull()),
            name = playerNameDic[playerData.person?.id.toString()] ?: (playerData.person?.fullName ?: ""),
            extraInfo = playerData.position?.abbreviation,
            dataList = listOf(
                (playerBatting?.atBats ?: 0).toString(),
                (playerBatting?.hits ?: 0).toString(),
                (playerBatting?.homeRuns ?: 0).toString(),
                (playerBatting?.rbi ?: 0).toString(),
                (playerBatting?.runs ?: 0).toString(),
                (playerBatting?.stolenBases ?: 0).toString(),
                (playerBatting?.baseOnBalls ?: 0).toString(),
                (playerBatting?.strikeOuts ?: 0).toString(),
                playerSeasonBatting?.avg ?: "0.0"
            )
        )
    }
    val pitcherStandings: List<StandingsItemState> = teamPitchers.map {
        val playerData = it.second
        val playerPitching = playerData.stats?.pitching
        val playerSeasonPitching = playerData.seasonStats?.pitching

        StandingsItemState(
            isGameStats = true,
            imageUrl = MLBUtil.playerPhotoUrl(it.first.removePrefix("ID").toIntOrNull()),
            name = playerNameDic[playerData.person?.id.toString()] ?: (playerData.person?.fullName ?: ""),
            extraInfo = playerData.position?.abbreviation,
            dataList = listOf(
                playerPitching?.inningsPitched ?: "0.0",
                (playerPitching?.runs ?: 0).toString(),
                (playerPitching?.earnedRuns ?: 0).toString(),
                (playerPitching?.baseOnBalls ?: 0).toString(),
                (playerPitching?.strikeOuts ?: 0).toString(),
                (playerPitching?.hits ?: 0).toString(),
                playerSeasonPitching?.era ?: "0.0"
            )
        )
    }

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.MLBGameStats) {
            mlbGameStatsViewModel.send(MLBGameStatsIntent.InitData(data))
        }
    }

    /* ---------------------
       ui
       --------------------- */
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        /* ---------------------
           game title, info
           --------------------- */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = UIConstants.Padding.DEFAULT_H_PADDING)
        ) {
            BaseballLeagueTitle(
                url = MLBUtil.mlbLogoUrl,
                leagueName = "MLB",
                leagueSeason = season
            )

            Spacer(Modifier.weight(1f))
        }

        MLBGameStatsScoreInfoItem()

        Box(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .height(1.dp)
                .clip(RoundedCornerShape(10.dp))
                .padding(horizontal = UIConstants.Padding.DEFAULT_H_PADDING)
                .background(MaterialTheme.colors.primary)
        )

        if (game?.status?.detailedState != StringConstants.MLB.GAME_SCHEDULED) {
            /* ---------------------
               team select button
               --------------------- */
            MLBGameStatsTeamButtonAdditionalInfoContainer()

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
                    secondCategories = StringConstants.MLB.GAME_STATS_HITTING_CATEGORIES,
                    standings = hitterStandings,
                    secondCategorySelectedIndex = firstCategorySelectedIndex
                ),
                actions = StandingsContainerActions(
                    secondCategoryButtonAction = { index, _ ->
                        mlbGameStatsViewModel.send(MLBGameStatsIntent.SelectFirstCategory(index))
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
                    secondCategories = StringConstants.MLB.GAME_STATS_PITCHING_CATEGORIES,
                    standings = pitcherStandings,
                    secondCategorySelectedIndex = secondCategorySelectedIndex
                ),
                actions = StandingsContainerActions(
                    secondCategoryButtonAction = { index, _ ->
                        mlbGameStatsViewModel.send(MLBGameStatsIntent.SelectSecondCategory(index))
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
fun MLBGameStatsScoreInfoItem(
    mlbGameStatsViewModel: MLBGameStatsViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by mlbGameStatsViewModel.displayModel.collectAsState()
    val game = displayModel?.game
    val homeTeamId = game?.teams?.home?.id
    val awayTeamId = game?.teams?.away?.id
    val gameStatus = game?.status?.detailedState
    val teamNameDic = mlbGameStatsViewModel.teamNameDictionary

    /* ---------------------
       constants
       --------------------- */
    val gameStatusText = when (gameStatus) {
        StringConstants.MLB.GAME_SCHEDULED -> StringConstants.GAME_NOT_STARTED_STR
        StringConstants.MLB.GAME_LIVE -> "${game.linescore.currentInning}회${if (game.linescore.isTopInning) "초" else "말"}"
        StringConstants.MLB.GAME_POSTPONED -> StringConstants.GAME_POSTPONED_STR
        in StringConstants.MLB.GAME_FINISHED_LIST -> StringConstants.GAME_FINISHED_STR
        else -> ""
    }

    val gameStatusColor = if (gameStatus == StringConstants.MLB.GAME_LIVE) {
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
                .padding(top = 26.dp) // for MLBGameStatsLineScoreTitle. TODO: MLBTitle 과의 간격 줄이고 싶음
        ) {
            URLImage(
                url = MLBUtil.teamLogoUrl(homeTeamId),
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
                url = MLBUtil.teamLogoUrl(awayTeamId),
                size = URLImageSize.SMALL,
                isSvg = true
            )
        }

        MLBGameStatsLineScoreContainer(
            modifier = Modifier
                .height(127.dp) // 25 + 1 + 50 + 1 + 50
                .weight(1f)
        )
    }
}

@Composable
fun MLBGameStatsLineScoreContainer(
    mlbGameStatsViewModel: MLBGameStatsViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val displayModel by mlbGameStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val game = it.game
        val isGameScheduled = game.status.detailedState == StringConstants.MLB.GAME_SCHEDULED
        val lineScore = game.linescore
        val homeTeamLineScore = lineScore.teams.home.runs
        val awayTeamLineScore = lineScore.teams.away.runs

        Column(
            modifier = modifier
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Box(Modifier.height(26.dp)) // Empty space to position pts to same line with linescore

                    if (!isGameScheduled) {
                        Box(
                            modifier = Modifier.height(mlbGameStatsViewModel.lineScoreItemHeight),
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
                    } else {
                        Box(
                            modifier = Modifier.height(mlbGameStatsViewModel.lineScoreItemHeight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "-",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .padding(start = 4.dp, end = 8.dp)
                                    .width(30.dp),
                                color = Color.Black
                            )
                        }
                    }
                }

                Column(
                    Modifier.weight(1f)
                ) {
                    MLBGameStatsLineScoreTitle(lineScore.innings)

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Gray)
                            .alpha(0.5f)
                    )

                    MLBGameStatsLineScoreItem(isHome = true, lineScoreInnings = lineScore.innings)
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
                if (!isGameScheduled) {
                    Box(
                        modifier = Modifier.height(mlbGameStatsViewModel.lineScoreItemHeight),
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
                } else {
                    Box(
                        modifier = Modifier.height(mlbGameStatsViewModel.lineScoreItemHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "-",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(start = 4.dp, end = 8.dp)
                                .width(30.dp),
                            color = Color.Black
                        )
                    }
                }

                MLBGameStatsLineScoreItem(isHome = false, lineScoreInnings = lineScore.innings)
            }
        }
    }
}

@Composable
fun MLBGameStatsLineScoreTitle(
    lineScoreInnings: List<MLBGameLineScoreInning>
) {
    val inningsCount = if (lineScoreInnings.isEmpty()) 9 else lineScoreInnings.size

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(25.dp)
    ) {
        for (index in 1..inningsCount) {
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

@Composable
fun MLBGameStatsLineScoreItem(
    mlbGameStatsViewModel: MLBGameStatsViewModel = hiltViewModel(),
    isHome: Boolean,
    lineScoreInnings: List<MLBGameLineScoreInning>
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(mlbGameStatsViewModel.lineScoreItemHeight)
    ) {
        if (lineScoreInnings.isNotEmpty()) {
            for ((_, item) in lineScoreInnings.withIndex()) {
                VCapsuleBar(modifier = Modifier.alpha(0.5f))
                Text(
                    text = "${if (isHome) item.home.runs else item.away.runs}",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            for (index in 0 until 9) {
                VCapsuleBar(modifier = Modifier.alpha(0.5f))
                Text(
                    text = "-",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MLBGameStatsTeamButtonAdditionalInfoContainer(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbGameStatsViewModel: MLBGameStatsViewModel = hiltViewModel()
) {
    val density = LocalDensity.current
    var teamButtonWidth by remember { mutableStateOf(0.dp) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by mlbGameStatsViewModel.displayModel.collectAsState()
    val selectedIndex by mlbGameStatsViewModel.selectedTeamIndex.collectAsState()
    val teamNameDic = mlbGameStatsViewModel.teamNameDictionary

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
                    MLBGameStatsTeamButton(
                        team = teamNameDic["short_${displayModel.game.teams.home.id}"] ?: "",
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
                    MLBGameStatsTeamButton(
                        team = teamNameDic["short_${displayModel.game.teams.away.id}"] ?: "",
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
                if (displayModel.game.status.detailedState == StringConstants.MLB.GAME_LIVE) {
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
                        text = "날짜: ${CalendarUtil.formatDate(displayModel.game.gameInfo.gameDate).split(" ").firstOrNull() ?: ""}",
                        fontSize = 12.sp,
                    )

                    Text(
                        text = CalendarUtil.formatDate(displayModel.game.gameInfo.gameDate, TimeFormatType.AMPM),
                        fontSize = 12.sp
                    )

                    Text(
                        text = "장소: ${teamNameDic["venue_${displayModel.game.teams.home.id}"] ?: ""}",
                        fontSize = 12.sp,
                    )

                    Text(
                        text = "관중수: ${displayModel.game.gameInfo.attendance}",
                        fontSize = 12.sp,
                    )

                    Text(
                        text = "심판:",
                        fontSize = 12.sp,
                    )

                    displayModel.game.boxscore?.officials?.let {
                        for (item in it) {
                            Text(
                                text = "• ${item.official.fullName}",
                                fontSize = 12.sp,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

// TODO: Make it Component
@Composable
fun MLBGameStatsTeamButton(
    mlbGameStatsViewModel: MLBGameStatsViewModel = hiltViewModel(),
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
                mlbGameStatsViewModel.send(MLBGameStatsIntent.SelectTeam(index))
            }
//            .width(mlbGameStatsViewModel.teamButtonWidth)
    )
}