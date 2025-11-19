package com.moare.android.features.search.display.kbo.view

import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.KBOUtil
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.StandingsViewContainer
import com.moare.android.features.search.display.kbo.store.KBOTeamStandingsAction
import com.moare.android.features.search.display.kbo.store.KBOTeamStandingsStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.ui.components.BaseballLeagueTitle

@Composable
fun KBOTeamStandingsView(
    searchStore: SearchStore,
    store: KBOTeamStandingsStore
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
    val standings by store.standings.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val season = displayModel.standings.firstOrNull()?.stats?.season

    val teamStandings: List<StandingsItemState> = standings.map {
        val rankData = it.stats.rankData
        val hitterData = it.stats.hitterData
        val pitcherData = it.stats.pitcherData
        StandingsItemState(
            id = it.team.id,
            imageUrl = KBOUtil.teamLogoUrl(it.team.id),
            name = teamNameDic["short_${it.team.id}"] ?: it.team.teamName,
            dataList = listOf(
                rankData.gb,
                rankData.winpct,
                rankData.wins,
                rankData.losses,
                rankData.gp,
                rankData.streak,
                hitterData.avg,
                hitterData.h,
                hitterData.hr,
                hitterData.slg,
                hitterData.r,
                pitcherData.er,
                pitcherData.avg,
                pitcherData.h,
                pitcherData.hr,
                pitcherData.r,
                it.stats.runnerData.sbPercent
            )
        )
    }
    val columnWidthList = listOf(50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 60.dp, 60.dp, 50.dp, 50.dp, 50.dp, 70.dp)

    StandingsViewContainer(
        state = NewStandingsContainerState(
            secondCategories = StringConstants.KBO.TEAM_STANDINGS_CATEGORIES,
            standings = teamStandings,
            secondCategorySelectedIndex = selectedCategoryIndex,
            firstColumnWidth = 100.dp,
            columnWidthList = columnWidthList,
        ),
        actions = StandingsContainerActions(
            secondCategoryButtonAction = { index, _ ->
                store.send(KBOTeamStandingsAction.SelectCategory(index))
            },
            itemButtonAction = { id ->
                store.send(KBOTeamStandingsAction.ShowTeamStats(id))
            }
        ),
        titleContent = {
            BaseballLeagueTitle(
                url = KBOUtil.kboLogoUrl,
                leagueName = "KBO",
                leagueSeason = season,
            )
        }
    )
}