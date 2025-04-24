package com.moare.android.features.search.display.football.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.rounded
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStandingsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplay
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplayModel
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.HCapsuleBarSize
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.ProgressIndicator
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.convertDpToPx
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar
import kotlinx.coroutines.delay

@Composable
fun FBPlayerStandingsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbPlayerStandingsViewModel: FBPlayerStandingsViewModel = hiltViewModel(),
    data: FBPlayerStandingsDisplayModel
) {
    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()
    var isFirstOpen by remember { mutableStateOf(true) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by fbPlayerStandingsViewModel.displayModel.collectAsState()
    val displayDataState by fbPlayerStandingsViewModel.displayDataState.collectAsState()
    val firstSelectedIndex by fbPlayerStandingsViewModel.firstSelectedIndex.collectAsState()
    val secondSelectedIndex by fbPlayerStandingsViewModel.secondSelectedIndex.collectAsState()
    val isKeyword by fbPlayerStandingsViewModel.isKeyword.collectAsState()
    val filteredStandings by fbPlayerStandingsViewModel.filteredStandings.collectAsState()

    val league = displayModel?.standings?.first()?.stats?.league

    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       etc
       --------------------- */
    val secondSelectedCategoryPosition = with(LocalDensity.current) {
        val attackCategoriesSize = StringConstants.Football.PLAYER_STANDINGS_ATTACK_CATEGORIES.size
        val defendCategoriesSize = StringConstants.Football.PLAYER_STANDINGS_DEFEND_CATEGORIES.size

        if (secondSelectedIndex in 0 until attackCategoriesSize) {
            (fbPlayerStandingsViewModel.itemWidth * secondSelectedIndex).toPx()
        } else if (secondSelectedIndex in attackCategoriesSize until attackCategoriesSize + defendCategoriesSize) {
            ((fbPlayerStandingsViewModel.itemWidth * secondSelectedIndex) + fbPlayerStandingsViewModel.barWidth).toPx()
        } else {
            ((fbPlayerStandingsViewModel.itemWidth * secondSelectedIndex) + (fbPlayerStandingsViewModel.barWidth * 2)).toPx()
        }
    }.toInt()

    val previousScrollPosition = convertDpToPx(fbPlayerStandingsViewModel.dataItemHeight * 10).toInt()
    val firstScrollPosition = convertDpToPx(fbPlayerStandingsViewModel.dataItemHeight).toInt()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.FBPlayerStandings) {
            fbPlayerStandingsViewModel.send(FBPlayerStandingsViewModel.Intent.InitData(data))
        }
    }

    // Scroll to category that matches with the keyword,
    // or when first category list's item is selected by click.
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

    LaunchedEffect(verticalScrollState.value) {
        // prevent executing at first open
        if (isFirstOpen) {
            isFirstOpen = false
            return@LaunchedEffect
        }

        when (verticalScrollState.value) {
            0 -> {
                fbPlayerStandingsViewModel.send(FBPlayerStandingsViewModel.Intent.ShowMoreStandings(true))
            }
            verticalScrollState.maxValue -> {
                fbPlayerStandingsViewModel.send(FBPlayerStandingsViewModel.Intent.ShowMoreStandings(false))
            }
        }
    }

    LaunchedEffect(filteredStandings) {
        if (filteredStandings.size == 20) {
            verticalScrollState.scrollTo(firstScrollPosition)
        } else if (filteredStandings.size > 20 && verticalScrollState.value == 0) {
            delay(100)
            verticalScrollState.scrollTo(previousScrollPosition)
        }
    }

    /* ---------------------
       ui
       --------------------- */
    Column(
        // NOTE: If set fillMaxSize, AnimatedVisibility works fine on first show.
        // But if fillMaxSize not set, AnimatedVisibility doesn't work on first show.
        // Not sure why yet
        modifier = Modifier.fillMaxSize()
    ) {
        league?.let {
            LeagueTitle(
                url = league.logo,
                leagueName = league.name,
                leagueSeason = league.season
            )
        }

        // category
        Row(
            modifier = Modifier.padding(top = 6.dp)
        ) {
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

        // loading
        AnimatedVisibility(
            visible = displayDataState == ApiFetchState.Fetching,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                ProgressIndicator()
            }
        }

        // standings data
        AnimatedVisibility(
            visible = displayDataState == ApiFetchState.Success
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(verticalScrollState)
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
            text = StringConstants.STANDINGS_FIRST_CATEGORY,
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
       constants
       --------------------- */
    val attackCategoriesSize = StringConstants.Football.PLAYER_STANDINGS_ATTACK_CATEGORIES.size
    val defendCategoriesSize = StringConstants.Football.PLAYER_STANDINGS_DEFEND_CATEGORIES.size
    val commonCategoriesSize = StringConstants.Football.PLAYER_STANDINGS_COMMON_CATEGORIES.size

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
                itemWidth = itemWidth * attackCategoriesSize,
                barWidth = 80.dp
            )
        } else if (selectedIndex == 1) {
            (itemWidth * attackCategoriesSize) + barWidth + getOffsetOfAniCapsuleBar(
                itemWidth = itemWidth * defendCategoriesSize,
                barWidth = 80.dp
            )
        } else {
            (itemWidth * attackCategoriesSize) + (barWidth * 2) + (itemWidth * defendCategoriesSize) + getOffsetOfAniCapsuleBar(
                itemWidth = itemWidth * commonCategoriesSize,
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
            for ((index, value) in StringConstants.STATS_FIRST_CATEGORIES.withIndex()) {
                FBPlayerStandingsFirstCategoryListItem(
                    category = value,
                    index = index
                )

                if (index != StringConstants.STATS_FIRST_CATEGORIES.size - 1) {
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
            .width(
                if (index == 0) {
                    (itemWidth * StringConstants.Football.PLAYER_STANDINGS_ATTACK_CATEGORIES.size)
                } else if (index == 1) {
                    (itemWidth * StringConstants.Football.PLAYER_STANDINGS_DEFEND_CATEGORIES.size)
                } else {
                    (itemWidth * StringConstants.Football.PLAYER_STANDINGS_COMMON_CATEGORIES.size)
                }
            )
            .clickable {
                fbPlayerStandingsViewModel.send(FBPlayerStandingsViewModel.Intent.SelectFirstCategory(index))
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
       constants
       --------------------- */
    val attackCategoriesSize = StringConstants.Football.PLAYER_STANDINGS_ATTACK_CATEGORIES.size
    val defendCategoriesSize = StringConstants.Football.PLAYER_STANDINGS_DEFEND_CATEGORIES.size

    /* ---------------------
       viewmodel state
       --------------------- */
    val selectedIndex by fbPlayerStandingsViewModel.secondSelectedIndex.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val barOffset by animateDpAsState(
        targetValue = if (selectedIndex in 0 until attackCategoriesSize) {
            getOffsetOfAniCapsuleBar(itemWidth = fbPlayerStandingsViewModel.itemWidth, index = selectedIndex)
        } else if (selectedIndex in attackCategoriesSize until attackCategoriesSize + defendCategoriesSize) {
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
            for ((index, value) in StringConstants.Football.PLAYER_STANDINGS_SECOND_CATEGORIES.withIndex()) {
                FBPlayerStandingsSecondCategoryListItem(
                    category = value,
                    index = index
                )

                if (index == attackCategoriesSize - 1 || index == (attackCategoriesSize + defendCategoriesSize - 1)) {
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
    val fontSize = when (index) {
        6, 9, 17 -> 13.sp
        else -> fbPlayerStandingsViewModel.categoryFontSize
    }

    Text(
        text = category,
        textAlign = TextAlign.Center,
        fontSize = fontSize,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .width(fbPlayerStandingsViewModel.itemWidth)
            .clickable {
                fbPlayerStandingsViewModel.send(
                    FBPlayerStandingsViewModel.Intent.SelectSecondCategory(index, category)
                )
            }
    )
}

@Composable
fun FBPlayerStandingsFirstDataList(
    fbPlayerStandingsViewModel: FBPlayerStandingsViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val filteredStandings by fbPlayerStandingsViewModel.filteredStandings.collectAsState()
    val entityIndex by fbPlayerStandingsViewModel.entityIndex.collectAsState()
    val filterStandingsStartIndex by fbPlayerStandingsViewModel.filteredStandingsStartIndex.collectAsState()
//    VSequentialListAni(
//        items = dataList
//    ) { index, item ->
//        FBTeamStandingsFirstDataItem(rank = index + 1, data = item, itemHeight = itemHeight)
//    }
    Column (
        modifier = Modifier.width(fbPlayerStandingsViewModel.firstCategoryItemWidth)
    ) {
        for ((index, value) in filteredStandings.withIndex()) {
            val standingsIndex = filterStandingsStartIndex + index

            if (entityIndex != null && entityIndex == standingsIndex) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Moare)
                )
            }

            FBPlayerStandingsFirstDataListItem(rank = standingsIndex + 1, data = value)

            if (entityIndex != null && entityIndex == standingsIndex) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Moare)
                )
            }
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
            .padding(start = 10.dp)
            .height(fbPlayerStandingsViewModel.dataItemHeight)
            .clickable {
                searchViewModel.send(SearchViewModel.Intent.ShowPlayerStats(category = "football", playerId = data.player.id))
            }
    ) {
        Text(
            text = "$rank",
            fontWeight = FontWeight.Medium,
            fontSize = fbPlayerStandingsViewModel.dataFontSize,
            modifier = Modifier
                .width(26.dp)
        )

        URLImage(
            url = data.player.photo,
            customSize = 25.dp,
            modifier = Modifier.padding(end = 4.dp)
        )

        Row(
            // added to make VCapsuleBar visible
            modifier = Modifier.weight(1f)
        ) {
            Column {
                Text(
                    text = fbPlayerStandingsViewModel.playerNameDictionary["${data.player.id}"] ?: data.player.name,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(bottom = 2.dp)
                )

                Text(
                    text = fbPlayerStandingsViewModel.teamNameDictionary["short_${data.stats.team.id}"] ?: data.stats.team.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                )
            }
        }

        VCapsuleBar(modifier = Modifier.alpha(0.5f))
    }
}

@Composable
fun FBPlayerStandingsDataList(
    fbPlayerStandingsViewModel: FBPlayerStandingsViewModel = hiltViewModel()
) {
    /* ---------------------
       constants
       --------------------- */
    val attackCategoriesSize = StringConstants.Football.PLAYER_STANDINGS_ATTACK_CATEGORIES.size
    val defendCategoriesSize = StringConstants.Football.PLAYER_STANDINGS_DEFEND_CATEGORIES.size

    /* ---------------------
       viewmodel state
       --------------------- */
    val filteredStandings by fbPlayerStandingsViewModel.filteredStandings.collectAsState()
    val entityIndex by fbPlayerStandingsViewModel.entityIndex.collectAsState()
    val filteredStandingsStartIndex by fbPlayerStandingsViewModel.filteredStandingsStartIndex.collectAsState()
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
        for ((index, value) in filteredStandings.withIndex()) {
            val standingsIndex = filteredStandingsStartIndex + index
            val categorySize = StringConstants.Football.PLAYER_STANDINGS_SECOND_CATEGORIES.size
            val highlightWidth = (fbPlayerStandingsViewModel.itemWidth * categorySize) + (2.dp * 2)

            if (entityIndex != null && entityIndex == standingsIndex) {
                Box(
                    Modifier
                        .width(highlightWidth)
                        .height(1.dp)
                        .background(Moare)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(fbPlayerStandingsViewModel.dataItemHeight)
            ) {
                for (index in 0 until categorySize) {
                    FBPlayerStandingsDataListItem(
                        data = value,
                        index = index
                    )

                    if (index == attackCategoriesSize - 1 || index == (attackCategoriesSize + defendCategoriesSize - 1)) {
                        VCapsuleBar(modifier = Modifier.alpha(0f))
                    }
                }
            }

            if (entityIndex != null && entityIndex == standingsIndex) {
                Box(
                    Modifier
                        .width(highlightWidth)
                        .height(1.dp)
                        .background(Moare)
                )
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
        5 -> "${data.stats.passes.key}"
        6 -> "${data.stats.dribbles.success}"
        7 -> "${data.stats.penalty.scored}"
        8 -> "${data.stats.tackles.total}"
        9 -> "${data.stats.duels.won}"
        10 -> "${data.stats.passes.total}"
        11 -> "${data.stats.fouls.committed}"
        12 -> "${data.stats.cards.yellow}"
        13 -> "${data.stats.cards.red}"
        14 -> "${data.stats.games.appearences}"
        15 -> "${data.stats.games.lineups}"
        16 -> "${data.stats.substitutes.substituteIn}"
        17 -> "${data.stats.games.minutes}"
        18 -> "${data.stats.games.rating.toDoubleOrNull()?.rounded(2) ?: 0.0}"
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