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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.common.collect.Multimaps.index
import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.constants.UIConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.NBAUtil
import com.moare.android.core.util.OutputTimeFormatType
import com.moare.android.core.util.displayOrDash
import com.moare.android.core.util.format3
import com.moare.android.features.search.display.common.container.state.GameStatsContainerActions
import com.moare.android.features.search.display.common.container.state.GameStatsContainerState
import com.moare.android.features.search.display.common.container.state.GameStatsTeamState
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.GameStatsViewContainer
import com.moare.android.features.search.display.nba.store.NBAGameStatsAction
import com.moare.android.features.search.display.nba.store.NBAGameStatsStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.models.models.nba.NBALineScore
import com.moare.android.ui.common.components.CapsuleButton
import com.moare.android.ui.common.components.GameStatusCapsuleButton
import com.moare.android.ui.common.components.GameStatusContext
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.RoundedBorderText
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.common.components.VCapsuleBar
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
    val firstCategorySelectedIndex by store.firstCategorySelectedIndex.collectAsState()
    val selectedTeamIndex by store.teamCategorySelectedIndex.collectAsState()
    val playerStats by store.playerStats.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()
    val playerNameDic by store.playerNameDic.collectAsState()
    val isRefreshing by store.isRefreshing.collectAsState()

    val teamIds = listOf(displayModel.game.gameSummary?.homeTeamId, displayModel.game.gameSummary?.awayTeamId)
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
            extraInfo = if (player.isStarter) "선발" else "후보",
            extraSubInfo = player.position,
            dataList = listOf(
                stats.minutes,
                stats.points.toString(),
                stats.assists.toString(),
                stats.reboundsTotal.toString(),
                "",
                "${stats.fieldGoalsMade}/${stats.fieldGoalsAttempted}(${stats.fieldGoalsPercentage.format3()})",
                "${stats.threePointersMade}/${stats.threePointersAttempted}(${stats.threePointersPercentage.format3()})",
                "${stats.freeThrowsMade}/${stats.freeThrowsAttempted}(${stats.freeThrowsPercentage.format3()})",
                "",
                stats.steals.toString(),
                stats.blocks.toString(),
                "",
                stats.turnovers.toString(),
                stats.foulsPersonal.toString(),
                "",
                "${stats.reboundsOffensive}/${stats.reboundsDefensive}",
                stats.plusMinusPoints.toString()
            )
        )
    }
    val columnWidthList = listOf(70.dp, 50.dp, 50.dp, 70.dp, 50.dp, 110.dp, 110.dp, 110.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 100.dp, 70.dp)
    val officials = displayModel.game.officials
    val gameDetailTitle = "날짜: \n\n장소: \n관중수: \n심판: "
    val gameDetailContent = buildString {
        append("${CalendarUtil.formatDate(displayModel.game.gameSummary?.gameDate).split(" ").firstOrNull() ?: ""}\n")
        append("${CalendarUtil.formatDate(displayModel.game.gameSummary?.gameDate, outputFormatType = OutputTimeFormatType.AMPM)}\n")
        append("${teamNameDic["venue_${displayModel.game.gameSummary?.homeTeamId}"] ?: ""}\n")
        append("${displayModel.game.gameSummary?.attendance ?: 0}\n")
        officials?.forEachIndexed { index, official ->
//            append("• ${official.firstName + official.lastName}")
            append("• ${official.name}")
            if (index != officials.lastIndex) {
                append("\n")
            }
        }
    }

    /* ---------------------
       etc
       --------------------- */
    val firstSelectedCategoryPosition = with(LocalDensity.current) {
        val attackCategoriesSize = StringConstants.NBA.GAME_STATS_ATTACK_CATEGORIES.size
        val defendCategoriesSize = StringConstants.NBA.GAME_STATS_DEFEND_CATEGORIES.size

        if (firstCategorySelectedIndex in 0 until attackCategoriesSize) {
            (store.itemWidth * firstCategorySelectedIndex).toPx()
        } else if (firstCategorySelectedIndex in attackCategoriesSize until attackCategoriesSize + defendCategoriesSize) {
            ((store.itemWidth * firstCategorySelectedIndex) + store.barWidth).toPx()
        } else {
            ((store.itemWidth * firstCategorySelectedIndex) + (store.barWidth * 2)).toPx()
        }
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

    GameStatsViewContainer(
        state = GameStatsContainerState(
            shouldShowStats = displayModel.game.gameSummary?.gameStatus != Constants.GameStatus.NBA.NOT_STARTED,
            shouldShowRefreshButton = displayModel.game.gameSummary?.gameStatus == Constants.GameStatus.NBA.LIVE,
            teamCategories = teamCategories,
            firstStatsCategories = StringConstants.NBA.GAME_STATS_CATEGORIES,
            teamCategorySelectedIndex = selectedTeamIndex,
            firstStatsCategorySelectedIndex = firstCategorySelectedIndex,
            firstStatsColumnWidthList = columnWidthList,
            firstStatsPlayerList = playerList,
            gameDetailTitle = gameDetailTitle,
            gameDetailContent = gameDetailContent
        ),
        actions = GameStatsContainerActions(
            teamCategoryButtonAction = { index ->
                store.send(NBAGameStatsAction.SelectTeam(index))
            },
            firstStatsTitleCategoryAction = {
                store.send(NBAGameStatsAction.SelectTitleCategory)
            },
            firstStatsCategoryButtonAction = { index ->
                store.send(NBAGameStatsAction.SelectFirstCategory(index))
            },
            refreshButtonAction = {
                store.send(NBAGameStatsAction.RefreshGame())
            },
            isRefreshing = isRefreshing
        ),
        titleContent = {
            val gameSummary = displayModel.game.gameSummary
            val gameType = gameSummary?.let {
                if (gameSummary.isPlayoffs) {
                    "${gameSummary.gameLabelKr} | ${gameSummary.seriesGameNumber}"
                } else {
                    gameSummary.weekName
                }
            } ?: ""

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = UIConstants.Padding.DEFAULT_H_PADDING)
            ) {
                NBATitle(
                    leagueName = "NBA",
                    leagueSeason = displayModel.season
                )

                Text(
                    text = " | $gameType",
                    fontSize = 14.sp
                )

                Spacer(Modifier.weight(1f))
            }

            gameSummary?.let {
                if (it.isPlayoffs) {
                    NBAGameStatsPlayoffsSeriesText(it.seriesTextKr)
                }
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
    val awayTeamId = game.gameSummary?.awayTeamId

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
            GameStatusCapsuleButton(
                gameStatusContext = GameStatusContext.Nba(
                    status = game.gameSummary?.gameStatus ?: 1,
                    period = game.gameSummary?.period
                ),
                leagueId = Constants.Ids.NBA,
                isDisabled = true,
                modifier = Modifier.padding(vertical = 4.dp)
            ) { }

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
    Column(
        modifier = Modifier.weight(1f)
    ) {
        NBAGameStatsLineScoreTitle(homeTeamLineScore)

        NBAGameStatsLineScoreItem(store = store, lineScore = homeTeamLineScore, isHome = true)

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Gray)
                .alpha(0.5f)
        )

        NBAGameStatsLineScoreItem(store = store, lineScore = awayTeamLineScore, isHome = false)
    }
}

@Composable
fun NBAGameStatsLineScoreTitle(
    lineScore: NBALineScore
) {
    val ptsOtCount = lineScore.ptsOtList.count { it != null }

    val maxPts = 4 + ptsOtCount

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
            for (index in 0..maxPts) {
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

                    val text: String
                    val fontSize: TextUnit

                    if (index <= 4) {
                        text = "$index"
                        fontSize = 15.sp
                    } else {
                        text = "${index - 4}OT"
                        fontSize = 14.sp
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = text,
                            textAlign = TextAlign.Center,
                            fontSize = fontSize,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NBAGameStatsLineScoreItem(
    store: NBAGameStatsStore,
    lineScore: NBALineScore,
    isHome: Boolean
) {
    val homeTeamLineScore by store.homeTeamLineScore.collectAsState()
    val awayTeamLineScore by store.awayTeamLineScore.collectAsState()

    val homePts = homeTeamLineScore?.pts
    val awayPts = awayTeamLineScore?.pts

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(store.lineScoreItemHeight)
    ) {
        val ptsOtCount = lineScore.ptsOtList.count { it != null }

        val maxPts = 4 + ptsOtCount

        for (index in 0..maxPts) {
            if (index == 0) {

                val text: String = if (isHome) {
                    homePts.toString()
                } else {
                    awayPts.toString()
                }

                val color: Color = if (isHome) {
                    if (homePts != null && awayPts != null && homePts >= awayPts)
                        MaterialTheme.colors.primary else Color.Black
                } else {
                    if (homePts != null && awayPts != null && awayPts >= homePts)
                        MaterialTheme.colors.primary else Color.Black
                }

                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    color = color
                )
            } else {
                VCapsuleBar(modifier = Modifier.alpha(0.5f))

                val ptsValue = lineScore.ptsList.getOrNull(index - 1)

                Text(
                    text = ptsValue.displayOrDash,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                // TODO: ptsOt1 에서 홈, 원정 둘중에 하나는 0이 아닌데 다른 팀은 0일때 0인팀의 UI가 깨짐
            }
        }
    }
}

@Composable
fun NBAGameStatsPlayoffsSeriesText(
    seriesTextKr: String
) {
    val result = Regex("""^(.*?)\s+(\d+)\s*-\s*(\d+)\s+(.*)$""")
        .find(seriesTextKr)

    if (result != null) {
        // ex) "시리즈 스코어: LA 레이커스 1 - 2 휴스턴"
        val before = result.groupValues[1].trim() // "시리즈 스코어: LA 레이커스"
        val score1 = result.groupValues[2].toInt() // 1
        val score2 = result.groupValues[3].toInt() // 2
        val after = result.groupValues[4].trim() // "휴스턴"

        CenterRow(
            modifier = Modifier.padding(horizontal = UIConstants.Padding.DEFAULT_H_PADDING)
        ) {
            Text(
                text = before,
                fontSize = 14.sp
            )

            Text(
                text = " $score1",
                color = if (score1 >= score2) Moare else Color.Black
            )

            Text(
                text = " - ",
                fontSize = 14.sp
            )

            Text(
                text = "$score2 ",
                color = if (score2 >= score1) Moare else Color.Black
            )

            Text(
                text = after,
                fontSize = 14.sp
            )

            Spacer(Modifier.weight(1f))
        }
    }
}














