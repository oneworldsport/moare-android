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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.moare.android.features.search.display.common.container.state.GameStatsContainerActions
import com.moare.android.features.search.display.common.container.state.GameStatsContainerState
import com.moare.android.features.search.display.common.container.state.GameStatsTeamState
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.GameStatsViewContainer
import com.moare.android.features.search.display.common.container.view.NewStandingsViewContainer
import com.moare.android.features.search.display.kbo.viewmodel.KBOGameStatsIntent
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
import com.moare.android.ui.util.CenterColumn
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
    val selectedTeamIndex by mlbGameStatsViewModel.selectedTeamIndex.collectAsState()
    val teamHitters by mlbGameStatsViewModel.teamHitters.collectAsState()
    val teamPitchers by mlbGameStatsViewModel.teamPitchers.collectAsState()
    val playerNameDic = mlbGameStatsViewModel.playerNameDictionary
    val teamNameDic = mlbGameStatsViewModel.teamNameDictionary

    val game = displayModel?.game
    val season = game?.game?.season?.toIntOrNull() ?: 2025

    val poppedView by searchViewModel.poppedView.collectAsState()

    val teamIds = listOf(displayModel?.game?.teams?.home?.id, displayModel?.game?.teams?.away?.id)
    val teamCategories = teamIds.map {
        GameStatsTeamState(
            name = teamNameDic["short_${it}"] ?: "",
            imageUrl = MLBUtil.teamLogoUrl(it)
        )
    }

    val hitterList: List<StandingsItemState> = teamHitters.map {
        val playerData = it.second
        val playerBatting = playerData.stats?.batting

        StandingsItemState(
            numInfo = playerData.battingOrder.take(1).toIntOrNull(),
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
                (playerBatting?.strikeOuts ?: 0).toString()
            )
        )
    }
    val pitcherList: List<StandingsItemState> = teamPitchers.map {
        val playerData = it.second
        val playerPitching = playerData.stats?.pitching

        StandingsItemState(
            isGameStats = true,
            imageUrl = MLBUtil.playerPhotoUrl(it.first.removePrefix("ID").toIntOrNull()),
            name = playerNameDic[playerData.person?.id.toString()] ?: (playerData.person?.fullName ?: ""),
            dataList = listOf(
                playerPitching?.inningsPitched ?: "0.0",
                (playerPitching?.runs ?: 0).toString(),
                (playerPitching?.earnedRuns ?: 0).toString(),
                (playerPitching?.baseOnBalls ?: 0).toString(),
                (playerPitching?.strikeOuts ?: 0).toString(),
                (playerPitching?.hits ?: 0).toString()
            )
        )
    }

    val columnWidthList = listOf(50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp)
    val secondStatsColumnWidthList = listOf(50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp)
    val officials = displayModel?.game?.boxscore?.officials ?: emptyList()
    val gameDetailTitle = "날짜: \n\n장소: \n관중수: \n심판: "
    val gameDetailContent = buildString {
        append("${CalendarUtil.formatDate(displayModel?.game?.gameInfo?.gameDate).split(" ").firstOrNull() ?: ""}\n")
        append("${CalendarUtil.formatDate(displayModel?.game?.gameInfo?.gameDate, TimeFormatType.AMPM)}\n")
        append("${teamNameDic["venue_${displayModel?.game?.teams?.home?.id}"] ?: ""}\n")
        append("${displayModel?.game?.gameInfo?.attendance ?: 0}\n")
        officials.forEachIndexed { index, official ->
            append("• ${official.official.fullName}")
            if (index != officials.lastIndex) {
                append("\n")
            }
        }
    }

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.MLBGameStats) {
            mlbGameStatsViewModel.send(MLBGameStatsIntent.InitData(data))
        }
    }

    GameStatsViewContainer(
        state = GameStatsContainerState(
            shouldShowStats = game?.status?.detailedState != StringConstants.MLB.GAME_SCHEDULED,
            teamCategories = teamCategories,
            secondCategories = StringConstants.MLB.GAME_STATS_HITTING_CATEGORIES,
            teamCategorySelectedIndex = selectedTeamIndex,
            secondCategorySelectedIndex = firstCategorySelectedIndex,
            columnWidthList = columnWidthList,
            playerList = hitterList,
            gameDetailTitle = gameDetailTitle,
            gameDetailContent = gameDetailContent,
            firstStatsTitle = "타자",
            secondStatsTitle = "투수",
            secondStatsCategories = StringConstants.MLB.GAME_STATS_PITCHING_CATEGORIES,
            secondStatsCategorySelectedIndex = secondCategorySelectedIndex,
            secondStatsColumnWidthList = secondStatsColumnWidthList,
            secondStatsPlayerList = pitcherList,
        ),
        actions = GameStatsContainerActions(
            teamCategoryButtonAction = { index ->
                mlbGameStatsViewModel.send(MLBGameStatsIntent.SelectTeam(index))
            },
            secondCategoryButtonAction = { index ->
                mlbGameStatsViewModel.send(MLBGameStatsIntent.SelectFirstCategory(index))
            },
            refreshButtonAction = {
                displayModel?.let {
                    searchViewModel.send(SearchViewModel.Intent.RefreshGame(season = it.season, category = "baseball"))
                }
            },
            secondStatsCategoryButtonAction = { index ->
                mlbGameStatsViewModel.send(MLBGameStatsIntent.SelectSecondCategory(index))
            }
        ),
        titleContent = {
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
        },
        gameContent = {
            MLBGameStatsScoreInfoItem()
        }
    )
}

@Composable
fun MLBGameStatsScoreInfoItem(
    mlbGameStatsViewModel: MLBGameStatsViewModel = hiltViewModel()
) {
    val density = LocalDensity.current
    var borderTextWidth by remember { mutableStateOf(0.dp) }

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
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = UIConstants.Padding.DEFAULT_H_PADDING)
    ) {
        CenterColumn(
            modifier = Modifier
                .weight(0.4f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RoundedBorderText(
                    text = "원정",
                    fontSize = 11.sp,
                    radius = 4.dp,
                    textColor = Color.Gray,
                    borderColor = Color.Gray,
                    modifier = Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            with(density) {
                                borderTextWidth = layoutCoordinates.size.width.toDp()
                            }
                        }
                )
                URLImage(
                    url = MLBUtil.teamLogoUrl(awayTeamId),
                    size = URLImageSize.SMALL,
                    isSvg = true
                )
                Text(
                    text = teamNameDic["short_$awayTeamId"] ?: "",
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }

            CapsuleButton(
                text = gameStatusText,
                color = gameStatusColor,
                isDisabled = true,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {}

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.width(borderTextWidth)
                ) {
                    RoundedBorderText(
                        text = "홈",
                        fontSize = 11.sp,
                        radius = 4.dp,
                        textColor = Moare,
                        borderColor = Moare
                    )
                }
                URLImage(
                    url = MLBUtil.teamLogoUrl(homeTeamId),
                    size = URLImageSize.SMALL,
                    isSvg = true
                )
                Text(
                    text = teamNameDic["short_$homeTeamId"] ?: "",
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }
        }

        MLBGameStatsLineScoreContainer()
    }
}

@Composable
fun RowScope.MLBGameStatsLineScoreContainer(
    mlbGameStatsViewModel: MLBGameStatsViewModel = hiltViewModel()
) {
    val displayModel by mlbGameStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val game = it.game
        val isGameScheduled = game.status.detailedState == StringConstants.MLB.GAME_SCHEDULED
        val lineScore = game.linescore
        val homeTeamLineScore = lineScore.teams.home.runs
        val awayTeamLineScore = lineScore.teams.away.runs

        Row(
            modifier = Modifier
                .height(127.dp) // 25 + 1 + 50 + 1 + 50
                .weight(1f)
        ) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxHeight()
            ) {
                if (!isGameScheduled) {
                    Text(
                        text = awayTeamLineScore.toString(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 50.sp,
                        modifier = Modifier
                            .padding(start = 4.dp, end = 8.dp)
                            .width(30.dp),
                        color = if (awayTeamLineScore >= homeTeamLineScore) MaterialTheme.colors.primary else Color.Black
                    )
                } else {
                    Text(
                        text = "-",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 50.sp,
                        modifier = Modifier
                            .padding(start = 4.dp, end = 8.dp)
                            .width(30.dp),
                        color = Color.Black
                    )
                }

                Box(
                    Modifier
                        .width(42.dp) // 30 + 8 + 4
                        .height(1.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Gray)
                        .alpha(0.5f)
                )

                if (!isGameScheduled) {
                    Text(
                        text = homeTeamLineScore.toString(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 50.sp,
                        modifier = Modifier
                            .padding(start = 4.dp, end = 8.dp)
                            .width(30.dp),
                        color = if (homeTeamLineScore >= awayTeamLineScore) MaterialTheme.colors.primary else Color.Black
                    )
                } else {
                    Text(
                        text = "-",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 50.sp,
                        modifier = Modifier
                            .padding(start = 4.dp, end = 8.dp)
                            .width(30.dp),
                        color = Color.Black
                    )
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

                MLBGameStatsLineScoreItem(isHome = false, lineScoreInnings = lineScore.innings)

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
                text = "$index",
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