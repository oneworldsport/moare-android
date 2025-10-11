package com.moare.android.features.search.display.kbo.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoStore
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoAction
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoDelegate
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerInfoDisplayModel
import com.moare.android.features.search.models.responsemodels.kbo.KBOPlayerInfoResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject


sealed interface KBOPlayerInfoAction {
    data object InitData : KBOPlayerInfoAction
    data object ShowPlayerStats : KBOPlayerInfoAction
}

sealed interface KBOPlayerInfoDelegate {
    data class ShowPlayerStats(val model: SportDecodableModel) : KBOPlayerInfoDelegate
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

    override fun send(aciton: KBOPlayerInfoAction) {
        when (aciton) {
            is KBOPlayerInfoAction.InitData -> initData()
            is KBOPlayerInfoAction.ShowPlayerStats -> showPlayerStats()
        }
    }

    private fun showPlayerStats() {
        val dataModel: SportDecodableModel = SportDecodableModel.KBOPlayerStats(
            responseModel = responseModel,
            displayModel = ModelConverter.kboPlayerStatsConverter(responseModel)
        )

        emitToParent(KBOPlayerInfoDelegate.ShowPlayerStats(dataModel))
    }
}