package com.moare.android.features.search.display.football.viewmodel

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplayModel
import com.moare.android.features.search.models.models.football.FBTeamStatsFixtures
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FBTeamStandingsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : MVIViewModel<FBTeamStandingsViewModel.Intent, FBTeamStandingsDisplayModel>() {
    /* ---------------------
       constants
       --------------------- */
    val dataItemHeight = 40.dp
    val categoryItemHeight = 40.dp
    val firstCategoryItemWidth = 132.dp
    val intDataItemWidth = 50.dp
    val stringDataItemWidth = 110.dp
    val stringDataItemTextWidth = 34.dp
    val categoryFontSize = 15.sp
    val dataFontSize = 15.sp

    /* ---------------------
       data state
       --------------------- */
    private var _displayModel = MutableStateFlow<FBTeamStandingsDisplayModel?>(null)
    val displayModel: StateFlow<FBTeamStandingsDisplayModel?> = _displayModel

    private var _standings = MutableStateFlow<List<FBTeamStandingsDisplay>>(emptyList())
    val standings: StateFlow<List<FBTeamStandingsDisplay>> = _standings

    /* ---------------------
       ui state
       --------------------- */
    private var _selectedIndex = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex

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
        data class InitData(val displayModel: FBTeamStandingsDisplayModel) : Intent()
        data class SelectCagetory(val index: Int) : Intent()
        data object SortStandings : Intent()
    }

    override fun send(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.InitData -> initData(intent.displayModel)
                is Intent.SelectCagetory -> selectCategory(intent.index)
                is Intent.SortStandings -> sortStandings()
            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: FBTeamStandingsDisplayModel) {
        viewModelScope.launch {
            // init with default value
            _selectedIndex.emit(0)
            _isKeyword.emit(false)

            // init data
            _displayModel.emit(displayModel)
            _standings.emit(displayModel.standings)

            val keywords = displayModel.keywords
            if (keywords.isNotEmpty()) {
                val index = StringConstants.Football.TEAM_STANDINGS_CATEGORIES.indexOfFirst { category ->
                    val keyword = keywords.find { it.keyword == category }
                    keyword != null
                }

                if (index != -1) {
                    _selectedIndex.emit(index)
                    _isKeyword.emit(true)
                }
            }

            sortStandings()
        }
    }

    /* ---------------------
       implements
       --------------------- */
    private suspend fun selectCategory(index: Int) {
        _selectedIndex.emit(index)

        sortStandings()
    }

    private suspend fun sortStandings() {
        var standings = standings.value.toMutableList()

        when (selectedIndex.value) {
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

        _standings.emit(standings)
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

    private fun calculateHomePoints(data: FBTeamStatsFixtures): Int {
        return ((data.wins.home) * 3 + (data.draws.home))
    }

    private fun calculateAwayPoints(data: FBTeamStatsFixtures): Int {
        return ((data.wins.away) * 3 + (data.draws.away))
    }
}