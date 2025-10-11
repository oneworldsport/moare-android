package com.moare.android.features.search.display.football.viewmodel

import androidx.compose.ui.unit.dp
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoStore
import com.moare.android.features.search.display.search.viewmodel.SearchDelegate
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.responsemodels.football.FBPlayerInfoResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

sealed interface FBPlayerInfoAction {
    data object InitData : FBPlayerInfoAction
    data object ShowPlayerStats : FBPlayerInfoAction
}

sealed interface FBPlayerInfoDelegate {
    data class ShowPlayerStats(val model: SportDecodableModel) : FBPlayerInfoDelegate
}

class FBPlayerInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: SportDecodableModel.FBPlayerInfo,
    @Assisted val emitToParent: (FBPlayerInfoDelegate) -> Unit
) : BaseInfoStore<FBPlayerInfoAction, FBPlayerInfoResponseModel, FBPlayerInfoDisplayModel>(
    model.responseModel, model.displayModel, nameProvider
) {
    val itemHeight = 30.dp

    @AssistedFactory
    interface Factory {
        fun create(
            model: SportDecodableModel.FBPlayerInfo,
            emitToParent: (FBPlayerInfoDelegate) -> Unit
        ) : FBPlayerInfoStore
    }

    override fun send(action: FBPlayerInfoAction) {
        when (action) {
            is FBPlayerInfoAction.InitData -> initData()
            is FBPlayerInfoAction.ShowPlayerStats -> showPlayerStats()
        }
    }

    private fun showPlayerStats() {
        val dataModel = SportDecodableModel.FBPlayerStats(
            responseModel = responseModel,
            displayModel = ModelConverter.fbPlayerStatsConverter(responseModel)
        )

        emitToParent(FBPlayerInfoDelegate.ShowPlayerStats(dataModel))
    }
}