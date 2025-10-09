package com.moare.android.features.search.display.kbo.view

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
import com.moare.android.core.util.KBOUtil
import com.moare.android.features.search.display.common.components.EmptyStatDataItem
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStatsIntent
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStatsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStatsDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOTeamStats
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.HDivider
import com.moare.android.ui.common.components.StatsDivider
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow

@Composable
fun KBOTeamStatsView(
    searchViewModel: SearchViewModel,
    kboTeamStatsViewModel: KBOTeamStatsViewModel = hiltViewModel(),
    data: KBOTeamStatsDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by kboTeamStatsViewModel.displayModel.collectAsState()
    val statsList = displayModel?.stats

    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.KBOTeamStats) {
            kboTeamStatsViewModel.send(KBOTeamStatsIntent.InitData(data))
        }
    }

    InfoViewContainer(
        searchViewModel = searchViewModel,
        itemCount = (statsList?.size ?: 0) + 1,
        shouldShowMeasureContent = true,
        modifier = Modifier,
//            .verticalScroll(rememberScrollState()),
        measureContent = {
            KBOTeamStatsTeamInfoItem { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            KBOTeamStatsList { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        }, displayContent = {
            KBOTeamStatsTeamInfoItem(
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha,
                measureContentAlpha = measureContentAlpha
            )

            KBOTeamStatsList(
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
fun KBOTeamStatsTeamInfoItem(
    kboTeamStatsViewModel: KBOTeamStatsViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 1f,
    containerModifier: Modifier = Modifier,
    measureContentAlpha: Float = 0f,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by kboTeamStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val team = it.team
        val venue = it.venue

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
                URLImage(url = KBOUtil.teamLogoUrl(team.id))

                // name, city
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Text(
                        text = kboTeamStatsViewModel.teamNameDictionary["full_${team.id}"] ?: team.teamName,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "연고지: ",
                            fontSize = 15.sp
                        )

                        Text(
                            text = team.city,
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
                            text = kboTeamStatsViewModel.teamNameDictionary["venue_${team.id}"] ?: venue.name,
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
fun KBOTeamStatsList(
    kboTeamStatsViewModel: KBOTeamStatsViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSizes: Map<Int, DpSize>? = null,
    itemPositions: Map<Int, Offset>? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 1f,
    containerModifier: Modifier = Modifier,
    measureContentAlpha: Float = 0f,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by kboTeamStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val statsList = it.stats

        /* ---------------------
           ui
           --------------------- */
        for ((index, value) in statsList.withIndex()) {
            KBOTeamStatsListItem(
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
}

@Composable
fun KBOTeamStatsListItem(
    index: Int,
    data: KBOTeamStats,
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
        KBOTeamStatsItem(
            data = data,
            contentsAlpha = contentsAlpha,
            measureContentAlpha = measureContentAlpha
        )
    }
}

@Composable
fun KBOTeamStatsItem(
    kboTeamStatsViewModel: KBOTeamStatsViewModel = hiltViewModel(),
    data: KBOTeamStats,
    contentsAlpha: Float,
    measureContentAlpha: Float,
) {
    var basicStatsOpenState by remember { mutableStateOf(true) }
    var hitterStatsOpenState by remember { mutableStateOf(false) }
    var pitcherStatsOpenState by remember { mutableStateOf(false) }

    val displayModel by kboTeamStatsViewModel.displayModel.collectAsState()
    val rank = data.rankData
    val hitter = data.hitterData
    val pitcher = data.pitcherData
    val defense = data.defenseData
    val runner = data.runnerData

    /* ---------------------
       ui
       --------------------- */
    CenterColumn(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        // league
        BaseballLeagueTitle(
            url = KBOUtil.kboLogoUrl,
            leagueName = "KBO",
            leagueSeason = data.season
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
                    category = "순위",
                    data = rank.rank,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "승",
                    data = rank.wins,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "무",
                    data = rank.draws,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "패",
                    data = rank.losses,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "경기수",
                    data = rank.gp,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "게임차",
                    data = rank.gb,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "승률",
                    data = rank.winpct,
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
                        data = hitter.avg,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "안타",
                        data = hitter.h,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "홈런",
                        data = hitter.hr,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "출루율",
                        data = hitter.obp,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "장탸율",
                        data = hitter.slg,
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
                        data = hitter.ops,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "득점권타율",
                        data = hitter.risp,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "득점",
                        data = hitter.r,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "타점",
                        data = hitter.rbi,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "사구",
                        data = hitter.hbp,
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
                        data = hitter.gdp,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "삼진",
                        data = hitter.so,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "도루성공",
                        data = runner.sb,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "도루실패",
                        data = runner.cs,
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
                        data = pitcher.era,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "피안타율",
                        data = pitcher.avg,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "피안타",
                        data = pitcher.h,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "피홈런",
                        data = pitcher.hr,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "세이브",
                        data = pitcher.sv,
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
                        data = pitcher.whip,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "볼넷",
                        data = pitcher.bb,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "보크",
                        data = pitcher.bk,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "홀드",
                        data = pitcher.hld,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "블론세이브",
                        data = pitcher.bsv,
                        customCategoryFontSize = 11,
                        customCategoryHeight = 30.dp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}