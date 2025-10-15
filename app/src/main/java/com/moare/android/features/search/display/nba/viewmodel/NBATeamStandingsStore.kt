package com.moare.android.features.search.display.nba.viewmodel

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.store.BaseTeamStandingsStore
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplayModel
import com.moare.android.features.search.models.models.nba.NBATeamStats
import com.moare.android.features.search.models.responsemodels.nba.NBATeamInfoResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBATeamStandingsResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface NBATeamStandingsAction {
    data object InitData : NBATeamStandingsAction
    data class SelectHeaderCategory(val index: Int) : NBATeamStandingsAction
    data class SelectCategory(val index: Int) : NBATeamStandingsAction
    data class ShowTeamStats(val id: Int) : NBATeamStandingsAction
}

sealed interface NBATeamStandingsDelegate {
    data class ShowTeamStats(val model: SportDecodableModel) : NBATeamStandingsDelegate
}

class NBATeamStandingsStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: SportDecodableModel.NBATeamStandings,
    @Assisted val emitToParent: (NBATeamStandingsDelegate) -> Unit
) : BaseTeamStandingsStore<NBATeamStandingsAction, NBATeamStandingsResponseModel, NBATeamStandingsDisplayModel>(
    model.responseModel, model.displayModel, nameProvider
) {
    val dataItemHeight = 40.dp
    val categoryItemHeight = 44.dp
    val firstCategoryItemWidth = 132.dp
    val dataItemWidth = 50.dp
    val categoryFontSize = 15.sp
    val dataFontSize = 15.sp

    private var _standings = MutableStateFlow<List<NBATeamStandingsDisplay>>(emptyList())
    val standings: StateFlow<List<NBATeamStandingsDisplay>> = _standings

    @AssistedFactory
    interface Factory {
        fun create(
            model: SportDecodableModel.NBATeamStandings,
            emitToParent: (NBATeamStandingsDelegate) -> Unit
        ) : NBATeamStandingsStore
    }

    override fun send(action: NBATeamStandingsAction) {
        scope.launch {
            when (action) {
                is NBATeamStandingsAction.InitData -> initData()
                is NBATeamStandingsAction.SelectHeaderCategory -> selectHeaderCategory(index = action.index)
                is NBATeamStandingsAction.SelectCategory -> selectCategory(action.index)
                is NBATeamStandingsAction.ShowTeamStats -> showTeamStats(action.id)
            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData() {
        super.initData()

        // init with default value
        _categorySelectedIndex.value = 1 // defalue category is "승률"
        _standings.value = emptyList()

        selectHeaderCategory(index = 0, isInit = true)
    }

    /* ---------------------
       implements
       --------------------- */
    override fun selectHeaderCategory(index: Int, isInit: Boolean) {
        super.selectHeaderCategory(index, isInit)

        val standings = if (isInit) {
            val entityTeam = displayModel.value.standings.firstOrNull { team ->
                // Any first team that matches with any team in entityInfo
                displayModel.value.entityInfo.firstOrNull { it.teamId == team.team.id } != null
            }

            // When init, if entity's conference is east, set index 1.
            // Otherwise do nothing, which would be set as default(0).
            if (entityTeam?.team?.teamConference?.lowercase() == "east") {
                _headerCategorySelectedIndex.value = 1
            }

            displayModel.value.standings.filter {
                if (entityTeam != null) {
                    it.team.teamConference == entityTeam.team.teamConference
                } else {
                    it.team.teamConference.lowercase() == "west" // default
                }
            }
        } else {
            _headerCategorySelectedIndex.value = index

            displayModel.value.standings.filter {
                if (index == 0) {
                    it.team.teamConference.lowercase() == "west"
                } else {
                    it.team.teamConference.lowercase() == "east"
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
            0 -> standings.sortBy { calculateGamesBack(it.stats) }
            1 -> standings.sortByDescending { it.stats.winsPct }
            2 -> standings.sortByDescending { it.stats.wins }
            3 -> standings.sortBy { it.stats.losses }
            4 -> standings.sortByDescending { it.stats.gp }
//            5 -> standings.sortedByDescending {  }
//            6 -> standings.sortedByDescending {  }
            5 -> standings.sortByDescending { it.stats.ptsPG }
            6 -> standings.sortByDescending { it.stats.plusMinusPG }
            7 -> standings.sortByDescending { it.stats.astPG }
            8 -> standings.sortByDescending { it.stats.rebPG }
            9 -> standings.sortByDescending { it.stats.fgPct }
            10 -> standings.sortByDescending { it.stats.fg3Pct }
            11 -> standings.sortByDescending { it.stats.ftPct }
            12 -> standings.sortByDescending { it.stats.blkPG }
            13 -> standings.sortByDescending { it.stats.stlPG }
            14 -> standings.sortBy { it.stats.tovPG }
            15 -> standings.sortBy { it.stats.pfPG }
        }

        _standings.value = standings
    }

    private fun showTeamStats(id: Int) {
        val team = responseModel.standings.find { team ->
            team.team.id == id
        }
        val responseModel = NBATeamInfoResponseModel(info = team)

        val dataModel = SportDecodableModel.NBATeamStats(
            responseModel = responseModel,
            displayModel = ModelConverter.nbaTeamStatsConverter(responseModel)
        )

        emitToParent(NBATeamStandingsDelegate.ShowTeamStats(dataModel))
    }

    // TODO: Should move to util
    fun calculateGamesBack(team: NBATeamStats): Double {
        val leader = standings.value.maxBy { it.stats.winsPct }

        return ((leader.stats.wins - team.wins) + (team.losses - leader.stats.losses)) / 2.0
    }
}






























