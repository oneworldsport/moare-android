package com.moare.android.features.search.display.nba.view

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.R
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.NBAUtil
import com.moare.android.features.search.display.common.components.EmptyStatDataItem
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.nba.store.NBAPlayerStatsStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.models.models.nba.NBAPlayerStats
import com.moare.android.ui.components.HDivider
import com.moare.android.ui.components.NBATitle
import com.moare.android.ui.components.StatsDivider
import com.moare.android.ui.components.URLImage
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow

@Composable
fun NBAPlayerStatsView(
    searchStore: SearchStore,
    store: NBAPlayerStatsStore
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by store.displayModel.collectAsState()
    val statsList = displayModel.stats

    InfoViewContainer(
        searchStore = searchStore,
        itemCount = statsList.size + 1,
        shouldShowMeasureContent = true,
        modifier = Modifier,
//            .verticalScroll(rememberScrollState()),
        measureContent = {
            NBAPlayerStatsPlayerInfoItem(store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            NBAPlayerStatsList(store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        },
        displayContent = {
            NBAPlayerStatsPlayerInfoItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha,
                measureContentAlpha = measureContentAlpha
            )

            NBAPlayerStatsList(
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

// player info
@Composable
fun NBAPlayerStatsPlayerInfoItem(
    store: NBAPlayerStatsStore,
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    startPosition: Offset = Offset.Zero,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 1f,
    containerModifier: Modifier = Modifier,
    measureContentAlpha: Float = 0f,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by store.displayModel.collectAsState()
    val playerNameDic by store.playerNameDic.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val player = displayModel.player

    MovingCapsuleItemContainer(
        isAniItem = isAniItem,
        itemSize = itemSize,
        itemPosition = itemPosition,
        startPosition = startPosition,
        aniPosition = aniPosition,
        updateItemPosition = { coordinates ->
            updateItemPosition?.let { it(0, coordinates) }
        },
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
            URLImage(url = NBAUtil.playerPhotoUrl(player.personId))

            // name
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 6.dp, end = 8.dp)
            ) {
                Text(
                    text = playerNameDic[player.personId.toString()] ?: player.displayFirstLast,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = player.displayFirstLast,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 2
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "국적: ",
                        fontSize = 15.sp
                    )

                    Text(
                        text = player.country,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            URLImage(
                url = NBAUtil.teamLogoUrl(player.teamId)
            )

            // nationality, team, jersey, position
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 6.dp)
            ) {
                Text(
                    text = teamNameDic["full_${player.teamId}"] ?: "${player.teamCity} ${player.teamName}",
                    fontWeight = FontWeight.Medium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "등번호: ",
                        fontSize = 15.sp
                    )

                    Text(
                        text = player.jersey,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "포지션: ",
                        fontSize = 15.sp
                    )

                    Text(
                        text = player.position,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// stats list
@Composable
fun NBAPlayerStatsList(
    store: NBAPlayerStatsStore,
    isAniItem: Boolean = false,
    itemSizes: Map<Int, DpSize>? = null,
    itemPositions: Map<Int, Offset>? = null,
    startPosition: Offset = Offset.Zero,
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
        NBAPlayerStatsListItem(
            index = index,
            data = value,
            isAniItem = isAniItem,
            itemSizes = itemSizes,
            itemPositions = itemPositions,
            startPosition = startPosition,
            aniPosition = aniPosition,
            contentsAlpha = contentsAlpha,
            containerModifier = containerModifier,
            measureContentAlpha = measureContentAlpha,
            updateItemPosition = updateItemPosition
        )
    }
}

@Composable
fun NBAPlayerStatsListItem(
     index: Int,
     data: NBAPlayerStats,
     isAniItem: Boolean,
     itemSizes: Map<Int, DpSize>?,
     itemPositions: Map<Int, Offset>?,
     startPosition: Offset,
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
        startPosition = startPosition,
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
        NBAPlayerStatsItem(
            data = data,
            contentsAlpha = contentsAlpha,
            measureContentAlpha = measureContentAlpha
        )
    }
}

@Composable
fun NBAPlayerStatsItem(
    data: NBAPlayerStats,
    contentsAlpha: Float,
    measureContentAlpha: Float,
) {
    var attackStatsOpenState by remember { mutableStateOf(true) }
    var defendStatsOpenState by remember { mutableStateOf(false) }
    var penaltyStatsOpenState by remember { mutableStateOf(false) }
    var etcStatsOpenState by remember { mutableStateOf(false) }

    /* ---------------------
       ui
       --------------------- */
    CenterColumn(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        // league
        NBATitle(
            leagueName = "NBA 정규시즌",
            leagueSeason = data.groupValue.split("-").firstOrNull()?.toIntOrNull() ?: CalendarUtil.currentYear
        )

        // stats
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
            CenterColumn {
                CenterRow {
                    FBStatDataItem(
                        category = "경기당 득점",
                        data = data.ptsPG.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "경기당 공격 리바운드",
                        data = data.orebPG.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "경기당 어시스트",
                        data = data.astPG.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "경기당 야투 시도",
                        data = data.fgaPG.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "경기당 야투 성공",
                        data = data.fgmPG.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "야투 성공률",
                        data = data.fgPct.toString(),
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
                        category = "경기당 3점 시도",
                        data = data.fg3aPG.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "경기당 3점 성공",
                        data = data.fg3mPG.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "3점 성공률",
                        data = data.fg3Pct.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "경기당 자유투 시도",
                        data = data.ftaPG.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "경기당 자유투 성공",
                        data = data.ftmPG.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "자유투 성공률",
                        data = data.ftPct.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                }
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
                    category = "경기당 수비 리바운드",
                    data = data.drebPG.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "경기당 블록",
                    data = data.blkPG.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "경기당 스틸",
                    data = data.stlPG.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        CenterRow(
            modifier = Modifier
                .clickable { penaltyStatsOpenState = !penaltyStatsOpenState }
        ) {
            Text(
                text = "패널티 기록",
                modifier = Modifier
                    .padding(end = 4.dp)
            )

            Icon(
                painter = painterResource(id = if (penaltyStatsOpenState) R.drawable.ic_round_arrow_drop_up_24 else R.drawable.ic_round_arrow_drop_down_24),
                contentDescription = null,
                tint = Color.Gray
            )
        }

        AnimatedVisibility(
            visible = penaltyStatsOpenState,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            CenterRow {
                FBStatDataItem(
                    category = "경기당 턴오버",
                    data = data.tovPG.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "경기당 파울",
                    data = data.pfPG.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "경기당 피블록",
                    data = data.blkaPG.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        CenterRow(
            modifier = Modifier
                .clickable { etcStatsOpenState = !etcStatsOpenState }
        ) {
            Text(
                text = "공통/기타 기록",
                modifier = Modifier
                    .padding(end = 4.dp)
            )

            Icon(
                painter = painterResource(id = if (etcStatsOpenState) R.drawable.ic_round_arrow_drop_up_24 else R.drawable.ic_round_arrow_drop_down_24),
                contentDescription = null,
                tint = Color.Gray
            )
        }

        AnimatedVisibility(
            visible = etcStatsOpenState,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            CenterColumn {
                CenterRow {
                    FBStatDataItem(
                        category = "경기수",
                        data = data.gp.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "경기당 출전 시간",
                        data = data.minPG,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "경기당 리바운드",
                        data = data.rebPG.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "(출전 경기)승",
                        data = data.wins.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "(출전 경기)패",
                        data = data.losses.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "(출전 경기)승률",
                        data = data.winsPct.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                }

                HDivider(
                    modifier = Modifier.alpha(0.5f).padding(vertical = 4.dp),
                    color = Color.Gray
                )

                CenterRow {
                    FBStatDataItem(
                        category = "경기당 파울 유도",
                        data = data.pfdPG.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "경기당 득실마진",
                        data = data.plusMinusPG.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "더블더블",
                        data = data.dd2.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "트리플더블",
                        data = data.td3.toString(),
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider(modifier = Modifier.alpha(0f))
                    EmptyStatDataItem(modifier = Modifier.weight(1f))
                    StatsDivider(modifier = Modifier.alpha(0f))
                    EmptyStatDataItem(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}




























