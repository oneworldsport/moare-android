package com.moare.android.features.search.display.mlb.viewmodel

import androidx.compose.ui.unit.dp
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseGameStatsStore
import com.moare.android.features.search.models.displaymodels.mlb.MLBGameStatsDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBGameBoxscoreTeamData
import com.moare.android.features.search.models.models.mlb.MLBGameBoxscoreTeamPlayer
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface MLBGameStatsAction {
    data object InitData : MLBGameStatsAction
    data class SelectFirstCategory(val index: Int) : MLBGameStatsAction
    data class SelectSecondCategory(val index: Int) : MLBGameStatsAction
    data class SelectTeam(val index: Int) : MLBGameStatsAction
}

class MLBGameStatsStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: MLBGameStatsDisplayModel
) : BaseGameStatsStore<MLBGameStatsAction, MLBGameStatsDisplayModel>(initial, nameProvider) {
    val lineScoreItemHeight = 50.dp
    val teamButtonWidth = 100.dp

    private val _teamBoxScore = MutableStateFlow<MLBGameBoxscoreTeamData?>(null)
    val teamBoxScore: StateFlow<MLBGameBoxscoreTeamData?> = _teamBoxScore

    private val _teamHitters = MutableStateFlow<List<Pair<String, MLBGameBoxscoreTeamPlayer>>>(emptyList())
    val teamHitters: StateFlow<List<Pair<String, MLBGameBoxscoreTeamPlayer>>> = _teamHitters

    private val _teamPitchers = MutableStateFlow<List<Pair<String, MLBGameBoxscoreTeamPlayer>>>(emptyList())
    val teamPitchers: StateFlow<List<Pair<String, MLBGameBoxscoreTeamPlayer>>> = _teamPitchers

    @AssistedFactory
    interface Factory {
        fun create(displayModel: MLBGameStatsDisplayModel) : MLBGameStatsStore
    }

    override fun send(action: MLBGameStatsAction) {
        when (action) {
            is MLBGameStatsAction.InitData -> initData()
            is MLBGameStatsAction.SelectFirstCategory -> selectFirstCategory(action.index)
            is MLBGameStatsAction.SelectSecondCategory -> selectSecondCategory(action.index)
            is MLBGameStatsAction.SelectTeam -> selectTeam(action.index)
        }
    }

    override fun initData() {
        super.initData()

        // init with default value
        _teamBoxScore.value = null
        _teamHitters.value = emptyList()
        _teamPitchers.value = emptyList()

        selectTeam(0)
    }

    override fun selectTeam(index: Int) {
        super.selectTeam(index)

        // set selected team's players stats
        _teamBoxScore.value = if (index == 0) {
            displayModel.value.game.boxscore?.teams?.home
        } else {
            displayModel.value.game.boxscore?.teams?.away
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

        when (firstCategorySelectedIndex.value) {
            0 -> teamHitters.sortByDescending { it.second.stats?.batting?.atBats ?: 0 }
            1 -> teamHitters.sortByDescending { it.second.stats?.batting?.hits ?: 0 }
            2 -> teamHitters.sortByDescending { it.second.stats?.batting?.homeRuns ?: 0 }
            3 -> teamHitters.sortByDescending { it.second.stats?.batting?.rbi ?: 0 }
            4 -> teamHitters.sortByDescending { it.second.stats?.batting?.runs ?: 0 }
            5 -> teamHitters.sortByDescending { it.second.stats?.batting?.stolenBases ?: 0 }
            6 -> teamHitters.sortByDescending { it.second.stats?.batting?.baseOnBalls ?: 0 }
            7 -> teamHitters.sortByDescending { it.second.stats?.batting?.strikeOuts ?: 0 }
            else -> {}
        }

        _teamHitters.value = teamHitters
    }

    private fun sortPitchers() {
        val teamPitchers = teamPitchers.value.toMutableList()

        when (secondCategorySelectedIndex.value) {
            0 -> teamPitchers.sortByDescending { it.second.stats?.pitching?.inningsPitched?.toDoubleOrNull() ?: 0.0 }
            1 -> teamPitchers.sortByDescending { it.second.stats?.pitching?.runs ?: 0 }
            2 -> teamPitchers.sortByDescending { it.second.stats?.pitching?.earnedRuns ?: 0 }
            3 -> teamPitchers.sortByDescending { it.second.stats?.pitching?.baseOnBalls ?: 0 }
            4 -> teamPitchers.sortByDescending { it.second.stats?.pitching?.strikeOuts ?: 0 }
            5 -> teamPitchers.sortByDescending { it.second.stats?.pitching?.hits ?: 0 }
            else -> {}
        }

        _teamPitchers.value = teamPitchers
    }
}