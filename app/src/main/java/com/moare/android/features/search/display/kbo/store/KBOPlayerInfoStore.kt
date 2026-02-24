package com.moare.android.features.search.display.kbo.store

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.store.BaseInfoStore
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerInfoDisplayModel
import com.moare.android.features.search.models.responsemodels.kbo.KBOGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.kbo.KBOPlayerInfoResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject


sealed interface KBOPlayerInfoAction {
    data object InitData : KBOPlayerInfoAction
    data object ShowPlayerStats : KBOPlayerInfoAction
    data class ShowGameStats(val isPrevious: Boolean = true) : KBOPlayerInfoAction
}

sealed interface KBOPlayerInfoDelegate {
    data class ShowPlayerStats(val model: SportDecodableModel.KBOPlayerStats) : KBOPlayerInfoDelegate
    data class ShowGameStats(val model: SportDecodableModel.KBOGameStats) : KBOPlayerInfoDelegate
}

class KBOPlayerInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: SportDecodableModel.KBOPlayerInfo,
    @Assisted val emitToParent: (KBOPlayerInfoDelegate) -> Unit
) : BaseInfoStore<KBOPlayerInfoAction, KBOPlayerInfoResponseModel, KBOPlayerInfoDisplayModel>(
    model.responseModel, model.displayModel, nameProvider
) {

    @AssistedFactory
    interface Factory {
        fun create(
            model: SportDecodableModel.KBOPlayerInfo,
            emitToParent: (KBOPlayerInfoDelegate) -> Unit
        ) : KBOPlayerInfoStore
    }

    override fun send(action: KBOPlayerInfoAction) {
        when (action) {
            is KBOPlayerInfoAction.InitData -> initData()
            is KBOPlayerInfoAction.ShowPlayerStats -> showPlayerStats()
            is KBOPlayerInfoAction.ShowGameStats -> showGameStats(action.isPrevious)
        }
    }

    private fun showPlayerStats() {
        val dataModel = SportDecodableModel.KBOPlayerStats(
            responseModel = responseModel,
            displayModel = ModelConverter.kboPlayerStatsConverter(responseModel)
        )

        emitToParent(KBOPlayerInfoDelegate.ShowPlayerStats(dataModel))
    }

    private fun showGameStats(isPrevious: Boolean) {
        val responseModel = if (isPrevious) KBOGameStatsResponseModel(responseModel.lastGame) else KBOGameStatsResponseModel(responseModel.nextGame)

        val dataModel = SportDecodableModel.KBOGameStats(
            responseModel = responseModel,
            displayModel = ModelConverter.kboGameStatsConverter(responseModel)
        )

        emitToParent(KBOPlayerInfoDelegate.ShowGameStats(dataModel))
    }
}