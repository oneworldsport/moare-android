package com.moare.android.features.search.display.kbo.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.util.KBOUtil
import com.moare.android.core.util.NBAUtil
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStatsIntent
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStatsViewModel
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStatsIntent
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStatsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStatsDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOTeamStats
import com.moare.android.features.search.models.models.nba.NBATeamStats
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.URLImage

@Composable
fun KBOTeamStatsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
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
        itemCount = (statsList?.size ?: 0) + 1,
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
                contentsAlpha = contentsAlpha
            )

            KBOTeamStatsList(
                isAniItem = true,
                itemSizes = itemSizes,
                itemPositions = itemPositions,
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
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
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
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
                URLImage(url = KBOUtil.teamLogoUrl(team.id))

                // name, city
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
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
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
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
            .padding(horizontal = if (isAniItem) 0.dp else 4.dp)
            .fillMaxWidth()
    ) {
        KBOTeamStatsItem(
            data = data,
            contentsAlpha = contentsAlpha
        )
    }
}

@Composable
fun KBOTeamStatsItem(
    kboTeamStatsViewModel: KBOTeamStatsViewModel = hiltViewModel(),
    data: KBOTeamStats,
    contentsAlpha: Float
) {
    val displayModel by kboTeamStatsViewModel.displayModel.collectAsState()
    val rank = data.rankData
    val hitting = data.hitterData
    val pitching = data.pitcherData
    val defense = data.defenseData
    val running = data.runnerData

    /* ---------------------
       ui
       --------------------- */
    HCapsuleBar()

    // league
    BaseballLeagueTitle(
        url = KBOUtil.kboLogoUrl,
        leagueName = "KBO",
        leagueSeason = data.season,
        modifier = Modifier.alpha(contentsAlpha)
    )

    // stats
    Row(
        modifier = Modifier
            .padding(top = 4.dp)
            .alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "순위",
            data = rank.rank,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "승",
            data = rank.wins,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "패",
            data = rank.losses,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "경기수",
            data = rank.gp,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "게임차",
            data = rank.gb,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "승률",
            data = rank.winpct,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "타율",
            data = hitting.avg,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "안타",
            data = hitting.h,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "홈런",
            data = hitting.hr,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "출루율",
            data = hitting.obp,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "장탸율",
            data = hitting.slg,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "ops",
            data = hitting.ops,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "득점권타율",
            data = hitting.risp,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "사구-[타자]",
            data = hitting.hbp,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "병살타",
            data = hitting.gdp,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "득점",
            data = hitting.r,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "타점",
            data = hitting.rbi,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "삼진-[타자]",
            data = hitting.so,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "피안타율",
            data = pitching.avg,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "피안타",
            data = pitching.h,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "피홈런",
            data = pitching.hr,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "평균자책점",
            data = pitching.era,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "세이브",
            data = pitching.sv,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "블론세이브",
            data = pitching.bsv,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.alpha(contentsAlpha)
    ) {

        FBStatDataItem(
            category = "볼넷-[투수]",
            data = pitching.bb,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "보크",
            data = pitching.bk,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "홀드",
            data = pitching.hld,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "이닝당 출루허용률",
            data = pitching.whip,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "도루성공",
            data = running.sb,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
        FBStatDataItem(
            category = "도루실패",
            data = running.cs,
            customCategoryFontSize = 11,
            customCategoryHeight = 30.dp,
            modifier = Modifier.weight(1f)
        )
    }
}