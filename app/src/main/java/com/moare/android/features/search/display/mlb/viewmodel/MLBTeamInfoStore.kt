package com.moare.android.features.search.display.mlb.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoStore
import com.moare.android.features.search.display.football.viewmodel.FBTeamInfoAction
import com.moare.android.features.search.display.football.viewmodel.FBTeamInfoDelegate
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamInfoDisplayModel
import com.moare.android.features.search.models.responsemodels.mlb.MLBTeamInfoResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

sealed interface MLBTeamInfoAction {
    data object InitData : MLBTeamInfoAction
    data object ShowTeamStats : MLBTeamInfoAction
}

sealed interface MLBTeamInfoDelegate {
    data class ShowTeamStats(val model: SportDecodableModel) : MLBTeamInfoDelegate
}

class MLBTeamInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: SportDecodableModel.MLBTeamInfo,
    @Assisted val emitToParent: (MLBTeamInfoDelegate) -> Unit
) : BaseInfoStore<MLBTeamInfoAction, MLBTeamInfoResponseModel, MLBTeamInfoDisplayModel>(
    model.responseModel, model.displayModel, nameProvider
) {

    @AssistedFactory
    interface Factory {
        fun create(
            model: SportDecodableModel.MLBTeamInfo,
            emitToParent: (MLBTeamInfoDelegate) -> Unit
        ) : MLBTeamInfoStore
    }

    override fun send(action: MLBTeamInfoAction) {
        when (action) {
            is MLBTeamInfoAction.InitData -> initData()
            is MLBTeamInfoAction.ShowTeamStats -> showTeamStats()
        }
    }

    private fun showTeamStats() {
        val dataModel = SportDecodableModel.MLBTeamStats(
            responseModel = responseModel,
            displayModel = ModelConverter.mlbTeamStatsConverter(responseModel)
        )

        emitToParent(MLBTeamInfoDelegate.ShowTeamStats(dataModel))
    }
}