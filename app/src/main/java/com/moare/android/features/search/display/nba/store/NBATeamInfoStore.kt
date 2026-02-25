package com.moare.android.features.search.display.nba.store

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.store.BaseInfoStore
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamInfoDisplayModel
import com.moare.android.features.search.models.responsemodels.nba.NBAGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBATeamInfoResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

sealed interface NBATeamInfoAction {
    data object InitData : NBATeamInfoAction
    data object ShowTeamStats : NBATeamInfoAction
    data class ShowGameStats(val isPrevious: Boolean = true) : NBATeamInfoAction
}

sealed interface NBATeamInfoDelegate {
    data class ShowTeamStats(val model: SportDecodableModel.NBATeamStats) : NBATeamInfoDelegate
    data class ShowGameStats(val model: SportDecodableModel.NBAGameStats) : NBATeamInfoDelegate
}

class NBATeamInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: SportDecodableModel.NBATeamInfo,
    @Assisted val emitToParent: (NBATeamInfoDelegate) -> Unit
) : BaseInfoStore<NBATeamInfoAction, NBATeamInfoResponseModel, NBATeamInfoDisplayModel>(
    model.responseModel, model.displayModel, nameProvider
) {

    @AssistedFactory
    interface Factory {
        fun create(
            model: SportDecodableModel.NBATeamInfo,
            emitToParent: (NBATeamInfoDelegate) -> Unit
        ) : NBATeamInfoStore
    }

    override fun send(action: NBATeamInfoAction) {
        when (action) {
            is NBATeamInfoAction.InitData -> initData()
            is NBATeamInfoAction.ShowTeamStats -> showTeamStats()
            is NBATeamInfoAction.ShowGameStats -> showGameStats(action.isPrevious)
        }
    }

    private fun showTeamStats() {
        val dataModel = SportDecodableModel.NBATeamStats(
            responseModel = responseModel,
            displayModel = ModelConverter.nbaTeamStatsConverter(responseModel)
        )

        emitToParent(NBATeamInfoDelegate.ShowTeamStats(dataModel))
    }

    private fun showGameStats(isPrevious: Boolean) {
        val responseModel = if (isPrevious) NBAGameStatsResponseModel(responseModel.lastGame) else NBAGameStatsResponseModel(responseModel.nextGame)

        val dataModel = SportDecodableModel.NBAGameStats(
            responseModel = responseModel,
            displayModel = ModelConverter.nbaGameStatsConverter(responseModel)
        )

        emitToParent(NBATeamInfoDelegate.ShowGameStats(dataModel))
    }
}