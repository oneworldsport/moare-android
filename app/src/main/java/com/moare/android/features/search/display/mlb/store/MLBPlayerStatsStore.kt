package com.moare.android.features.search.display.mlb.store

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.store.BaseStatsStore
import com.moare.android.features.search.models.displaymodels.mlb.MLBPlayerStatsDisplayModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

sealed interface MLBPlayerStatsAction {
    data object InitData : MLBPlayerStatsAction
}

class MLBPlayerStatsStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: MLBPlayerStatsDisplayModel
) : BaseStatsStore<MLBPlayerStatsAction, MLBPlayerStatsDisplayModel>(initial, nameProvider) {

    @AssistedFactory
    interface Factory {
        fun create(displayModel: MLBPlayerStatsDisplayModel) : MLBPlayerStatsStore
    }

    override fun send(action: MLBPlayerStatsAction) {
        when (action) {
            is MLBPlayerStatsAction.InitData -> initData()
        }
    }
}