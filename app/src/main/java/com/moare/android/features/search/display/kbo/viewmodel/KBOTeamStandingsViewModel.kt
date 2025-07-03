package com.moare.android.features.search.display.kbo.viewmodel

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseTeamStandingsViewModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class KBOTeamStandingsIntent {
    data class InitData(val displayModel: KBOTeamStandingsDisplayModel) : KBOTeamStandingsIntent()
    data class SelectCategory(val index: Int) : KBOTeamStandingsIntent()
}

@HiltViewModel
class KBOTeamStandingsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseTeamStandingsViewModel<KBOTeamStandingsIntent, KBOTeamStandingsDisplayModel>(nameProvider) {
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
    private var _standings = MutableStateFlow<List<KBOTeamStandingsDisplay>>(emptyList())
    val standings: StateFlow<List<KBOTeamStandingsDisplay>> = _standings

    override fun send(intent: KBOTeamStandingsIntent) {
        viewModelScope.launch {
            when (intent) {
                is KBOTeamStandingsIntent.InitData -> initData(intent.displayModel)
                is KBOTeamStandingsIntent.SelectCategory -> selectCategory(intent.index)
            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: KBOTeamStandingsDisplayModel) {
        super.initData(displayModel)

        // init with default value
        _selectedCategoryIndex.value = 1 // defalue category is "승률"
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
            0 -> standings.sortBy { it.stats.rankData.gb.toFloatOrNull() }
            1 -> standings.sortByDescending { it.stats.rankData.winpct.toFloatOrNull() }
            2 -> standings.sortByDescending { it.stats.rankData.wins.toIntOrNull() }
            3 -> standings.sortBy { it.stats.rankData.losses.toIntOrNull() }
            4 -> standings.sortByDescending { it.stats.rankData.gp.toIntOrNull() }
            5 -> standings.sortByDescending { it.stats.rankData.streak.take(1).toIntOrNull() }
            6 -> standings.sortByDescending { it.stats.hitterData.avg.toFloatOrNull() }
            7 -> standings.sortByDescending { it.stats.hitterData.h.toIntOrNull() }
            8 -> standings.sortByDescending { it.stats.hitterData.hr.toIntOrNull() }
            9 -> standings.sortByDescending { it.stats.hitterData.slg.toFloatOrNull() }
            10 -> standings.sortByDescending { it.stats.hitterData.r.toIntOrNull() }
            11 -> standings.sortBy { it.stats.pitcherData.er.toFloatOrNull() }
            12 -> standings.sortBy { it.stats.pitcherData.avg.toFloatOrNull() }
            13 -> standings.sortBy { it.stats.pitcherData.h.toIntOrNull() }
            14 -> standings.sortBy { it.stats.pitcherData.hr.toIntOrNull() }
            15 -> standings.sortBy { it.stats.pitcherData.r.toIntOrNull() }
            16 -> standings.sortByDescending { it.stats.runnerData.sbPercent.toFloatOrNull() }
        }

        _standings.value = standings
    }
}