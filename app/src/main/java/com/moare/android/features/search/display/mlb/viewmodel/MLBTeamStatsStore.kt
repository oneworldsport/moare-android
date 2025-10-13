package com.moare.android.features.search.display.mlb.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.store.BaseStatsStore
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStatsDisplayModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

sealed interface MLBTeamStatsAction {
    data object InitData : MLBTeamStatsAction
}

class MLBTeamStatsStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: MLBTeamStatsDisplayModel
) : BaseStatsStore<MLBTeamStatsAction, MLBTeamStatsDisplayModel>(initial, nameProvider) {

    @AssistedFactory
    interface Factory {
        fun create(displayModel: MLBTeamStatsDisplayModel) : MLBTeamStatsStore
    }

    override fun send(action: MLBTeamStatsAction) {
        when (action) {
            is MLBTeamStatsAction.InitData -> initData()
        }
    }
}