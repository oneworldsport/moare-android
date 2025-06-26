package com.moare.android.features.search.display.nba.view

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
import com.moare.android.core.util.NBAUtil
import com.moare.android.features.search.display.common.container.state.StandingsContainerState
import com.moare.android.features.search.display.common.container.view.StandingsViewContainer
import com.moare.android.features.search.display.football.view.FBPlayerStandingsDataList
import com.moare.android.features.search.display.football.view.FBPlayerStandingsFirstCategoryList
import com.moare.android.features.search.display.football.view.FBPlayerStandingsFirstDataList
import com.moare.android.features.search.display.football.view.FBPlayerStandingsSecondCategoryList
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerStandingsIntent
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerStandingsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStandingsDisplay
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStandingsDisplayModel
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.HCapsuleBarSize
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.ProgressIndicator
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.convertDpToPx
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar
import kotlinx.coroutines.delay

@Composable
fun NBAPlayerStandingsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaPlayerStandingsViewModel: NBAPlayerStandingsViewModel = hiltViewModel(),
    data: NBAPlayerStandingsDisplayModel
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
    val displayModel by nbaPlayerStandingsViewModel.displayModel.collectAsState()
    val displayDataState by nbaPlayerStandingsViewModel.displayDataState.collectAsState()
    val firstSelectedIndex by nbaPlayerStandingsViewModel.firstSelectedIndex.collectAsState()
    val secondSelectedIndex by nbaPlayerStandingsViewModel.secondSelectedIndex.collectAsState()
    val isKeyword by nbaPlayerStandingsViewModel.isKeyword.collectAsState()
    val filteredStandings by nbaPlayerStandingsViewModel.filteredStandings.collectAsState()

    val season = displayModel?.standings?.firstOrNull()?.stats?.groupValue

    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       etc
       --------------------- */
    val secondSelectedCategoryPosition = with(LocalDensity.current) {
        val attackCategoriesSize = StringConstants.NBA.PLAYER_STANDINGS_ATTACK_CATEGORIES.size
        val defendCategoriesSize = StringConstants.NBA.PLAYER_STANDINGS_DEFEND_CATEGORIES.size

        if (secondSelectedIndex in 0 until attackCategoriesSize) {
            (nbaPlayerStandingsViewModel.itemWidth * secondSelectedIndex).toPx()
        } else if (secondSelectedIndex in attackCategoriesSize until attackCategoriesSize + defendCategoriesSize) {
            ((nbaPlayerStandingsViewModel.itemWidth * secondSelectedIndex) + nbaPlayerStandingsViewModel.barWidth).toPx()
        } else {
            ((nbaPlayerStandingsViewModel.itemWidth * secondSelectedIndex) + (nbaPlayerStandingsViewModel.barWidth * 2)).toPx()
        }
    }.toInt()

    val previousScrollPosition = convertDpToPx(nbaPlayerStandingsViewModel.dataItemHeight * 10).toInt()
    val firstScrollPosition = convertDpToPx(nbaPlayerStandingsViewModel.dataItemHeight).toInt()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.NBAPlayerStandings) {
            nbaPlayerStandingsViewModel.send(NBAPlayerStandingsIntent.InitData(data))
        }
    }

    // Scroll to category that matches with the keyword,
    // or when first category list's item is selected by click.
    LaunchedEffect(isKeyword, firstSelectedIndex) {
        if (nbaPlayerStandingsViewModel.shouldScrollCategory) {
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
                nbaPlayerStandingsViewModel.send(NBAPlayerStandingsIntent.ShowMoreStandings(true))
            }
            verticalScrollState.maxValue -> {
                nbaPlayerStandingsViewModel.send(NBAPlayerStandingsIntent.ShowMoreStandings(false))
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

    StandingsViewContainer(
        state = StandingsContainerState(
            displayDataState = displayDataState,
            firstCategoryItemHeight = nbaPlayerStandingsViewModel.firstCategoryItemHeight + nbaPlayerStandingsViewModel.secondCategoryItemHeight
        ),
        headerContent = {
            NBATitle(
                leagueName = "NBA 정규시즌",
                leagueSeason = season?.split("-")?.firstOrNull()?.toIntOrNull() ?: 2024
            )
        },
        categoryListContent = {
            Column {
                NBAPlayerStandingsFirstCategoryList()
                NBAPlayerStandingsSecondCategoryList()
            }
        },
        standingsFirstDataContent = {
            NBAPlayerStandingsFirstDataList()
        },
        standingsDataContent = {
            NBAPlayerStandingsDataList()
        }
    )
}

@Composable
fun NBAPlayerStandingsFirstCategoryList(
    nbaPlayerStandingsViewModel: NBAPlayerStandingsViewModel = hiltViewModel()
) {
    /* ---------------------
       constants
       --------------------- */
    val attackCategoriesSize = StringConstants.NBA.PLAYER_STANDINGS_ATTACK_CATEGORIES.size
    val defendCategoriesSize = StringConstants.NBA.PLAYER_STANDINGS_DEFEND_CATEGORIES.size
    val commonCategoriesSize = StringConstants.NBA.PLAYER_STANDINGS_COMMON_CATEGORIES.size

    /* ---------------------
       viewmodel state
       --------------------- */
    val selectedIndex by nbaPlayerStandingsViewModel.firstSelectedIndex.collectAsState()

    val itemWidth = nbaPlayerStandingsViewModel.itemWidth
    val barWidth = nbaPlayerStandingsViewModel.barWidth

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
                .height(nbaPlayerStandingsViewModel.firstCategoryItemHeight - 2.dp)
        ) {
            for ((index, value) in StringConstants.STATS_FIRST_CATEGORIES.withIndex()) {
                NBAPlayerStandingsFirstCategoryListItem(
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
fun NBAPlayerStandingsFirstCategoryListItem(
    nbaPlayerStandingsViewModel: NBAPlayerStandingsViewModel = hiltViewModel(),
    category: String,
    index: Int
) {
    val itemWidth = nbaPlayerStandingsViewModel.itemWidth

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(
                if (index == 0) {
                    (itemWidth * StringConstants.NBA.PLAYER_STANDINGS_ATTACK_CATEGORIES.size)
                } else if (index == 1) {
                    (itemWidth * StringConstants.NBA.PLAYER_STANDINGS_DEFEND_CATEGORIES.size)
                } else {
                    (itemWidth * StringConstants.NBA.PLAYER_STANDINGS_COMMON_CATEGORIES.size)
                }
            )
            .clickable {
                nbaPlayerStandingsViewModel.send(NBAPlayerStandingsIntent.SelectFirstCategory(index))
            }
    ) {
        Text(
            text = category,
            fontSize = nbaPlayerStandingsViewModel.firstCategoryFontSize,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NBAPlayerStandingsSecondCategoryList(
    nbaPlayerStandingsViewModel: NBAPlayerStandingsViewModel = hiltViewModel()
) {
    /* ---------------------
       constants
       --------------------- */
    val attackCategoriesSize = StringConstants.NBA.PLAYER_STANDINGS_ATTACK_CATEGORIES.size
    val defendCategoriesSize = StringConstants.NBA.PLAYER_STANDINGS_DEFEND_CATEGORIES.size

    /* ---------------------
       viewmodel state
       --------------------- */
    val selectedIndex by nbaPlayerStandingsViewModel.secondSelectedIndex.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val barOffset by animateDpAsState(
        targetValue = if (selectedIndex in 0 until attackCategoriesSize) {
            getOffsetOfAniCapsuleBar(itemWidth = nbaPlayerStandingsViewModel.itemWidth, index = selectedIndex)
        } else if (selectedIndex in attackCategoriesSize until attackCategoriesSize + defendCategoriesSize) {
            getOffsetOfAniCapsuleBar(itemWidth = nbaPlayerStandingsViewModel.itemWidth, index = selectedIndex) + nbaPlayerStandingsViewModel.barWidth
        } else {
            getOffsetOfAniCapsuleBar(itemWidth = nbaPlayerStandingsViewModel.itemWidth, index = selectedIndex) + (nbaPlayerStandingsViewModel.barWidth * 2)
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
                .height(nbaPlayerStandingsViewModel.secondCategoryItemHeight - 2.dp)
        ) {
            for ((index, value) in StringConstants.NBA.PLAYER_STANDINGS_SECOND_CATEGORIES.withIndex()) {
                NBAPlayerStandingsSecondCategoryListItem(
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
fun NBAPlayerStandingsSecondCategoryListItem(
    nbaPlayerStandingsViewModel: NBAPlayerStandingsViewModel = hiltViewModel(),
    category: String,
    index: Int
) {
    Text(
        text = if (category.contains("경기당")) {
            "경기당\n${category.substringAfter("경기당 ")}"
        } else {
            category
        },
        textAlign = TextAlign.Center,
        fontSize = nbaPlayerStandingsViewModel.secondCategoryFontSize,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .width(nbaPlayerStandingsViewModel.itemWidth)
            .clickable {
                nbaPlayerStandingsViewModel.send(
                    NBAPlayerStandingsIntent.SelectSecondCategory(index, category)
                )
            }
    )
}

@Composable
fun NBAPlayerStandingsFirstDataList(
    nbaPlayerStandingsViewModel: NBAPlayerStandingsViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val filteredStandings by nbaPlayerStandingsViewModel.filteredStandings.collectAsState()
    val entityIndex by nbaPlayerStandingsViewModel.entityIndex.collectAsState()
    val filterStandingsStartIndex by nbaPlayerStandingsViewModel.filteredStandingsStartIndex.collectAsState()

    Column (
        modifier = Modifier.width(nbaPlayerStandingsViewModel.firstCategoryItemWidth)
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

            NBAPlayerStandingsFirstDataListItem(rank = standingsIndex + 1, data = value)

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
fun NBAPlayerStandingsFirstDataListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaPlayerStandingsViewModel: NBAPlayerStandingsViewModel = hiltViewModel(),
    rank: Int,
    data: NBAPlayerStandingsDisplay
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 10.dp)
            .height(nbaPlayerStandingsViewModel.dataItemHeight)
            .clickable {
                searchViewModel.send(SearchViewModel.Intent.ShowPlayerStats(category = "basketball", playerId = data.player.personId))
            }
    ) {
        Text(
            text = "$rank",
            fontWeight = FontWeight.Medium,
            fontSize = nbaPlayerStandingsViewModel.dataFontSize,
            modifier = Modifier
                .width(26.dp)
        )

        URLImage(
            url = NBAUtil.playerPhotoUrl(data.player.personId),
            customSize = 25.dp,
            modifier = Modifier.padding(end = 4.dp)
        )

        Row(
            // added to make VCapsuleBar visible
            modifier = Modifier.weight(1f)
        ) {
            Column {
                Text(
                    text = nbaPlayerStandingsViewModel.playerNameDictionary[data.player.personId.toString()] ?: data.player.displayFirstLast,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(bottom = 2.dp)
                )

                Text(
                    text = nbaPlayerStandingsViewModel.teamNameDictionary["short_${data.player.teamId}"] ?: data.player.teamCity,
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
fun NBAPlayerStandingsDataList(
    nbaPlayerStandingsViewModel: NBAPlayerStandingsViewModel = hiltViewModel()
) {
    /* ---------------------
       constants
       --------------------- */
    val attackCategoriesSize = StringConstants.NBA.PLAYER_STANDINGS_ATTACK_CATEGORIES.size
    val defendCategoriesSize = StringConstants.NBA.PLAYER_STANDINGS_DEFEND_CATEGORIES.size

    /* ---------------------
       viewmodel state
       --------------------- */
    val filteredStandings by nbaPlayerStandingsViewModel.filteredStandings.collectAsState()
    val entityIndex by nbaPlayerStandingsViewModel.entityIndex.collectAsState()
    val filteredStandingsStartIndex by nbaPlayerStandingsViewModel.filteredStandingsStartIndex.collectAsState()

    Column {
        for ((index, value) in filteredStandings.withIndex()) {
            val standingsIndex = filteredStandingsStartIndex + index
            val categorySize = StringConstants.NBA.PLAYER_STANDINGS_SECOND_CATEGORIES.size
            val highlightWidth = (nbaPlayerStandingsViewModel.itemWidth * categorySize) + (2.dp * 2)

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
                    .height(nbaPlayerStandingsViewModel.dataItemHeight)
            ) {
                for (index in 0 until categorySize) {
                    NBAPlayerStandingsDataListItem(
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
fun NBAPlayerStandingsDataListItem(
    nbaPlayerStandingsViewModel: NBAPlayerStandingsViewModel = hiltViewModel(),
    data: NBAPlayerStandingsDisplay,
    index: Int
) {
    val intDataText = when (index) {
        0 -> "${data.stats.ptsPG}"
        1 -> "${data.stats.astPG}"
        2 -> "${data.stats.orebPG}"
        3 -> "${data.stats.fgaPG}"
        4 -> "${data.stats.fgmPG}"
        5 -> "${data.stats.fgPct}"
        6 -> "${data.stats.fg3aPG}"
        7 -> "${data.stats.fg3mPG}"
        8 -> "${data.stats.fg3Pct}"
        9 -> "${data.stats.ftaPG}"
        10 -> "${data.stats.ftmPG}"
        11 -> "${data.stats.ftPct}"
        12 -> "${data.stats.drebPG}"
        13 -> "${data.stats.blkPG}"
        14 -> "${data.stats.stlPG}"
        15 -> "${data.stats.rebPG}"
        16 -> "${data.stats.tovPG}"
        17 -> "${data.stats.pfPG}"
        18 -> "${data.stats.pfdPG}"
        19 -> "${data.stats.blkaPG}"
        20 -> "${data.stats.plusMinusPG}"
        21 -> "${data.stats.gp}"
        22 -> data.stats.minPG
        23 -> "${data.stats.wins}"
        24 -> "${data.stats.losses}"
        25 -> "${data.stats.winsPct}"
        26 -> "${data.stats.td3}"
        27 -> "${data.stats.dd2}"
        else -> ""
    }

    Text(
        text = intDataText,
        textAlign = TextAlign.Center,
        fontSize = nbaPlayerStandingsViewModel.dataFontSize,
        modifier = Modifier
            .width(nbaPlayerStandingsViewModel.itemWidth)
    )
}
















