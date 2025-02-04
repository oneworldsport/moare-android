package com.moare.android.features.search.display.football.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStandingsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplay
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplayModel
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.HCapsuleBarSize
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar

@Composable
fun FBPlayerStandingsView(
    fbPlayerStandingsViewModel: FBPlayerStandingsViewModel = hiltViewModel(),
    data: FBPlayerStandingsDisplayModel
) {
    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by fbPlayerStandingsViewModel.displayModel.collectAsState()
    val firstSelectedIndex by fbPlayerStandingsViewModel.firstSelectedIndex.collectAsState()
    val secondSelectedIndex by fbPlayerStandingsViewModel.secondSelectedIndex.collectAsState()
    val isKeyword by fbPlayerStandingsViewModel.isKeyword.collectAsState()

    val league = displayModel?.standings?.first()?.stats?.league

    /* ---------------------
       etc
       --------------------- */
    val secondSelectedCategoryPosition = with(LocalDensity.current) {
        if (secondSelectedIndex in 0 until fbPlayerStandingsViewModel.attackCategoryList.size) {
            (fbPlayerStandingsViewModel.itemWidth * secondSelectedIndex).toPx()
        } else if (secondSelectedIndex in fbPlayerStandingsViewModel.attackCategoryList.size until fbPlayerStandingsViewModel.attackCategoryList.size + fbPlayerStandingsViewModel.defendCategoryList.size) {
            ((fbPlayerStandingsViewModel.itemWidth * secondSelectedIndex) + fbPlayerStandingsViewModel.barWidth).toPx()
        } else {
            ((fbPlayerStandingsViewModel.itemWidth * secondSelectedIndex) + (fbPlayerStandingsViewModel.barWidth * 2)).toPx()
        }
    }.toInt()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        fbPlayerStandingsViewModel.initData(data)
    }

    // scroll to category that matches with the keyword,
    // and when first category list's item is selected by click
    LaunchedEffect(isKeyword, firstSelectedIndex) {
        if (fbPlayerStandingsViewModel.shouldScrollCategory) {
            horizontalScrollState.animateScrollTo(
                value = secondSelectedCategoryPosition,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearOutSlowInEasing
                )
            )
        }
    }

    /* ---------------------
       ui
       --------------------- */
    Column(
        // if set fillMaxSize, AnimatedVisibility works fine on first show.
        // But if fillMaxSize not set, AnimatedVisibility doesn't work on first show.
        // not sure why yet
        modifier = Modifier.fillMaxSize()
    ) {
        league?.let {
            LeagueTitle(
                url = league.logo,
                leagueName = league.name,
                leagueSeason = league.season
            )
        }

        Box(
            Modifier.padding(top = 6.dp)
        ) {
            // category
            Row {
                FBPlayerStandingsFirstCategoryItem()

                Row(
                    Modifier.horizontalScroll(horizontalScrollState)
                ) {
                    Column {
                        FBPlayerStandingsFirstCategoryList()
                        FBPlayerStandingsSecondCategoryList()
                    }
                }
            }

            // standings data
            Column(
                modifier = Modifier
                    .padding(top = fbPlayerStandingsViewModel.categoryItemHeight * 2)
                    .verticalScroll(rememberScrollState())
            ) {
                Row {
                    FBPlayerStandingsFirstDataList()

                    Row(
                        Modifier.horizontalScroll(horizontalScrollState)
                    ) {
                        FBPlayerStandingsDataList()
                    }
                }
            }
        }
    }
}

@Composable
fun FBPlayerStandingsFirstCategoryItem(
    fbPlayerStandingsViewModel: FBPlayerStandingsViewModel = hiltViewModel()
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(fbPlayerStandingsViewModel.categoryItemHeight * 2)
    ) {
        Text(
            text = fbPlayerStandingsViewModel.firstCategory,
            fontSize = fbPlayerStandingsViewModel.categoryFontSize,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(130.dp)
        )

        VCapsuleBar(modifier = Modifier.alpha(0.5f))
    }
}

@Composable
fun FBPlayerStandingsFirstCategoryList(
    fbPlayerStandingsViewModel: FBPlayerStandingsViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val selectedIndex by fbPlayerStandingsViewModel.firstSelectedIndex.collectAsState()

    val itemWidth = fbPlayerStandingsViewModel.itemWidth
    val barWidth = fbPlayerStandingsViewModel.barWidth

    /* ---------------------
       animation
       --------------------- */
    val barOffset by animateDpAsState(
        targetValue = if (selectedIndex == 0) {
            getOffsetOfAniCapsuleBar(
                itemWidth = itemWidth * 5,
                barWidth = 80.dp
            )
        } else if (selectedIndex == 1) {
            (itemWidth * 5) + barWidth + getOffsetOfAniCapsuleBar(
                itemWidth = itemWidth * 2,
                barWidth = 80.dp
            )
        } else {
            (itemWidth * 5) + (barWidth * 2) + (itemWidth * 2) + getOffsetOfAniCapsuleBar(
                itemWidth = itemWidth * 4,
                barWidth = 80.dp
            )
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(fbPlayerStandingsViewModel.categoryItemHeight - 2.dp)
        ) {
            for ((index, value) in fbPlayerStandingsViewModel.firstCategoryList.withIndex()) {
                FBPlayerStandingsFirstCategoryListItem(
                    category = value,
                    index = index
                )

                if (index != fbPlayerStandingsViewModel.firstCategoryList.size - 1) {
                    VCapsuleBar(modifier = Modifier.alpha(0.5f))
                }
            }
        }

        HCapsuleBar(
            modifier = Modifier
                .offset(x = barOffset),
            size = HCapsuleBarSize.LARGE
        )
    }
}

@Composable
fun FBPlayerStandingsFirstCategoryListItem(
    fbPlayerStandingsViewModel: FBPlayerStandingsViewModel = hiltViewModel(),
    category: String,
    index: Int
) {
    val itemWidth = fbPlayerStandingsViewModel.itemWidth

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(if (index == 0) (itemWidth * 5) else if (index == 1) (itemWidth * 2) else (itemWidth * 4))
            .clickable {
                fbPlayerStandingsViewModel.send(
                    FBPlayerStandingsViewModel.Intent.SelectFirstCategory(
                        index
                    )
                )
            }
    ) {
        Text(
            text = category,
            fontSize = fbPlayerStandingsViewModel.categoryFontSize,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FBPlayerStandingsSecondCategoryList(
    fbPlayerStandingsViewModel: FBPlayerStandingsViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val selectedIndex by fbPlayerStandingsViewModel.secondSelectedIndex.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val barOffset by animateDpAsState(
        targetValue = if (selectedIndex in 0 until fbPlayerStandingsViewModel.attackCategoryList.size) {
            getOffsetOfAniCapsuleBar(itemWidth = fbPlayerStandingsViewModel.itemWidth, index = selectedIndex)
        } else if (selectedIndex in fbPlayerStandingsViewModel.attackCategoryList.size until fbPlayerStandingsViewModel.attackCategoryList.size + fbPlayerStandingsViewModel.defendCategoryList.size) {
            getOffsetOfAniCapsuleBar(itemWidth = fbPlayerStandingsViewModel.itemWidth, index = selectedIndex) + fbPlayerStandingsViewModel.barWidth
        } else {
            getOffsetOfAniCapsuleBar(itemWidth = fbPlayerStandingsViewModel.itemWidth, index = selectedIndex) + (fbPlayerStandingsViewModel.barWidth * 2)
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(fbPlayerStandingsViewModel.categoryItemHeight - 2.dp)
        ) {
            for ((index, value) in fbPlayerStandingsViewModel.secondCategoryList.withIndex()) {
                FBPlayerStandingsSecondCategoryListItem(
                    category = value,
                    index = index
                )

                if (index == fbPlayerStandingsViewModel.attackCategoryList.size - 1 || index == (fbPlayerStandingsViewModel.attackCategoryList.size + fbPlayerStandingsViewModel.defendCategoryList.size - 1)) {
                    VCapsuleBar(modifier = Modifier.alpha(0.5f))
                }
            }
        }

        HCapsuleBar(
            modifier = Modifier
                .offset(x = barOffset)
        )
    }
}

@Composable
fun FBPlayerStandingsSecondCategoryListItem(
    fbPlayerStandingsViewModel: FBPlayerStandingsViewModel = hiltViewModel(),
    category: String,
    index: Int
) {
    Text(
        text = category,
        textAlign = TextAlign.Center,
        fontSize = fbPlayerStandingsViewModel.categoryFontSize,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .width(fbPlayerStandingsViewModel.itemWidth)
            .clickable {
                fbPlayerStandingsViewModel.send(
                    FBPlayerStandingsViewModel.Intent.SelectSecondCategory(index)
                )
            }
    )
}

@Composable
fun FBPlayerStandingsFirstDataList(
    fbPlayerStandingsViewModel: FBPlayerStandingsViewModel = hiltViewModel()
) {
    val standings by fbPlayerStandingsViewModel.standings.collectAsState()
//    VSequentialListAni(
//        items = dataList
//    ) { index, item ->
//        FBTeamStandingsFirstDataItem(rank = index + 1, data = item, itemHeight = itemHeight)
//    }
    Column {
        for ((index, value) in standings.withIndex()) {
            FBPlayerStandingsFirstDataListItem(rank = index + 1, data = value)
        }
    }
}

@Composable
fun FBPlayerStandingsFirstDataListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbPlayerStandingsViewModel: FBPlayerStandingsViewModel = hiltViewModel(),
    rank: Int,
    data: FBPlayerStandingsDisplay
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(fbPlayerStandingsViewModel.firstCategoryItemWidth)
            .padding(start = 10.dp)
            .height(fbPlayerStandingsViewModel.dataItemHeight)
            .clickable {
                searchViewModel.send(SearchViewModel.Intent.ShowPlayerStats(from = "standings", playerId = data.player.id))
            }
    ) {
        Text(
            text = "$rank",
            fontWeight = FontWeight.Medium,
            fontSize = fbPlayerStandingsViewModel.dataFontSize,
            modifier = Modifier
                .width(22.dp)
        )

        URLImage(
            url = data.player.photo,
            customSize = 25.dp,
            modifier = Modifier.padding(end = 4.dp)
        )

        Text(
            text = data.player.krname,
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
        )

        VCapsuleBar(modifier = Modifier.alpha(0.5f))
    }
}

@Composable
fun FBPlayerStandingsDataList(
    fbPlayerStandingsViewModel: FBPlayerStandingsViewModel = hiltViewModel()
) {
    val standings by fbPlayerStandingsViewModel.standings.collectAsState()
//    VSequentialListAni(
//        items = dataList
//    ) { _, item ->
//        Row {
//            for (index in 0 until 10) {
//                FBTeamStandingsDataItem(
//                    data = item,
//                    isInt = !(index == 8 || index == 9),
//                    index = index,
//                    itemHeight = itemHeight
//                )
//            }
//        }
//    }
    Column {
        for (value in standings) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(fbPlayerStandingsViewModel.dataItemHeight)
            ) {
                for (index in 0 until fbPlayerStandingsViewModel.secondCategoryList.size) {
                    FBPlayerStandingsDataListItem(
                        data = value,
                        index = index
                    )

                    if (index == fbPlayerStandingsViewModel.attackCategoryList.size - 1 || index == (fbPlayerStandingsViewModel.attackCategoryList.size + fbPlayerStandingsViewModel.defendCategoryList.size - 1)) {
                        VCapsuleBar(modifier = Modifier.alpha(0f))
                    }
                }
            }
        }
    }
}

@Composable
fun FBPlayerStandingsDataListItem(
    fbPlayerStandingsViewModel: FBPlayerStandingsViewModel = hiltViewModel(),
    data: FBPlayerStandingsDisplay,
    index: Int
) {
    val intDataText = when (index) {
        0 -> "${data.stats.goals.total}"
        1 -> "${data.stats.goals.assists}"
        2 -> "${(data.stats.goals.total) + (data.stats.goals.assists)}"
        3 -> "${data.stats.shots.total}"
        4 -> "${data.stats.shots.on}"
        5 -> "${data.stats.tackles.total}"
        6 -> "${data.stats.passes.total}"
        7 -> "${data.stats.fouls.committed}"
        8 -> "${data.stats.cards.yellow}"
        9 -> "${data.stats.cards.red}"
        10 -> "${data.stats.games.appearences}"
        else -> ""
    }

    Text(
        text = intDataText,
        textAlign = TextAlign.Center,
        fontSize = fbPlayerStandingsViewModel.dataFontSize,
        modifier = Modifier
            .width(fbPlayerStandingsViewModel.itemWidth)
    )
}