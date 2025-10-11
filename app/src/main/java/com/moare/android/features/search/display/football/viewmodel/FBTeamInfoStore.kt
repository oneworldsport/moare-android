package com.moare.android.features.search.display.football.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoStore
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamInfoDisplayModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamInfoResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

sealed interface FBTeamInfoAction {
    data object InitData : FBTeamInfoAction
}

class FBTeamInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: SportDecodableModel.FBTeamInfo
) : BaseInfoStore<FBTeamInfoAction, FBTeamInfoResponseModel, FBTeamInfoDisplayModel>(
    model.responseModel, model.displayModel, nameProvider
) {

    @AssistedFactory
    interface Factory {
        fun create(model: SportDecodableModel.FBTeamInfo) : FBTeamInfoStore
    }

    override fun send(action: FBTeamInfoAction) {
        when (action) {
            is FBTeamInfoAction.InitData -> initData()
        }
    }
}