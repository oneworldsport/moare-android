package com.moare.android.features.search.display.common.container.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.R
import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.TimeFormatType
import com.moare.android.core.util.Util
import com.moare.android.features.search.display.common.container.component.TournamentHBar
import com.moare.android.features.search.display.common.container.component.TournamentVBar
import com.moare.android.features.search.models.models.common.GameForSchedule
import com.moare.android.features.search.models.models.football.FBGameInfoForSchedule
import com.moare.android.ui.common.components.CapsuleButton
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow

@Composable
fun <T> TournamentBracketSingleLeftGameItem(
    leagueId: Int,
    teamNameDic: Map<String, String>,
    game: GameForSchedule<T>?,
    seedIdPair: Pair<Int?, Int?>,
    itemPosition: RoundSeriesKey, // ui상에서 시리즈의 위치 ex) 1라운드의 첫번째 시리즈면 1_1
    itemHeights: Map<RoundSeriesKey, Dp>,
    modifier: Modifier = Modifier,
    onItemHeightChange: (RoundSeriesKey, Dp) -> Unit,
    selectGame: ((GameForSchedule<T>) -> Unit)? = null
) {
    val density = LocalDensity.current

    var itemHeight by remember { mutableStateOf(0.dp) }

    val topSeedTeamId = seedIdPair.first
    val bottomSeedTeamId = seedIdPair.second
    val gameStatus = game?.gameStatus ?: Constants.GameStatus.Football.NOT_STARTED
    val homeTeamScore = game?.homeTeamScore ?: 0
    val awayTeamScore = game?.awayTeamScore ?: 0
    val elapsed = game?.gameInfo?.let { (it as? FBGameInfoForSchedule)?.elapsed }
    val homeTeamPenaltyScore = game?.gameInfo?.let { (it as? FBGameInfoForSchedule)?.homeTeamPenaltyScore }
    val awayTeamPenaltyScore = game?.gameInfo?.let { (it as? FBGameInfoForSchedule)?.awayTeamPenaltyScore }
    val shouldShowScore = !Constants.GameStatus.isBeforeGame(leagueId, gameStatus)

    val isHomeWinner = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
        homeTeamPenaltyScore > awayTeamPenaltyScore
    } else {
        homeTeamScore > awayTeamScore
    }

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

    CenterColumn(
        modifier = modifier.width(170.dp)
    ) {
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
                    .clickable { game?.let { selectGame?.let { it(game) } } }
            ) {
                CenterRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(bottom = 2.dp)
                ) {
                    CenterRow(
                        modifier = Modifier.width(110.dp)
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

                    // 축구 패널티킥 경기는 일반 스코어 검정색
                    val scoreColor = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                        Color.Black
                    } else {
                        if (homeTeamScore >= awayTeamScore) Moare else Color.Black
                    }

                    Text(
                        text = if (shouldShowScore) homeTeamScore.toString() else "-",
                        color = if (shouldShowScore) scoreColor else Color.Black,
                        textAlign = TextAlign.Center
                    )

                    if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                        Text(
                            text = homeTeamPenaltyScore.toString(),
                            fontSize = 12.sp,
                            color = if (homeTeamPenaltyScore >= awayTeamPenaltyScore) Moare else Color.Black
                        )
                    }
                }

                CenterColumn {
                    // game status
                    CapsuleButton(
                        text = Constants.GameStatus.fbGameStatusText(gameStatus, elapsed),
                        color = Constants.GameStatus.gameStatusColor(leagueId, gameStatus)
                    ) { }

                    // game date
                    game?.date?.let {
                        Text(
                            text = CalendarUtil.formatDate(game.date).split(" ").firstOrNull() ?: "",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Text(
                            text = CalendarUtil.formatDate(game.date, formatType = TimeFormatType.AMPM),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }

                CenterRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    CenterRow(
                        modifier = Modifier.width(110.dp)
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

                    // 축구 패널티킥 경기는 일반 스코어 검정색
                    val scoreColor = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                        Color.Black
                    } else {
                        if (awayTeamScore >= homeTeamScore) Moare else Color.Black
                    }

                    Text(
                        text = if (shouldShowScore) awayTeamScore.toString() else "-",
                        color = if (shouldShowScore) scoreColor else Color.Black,
                        textAlign = TextAlign.Center
                    )

                    if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                        Text(
                            text = awayTeamPenaltyScore.toString(),
                            fontSize = 12.sp,
                            color = if (awayTeamPenaltyScore >= homeTeamPenaltyScore) Moare else Color.Black
                        )
                    }
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

        Row {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(bottom = bottomPadding())
            ) {
                TournamentVBar(bottomHeight())
                TournamentHBar(75.dp)
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun <T> TournamentBracketSingleRightGameItem(
    leagueId: Int,
    teamNameDic: Map<String, String>,
    game: GameForSchedule<T>?,
    seedIdPair: Pair<Int?, Int?>,
    itemPosition: RoundSeriesKey, // ui상에서 시리즈의 위치 ex) 1라운드의 첫번째 시리즈면 1_1
    itemHeights: Map<RoundSeriesKey, Dp>,
    modifier: Modifier = Modifier,
    onItemHeightChange: (RoundSeriesKey, Dp) -> Unit,
    selectGame: ((GameForSchedule<T>) -> Unit)? = null
) {
    val density = LocalDensity.current

    var itemHeight by remember { mutableStateOf(0.dp) }

    val topSeedTeamId = seedIdPair.first
    val bottomSeedTeamId = seedIdPair.second
    val gameStatus = game?.gameStatus ?: Constants.GameStatus.Football.NOT_STARTED
    val homeTeamScore = game?.homeTeamScore ?: 0
    val awayTeamScore = game?.awayTeamScore ?: 0
    val elapsed = game?.gameInfo?.let { (it as? FBGameInfoForSchedule)?.elapsed }
    val homeTeamPenaltyScore = game?.gameInfo?.let { (it as? FBGameInfoForSchedule)?.homeTeamPenaltyScore }
    val awayTeamPenaltyScore = game?.gameInfo?.let { (it as? FBGameInfoForSchedule)?.awayTeamPenaltyScore }
    val shouldShowScore = !Constants.GameStatus.isBeforeGame(leagueId, gameStatus)

    val isHomeWinner = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
        homeTeamPenaltyScore > awayTeamPenaltyScore
    } else {
        homeTeamScore > awayTeamScore
    }

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

    CenterColumn(
        modifier = modifier.width(170.dp)
    ) {
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
                    .clickable { game?.let { selectGame?.let { it(game) } } }
            ) {
                CenterRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(bottom = 2.dp)
                ) {
                    // 축구 패널티킥 경기는 일반 스코어 검정색
                    val scoreColor = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                        Color.Black
                    } else {
                        if (homeTeamScore >= awayTeamScore) Moare else Color.Black
                    }

                    Text(
                        text = if (shouldShowScore) homeTeamScore.toString() else "-",
                        color = if (shouldShowScore) scoreColor else Color.Black,
                        textAlign = TextAlign.Center
                    )

                    if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                        Text(
                            text = homeTeamPenaltyScore.toString(),
                            fontSize = 12.sp,
                            color = if (homeTeamPenaltyScore >= awayTeamPenaltyScore) Moare else Color.Black
                        )
                    }

                    CenterRow(
                        modifier = Modifier.width(110.dp)
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

                CenterColumn {
                    // game status
                    CapsuleButton(
                        text = Constants.GameStatus.fbGameStatusText(gameStatus, elapsed),
                        color = Constants.GameStatus.gameStatusColor(leagueId, gameStatus)
                    ) { }

                    // game date
                    game?.date?.let {
                        Text(
                            text = CalendarUtil.formatDate(game.date).split(" ").firstOrNull() ?: "",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Text(
                            text = CalendarUtil.formatDate(game.date, formatType = TimeFormatType.AMPM),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }

                CenterRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    // 축구 패널티킥 경기는 일반 스코어 검정색
                    val scoreColor = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                        Color.Black
                    } else {
                        if (awayTeamScore >= homeTeamScore) Moare else Color.Black
                    }

                    Text(
                        text = if (shouldShowScore) awayTeamScore.toString() else "-",
                        color = if (shouldShowScore) scoreColor else Color.Black,
                        textAlign = TextAlign.Center
                    )

                    if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                        Text(
                            text = awayTeamPenaltyScore.toString(),
                            fontSize = 12.sp,
                            color = if (awayTeamPenaltyScore >= homeTeamPenaltyScore) Moare else Color.Black
                        )
                    }

                    CenterRow(
                        modifier = Modifier.width(110.dp)
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

        Row {
            Spacer(Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(bottom = bottomPadding())
            ) {
                TournamentVBar(bottomHeight())
                TournamentHBar(75.dp)
            }
        }
    }
}

@Composable
fun <T> TournamentBracketSingleFinalGameItem(
    leagueId: Int,
    teamNameDic: Map<String, String>,
    game: GameForSchedule<T>?,
    seedIdPair: Pair<Int?, Int?>,
    itemHeights: Map<RoundSeriesKey, Dp>,
    selectGame: ((GameForSchedule<T>) -> Unit)? = null
) {
    var itemTopPadding by remember { mutableStateOf(0.dp) } // 아이템 Y 위치

    val topSeedTeamId = seedIdPair.first
    val bottomSeedTeamId = seedIdPair.second
    val gameStatus = game?.gameStatus ?: Constants.GameStatus.Football.NOT_STARTED
    val homeTeamScore = game?.homeTeamScore ?: 0
    val awayTeamScore = game?.awayTeamScore ?: 0
    val elapsed = game?.gameInfo?.let { (it as? FBGameInfoForSchedule)?.elapsed }
    val homeTeamPenaltyScore = game?.gameInfo?.let { (it as? FBGameInfoForSchedule)?.homeTeamPenaltyScore }
    val awayTeamPenaltyScore = game?.gameInfo?.let { (it as? FBGameInfoForSchedule)?.awayTeamPenaltyScore }
    val shouldShowScore = !Constants.GameStatus.isBeforeGame(leagueId, gameStatus)

    val isHomeWinner = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
        homeTeamPenaltyScore > awayTeamPenaltyScore
    } else {
        homeTeamScore > awayTeamScore
    }

    // function
    fun h(r: Int, s: Int): Dp {
        return itemHeights[RoundSeriesKey(r, s)] ?: 0.dp
    }

    // ui
    CenterColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .padding(top = 250.dp)
            .padding(horizontal = 8.dp)
            .border(BorderStroke(1.dp, Moare), RoundedCornerShape(10.dp))
            .padding(vertical = 6.dp, horizontal = 8.dp)
            .clickable { game?.let { selectGame?.let { it(game) } } }
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

            CenterColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // 축구 패널티킥 경기는 일반 스코어 검정색
                val scoreColor = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                    Color.Black
                } else {
                    if (homeTeamScore >= awayTeamScore) Moare else Color.Black
                }

                Text(
                    text = if (shouldShowScore) homeTeamScore.toString() else "-",
                    color = if (shouldShowScore) scoreColor else Color.Black,
                    textAlign = TextAlign.Center
                )

                if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                    Text(
                        text = homeTeamPenaltyScore.toString(),
                        fontSize = 12.sp,
                        color = if (homeTeamPenaltyScore >= awayTeamPenaltyScore) Moare else Color.Black
                    )
                }
            }

            CenterColumn(
                modifier = Modifier.width(110.dp)
            ) {
                // game status
                CapsuleButton(
                    text = Constants.GameStatus.fbGameStatusText(gameStatus, elapsed),
                    color = Constants.GameStatus.gameStatusColor(leagueId, gameStatus)
                ) { }

                // game date
                game?.date?.let {
                    Text(
                        text = CalendarUtil.formatDate(game.date).split(" ").firstOrNull() ?: "",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Text(
                        text = CalendarUtil.formatDate(game.date, formatType = TimeFormatType.AMPM),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            CenterColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // 축구 패널티킥 경기는 일반 스코어 검정색
                val scoreColor = if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                    Color.Black
                } else {
                    if (awayTeamScore >= homeTeamScore) Moare else Color.Black
                }

                Text(
                    text = if (shouldShowScore) awayTeamScore.toString() else "-",
                    color = if (shouldShowScore) scoreColor else Color.Black,
                    textAlign = TextAlign.Center
                )

                if (homeTeamPenaltyScore != null && awayTeamPenaltyScore != null) {
                    Text(
                        text = awayTeamPenaltyScore.toString(),
                        fontSize = 12.sp,
                        color = if (awayTeamPenaltyScore >= homeTeamPenaltyScore) Moare else Color.Black
                    )
                }
            }

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
    }
}