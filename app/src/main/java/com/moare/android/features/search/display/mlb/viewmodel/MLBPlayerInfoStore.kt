package com.moare.android.features.search.display.mlb.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoStore
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoAction
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoDelegate
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBPlayerInfoDisplayModel
import com.moare.android.features.search.models.responsemodels.mlb.MLBPlayerInfoResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject


sealed interface MLBPlayerInfoAction {
    data object InitData : MLBPlayerInfoAction
    data object ShowPlayerStats : MLBPlayerInfoAction
}

sealed interface MLBPlayerInfoDelegate {
    data class ShowPlayerStats(val model: SportDecodableModel) : MLBPlayerInfoDelegate
}

class MLBPlayerInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: SportDecodableModel.MLBPlayerInfo,
    @Assisted val emitToParent: (MLBPlayerInfoDelegate) -> Unit
) : BaseInfoStore<MLBPlayerInfoAction, MLBPlayerInfoResponseModel, MLBPlayerInfoDisplayModel>(
    model.responseModel, model.displayModel, nameProvider
) {

    @AssistedFactory
    interface Factory {
        fun create(
            model: SportDecodableModel.MLBPlayerInfo,
            emitToParent: (MLBPlayerInfoDelegate) -> Unit
        ) : MLBPlayerInfoStore
    }

    override fun send(action: MLBPlayerInfoAction) {
        when (action) {
            is MLBPlayerInfoAction.InitData -> initData()
            is MLBPlayerInfoAction.ShowPlayerStats -> showPlayerStats()
        }
    }

    private fun showPlayerStats() {
        val dataModel = SportDecodableModel.MLBPlayerStats(
            responseModel = responseModel,
            displayModel = ModelConverter.mlbPlayerStatsConverter(responseModel)
        )

        emitToParent(MLBPlayerInfoDelegate.ShowPlayerStats(dataModel))
    }
}