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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.util.EnNameTranslationUtils
import com.moare.android.core.util.TranslationType
import com.moare.android.features.search.display.components.FBStatDataItem
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStatsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStatsDisplayModel
import com.moare.android.features.search.models.models.football.FBPlayerStats
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.URLImage
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun FBPlayerStatsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbPlayerStatsViewModel: FBPlayerStatsViewModel = hiltViewModel(),
    data: FBPlayerStatsDisplayModel
) {
    /* ---------------------
       ui state
       --------------------- */

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by fbPlayerStatsViewModel.displayModel.collectAsState()

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
        if (poppedView == null || poppedView is SportDecodableModel.FBPlayerStats) {
            fbPlayerStatsViewModel.send(FBPlayerStatsViewModel.Intent.InitData(data))
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
       - invisible first
       - set ani ui's position
       - visible after ani ui
       --------------------- */
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
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
            // player info
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
                FBPlayerStatsPlayerInfoItem()
            }

            // stats list
            FBPlayerStatsList(
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
           animation ui
           - invisible after ani
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
            FBPlayerStatsPlayerInfoItem(contentsAlpha = contentsAlpha)
        }

        FBPlayerStatsList(
            itemPositions = itemPositions,
            aniPositions = aniPositions,
            contentsAlpha = contentsAlpha
        )
    }
}

@Composable
fun FBPlayerStatsPlayerInfoItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbPlayerStatsViewModel: FBPlayerStatsViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f,
) {
    val displayModel by fbPlayerStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.player
        val team = it.team

        var nationalityKrName by remember { mutableStateOf("") }

        LaunchedEffect(displayModel) {
            nationalityKrName = EnNameTranslationUtils.translateByDic(TranslationType.COUNTRY, input = player.nationality)
        }

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
            URLImage(url = player.photo)

            // name
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = fbPlayerStatsViewModel.playerNameDictionary[player.name.lowercase()] ?: player.name,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = player.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 2
                )
            }

            // nationality / team
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row { // TODO: 가운데 정렬
                    // TODO: 나라 국기..?
                    Text(
                        text = "국적: ",
                        fontSize = 15.sp
                    )

                    Text(
                        text = nationalityKrName,
                        fontWeight = FontWeight.Medium
                    )
                }

                team?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "소속팀: ",
                            fontSize = 15.sp
                        )

                        URLImage(
                            url = team.logo,
                            customSize = 24.dp
                        )

                        Text(
                            text = fbPlayerStatsViewModel.teamNameDictionary["full_${team.id}"] ?: team.name,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FBPlayerStatsList(
    fbPlayerStatsViewModel: FBPlayerStatsViewModel = hiltViewModel(),
    isAniList: Boolean = false,
    itemPositions: Map<Int, Offset>? = null,
    aniPositions: Boolean = true,
    contentsAlpha: Float = 1f,
    addItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by fbPlayerStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val statsList = it.stats

        /* ---------------------
           ui
           --------------------- */
        for ((index, value) in statsList.withIndex()) {
            FBPlayerStatsListItem(
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
fun FBPlayerStatsListItem(
    index: Int,
    data: FBPlayerStats,
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
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = if (isAniList) 0.dp else 12.dp)
            .padding(horizontal = 4.dp)
            .fillMaxWidth()
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
        FBPlayerStatsItem(
            data = data,
            contentsAlpha = contentsAlpha
        )
    }
}

@Composable
fun FBPlayerStatsItem(
    fbPlayerStatsViewModel: FBPlayerStatsViewModel = hiltViewModel(),
    data: FBPlayerStats,
    contentsAlpha: Float = 1f
) {
    /* ---------------------
       ui
       --------------------- */
    HCapsuleBar()

    // league / team
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = 4.dp)
            .alpha(contentsAlpha)
    ) {
        LeagueTitle(
            url = data.league.logo,
            leagueName = data.league.name,
            leagueSeason = data.league.season
        )

        Text(
            text = " - ",
            fontWeight = FontWeight.Medium
        )

        URLImage(
            url = data.team.logo,
            customSize = 24.dp
        )

        Text(
            text = fbPlayerStatsViewModel.teamNameDictionary["short_${data.team.id}"] ?: data.team.name,
            fontWeight = FontWeight.Medium
        )
    }

    // stats
    // TODO: 수비수, 골기퍼, 공격수 별로 데이터 노출 다르게
    Row(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "출전 경기수",
            data = data.games.appearences.toString(),
            customCategoryFontSize = 14,
            customWidth = 70.dp
        )
//            FBPlayerStatsDataItem(
//                category = "선발 출전",
//                data =
//            )
        FBStatDataItem(
            category = "평균 평점",
            data = data.games.rating.take(3),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "골",
            data = data.goals.total.toString()
        )
        FBStatDataItem(
            category = "패널티 골",
            data = data.penalty.scored.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "도움",
            data = data.goals.assists.toString(),
            customWidth = 70.dp
        )
    }

    Row(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "슈팅",
            data = data.shots.total.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "유효 슈팅",
            data = data.shots.on.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "패스",
            data = data.passes.total.toString()
        )
        FBStatDataItem(
            category = "태클",
            data = data.tackles.total.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "드리블",
            data = data.dribbles.attempts.toString(),
            customWidth = 70.dp
        )
    }

    Row(
        modifier = Modifier
            .padding(bottom = 6.dp)
            .alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "파울",
            data = data.fouls.committed.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "경고",
            data = data.cards.yellow.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "퇴장",
            data = data.cards.red.toString()
        )
        FBStatDataItem(
            category = "",
            data = "",
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "",
            data = "",
            customWidth = 70.dp
        )
    }
}