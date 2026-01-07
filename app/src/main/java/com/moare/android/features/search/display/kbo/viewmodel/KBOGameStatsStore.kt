package com.moare.android.features.search.display.kbo.viewmodel

import androidx.compose.ui.unit.dp
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.store.BaseGameStatsStore
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOGameStatsDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOGameHitterStats
import com.moare.android.features.search.models.models.kbo.KBOGameLineup
import com.moare.android.features.search.models.models.kbo.KBOGamePitcherStats
import com.moare.android.features.search.models.responsemodels.kbo.KBOGameStatsResponseModel
import com.moare.android.features.search.networking.SearchClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface KBOGameStatsAction {
    data object InitData : KBOGameStatsAction
    data class SelectFirstCategory(val index: Int) : KBOGameStatsAction
    data class SelectSecondCategory(val index: Int) : KBOGameStatsAction
    data class SelectTeam(val index: Int) : KBOGameStatsAction
    data class RefreshGame(val shouldFetch: Boolean = true) : KBOGameStatsAction
    data object SortByBattingOrder : KBOGameStatsAction
}

sealed interface KBOGameStatsDelegate {
    data class RefreshGame(val model: SportDecodableModel.KBOGameStats) : KBOGameStatsDelegate
}

class KBOGameStatsStore @AssistedInject constructor(
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: KBOGameStatsDisplayModel,
    @Assisted val emitToParent: (KBOGameStatsDelegate) -> Unit
) : BaseGameStatsStore<KBOGameStatsAction, KBOGameStatsDisplayModel>(model, nameProvider) {
    val lineScoreItemHeight = 50.dp
    val teamButtonWidth = 100.dp

    private val _teamLineup = MutableStateFlow<KBOGameLineup?>(null)
    val teamLineup: StateFlow<KBOGameLineup?> = _teamLineup

    private val _teamHitters = MutableStateFlow<List<KBOGameHitterStats>>(emptyList())
    val teamHitters: StateFlow<List<KBOGameHitterStats>> = _teamHitters

    private val _teamPitchers = MutableStateFlow<List<KBOGamePitcherStats>>(emptyList())
    val teamPitchers: StateFlow<List<KBOGamePitcherStats>> = _teamPitchers

    @AssistedFactory
    interface Factory {
        fun create(
            model: KBOGameStatsDisplayModel,
            emitToParent: (KBOGameStatsDelegate) -> Unit
        ) : KBOGameStatsStore
    }

    override fun send(action: KBOGameStatsAction) {
        when (action) {
            is KBOGameStatsAction.InitData -> initData()
            is KBOGameStatsAction.SelectFirstCategory -> selectFirstCategory(action.index)
            is KBOGameStatsAction.SelectSecondCategory -> selectSecondCategory(action.index)
            is KBOGameStatsAction.SelectTeam -> selectTeam(false, action.index)
            is KBOGameStatsAction.RefreshGame -> refreshGame(action.shouldFetch)
            is KBOGameStatsAction.SortByBattingOrder -> sortByBattingOrder()
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData() {
        super.initData()

        // init with default value
        _teamLineup.value = null
        _teamHitters.value = emptyList()
        _teamPitchers.value = emptyList()

        selectTeam(true, 0)
    }

    /* ---------------------
       implements
       --------------------- */
    override fun selectTeam(isInit:Boolean, index: Int) {
        super.selectTeam(isInit, index)

        // set selected team's players stats
        _teamLineup.value = if (index == 0) {
            displayModel.value.game.lineup?.home
        } else {
            displayModel.value.game.lineup?.away
        }

        _teamHitters.value = teamLineup.value?.hitters ?: emptyList()
        _teamPitchers.value = teamLineup.value?.pitchers ?: emptyList()

        if (isInit) {
            sortByBattingOrder()
            refreshGame(false)
        } else {
            sortHitters()
        }
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

        when (secondCategorySelectedIndex.value) {
            0 -> teamHitters.sortByDescending { it.ab }
            1 -> teamHitters.sortByDescending { it.h }
            2 -> teamHitters.sortByDescending { it.homeRuns }
            3 -> teamHitters.sortByDescending { it.rbi }
            4 -> teamHitters.sortByDescending { it.r }
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
            0 -> teamPitchers.sortByDescending { it.inningsPitched }
            1 -> teamPitchers.sortByDescending { it.r.toDoubleOrNull() ?: 0.0 }
            2 -> teamPitchers.sortByDescending { it.er.toDoubleOrNull() ?: 0.0 }
            3 -> teamPitchers.sortByDescending { it.bb.toDoubleOrNull() ?: 0.0 }
            4 -> teamPitchers.sortByDescending { it.so.toDoubleOrNull() ?: 0.0 }
            5 -> teamPitchers.sortByDescending { it.h.toDoubleOrNull() ?: 0.0 }
            else -> {}
        }

        _teamPitchers.value = teamPitchers
    }

    private fun refreshGame(shouldFetch: Boolean) {
        if (shouldFetch) {
            _isRefreshing.value = true
            scope.launch {
                val game = displayModel.value.game
                val gameInfo = game.gameInfo

                gameInfo?.let {
                    // TODO: Has to add loading
                    val result = searchClient.fetchById(
                        season = displayModel.value.season,
                        category = "baseball",
                        date = gameInfo.date,
                        dataType = "baseball_game_stats",
                        leagueId = Constants.Ids.KBO,
                        id = gameInfo.gameId
                    )

                    if (result.data is SportDecodableModel.KBOGameStats) {
                        _displayModel.value = result.data.displayModel
                        initData()
                        emitToParent(KBOGameStatsDelegate.RefreshGame(result.data))
                    }
                }

                _isRefreshing.value = false
            }
        } else {
            val responseModel = KBOGameStatsResponseModel(game = displayModel.value.game)
            val dataModel = SportDecodableModel.KBOGameStats(responseModel, displayModel.value)

            emitToParent(KBOGameStatsDelegate.RefreshGame(dataModel))
        }
    }

    private fun sortByBattingOrder() {
        _teamHitters.update { it.toMutableList().apply { sortBy { it.battingNumber } } }
    }
}






















