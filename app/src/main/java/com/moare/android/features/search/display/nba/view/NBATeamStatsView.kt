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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.R
import com.moare.android.core.util.NBAUtil
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStatsAction
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStatsStore
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStatsDisplayModel
import com.moare.android.features.search.models.models.nba.NBATeamStats
import com.moare.android.ui.common.components.HDivider
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.StatsDivider
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow

@Composable
fun NBATeamStatsView(
    searchStore: SearchViewModel,
    store: NBATeamStatsStore
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
            NBATeamStatsTeamInfoItem(store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            NBATeamStatsList(store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        }, displayContent = {
            NBATeamStatsTeamInfoItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha,
                measureContentAlpha = measureContentAlpha
            )

            NBATeamStatsList(
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
fun NBATeamStatsTeamInfoItem(
    store: NBATeamStatsStore,
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
                url = NBAUtil.teamLogoUrl(team.id),
                isSvg = true
            )

            // name, state and city
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 6.dp)
            ) {
                Text(
                    text = teamNameDic["full_${team.id}"] ?: team.fullName,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = team.fullName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 2
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "연고지: ",
                        fontSize = 15.sp
                    )

                    Text(
                        text = team.state,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // venue, conference, division
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
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

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "컨퍼런스: ",
                        fontSize = 15.sp
                    )

                    Text(
                        text = NBAUtil.translateEastWest(team.teamConference),
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "디비전: ",
                        fontSize = 15.sp
                    )

                    Text(
                        text = team.teamDivision,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// stats list
@Composable
fun NBATeamStatsList(
    store: NBATeamStatsStore,
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
        NBATeamStatsListItem(
            store = store,
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
fun NBATeamStatsListItem(
    store: NBATeamStatsStore,
    index: Int,
    data: NBATeamStats,
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
        NBATeamStatsItem(
            store = store,
            data = data,
            contentsAlpha = contentsAlpha,
            measureContentAlpha = measureContentAlpha
        )
    }
}

@Composable
fun NBATeamStatsItem(
    store: NBATeamStatsStore,
    data: NBATeamStats,
    contentsAlpha: Float,
    measureContentAlpha: Float,
) {
    var basicStatsOpenState by remember { mutableStateOf(true) }
    var attackStatsOpenState by remember { mutableStateOf(false) }
    var defendStatsOpenState by remember { mutableStateOf(false) }
    var penaltyStatsOpenState by remember { mutableStateOf(false) }
    var etcStatsOpenState by remember { mutableStateOf(false) }

    val displayModel by store.displayModel.collectAsState()
    val team = displayModel.team

    /* ---------------------
       ui
       --------------------- */
    CenterColumn(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        // league
        NBATitle(
            leagueName = "NBA 정규시즌",
            leagueSeason = data.groupValue.split("-").firstOrNull()?.toIntOrNull()
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
                    category = "${NBAUtil.translateEastWest(team.teamConference)}컨퍼런스 순위",
                    data = team.confRank.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "승",
                    data = data.wins.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "패",
                    data = data.losses.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "경기수",
                    data = data.gp.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "승률",
                    data = data.winsPct.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
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
            CenterRow {
                FBStatDataItem(
                    category = "경기당 리바운드",
                    data = data.rebPG.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
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
            }
        }
    }
}
































