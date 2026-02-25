package com.moare.android.features.search.display.tennis.store

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.rounded
import com.moare.android.features.search.display.common.store.BaseGameStatsStore
import com.moare.android.features.search.display.nba.store.NBAGameStatsAction
import com.moare.android.features.search.display.nba.store.NBAGameStatsDelegate
import com.moare.android.features.search.display.nba.store.NBAGameStatsStore
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.tennis.TennisGameStatsDisplayModel
import com.moare.android.features.search.models.models.nba.NBABoxScoreTeamPlayer
import com.moare.android.features.search.models.models.nba.NBAGameBoxScoreStats
import com.moare.android.features.search.models.models.nba.NBALineScore
import com.moare.android.features.search.models.responsemodels.nba.NBAGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.tennis.TennisGameStatsResponseModel
import com.moare.android.features.search.networking.SearchClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface TennisGameStatsAction {
    data object InitData : TennisGameStatsAction
    data class SelectFirstCategory(val index: Int) : TennisGameStatsAction
    data class SelectTeam(val index: Int) : TennisGameStatsAction
    data class RefreshGame(val shouldFetch: Boolean = true) : TennisGameStatsAction
}

sealed interface TennisGameStatsDelegate {
    data class RefreshGame(val model: SportDecodableModel.TennisGameStats) : TennisGameStatsDelegate
}

class TennisGameStatsStore @AssistedInject constructor(
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: TennisGameStatsDisplayModel,
    @Assisted val emitToParent: (TennisGameStatsDelegate) -> Unit
) : BaseGameStatsStore<TennisGameStatsAction, TennisGameStatsDisplayModel>(model, nameProvider) {
    var isDoubles = false

    @AssistedFactory
    interface Factory {
        fun create(
            model: TennisGameStatsDisplayModel,
            emitToParent: (TennisGameStatsDelegate) -> Unit
        ) : TennisGameStatsStore
    }

    override fun send(action: TennisGameStatsAction) {
        when (action) {
            is TennisGameStatsAction.InitData -> initData()
            is TennisGameStatsAction.SelectFirstCategory -> selectFirstCategory(action.index)
            is TennisGameStatsAction.SelectTeam -> selectTeam(false, action.index)
            is TennisGameStatsAction.RefreshGame -> refreshGame(action.shouldFetch)
        }
    }

    override fun initData() {
        super.initData()

        // init with default value
        isDoubles = false

        val tournament = displayModel.value.game.gameInfo.tournament
        if (tournament?.slug?.contains("doubles") == true) {
            isDoubles = true
        }
    }

    private fun refreshGame(shouldFetch: Boolean) {
        if (shouldFetch) {
            scope.launch {
                _isRefreshing.value = true

                val displayModel = displayModel.value
                val gameInfo = displayModel.game.gameInfo
                val result = searchClient.fetchById(
                    season = displayModel.season,
                    category = "tennis",
                    date = gameInfo.gameDate,
                    dataType = "tennis_game_stats",
                    leagueId = displayModel.leagueId,
                    id = gameInfo.id.toString()
                )

                if (result.data is SportDecodableModel.TennisGameStats) {
                    _displayModel.value = result.data.displayModel
                    initData()
                    emitToParent(TennisGameStatsDelegate.RefreshGame(result.data))
                }

                _isRefreshing.value = false
            }
        } else {
            val responseModel = TennisGameStatsResponseModel(game = displayModel.value.game)
            val dataModel = SportDecodableModel.TennisGameStats(responseModel, displayModel.value)

            emitToParent(TennisGameStatsDelegate.RefreshGame(dataModel))
        }
    }
}