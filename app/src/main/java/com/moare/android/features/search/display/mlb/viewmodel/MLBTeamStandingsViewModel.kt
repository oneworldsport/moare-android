package com.moare.android.features.search.display.mlb.viewmodel

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseTeamStandingsViewModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MLBTeamStandingsIntent {
    data class InitData(val displayModel: MLBTeamStandingsDisplayModel) : MLBTeamStandingsIntent()
    data class SelectDivison(val index: Int) : MLBTeamStandingsIntent()
    data class SelectCategory(val index: Int) : MLBTeamStandingsIntent()
}

@HiltViewModel
class MLBTeamStandingsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseTeamStandingsViewModel<MLBTeamStandingsIntent, MLBTeamStandingsDisplayModel>(nameProvider) {
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
    private var _standings = MutableStateFlow<List<MLBTeamStandingsDisplay>>(emptyList())
    val standings: StateFlow<List<MLBTeamStandingsDisplay>> = _standings

    /* ---------------------
       ui state
       --------------------- */
    private var _selectedDivisionIndex = MutableStateFlow(0)
    val selectedDivisionIndex: StateFlow<Int> = _selectedDivisionIndex

    override fun send(intent: MLBTeamStandingsIntent) {
        viewModelScope.launch {
            when (intent) {
                is MLBTeamStandingsIntent.InitData -> initData(intent.displayModel)
                is MLBTeamStandingsIntent.SelectDivison -> selectDivision(intent.index)
                is MLBTeamStandingsIntent.SelectCategory -> selectCategory(intent.index)
            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: MLBTeamStandingsDisplayModel) {
        super.initData(displayModel)

        // init with default value
        _selectedDivisionIndex.value = 0
        _selectedCategoryIndex.value = 0 // defalue category is "승률"
        _standings.value = emptyList()

        selectDivision(isInit = true)
    }

    /* ---------------------
       implements
       --------------------- */
    private fun selectDivision(index: Int = 0, isInit: Boolean = false) {
        val standings = if (isInit) {
            val entityTeam = displayModel.value?.standings?.firstOrNull { team ->
                // Any first team that matches with any team in entityInfo
                displayModel.value?.entityInfo?.firstOrNull { it.teamId == team.team.id } != null
            }

            // When init, if set to matching index with entity's division.
            when (entityTeam?.team?.division?.id) {
                Constants.Ids.NATIONAL_LEAGUE_WEST -> _selectedDivisionIndex.value = 0
                Constants.Ids.NATIONAL_LEAGUE_EAST -> _selectedDivisionIndex.value = 1
                Constants.Ids.NATIONAL_LEAGUE_CENTRAL -> _selectedDivisionIndex.value = 2
                Constants.Ids.AMERICAN_LEAGUE_WEST -> _selectedDivisionIndex.value = 3
                Constants.Ids.AMERICAN_LEAGUE_EAST -> _selectedDivisionIndex.value = 4
                Constants.Ids.AMERICAN_LEAGUE_CENTRAL -> _selectedDivisionIndex.value = 5
            }

            displayModel.value?.standings?.filter {
                if (entityTeam != null) {
                    it.team.division.id == entityTeam.team.division.id
                } else {
                    it.team.division.id == Constants.Ids.NATIONAL_LEAGUE_WEST // default
                }
            }
        } else {
            _selectedDivisionIndex.value = index

            displayModel.value?.standings?.filter {
                when (index) {
                    0 -> it.team.division.id == Constants.Ids.NATIONAL_LEAGUE_WEST
                    1 -> it.team.division.id == Constants.Ids.NATIONAL_LEAGUE_EAST
                    2 -> it.team.division.id == Constants.Ids.NATIONAL_LEAGUE_CENTRAL
                    3 -> it.team.division.id == Constants.Ids.AMERICAN_LEAGUE_WEST
                    4 -> it.team.division.id == Constants.Ids.AMERICAN_LEAGUE_EAST
                    5 -> it.team.division.id == Constants.Ids.AMERICAN_LEAGUE_CENTRAL
                    else -> it.team.division.id == Constants.Ids.NATIONAL_LEAGUE_WEST
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
            0 -> standings.sortByDescending { it.stats.recordData?.winningPercentage?.toFloatOrNull() }
            1 -> standings.sortBy { it.stats.recordData?.gamesBack?.toFloatOrNull() }
            2 -> standings.sortByDescending { it.stats.recordData?.wins }
            3 -> standings.sortBy { it.stats.recordData?.losses }
            4 -> standings.sortByDescending { it.stats.recordData?.gamesPlayed }
            5 -> standings.sortByDescending { it.stats.recordData?.streak?.streakNumber } // TODO: type 구분필요
            6 -> standings.sortByDescending { it.stats.hitting?.avg?.toFloatOrNull() }
            7 -> standings.sortByDescending { it.stats.hitting?.hits }
            8 -> standings.sortByDescending { it.stats.hitting?.homeRuns }
            9 -> standings.sortByDescending { it.stats.hitting?.slg?.toFloatOrNull() }
            10 -> standings.sortByDescending { it.stats.hitting?.runs }
            11 -> standings.sortBy { it.stats.pitching?.era?.toFloatOrNull() }
            12 -> standings.sortBy { it.stats.pitching?.avg?.toFloatOrNull() }
            13 -> standings.sortBy { it.stats.pitching?.hits }
            14 -> standings.sortBy { it.stats.pitching?.homeRuns }
            15 -> standings.sortBy { it.stats.pitching?.runs }
            16 -> standings.sortByDescending { it.stats.hitting?.stolenBasePercentage?.toFloatOrNull() }
        }

        _standings.value = standings
    }
}