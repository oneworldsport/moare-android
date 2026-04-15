package com.moare.android.features.search.display.kbo.view

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
import com.moare.android.core.util.KBOUtil
import com.moare.android.core.util.OutputTimeFormatType
import com.moare.android.features.search.display.common.container.state.GameStatsContainerActions
import com.moare.android.features.search.display.common.container.state.GameStatsContainerState
import com.moare.android.features.search.display.common.container.state.GameStatsTeamState
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.GameStatsViewContainer
import com.moare.android.features.search.display.kbo.store.KBOGameStatsAction
import com.moare.android.features.search.display.kbo.store.KBOGameStatsStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.models.models.kbo.KBOGameLineScore
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
fun KBOGameStatsView(
    searchStore: SearchStore,
    store: KBOGameStatsStore
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
    val teamNameDic by store.teamNameDic.collectAsState()
    val isRefreshing by store.isRefreshing.collectAsState()

    val game = displayModel.game

    val teamIds = listOf(displayModel.game.gameInfo?.homeTeamId, displayModel.game.gameInfo?.awayTeamId)
    val teamCategories = teamIds.map {
        GameStatsTeamState(
            name = teamNameDic["short_${it}"] ?: "",
            imageUrl = KBOUtil.teamLogoUrl(it)
        )
    }

    val hitterList: List<StandingsItemState> = teamHitters.map {
        StandingsItemState(
            numInfo = it.battingNumber,
            imageUrl = KBOUtil.playerPhotoUrl(displayModel.season, it.id),
            name = it.name,
            extraInfo = it.position
                .replace("#", "•")
                .replace("지명타자", "지명"),
            dataList = listOf(
                it.ab.toString(),
                it.h.toString(),
//                it.doubles.toString(), // live 제공 X
                it.homeRuns.toString(),
                it.rbi.toString(),
                it.r.toString(),
                it.baseOnBalls.toString(),
                it.strikeOuts.toString(),
                it.groundIntoDoublePlay.toString(),
//                it.hitByPitch.toString() // live 제공 X
            )
        )
    }
    val pitcherList: List<StandingsItemState> = teamPitchers.map {
        StandingsItemState(
            imageUrl = KBOUtil.playerPhotoUrl(displayModel.season, it.id),
            name = it.name,
            dataList = listOf(
                it.ip, it.r, it.er, it.bb, it.so, it.h
            )
        )
    }

    val columnWidthList = listOf(50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp)
    val secondStatsColumnWidthList = listOf(50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp)
    val gameDetailTitle = "날짜: \n\n장소: "
    val gameDetailContent = buildString {
        append("${CalendarUtil.formatDate(displayModel.game.gameInfo?.date).split(" ").firstOrNull() ?: ""}\n")
        append("${CalendarUtil.formatDate(displayModel.game.gameInfo?.date, outputFormatType = OutputTimeFormatType.AMPM)}\n")
        append(teamNameDic["venue_${displayModel.game.gameInfo?.homeTeamId}"] ?: "")
    }

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
            shouldShowStats = game.gameInfo?.gameStatus?.toIntOrNull() == StringConstants.KBO.GAME_LIVE || game.gameInfo?.gameStatus?.toIntOrNull() == StringConstants.KBO.GAME_FINAL,
            shouldShowRefreshButton = game.gameInfo?.gameStatus?.toIntOrNull() == StringConstants.KBO.GAME_LIVE,
            teamCategories = teamCategories,
            teamCategorySelectedIndex = selectedTeamIndex,
            firstStatsPlayerList = hitterList,
            gameDetailTitle = gameDetailTitle,
            gameDetailContent = gameDetailContent,
            noStatsText = if (game.gameInfo?.gameStatus?.toIntOrNull() == StringConstants.KBO.GAME_CANCELED) "취소된 경기입니다." else null,
            firstStatsTitle = "타자",
            firstStatsCategories = StringConstants.KBO.GAME_STATS_HITTING_CATEGORIES,
            firstStatsCategorySelectedIndex = firstCategorySelectedIndex,
            firstColumnWidth = 150.dp,
            firstStatsColumnWidthList = columnWidthList,
            secondStatsTitle = "투수",
            secondStatsCategories = StringConstants.KBO.GAME_STATS_PITCHING_CATEGORIES,
            secondStatsCategorySelectedIndex = secondCategorySelectedIndex,
            secondStatsColumnWidthList = secondStatsColumnWidthList,
            secondStatsPlayerList = pitcherList,
        ),
        actions = GameStatsContainerActions(
            teamCategoryButtonAction = { index ->
                store.send(KBOGameStatsAction.SelectTeam(index))
            },
            firstStatsTitleCategoryAction = {
                store.send(KBOGameStatsAction.SortByBattingOrder)
            },
            firstStatsCategoryButtonAction = { index ->
                store.send(KBOGameStatsAction.SelectFirstCategory(index))
            },
            secondStatsTitleCategoryAction = {
                store.send(KBOGameStatsAction.SortByPitcherOrder)
            },
            secondStatsCategoryButtonAction = { index ->
                store.send(KBOGameStatsAction.SelectSecondCategory(index))
            },
            refreshButtonAction = {
                store.send(KBOGameStatsAction.RefreshGame())
            },
            isRefreshing = isRefreshing
        ),
        titleContent = {
            Column {
                BaseballLeagueTitleForGameStats(
                    url = KBOUtil.kboLogoUrl,
                    name = "KBO",
                    leagueSeason = displayModel.season,
                    seriesDescription = game.gameInfo?.seriesDescription ?: ""
                )
            }
        },
        gameContent = {
//            if (
//                game?.gameInfo?.gameStatus?.toIntOrNull() == StringConstants.KBO.GAME_SCHEDULED ||
//                game?.gameInfo?.gameStatus?.toIntOrNull() == StringConstants.KBO.GAME_CANCELED
//            ) {
//                KBOLeagueScheduleListItem(
//                    searchStore = searchStore,
//                    data = ModelConverter().kboGameToGameScheduleConverter(game),
//                    teamNameDic = teamNameDic
//                )
//            } else {
//                
//            }
            KBOGameStatsScoreInfoItem(store)
        }
    )
}

@Composable
fun KBOGameStatsScoreInfoItem(
    store: KBOGameStatsStore
) {
    val density = LocalDensity.current
    var borderTextWidth by remember { mutableStateOf(0.dp) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by store.displayModel.collectAsState()
    val game = displayModel.game
    val homeTeamId = Constants.Ids.checkTeamId(Constants.Ids.KBO, game.gameInfo?.homeTeamId)
    val awayTeamId = Constants.Ids.checkTeamId(Constants.Ids.KBO, game.gameInfo?.awayTeamId)
    val gameStatus = game.gameInfo?.gameStatus?.toIntOrNull() ?: 0
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
                    url = KBOUtil.teamLogoUrl(awayTeamId),
                    size = URLImageSize.SMALL
                )
                Text(
                    text = if (awayTeamId == null) "미정" else teamNameDic["short_$awayTeamId"] ?: "",
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }

            GameStatusCapsuleButton(
                gameStatusContext = GameStatusContext.Kbo(gameStatus.toString()),
                leagueId = Constants.Ids.KBO,
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
                    url = KBOUtil.teamLogoUrl(homeTeamId),
                    size = URLImageSize.SMALL
                )
                Text(
                    text = if (homeTeamId == null) "미정" else teamNameDic["short_$homeTeamId"] ?: "",
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }
        }

        KBOGameStatsLineScoreContainer(store)
    }
}

@Composable
fun RowScope.KBOGameStatsLineScoreContainer(
    store: KBOGameStatsStore
) {
    val displayModel by store.displayModel.collectAsState()

    val game = displayModel.game

    game.lineScore?.let { lineScore ->
        Row(
            modifier = Modifier
                .height(127.dp) // 25 + 1 + 50 + 1 + 50
                .weight(1f)
        ) {
            Column(
                Modifier.weight(1f)
            ) {
                KBOGameStatsLineScoreTitle(lineScore.away)

                KBOGameStatsLineScoreItem(store = store, lineScore = lineScore.away, isHome = false)

                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Gray)
                        .alpha(0.5f)
                )

                KBOGameStatsLineScoreItem(store = store, lineScore = lineScore.home, isHome = true)
            }
        }
    }
}

@Composable
fun KBOGameStatsLineScoreTitle(
    lineScore: KBOGameLineScore
) {
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
            for (index in 0..12) {
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
                } else if (index < 10 ||
                    (index == 10 && lineScore.inning10 != "-") ||
                    (index == 11 && lineScore.inning11 != "-") ||
                    (index == 12 && lineScore.inning12 != "-")
                ) {
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
fun KBOGameStatsLineScoreItem(
    store: KBOGameStatsStore,
    lineScore: KBOGameLineScore,
    isHome: Boolean
) {
    val displayModel by store.displayModel.collectAsState()

    val game = displayModel.game

    game.lineScore?.let { gameLineScore ->
        val homeTeamLineScore = gameLineScore.home.r.toIntOrNull() ?: 0
        val awayTeamLineScore = gameLineScore.away.r.toIntOrNull() ?: 0

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(store.lineScoreItemHeight)
        ) {
            for (index in 0 .. 12) {
                val text: String
                val color: Color

                if (index == 0) {
                    text = if (isHome) {
                        homeTeamLineScore.toString()
                    } else {
                        awayTeamLineScore.toString()
                    }

                    color = if (isHome) {
                        if (homeTeamLineScore >= awayTeamLineScore)
                            MaterialTheme.colors.primary else Color.Black
                    } else {
                        if (awayTeamLineScore >= homeTeamLineScore)
                            MaterialTheme.colors.primary else Color.Black
                    }

                    Text(
                        text = text,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        color = color,
                        modifier = Modifier.weight(1f)
                    )

                } else if (index < 10 ||
                    (index == 10 && lineScore.inning10 != "-") ||
                    (index == 11 && lineScore.inning11 != "-") ||
                    (index == 12 && lineScore.inning12 != "-")
                ) {
                    VCapsuleBar(modifier = Modifier.alpha(0.5f))

                    Text(
                        text = lineScore.innings[index - 1],
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}