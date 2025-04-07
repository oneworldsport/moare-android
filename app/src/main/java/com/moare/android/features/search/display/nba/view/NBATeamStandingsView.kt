package com.moare.android.features.search.display.nba.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import com.moare.android.core.util.NBAUtil
import com.moare.android.core.util.TranslationType
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStandingsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplayModel
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar
import com.moare.android.ui.util.screenWidthDp

@Composable
fun NBATeamStandingsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaTeamStandingsViewModel: NBATeamStandingsViewModel = hiltViewModel(),
    data: NBATeamStandingsDisplayModel
) {
    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by nbaTeamStandingsViewModel.displayModel.collectAsState()
    val selectedConferenceIndex by nbaTeamStandingsViewModel.selectedConferenceIndex.collectAsState()
    val selectedCategoryIndex by nbaTeamStandingsViewModel.selectedCategoryIndex.collectAsState()
    val isKeyword by nbaTeamStandingsViewModel.isKeyword.collectAsState()

    val season = displayModel?.standings?.firstOrNull()?.stats?.groupValue

    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       etc
       --------------------- */
    val selectedCategoryPosition = with(LocalDensity.current) {
        val position = nbaTeamStandingsViewModel.dataItemWidth * selectedCategoryIndex
        position.toPx()
    }.toInt()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.NBATeamStandings) {
            nbaTeamStandingsViewModel.send(NBATeamStandingsViewModel.Intent.InitData(data))
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
        NBATitle(
            leagueName = "NBA 정규시즌",
            leagueSeason = season?.split("-")?.firstOrNull()?.toIntOrNull() ?: 2024
        )

        // conference
        Row(
            modifier = Modifier.padding(top = 6.dp)
        ) {
            NBAConferenceButtonContainer()
        }

        // category
        Row {
            NBATeamStandingsFirstCategoryItem()

            Row(
                Modifier.horizontalScroll(horizontalScrollState)
            ) {
                NBATeamStandingsCategoryList()
            }
        }

        // standings data
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Row {
                NBATeamStandingsFirstDataList()

                Row(
                    Modifier.horizontalScroll(horizontalScrollState)
                ) {
                    NBATeamStandingsDataList()
                }
            }
        }
    }
}

@Composable
fun NBAConferenceButtonContainer(
    nbaTeamStandingsViewModel: NBATeamStandingsViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val selectedConferenceIndex by nbaTeamStandingsViewModel.selectedConferenceIndex.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val barOffset by animateDpAsState(
        targetValue = getOffsetOfAniCapsuleBar(itemWidth = screenWidthDp() / 2, index = selectedConferenceIndex),
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(nbaTeamStandingsViewModel.categoryItemHeight - 2.dp)
        ) {
            for (index in 0 until 2) {
                Text(
                    text = if (index == 0) "서부 컨퍼런스" else "동부 컨퍼런스",
                    textAlign = TextAlign.Center,
                    fontSize = nbaTeamStandingsViewModel.categoryFontSize,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable {
                            nbaTeamStandingsViewModel.send(
                                NBATeamStandingsViewModel.Intent.SelectConference(
                                    index
                                )
                            )
                        }
                )

                if (index == 0) {
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
fun NBATeamStandingsFirstCategoryItem(
    nbaTeamStandingsViewModel: NBATeamStandingsViewModel = hiltViewModel()
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(nbaTeamStandingsViewModel.categoryItemHeight)
    ) {
        Text(
            text = StringConstants.standingsFirstCategory,
            fontSize = nbaTeamStandingsViewModel.categoryFontSize,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(130.dp)
        )

        VCapsuleBar(modifier = Modifier.alpha(0.5f))
    }
}

@Composable
fun NBATeamStandingsCategoryList(
    nbaTeamStandingsViewModel: NBATeamStandingsViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val selectedCategoryIndex by nbaTeamStandingsViewModel.selectedCategoryIndex.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val barOffset by animateDpAsState(
        targetValue = getOffsetOfAniCapsuleBar(itemWidth = nbaTeamStandingsViewModel.dataItemWidth, index = selectedCategoryIndex),
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(nbaTeamStandingsViewModel.categoryItemHeight - 2.dp)
        ) {
            for ((index, value) in StringConstants.NBA.teamStandingsCategories.withIndex()) {
                NBATeamStandingsCategoryListItem(
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
fun NBATeamStandingsCategoryListItem(
    nbaTeamStandingsViewModel: NBATeamStandingsViewModel = hiltViewModel(),
    category: String,
    index: Int
) {
    Text(
        text = category,
        textAlign = TextAlign.Center,
        fontSize = nbaTeamStandingsViewModel.categoryFontSize,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .width(nbaTeamStandingsViewModel.dataItemWidth)
            .clickable {
                nbaTeamStandingsViewModel.send(NBATeamStandingsViewModel.Intent.SelectCagetory(index))
            }
    )
}

@Composable
fun NBATeamStandingsFirstDataList(
    nbaTeamStandingsViewModel: NBATeamStandingsViewModel = hiltViewModel()
) {
    val standings by nbaTeamStandingsViewModel.standings.collectAsState()

    Column(
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        for ((index, value) in standings.withIndex()) {
            NBATeamStandingsFirstDataListItem(rank = index + 1, data = value)
        }
    }
}

@Composable
fun NBATeamStandingsFirstDataListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaTeamStandingsViewModel: NBATeamStandingsViewModel = hiltViewModel(),
    rank: Int,
    data: NBATeamStandingsDisplay,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(nbaTeamStandingsViewModel.firstCategoryItemWidth)
            .padding(start = 10.dp)
            .height(nbaTeamStandingsViewModel.dataItemHeight)
            .clickable {
//                searchViewModel.send(SearchViewModel.Intent.ShowTeamStats(teamId = data.team.id))
            }
    ) {
        Text(
            text = "$rank",
            fontWeight = FontWeight.Medium,
            fontSize = nbaTeamStandingsViewModel.dataFontSize,
            modifier = Modifier
                .width(22.dp)
        )

        URLImage(
            url = NBAUtil.teamLogoUrl(data.team.id),
            customSize = 25.dp,
            modifier = Modifier.padding(end = 4.dp),
            isSvg = true
        )

        Text(
            text = EnNameTranslationUtils.translateByDic(TranslationType.TEAM, input = data.team.fullName),
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
fun NBATeamStandingsDataList(
    nbaTeamStandingsViewModel: NBATeamStandingsViewModel = hiltViewModel()
) {
    val standings by nbaTeamStandingsViewModel.standings.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (value in standings) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(nbaTeamStandingsViewModel.dataItemHeight)
            ) {
                for (index in 0 until StringConstants.NBA.teamStandingsCategories.size) {
                    NBATeamStandingsDataItem(
                        data = value,
                        index = index
                    )
                }
            }
        }
    }
}

@Composable
fun NBATeamStandingsDataItem(
    nbaTeamStandingsViewModel: NBATeamStandingsViewModel = hiltViewModel(),
    data: NBATeamStandingsDisplay,
    index: Int
) {
    val dataText = when (index) {
        0 -> nbaTeamStandingsViewModel.calculateGamesBack(data.stats).toString()
        1 -> "${data.stats.winsPct}"
        2 -> "${data.stats.wins}"
        3 -> "${data.stats.losses}"
        4 -> "${data.stats.gp}"
        5 -> "${data.stats.ptsPG}"
        6 -> "${data.stats.plusMinusPG}"
        7 -> "${data.stats.astPG}"
        8 -> "${data.stats.rebPG}"
        9 -> "${data.stats.fgPct}"
        10 -> "${data.stats.fg3Pct}"
        11 -> "${data.stats.ftPct}"
        12 -> "${data.stats.blkPG}"
        13 -> "${data.stats.stlPG}"
        14 -> "${data.stats.tovPG}"
        15 -> "${data.stats.pfPG}"
        else -> ""
    }

    Text(
        text = dataText,
        textAlign = TextAlign.Center,
        fontSize = nbaTeamStandingsViewModel.dataFontSize,
        modifier = Modifier
            .width(nbaTeamStandingsViewModel.dataItemWidth)
    )
}































