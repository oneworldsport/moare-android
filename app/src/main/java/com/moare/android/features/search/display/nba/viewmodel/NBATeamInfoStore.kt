package com.moare.android.features.search.display.nba.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoStore
import com.moare.android.features.search.display.football.viewmodel.FBTeamInfoAction
import com.moare.android.features.search.display.football.viewmodel.FBTeamInfoDelegate
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamInfoDisplayModel
import com.moare.android.features.search.models.responsemodels.nba.NBATeamInfoResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

sealed interface NBATeamInfoAction {
    data object InitData : NBATeamInfoAction
    data object ShowTeamStats : NBATeamInfoAction
}

sealed interface NBATeamInfoDelegate {
    data class ShowTeamStats(val model: SportDecodableModel) : NBATeamInfoDelegate
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
        }
    }

    private fun showTeamStats() {
        val dataModel = SportDecodableModel.NBATeamStats(
            responseModel = responseModel,
            displayModel = ModelConverter.nbaTeamStatsConverter(responseModel)
        )

        emitToParent(NBATeamInfoDelegate.ShowTeamStats(dataModel))
    }
}