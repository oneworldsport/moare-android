package com.moare.android.features.search.display.nba.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.util.EnNameTranslationUtils
import com.moare.android.core.util.NBAUtil
import com.moare.android.core.util.TranslationType
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.components.FBStatDataItem
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerStatsIntent
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerStatsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStatsDisplayModel
import com.moare.android.features.search.models.models.nba.NBAPlayerStats
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.URLImage
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun NBAPlayerStatsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaPlayerStatsViewModel: NBAPlayerStatsViewModel = hiltViewModel(),
    data: NBAPlayerStatsDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by nbaPlayerStatsViewModel.displayModel.collectAsState()
    val statsList = displayModel?.stats

    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.NBAPlayerStats) {
            nbaPlayerStatsViewModel.send(NBAPlayerStatsIntent.InitData(data))
        }
    }

    InfoViewContainer(
        itemCount = (statsList?.size ?: 0) + 1,
        modifier = Modifier,
//            .verticalScroll(rememberScrollState()),
        measureContent = {
            NBAPlayerStatsPlayerInfoItem { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            NBAPlayerStatsList { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        },
        displayContent = {
            NBAPlayerStatsPlayerInfoItem(
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBAPlayerStatsList(
                isAniItem = true,
                itemSizes = itemSizes,
                itemPositions = itemPositions,
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )
        }
    )
}

// player info
@Composable
fun NBAPlayerStatsPlayerInfoItem(
    nbaPlayerStatsViewModel: NBAPlayerStatsViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    startPosition: Offset = Offset.Zero,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by nbaPlayerStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.player

        MovingCapsuleItemContainer(
            isAniItem = isAniItem,
            itemSize = itemSize,
            itemPosition = itemPosition,
            startPosition = startPosition,
            aniPosition = aniPosition,
            updateItemPosition = { coordinates ->
                updateItemPosition?.let { it(0, coordinates) }
            },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = containerModifier.fillMaxWidth()
        ) {
            HCapsuleBar()

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .alpha(contentsAlpha)
            ) {
                URLImage(url = NBAUtil.playerPhotoUrl(player.personId))

                // name
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = nbaPlayerStatsViewModel.playerNameDictionary[player.displayFirstLast.lowercase()] ?: player.displayFirstLast,
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
                    url = NBAUtil.teamLogoUrl(player.teamId),
                    isSvg = true
                )

                // nationality, team, jersey, position
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = nbaPlayerStatsViewModel.teamNameDictionary["full_${player.teamId}"] ?: "${player.teamCity} ${player.teamName}",
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
}

// stats list
@Composable
fun NBAPlayerStatsList(
    nbaPlayerStatsViewModel: NBAPlayerStatsViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSizes: Map<Int, DpSize>? = null,
    itemPositions: Map<Int, Offset>? = null,
    startPosition: Offset = Offset.Zero,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by nbaPlayerStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val statsList = it.stats

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
                updateItemPosition = updateItemPosition
            )
        }
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
            .padding(horizontal = if (isAniItem) 0.dp else 4.dp)
            .fillMaxWidth()
    ) {
        NBAPlayerStatsItem(data, contentsAlpha)
    }
}

@Composable
fun NBAPlayerStatsItem(
    data: NBAPlayerStats,
    contentsAlpha: Float
) {
    /* ---------------------
       ui
       --------------------- */
    HCapsuleBar()

    // league
    NBATitle(
        leagueName = "NBA 정규시즌",
        leagueSeason = data.groupValue.split("-").firstOrNull()?.toIntOrNull() ?: 2024,
        modifier = Modifier.alpha(contentsAlpha)
    )

    // stats
    Row(
        modifier = Modifier
            .padding(top = 4.dp)
            .alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "경기수",
            data = data.gp.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "경기당 득점",
            data = data.ptsPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "경기당 리바운드",
            data = data.rebPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "경기당 수비 리바운드",
            data = data.drebPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "경기당 공격 리바운드",
            data = data.orebPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "경기당 어시스트",
            data = data.astPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "경기당 블록",
            data = data.blkPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "경기당 스틸",
            data = data.stlPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "경기당 턴오버",
            data = data.tovPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "경기당 야투 시도",
            data = data.fgaPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "경기당 야투 성공",
            data = data.fgmPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "야투 성공률",
            data = data.fgPct.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "경기당 3점 시도",
            data = data.fg3aPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "경기당 3점 성공",
            data = data.fg3mPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "3점 성공률",
            data = data.fg3Pct.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "경기당 자유투 시도",
            data = data.ftaPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "경기당 자유투 성공",
            data = data.ftmPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "자유투 성공률",
            data = data.ftPct.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "경기당 파울",
            data = data.pfPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "경기당 파울 유도",
            data = data.pfdPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "경기당 득실마진",
            data = data.plusMinusPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "경기당 피블록",
            data = data.blkaPG.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "더블더블",
            data = data.dd2.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "트리플더블",
            data = data.td3.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "(출전 경기)승",
            data = data.wins.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "(출전 경기)패",
            data = data.losses.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "경기당 출전 시간",
            data = data.minPG,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "(출전 경기)승률",
            data = data.winsPct.toString(),
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "",
            data = "",
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "",
            data = "",
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
    }
}




























