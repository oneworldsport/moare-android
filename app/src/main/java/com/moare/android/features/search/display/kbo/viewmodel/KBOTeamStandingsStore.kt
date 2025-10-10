package com.moare.android.features.search.display.kbo.viewmodel

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseTeamStandingsStore
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStandingsDisplayModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface KBOTeamStandingsAction {
    data object InitData : KBOTeamStandingsAction
    data class SelectCategory(val index: Int) : KBOTeamStandingsAction
}

class KBOTeamStandingsStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: KBOTeamStandingsDisplayModel
) : BaseTeamStandingsStore<KBOTeamStandingsAction, KBOTeamStandingsDisplayModel>(initial, nameProvider) {
    val dataItemHeight = 40.dp
    val categoryItemHeight = 44.dp
    val firstCategoryItemWidth = 132.dp
    val dataItemWidth = 50.dp
    val categoryFontSize = 15.sp
    val dataFontSize = 15.sp

    private var _standings = MutableStateFlow<List<KBOTeamStandingsDisplay>>(emptyList())
    val standings: StateFlow<List<KBOTeamStandingsDisplay>> = _standings

    @AssistedFactory
    interface Factory {
        fun create(displayModel: KBOTeamStandingsDisplayModel) : KBOTeamStandingsStore
    }

    override fun send(action: KBOTeamStandingsAction) {
        scope.launch {
            when (action) {
                is KBOTeamStandingsAction.InitData -> initData()
                is KBOTeamStandingsAction.SelectCategory -> selectCategory(action.index)
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
        _standings.value = displayModel.value.standings

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
        val standings = standings.value.toMutableList()

        when (categorySelectedIndex.value) {
            0 -> standings.sortBy { it.stats.rankData.gb.toFloatOrNull() }
            1 -> standings.sortByDescending { it.stats.rankData.winpct.toFloatOrNull() }
            2 -> standings.sortByDescending { it.stats.rankData.wins.toIntOrNull() }
            3 -> standings.sortBy { it.stats.rankData.losses.toIntOrNull() }
            4 -> standings.sortByDescending { it.stats.rankData.gp.toIntOrNull() }
            5 -> standings.sortByDescending {
                // 승은 * 1, 패는 * (-1)을 해서 정렬
                val streak = it.stats.rankData.streak
                val num = streak.take(1).toIntOrNull() ?: 0
                val sign = if (streak.contains("승")) 1 else -1
                num * sign
            }
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