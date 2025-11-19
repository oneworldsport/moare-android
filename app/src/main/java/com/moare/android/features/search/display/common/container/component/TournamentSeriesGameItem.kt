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
import com.moare.android.ui.components.URLImage
import com.moare.android.ui.components.URLImageSize
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow
import com.moare.android.ui.util.nullableMaxHeight

@Composable
fun <T> TournamentSeriesLeftGameItem(
    leagueId: Int,
    teamNameDic: Map<String, String>,
    games: List<GameForSchedule<T>>?,
    seedIdPair: Pair<Int?, Int?>,
    itemPosition: RoundSeriesKey, // ui상에서 시리즈의 위치 ex) 1라운드의 첫번째 시리즈면 1_1
    shouldRemoveBar: Boolean = false, // NOTE: MLB의 경우 이전 라운드에 시리즈가 하나 없으면 하단에 HBar가 필요없는 경우가 있음. KBO는 그냥 필요없음.
    itemHeights: Map<RoundSeriesKey, Dp>,
    modifier: Modifier = Modifier,
    onItemHeightChange: (RoundSeriesKey, Dp) -> Unit,
    selectSeries: ((List<GameForSchedule<T>>) -> Unit)? = null
) {
    val density = LocalDensity.current
    val topSeedTeamId = seedIdPair.first
    val bottomSeedTeamId = seedIdPair.second
    val isSeriesStarted = topSeedTeamId != null && bottomSeedTeamId != null

    var itemHeight by remember { mutableStateOf(0.dp) }
    var isScoreOpened by remember { mutableStateOf(false) }

    // function
    fun h(r: Int, s: Int): Dp {
        return itemHeights[RoundSeriesKey(r, s)] ?: 0.dp
    }

    fun topPadding(): Dp {
        return when (itemPosition.round to itemPosition.series) {
            2 to 1 -> h(1, 1) / 2
            2 to 2 -> h(1, 3) / 2
            3 to 1 -> h(1, 1) + h(2, 1) / 2
            4 to 1 -> h(1, 1) + h(2, 1) + h(3, 1) / 2
            else -> 0.dp
        }
    }

    fun topHeight(): Dp {
        return when (itemPosition.round to itemPosition.series) {
            2 to 1 -> h(1, 1) / 2
            2 to 2 -> h(1, 3) / 2
            3 to 1 -> h(1, 2) + h(2, 1) / 2
            4 to 1 -> h(3, 1) / 2 // NOTE: 일단은 KBO의 경우만 고려
            else -> 0.dp
        }
    }

    fun bottomPadding(): Dp {
        return when (itemPosition.round to itemPosition.series) {
            2 to 1 -> h(1, 2) / 2
            2 to 2 -> h(1, 4) / 2
            3 to 1 -> h(1, 4) + h(2, 2) / 2
            else -> 0.dp
        }
    }

    fun bottomHeight(): Dp {
        return when (itemPosition.round to itemPosition.series) {
            2 to 1 -> h(1, 2) / 2
            2 to 2 -> h(1, 4) / 2
            3 to 1 -> h(1, 3) + h(2, 2) / 2
            else -> 0.dp
        }
    }

    // ui
    if (games != null) {
        val (topSeedTeamSeriesScore, bottomSeedTeamSeriesScore) = games.fold(0 to 0) { partial, game ->
            var (top, bottom) = partial

            if (game.homeTeamId == topSeedTeamId && game.awayTeamId == bottomSeedTeamId) {
                // 홈팀이 topSeed인경우
                if (game.homeTeamScore > game.awayTeamScore) {
                    top += 1
                } else if (game.awayTeamScore > game.homeTeamScore) {
                    bottom += 1
                }
            } else if (game.homeTeamId == bottomSeedTeamId && game.awayTeamId == topSeedTeamId) {
                // 홈팀이 bottomSeed인경우
                if (game.awayTeamScore > game.homeTeamScore) {
                    top += 1
                } else if (game.homeTeamScore > game.awayTeamScore) {
                    bottom += 1
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
                        modifier = Modifier.padding(top = topPadding())
                    ) {
                        TournamentHBar(75.dp)
                        TournamentVBar(topHeight())
                    }

                    Spacer(Modifier.weight(1f))
                }
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
                                text = if (topSeedTeamId == null) "미정" else teamNameDic["short_${topSeedTeamId}"] ?: "",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
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

                    CenterRow(
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        CenterRow(
                            modifier = Modifier.width(130.dp)
                        ) {
                            Text(
                                text = if (bottomSeedTeamId == null) "미정" else teamNameDic["short_${bottomSeedTeamId}"] ?: "",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
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

            if (itemPosition.round == 2 || itemPosition.round == 3) {
                Row {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(bottom = bottomPadding())
                    ) {
                        TournamentVBar(bottomHeight())
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
    games: List<GameForSchedule<T>>?,
    seedIdPair: Pair<Int?, Int?>,
    itemPosition: RoundSeriesKey, // ui상에서 시리즈의 위치 ex) 1라운드의 첫번째 시리즈면 1_1
    shouldRemoveBar: Boolean = false, // NOTE: MLB의 경우 이전 라운드에 시리즈가 하나 없으면 하단에 HBar가 필요없는 경우가 있음. KBO는 그냥 필요없음.
    itemHeights: Map<RoundSeriesKey, Dp>,
    modifier: Modifier = Modifier,
    onItemHeightChange: (RoundSeriesKey, Dp) -> Unit,
    selectSeries: ((List<GameForSchedule<T>>) -> Unit)? = null
) {
    val density = LocalDensity.current
    val topSeedTeamId = seedIdPair.first
    val bottomSeedTeamId = seedIdPair.second
    val isSeriesStarted = topSeedTeamId != null && bottomSeedTeamId != null

    var itemHeight by remember { mutableStateOf(0.dp) }
    var isScoreOpened by remember { mutableStateOf(false) }

    // function
    fun h(r: Int, s: Int): Dp {
        return itemHeights[RoundSeriesKey(r, s)] ?: 0.dp
    }

    fun topPadding(): Dp {
        return when (itemPosition.round to itemPosition.series) {
            6 to 1 -> h(7, 1) / 2
            6 to 2 -> h(7, 3) / 2
            5 to 1 -> h(7, 1) + h(6, 1) / 2
            else -> 0.dp
        }
    }

    fun topHeight(): Dp {
        return when (itemPosition.round to itemPosition.series) {
            6 to 1 -> h(7, 1) / 2
            6 to 2 -> h(7, 3) / 2
            5 to 1 -> h(7, 2) + h(6, 1) / 2
            else -> 0.dp
        }
    }

    fun bottomPadding(): Dp {
        return when (itemPosition.round to itemPosition.series) {
            6 to 1 -> h(7, 2) / 2
            6 to 2 -> h(7, 4) / 2
            5 to 1 -> h(7, 4) + h(6, 2) / 2
            else -> 0.dp
        }
    }

    fun bottomHeight(): Dp {
        return when (itemPosition.round to itemPosition.series) {
            6 to 1 -> h(7, 2) / 2
            6 to 2 -> h(7, 4) / 2
            5 to 1 -> h(7, 3) + h(6, 2) / 2
            else -> 0.dp
        }
    }

    // ui
    if (games != null) {
        val (topSeedTeamSeriesScore, bottomSeedTeamSeriesScore) = games.fold(0 to 0) { partial, game ->
            var (top, bottom) = partial

            if (game.homeTeamId == topSeedTeamId && game.awayTeamId == bottomSeedTeamId) {
                // 홈팀이 topSeed인경우
                if (game.homeTeamScore > game.awayTeamScore) {
                    top += 1
                } else if (game.awayTeamScore > game.homeTeamScore) {
                    bottom += 1
                }
            } else if (game.homeTeamId == bottomSeedTeamId && game.awayTeamId == topSeedTeamId) {
                // 홈팀이 bottomSeed인경우
                if (game.awayTeamScore > game.homeTeamScore) {
                    top += 1
                } else if (game.homeTeamScore > game.awayTeamScore) {
                    bottom += 1
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
                        modifier = Modifier.padding(top = topPadding())
                    ) {
                        TournamentHBar(75.dp)
                        TournamentVBar(topHeight())
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
                        CenterRow(
                            modifier = Modifier.width(130.dp)
                        ) {
                            Text(
                                text = if (topSeedTeamId == null) "미정" else teamNameDic["short_${topSeedTeamId}"] ?: "",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
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

                    CenterRow(
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        CenterRow(
                            modifier = Modifier.width(130.dp)
                        ) {
                            Text(
                                text = if (bottomSeedTeamId == null) "미정" else teamNameDic["short_${bottomSeedTeamId}"] ?: "",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
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
            }

            if (itemPosition.round == 5 || itemPosition.round == 6) {
                Row {
                    Spacer(Modifier.weight(1f))

                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.padding(bottom = bottomPadding())
                    ) {
                        TournamentVBar(bottomHeight())
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
    seedIdPair: Pair<Int?, Int?>,
    itemHeights: Map<RoundSeriesKey, Dp>,
    selectSeries: ((List<GameForSchedule<T>>) -> Unit)? = null
) {
    val topSeedTeamId = seedIdPair.first
    val bottomSeedTeamId = seedIdPair.second
    val isSeriesStarted = topSeedTeamId != null && bottomSeedTeamId != null

    var isScoreOpened by remember { mutableStateOf(false) }
    var itemTopPadding by remember { mutableStateOf(0.dp) } // 아이템 Y 위치

    val (topSeedTeamSeriesScore, bottomSeedTeamSeriesScore) = games.fold(0 to 0) { partial, game ->
        var (top, bottom) = partial

        if (game.homeTeamId == topSeedTeamId && game.awayTeamId == bottomSeedTeamId) {
            // 홈팀이 topSeed인경우
            if (game.homeTeamScore > game.awayTeamScore) {
                top += 1
            } else if (game.awayTeamScore > game.homeTeamScore) {
                bottom += 1
            }
        } else if (game.homeTeamId == bottomSeedTeamId && game.awayTeamId == topSeedTeamId) {
            // 홈팀이 bottomSeed인경우
            if (game.awayTeamScore > game.homeTeamScore) {
                top += 1
            } else if (game.homeTeamScore > game.awayTeamScore) {
                bottom += 1
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
























