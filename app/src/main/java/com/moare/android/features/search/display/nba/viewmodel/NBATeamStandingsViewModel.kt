package com.moare.android.features.search.display.nba.viewmodel

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.display.common.viewmodel.BaseTeamStandingsViewModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplayModel
import com.moare.android.features.search.models.models.nba.NBATeamStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NBATeamStandingsIntent {
    data class InitData(val displayModel: NBATeamStandingsDisplayModel) : NBATeamStandingsIntent()
    data class SelectConference(val index: Int) : NBATeamStandingsIntent()
    data class SelectCategory(val index: Int) : NBATeamStandingsIntent()
}

@HiltViewModel
class NBATeamStandingsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseTeamStandingsViewModel<NBATeamStandingsIntent, NBATeamStandingsDisplayModel>(nameProvider) {
    /* ---------------------
       constants
       --------------------- */
    val dataItemHeight = 40.dp
    val categoryItemHeight = 44.dp
    val firstCategoryItemWidth = 132.dp
    val dataItemWidth = 50.dp
    val categoryFontSize = 15.sp
    val dataFontSize = 15.sp

    /* ---------------------
       data state
       --------------------- */
    private var _standings = MutableStateFlow<List<NBATeamStandingsDisplay>>(emptyList())
    val standings: StateFlow<List<NBATeamStandingsDisplay>> = _standings

    /* ---------------------
       ui state
       --------------------- */
    private var _selectedConferenceIndex = MutableStateFlow(0)
    val selectedConferenceIndex: StateFlow<Int> = _selectedConferenceIndex

    override fun send(intent: NBATeamStandingsIntent) {
        viewModelScope.launch {
            when (intent) {
                is NBATeamStandingsIntent.InitData -> initData(intent.displayModel)
                is NBATeamStandingsIntent.SelectConference -> selectConference(intent.index)
                is NBATeamStandingsIntent.SelectCategory -> selectCategory(intent.index)
            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: NBATeamStandingsDisplayModel) {
        super.initData(displayModel)

        // init with default value
        _selectedConferenceIndex.value = 0
        _selectedCategoryIndex.value = 1 // defalue category is "승률"
        _standings.value = emptyList()

        selectConference(isInit = true)
    }

    /* ---------------------
       implements
       --------------------- */
    private fun selectConference(index: Int = 0, isInit: Boolean = false) {
        val standings = if (isInit) {
            val entityTeam = displayModel.value?.standings?.firstOrNull { team ->
                // Any first team that matches with any team in entityInfo
                displayModel.value?.entityInfo?.firstOrNull { it.teamId == team.team.id } != null
            }

            // When init, if entity's conference is east, set index 1.
            // Otherwise do nothing, which would be set as default(0).
            if (entityTeam?.team?.teamConference?.lowercase() == "east") {
                _selectedConferenceIndex.value = 1
            }

            displayModel.value?.standings?.filter {
                if (entityTeam != null) {
                    it.team.teamConference == entityTeam.team.teamConference
                } else {
                    it.team.teamConference.lowercase() == "west" // default
                }
            }
        } else {
            _selectedConferenceIndex.value = index

            displayModel.value?.standings?.filter {
                if (index == 0) {
                    it.team.teamConference.lowercase() == "west"
                } else {
                    it.team.teamConference.lowercase() == "east"
                }
            }
        }

        _standings.value = standings ?: emptyList()

        sortStandings()
    }

    override fun selectCategory(index: Int) {
        super.selectCategory(index)

        sortStandings()
    }

    private fun sortStandings() {
        var standings = standings.value.toMutableList()

        when (selectedCategoryIndex.value) {
            0 -> standings.sortByDescending { calculateGamesBack(it.stats) }
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

    // TODO: Should move to util
    fun calculateGamesBack(team: NBATeamStats): Double {
        val leader = standings.value.maxBy { it.stats.winsPct }

        return ((leader.stats.wins - team.wins) + (team.losses - leader.stats.losses)) / 2.0
    }
}






























