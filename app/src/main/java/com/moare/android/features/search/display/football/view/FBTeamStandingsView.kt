package com.moare.android.features.search.display.football.view

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
import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.MLBUtil
import com.moare.android.core.util.Util
import com.moare.android.features.search.display.common.container.component.StandingsRankItem
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.StandingsViewContainer
import com.moare.android.features.search.display.football.store.FBTeamStandingsAction
import com.moare.android.features.search.display.football.store.FBTeamStandingsStore
import com.moare.android.features.search.display.mlb.store.MLBTeamStandingsAction
import com.moare.android.features.search.display.mlb.store.MLBTeamStandingsStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplay
import com.moare.android.ui.common.components.FBLeagueTitle
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.util.CenterBox
import com.moare.android.ui.util.CenterColumn

@Composable
fun FBTeamStandingsView(
    searchStore: SearchStore,
    store: FBTeamStandingsStore
) {
    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by store.displayModel.collectAsState()
    val isMLS by store.isMLS.collectAsState()
    val isGroupStandings by store.isGroupStandings.collectAsState()
    val headerCategorySelectedIndex by store.headerCategorySelectedIndex.collectAsState()
    val selectedCategoryIndex by store.categorySelectedIndex.collectAsState()
    val standings by store.standings.collectAsState()
    val groupStandings by store.groupStandings.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val league = displayModel.league
    val leagueId = displayModel.leagueId

    val teamStandings: List<StandingsItemState> = standings.map {
        StandingsItemState(
            id = it.team.id,
            rank = it.displayRank,
            imageUrl = it.team.logo,
            name = teamNameDic["short_${it.team.id}"] ?: it.team.name,
            dataList = listOf(
//                store.calculatePoints(it.homeAwayStats).toString(),
                it.points.toString(),
                it.homeAwayStats.wins.total.toString(),
                it.homeAwayStats.draws.total.toString(),
                it.homeAwayStats.loses.total.toString(),
                it.homeAwayStats.played.total.toString(),
                it.goalsFor.total.toString(),
                it.goalsAgainst.total.toString(),
                (it.goalsFor.total - it.goalsAgainst.total).toString(),
                store.getRecordString(it.homeAwayStats),
                store.getRecordString(it.homeAwayStats, false)
            )
        )
    }

    val columnWidthList = if (leagueId == Constants.Ids.WORLD_CUP) {
        listOf(50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp)
    } else {
        listOf(50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 100.dp, 100.dp)
    }
    val headerCategories = if (leagueId == Constants.Ids.WORLD_CUP) {
        listOf("A조 ~ F조", "G조 ~ L조")
    } else {
        listOf("서부 컨퍼런스", "동부 컨퍼런스")
    }

    StandingsViewContainer(
        state = NewStandingsContainerState(
            headerCategories = if (isMLS || isGroupStandings) headerCategories else null,
            secondCategories = if (isGroupStandings) StringConstants.Football.TEAM_GROUP_STANDINGS_CATEGORIES else StringConstants.Football.TEAM_STANDINGS_CATEGORIES,
            standings = teamStandings,
            headerCategorySelectedIndex = headerCategorySelectedIndex,
            secondCategorySelectedIndex = selectedCategoryIndex,
            columnWidthList = columnWidthList
        ),
        actions = StandingsContainerActions(
            headerCategoryButtonAction = { index ->
                store.send(FBTeamStandingsAction.SelectHeaderCategory(index))
            },
            secondCategoryButtonAction = { index, _ ->
                store.send(FBTeamStandingsAction.SelectCategory(index))
            },
            itemButtonAction = { id ->
                // TODO: UEFA 리그는 showTeamStats가 안돼서 Button disabled 처리 해야함.
                store.send(FBTeamStandingsAction.ShowTeamStats(id))
            }
        ),
        shouldUseCustomListContent = leagueId == Constants.Ids.WORLD_CUP,
        titleContent = {
            league?.let {
                FBLeagueTitle(
                    url = league.logo,
                    leagueName = league.name,
                    leagueSeason = league.season,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        },
        customListContent = { hScrollState ->
            CenterColumn {
                groupStandings.keys
                    .sorted()
                    .forEach { group ->
                        groupStandings[group]?.let {
                            FBTeamStandingsDataList(
                                searchStore = searchStore,
                                store = store,
                                group = "${group}조",
                                standings = it,
                                hScrollState = hScrollState
                            )
                        }
                    }
            }
        }
    )
}

@Composable
fun FBTeamStandingsDataList(
    searchStore: SearchStore,
    store: FBTeamStandingsStore,
    group: String,
    standings: List<FBTeamStandingsDisplay>,
    hScrollState: ScrollState
) {
    val displayModel by store.displayModel.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val leagueId = displayModel.leagueId

    Row {
        CenterColumn(
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            for (index in 0 until standings.size + 1) {
                if (index == 0) {
                    CenterBox(height = 40.dp) {
                        CenterColumn {
                            Text(
                                text = group,
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
                        rank = data.displayRank ,
                        imageUrl = Util.teamLogoUrl(leagueId, teamId),
                        isSvgLogo = true,
                        name = teamNameDic["short_${teamId}"] ?: data.team.name,
                        isLastItem = index == standings.size,
                        action = { id ->
                            store.send(FBTeamStandingsAction.ShowTeamStats(id))
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

                            for (index in 0 until StringConstants.Football.TEAM_GROUP_STANDINGS_CATEGORIES.size) {
                                FBTeamStandingsDataListItem(
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
fun FBTeamStandingsDataListItem(
    store: FBTeamStandingsStore,
    data: FBTeamStandingsDisplay,
    standings: List<FBTeamStandingsDisplay>,
    index: Int
) {
    val dataText = when (index) {
        0 -> data.points.toString()
        1 -> data.homeAwayStats.wins.total.toString()
        2 -> data.homeAwayStats.draws.total.toString()
        3 -> data.homeAwayStats.loses.total.toString()
        4 -> data.homeAwayStats.played.total.toString()
        5 -> data.goalsFor.total.toString()
        6 -> data.goalsAgainst.total.toString()
        7 -> (data.goalsFor.total - data.goalsAgainst.total).toString()
        else -> ""
    }

    Text(
        text = dataText,
        textAlign = TextAlign.Center,
        fontSize = 15.sp,
        modifier = Modifier
            .width(50.dp)
    )
}























