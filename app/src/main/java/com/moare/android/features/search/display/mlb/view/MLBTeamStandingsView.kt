package com.moare.android.features.search.display.mlb.view

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
import com.moare.android.core.util.MLBUtil
import com.moare.android.features.search.display.common.container.state.StandingsContainerState
import com.moare.android.features.search.display.common.container.view.StandingsViewContainer
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStandingsIntent
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStandingsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplayModel
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar
import com.moare.android.ui.util.screenWidthDp

@Composable
fun MLBTeamStandingsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbTeamStandingsViewModel: MLBTeamStandingsViewModel = hiltViewModel(),
    data: MLBTeamStandingsDisplayModel
) {
    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by mlbTeamStandingsViewModel.displayModel.collectAsState()
    val selectedCategoryIndex by mlbTeamStandingsViewModel.selectedCategoryIndex.collectAsState()
    val isKeyword by mlbTeamStandingsViewModel.isKeyword.collectAsState()

    val season = displayModel?.standings?.firstOrNull()?.team?.season

    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       etc
       --------------------- */
    val selectedCategoryPosition = with(LocalDensity.current) {
        val position = mlbTeamStandingsViewModel.dataItemWidth * selectedCategoryIndex
        position.toPx()
    }.toInt()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.MLBTeamStandings) {
            mlbTeamStandingsViewModel.send(MLBTeamStandingsIntent.InitData(data))
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
            firstCategoryItemHeight = mlbTeamStandingsViewModel.categoryItemHeight,
            isTopPaddingOnHeader = false
        ),
        headerContent = {
            BaseballLeagueTitle(
                url = MLBUtil.mlbLogoUrl,
                leagueName = "MLB",
                leagueSeason = season ?: 2025
            )

            // conference
            Row(
                modifier = Modifier.padding(top = 6.dp)
            ) {
                MLBDivisionButtonContainer()
            }
        },
        categoryListContent = {
            MLBTeamStandingsCategoryList()
        },
        standingsFirstDataContent = {
            MLBTeamStandingsFirstDataList()
        },
        standingsDataContent = {
            MLBTeamStandingsDataList()
        }
    )
}

@Composable
fun MLBDivisionButtonContainer(
    mlbTeamStandingsViewModel: MLBTeamStandingsViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val selectedConferenceIndex by mlbTeamStandingsViewModel.selectedDivisionIndex.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val barOffset by animateDpAsState(
        targetValue = getOffsetOfAniCapsuleBar(itemWidth = screenWidthDp() / 6, index = selectedConferenceIndex),
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(mlbTeamStandingsViewModel.categoryItemHeight - 2.dp)
        ) {
            for (index in 0 until StringConstants.MLB.DIVISION_CATEGORY.size) {
                Text(
                    text = StringConstants.MLB.DIVISION_CATEGORY[index],
                    textAlign = TextAlign.Center,
                    fontSize = mlbTeamStandingsViewModel.categoryFontSize,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable {
                            mlbTeamStandingsViewModel.send(
                                MLBTeamStandingsIntent.SelectDivison(
                                    index
                                )
                            )
                        }
                )

                if (index == 2) {
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
fun MLBTeamStandingsCategoryList(
    mlbTeamStandingsViewModel: MLBTeamStandingsViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val selectedCategoryIndex by mlbTeamStandingsViewModel.selectedCategoryIndex.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val barOffset by animateDpAsState(
        targetValue = getOffsetOfAniCapsuleBar(itemWidth = mlbTeamStandingsViewModel.dataItemWidth, index = selectedCategoryIndex),
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(mlbTeamStandingsViewModel.categoryItemHeight - 2.dp)
        ) {
            for ((index, value) in StringConstants.MLB.TEAM_STANDINGS_CATEGORIES.withIndex()) {
                MLBTeamStandingsCategoryListItem(
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
fun MLBTeamStandingsCategoryListItem(
    mlbTeamStandingsViewModel: MLBTeamStandingsViewModel = hiltViewModel(),
    category: String,
    index: Int
) {
    Text(
        text = category,
        textAlign = TextAlign.Center,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .width(mlbTeamStandingsViewModel.dataItemWidth)
            .clickable {
                mlbTeamStandingsViewModel.send(MLBTeamStandingsIntent.SelectCategory(index))
            }
    )
}

@Composable
fun MLBTeamStandingsFirstDataList(
    mlbTeamStandingsViewModel: MLBTeamStandingsViewModel = hiltViewModel()
) {
    val standings by mlbTeamStandingsViewModel.standings.collectAsState()

    Column(
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        for ((index, value) in standings.withIndex()) {
            MLBTeamStandingsFirstDataListItem(rank = index + 1, data = value)
        }
    }
}

@Composable
fun MLBTeamStandingsFirstDataListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbTeamStandingsViewModel: MLBTeamStandingsViewModel = hiltViewModel(),
    rank: Int,
    data: MLBTeamStandingsDisplay,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(mlbTeamStandingsViewModel.firstCategoryItemWidth)
            .padding(start = 10.dp)
            .height(mlbTeamStandingsViewModel.dataItemHeight)
            .clickable {
                searchViewModel.send(SearchViewModel.Intent.ShowTeamStats(teamId = data.team.id))
            }
    ) {
        Text(
            text = "$rank",
            fontWeight = FontWeight.Medium,
            fontSize = mlbTeamStandingsViewModel.dataFontSize,
            modifier = Modifier
                .width(22.dp)
        )

        URLImage(
            url = MLBUtil.teamLogoUrl(data.team.id),
            customSize = 25.dp,
            modifier = Modifier.padding(end = 4.dp),
            isSvg = true
        )

        Text(
            text = mlbTeamStandingsViewModel.teamNameDictionary["short_${data.team.id}"] ?: data.team.teamName,
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
fun MLBTeamStandingsDataList(
    mlbTeamStandingsViewModel: MLBTeamStandingsViewModel = hiltViewModel()
) {
    val standings by mlbTeamStandingsViewModel.standings.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (value in standings) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(mlbTeamStandingsViewModel.dataItemHeight)
            ) {
                for (index in 0 until StringConstants.MLB.TEAM_STANDINGS_CATEGORIES.size) {
                    MLBTeamStandingsDataItem(
                        data = value,
                        index = index
                    )
                }
            }
        }
    }
}

@Composable
fun MLBTeamStandingsDataItem(
    mlbTeamStandingsViewModel: MLBTeamStandingsViewModel = hiltViewModel(),
    data: MLBTeamStandingsDisplay,
    index: Int
) {
    val dataText = when (index) {
        0 -> data.stats.recordData?.winningPercentage
        1 -> data.stats.recordData?.gamesBack
        2 -> data.stats.recordData?.wins.toString()
        3 -> data.stats.recordData?.losses.toString()
        4 -> data.stats.recordData?.gamesPlayed.toString()
        5 -> data.stats.recordData?.streak?.streakNumber.toString()
        6 -> data.stats.hitting?.avg
        7 -> data.stats.hitting?.hits.toString()
        8 -> data.stats.hitting?.homeRuns.toString()
        9 -> data.stats.hitting?.slg
        10 -> data.stats.hitting?.runs.toString()
        11 -> data.stats.pitching?.era
        12 -> data.stats.pitching?.avg
        13 -> data.stats.pitching?.hits.toString()
        14 -> data.stats.pitching?.homeRuns.toString()
        15 -> data.stats.pitching?.runs.toString()
        16 -> data.stats.hitting?.stolenBasePercentage
        else -> ""
    } ?: ""

    Text(
        text = dataText,
        textAlign = TextAlign.Center,
        fontSize = mlbTeamStandingsViewModel.dataFontSize,
        modifier = Modifier
            .width(mlbTeamStandingsViewModel.dataItemWidth)
    )
}