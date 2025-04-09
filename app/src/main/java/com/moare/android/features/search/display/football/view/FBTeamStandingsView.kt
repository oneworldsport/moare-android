package com.moare.android.features.search.display.football.view

import android.util.Log
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
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.EnNameTranslationUtils
import com.moare.android.core.util.TranslationType
import com.moare.android.features.search.display.football.viewmodel.FBTeamStandingsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplayModel
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar

@Composable
fun FBTeamStandingsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbTeamStandingsViewModel: FBTeamStandingsViewModel = hiltViewModel(),
    data: FBTeamStandingsDisplayModel
) {
    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by fbTeamStandingsViewModel.displayModel.collectAsState()
    val selectedIndex by fbTeamStandingsViewModel.selectedIndex.collectAsState()
    val isKeyword by fbTeamStandingsViewModel.isKeyword.collectAsState()

    val league = displayModel?.league

    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       etc
       --------------------- */
    val selectedCategoryPosition = with(LocalDensity.current) {
        val position = if (selectedIndex == 9) {
            (fbTeamStandingsViewModel.intDataItemWidth * 8) + fbTeamStandingsViewModel.stringDataItemWidth
        } else {
            fbTeamStandingsViewModel.intDataItemWidth * selectedIndex
        }

        position.toPx()
    }.toInt()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.FBTeamStandings) {
            fbTeamStandingsViewModel.send(FBTeamStandingsViewModel.Intent.InitData(data))
        }
    }

    // scroll to category that matches with the keyword
    LaunchedEffect(isKeyword) {
        if (isKeyword) {
            horizontalScrollState.animateScrollTo(
                value = selectedCategoryPosition,
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
            FBTeamStandingsFirstCategoryItem()

            Row(
                Modifier.horizontalScroll(horizontalScrollState)
            ) {
                FBTeamStandingsCategoryList()
            }
        }

        // standings data
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Row {
                FBTeamStandingsFirstDataList()

                Row(
                    Modifier.horizontalScroll(horizontalScrollState)
                ) {
                    FBTeamStandingsDataList()
                }
            }
        }
    }
}

@Composable
fun FBTeamStandingsFirstCategoryItem(
    fbTeamStandingsViewModel: FBTeamStandingsViewModel = hiltViewModel()
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(fbTeamStandingsViewModel.categoryItemHeight)
    ) {
        Text(
            text = StringConstants.standingsFirstCategory,
            fontSize = fbTeamStandingsViewModel.categoryFontSize,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(130.dp)
        )

        VCapsuleBar(modifier = Modifier.alpha(0.5f))
    }
}

@Composable
fun FBTeamStandingsCategoryList(
    fbTeamStandingsViewModel: FBTeamStandingsViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val selectedIndex by fbTeamStandingsViewModel.selectedIndex.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val barOffset by animateDpAsState(
        targetValue = if (fbTeamStandingsViewModel.isStringData(selectedIndex)) {
            if (selectedIndex == 8) {
                fbTeamStandingsViewModel.intDataItemWidth * (selectedIndex) +
                getOffsetOfAniCapsuleBar(itemWidth = fbTeamStandingsViewModel.stringDataItemWidth)
            } else {
                fbTeamStandingsViewModel.intDataItemWidth * (selectedIndex - 1) +
                getOffsetOfAniCapsuleBar(itemWidth = fbTeamStandingsViewModel.stringDataItemWidth, index = 1)
            }
        } else {
            getOffsetOfAniCapsuleBar(itemWidth = fbTeamStandingsViewModel.intDataItemWidth, index = selectedIndex)
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
                .height(fbTeamStandingsViewModel.categoryItemHeight - 2.dp)
        ) {
            for ((index, value) in StringConstants.Football.teamStandingsCategories.withIndex()) {
                FBTeamStandingsCategoryListItem(
                    category = value,
                    index = index
                )
            }
        }

        HCapsuleBar(
            modifier = Modifier
                .offset(x = barOffset)
        )
    }
}

@Composable
fun FBTeamStandingsCategoryListItem(
    fbTeamStandingsViewModel: FBTeamStandingsViewModel = hiltViewModel(),
    category: String,
    index: Int
) {
    Text(
        text = category,
        textAlign = TextAlign.Center,
        fontSize = fbTeamStandingsViewModel.categoryFontSize,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .width(fbTeamStandingsViewModel.getItemWidth(index))
            .clickable {
                fbTeamStandingsViewModel.send(FBTeamStandingsViewModel.Intent.SelectCagetory(index))
            }
    )
}

@Composable
fun FBTeamStandingsFirstDataList(
    fbTeamStandingsViewModel: FBTeamStandingsViewModel = hiltViewModel(),
) {
    val standings by fbTeamStandingsViewModel.standings.collectAsState()
//    VSequentialListAni(
//        items = dataList
//    ) { index, item ->
//        FBTeamStandingsFirstDataItem(rank = index + 1, data = item, itemHeight = itemHeight)
//    }
    Column(
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        for ((index, value) in standings.withIndex()) {
            FBTeamStandingsFirstDataListItem(rank = index + 1, data = value)
        }
    }
}

@Composable
fun FBTeamStandingsFirstDataListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbTeamStandingsViewModel: FBTeamStandingsViewModel = hiltViewModel(),
    rank: Int,
    data: FBTeamStandingsDisplay,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(fbTeamStandingsViewModel.firstCategoryItemWidth)
            .padding(start = 10.dp)
            .height(fbTeamStandingsViewModel.dataItemHeight)
            .clickable {
                searchViewModel.send(SearchViewModel.Intent.ShowTeamStats(teamId = data.team.id))
            }
    ) {
        Text(
            text = "$rank",
            fontWeight = FontWeight.Medium,
            fontSize = fbTeamStandingsViewModel.dataFontSize,
            modifier = Modifier
                .width(22.dp)
        )

        URLImage(
            url = data.team.logo,
            customSize = 25.dp,
            modifier = Modifier.padding(end = 4.dp)
        )

        Text(
            text = fbTeamStandingsViewModel.teamNameDictionary["short_${data.team.id}"] ?: data.team.name,
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
fun FBTeamStandingsDataList(
    fbTeamStandingsViewModel: FBTeamStandingsViewModel = hiltViewModel()
) {
    val standings by fbTeamStandingsViewModel.standings.collectAsState()
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (value in standings) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(fbTeamStandingsViewModel.dataItemHeight)
            ) {
                for (index in 0 until StringConstants.Football.teamStandingsCategories.size) {
                    FBTeamStandingsDataItem(
                        data = value,
                        index = index
                    )
                }
            }
        }
    }
}

@Composable
fun FBTeamStandingsDataItem(
    fbTeamStandingsViewModel: FBTeamStandingsViewModel = hiltViewModel(),
    data: FBTeamStandingsDisplay,
    index: Int
) {
    val intDataText = when (index) {
        0 -> fbTeamStandingsViewModel.calculatePoints(data.homeAwayStats).toString()
        1 -> "${data.homeAwayStats.wins.total}"
        2 -> "${data.homeAwayStats.draws.total}"
        3 -> "${data.homeAwayStats.loses.total}"
        4 -> "${data.homeAwayStats.played.total}"
        5 -> "${data.goalsFor.total}"
        6 -> "${data.goalsAgainst.total}"
        7 -> "${(data.goalsFor.total) - (data.goalsAgainst.total)}"
        else -> ""
    }

    if (fbTeamStandingsViewModel.isStringData(index)) {
        if (index == 8) {
            Row(
                Modifier
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "${data.homeAwayStats.wins.home}승",
                    textAlign = TextAlign.Center,
                    fontSize = fbTeamStandingsViewModel.dataFontSize,
                    modifier = Modifier
                        .width(fbTeamStandingsViewModel.stringDataItemTextWidth)
                )
                Text(
                    text = "${data.homeAwayStats.draws.home}무",
                    textAlign = TextAlign.Center,
                    fontSize = fbTeamStandingsViewModel.dataFontSize,
                    modifier = Modifier
                        .width(fbTeamStandingsViewModel.stringDataItemTextWidth)
                )
                Text(
                    text = "${data.homeAwayStats.loses.home}패",
                    textAlign = TextAlign.Center,
                    fontSize = fbTeamStandingsViewModel.dataFontSize,
                    modifier = Modifier
                        .width(fbTeamStandingsViewModel.stringDataItemTextWidth)
                )
            }
        } else {
            Row(
                Modifier
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "${data.homeAwayStats.wins.away}승",
                    textAlign = TextAlign.Center,
                    fontSize = fbTeamStandingsViewModel.dataFontSize,
                    modifier = Modifier
                        .width(fbTeamStandingsViewModel.stringDataItemTextWidth)
                )
                Text(
                    text = "${data.homeAwayStats.draws.away}무",
                    textAlign = TextAlign.Center,
                    fontSize = fbTeamStandingsViewModel.dataFontSize,
                    modifier = Modifier
                        .width(fbTeamStandingsViewModel.stringDataItemTextWidth)
                )
                Text(
                    text = "${data.homeAwayStats.loses.away}패",
                    textAlign = TextAlign.Center,
                    fontSize = fbTeamStandingsViewModel.dataFontSize,
                    modifier = Modifier
                        .width(fbTeamStandingsViewModel.stringDataItemTextWidth)
                )
            }
        }
    } else {
        Text(
            text = intDataText,
            textAlign = TextAlign.Center,
            fontSize = fbTeamStandingsViewModel.dataFontSize,
            modifier = Modifier
                .width(fbTeamStandingsViewModel.intDataItemWidth)
        )
    }
}
























