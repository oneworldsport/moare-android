package com.moare.android.features.search.display.mlb.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoStore
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamInfoDisplayModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

sealed interface MLBTeamInfoAction {
    data object InitData : MLBTeamInfoAction
}

class MLBTeamInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: MLBTeamInfoDisplayModel
) : BaseInfoStore<MLBTeamInfoAction, MLBTeamInfoDisplayModel>(initial, nameProvider) {

    @AssistedFactory
    interface Factory {
        fun create(displayModel: MLBTeamInfoDisplayModel) : MLBTeamInfoStore
    }

    override fun send(action: MLBTeamInfoAction) {
        when (action) {
            is MLBTeamInfoAction.InitData -> initData()
        }
    }
}