package com.moare.android.features.search.display.football.store

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.store.BaseInfoStore
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamInfoDisplayModel
import com.moare.android.features.search.models.responsemodels.football.FBGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamInfoResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

sealed interface FBTeamInfoAction {
    data object InitData : FBTeamInfoAction
    data object ShowTeamStats : FBTeamInfoAction
    data class ShowGameStats(val isPrevious: Boolean = true) : FBTeamInfoAction
}

sealed interface FBTeamInfoDelegate {
    data class ShowTeamStats(val model: SportDecodableModel.FBTeamStats) : FBTeamInfoDelegate
    data class ShowGameStats(val model: SportDecodableModel.FBGameStats) : FBTeamInfoDelegate
}

class FBTeamInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: SportDecodableModel.FBTeamInfo,
    @Assisted val emitToParent: (FBTeamInfoDelegate) -> Unit
) : BaseInfoStore<FBTeamInfoAction, FBTeamInfoResponseModel, FBTeamInfoDisplayModel>(
    model.responseModel, model.displayModel, nameProvider
) {

    @AssistedFactory
    interface Factory {
        fun create(
            model: SportDecodableModel.FBTeamInfo,
            emitToParent: (FBTeamInfoDelegate) -> Unit
        ) : FBTeamInfoStore
    }

    override fun send(action: FBTeamInfoAction) {
        when (action) {
            is FBTeamInfoAction.InitData -> initData()
            is FBTeamInfoAction.ShowTeamStats -> showTeamStats()
            is FBTeamInfoAction.ShowGameStats -> showGameStats(action.isPrevious)
        }
    }

    private fun showTeamStats() {
        val dataModel = SportDecodableModel.FBTeamStats(
            responseModel = responseModel,
            displayModel = ModelConverter.fbTeamStatsConverter(responseModel)
        )

        emitToParent(FBTeamInfoDelegate.ShowTeamStats(dataModel))
    }

    private fun showGameStats(isPrevious: Boolean) {
        val responseModel = if (isPrevious) FBGameStatsResponseModel(responseModel.lastGame) else FBGameStatsResponseModel(responseModel.nextGame)

        val dataModel = SportDecodableModel.FBGameStats(
            responseModel = responseModel,
            displayModel = ModelConverter.fbGameStatsConverter(responseModel)
        )

        emitToParent(FBTeamInfoDelegate.ShowGameStats(dataModel))
    }
}