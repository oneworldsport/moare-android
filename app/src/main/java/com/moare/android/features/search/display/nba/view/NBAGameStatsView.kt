package com.moare.android.features.search.display.nba.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
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
import com.moare.android.core.util.NBAUtil
import com.moare.android.core.util.TimeFormatType
import com.moare.android.core.util.displayOrDash
import com.moare.android.features.search.display.common.container.state.GameStatsContainerActions
import com.moare.android.features.search.display.common.container.state.GameStatsContainerState
import com.moare.android.features.search.display.common.container.state.GameStatsTeamState
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.GameStatsViewContainer
import com.moare.android.features.search.display.nba.store.NBAGameStatsAction
import com.moare.android.features.search.display.nba.store.NBAGameStatsStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.models.models.nba.NBALineScore
import com.moare.android.ui.components.CapsuleButton
import com.moare.android.ui.components.NBATitle
import com.moare.android.ui.components.RoundedBorderText
import com.moare.android.ui.components.URLImage
import com.moare.android.ui.components.URLImageSize
import com.moare.android.ui.components.VCapsuleBar
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow

@Composable
fun NBAGameStatsView(
    searchStore: SearchStore,
    store: NBAGameStatsStore
) {
    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by store.displayModel.collectAsState()
    val firstSelectedIndex by store.firstCategorySelectedIndex.collectAsState()
    val secondCategorySelectedIndex by store.secondCategorySelectedIndex.collectAsState()
    val selectedTeamIndex by store.teamCategorySelectedIndex.collectAsState()
    val playerStats by store.playerStats.collectAsState()
    val season = displayModel.game.gameSummary?.season
    val teamNameDic by store.teamNameDic.collectAsState()
    val playerNameDic by store.playerNameDic.collectAsState()

    val teamIds = listOf(displayModel.game.gameSummary?.homeTeamId, displayModel.game.gameSummary?.visitorTeamId)
    val teamCategories = teamIds.map {
        GameStatsTeamState(
            name = teamNameDic["short_${it}"] ?: "",
            imageUrl = NBAUtil.teamLogoUrl(it)
        )
    }

    val playerList = playerStats.map { player ->
        val stats = player.statistics
        val playerId = player.personId

        StandingsItemState(
            id = playerId,
            isGameStats = true,
            imageUrl = NBAUtil.playerPhotoUrl(playerId),
            name = playerNameDic["${playerId}"] ?: player.nameI,
            extraInfo = if (player.position.isNotBlank()) "선발" else "후보",
            extraSubInfo = player.position,
            dataList = listOf(
                stats.points.toString(),
                stats.assists.toString(),
                stats.reboundsOffensive.toString(),
                stats.fieldGoalsAttempted.toString(),
                stats.fieldGoalsMade.toString(),
                stats.fieldGoalsPercentage.toString(),
                stats.threePointersAttempted.toString(),
                stats.threePointersMade.toString(),
                stats.threePointersPercentage.toString(),
                stats.freeThrowsAttempted.toString(),
                stats.freeThrowsMade.toString(),
                stats.freeThrowsPercentage.toString(),
                stats.reboundsDefensive.toString(),
                stats.blocks.toString(),
                stats.steals.toString(),
                stats.reboundsTotal.toString(),
                stats.turnovers.toString(),
                stats.foulsPersonal.toString(),
                stats.plusMinusPoints.toString(),
                stats.minutes,
            )
        )
    }
    val columnWidthList = listOf(50.dp, 50.dp, 80.dp, 70.dp, 70.dp, 80.dp, 70.dp, 70.dp, 80.dp, 80.dp, 80.dp, 100.dp, 80.dp, 50.dp, 50.dp, 70.dp, 50.dp, 50.dp, 70.dp, 70.dp)
    val officials = displayModel.game.officials
    val gameDetailTitle = "날짜: \n\n장소: \n관중수: \n심판: "
    val gameDetailContent = buildString {
        append("${CalendarUtil.formatDate(displayModel.game.gameSummary?.date).split(" ").firstOrNull() ?: ""}\n")
        append("${CalendarUtil.formatDate(displayModel.game.gameSummary?.date, TimeFormatType.AMPM)}\n")
        append("${teamNameDic["venue_${displayModel.game.gameSummary?.homeTeamId}"] ?: ""}\n")
        append("${displayModel.game.gameInfo?.attendance ?: 0}\n")
        officials.forEachIndexed { index, official ->
            append("• ${official.firstName + official.lastName}")
            if (index != officials.lastIndex) {
                append("\n")
            }
        }
    }

    /* ---------------------
       etc
       --------------------- */
    val secondSelectedCategoryPosition = with(LocalDensity.current) {
        val attackCategoriesSize = StringConstants.NBA.GAME_STATS_ATTACK_CATEGORIES.size
        val defendCategoriesSize = StringConstants.NBA.GAME_STATS_DEFEND_CATEGORIES.size

        if (secondCategorySelectedIndex in 0 until attackCategoriesSize) {
            (store.itemWidth * secondCategorySelectedIndex).toPx()
        } else if (secondCategorySelectedIndex in attackCategoriesSize until attackCategoriesSize + defendCategoriesSize) {
            ((store.itemWidth * secondCategorySelectedIndex) + store.barWidth).toPx()
        } else {
            ((store.itemWidth * secondCategorySelectedIndex) + (store.barWidth * 2)).toPx()
        }
    }.toInt()

    // scroll to category that matches with the keyword,
    // and when first category list's item is selected by click
    LaunchedEffect(firstSelectedIndex) {
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
            shouldShowStats = displayModel.game.gameSummary?.gameStatusId != Constants.GameStatus.NBA.NOT_STARTED,
            shouldShowRefreshButton = displayModel.game.gameSummary?.gameStatusId == Constants.GameStatus.NBA.LIVE,
            teamCategories = teamCategories,
            secondCategories = StringConstants.NBA.GAME_STATS_SECOND_CATEGORIES,
            teamCategorySelectedIndex = selectedTeamIndex,
            secondCategorySelectedIndex = secondCategorySelectedIndex,
            columnWidthList = columnWidthList,
            playerList = playerList,
            gameDetailTitle = gameDetailTitle,
            gameDetailContent = gameDetailContent
        ),
        actions = GameStatsContainerActions(
            teamCategoryButtonAction = { index ->
                store.send(NBAGameStatsAction.SelectTeam(index))
            },
            secondCategoryButtonAction = { index ->
                store.send(NBAGameStatsAction.SelectSecondCategory(index))
            },
            refreshButtonAction = {
                store.send(NBAGameStatsAction.RefreshGame())
            }
        ),
        titleContent = {
            /* ---------------------
               game title, info
               --------------------- */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = UIConstants.Padding.DEFAULT_H_PADDING)
            ) {
                NBATitle(
                    leagueName = "NBA",
                    leagueSeason = season?.split("-")?.firstOrNull()?.toIntOrNull() ?: CalendarUtil.currentYear
                )

                Text(
                    text = " | ${NBAUtil.gameType(displayModel.game.gameSummary)}",
                    fontSize = 14.sp
                )

                Spacer(Modifier.weight(1f))
            }

            /* ---------------------
               playoffs series text
               --------------------- */
            if (displayModel.game.gameSummary?.seriesGameNumber?.isNotEmpty() == true) {
                NBAGameStatsPlayoffsSeriesTextContainer(store)
            }
        },
        gameContent = {
//            if (displayModel.game.gameSummary?.gameStatusId == StringConstants.NBA.GAME_SCHEDULED) {
//                NBALeagueScheduleListItem(
//                    searchStore = searchStore,
//                    data = ModelConverter().nbaGameToGameScheduleConverter(displayModel!!.game),
//                    teamNameDic = teamNameDic
//                )
//            } else {
//            }
            NBAGameStatsScoreInfoItem(store)
        }
    )
}

@Composable
fun NBAGameStatsScoreInfoItem(
    store: NBAGameStatsStore
) {
    val density = LocalDensity.current
    var borderTextWidth by remember { mutableStateOf(0.dp) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by store.displayModel.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()
    val homeTeamLineScore by store.homeTeamLineScore.collectAsState()
    val awayTeamLineScore by store.awayTeamLineScore.collectAsState()

    val game = displayModel.game
    val homeTeamId = game.gameSummary?.homeTeamId
    val awayTeamId = game.gameSummary?.visitorTeamId

    /* ---------------------
       constants
       --------------------- */
    val gameStatusText = when (game.gameSummary?.gameStatusId) {
        Constants.GameStatus.NBA.NOT_STARTED -> StringConstants.GAME_NOT_STARTED_STR
        Constants.GameStatus.NBA.LIVE -> if (homeTeamLineScore?.ptsOt3 != null) {
            StringConstants.NBA.GAME_OT_3
        } else if (homeTeamLineScore?.ptsOt2 != null) {
            StringConstants.NBA.GAME_OT_2
        } else if (homeTeamLineScore?.ptsOt1 != null) {
            StringConstants.NBA.GAME_OT_1
        } else if (homeTeamLineScore?.ptsQtr4 != null) {
            StringConstants.NBA.GAME_QTR_4
        } else if (homeTeamLineScore?.ptsQtr3 != null) {
            StringConstants.NBA.GAME_QTR_3
        } else if (homeTeamLineScore?.ptsQtr2 != null) {
            StringConstants.NBA.GAME_QTR_2
        } else if (homeTeamLineScore?.ptsQtr1 != null) {
            StringConstants.NBA.GAME_QTR_1
        } else {
            ""
        }
        Constants.GameStatus.NBA.FINISHED -> StringConstants.GAME_FINISHED_STR
        else -> ""
    }

    val gameStatusColor = if (game.gameSummary?.gameStatusId == Constants.GameStatus.NBA.LIVE) {
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
                    url = NBAUtil.teamLogoUrl(homeTeamId),
                    size = URLImageSize.SMALL
                )
                Text(
                    text = teamNameDic["short_$homeTeamId"] ?: "",
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }

            // game status
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
                    url = NBAUtil.teamLogoUrl(awayTeamId),
                    size = URLImageSize.SMALL
                )
                Text(
                    text = teamNameDic["short_$awayTeamId"] ?: "",
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }
        }

        homeTeamLineScore?.let { home ->
            awayTeamLineScore?.let { away ->
                NBAGameStatsLineScoreContainer(
                    store = store,
                    homeTeamLineScore = home,
                    awayTeamLineScore = away
                )
            }
        }
    }
}

@Composable
fun RowScope.NBAGameStatsLineScoreContainer(
    store: NBAGameStatsStore,
    homeTeamLineScore: NBALineScore,
    awayTeamLineScore: NBALineScore
) {
    Row(
        modifier = Modifier
            .height(127.dp) // 25 + 1 + 50 + 1 + 50
            .weight(1f)
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(
                text = homeTeamLineScore.pts.displayOrDash,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                lineHeight = 50.sp,
                modifier = Modifier
                    .padding(start = 4.dp, end = 8.dp)
                    .width(30.dp),
                color = homeTeamLineScore.pts?.let { homePts ->
                    awayTeamLineScore.pts?.let { awayPts ->
                        if (homePts >= awayPts) MaterialTheme.colors.primary else Color.Black
                    }
                } ?: Color.Black
            )

            Box(
                Modifier
                    .width(42.dp) // 30 + 8 + 4
                    .height(1.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Gray)
                    .alpha(0.5f)
            )

            Text(
                text = awayTeamLineScore.pts.displayOrDash,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                lineHeight = 50.sp,
                modifier = Modifier
                    .padding(start = 4.dp, end = 8.dp)
                    .width(30.dp),
                color = homeTeamLineScore.pts?.let { homePts ->
                    awayTeamLineScore.pts?.let { awayPts ->
                        if (awayPts >= homePts) MaterialTheme.colors.primary else Color.Black
                    }
                } ?: Color.Black
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            NBAGameStatsLineScoreTitle(homeTeamLineScore)

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Gray)
                    .alpha(0.5f)
            )

            NBAGameStatsLineScoreItem(store = store, lineScore = homeTeamLineScore)

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Gray)
                    .alpha(0.5f)
            )

            NBAGameStatsLineScoreItem(store = store, lineScore = awayTeamLineScore)
        }
    }
}

@Composable
fun NBAGameStatsLineScoreTitle(
    lineScore: NBALineScore
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(25.dp)
    ) {
        for (index in 1 until 5) {
            VCapsuleBar(modifier = Modifier.alpha(0.5f))
            Text(
                text = "$index",
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
        }

        if (lineScore.ptsOt1 != null && lineScore.ptsOt1 != 0) {
            VCapsuleBar(modifier = Modifier.alpha(0.5f))
            Text(
                text = "1OT",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
        }

        if (lineScore.ptsOt2 != null && lineScore.ptsOt2 != 0) {
            VCapsuleBar(modifier = Modifier.alpha(0.5f))
            Text(
                text = "2OT",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
        }

        if (lineScore.ptsOt3 != null && lineScore.ptsOt3 != 0) {
            VCapsuleBar(modifier = Modifier.alpha(0.5f))
            Text(
                text = "3OT",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun NBAGameStatsLineScoreItem(
    store: NBAGameStatsStore,
    lineScore: NBALineScore
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(store.lineScoreItemHeight)
    ) {
        VCapsuleBar(modifier = Modifier.alpha(0.5f))
        Text(
            text = lineScore.ptsQtr1.displayOrDash,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        VCapsuleBar(modifier = Modifier.alpha(0.5f))
        Text(
            text = lineScore.ptsQtr2.displayOrDash,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        VCapsuleBar(modifier = Modifier.alpha(0.5f))
        Text(
            text = lineScore.ptsQtr3.displayOrDash,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        VCapsuleBar(modifier = Modifier.alpha(0.5f))
        Text(
            text = lineScore.ptsQtr4.displayOrDash,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        // TODO: 홈, 원정 둘중에 하나는 0이 아닌데 다른 팀은 0일때 0인팀의 UI가 깨짐
        if (lineScore.ptsOt1 != null && lineScore.ptsOt1 != 0) {
            VCapsuleBar(modifier = Modifier.alpha(0.5f))
            Text(
                text = lineScore.ptsOt1.toString(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }

        if (lineScore.ptsOt2 != null && lineScore.ptsOt2 != 0) {
            VCapsuleBar(modifier = Modifier.alpha(0.5f))
            Text(
                text = lineScore.ptsOt2.toString(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }

        if (lineScore.ptsOt3 != null && lineScore.ptsOt3 != 0) {
            VCapsuleBar(modifier = Modifier.alpha(0.5f))
            Text(
                text = lineScore.ptsOt3.toString(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun NBAGameStatsPlayoffsSeriesTextContainer(
    store: NBAGameStatsStore
) {
    val displayModel by store.displayModel.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    displayModel.game.seasonSeries?.let {
        CenterRow(
            modifier = Modifier.padding(horizontal = UIConstants.Padding.DEFAULT_H_PADDING)
        ) {
            // NOTE: 게임별 시리즈 스코어 정보를 가져올 방법을 찾지 못해서 일단은 현재 시리즈 스코어로 표시
            Text(
                text = "현재 시리즈 스코어: ",
                fontSize = 14.sp
            )

            Text(
                text = teamNameDic["short_${it.homeTeamId}"] ?: "",
                fontSize = 14.sp
            )

            Text(
                text = " ${it.homeTeamWins} ",
                color = if (it.homeTeamWins >= it.homeTeamLosses) Moare else Color.Black
            )

            Text(
                text = "-",
                fontSize = 14.sp
            )

            Text(
                text = " ${it.homeTeamLosses} ",
                color = if (it.homeTeamLosses >= it.homeTeamWins) Moare else Color.Black
            )

            Text(
                text = teamNameDic["short_${it.visitorTeamId}"] ?: "",
                fontSize = 14.sp
            )

            Spacer(Modifier.weight(1f))
        }
    }
}














