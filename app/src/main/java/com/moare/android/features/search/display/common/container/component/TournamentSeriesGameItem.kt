package com.moare.android.features.search.display.common.container.component

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.R
import com.moare.android.core.constants.Constants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.Util
import com.moare.android.features.search.display.common.container.view.RoundSeriesKey
import com.moare.android.features.search.models.models.common.GameForSchedule
import com.moare.android.features.search.models.models.football.FBGameInfoForSchedule
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow
import com.moare.android.ui.util.nullableMaxHeight

@Composable
fun <T> TournamentSeriesLeftGameItem(
    leagueId: Int,
    teamNameDic: Map<String, String>,
    maxRound: Int,
    games: List<GameForSchedule<T>>?,
    itemPosition: RoundSeriesKey, // ui상에서 시리즈의 위치 ex) 1라운드의 첫번째 시리즈면 1_1
    shouldRemoveBar: Boolean = false, // NOTE: MLB의 경우 이전 라운드에 시리즈가 하나 없으면 하단에 HBar가 필요없는 경우가 있음. KBO는 그냥 필요없음.
    itemHeights: Map<RoundSeriesKey, Dp>,
    modifier: Modifier = Modifier,
    onItemHeightChange: (RoundSeriesKey, Dp) -> Unit,
    selectSeries: ((List<GameForSchedule<T>>) -> Unit)? = null
) {
    val scoreTitleHeight = 16.dp

    val density = LocalDensity.current
    var itemHeight by remember { mutableStateOf(0.dp) }
    var isScoreOpened by remember { mutableStateOf(false) }

    // ui
    if (games != null) {
        val game = games.firstOrNull()
        val topSeedTeamId = if (game?.isHomeTopSeed == true) game.homeTeamIdOrNull else game?.awayTeamIdOrNull
        val bottomSeedTeamId = if (game?.isHomeTopSeed == true) game.awayTeamIdOrNull else game?.homeTeamIdOrNull
        val isUEFALeague = leagueId in Constants.Ids.FOOTBALL_UEFA_LEAGUES
        val isSeriesStarted = if (isUEFALeague) {
            // UEFA리그(합산 스코어 방식)는 경기중이어도 isSeriesStarted = true
            !Constants.GameStatus.isBeforeGame(leagueId = leagueId, status = game?.gameStatus ?: "")
        } else {
            Constants.GameStatus.isGameFinished(leagueId = leagueId, status = game?.gameStatus ?: "")
        }

        val (topSeedTeamSeriesScore, bottomSeedTeamSeriesScore) = games.fold(0 to 0) { partial, game ->
            var (top, bottom) = partial

            val homeTeamScore = game.homeTeamScore
            val awayTeamScore = game.awayTeamScore
            val homeTeamPenaltyScore = game.gameInfo?.let { (it as? FBGameInfoForSchedule)?.homeTeamPenaltyScore }
            val awayTeamPenaltyScore = game.gameInfo?.let { (it as? FBGameInfoForSchedule)?.awayTeamPenaltyScore }

            val isHomeWinner = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                // 축구경기에서 승부차기로 끝난경우
                homeTeamPenaltyScore > awayTeamPenaltyScore
            } else {
                homeTeamScore > awayTeamScore
            }
            val isAwayWinner = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                // 축구경기에서 승부차기로 끝난경우
                awayTeamPenaltyScore > homeTeamPenaltyScore
            } else {
                awayTeamScore > homeTeamScore
            }

            if (isUEFALeague) {
                if (!Constants.GameStatus.isBeforeGame(leagueId = leagueId, status = game.gameStatus)) {
                    if (game.isHomeTopSeed == true) {
                        top += homeTeamScore
                        bottom += awayTeamScore
                    } else {
                        top += awayTeamScore
                        bottom += homeTeamScore
                    }
                }
            } else {
                if (Constants.GameStatus.isGameFinished(leagueId = leagueId, status = game.gameStatus)) {
                    if (game.isHomeTopSeed == true) {
                        // 홈팀이 topSeed인경우
                        if (isHomeWinner) {
                            top += 1
                        } else if (isAwayWinner) {
                            bottom += 1
                        }
                    } else {
                        // 홈팀이 bottomSeed인경우
                        if (isHomeWinner) {
                            bottom += 1
                        } else if (isAwayWinner) {
                            top += 1
                        }
                    }
                }
            }
            top to bottom
        }

        CenterColumn(
            modifier = modifier.width(170.dp)
        ) {
            if (itemPosition.round > 1) {
                Row {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(top = verticalMetric(
                            leagueId = leagueId,
                            itemHeights = itemHeights,
                            round = itemPosition.round,
                            series = itemPosition.series,
                            maxRound = maxRound,
                            metric = VerticalMetric.TOP_PADDING,
                            direction = RoundDirection.LEFT
                        ))
                    ) {
                        TournamentHBar(75.dp)
                        TournamentVBar(verticalMetric(
                            leagueId = leagueId,
                            itemHeights = itemHeights,
                            round = itemPosition.round,
                            series = itemPosition.series,
                            maxRound = maxRound,
                            metric = VerticalMetric.TOP_HEIGHT,
                            direction = RoundDirection.LEFT
                        ))
                    }

                    Spacer(Modifier.weight(1f))
                }
            }

            CenterColumn(
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                if (isUEFALeague) {
                    Text(
                        text = "합산 스코어",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth().height(scoreTitleHeight)
                    )
                }

                CenterRow {
                    CenterColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .width(150.dp)
                            .onGloballyPositioned { layoutCoordinates ->
                                val size = layoutCoordinates.size

                                with(density) {
                                    itemHeight = size.height.toDp()
                                    onItemHeightChange(itemPosition, size.height.toDp())
                                }
                            }
                    ) {
                        CenterRow(
                            modifier = Modifier.padding(bottom = 2.dp)
                        ) {
                            CenterRow(
                                modifier = Modifier.width(130.dp)
                            ) {
                                Text(
                                    text = if (topSeedTeamId == null) "미정" else teamNameDic["short_${topSeedTeamId}"]
                                        ?: "",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(end = 4.dp)
                                )

                                URLImage(
                                    url = Util.teamLogoUrl(leagueId, topSeedTeamId),
                                    size = URLImageSize.SMALL
                                )
                            }

                            Text(
                                text = if (isSeriesStarted) topSeedTeamSeriesScore.toString() else "-",
                                color = if (isSeriesStarted) {
                                    if (topSeedTeamSeriesScore >= bottomSeedTeamSeriesScore) Moare else Color.Black
                                } else {
                                    Color.Black
                                }
                            )
                        }

                        if (isScoreOpened) {
                            CenterColumn(
                                modifier = Modifier.clickable { selectSeries?.let { it(games) } }
                            ) {
                                games.forEachIndexed { index, game ->
                                    val topSeedScore = if (game.isHomeTopSeed == true) game.homeTeamScore else game.awayTeamScore
                                    val lowerSeedScore = if (game.isHomeTopSeed == true) game.awayTeamScore else game.homeTeamScore
                                    val isBeforeGame = Constants.GameStatus.isBeforeGame(leagueId, game.gameStatus)

                                    // only football
                                    val homeTeamPenaltyScore = game.gameInfo?.let { (it as? FBGameInfoForSchedule)?.homeTeamPenaltyScore }
                                    val awayTeamPenaltyScore = game.gameInfo?.let { (it as? FBGameInfoForSchedule)?.awayTeamPenaltyScore }
                                    val topSeedPenaltyScore = if (game.isHomeTopSeed == true) homeTeamPenaltyScore else awayTeamPenaltyScore
                                    val lowerSeedPenaltyScore = if (game.isHomeTopSeed == true) awayTeamPenaltyScore else homeTeamPenaltyScore

                                    // 축구 패널티킥 경기에서 일반 스코어는 검정색
                                    val topSeedScoreColor = if (topSeedPenaltyScore != null && lowerSeedPenaltyScore != null) {
                                        Color.Black
                                    } else {
                                        if (topSeedScore >= lowerSeedScore) Moare else Color.Black
                                    }
                                    val lowerSeedScoreColor = if (topSeedPenaltyScore != null && lowerSeedPenaltyScore != null) {
                                        Color.Black
                                    } else {
                                        if (lowerSeedScore >= topSeedScore) Moare else Color.Black
                                    }

                                    CenterColumn {
                                        Text(
                                            text = "Game ${index + 1} - ${
                                                CalendarUtil.formatDate(
                                                    game.date
                                                ).split(" ").firstOrNull() ?: ""
                                            }",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Light,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )

                                        CenterRow {
                                            Text(
                                                text = if (isBeforeGame) "-" else topSeedScore.toString(),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.width(30.dp),
                                                color = if (isBeforeGame) {
                                                    Color.Black
                                                } else {
                                                    topSeedScoreColor
                                                }
                                            )

                                            if (topSeedPenaltyScore != null && lowerSeedPenaltyScore != null) {
                                                Text(
                                                    text = topSeedPenaltyScore.toString(),
                                                    fontSize = 12.sp,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.width(20.dp),
                                                    color = if (topSeedPenaltyScore >= lowerSeedPenaltyScore) Moare else Color.Black
                                                )
                                            }

                                            Text("-")

                                            if (topSeedPenaltyScore != null && lowerSeedPenaltyScore != null) {
                                                Text(
                                                    text = lowerSeedPenaltyScore.toString(),
                                                    fontSize = 12.sp,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.width(20.dp),
                                                    color = if (lowerSeedPenaltyScore >= topSeedPenaltyScore) Moare else Color.Black
                                                )
                                            }

                                            Text(
                                                text = if (isBeforeGame) "-" else lowerSeedScore.toString(),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.width(30.dp),
                                                color = if (isBeforeGame) {
                                                    Color.Black
                                                } else {
                                                    lowerSeedScoreColor
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        CenterRow(
                            modifier = Modifier
                                .alpha(0.7f)
                                .clickable { isScoreOpened = !isScoreOpened }
                        ) {
                            Text(
                                text = if (isScoreOpened) "경기결과 숨기기" else "경기결과 보기",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            Box(
                                Modifier
                                    .padding(start = 3.dp)
                                    .border(
                                        BorderStroke(1.dp, Color.Gray),
                                        RoundedCornerShape(10.dp)
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(id = if (isScoreOpened) R.drawable.ic_round_arrow_drop_up_24 else R.drawable.ic_round_arrow_drop_down_24),
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        }

                        CenterRow(
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            CenterRow(
                                modifier = Modifier.width(130.dp)
                            ) {
                                Text(
                                    text = if (bottomSeedTeamId == null) "미정" else teamNameDic["short_${bottomSeedTeamId}"]
                                        ?: "",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(end = 4.dp)
                                )

                                URLImage(
                                    url = Util.teamLogoUrl(leagueId, bottomSeedTeamId),
                                    size = URLImageSize.SMALL
                                )
                            }

                            Text(
                                text = if (isSeriesStarted) bottomSeedTeamSeriesScore.toString() else "-",
                                color = if (isSeriesStarted) {
                                    if (bottomSeedTeamSeriesScore >= topSeedTeamSeriesScore) Moare else Color.Black
                                } else {
                                    Color.Black
                                }
                            )
                        }
                    }

                    // bar
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.height(itemHeight).padding(vertical = 15.dp)
                    ) {
                        TournamentHBar()
                        TournamentVBar(modifier = Modifier.weight(1f))
                        TournamentHBar()
                    }
                }
            }

            if (itemPosition.round == 2 || itemPosition.round == 3) {
                Row {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(bottom = verticalMetric(
                            leagueId = leagueId,
                            itemHeights = itemHeights,
                            round = itemPosition.round,
                            series = itemPosition.series,
                            maxRound = maxRound,
                            metric = VerticalMetric.BOTTOM_PADDING,
                            direction = RoundDirection.LEFT
                        ))
                    ) {
                        TournamentVBar(verticalMetric(
                            leagueId = leagueId,
                            itemHeights = itemHeights,
                            round = itemPosition.round,
                            series = itemPosition.series,
                            maxRound = maxRound,
                            metric = VerticalMetric.BOTTOM_HEIGHT,
                            direction = RoundDirection.LEFT
                        ))
                        if (!shouldRemoveBar) {
                            TournamentHBar(75.dp)
                        }
                    }

                    Spacer(Modifier.weight(1f))
                }
            }
        }
    } else {
        // NOTE: MLB의 경우 첫번째 라운드에 시리즈가 없는 경우가 있어, 해당 경우 비워놔야해서 추가.
        Box(modifier = modifier)
    }
}

@Composable
fun <T> TournamentSeriesRightGameItem(
    leagueId: Int,
    teamNameDic: Map<String, String>,
    maxRound: Int,
    games: List<GameForSchedule<T>>?,
    itemPosition: RoundSeriesKey, // ui상에서 시리즈의 위치 ex) 1라운드의 첫번째 시리즈면 1_1
    shouldRemoveBar: Boolean = false, // NOTE: MLB의 경우 이전 라운드에 시리즈가 하나 없으면 하단에 HBar가 필요없는 경우가 있음. KBO는 그냥 필요없음.
    itemHeights: Map<RoundSeriesKey, Dp>,
    modifier: Modifier = Modifier,
    onItemHeightChange: (RoundSeriesKey, Dp) -> Unit,
    selectSeries: ((List<GameForSchedule<T>>) -> Unit)? = null
) {
    val density = LocalDensity.current
    var itemHeight by remember { mutableStateOf(0.dp) }
    var isScoreOpened by remember { mutableStateOf(false) }

    // ui
    if (games != null) {
        val game = games.firstOrNull()
        val topSeedTeamId = if (game?.isHomeTopSeed == true) game.homeTeamIdOrNull else game?.awayTeamIdOrNull
        val bottomSeedTeamId = if (game?.isHomeTopSeed == true) game.awayTeamIdOrNull else game?.homeTeamIdOrNull
        val isSeriesStarted = Constants.GameStatus.isGameFinished(leagueId = leagueId, status = game?.gameStatus ?: "")

        val (topSeedTeamSeriesScore, bottomSeedTeamSeriesScore) = games.fold(0 to 0) { partial, game ->
            var (top, bottom) = partial

            val homeTeamScore = game.homeTeamScore
            val awayTeamScore = game.awayTeamScore
            val homeTeamPenaltyScore = game.gameInfo?.let { (it as? FBGameInfoForSchedule)?.homeTeamPenaltyScore }
            val awayTeamPenaltyScore = game.gameInfo?.let { (it as? FBGameInfoForSchedule)?.awayTeamPenaltyScore }

            val isHomeWinner = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                // 축구경기에서 승부차기로 끝난경우
                homeTeamPenaltyScore > awayTeamPenaltyScore
            } else {
                homeTeamScore > awayTeamScore
            }
            val isAwayWinner = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                // 축구경기에서 승부차기로 끝난경우
                awayTeamPenaltyScore > homeTeamPenaltyScore
            } else {
                awayTeamScore > homeTeamScore
            }

            if (Constants.GameStatus.isGameFinished(leagueId = leagueId, status = game.gameStatus)) {
                if (game.isHomeTopSeed == true) {
                    // 홈팀이 topSeed인경우
                    if (isHomeWinner) {
                        top += 1
                    } else if (isAwayWinner) {
                        bottom += 1
                    }
                } else {
                    // 홈팀이 bottomSeed인경우
                    if (isHomeWinner) {
                        bottom += 1
                    } else if (isAwayWinner) {
                        top += 1
                    }
                }
            }

            top to bottom
        }

        CenterColumn(
            modifier = modifier.width(170.dp)
        ) {
            if (itemPosition.round == 5 || itemPosition.round == 6) {
                Row {
                    Spacer(Modifier.weight(1f))

                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.padding(top = verticalMetric(
                            leagueId = leagueId,
                            itemHeights = itemHeights,
                            round = itemPosition.round,
                            series = itemPosition.series,
                            maxRound = maxRound,
                            metric = VerticalMetric.TOP_PADDING,
                            direction = RoundDirection.RIGHT
                        ))
                    ) {
                        TournamentHBar(75.dp)
                        TournamentVBar(verticalMetric(
                            leagueId = leagueId,
                            itemHeights = itemHeights,
                            round = itemPosition.round,
                            series = itemPosition.series,
                            maxRound = maxRound,
                            metric = VerticalMetric.TOP_HEIGHT,
                            direction = RoundDirection.RIGHT
                        ))
                    }
                }
            }

            CenterRow {
                // bar
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.height(itemHeight).padding(vertical = 15.dp)
                ) {
                    TournamentHBar()
                    TournamentVBar(modifier = Modifier.weight(1f))
                    TournamentHBar()
                }

                CenterColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .width(150.dp)
                        .onGloballyPositioned { layoutCoordinates ->
                            val size = layoutCoordinates.size

                            with(density) {
                                itemHeight = size.height.toDp()
                                onItemHeightChange(itemPosition, size.height.toDp())
                            }
                        }
                ) {
                    CenterRow(
                        modifier = Modifier.padding(bottom = 2.dp)
                    ) {
                        Text(
                            text = if (isSeriesStarted) topSeedTeamSeriesScore.toString() else "-",
                            color = if (isSeriesStarted) {
                                if (topSeedTeamSeriesScore >= bottomSeedTeamSeriesScore) Moare else Color.Black
                            } else {
                                Color.Black
                            }
                        )

                        CenterRow(
                            modifier = Modifier.width(130.dp)
                        ) {
                            Text(
                                text = if (topSeedTeamId == null) "미정" else teamNameDic["short_${topSeedTeamId}"] ?: "",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(end = 4.dp)
                            )

                            URLImage(
                                url = Util.teamLogoUrl(leagueId, topSeedTeamId),
                                size = URLImageSize.SMALL
                            )
                        }
                    }

                    if (isScoreOpened) {
                        CenterColumn(
                            modifier = Modifier.clickable { selectSeries?.let { it(games) } }
                        ) {
                            games.forEachIndexed { index, game ->
                                val topSeedScore = if (game.isHomeTopSeed == true) game.homeTeamScore else game.awayTeamScore
                                val lowerSeedScore = if (game.isHomeTopSeed == true) game.awayTeamScore else game.homeTeamScore
                                val isBeforeGame = Constants.GameStatus.isBeforeGame(leagueId, game.gameStatus)

                                // only football
                                val homeTeamPenaltyScore = game.gameInfo?.let { (it as? FBGameInfoForSchedule)?.homeTeamPenaltyScore }
                                val awayTeamPenaltyScore = game.gameInfo?.let { (it as? FBGameInfoForSchedule)?.awayTeamPenaltyScore }
                                val topSeedPenaltyScore = if (game.isHomeTopSeed == true) homeTeamPenaltyScore else awayTeamPenaltyScore
                                val lowerSeedPenaltyScore = if (game.isHomeTopSeed == true) awayTeamPenaltyScore else homeTeamPenaltyScore

                                // 축구 패널티킥 경기에서 일반 스코어는 검정색
                                val topSeedScoreColor = if (topSeedPenaltyScore != null && lowerSeedPenaltyScore != null) {
                                    Color.Black
                                } else {
                                    if (topSeedScore >= lowerSeedScore) Moare else Color.Black
                                }
                                val lowerSeedScoreColor = if (topSeedPenaltyScore != null && lowerSeedPenaltyScore != null) {
                                    Color.Black
                                } else {
                                    if (lowerSeedScore >= topSeedScore) Moare else Color.Black
                                }

                                CenterColumn {
                                    Text(
                                        text = "Game ${index + 1} - ${CalendarUtil.formatDate(game.date).split(" ").firstOrNull() ?: ""}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Light,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )

                                    CenterRow {
                                        Text(
                                            text = if (isBeforeGame) "-" else topSeedScore.toString(),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.width(30.dp),
                                            color = if (isBeforeGame) {
                                                Color.Black
                                            } else {
                                                topSeedScoreColor
                                            }
                                        )

                                        if (topSeedPenaltyScore != null && lowerSeedPenaltyScore != null) {
                                            Text(
                                                text = topSeedPenaltyScore.toString(),
                                                fontSize = 12.sp,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.width(20.dp),
                                                color = if (topSeedPenaltyScore >= lowerSeedPenaltyScore) Moare else Color.Black
                                            )
                                        }

                                        Text("-")

                                        if (topSeedPenaltyScore != null && lowerSeedPenaltyScore != null) {
                                            Text(
                                                text = lowerSeedPenaltyScore.toString(),
                                                fontSize = 12.sp,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.width(20.dp),
                                                color = if (lowerSeedPenaltyScore >= topSeedPenaltyScore) Moare else Color.Black
                                            )
                                        }

                                        Text(
                                            text = if (isBeforeGame) "-" else lowerSeedScore.toString(),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.width(30.dp),
                                            color = if (isBeforeGame) {
                                                Color.Black
                                            } else {
                                                lowerSeedScoreColor
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    CenterRow(
                        modifier = Modifier
                            .alpha(0.7f)
                            .clickable { isScoreOpened = !isScoreOpened }
                    ) {
                        Text(
                            text = if (isScoreOpened) "경기결과 숨기기" else "경기결과 보기",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        Box(
                            Modifier
                                .padding(start = 3.dp)
                                .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(10.dp))
                        ) {
                            Icon(
                                painter = painterResource(id = if (isScoreOpened) R.drawable.ic_round_arrow_drop_up_24 else R.drawable.ic_round_arrow_drop_down_24),
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    }

                    CenterRow(
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = if (isSeriesStarted) bottomSeedTeamSeriesScore.toString() else "-",
                            color = if (isSeriesStarted) {
                                if (bottomSeedTeamSeriesScore >= topSeedTeamSeriesScore) Moare else Color.Black
                            } else {
                                Color.Black
                            }
                        )

                        CenterRow(
                            modifier = Modifier.width(130.dp)
                        ) {
                            Text(
                                text = if (bottomSeedTeamId == null) "미정" else teamNameDic["short_${bottomSeedTeamId}"] ?: "",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(end = 4.dp)
                            )

                            URLImage(
                                url = Util.teamLogoUrl(leagueId, bottomSeedTeamId),
                                size = URLImageSize.SMALL
                            )
                        }
                    }
                }
            }

            if (itemPosition.round == 5 || itemPosition.round == 6) {
                Row {
                    Spacer(Modifier.weight(1f))

                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.padding(bottom = verticalMetric(
                            leagueId = leagueId,
                            itemHeights = itemHeights,
                            round = itemPosition.round,
                            series = itemPosition.series,
                            maxRound = maxRound,
                            metric = VerticalMetric.BOTTOM_PADDING,
                            direction = RoundDirection.RIGHT
                        ))
                    ) {
                        TournamentVBar(verticalMetric(
                            leagueId = leagueId,
                            itemHeights = itemHeights,
                            round = itemPosition.round,
                            series = itemPosition.series,
                            maxRound = maxRound,
                            metric = VerticalMetric.BOTTOM_HEIGHT,
                            direction = RoundDirection.RIGHT
                        ))
                        if (!shouldRemoveBar) {
                            TournamentHBar(75.dp)
                        }
                    }
                }
            }
        }
    } else {
        // NOTE: MLB의 경우 첫번째 라운드에 시리즈가 없는 경우가 있어, 해당 경우 비워놔야해서 추가.
        Box(modifier = modifier)
    }
}

@Composable
fun <T> TournamentSeriesFinalGameItem(
    leagueId: Int,
    teamNameDic: Map<String, String>,
    games: List<GameForSchedule<T>>,
    itemHeights: Map<RoundSeriesKey, Dp>,
    selectSeries: ((List<GameForSchedule<T>>) -> Unit)? = null
) {
    val game = games.firstOrNull()
    val topSeedTeamId = if (game?.isHomeTopSeed == true) game.homeTeamIdOrNull else game?.awayTeamIdOrNull
    val bottomSeedTeamId = if (game?.isHomeTopSeed == true) game.awayTeamIdOrNull else game?.homeTeamIdOrNull
    val isSeriesStarted = Constants.GameStatus.isGameFinished(leagueId = leagueId, status = game?.gameStatus ?: "")

    var isScoreOpened by remember { mutableStateOf(false) }
    var itemTopPadding by remember { mutableStateOf(0.dp) } // 아이템 Y 위치

    val (topSeedTeamSeriesScore, bottomSeedTeamSeriesScore) = games.fold(0 to 0) { partial, game ->
        var (top, bottom) = partial

        val homeTeamScore = game.homeTeamScore
        val awayTeamScore = game.awayTeamScore
        val homeTeamPenaltyScore = game.gameInfo?.let { (it as? FBGameInfoForSchedule)?.homeTeamPenaltyScore }
        val awayTeamPenaltyScore = game.gameInfo?.let { (it as? FBGameInfoForSchedule)?.awayTeamPenaltyScore }

        val isHomeWinner = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
            // 축구경기에서 승부차기로 끝난경우
            homeTeamPenaltyScore > awayTeamPenaltyScore
        } else {
            homeTeamScore > awayTeamScore
        }
        val isAwayWinner = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
            // 축구경기에서 승부차기로 끝난경우
            awayTeamPenaltyScore > homeTeamPenaltyScore
        } else {
            awayTeamScore > homeTeamScore
        }

        if (Constants.GameStatus.isGameFinished(leagueId = leagueId, status = game.gameStatus)) {
            if (game.isHomeTopSeed == true) {
                // 홈팀이 topSeed인경우
                if (isHomeWinner) {
                    top += 1
                } else if (isAwayWinner) {
                    bottom += 1
                }
            } else {
                // 홈팀이 bottomSeed인경우
                if (isHomeWinner) {
                    bottom += 1
                } else if (isAwayWinner) {
                    top += 1
                }
            }
        }

        top to bottom
    }

    // function
    fun h(r: Int, s: Int): Dp {
        return itemHeights[RoundSeriesKey(r, s)] ?: 0.dp
    }

    // ui
    CenterColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .padding(top = 200.dp)
            .padding(horizontal = 8.dp)
            .border(BorderStroke(1.dp, Moare), RoundedCornerShape(10.dp))
            .padding(vertical = 6.dp, horizontal = 8.dp)
    ) {
        CenterRow(
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            CenterColumn(
                modifier = Modifier.width(100.dp)
            ) {
                URLImage(
                    url = Util.teamLogoUrl(leagueId, topSeedTeamId),
                    size = URLImageSize.SMALL
                )

                Text(
                    text = if (topSeedTeamId == null) "미정" else teamNameDic["short_${topSeedTeamId}"] ?: "",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = if (isSeriesStarted) topSeedTeamSeriesScore.toString() else "-",
                color = if (isSeriesStarted) {
                    if (topSeedTeamSeriesScore >= bottomSeedTeamSeriesScore) Moare else Color.Black
                } else {
                    Color.Black
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.width(20.dp)
            )

            Text("-")

            Text(
                text = if (isSeriesStarted) bottomSeedTeamSeriesScore.toString() else "-",
                color = if (isSeriesStarted) {
                    if (bottomSeedTeamSeriesScore >= topSeedTeamSeriesScore) Moare else Color.Black
                } else {
                    Color.Black
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.width(20.dp)
            )

            CenterColumn(
                modifier = Modifier.width(100.dp)
            ) {
                URLImage(
                    url = Util.teamLogoUrl(leagueId, bottomSeedTeamId),
                    size = URLImageSize.SMALL
                )

                Text(
                    text = if (bottomSeedTeamId == null) "미정" else teamNameDic["short_${bottomSeedTeamId}"] ?: "",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (isScoreOpened) {
            CenterColumn(
                modifier = Modifier.clickable { selectSeries?.let { it(games) } }
            ) {
                games.forEachIndexed { index, game ->
                    // NOTE: 축구에서 final이 series인 경우는 아직 없어서 관련 코드가 없음
                    val topSeedScore = if (game.homeTeamId == topSeedTeamId) game.homeTeamScore else game.awayTeamScore
                    val bottomSeedScore = if (game.homeTeamId == bottomSeedTeamId) game.homeTeamScore else game.awayTeamScore
                    val isBeforeGame = Constants.GameStatus.isBeforeGame(leagueId, game.gameStatus)

                    CenterColumn {
                        Text(
                            text = "Game ${index + 1} - ${CalendarUtil.formatDate(game.date).split(" ").firstOrNull() ?: ""}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        CenterRow {
                            Text(
                                text = if (isBeforeGame) "-" else topSeedScore.toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(30.dp),
                                color = if (isBeforeGame) {
                                    Color.Black
                                } else {
                                    if (topSeedScore >= bottomSeedScore) Moare else Color.Black
                                }
                            )

                            Text("-")

                            Text(
                                text = if (isBeforeGame) "-" else bottomSeedScore.toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(30.dp),
                                color = if (isBeforeGame) {
                                    Color.Black
                                } else {
                                    if (bottomSeedScore >= topSeedScore) Moare else Color.Black
                                }
                            )
                        }
                    }
                }
            }
        }

        CenterRow(
            modifier = Modifier
                .alpha(0.7f)
                .clickable { isScoreOpened = !isScoreOpened }
        ) {
            Text(
                text = if (isScoreOpened) "경기결과 숨기기" else "경기결과 보기",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Box(
                Modifier
                    .padding(start = 3.dp)
                    .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(10.dp))
            ) {
                Icon(
                    painter = painterResource(id = if (isScoreOpened) R.drawable.ic_round_arrow_drop_up_24 else R.drawable.ic_round_arrow_drop_down_24),
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun TournamentHBar(width: Dp = 20.dp) {
    Box(
        Modifier
            .size(width = width, height = 1.dp)
            .background(Color.Gray)
            .alpha(0.7f)
    )
}

@Composable
fun TournamentVBar(height: Dp? = null, modifier: Modifier = Modifier) {
    Box(
        modifier
            .width(1.dp)
            .nullableMaxHeight(height)
            .background(Color.Gray)
            .alpha(0.7f)
    )
}

enum class VerticalMetric {
    TOP_PADDING, // ⏋or ⎾ 위 패딩
    BOTTOM_PADDING, // ⏌ or ⎿ 아래 패딩
    TOP_HEIGHT, // ⏋or ⎾ 에서 | 부분 높이
    BOTTOM_HEIGHT // ⏌ or ⎿ 에서 | 부분 높이
}

enum class RoundDirection {
    LEFT, RIGHT
}

fun verticalMetric(
    leagueId: Int,
    itemHeights: Map<RoundSeriesKey, Dp>,
    round: Int,
    series: Int,
    maxRound: Int,
    metric: VerticalMetric,
    direction: RoundDirection
): Dp {
    val isUEFALeague = Constants.Ids.FOOTBALL_UEFA_LEAGUES.contains(leagueId)

    fun h(r: Int, s: Int): Dp {
        return itemHeights[RoundSeriesKey(round = r, series = s)] ?: 0.dp
    }

    require(series >= 1) { "series must be >= 1" }
    require(maxRound >= 2) { "maxRound must be >= 2" }

    when (direction) {
        RoundDirection.LEFT -> {
            require(round >= 2) { "round must be >= 2" }
            require(round <= maxRound) { "round must be <= maxRound" }
        }
        RoundDirection.RIGHT -> {
            require(round >= 1) { "round must be < maxRound" }
            require(round < maxRound) { "round must be < maxRound" }
        }
    }

    val depth: Int
    val halfRound: Int
    val roundsToSum: List<Int>

    when (direction) {
        RoundDirection.LEFT -> {
            depth = round
            halfRound = round - 1
            roundsToSum = if (round > 2) {
                (1..(round - 2)).toList()
            } else {
                emptyList()
            }
        }
        RoundDirection.RIGHT -> {
            depth = maxRound - round + 1
            halfRound = round + 1
            roundsToSum = if (depth > 2) {
                (maxRound downTo (round + 2)).toList()
            } else {
                emptyList()
            }
        }
    }

    val quarterIndex = when (metric) {
        VerticalMetric.TOP_PADDING -> 0
        VerticalMetric.TOP_HEIGHT -> 1
        VerticalMetric.BOTTOM_HEIGHT -> 2
        VerticalMetric.BOTTOM_PADDING -> 3
    }

    var result = 0.dp

    for ((index, a) in roundsToSum.withIndex()) {
        val count = 1 shl (depth - index - 3)
        val blockSize = 1 shl (depth - index - 1)
        val blockStart = 1 + (series - 1) * blockSize
        val startB = blockStart + quarterIndex * count

        for (offset in 0 until count) {
            result += h(a, startB + offset)
        }
    }

    val halfB = when (metric) {
        VerticalMetric.TOP_PADDING,
        VerticalMetric.TOP_HEIGHT -> 2 * series - 1

        VerticalMetric.BOTTOM_PADDING,
        VerticalMetric.BOTTOM_HEIGHT -> 2 * series
    }

    result += h(halfRound, halfB) / 2

    if (isUEFALeague &&
        direction == RoundDirection.LEFT &&
        metric == VerticalMetric.TOP_PADDING) {
        result += 16.dp
    }

    return result
}






















