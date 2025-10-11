package com.moare.android.features.search.display.football.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoStore
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamInfoDisplayModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamInfoResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

sealed interface FBTeamInfoAction {
    data object InitData : FBTeamInfoAction
    data object ShowTeamStats : FBTeamInfoAction
}

sealed interface FBTeamInfoDelegate {
    data class ShowTeamStats(val model: SportDecodableModel) : FBTeamInfoDelegate
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
        }
    }

    private fun showTeamStats() {
        val dataModel = SportDecodableModel.FBTeamStats(
            responseModel = responseModel,
            displayModel = ModelConverter.fbTeamStatsConverter(responseModel)
        )

        emitToParent(FBTeamInfoDelegate.ShowTeamStats(dataModel))
    }
}