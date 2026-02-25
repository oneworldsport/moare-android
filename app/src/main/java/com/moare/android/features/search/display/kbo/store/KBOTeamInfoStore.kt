package com.moare.android.features.search.display.kbo.store

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.store.BaseInfoStore
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamInfoDisplayModel
import com.moare.android.features.search.models.responsemodels.kbo.KBOGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.kbo.KBOTeamInfoResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject


sealed interface KBOTeamInfoAction {
    data object InitData : KBOTeamInfoAction
    data object ShowTeamStats : KBOTeamInfoAction
    data class ShowGameStats(val isPrevious: Boolean = true) : KBOTeamInfoAction
}

sealed interface KBOTeamInfoDelegate {
    data class ShowTeamStats(val model: SportDecodableModel.KBOTeamStats) : KBOTeamInfoDelegate
    data class ShowGameStats(val model: SportDecodableModel.KBOGameStats) : KBOTeamInfoDelegate
}

class KBOTeamInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: SportDecodableModel.KBOTeamInfo,
    @Assisted val emitToParent: (KBOTeamInfoDelegate) -> Unit
) : BaseInfoStore<KBOTeamInfoAction, KBOTeamInfoResponseModel, KBOTeamInfoDisplayModel>(
    model.responseModel, model.displayModel, nameProvider
) {

    @AssistedFactory
    interface Factory {
        fun create(
            model: SportDecodableModel.KBOTeamInfo,
            emitToParent: (KBOTeamInfoDelegate) -> Unit
        ) : KBOTeamInfoStore
    }

    override fun send(action: KBOTeamInfoAction) {
        when (action) {
            is KBOTeamInfoAction.InitData -> initData()
            is KBOTeamInfoAction.ShowTeamStats -> showTeamStats()
            is KBOTeamInfoAction.ShowGameStats -> showGameStats(action.isPrevious)
        }
    }

    private fun showTeamStats() {
        val dataModel = SportDecodableModel.KBOTeamStats(
            responseModel = responseModel,
            displayModel = ModelConverter.kboTeamStatsConverter(responseModel)
        )

        emitToParent(KBOTeamInfoDelegate.ShowTeamStats(dataModel))
    }

    private fun showGameStats(isPrevious: Boolean) {
        val responseModel = if (isPrevious) KBOGameStatsResponseModel(responseModel.lastGame) else KBOGameStatsResponseModel(responseModel.nextGame)

        val dataModel = SportDecodableModel.KBOGameStats(
            responseModel = responseModel,
            displayModel = ModelConverter.kboGameStatsConverter(responseModel)
        )

        emitToParent(KBOTeamInfoDelegate.ShowGameStats(dataModel))
    }
}