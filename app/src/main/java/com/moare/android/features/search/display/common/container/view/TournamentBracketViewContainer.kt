package com.moare.android.features.search.display.common.container.view

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.moare.android.core.constants.Constants
import com.moare.android.features.search.display.common.container.component.TournamentSeriesFinalGameItem
import com.moare.android.features.search.display.common.container.component.TournamentSeriesLeftGameItem
import com.moare.android.features.search.display.common.container.component.TournamentSeriesRightGameItem
import com.moare.android.features.search.display.common.container.state.TournamentBracketContainerState
import com.moare.android.features.search.display.common.container.state.TournamentContainerAction
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.util.CenterColumn
import kotlin.math.max

data class RoundSeriesKey(
    val round: Int,
    val series: Int
)

fun clampOffset(
    offset: Offset,
    scale: Float,
    container: IntSize,
    content: IntSize
): Offset {
    val scaledWidth = content.width * scale
    val scaledHeight = content.height * scale

    val maxX = max(0f, (scaledWidth - container.width) / 2f)
    val maxY = max(0f, (scaledHeight - container.height) / 2f)

    return Offset(
        x = offset.x.coerceIn(-maxX, maxX),
        y = offset.y.coerceIn(-maxY, maxY)
    )
}

@Composable
fun <T> TournamentBracketViewContainer(
    state: TournamentBracketContainerState<T>,
    action: TournamentContainerAction<T>
) {
    val leftBracketTitles = listOf("서부", "NL", "와일드카드", "준플레이오프", "플레이오프", "한국시리즈")
    val rightBracketTitles = listOf("동부", "AL")
    val finalBracketTitles = listOf("NBA", "월드", "MLS")
    val mlbBracketTitles = listOf("NL", "AL")

    var leftItemHeights = remember { mutableStateMapOf<RoundSeriesKey, Dp>() }
    val rightItemHeights = remember { mutableStateMapOf<RoundSeriesKey, Dp>() }

    // zoom
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var contentSize by remember { mutableStateOf(IntSize.Zero) }

    val minScale = 0.3f
    val maxScale = 1.3f

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(minScale, maxScale)

        val newOffset = clampOffset(
            offset = offset + panChange,
            scale = newScale,
            container = containerSize,
            content = contentSize
        )

        scale = newScale
        offset = newOffset
    }

    // 처음 오픈 시 좌측 상단으로 offset설정
    var initialized by remember { mutableStateOf(false) }
    LaunchedEffect(containerSize, contentSize) {
        if (!initialized &&
            containerSize != IntSize.Zero &&
            contentSize != IntSize.Zero
        ) {
            offset = clampOffset(
                offset = Offset(
                    x = (contentSize.width - containerSize.width) / 2f,
                    y = (contentSize.height - containerSize.height) / 2f
                ),
                scale = scale,
                container = containerSize,
                content = contentSize
            )
            initialized = true
        }
    }

    // function
    fun h(r: Int, s: Int, isLeft: Boolean): Dp {
        val key = RoundSeriesKey(r, s)
        return if (isLeft) {
            leftItemHeights[key] ?: 0.dp
        } else {
            rightItemHeights[key] ?: 0.dp
        }
    }

    fun bottomPadding(r: Int, s: Int, isLeft: Boolean): Dp {
        return if (isLeft) {
            when (r to s) {
                1 to 1 -> h(2, 1, isLeft)
                1 to 2 -> h(3, 1, isLeft)
                1 to 3 -> h(2, 2, isLeft)
                2 to 1 -> h(3, 1, isLeft)
                else -> 0.dp
            }
        } else {
            when (r to s) {
                7 to 1 -> h(6, 1, isLeft)
                7 to 2 -> h(5, 1, isLeft)
                7 to 3 -> h(6, 2, isLeft)
                6 to 1 -> h(5, 1, isLeft)
                else -> 0.dp
            }
        }
    }

    Box(
        modifier = Modifier
            .onSizeChanged { containerSize = it }
            .clipToBounds()
            .transformable(state = transformableState)
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize(unbounded = true)
                .onSizeChanged { contentSize = it }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                }
        ) {
            state.gameListTuple.forEachIndexed { roundIndex, item ->
                val roundIndexForPosition = roundIndex + 1
                val title = item.title
                val gameList = item.gameList
                val isLeft = if (state.isConference) {
                    leftBracketTitles.contains(title.split(" ").firstOrNull() ?: "")
                } else {
                    true
                }
                val isMLB = state.leagueId == Constants.Ids.MLB
                val isKBO = state.leagueId == Constants.Ids.KBO
                val isSeries = if (state.leagueId == Constants.Ids.MLS) {
                    // mls는 (동/서부)1라운드만 series
                    roundIndex == 0 || roundIndex == 6
                } else if (state.leagueId in Constants.Ids.FOOTBALL_UEFA_LEAGUES) {
                    // uefa리그들은 final만 single
                    roundIndex != 3
                } else {
                    state.isSeries
                }

                // left
                if (isLeft) {
                    CenterColumn(
                        modifier = Modifier.widthIn(min = 170.dp)
                    ) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Medium
                        )
                        HCapsuleBar(
                            modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
                        )

                        gameList.forEachIndexed { seriesIndex, games ->
                            val seriesIndexForPosition = seriesIndex + 1
                            val bottom = bottomPadding(roundIndexForPosition, seriesIndexForPosition, true)

                            if (isSeries) {
                                TournamentSeriesLeftGameItem(
                                    leagueId = state.leagueId,
                                    teamNameDic = state.teamNameDic,
                                    games = games,
                                    itemPosition = RoundSeriesKey(
                                        roundIndexForPosition,
                                        seriesIndexForPosition
                                    ),
                                    shouldRemoveBar = isKBO || (isMLB && roundIndexForPosition == 2), // mlb 2라운드, kbo
                                    itemHeights = leftItemHeights,
                                    modifier = Modifier.padding(bottom = bottom),
                                    onItemHeightChange = { key, height ->
                                        leftItemHeights[key] = height
                                    },
                                    selectSeries = action.selectSeries
                                )
                            } else {
                                TournamentBracketSingleLeftGameItem(
                                    leagueId = state.leagueId,
                                    teamNameDic = state.teamNameDic,
                                    game = games?.firstOrNull(),
                                    itemPosition = RoundSeriesKey(
                                        roundIndexForPosition,
                                        seriesIndexForPosition
                                    ),
                                    itemHeights = leftItemHeights,
                                    modifier = Modifier.padding(bottom = bottom),
                                    onItemHeightChange = { key, height ->
                                        leftItemHeights[key] = height
                                    },
                                    selectGame = action.selectGame
                                )
                            }
                        }
                    }
                }

                if (state.isConference) {
                    // final
                    if (finalBracketTitles.contains(title.split(" ").firstOrNull() ?: "")) {
                        CenterColumn(
                            modifier = Modifier.widthIn(min = 170.dp)
                        ) {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Medium
                            )
                            HCapsuleBar(
                                modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
                            )

                            gameList.firstOrNull()?.let { games ->
                                if (isSeries) {
                                    TournamentSeriesFinalGameItem(
                                        leagueId = state.leagueId,
                                        teamNameDic = state.teamNameDic,
                                        games = games,
                                        itemHeights = leftItemHeights,
                                        selectSeries = action.selectSeries
                                    )
                                } else {
                                    TournamentBracketSingleFinalGameItem(
                                        leagueId = state.leagueId,
                                        teamNameDic = state.teamNameDic,
                                        game = games.firstOrNull(),
                                        itemHeights = leftItemHeights,
                                        selectGame = action.selectGame
                                    )
                                }
                            }
                        }
                    }

                    // right
                    if (rightBracketTitles.contains(title.split(" ").firstOrNull() ?: "")) {
                        CenterColumn(
                            modifier = Modifier.widthIn(min = 170.dp)
                        ) {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Medium
                            )
                            HCapsuleBar(
                                modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
                            )

                            gameList.forEachIndexed { seriesIndex, games ->
                                val seriesIndexForPosition = seriesIndex + 1
                                val bottom =
                                    bottomPadding(roundIndexForPosition, seriesIndexForPosition, false)

                                if (isSeries) {
                                    TournamentSeriesRightGameItem(
                                        leagueId = state.leagueId,
                                        teamNameDic = state.teamNameDic,
                                        games = games,
                                        itemPosition = RoundSeriesKey(
                                            roundIndexForPosition,
                                            seriesIndexForPosition
                                        ),
                                        shouldRemoveBar = isMLB && roundIndexForPosition == 6, // mlb 2라운드만
                                        itemHeights = rightItemHeights,
                                        modifier = Modifier.padding(bottom = bottom),
                                        onItemHeightChange = { key, height ->
                                            rightItemHeights[key] = height
                                        },
                                        selectSeries = action.selectSeries
                                    )
                                } else {
                                    TournamentBracketSingleRightGameItem(
                                        leagueId = state.leagueId,
                                        teamNameDic = state.teamNameDic,
                                        game = games?.firstOrNull(),
                                        itemPosition = RoundSeriesKey(
                                            roundIndexForPosition,
                                            seriesIndexForPosition
                                        ),
                                        itemHeights = rightItemHeights,
                                        modifier = Modifier.padding(bottom = bottom),
                                        onItemHeightChange = { key, height ->
                                            rightItemHeights[key] = height
                                        },
                                        selectGame = action.selectGame
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}