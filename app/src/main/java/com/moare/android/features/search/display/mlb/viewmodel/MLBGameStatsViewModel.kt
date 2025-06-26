package com.moare.android.features.search.display.mlb.viewmodel

import androidx.compose.ui.unit.dp
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseGameStatsViewModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBGameStatsDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBGameBoxscoreTeamData
import com.moare.android.features.search.models.models.mlb.MLBGameBoxscoreTeamPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed class MLBGameStatsIntent {
    data class InitData(val displayModel: MLBGameStatsDisplayModel) : MLBGameStatsIntent()
    data class SelectFirstCategory(val index: Int) : MLBGameStatsIntent()
    data class SelectSecondCategory(val index: Int) : MLBGameStatsIntent()
    data class SelectTeam(val index: Int) : MLBGameStatsIntent()
}

@HiltViewModel
class MLBGameStatsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseGameStatsViewModel<MLBGameStatsIntent, MLBGameStatsDisplayModel>(nameProvider) {
    /* ---------------------
       constants
       --------------------- */
    val lineScoreItemHeight = 50.dp
    val teamButtonWidth = 100.dp

    /* ---------------------
       data state
       --------------------- */
    private val _teamBoxScore = MutableStateFlow<MLBGameBoxscoreTeamData?>(null)
    val teamBoxScore: StateFlow<MLBGameBoxscoreTeamData?> = _teamBoxScore

    private val _teamHitters = MutableStateFlow<List<Pair<String, MLBGameBoxscoreTeamPlayer>>>(emptyList())
    val teamHitters: StateFlow<List<Pair<String, MLBGameBoxscoreTeamPlayer>>> = _teamHitters

    private val _teamPitchers = MutableStateFlow<List<Pair<String, MLBGameBoxscoreTeamPlayer>>>(emptyList())
    val teamPitchers: StateFlow<List<Pair<String, MLBGameBoxscoreTeamPlayer>>> = _teamPitchers

    override fun send(intent: MLBGameStatsIntent) {
        when (intent) {
            is MLBGameStatsIntent.InitData -> initData(intent.displayModel)
            is MLBGameStatsIntent.SelectFirstCategory -> selectFirstCategory(intent.index)
            is MLBGameStatsIntent.SelectSecondCategory -> selectSecondCategory(intent.index)
            is MLBGameStatsIntent.SelectTeam -> selectTeam(intent.index)
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: MLBGameStatsDisplayModel) {
        super.initData(displayModel)

        // init with default value
        _teamBoxScore.value = null
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
        _teamBoxScore.value = if (index == 0) {
            displayModel.value?.game?.boxscore?.teams?.home
        } else {
            displayModel.value?.game?.boxscore?.teams?.away
        }

        _teamHitters.value = teamBoxScore.value?.players?.filter {
            it.value.position?.abbreviation != "P" && it.value.battingOrder.isNotEmpty()
        }?.map { (it.key to it.value) } ?: emptyList()

        _teamPitchers.value = teamBoxScore.value?.players?.filter {
            it.value.position?.abbreviation == "P" && it.value.allPositions.isNotEmpty()
        }?.map { (it.key to it.value) } ?: emptyList()

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
            0 -> teamHitters.sortedByDescending { it.second.stats?.batting?.atBats ?: 0 }
            1 -> teamHitters.sortedByDescending { it.second.stats?.batting?.hits ?: 0 }
            2 -> teamHitters.sortedByDescending { it.second.stats?.batting?.homeRuns ?: 0 }
            3 -> teamHitters.sortedByDescending { it.second.stats?.batting?.rbi ?: 0 }
            4 -> teamHitters.sortedByDescending { it.second.stats?.batting?.runs ?: 0 }
            5 -> teamHitters.sortedByDescending { it.second.stats?.batting?.stolenBases ?: 0 }
            6 -> teamHitters.sortedByDescending { it.second.stats?.batting?.baseOnBalls ?: 0 }
            7 -> teamHitters.sortedByDescending { it.second.stats?.batting?.strikeOuts ?: 0 }
            8 -> teamHitters.sortedByDescending { it.second.stats?.batting?.avg?.toDoubleOrNull() ?: 0.0 }
            else -> {}
        }

        _teamHitters.value = teamHitters
    }

    private fun sortPitchers() {
        val teamPitchers = teamPitchers.value.toMutableList()

        when (secondCategorySelectedIndex.value) {
            0 -> teamPitchers.sortedByDescending { it.second.stats?.pitching?.inningsPitched?.toDoubleOrNull() ?: 0.0 }
            1 -> teamPitchers.sortedByDescending { it.second.stats?.pitching?.runs ?: 0 }
            2 -> teamPitchers.sortedByDescending { it.second.stats?.pitching?.earnedRuns ?: 0 }
            3 -> teamPitchers.sortedByDescending { it.second.stats?.pitching?.baseOnBalls ?: 0 }
            4 -> teamPitchers.sortedByDescending { it.second.stats?.pitching?.strikeOuts ?: 0 }
            5 -> teamPitchers.sortedByDescending { it.second.stats?.pitching?.hits ?: 0 }
            6 -> teamPitchers.sortedBy { it.second.stats?.pitching?.era?.toDoubleOrNull() ?: 0.0 }
            else -> {}
        }

        _teamPitchers.value = teamPitchers
    }
}