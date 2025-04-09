package com.moare.android.features.search.display.nba.viewmodel

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplayModel
import com.moare.android.features.search.models.models.nba.NBATeamStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NBATeamStandingsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : MVIViewModel<NBATeamStandingsViewModel.Intent, NBATeamStandingsDisplayModel>() {
    /* ---------------------
       constants
       --------------------- */
    val dataItemHeight = 40.dp
    val categoryItemHeight = 40.dp
    val firstCategoryItemWidth = 132.dp
    val dataItemWidth = 50.dp
    val categoryFontSize = 15.sp
    val dataFontSize = 15.sp

    /* ---------------------
       data state
       --------------------- */
    private var _displayModel = MutableStateFlow<NBATeamStandingsDisplayModel?>(null)
    val displayModel: StateFlow<NBATeamStandingsDisplayModel?> = _displayModel

    private var _standings = MutableStateFlow<List<NBATeamStandingsDisplay>>(emptyList())
    val standings: StateFlow<List<NBATeamStandingsDisplay>> = _standings

    /* ---------------------
       ui state
       --------------------- */
    private var _selectedConferenceIndex = MutableStateFlow(0)
    val selectedConferenceIndex: StateFlow<Int> = _selectedConferenceIndex

    private var _selectedCategoryIndex = MutableStateFlow(1) // defalue category is "승률"
    val selectedCategoryIndex: StateFlow<Int> = _selectedCategoryIndex

    private var _isKeyword = MutableStateFlow(false)
    val isKeyword: StateFlow<Boolean> = _isKeyword

    /* ---------------------
       etc
       --------------------- */
    var teamNameDictionary: Map<String, String> = emptyMap()

    init {
        teamNameDictionary = nameProvider.getDictionary("nba_team")
    }

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data class InitData(val displayModel: NBATeamStandingsDisplayModel) : Intent()
        data class SelectConference(val index: Int) : Intent()
        data class SelectCagetory(val index: Int) : Intent()
        data object SortStandings : Intent()
    }

    override fun send(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.InitData -> initData(intent.displayModel)
                is Intent.SelectConference -> selectConference(intent.index)
                is Intent.SelectCagetory -> selectCategory(intent.index)
                is Intent.SortStandings -> sortStandings()
            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: NBATeamStandingsDisplayModel) {
        viewModelScope.launch {
            // init with default value
            _selectedConferenceIndex.emit(0)
            _selectedCategoryIndex.emit(1)
            _isKeyword.emit(false)
            _standings.emit(emptyList())

            // init data
            _displayModel.emit(displayModel)

            val keywords = displayModel.keywords
            if (keywords.isNotEmpty()) {
                val index = StringConstants.NBA.teamStandingsCategories.indexOfFirst { category ->
                    val keyword = keywords.find { it.keyword == category }
                    keyword != null
                }

                if (index != -1) {
                    _selectedCategoryIndex.emit(index)
                    _isKeyword.emit(true)
                }
            }

            selectConference(isInit = true)
        }
    }

    /* ---------------------
       implements
       --------------------- */
    private suspend fun selectConference(index: Int = 0, isInit: Boolean = false) {
        val standings = if (isInit) {
            val entityTeam = displayModel.value?.standings?.firstOrNull { team ->
                // Any first team that matches with any team in entityInfo
                displayModel.value?.entityInfo?.firstOrNull { it.teamId == team.team.id } != null
            }

            // When init, if entity's conference is east, set index 1.
            // Otherwise do nothing, which would be set as default(0).
            if (entityTeam?.team?.teamConference?.lowercase() == "east") {
                _selectedConferenceIndex.emit(1)
            }

            displayModel.value?.standings?.filter {
                if (entityTeam != null) {
                    it.team.teamConference == entityTeam.team.teamConference
                } else {
                    it.team.teamConference.lowercase() == "west" // default
                }
            }
        } else {
            _selectedConferenceIndex.emit(index)

            displayModel.value?.standings?.filter {
                if (index == 0) {
                    it.team.teamConference.lowercase() == "west"
                } else {
                    it.team.teamConference.lowercase() == "east"
                }
            }
        }

        _standings.emit(standings ?: emptyList())

        sortStandings()
    }

    private suspend fun selectCategory(index: Int) {
        _selectedCategoryIndex.emit(index)

        sortStandings()
    }

    private suspend fun sortStandings() {
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

        _standings.emit(standings)
    }

    // TODO: Should move to util
    fun calculateGamesBack(team: NBATeamStats): Double {
        val leader = standings.value.maxBy { it.stats.winsPct }

        return ((leader.stats.wins - team.wins) + (team.losses - leader.stats.losses)) / 2.0
    }
}






























