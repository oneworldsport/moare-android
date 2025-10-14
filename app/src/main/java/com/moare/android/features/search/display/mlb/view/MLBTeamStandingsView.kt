package com.moare.android.features.search.display.mlb.view

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.MLBUtil
import com.moare.android.features.search.display.common.container.component.StandingsRankItem
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.display.common.container.view.StandingsViewContainer
import com.moare.android.features.search.display.football.viewmodel.FBTeamStandingsAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStandingsAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStandingsStore
import com.moare.android.features.search.display.search.viewmodel.SearchAction
import com.moare.android.features.search.display.search.viewmodel.SearchStore
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplay
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.util.CenterBox
import com.moare.android.ui.util.CenterColumn

@Composable
fun MLBTeamStandingsView(
    searchStore: SearchStore,
    store: MLBTeamStandingsStore
) {
    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by store.displayModel.collectAsState()
    val selectedCategoryIndex by store.categorySelectedIndex.collectAsState()
    val headerCategorySelectedIndex by store.headerCategorySelectedIndex.collectAsState()
    val westStandings by store.westStandings.collectAsState()
    val eastStandings by store.eastStandings.collectAsState()
    val centralStandings by store.centralStandings.collectAsState()

    val season = displayModel.standings.firstOrNull()?.team?.season

    StandingsViewContainer(
        state = NewStandingsContainerState(
            headerCategories = StringConstants.MLB.CONFERENCE_CATEGORY,
            secondCategories = StringConstants.MLB.TEAM_STANDINGS_CATEGORIES,
            standings = emptyList(),
            headerCategorySelectedIndex = headerCategorySelectedIndex,
            secondCategorySelectedIndex = selectedCategoryIndex,
            columnWidthList = store.columnWidthList,
        ),
        actions = StandingsContainerActions(
            headerCategoryButtonAction = { index ->
                store.send(MLBTeamStandingsAction.SelectHeaderCategory(index))
            },
            secondCategoryButtonAction = { index, _ ->
                store.send(MLBTeamStandingsAction.SelectCategory(index))
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
                    searchStore = searchStore,
                    store = store,
                    divisionTitle = "서부",
                    standings = westStandings,
                    hScrollState = hScrollState
                )

                // east
                MLBTeamStandingsDataList(
                    searchStore = searchStore,
                    store = store,
                    divisionTitle = "동부",
                    standings = eastStandings,
                    hScrollState = hScrollState
                )

                // central
                MLBTeamStandingsDataList(
                    searchStore = searchStore,
                    store = store,
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
    searchStore: SearchStore,
    store: MLBTeamStandingsStore,
    divisionTitle: String,
    standings: List<MLBTeamStandingsDisplay>,
    hScrollState: ScrollState
) {
    val teamNameDic by store.teamNameDic.collectAsState()

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
                        action = { id ->
                            store.send(MLBTeamStandingsAction.ShowTeamStats(id))
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
                                    store = store,
                                    data = data,
                                    standings = standings,
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
    store: MLBTeamStandingsStore,
    data: MLBTeamStandingsDisplay,
    standings: List<MLBTeamStandingsDisplay>,
    index: Int
) {
    val dataText = when (index) {
        0 -> if (MLBUtil.calculateGamesBack(data.stats, standings) == 0.0) "-" else MLBUtil.calculateGamesBack(data.stats, standings).toString()
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
            .width(store.columnWidthList.getOrNull(index) ?: 100.dp)
    )
}