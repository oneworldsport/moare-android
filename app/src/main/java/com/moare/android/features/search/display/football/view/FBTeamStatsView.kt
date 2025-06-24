package com.moare.android.features.search.display.football.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.football.viewmodel.FBTeamStatsIntent
import com.moare.android.features.search.display.football.viewmodel.FBTeamStatsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStatsDisplayModel
import com.moare.android.features.search.models.models.football.FBTeamStats
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.URLImage

@Composable
fun FBTeamStatsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbTeamStatsViewModel: FBTeamStatsViewModel = hiltViewModel(),
    data: FBTeamStatsDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by fbTeamStatsViewModel.displayModel.collectAsState()
    val statsList = displayModel?.stats

    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.FBTeamStats) {
            fbTeamStatsViewModel.send(FBTeamStatsIntent.InitData(data))
        }
    }

    InfoViewContainer(
        itemCount = (statsList?.size ?: 0) + 1,
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        measureContent = {
            FBTeamStatsTeamInfoItem { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            FBTeamStatsList { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        }, displayContent = {
            FBTeamStatsTeamInfoItem(
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            FBTeamStatsList(
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
fun FBTeamStatsTeamInfoItem(
    fbTeamStatsViewModel: FBTeamStatsViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by fbTeamStatsViewModel.displayModel.collectAsState()

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
                URLImage(url = team.logo)

                // name
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = fbTeamStatsViewModel.teamNameDictionary["full_${team.id}"] ?: team.name,
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
                            text = fbTeamStatsViewModel.teamNameDictionary["venue_${team.id}"] ?: venue.name,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// team stats list
@Composable
fun FBTeamStatsList(
    fbTeamStatsViewModel: FBTeamStatsViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSizes: Map<Int, DpSize>? = null,
    itemPositions: Map<Int, Offset>? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by fbTeamStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val statsList = it.stats

        /* ---------------------
           ui
           --------------------- */
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
                updateItemPosition = updateItemPosition
            )
        }
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
        FBTeamStatsItem(
            data = data,
            contentsAlpha = contentsAlpha
        )
    }
}

@Composable
fun FBTeamStatsItem(
    data: FBTeamStats,
    contentsAlpha: Float = 1f
) {
    /* ---------------------
       ui
       --------------------- */
    HCapsuleBar()

    // league
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .alpha(contentsAlpha)
    ) {
        LeagueTitle(
            url = data.league.logo,
            leagueName = data.league.name,
            leagueSeason = data.league.season
        )

//        Text(
//            text = " - ",
//            fontWeight = FontWeight.Medium
//        )
//
//        URLImage(
//            url = stats.team.logo,
//            customSize = 24.dp
//        )
//
//        Text(
//            text = EnNameTranslationUtils.translateByDic(TranslationType.TEAM, input = stats.team.name),
//            fontWeight = FontWeight.Medium,
//            modifier = Modifier.padding(start = 4.dp)
//        )
    }

    // stats
    Row(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "경기수",
            data = data.fixtures.played.total.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "승",
            data = data.fixtures.wins.total.toString(),
            customWidth = 80.dp
        )
        FBStatDataItem(
            category = "무",
            data = data.fixtures.draws.total.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "패",
            data = data.fixtures.loses.total.toString(),
            customWidth = 80.dp
        )
//            FBTeamStatsDataItem(
//                category = "득점",
//                data = data
//            )
    }

    Row(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "득점",
            data = data.goals.teamGoalsFor.total.total.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "경기당 평균득점",
            data = data.goals.teamGoalsFor.average.total,
            customCategoryFontSize = 11,
            customWidth = 80.dp
        )
        FBStatDataItem(
            category = "실점",
            data = data.goals.teamGoalsAgainst.total.total.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "경기당 평균실점",
            data = data.goals.teamGoalsAgainst.average.total,
            customCategoryFontSize = 11,
            customWidth = 80.dp
        )
//            FBTeamStatsDataItem(
//                category = "",
//                data = data
//            )
    }

    Row(
        modifier = Modifier
            .padding(bottom = 6.dp)
            .alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "득실차",
            data = ((data.goals.teamGoalsFor.total.total) - (data.goals.teamGoalsAgainst.total.total)).toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "클린시트",
            data = (data.cleanSheet?.total ?: 0).toString(),
            customWidth = 80.dp
        )
        FBStatDataItem(
            category = "홈성적",
            data = "",
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "원정성적",
            data = "",
            customWidth = 80.dp
        )
//            FBTeamStatsDataItem(
//                category = "",
//                data = data
//            )
    }
}


















