package com.moare.android.features.search.display.kbo.viewmodel

import androidx.compose.ui.unit.dp
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseGameStatsViewModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOGameStatsDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOGameHitterStats
import com.moare.android.features.search.models.models.kbo.KBOGameLineup
import com.moare.android.features.search.models.models.kbo.KBOGamePitcherStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed class KBOGameStatsIntent {
    data class InitData(val displayModel: KBOGameStatsDisplayModel) : KBOGameStatsIntent()
    data class SelectFirstCategory(val index: Int) : KBOGameStatsIntent()
    data class SelectSecondCategory(val index: Int) : KBOGameStatsIntent()
    data class SelectTeam(val index: Int) : KBOGameStatsIntent()
}

@HiltViewModel
class KBOGameStatsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseGameStatsViewModel<KBOGameStatsIntent, KBOGameStatsDisplayModel>(nameProvider) {
    /* ---------------------
       constants
       --------------------- */
    val lineScoreItemHeight = 50.dp
    val teamButtonWidth = 100.dp

    /* ---------------------
       data state
       --------------------- */
    private val _teamLineup = MutableStateFlow<KBOGameLineup?>(null)
    val teamLineup: StateFlow<KBOGameLineup?> = _teamLineup

    private val _teamHitters = MutableStateFlow<List<KBOGameHitterStats>>(emptyList())
    val teamHitters: StateFlow<List<KBOGameHitterStats>> = _teamHitters

    private val _teamPitchers = MutableStateFlow<List<KBOGamePitcherStats>>(emptyList())
    val teamPitchers: StateFlow<List<KBOGamePitcherStats>> = _teamPitchers

    override fun send(intent: KBOGameStatsIntent) {
        when (intent) {
            is KBOGameStatsIntent.InitData -> initData(intent.displayModel)
            is KBOGameStatsIntent.SelectFirstCategory -> selectFirstCategory(intent.index)
            is KBOGameStatsIntent.SelectSecondCategory -> selectSecondCategory(intent.index)
            is KBOGameStatsIntent.SelectTeam -> selectTeam(intent.index)
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: KBOGameStatsDisplayModel) {
        super.initData(displayModel)

        // init with default value
        _teamLineup.value = null
        _teamHitters.value = emptyList()
        _teamPitchers.value = emptyList()

        selectTeam(0)
    }

    /* ---------------------
       implements
       --------------------- */
    override fun selectTeam(index: Int) {
        super.selectTeam(index)

        // set selected team's players stats
        _teamLineup.value = if (index == 0) {
            displayModel.value?.game?.lineup?.home
        } else {
            displayModel.value?.game?.lineup?.away
        }

        _teamHitters.value = teamLineup.value?.hitters ?: emptyList()
        _teamPitchers.value = teamLineup.value?.pitchers ?: emptyList()

        sortHitters()
        sortPitchers()
    }

    override fun selectFirstCategory(index: Int) {
        super.selectFirstCategory(index)
        _firstCategorySelectedIndex.value = index

        sortHitters()
    }

    override fun selectSecondCategory(index: Int) {
        super.selectSecondCategory(index)

        sortPitchers()
    }

    override fun sortPlayers() {
    }

    override fun setPlayersTotalStats() {
    }

    private fun sortHitters() {
        val teamHitters = teamHitters.value.toMutableList()

        when (secondCategorySelectedIndex.value) {
            0 -> teamHitters.sortByDescending { it.ab.toDoubleOrNull() ?: 0.0 }
            1 -> teamHitters.sortByDescending { it.h.toDoubleOrNull() ?: 0.0 }
            2 -> teamHitters.sortByDescending { it.homeRuns }
            3 -> teamHitters.sortByDescending { it.rbi.toDoubleOrNull() ?: 0.0 }
            4 -> teamHitters.sortByDescending { it.r.toDoubleOrNull() ?: 0.0 }
            5 -> teamHitters.sortByDescending { it.baseOnBalls }
            6 -> teamHitters.sortByDescending { it.strikeOuts }
            7 -> teamHitters.sortByDescending { it.groundIntoDoublePlay }
            else -> {}
        }

        _teamHitters.value = teamHitters
    }

    private fun sortPitchers() {
        val teamPitchers = teamPitchers.value.toMutableList()

        when (secondCategorySelectedIndex.value) {
            0 -> teamPitchers.sortByDescending { it.ip.toDoubleOrNull() ?: 0.0 }
            1 -> teamPitchers.sortByDescending { it.r.toDoubleOrNull() ?: 0.0 }
            2 -> teamPitchers.sortByDescending { it.er.toDoubleOrNull() ?: 0.0 }
            3 -> teamPitchers.sortByDescending { it.bb.toDoubleOrNull() ?: 0.0 }
            4 -> teamPitchers.sortByDescending { it.so.toDoubleOrNull() ?: 0.0 }
            5 -> teamPitchers.sortByDescending { it.h.toDoubleOrNull() ?: 0.0 }
            else -> {}
        }

        _teamPitchers.value = teamPitchers
    }
}