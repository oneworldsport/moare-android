package com.moare.android.features.search.display.nba.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoStore
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoAction
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoDelegate
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerInfoDisplayModel
import com.moare.android.features.search.models.responsemodels.nba.NBAPlayerInfoResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

sealed interface NBAPlayerInfoAction {
    data object InitData : NBAPlayerInfoAction
    data object ShowPlayerStats : NBAPlayerInfoAction
}

sealed interface NBAPlayerInfoDelegate {
    data class ShowPlayerStats(val model: SportDecodableModel) : NBAPlayerInfoDelegate
}

class NBAPlayerInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: SportDecodableModel.NBAPlayerInfo,
    @Assisted val emitToParent: (NBAPlayerInfoDelegate) -> Unit
) : BaseInfoStore<NBAPlayerInfoAction, NBAPlayerInfoResponseModel, NBAPlayerInfoDisplayModel>(
    model.responseModel, model.displayModel, nameProvider
) {

    @AssistedFactory
    interface Factory {
        fun create(
            model: SportDecodableModel.NBAPlayerInfo,
            emitToParent: (NBAPlayerInfoDelegate) -> Unit
        ) : NBAPlayerInfoStore
    }

    override fun send(action: NBAPlayerInfoAction) {
        when (action) {
            is NBAPlayerInfoAction.InitData -> initData()
            is NBAPlayerInfoAction.ShowPlayerStats -> showPlayerStats()
        }
    }

    private fun showPlayerStats() {
        val dataModel: SportDecodableModel = SportDecodableModel.NBAPlayerStats(
            responseModel = responseModel,
            displayModel = ModelConverter.nbaPlayerStatsConverter(responseModel)
        )

        emitToParent(NBAPlayerInfoDelegate.ShowPlayerStats(dataModel))
    }
}

















