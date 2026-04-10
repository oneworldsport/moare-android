package com.moare.android.features.search.display.kbo.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.util.KBOUtil
import com.moare.android.features.search.display.common.components.EmptyStatDataItem
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.kbo.store.KBOPlayerStatsStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.models.models.kbo.KBOPlayerStats
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.HDivider
import com.moare.android.ui.common.components.StatsDivider
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow

@Composable
fun KBOPlayerStatsView(
    searchStore: SearchStore,
    store: KBOPlayerStatsStore
) {
    val displayModel by store.displayModel.collectAsState()
    val statsList = displayModel.stats

    InfoViewContainer(
        searchStore = searchStore,
        itemCount = statsList.size + 1,
//        shouldShowMeasureContent = true,
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        measureContent = {
            KBOPlayerStatsPlayerInfoItem(store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            KBOPlayerStatsList(store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        },
        displayContent = {
            KBOPlayerStatsPlayerInfoItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha,
                measureContentAlpha = measureContentAlpha
            )

            KBOPlayerStatsList(
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
fun KBOPlayerStatsPlayerInfoItem(
    store: KBOPlayerStatsStore,
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
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 8.dp)
                .alpha(contentsAlpha)
        ) {
            URLImage(url = KBOUtil.playerPhotoUrl(displayModel.season, player.id))

            // name
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 6.dp, end = 8.dp)
            ) {
                Text(
                    text = player.name,
                    fontWeight = FontWeight.Medium
                )

//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = "국적: ",
//                            fontSize = 15.sp
//                        )
//
//                        Text(
//                            text = player.country,
//                            fontWeight = FontWeight.Medium
//                        )
//                    }
            }

            URLImage(
                url = KBOUtil.teamLogoUrl(player.teamId)
            )

            // team, jersey, position
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 6.dp)
            ) {
                Text(
                    text = teamNameDic["full_${player.teamId}"] ?: "",
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
fun KBOPlayerStatsList(
    store: KBOPlayerStatsStore,
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
        KBOPlayerStatsListItem(
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
fun KBOPlayerStatsListItem(
    index: Int,
    data: KBOPlayerStats,
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
        KBOPlayerStatsItem(
            data = data,
            contentsAlpha = contentsAlpha,
            measureContentAlpha = measureContentAlpha
        )
    }
}

@Composable
fun KBOPlayerStatsItem(
    data: KBOPlayerStats,
    contentsAlpha: Float,
    measureContentAlpha: Float,
) {
    /* ---------------------
       ui
       --------------------- */
    CenterColumn(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        data.hitter?.let {
            CenterRow {
                BaseballLeagueTitle(
                    url = KBOUtil.kboLogoUrl,
                    leagueName = "KBO",
                    leagueSeason = data.season
                )
                Text(" - [타자]")
            }

            // stats
            CenterRow(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                FBStatDataItem(
                    category = "경기수",
                    data = it.g,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "타수",
                    data = it.ab,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "타율",
                    data = it.avg,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "안타",
                    data = it.h,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "2루타",
                    data = it.double,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "3루타",
                    data = it.triple,
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
                    category = "홈런",
                    data = it.hr,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "출루율",
                    data = it.obp,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "장타율",
                    data = it.slg,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "ops",
                    data = it.ops,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "득점",
                    data = it.r,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "타점",
                    data = it.rbi,
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
                    category = "득점권 타율",
                    data = it.risp,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "볼넷",
                    data = it.bb,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "삼진",
                    data = it.so,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "도루",
                    data = it.sb,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "도루 실패",
                    data = it.cs,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "도루 성공률",
                    data = it.sbPercent,
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
                    category = "멀티히트",
                    data = it.mh,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "사구",
                    data = it.hbp,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "희생번트",
                    data = it.sac,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "희생플라이",
                    data = it.sf,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "병살타",
                    data = it.gdp,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "대타 타율",
                    data = it.phBa,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        data.pitcher?.let {
            CenterRow {
                BaseballLeagueTitle(
                    url = KBOUtil.kboLogoUrl,
                    leagueName = "KBO",
                    leagueSeason = data.season
                )
                Text(" - [투수]")
            }

            // stats
            CenterRow(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                FBStatDataItem(
                    category = "경기수",
                    data = it.g,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "이닝",
                    data = it.ip,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "평균자책",
                    data = it.era,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "승",
                    data = it.w,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "패",
                    data = it.l,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "홀드",
                    data = it.hld,
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
                    category = "세이브",
                    data = it.sv,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "삼진",
                    data = it.so,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "볼넷",
                    data = it.bb,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "피안타",
                    data = it.h,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "피2루타",
                    data = it.double,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "피3루타",
                    data = it.triple,
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
                    category = "피홈런",
                    data = it.hr,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "피안타율",
                    data = it.avg,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "실점",
                    data = it.r,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "자책점",
                    data = it.er,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "블론세이브",
                    data = it.bsv,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "보크",
                    data = it.bk,
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
                    category = "고의4구",
                    data = it.ibb,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "폭투",
                    data = it.wp,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "완투",
                    data = it.cg,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "완봉",
                    data = it.sho,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "투구수",
                    data = it.np,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "퀄리티 스타트",
                    data = it.qs,
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
                    data = it.whip,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "승률",
                    data = it.wpct,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "경기당 평균 투구수",
                    data = it.npsPG.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider(modifier = Modifier.alpha(0f))
                EmptyStatDataItem(modifier = Modifier.weight(1f))
                StatsDivider(modifier = Modifier.alpha(0f))
                EmptyStatDataItem(modifier = Modifier.weight(1f))
                StatsDivider(modifier = Modifier.alpha(0f))
                EmptyStatDataItem(modifier = Modifier.weight(1f))
            }
        }
    }
}