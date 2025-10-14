package com.moare.android.features.search.display.football.view

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.R
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.football.viewmodel.FBTeamStatsStore
import com.moare.android.features.search.display.search.viewmodel.SearchStore
import com.moare.android.features.search.models.models.football.FBTeamStats
import com.moare.android.ui.common.components.FBLeagueTitle
import com.moare.android.ui.common.components.StatsDivider
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow

@Composable
fun FBTeamStatsView(
    searchStore: SearchStore,
    store: FBTeamStatsStore
) {
    val displayModel by store.displayModel.collectAsState()
    val statsList = displayModel.stats

    InfoViewContainer(
        searchStore = searchStore,
        itemCount = statsList.size + 1,
        shouldShowMeasureContent = true,
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        measureContent = {
            FBTeamStatsTeamInfoItem(store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            FBTeamStatsList(store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        }, displayContent = {
            FBTeamStatsTeamInfoItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha,
                measureContentAlpha = measureContentAlpha
            )

            FBTeamStatsList(
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
fun FBTeamStatsTeamInfoItem(
    store: FBTeamStatsStore,
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
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = containerModifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 8.dp)
                .alpha(contentsAlpha)
        ) {
            URLImage(url = team.logo)

            // name
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    text = teamNameDic["full_${team.id}"] ?: team.name,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = team.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 2
                )
            }

            // venue
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "연고지: ",
                        fontSize = 15.sp
                    )

                    Text(
                        text = venue.city,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "홈구장: ",
                        fontSize = 15.sp
                    )

                    Text(
                        text = teamNameDic["venue_${team.id}"] ?: venue.name,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// team stats list
@Composable
fun FBTeamStatsList(
    store: FBTeamStatsStore,
    isAniItem: Boolean = false,
    itemSizes: Map<Int, DpSize>? = null,
    itemPositions: Map<Int, Offset>? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 1f,
    containerModifier: Modifier = Modifier,
    measureContentAlpha: Float = 0f,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val statsList by store.statsList.collectAsState()

    for ((index, value) in statsList.withIndex()) {
        FBTeamStatsListItem(
            index = index,
            data = value,
            isAniItem = isAniItem,
            itemSizes = itemSizes,
            itemPositions = itemPositions,
            aniPosition = aniPosition,
            contentsAlpha = contentsAlpha,
            containerModifier = containerModifier,
            updateItemPosition = updateItemPosition,
            measureContentAlpha = measureContentAlpha
        )
    }
}

@Composable
fun FBTeamStatsListItem(
    index: Int,
    data: FBTeamStats,
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
        FBTeamStatsItem(
            data = data,
            contentsAlpha = contentsAlpha,
            measureContentAlpha = measureContentAlpha
        )
    }
}

@Composable
fun FBTeamStatsItem(
    data: FBTeamStats,
    contentsAlpha: Float,
    measureContentAlpha: Float,
) {
    var basicStatsOpenState by remember { mutableStateOf(true) }
    var attackStatsOpenState by remember { mutableStateOf(false) }
    var defendStatsOpenState by remember { mutableStateOf(false) }

    /* ---------------------
       ui
       --------------------- */
    CenterColumn(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        // league
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .alpha(contentsAlpha)
        ) {
            FBLeagueTitle(
                url = data.league.logo,
                leagueName = data.league.name,
                leagueSeason = data.league.season
            )
        }

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
                    category = "경기수",
                    data = data.fixtures.played.total.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "승",
                    data = data.fixtures.wins.total.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "무",
                    data = data.fixtures.draws.total.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "패",
                    data = data.fixtures.loses.total.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        CenterRow(
            modifier = Modifier
                .clickable { attackStatsOpenState = !attackStatsOpenState }
        ) {
            Text(
                text = "공격 기록",
                modifier = Modifier
                    .padding(end = 4.dp)
            )

            Icon(
                painter = painterResource(id = if (attackStatsOpenState) R.drawable.ic_round_arrow_drop_up_24 else R.drawable.ic_round_arrow_drop_down_24),
                contentDescription = null,
                tint = Color.Gray
            )
        }

        AnimatedVisibility(
            visible = attackStatsOpenState,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            CenterRow {
                FBStatDataItem(
                    category = "득점",
                    data = data.goals.teamGoalsFor.total.total.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "경기당 평균득점",
                    data = data.goals.teamGoalsFor.average.total,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "득실차",
                    data = ((data.goals.teamGoalsFor.total.total) - (data.goals.teamGoalsAgainst.total.total)).toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        CenterRow(
            modifier = Modifier
                .clickable { defendStatsOpenState = !defendStatsOpenState }
        ) {
            Text(
                text = "수비 기록",
                modifier = Modifier
                    .padding(end = 4.dp)
            )

            Icon(
                painter = painterResource(id = if (defendStatsOpenState) R.drawable.ic_round_arrow_drop_up_24 else R.drawable.ic_round_arrow_drop_down_24),
                contentDescription = null,
                tint = Color.Gray
            )
        }

        AnimatedVisibility(
            visible = defendStatsOpenState,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            CenterRow {
                FBStatDataItem(
                    category = "실점",
                    data = data.goals.teamGoalsAgainst.total.total.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "경기당 평균실점",
                    data = data.goals.teamGoalsAgainst.average.total,
                    customCategoryFontSize = 11,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "클린시트",
                    data = (data.cleanSheet?.total ?: 0).toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


















