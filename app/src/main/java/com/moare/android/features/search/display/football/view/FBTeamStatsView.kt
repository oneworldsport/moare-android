package com.moare.android.features.search.display.football.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.util.EnNameTranslationUtils
import com.moare.android.core.util.TranslationType
import com.moare.android.features.search.display.components.FBStatDataItem
import com.moare.android.features.search.display.football.viewmodel.FBTeamStatsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStatsDisplayModel
import com.moare.android.features.search.models.models.football.FBTeamStats
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.URLImage
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

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
       animation
       --------------------- */
    var parentPosition by remember { mutableStateOf(Offset.Zero) }
    var parentCenter by remember { mutableStateOf(Offset.Zero) }
    val itemPositions = remember { mutableStateMapOf<Int, Offset>() }
    var aniPositions by remember { mutableStateOf(false) }
    var showContents by remember { mutableStateOf(false) }
    val contentsAlpha by animateFloatAsState(
        targetValue = if (showContents) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.FBTeamStats) {
            fbTeamStatsViewModel.send(FBTeamStatsViewModel.Intent.InitData(data))
        }
    }

    LaunchedEffect(itemPositions) {
        if (itemPositions.size == (statsList?.size ?: 0) + 1) {
            aniPositions = true
            delay(1000)
            showContents = true
        }
    }

    /* ---------------------
       ui
       --------------------- */
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        /* ---------------------
           invisible ui
           - for position
           --------------------- */
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { parentCoordinates ->
                    parentPosition = parentCoordinates.positionInWindow()
                    parentCenter = Offset(
                        x = parentCoordinates.size.width / 2f,
                        y = parentCoordinates.size.height / 2f
                    )
                }
                .alpha(0f)
        ) {
            // team info
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        val itemSize = layoutCoordinates.size
                        val position = layoutCoordinates.positionInWindow()
                        val relativeY = position.y - parentPosition.y
                        val centerY = relativeY + itemSize.height / 2f

                        itemPositions[0] = Offset(0f, centerY - parentCenter.y)
                    }
            ) {
                FBTeamStatsTeamInfoItem()
            }

            // team stats list
            FBTeamStatsList(
                addItemPosition = { index, layoutCoordinates ->
                    val itemSize = layoutCoordinates.size
                    val position = layoutCoordinates.positionInWindow()
                    val relativeY = position.y - parentPosition.y
                    val centerY = relativeY + itemSize.height / 2f

                    itemPositions[index] = Offset(0f, centerY - parentCenter.y)
                }
            )
        }

        /* ---------------------
           visible ui
           - with animation effect
           --------------------- */
        val firstPosition = itemPositions[0] ?: Offset.Zero
        val firstAnimatedPosition by animateOffsetAsState(
            targetValue = if (aniPositions) firstPosition else Offset.Zero,
            animationSpec = tween(1000),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .offset {
                    IntOffset(
                        firstAnimatedPosition.x.roundToInt(),
                        firstAnimatedPosition.y.roundToInt()
                    )
                }
        ) {
            FBTeamStatsTeamInfoItem(contentsAlpha = contentsAlpha)
        }

        FBTeamStatsList(
            isAniList = true,
            itemPositions = itemPositions,
            aniPositions = aniPositions,
            contentsAlpha = contentsAlpha
        )
    }
}

@Composable
fun FBTeamStatsTeamInfoItem(
    fbTeamStatsViewModel: FBTeamStatsViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by fbTeamStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val team = it.team
        val venue = it.venue

        /* ---------------------
           ui
           --------------------- */
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

@Composable
fun FBTeamStatsList(
    fbTeamStatsViewModel: FBTeamStatsViewModel = hiltViewModel(),
    isAniList: Boolean = false,
    itemPositions: Map<Int, Offset>? = null,
    aniPositions: Boolean = true,
    contentsAlpha: Float = 1f,
    addItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
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
                isAniList = isAniList,
                itemPositions = itemPositions,
                aniPositions = aniPositions,
                contentsAlpha = contentsAlpha,
                addItemPosition = addItemPosition
            )
        }
    }
}

@Composable
fun FBTeamStatsListItem(
    index: Int,
    data: FBTeamStats,
    isAniList: Boolean,
    itemPositions: Map<Int, Offset>?,
    aniPositions: Boolean,
    contentsAlpha: Float,
    addItemPosition: ((Int, LayoutCoordinates) -> Unit)?
) {
    val position = if (itemPositions != null) {
        itemPositions[index + 1] ?: Offset.Zero
    } else {
        Offset.Zero
    }
    val animatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) position else Offset.Zero,
        animationSpec = tween(1000),
    )

    Column(
//        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = if (isAniList) 0.dp else 12.dp)
            .padding(horizontal = 4.dp)
            .onGloballyPositioned { layoutCoordinates ->
                if (!isAniList && addItemPosition != null) {
                    addItemPosition(index + 1, layoutCoordinates)
                }
            }
            .offset {
                IntOffset(
                    animatedPosition.x.roundToInt(),
                    animatedPosition.y.roundToInt()
                )
            }
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
            data = data.cleanSheet.total.toString(),
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


















