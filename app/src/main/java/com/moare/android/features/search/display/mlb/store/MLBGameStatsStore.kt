package com.moare.android.features.search.display.mlb.store

import androidx.compose.ui.unit.dp
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.store.BaseGameStatsStore
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBGameStatsDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBGameBoxscoreTeamData
import com.moare.android.features.search.models.models.mlb.MLBGameBoxscoreTeamPlayer
import com.moare.android.features.search.models.responsemodels.mlb.MLBGameStatsResponseModel
import com.moare.android.features.search.data.networking.SearchClient
import com.moare.android.features.search.domain.repository.SearchRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface MLBGameStatsAction {
    data object InitData : MLBGameStatsAction
    data class SelectFirstCategory(val index: Int) : MLBGameStatsAction
    data class SelectSecondCategory(val index: Int) : MLBGameStatsAction
    data class SelectTeam(val index: Int) : MLBGameStatsAction
    data class RefreshGame(val shouldFetch: Boolean = true) : MLBGameStatsAction
    data object SortByBattingOrder : MLBGameStatsAction
    data object SortByPitcherOrder : MLBGameStatsAction
}

sealed interface MLBGameStatsDelegate {
    data class RefreshGame(val model: SportDecodableModel.MLBGameStats) : MLBGameStatsDelegate
}

class MLBGameStatsStore @AssistedInject constructor(
    private val searchRepository: SearchRepository,
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: MLBGameStatsDisplayModel,
    @Assisted val emitToParent: (MLBGameStatsDelegate) -> Unit
) : BaseGameStatsStore<MLBGameStatsAction, MLBGameStatsDisplayModel>(model, nameProvider) {
    val lineScoreItemHeight = 50.dp
    val teamButtonWidth = 100.dp
    val itemWidth = 70.dp

    private val _teamBoxScore = MutableStateFlow<MLBGameBoxscoreTeamData?>(null)
    val teamBoxScore: StateFlow<MLBGameBoxscoreTeamData?> = _teamBoxScore

    private val _teamHitters = MutableStateFlow<List<Pair<String, MLBGameBoxscoreTeamPlayer>>>(emptyList())
    val teamHitters: StateFlow<List<Pair<String, MLBGameBoxscoreTeamPlayer>>> = _teamHitters

    private val _teamPitchers = MutableStateFlow<List<Pair<String, MLBGameBoxscoreTeamPlayer>>>(emptyList())
    val teamPitchers: StateFlow<List<Pair<String, MLBGameBoxscoreTeamPlayer>>> = _teamPitchers

    @AssistedFactory
    interface Factory {
        fun create(
            model: MLBGameStatsDisplayModel,
            emitToParent: (MLBGameStatsDelegate) -> Unit
        ) : MLBGameStatsStore
    }

    override fun send(action: MLBGameStatsAction) {
        when (action) {
            is MLBGameStatsAction.InitData -> initData()
            is MLBGameStatsAction.SelectFirstCategory -> selectFirstCategory(action.index)
            is MLBGameStatsAction.SelectSecondCategory -> selectSecondCategory(action.index)
            is MLBGameStatsAction.SelectTeam -> selectTeam(false, action.index)
            is MLBGameStatsAction.RefreshGame -> refreshGame(action.shouldFetch)
            is MLBGameStatsAction.SortByBattingOrder -> sortByBattingOrder()
            is MLBGameStatsAction.SortByPitcherOrder -> sortByPitcherOrder()
        }
    }

    override fun initData() {
        super.initData()

        // init with default value
        _teamBoxScore.value = null
        _teamHitters.value = emptyList()
        _teamPitchers.value = emptyList()

        selectTeam(true, 0)
    }

    override fun selectTeam(isInit: Boolean, index: Int) {
        super.selectTeam(isInit, index)

        // set selected team's players stats
        _teamBoxScore.value = if (index == 0) {
            displayModel.value.game.boxscore?.teams?.home
        } else {
            displayModel.value.game.boxscore?.teams?.away
        }

        _teamHitters.value = teamBoxScore.value?.players?.filter {
            it.value.battingOrder.isNotEmpty()
        }?.map { (it.key to it.value) } ?: emptyList()

        _teamPitchers.value = teamBoxScore.value?.players?.filter {
            ((it.value.position?.abbreviation == "P") && it.value.allPositions.isNotEmpty()) ||
            (it.value.allPositions.any { position -> position.abbreviation == "P" }) // 투수, 타자 모두 뛴 경우 _allPositions 에 값이 2개 들어감.
        }?.map { (it.key to it.value) } ?: emptyList()

        if (isInit) {
            refreshGame(false)
            sortHitters()
            sortPitchers()
        } else {
            if (firstCategorySelectedIndex.value == -1) {
                sortByBattingOrder()
            }
            if (secondCategorySelectedIndex.value == -1) {
                sortByPitcherOrder()
            }
        }
        sortByBattingOrder()
        sortByPitcherOrder()
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
        }

        _teamPitchers.value = teamPitchers
    }

    private fun refreshGame(shouldFetch: Boolean) {
        if (shouldFetch) {
            scope.launch {
                _isRefreshing.value = true

                val game = displayModel.value.game

                // TODO: Has to add loading
                val result = searchRepository.fetchById(
                    season = displayModel.value.season,
                    category = "baseball",
                    date = game.gameInfo.gameDate,
                    dataType = "baseball_game_stats",
                    leagueId = Constants.Ids.MLB,
                    id = game.game.pk.toString()
                )

                if (result.data is SportDecodableModel.MLBGameStats) {
                    _displayModel.value = result.data.displayModel
                    initData()
                    emitToParent(MLBGameStatsDelegate.RefreshGame(result.data))
                }

                _isRefreshing.value = false
            }
        } else {
            val responseModel = MLBGameStatsResponseModel(game = displayModel.value.game)
            val dataModel = SportDecodableModel.MLBGameStats(responseModel, displayModel.value)

            emitToParent(MLBGameStatsDelegate.RefreshGame(dataModel))
        }
    }

    private fun sortByBattingOrder() {
        _teamHitters.update { it.toMutableList().apply { sortBy { it.second.battingOrder.take(1).toIntOrNull() ?: 0 } } }

        selectFirstCategory(-1)
    }

    private fun sortByPitcherOrder() {
        val pitchersOrder = teamBoxScore.value?.pitchers.orEmpty()

        val orderMap = pitchersOrder.withIndex().associate { it.value to it.index }

        _teamPitchers.value = _teamPitchers.value
            .sortedBy { (id, _) ->
                val numericId = id.filter { it.isDigit() }.toIntOrNull()  // first = "ID621107" 이런 형태라서 isDigit()로 숫자만 추출
                orderMap[numericId] ?: Int.MAX_VALUE
            }

        selectSecondCategory(-1)
    }
}