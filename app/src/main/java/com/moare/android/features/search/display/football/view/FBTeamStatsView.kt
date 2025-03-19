package com.moare.android.features.search.display.football.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
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
import com.moare.android.ui.common.components.HCapsuleBarSize
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun FBTeamStatsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbTeamStatsViewModel: FBTeamStatsViewModel = hiltViewModel(),
    data: FBTeamStatsDisplayModel,
    center: State<Offset>
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
    var parentOffset by remember { mutableStateOf(Offset.Zero) }
    val itemPositions = remember { mutableStateMapOf<Int, Offset>() }
    var aniPositions by remember { mutableStateOf(false) }
    var aniShowContents by remember { mutableStateOf(false) }
    val aniContentsAlpha by animateFloatAsState(
        targetValue = if (aniShowContents) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )
    var showContents by remember { mutableStateOf(false) }

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

            aniShowContents = true

            delay(500)

            showContents = true
        }
    }

    /* ---------------------
       ui
       - invisible first
       - set ani ui's position
       - visible after ani ui
       --------------------- */
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .onGloballyPositioned { parentCoordinates ->
                val parentPosition = parentCoordinates.positionInWindow()
                parentOffset = parentPosition
            }
            .alpha(if (showContents) 1f else 0f)
    ) {
        // team info
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .onGloballyPositioned { layoutCoordinates ->
                    val itemSize = layoutCoordinates.size
                    val position = layoutCoordinates.positionInWindow()

                    val relativeX = position.x - parentOffset.x
                    val relativeY = position.y - parentOffset.y

                    // Calculate the center of the InfoItem
                    val centerX = relativeX + itemSize.width / 2f
                    val centerY = relativeY + itemSize.height / 2f

                    itemPositions[0] = Offset(centerX - center.value.x, centerY - center.value.y)
                }
        ) {
            FBTeamStatsTeamInfoItem()
        }

        // team stats list
        FBTeamStatsList(
            addItemPosition = { index, layoutCoordinates ->
                val itemSize = layoutCoordinates.size
                val position = layoutCoordinates.positionInWindow()

                val relativeX = position.x - parentOffset.x
                val relativeY = position.y - parentOffset.y

                // Calculate the center of the InfoItem
                val centerX = relativeX + itemSize.width / 2f
                val centerY = relativeY + itemSize.height / 2f

                itemPositions[index] = Offset(centerX - center.value.x, centerY - center.value.y)
            }
        )
    }

    /* ---------------------
       animation ui
       - invisible after ani
       --------------------- */
    val firstPosition = itemPositions[0] ?: Offset.Zero
    val firstAnimatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) firstPosition else Offset.Zero,
        animationSpec = tween(1000),
    )

    if (!showContents) {
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
            FBTeamStatsTeamInfoItem(aniContentsAlpha = aniContentsAlpha)
        }

        FBTeamStatsAniList(
            itemPositions = itemPositions,
            aniPositions = aniPositions,
            aniContentsAlpha = aniContentsAlpha
        )
    }
}

@Composable
fun FBTeamStatsTeamInfoItem(
    fbTeamStatsViewModel: FBTeamStatsViewModel = hiltViewModel(),
    aniContentsAlpha: Float = 1f
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
                .alpha(aniContentsAlpha)
        ) {
            URLImage(url = team.logo)

            // name
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = EnNameTranslationUtils.translateByDic(TranslationType.TEAM,false, team.name),
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
                        text = venue.name,
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
    addItemPosition: (Int, LayoutCoordinates) -> Unit
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
                addItemPosition = addItemPosition
            )
        }
    }
}

@Composable
fun FBTeamStatsListItem(
    fbTeamStatsViewModel: FBTeamStatsViewModel = hiltViewModel(),
    index: Int,
    data: FBTeamStats,
    addItemPosition: (Int, LayoutCoordinates) -> Unit
) {
    Column(
//        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 12.dp)
            .onGloballyPositioned { layoutCoordinates ->
                addItemPosition(index + 1, layoutCoordinates)
            }
    ) {
        FBTeamStatsItem(data)
    }
}

@Composable
fun FBTeamStatsAniList(
    fbTeamStatsViewModel: FBTeamStatsViewModel = hiltViewModel(),
    itemPositions: Map<Int, Offset>,
    aniPositions: Boolean,
    aniContentsAlpha: Float
) {
    val displayModel by fbTeamStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val statsList = it.stats

        /* ---------------------
           ui
           --------------------- */
        for ((index, value) in statsList.withIndex()) {
            FBTeamStatsAniListItem(
                index = index,
                data = value,
                itemPositions = itemPositions,
                aniPositions = aniPositions,
                aniContentsAlpha = aniContentsAlpha
            )
        }
    }

}

@Composable
fun FBTeamStatsAniListItem(
    index: Int,
    data: FBTeamStats,
    itemPositions: Map<Int, Offset>,
    aniPositions: Boolean,
    aniContentsAlpha: Float
) {
    // add 1 to index due to the first item
    val position = itemPositions[index + 1] ?: Offset.Zero
    val animatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) position else Offset.Zero,
        animationSpec = tween(1000),
    )

    /* ---------------------
       ui
       --------------------- */
    Column(
//        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset {
                IntOffset(
                    animatedPosition.x.roundToInt(),
                    animatedPosition.y.roundToInt()
                )
            }
    ) {
        FBTeamStatsItem(data, aniContentsAlpha)
    }
}

@Composable
fun FBTeamStatsItem(
    stats: FBTeamStats,
    aniContentsAlpha: Float = 1f
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
            .alpha(aniContentsAlpha)
    ) {
        LeagueTitle(
            url = stats.league.logo,
            leagueName = stats.league.name,
            leagueSeason = stats.league.season
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
        modifier = Modifier.alpha(aniContentsAlpha)
    ) {
        FBStatDataItem(
            category = "경기수",
            data = stats.fixtures.played.total.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "승",
            data = stats.fixtures.wins.total.toString(),
            customWidth = 80.dp
        )
        FBStatDataItem(
            category = "무",
            data = stats.fixtures.draws.total.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "패",
            data = stats.fixtures.loses.total.toString(),
            customWidth = 80.dp
        )
//            FBTeamStatsDataItem(
//                category = "득점",
//                data = data
//            )
    }

    Row(
        modifier = Modifier.alpha(aniContentsAlpha)
    ) {
        FBStatDataItem(
            category = "득점",
            data = stats.goals.teamGoalsFor.total.total.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "경기당 평균득점",
            data = stats.goals.teamGoalsFor.average.total,
            customFontSize = 11,
            customWidth = 80.dp
        )
        FBStatDataItem(
            category = "실점",
            data = stats.goals.teamGoalsAgainst.total.total.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "경기당 평균실점",
            data = stats.goals.teamGoalsAgainst.average.total,
            customFontSize = 11,
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
            .alpha(aniContentsAlpha)
    ) {
        FBStatDataItem(
            category = "득실차",
            data = ((stats.goals.teamGoalsFor.total.total) - (stats.goals.teamGoalsAgainst.total.total)).toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "클린시트",
            data = stats.cleanSheet.total.toString(),
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


















