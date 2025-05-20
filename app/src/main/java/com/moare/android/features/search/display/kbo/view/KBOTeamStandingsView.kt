package com.moare.android.features.search.display.kbo.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import com.moare.android.core.util.KBOUtil
import com.moare.android.core.util.MLBUtil
import com.moare.android.features.search.display.common.container.state.StandingsContainerState
import com.moare.android.features.search.display.common.container.view.StandingsViewContainer
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStandingsIntent
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStandingsViewModel
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStandingsIntent
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStandingsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplayModel
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar
import com.moare.android.ui.util.screenWidthDp

@Composable
fun KBOTeamStandingsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboTeamStandingsViewModel: KBOTeamStandingsViewModel = hiltViewModel(),
    data: KBOTeamStandingsDisplayModel
) {
    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by kboTeamStandingsViewModel.displayModel.collectAsState()
    val selectedCategoryIndex by kboTeamStandingsViewModel.selectedCategoryIndex.collectAsState()
    val isKeyword by kboTeamStandingsViewModel.isKeyword.collectAsState()

    val season = displayModel?.standings?.firstOrNull()?.stats?.season

    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       etc
       --------------------- */
    val selectedCategoryPosition = with(LocalDensity.current) {
        val position = kboTeamStandingsViewModel.dataItemWidth * selectedCategoryIndex
        position.toPx()
    }.toInt()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.KBOTeamStandings) {
            kboTeamStandingsViewModel.send(KBOTeamStandingsIntent.InitData(data))
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

    StandingsViewContainer(
        state = StandingsContainerState(
            firstCategoryItemHeight = kboTeamStandingsViewModel.categoryItemHeight
        ),
        headerContent = {
            BaseballLeagueTitle(
                url = KBOUtil.kboLogoUrl,
                leagueName = "KBO",
                leagueSeason = season ?: 2025,
            )
        },
        categoryListContent = {
            KBOTeamStandingsCategoryList()
        },
        standingsFirstDataContent = {
            KBOTeamStandingsFirstDataList()
        },
        standingsDataContent = {
            KBOTeamStandingsDataList()
        }
    )
}

@Composable
fun KBOTeamStandingsCategoryList(
    kboTeamStandingsViewModel: KBOTeamStandingsViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val selectedCategoryIndex by kboTeamStandingsViewModel.selectedCategoryIndex.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val barOffset by animateDpAsState(
        targetValue = getOffsetOfAniCapsuleBar(itemWidth = kboTeamStandingsViewModel.dataItemWidth, index = selectedCategoryIndex),
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(kboTeamStandingsViewModel.categoryItemHeight - 2.dp)
        ) {
            for ((index, value) in StringConstants.KBO.TEAM_STANDINGS_CATEGORIES.withIndex()) {
                KBOTeamStandingsCategoryListItem(
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
fun KBOTeamStandingsCategoryListItem(
    kboTeamStandingsViewModel: KBOTeamStandingsViewModel = hiltViewModel(),
    category: String,
    index: Int
) {
    Text(
        text = category,
        textAlign = TextAlign.Center,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .width(kboTeamStandingsViewModel.dataItemWidth)
            .clickable {
                kboTeamStandingsViewModel.send(KBOTeamStandingsIntent.SelectCategory(index))
            }
    )
}

@Composable
fun KBOTeamStandingsFirstDataList(
    kboTeamStandingsViewModel: KBOTeamStandingsViewModel = hiltViewModel()
) {
    val standings by kboTeamStandingsViewModel.standings.collectAsState()

    Column(
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        for ((index, value) in standings.withIndex()) {
            KBOTeamStandingsFirstDataListItem(rank = index + 1, data = value)
        }
    }
}

@Composable
fun KBOTeamStandingsFirstDataListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboTeamStandingsViewModel: KBOTeamStandingsViewModel = hiltViewModel(),
    rank: Int,
    data: KBOTeamStandingsDisplay,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(kboTeamStandingsViewModel.firstCategoryItemWidth)
            .padding(start = 10.dp)
            .height(kboTeamStandingsViewModel.dataItemHeight)
            .clickable {
                searchViewModel.send(SearchViewModel.Intent.ShowTeamStats(teamId = data.team.id))
            }
    ) {
        Text(
            text = "$rank",
            fontWeight = FontWeight.Medium,
            fontSize = kboTeamStandingsViewModel.dataFontSize,
            modifier = Modifier
                .width(22.dp)
        )

        URLImage(
            url = KBOUtil.teamLogoUrl(data.team.id),
            customSize = 25.dp,
            modifier = Modifier.padding(end = 4.dp),
            isSvg = true
        )

        Text(
            text = kboTeamStandingsViewModel.teamNameDictionary["short_${data.team.id}"] ?: data.team.teamName,
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
fun KBOTeamStandingsDataList(
    kboTeamStandingsViewModel: KBOTeamStandingsViewModel = hiltViewModel()
) {
    val standings by kboTeamStandingsViewModel.standings.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (value in standings) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(kboTeamStandingsViewModel.dataItemHeight)
            ) {
                for (index in 0 until StringConstants.KBO.TEAM_STANDINGS_CATEGORIES.size) {
                    KBOTeamStandingsDataItem(
                        data = value,
                        index = index
                    )
                }
            }
        }
    }
}

@Composable
fun KBOTeamStandingsDataItem(
    kboTeamStandingsViewModel: KBOTeamStandingsViewModel = hiltViewModel(),
    data: KBOTeamStandingsDisplay,
    index: Int
) {
    val dataText = when (index) {
        0 -> data.stats.rankData.winpct
        1 -> data.stats.rankData.gb
        2 -> data.stats.rankData.wins
        3 -> data.stats.rankData.losses
        4 -> data.stats.rankData.gp
        5 -> data.stats.rankData.streak
        6 -> data.stats.hitterData.avg
        7 -> data.stats.hitterData.h
        8 -> data.stats.hitterData.hr
        9 -> data.stats.hitterData.slg
        10 -> data.stats.hitterData.r
        11 -> data.stats.pitcherData.er
        12 -> data.stats.pitcherData.avg
        13 -> data.stats.pitcherData.h
        14 -> data.stats.pitcherData.hr
        15 -> data.stats.pitcherData.r
        16 -> data.stats.runnerData.sbPercent
        else -> ""
    }

    Text(
        text = dataText,
        textAlign = TextAlign.Center,
        fontSize = kboTeamStandingsViewModel.dataFontSize,
        modifier = Modifier
            .width(kboTeamStandingsViewModel.dataItemWidth)
    )
}