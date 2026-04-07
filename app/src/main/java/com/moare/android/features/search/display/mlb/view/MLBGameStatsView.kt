package com.moare.android.features.search.display.mlb.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.constants.UIConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.FormatSeriesResult
import com.moare.android.core.util.MLBUtil
import com.moare.android.core.util.OutputTimeFormatType
import com.moare.android.features.search.display.common.container.state.GameStatsContainerActions
import com.moare.android.features.search.display.common.container.state.GameStatsContainerState
import com.moare.android.features.search.display.common.container.state.GameStatsTeamState
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.GameStatsViewContainer
import com.moare.android.features.search.display.mlb.store.MLBGameStatsAction
import com.moare.android.features.search.display.mlb.store.MLBGameStatsStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.models.models.mlb.MLBGameLineScoreInning
import com.moare.android.ui.common.components.BaseballLeagueTitleForGameStats
import com.moare.android.ui.common.components.CapsuleButton
import com.moare.android.ui.common.components.GameStatusCapsuleButton
import com.moare.android.ui.common.components.GameStatusContext
import com.moare.android.ui.common.components.RoundedBorderText
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterColumn

@Composable
fun MLBGameStatsView(
    searchStore: SearchStore,
    store: MLBGameStatsStore
) {
    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by store.displayModel.collectAsState()
    val firstCategorySelectedIndex by store.firstCategorySelectedIndex.collectAsState()
    val secondCategorySelectedIndex by store.secondCategorySelectedIndex.collectAsState()
    val selectedTeamIndex by store.teamCategorySelectedIndex.collectAsState()
    val teamHitters by store.teamHitters.collectAsState()
    val teamPitchers by store.teamPitchers.collectAsState()
    val playerNameDic by store.playerNameDic.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()
    val isRefreshing by store.isRefreshing.collectAsState()

    val game = displayModel.game
    val season = game.game.season.toIntOrNull()

    val teamIds = listOf(displayModel.game.teams.home.id, displayModel.game.teams.away.id)
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
    val officials = displayModel.game.boxscore?.officials ?: emptyList()
    val gameDetailTitle = "날짜: \n\n장소: \n관중수: \n심판: "
    val gameDetailContent = buildString {
        append("${CalendarUtil.formatDate(displayModel.game.gameInfo.gameDate).split(" ").firstOrNull() ?: ""}\n")
        append("${CalendarUtil.formatDate(displayModel.game.gameInfo.gameDate, outputFormatType = OutputTimeFormatType.AMPM)}\n")
        append("${teamNameDic["venue_${displayModel.game.teams.home.id}"] ?: ""}\n")
        append("${displayModel.game.gameInfo.attendance}\n")
        officials.forEachIndexed { index, official ->
            append("• ${official.official.fullName}")
            if (index != officials.lastIndex) {
                append("\n")
            }
        }
    }

    /* ---------------------
   etc
   --------------------- */
    val firstSelectedCategoryPosition = with(LocalDensity.current) {
        (store.itemWidth * firstCategorySelectedIndex).toPx()
    }.toInt()

    val secondSelectedCategoryPosition = with(LocalDensity.current) {
        (store.itemWidth * secondCategorySelectedIndex).toPx()
    }.toInt()

    // scroll to category that matches with the keyword,
    // and when first category list's item is selected by click
    LaunchedEffect(firstCategorySelectedIndex) {
        if (store.shouldScrollCategory) {
            horizontalScrollState.animateScrollTo(
                value = firstSelectedCategoryPosition,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearOutSlowInEasing
                )
            )
        }
    }

    LaunchedEffect(secondCategorySelectedIndex) {
        if (store.shouldScrollCategory) {
            horizontalScrollState.animateScrollTo(
                value = secondSelectedCategoryPosition,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearOutSlowInEasing
                )
            )
        }
    }

    GameStatsViewContainer(
        state = GameStatsContainerState(
            shouldShowStats = game.status.detailedState != StringConstants.MLB.GAME_SCHEDULED,
            shouldShowRefreshButton = game.status.detailedState == StringConstants.MLB.GAME_LIVE,
            teamCategories = teamCategories,
            teamCategorySelectedIndex = selectedTeamIndex,
            gameDetailTitle = gameDetailTitle,
            gameDetailContent = gameDetailContent,
            firstStatsTitle = "타자",
            firstStatsCategories = StringConstants.MLB.GAME_STATS_HITTING_CATEGORIES,
            firstStatsCategorySelectedIndex = firstCategorySelectedIndex,
            firstStatsColumnWidthList = columnWidthList,
            firstStatsPlayerList = hitterList,
            secondStatsTitle = "투수",
            secondStatsCategories = StringConstants.MLB.GAME_STATS_PITCHING_CATEGORIES,
            secondStatsCategorySelectedIndex = secondCategorySelectedIndex,
            secondStatsColumnWidthList = secondStatsColumnWidthList,
            secondStatsPlayerList = pitcherList,
        ),
        actions = GameStatsContainerActions(
            teamCategoryButtonAction = { index ->
                store.send(MLBGameStatsAction.SelectTeam(index))
            },
            firstStatsTitleCategoryAction = {
                store.send(MLBGameStatsAction.SortByBattingOrder)
            },
            firstStatsCategoryButtonAction = { index ->
                store.send(MLBGameStatsAction.SelectFirstCategory(index))
            },
            secondStatsTitleCategoryAction = {
                store.send(MLBGameStatsAction.SortByPitcherOrder)
            },
            secondStatsCategoryButtonAction = { index ->
                store.send(MLBGameStatsAction.SelectSecondCategory(index))
            },
            refreshButtonAction = {
                store.send(MLBGameStatsAction.RefreshGame())
            },
            isRefreshing = isRefreshing,
        ),
        titleContent = {
            Column {
                BaseballLeagueTitleForGameStats(
                    url = MLBUtil.mlbLogoUrl,
                    name = "MLB",
                    leagueSeason = game.game.season.toIntOrNull(),
                    seriesDescription = game.game.seriesDescription
                )

                if (game.game.seriesStatus.isNotEmpty()) {
                    FormatSeriesResult(
                        seriesStatus = game.game.seriesStatus,
                        homeTeamId = game.teams.home.id,
                        awayTeamId = game.teams.away.id,
                        teamNameDic = teamNameDic
                    )
                }
            }
        },
        gameContent = {
//            if (game.status.detailedState == StringConstants.MLB.GAME_SCHEDULED) {
//                MLBLeagueScheduleListItem(
//                    searchStore = searchStore,
//                    data = ModelConverter().mlbGameToGameScheduleConverter(game),
//                    teamNameDic = teamNameDic
//                )
//            } else {
//
//            }
            MLBGameStatsScoreInfoItem(store)
        }
    )
}

@Composable
fun MLBGameStatsScoreInfoItem(
    store: MLBGameStatsStore
) {
    val density = LocalDensity.current
    var borderTextWidth by remember { mutableStateOf(0.dp) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by store.displayModel.collectAsState()
    val game = displayModel.game
    val homeTeamId = Constants.Ids.checkTeamId(Constants.Ids.MLB, game.teams.home.id)
    val awayTeamId = Constants.Ids.checkTeamId(Constants.Ids.MLB, game.teams.away.id)
    val gameStatus = game.status.detailedState
    val teamNameDic by store.teamNameDic.collectAsState()

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
                    size = URLImageSize.SMALL
                )
                Text(
                    text = if (awayTeamId == null) "미정" else teamNameDic["short_$awayTeamId"] ?: "",
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }

            GameStatusCapsuleButton(
                gameStatusContext = GameStatusContext.Mlb(status = gameStatus, linescore = game.linescore),
                leagueId = Constants.Ids.MLB,
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
                    size = URLImageSize.SMALL
                )
                Text(
                    text = if (homeTeamId == null) "미정" else teamNameDic["short_$homeTeamId"] ?: "",
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }
        }

        MLBGameStatsLineScoreContainer(store)
    }
}

@Composable
fun RowScope.MLBGameStatsLineScoreContainer(
    store: MLBGameStatsStore
) {
    val displayModel by store.displayModel.collectAsState()

    val game = displayModel.game
    val lineScore = game.linescore

    Row(
        modifier = Modifier
            .height(127.dp) // 25 + 1 + 50 + 1 + 50
            .weight(1f)
    ) {
        Column(
            Modifier.weight(1f)
        ) {
            MLBGameStatsLineScoreTitle(lineScore?.innings ?: emptyList())

            MLBGameStatsLineScoreItem(
                store = store,
                isHome = false,
                lineScoreInnings = lineScore?.innings ?: emptyList()
            )

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Gray)
                    .alpha(0.5f)
            )

            MLBGameStatsLineScoreItem(
                store = store,
                isHome = true,
                lineScoreInnings = lineScore?.innings ?: emptyList()
            )
        }
    }
}

@Composable
fun MLBGameStatsLineScoreTitle(
    lineScoreInnings: List<MLBGameLineScoreInning>
) {
    val maxInnings = maxOf(9, lineScoreInnings.size) // 둘 중 더 큰 값 선택

    Box {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Gray)
                .alpha(0.5f)
                .align(Alignment.BottomStart)
        )

        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .height(25.dp)
        ) {
            for (index in 0..maxInnings) {
                if (index == 0) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colors.background)
                                .align(Alignment.BottomCenter) // 1회 왼쪽으로 그어지는 하단 선 가리는 박스
                        )
                    }
                } else {
                    VCapsuleBar(modifier = Modifier.alpha(0.5f))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$index",
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MLBGameStatsLineScoreItem(
    store: MLBGameStatsStore,
    isHome: Boolean,
    lineScoreInnings: List<MLBGameLineScoreInning>
) {
    val displayModel by store.displayModel.collectAsState()

    val game = displayModel.game
    val isGameScheduled = game.status.detailedState == StringConstants.MLB.GAME_SCHEDULED
    val lineScore = game.linescore
    val homeTeamLineScore = lineScore?.teams?.home?.runs ?: 0
    val awayTeamLineScore = lineScore?.teams?.away?.runs ?: 0

    val maxInnings = maxOf(9, lineScoreInnings.size)

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(store.lineScoreItemHeight)
    ) {
        for (index in 0..maxInnings) {

            val text: String
            val color: Color

            if (index == 0) {
                text = if (!isGameScheduled) {
                    if (isHome) homeTeamLineScore.toString()
                    else awayTeamLineScore.toString()
                } else {
                    "-"
                }

                color = if (!isGameScheduled) {
                    if (isHome) {
                        if (homeTeamLineScore >= awayTeamLineScore)
                            MaterialTheme.colors.primary else Color.Black
                    } else {
                        if (awayTeamLineScore >= homeTeamLineScore)
                            MaterialTheme.colors.primary else Color.Black
                    }
                } else {
                    Color.Black
                }

            } else {
                VCapsuleBar(modifier = Modifier.alpha(0.5f))

                val item = lineScoreInnings.getOrNull(index - 1)

                text = if (item != null && !isGameScheduled) {
                    if (isHome) item.home.runs.toString()
                    else item.away.runs.toString()
                } else {
                    "-"
                }

                color = Color.Black
            }

            Text(
                text = text,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                color = color,
                modifier = Modifier.weight(1f)
            )
        }
    }
}