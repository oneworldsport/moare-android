package com.moare.android.features.search.display.common.container.view

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moare.android.core.constants.Constants
import com.moare.android.features.search.display.common.container.component.TournamentSeriesFinalGameItem
import com.moare.android.features.search.display.common.container.component.TournamentSeriesLeftGameItem
import com.moare.android.features.search.display.common.container.component.TournamentSeriesRightGameItem
import com.moare.android.features.search.display.common.container.state.TournamentBracketContainerState
import com.moare.android.features.search.display.common.container.state.TournamentContainerAction
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.util.CenterColumn

data class RoundSeriesKey(
    val round: Int,
    val series: Int
)

@Composable
fun <T> TournamentBracketViewContainer(
    state: TournamentBracketContainerState<T>,
    action: TournamentContainerAction<T>
) {
    val leftBracketTitles = listOf("서부", "NL", "와일드카드", "준플레이오프", "플레이오프", "한국시리즈")
    val rightBracketTitles = listOf("동부", "AL")
    val finalBracketTitles = listOf("NBA", "월드")
    val mlbBracketTitles = listOf("NL", "AL")

    var leftItemHeights = remember { mutableStateMapOf<RoundSeriesKey, Dp>() }
    val rightItemHeights = remember { mutableStateMapOf<RoundSeriesKey, Dp>() }

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

    // ui
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .verticalScroll(rememberScrollState())
    ) {
        state.gameListTuple.forEachIndexed { roundIndex, item ->
            val roundIndexForPosition = roundIndex + 1
            val title = item.title
            val gameList = item.gameList
            val shouldShow = if (state.isConference) leftBracketTitles.contains(title.split(" ").firstOrNull() ?: "") else true
            val isMLB = state.leagueId == Constants.Ids.MLB
            val isKBO = state.leagueId == Constants.Ids.KBO

            // default or left
            if (shouldShow) {
                CenterColumn {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Medium
                    )
                    HCapsuleBar(
                        modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
                    )

                    gameList.forEachIndexed { seriesIndex, games ->
                        val seriesIndexForPosition = seriesIndex + 1

                        if (state.isSeries) {
                            val bottom = bottomPadding(roundIndexForPosition, seriesIndexForPosition, true)

                            TournamentSeriesLeftGameItem(
                                leagueId = state.leagueId,
                                teamNameDic = state.teamNameDic,
                                games = games,
                                seedIdPair = state.seedIdPairList[roundIndex][seriesIndex],
                                itemPosition = RoundSeriesKey(roundIndexForPosition, seriesIndexForPosition),
                                shouldRemoveBar = isKBO || (isMLB && roundIndexForPosition == 2), // mlb 2라운드, kbo
                                itemHeights = leftItemHeights,
                                modifier = Modifier.padding(bottom = bottom),
                                onItemHeightChange = { key, height ->
                                    leftItemHeights[key] = height
                                },
                                selectSeries = action.selectSeries
                            )
                        } else {
                            games?.firstOrNull()?.let {
                                // TODO: Barcket인데 단판인 경우 생기면 작업
                            }
                        }
                    }
                }
            }

            if (state.isConference) {
                // final
                if (finalBracketTitles.contains(title.split(" ").firstOrNull() ?: "")) {
                    CenterColumn {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Medium
                        )
                        HCapsuleBar(
                            modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
                        )

                        gameList.firstOrNull()?.let { games ->
                            TournamentSeriesFinalGameItem(
                                leagueId = state.leagueId,
                                teamNameDic = state.teamNameDic,
                                games = games,
                                seedIdPair = state.seedIdPairList[roundIndex][0],
                                itemHeights = leftItemHeights,
                                selectSeries = action.selectSeries
                            )
                        }
                    }
                }

                // right
                if (rightBracketTitles.contains(title.split(" ").firstOrNull() ?: "")) {
                    CenterColumn {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Medium
                        )
                        HCapsuleBar(
                            modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
                        )

                        gameList.forEachIndexed { seriesIndex, games ->
                            val seriesIndexForPosition = seriesIndex + 1

                            if (state.isSeries) {
                                val bottom = bottomPadding(roundIndexForPosition, seriesIndexForPosition, false)

                                TournamentSeriesRightGameItem(
                                    leagueId = state.leagueId,
                                    teamNameDic = state.teamNameDic,
                                    games = games,
                                    seedIdPair = state.seedIdPairList[roundIndex][seriesIndex],
                                    itemPosition = RoundSeriesKey(roundIndexForPosition, seriesIndexForPosition),
                                    shouldRemoveBar = isMLB && roundIndexForPosition == 6, // mlb 2라운드만
                                    itemHeights = rightItemHeights,
                                    modifier = Modifier.padding(bottom = bottom),
                                    onItemHeightChange = { key, height ->
                                        rightItemHeights[key] = height
                                    },
                                    selectSeries = action.selectSeries
                                )
                            } else {
                                games?.firstOrNull()?.let {
                                    // TODO: Barcket인데 단판인 경우 생기면 작업
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}























