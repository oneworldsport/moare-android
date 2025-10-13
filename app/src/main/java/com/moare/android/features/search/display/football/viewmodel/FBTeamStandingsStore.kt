package com.moare.android.features.search.display.football.viewmodel

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.store.BaseTeamStandingsStore
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplayModel
import com.moare.android.features.search.models.models.football.FBTeamStatsFixtures
import com.moare.android.features.search.models.responsemodels.football.FBTeamInfoResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamStandingsResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface FBTeamStandingsAction {
    data object InitData : FBTeamStandingsAction
    data class SelectHeaderCategory(val index: Int) : FBTeamStandingsAction
    data class SelectCategory(val index: Int) : FBTeamStandingsAction
    data class ShowTeamStats(val id: Int) : FBTeamStandingsAction
}

sealed interface FBTeamStandingsDelegate {
    data class ShowTeamStats(val model: SportDecodableModel) : FBTeamStandingsDelegate
}

class FBTeamStandingsStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: SportDecodableModel.FBTeamStandings,
    @Assisted val emitToParent: (FBTeamStandingsDelegate) -> Unit
) : BaseTeamStandingsStore<FBTeamStandingsAction, FBTeamStandingsResponseModel, FBTeamStandingsDisplayModel>(
    model.responseModel, model.displayModel, nameProvider
) {
    val dataItemHeight = 40.dp
    val categoryItemHeight = 40.dp
    val firstCategoryItemWidth = 132.dp
    val intDataItemWidth = 50.dp
    val stringDataItemWidth = 110.dp
    val stringDataItemTextWidth = 34.dp
    val categoryFontSize = 15.sp
    val dataFontSize = 15.sp

    private var _standings = MutableStateFlow<List<FBTeamStandingsDisplay>>(emptyList())
    val standings: StateFlow<List<FBTeamStandingsDisplay>> = _standings

    private var _isMLS = MutableStateFlow(false)
    val isMLS: StateFlow<Boolean> = _isMLS

    @AssistedFactory
    interface Factory {
        fun create(
            model: SportDecodableModel.FBTeamStandings,
            emitToParent: (FBTeamStandingsDelegate) -> Unit
        ) : FBTeamStandingsStore
    }

    override fun send(action: FBTeamStandingsAction) {
        scope.launch {
            when (action) {
                is FBTeamStandingsAction.InitData -> initData()
                is FBTeamStandingsAction.SelectHeaderCategory -> selectHeaderCategory(index = action.index)
                is FBTeamStandingsAction.SelectCategory -> selectCategory(action.index)
                is FBTeamStandingsAction.ShowTeamStats -> showTeamStats(action.id)
            }
        }
    }

    override fun initData() {
        super.initData()

        // init data
        _standings.value = displayModel.value.standings
        _isMLS.value = displayModel.value.leagueId == Constants.Ids.MLS

        if (isMLS.value) {
            selectHeaderCategory(index = 0, isInit = true)
        } else {
            sortStandings()
        }
    }

    override fun selectHeaderCategory(index: Int, isInit: Boolean) {
        super.selectHeaderCategory(index, isInit)

        val standings = if (isInit) {
            val entityTeam = displayModel.value.standings.firstOrNull { team ->
                // Any first team that matches with any team in entityInfo
                displayModel.value.entityInfo.firstOrNull { it.teamId == team.team.id } != null
            }

            // When init, if entity's conference is east, set index 1.
            // Otherwise do nothing, which would be set as default(0).
            if (Constants.Ids.MLSTeam.eastConference.contains(entityTeam?.team?.id)) {
                _headerCategorySelectedIndex.value = 1
            }

            displayModel.value.standings.filter {
                if (entityTeam != null) {
                    Constants.Ids.MLSTeam.eastConference.contains(it.team.id)
                } else {
                    Constants.Ids.MLSTeam.westConference.contains(it.team.id)
                }
            }
        } else {
            _headerCategorySelectedIndex.value = index

            displayModel.value.standings.filter {
                if (index == 0) {
                    Constants.Ids.MLSTeam.westConference.contains(it.team.id)
                } else {
                    Constants.Ids.MLSTeam.eastConference.contains(it.team.id)
                }
            }
        }

        _standings.value = standings

        sortStandings()
    }

    override fun selectCategory(index: Int) {
        super.selectCategory(index)

        sortStandings()
    }

    private fun sortStandings() {
        val standings = standings.value.toMutableList()

        when (categorySelectedIndex.value) {
            0 -> standings.sortByDescending { calculatePoints(it.homeAwayStats) }
            1 -> standings.sortByDescending { it.homeAwayStats.wins.total }
            2 -> standings.sortByDescending { it.homeAwayStats.draws.total }
            3 -> standings.sortBy { it.homeAwayStats.loses.total }
            4 -> standings.sortByDescending { it.homeAwayStats.played.total }
            5 -> standings.sortByDescending { it.goalsFor.total }
            6 -> standings.sortBy { it.goalsAgainst.total }
            7 -> standings.sortByDescending { it.goalsFor.total - it.goalsAgainst.total }
            8 -> standings.sortByDescending { calculateHomePoints(it.homeAwayStats) }
            9 -> standings.sortByDescending { calculateAwayPoints(it.homeAwayStats) }
        }

        _standings.value = standings
    }

    private fun showTeamStats(id: Int) {
        val team = responseModel.standings.find { team ->
            team.team.id == id
        }
        val responseModel = FBTeamInfoResponseModel(info = team)

        val dataModel = SportDecodableModel.FBTeamStats(
            responseModel = responseModel,
            displayModel = ModelConverter.fbTeamStatsConverter(responseModel)
        )

        emitToParent(FBTeamStandingsDelegate.ShowTeamStats(dataModel))
    }

    // TODO: Should move to util or make it as intent(mvi)
    fun isStringData(index: Int): Boolean {
        return index == 8 || index == 9
    }

    fun getItemWidth(index: Int): Dp {
        return if (isStringData(index)) stringDataItemWidth else intDataItemWidth
    }

    fun calculatePoints(data: FBTeamStatsFixtures): Int {
        return ((data.wins.total) * 3 + (data.draws.total))
    }

    fun getRecordString(
        data: FBTeamStatsFixtures,
        isHome: Boolean = true
    ): String {
        return if (isHome) {
            "${data.wins.home}승 ${data.draws.home}무 ${data.loses.home}패"
        } else {
            "${data.wins.away}승 ${data.draws.away}무 ${data.loses.away}패"
        }
    }

    private fun calculateHomePoints(data: FBTeamStatsFixtures): Int {
        return ((data.wins.home) * 3 + (data.draws.home))
    }

    private fun calculateAwayPoints(data: FBTeamStatsFixtures): Int {
        return ((data.wins.away) * 3 + (data.draws.away))
    }
}