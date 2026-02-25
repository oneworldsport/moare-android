package com.moare.android.features.search.display.mlb.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.R
import com.moare.android.core.util.MLBUtil
import com.moare.android.features.search.display.common.components.EmptyStatDataItem
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.mlb.store.MLBTeamStatsStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.models.models.mlb.MLBTeamStats
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.HDivider
import com.moare.android.ui.common.components.StatsDivider
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow

@Composable
fun MLBTeamStatsView(
    searchStore: SearchStore,
    store: MLBTeamStatsStore
) {
    val displayModel by store.displayModel.collectAsState()
    val statsList = displayModel.stats

    InfoViewContainer(
        searchStore = searchStore,
        itemCount = statsList.size + 1,
        shouldShowMeasureContent = true,
        modifier = Modifier,
//            .verticalScroll(rememberScrollState()),
        measureContent = {
            MLBTeamStatsTeamInfoItem(store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            MLBTeamStatsList(store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        }, displayContent = {
            MLBTeamStatsTeamInfoItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha,
                measureContentAlpha = measureContentAlpha
            )

            MLBTeamStatsList(
                store = store,
                isAniItem = true,
                itemSizes = itemSizes,
                itemPositions = itemPositions,
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha,
                measureContentAlpha = measureContentAlpha
            )
        }
    )
}

// team info
@Composable
fun MLBTeamStatsTeamInfoItem(
    store: MLBTeamStatsStore,
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 1f,
    containerModifier: Modifier = Modifier,
    measureContentAlpha: Float = 0f,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by store.displayModel.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val team = displayModel.team
    val venue = displayModel.venue

    MovingCapsuleItemContainer(
        isAniItem = isAniItem,
        itemSize = itemSize,
        itemPosition = itemPosition,
        aniPosition = aniPosition,
        updateItemPosition = { coordinates ->
            updateItemPosition?.let { it(0, coordinates) }
        },
        modifier = containerModifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 8.dp)
                .alpha(contentsAlpha)
        ) {
            URLImage(
                url = MLBUtil.teamLogoUrl(team.id)
            )

            // name, state and city
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 6.dp)
            ) {
                Text(
                    text = teamNameDic["full_${team.id}"] ?: team.teamName,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = team.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 2
                )

                Text(
                    text = buildAnnotatedString {
                        append("연고지: ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                            append(team.locationName)
                        }
                    },
                    fontSize = 15.sp
                )
            }

            // venue, conference, division
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("홈구장: ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                            append(teamNameDic["venue_${team.id}"] ?: venue.name)
                        }
                    },
                    fontSize = 15.sp
                )

                Text(
                    text = buildAnnotatedString {
                        append("리그: ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                            append(MLBUtil.leagueDivisionMap[team.league.id] ?: team.league.name)
                        }
                    },
                    fontSize = 15.sp
                )

                Text(
                    text = buildAnnotatedString {
                        append("디비전: ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                            append(MLBUtil.leagueDivisionMap[team.division.id] ?: team.division.name)
                        }
                    },
                    fontSize = 15.sp
                )
            }
        }
    }
}

// stats list
@Composable
fun MLBTeamStatsList(
    store: MLBTeamStatsStore,
    isAniItem: Boolean = false,
    itemSizes: Map<Int, DpSize>? = null,
    itemPositions: Map<Int, Offset>? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 1f,
    containerModifier: Modifier = Modifier,
    measureContentAlpha: Float = 0f,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by store.displayModel.collectAsState()

    val statsList = displayModel.stats

    /* ---------------------
       ui
       --------------------- */
    for ((index, value) in statsList.withIndex()) {
        MLBTeamStatsListItem(
            store = store,
            index = index,
            data = value,
            isAniItem = isAniItem,
            itemSizes = itemSizes,
            itemPositions = itemPositions,
            aniPosition = aniPosition,
            contentsAlpha = contentsAlpha,
            containerModifier = containerModifier,
            measureContentAlpha = measureContentAlpha,
            updateItemPosition = updateItemPosition
        )
    }
}

@Composable
fun MLBTeamStatsListItem(
    store: MLBTeamStatsStore,
    index: Int,
    data: MLBTeamStats,
    isAniItem: Boolean,
    itemSizes: Map<Int, DpSize>?,
    itemPositions: Map<Int, Offset>?,
    aniPosition: Boolean,
    contentsAlpha: Float,
    containerModifier: Modifier = Modifier,
    measureContentAlpha: Float,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)?
) {
    MovingCapsuleItemContainer(
        isAniItem = isAniItem,
        itemSize = itemSizes?.get(index + 1),
        itemPosition = itemPositions?.get(index + 1),
        aniPosition = aniPosition,
        updateItemPosition = { coordinates ->
            updateItemPosition?.let { it(index + 1, coordinates) }
        },
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = containerModifier
            .padding(top = if (isAniItem) 0.dp else 12.dp)
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        MLBTeamStatsItem(
            store = store,
            data = data,
            contentsAlpha = contentsAlpha,
            measureContentAlpha = measureContentAlpha
        )
    }
}

@Composable
fun MLBTeamStatsItem(
    store: MLBTeamStatsStore,
    data: MLBTeamStats,
    contentsAlpha: Float,
    measureContentAlpha: Float,
) {
    var basicStatsOpenState by remember { mutableStateOf(true) }
    var hitterStatsOpenState by remember { mutableStateOf(false) }
    var pitcherStatsOpenState by remember { mutableStateOf(false) }

    val displayModel by store.displayModel.collectAsState()
    val team = displayModel.team
    val record = data.recordData
    val hitting = data.hitting
    val pitching = data.pitching
    val fielding = data.fielding
    val catching = data.catching

    /* ---------------------
       ui
       --------------------- */
    CenterColumn(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        if (record != null && hitting != null && pitching != null && fielding != null && catching != null) {
            // league
            BaseballLeagueTitle(
                url = MLBUtil.mlbLogoUrl,
                leagueName = "MLB",
                leagueSeason = team.season
            )

            // stats
            CenterRow(
                modifier = Modifier
                    .clickable { basicStatsOpenState = !basicStatsOpenState }
            ) {
                Text(
                    text = "기본 기록",
                    modifier = Modifier
                        .padding(end = 4.dp)
                )

                Icon(
                    painter = painterResource(id = if (basicStatsOpenState) R.drawable.ic_round_arrow_drop_up_24 else R.drawable.ic_round_arrow_drop_down_24),
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            AnimatedVisibility(
                visible = basicStatsOpenState,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                CenterRow {
                    FBStatDataItem(
                        category = "${MLBUtil.leagueDivisionMap[team.league.id] ?: team.league.name} 순위",
                        data = record.divisionRank,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "승",
                        data = record.wins.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "패",
                        data = record.losses.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "경기수",
                        data = record.gamesPlayed.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "게임차",
                        data = record.gamesBack,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "승률",
                        data = record.winningPercentage,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            CenterRow(
                modifier = Modifier
                    .clickable { hitterStatsOpenState = !hitterStatsOpenState }
            ) {
                Text(
                    text = "타자 기록",
                    modifier = Modifier
                        .padding(end = 4.dp)
                )

                Icon(
                    painter = painterResource(id = if (hitterStatsOpenState) R.drawable.ic_round_arrow_drop_up_24 else R.drawable.ic_round_arrow_drop_down_24),
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            AnimatedVisibility(
                visible = hitterStatsOpenState,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                CenterColumn {
                    CenterRow {
                        FBStatDataItem(
                            category = "타율",
                            data = hitting.avg,
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "안타",
                            data = hitting.hits.toString(),
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "홈런",
                            data = hitting.homeRuns.toString(),
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "출루율",
                            data = hitting.obp,
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "장타율",
                            data = hitting.slg,
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HDivider(
                        modifier = Modifier.alpha(0.5f).padding(vertical = 4.dp),
                        color = Color.Gray,
                    )

                    CenterRow {
                        FBStatDataItem(
                            category = "ops",
                            data = hitting.ops,
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "득점",
                            data = hitting.runs.toString(),
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "타점",
                            data = hitting.rbi.toString(),
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "삼진",
                            data = hitting.strikeOuts.toString(),
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "사구",
                            data = hitting.hitByPitch.toString(),
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HDivider(
                        modifier = Modifier.alpha(0.5f).padding(vertical = 4.dp),
                        color = Color.Gray,
                    )

                    CenterRow {
                        FBStatDataItem(
                            category = "병살타",
                            data = hitting.groundIntoDoublePlay.toString(),
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "땅볼아웃",
                            data = hitting.groundOuts.toString(),
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "도루성공",
                            data = hitting.stolenBases.toString(),
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "도루실패",
                            data = hitting.caughtStealing.toString(),
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider(modifier = Modifier.alpha(0f))
                        EmptyStatDataItem(modifier = Modifier.weight(1f))
                    }
                }
            }

            CenterRow(
                modifier = Modifier
                    .clickable { pitcherStatsOpenState = !pitcherStatsOpenState }
            ) {
                Text(
                    text = "투수 기록",
                    modifier = Modifier
                        .padding(end = 4.dp)
                )

                Icon(
                    painter = painterResource(id = if (pitcherStatsOpenState) R.drawable.ic_round_arrow_drop_up_24 else R.drawable.ic_round_arrow_drop_down_24),
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            AnimatedVisibility(
                visible = pitcherStatsOpenState,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                CenterColumn {
                    CenterRow {
                        FBStatDataItem(
                            category = "평균자책점",
                            data = pitching.era,
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "피안타율",
                            data = pitching.avg,
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "피안타",
                            data = pitching.hits.toString(),
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "피홈런",
                            data = pitching.homeRuns.toString(),
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "세이브",
                            data = pitching.saves.toString(),
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HDivider(
                        modifier = Modifier.alpha(0.5f).padding(vertical = 4.dp),
                        color = Color.Gray,
                    )

                    CenterRow {
                        FBStatDataItem(
                            category = "이닝당 출루허용률",
                            data = pitching.whip,
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "볼넷",
                            data = pitching.baseOnBalls.toString(),
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "보크",
                            data = pitching.balks.toString(),
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "홀드",
                            data = pitching.holds.toString(),
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "블론세이브",
                            data = pitching.blownSaves.toString(),
                            customCategoryFontSize = 11,
                            customCategoryHeight = 30.dp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

}