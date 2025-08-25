package com.moare.android.features.search.display.mlb.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.KBOUtil
import com.moare.android.core.util.MLBUtil
import com.moare.android.features.search.display.common.container.component.StandingsRankItem
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.display.common.container.state.StandingsContainerState
import com.moare.android.features.search.display.common.container.view.NewStandingsViewContainer
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
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterBox
import com.moare.android.ui.util.CenterColumn
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
    val headerCategorySelectedIndex by mlbTeamStandingsViewModel.headerCategorySelectedIndex.collectAsState()
    val westStandings by mlbTeamStandingsViewModel.westStandings.collectAsState()
    val eastStandings by mlbTeamStandingsViewModel.eastStandings.collectAsState()
    val centralStandings by mlbTeamStandingsViewModel.centralStandings.collectAsState()

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

    NewStandingsViewContainer(
        state = NewStandingsContainerState(
            headerCategories = StringConstants.MLB.CONFERENCE_CATEGORY,
            secondCategories = StringConstants.MLB.TEAM_STANDINGS_CATEGORIES,
            standings = emptyList(),
            headerCategorySelectedIndex = headerCategorySelectedIndex,
            secondCategorySelectedIndex = selectedCategoryIndex,
            columnWidthList = mlbTeamStandingsViewModel.columnWidthList,
        ),
        actions = StandingsContainerActions(
            headerCategoryButtonAction = { index ->
                mlbTeamStandingsViewModel.send(MLBTeamStandingsIntent.SelectHeaderCategory(index))
            },
            secondCategoryButtonAction = { index, _ ->
                mlbTeamStandingsViewModel.send(MLBTeamStandingsIntent.SelectCategory(index))
            },
            itemButtonAction = {}
        ),
        shouldUseCustomListContent = true,
        titleContent = {
            BaseballLeagueTitle(
                url = MLBUtil.mlbLogoUrl,
                leagueName = "MLB",
                leagueSeason = season
            )
        },
        customListContent = { hScrollState ->
            CenterColumn {
                // west
                MLBTeamStandingsDataList(
                    divisionTitle = "서부",
                    standings = westStandings,
                    hScrollState = hScrollState
                )

                // east
                MLBTeamStandingsDataList(
                    divisionTitle = "동부",
                    standings = eastStandings,
                    hScrollState = hScrollState
                )

                // central
                MLBTeamStandingsDataList(
                    divisionTitle = "중부",
                    standings = centralStandings,
                    hScrollState = hScrollState
                )
            }
        }
    )
}

@Composable
fun MLBTeamStandingsDataList(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbTeamStandingsViewModel: MLBTeamStandingsViewModel = hiltViewModel(),
    divisionTitle: String,
    standings: List<MLBTeamStandingsDisplay>,
    hScrollState: ScrollState
) {
    val teamNameDic = mlbTeamStandingsViewModel.teamNameDictionary

    Row {
        CenterColumn(
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            for (index in 0 until standings.size + 1) {
                if (index == 0) {
                    CenterBox(height = 40.dp) {
                        CenterColumn {
                            Text(
                                text = divisionTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            HCapsuleBar()
                        }
                    }
                } else {
                    val data = standings[index - 1]
                    val teamId = data.team.id

                    StandingsRankItem(
                        id = teamId,
                        rank = index ,
                        imageUrl = MLBUtil.teamLogoUrl(teamId),
                        isSvgLogo = true,
                        name = teamNameDic["short_${teamId}"] ?: data.team.shortName,
                        isLastItem = index == standings.size,
                        action = {
                            searchViewModel.send(SearchViewModel.Intent.ShowTeamStats(teamId = teamId))
                        }
                    )
                }
            }
        }

        Row(
            Modifier.horizontalScroll(hScrollState)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (index in 0 until standings.size + 1) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .height(40.dp)
                    ) {
                        if (index == 0) {
                            Spacer(modifier = Modifier.weight(1f))
                        } else {
                            val data = standings[index - 1]

                            for (index in 0 until StringConstants.MLB.TEAM_STANDINGS_CATEGORIES.size) {
                                MLBTeamStandingsDataListItem(
                                    data = data,
                                    index = index
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MLBTeamStandingsDataListItem(
    mlbTeamStandingsViewModel: MLBTeamStandingsViewModel = hiltViewModel(),
    data: MLBTeamStandingsDisplay,
    index: Int
) {
    val dataText = when (index) {
        0 -> data.stats.recordData?.gamesBack
        1 -> data.stats.recordData?.winningPercentage
        2 -> data.stats.recordData?.wins.toString()
        3 -> data.stats.recordData?.losses.toString()
        4 -> data.stats.recordData?.gamesPlayed.toString()
        5 -> {
            if (data.stats.recordData?.streak?.streakType?.lowercase()?.startsWith("w") == true) {
                "${data.stats.recordData.streak.streakNumber}승"
            } else if (data.stats.recordData?.streak?.streakType?.lowercase()?.startsWith("l") == true) {
                "${data.stats.recordData.streak.streakNumber}패"
            } else {
                "-"
            }
        }
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
        fontSize = 15.sp,
        modifier = Modifier
            .width(mlbTeamStandingsViewModel.columnWidthList.getOrNull(index) ?: 100.dp)
    )
}