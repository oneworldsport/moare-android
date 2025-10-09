package com.moare.android.features.search.display.mlb.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoStore
import com.moare.android.features.search.models.displaymodels.mlb.MLBPlayerInfoDisplayModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject


sealed interface MLBPlayerInfoAction {
    data object InitData : MLBPlayerInfoAction
}

class MLBPlayerInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: MLBPlayerInfoDisplayModel
) : BaseInfoStore<MLBPlayerInfoAction, MLBPlayerInfoDisplayModel>(initial, nameProvider) {

    @AssistedFactory
    interface Factory {
        fun create(displayModel: MLBPlayerInfoDisplayModel) : MLBPlayerInfoStore
    }

    override fun send(action: MLBPlayerInfoAction) {
        when (action) {
            is MLBPlayerInfoAction.InitData -> initData()
        }
    }
}