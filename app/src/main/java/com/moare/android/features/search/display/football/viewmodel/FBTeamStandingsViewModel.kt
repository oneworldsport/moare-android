package com.moare.android.features.search.display.football.viewmodel

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseTeamStandingsViewModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplayModel
import com.moare.android.features.search.models.models.football.FBTeamStatsFixtures
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FBTeamStandingsIntent {
    data class InitData(val displayModel: FBTeamStandingsDisplayModel) : FBTeamStandingsIntent()
    data class SelectCategory(val index: Int) : FBTeamStandingsIntent()
}

@HiltViewModel
class FBTeamStandingsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseTeamStandingsViewModel<FBTeamStandingsIntent, FBTeamStandingsDisplayModel>(nameProvider) {
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
    private var _standings = MutableStateFlow<List<FBTeamStandingsDisplay>>(emptyList())
    val standings: StateFlow<List<FBTeamStandingsDisplay>> = _standings

    override fun send(intent: FBTeamStandingsIntent) {
        viewModelScope.launch {
            when (intent) {
                is FBTeamStandingsIntent.InitData -> initData(intent.displayModel)
                is FBTeamStandingsIntent.SelectCategory -> selectCategory(intent.index)
            }
        }
    }

    override fun initData(displayModel: FBTeamStandingsDisplayModel) {
        super.initData(displayModel)

        // init data
        _standings.value = displayModel.standings

        sortStandings()
    }

    /* ---------------------
       implements
       --------------------- */
    override fun selectCategory(index: Int) {
        super.selectCategory(index)

        sortStandings()
    }

    private fun sortStandings() {
        var standings = standings.value.toMutableList()

        when (selectedCategoryIndex.value) {
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