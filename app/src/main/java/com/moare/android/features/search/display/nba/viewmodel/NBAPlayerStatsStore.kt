package com.moare.android.features.search.display.nba.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.store.BaseStatsStore
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStatsDisplayModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

sealed interface NBAPlayerStatsAction {
    data object InitData : NBAPlayerStatsAction
}

class NBAPlayerStatsStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: NBAPlayerStatsDisplayModel
) : BaseStatsStore<NBAPlayerStatsAction, NBAPlayerStatsDisplayModel>(initial, nameProvider) {

    @AssistedFactory
    interface Factory {
        fun create(displayModel: NBAPlayerStatsDisplayModel) : NBAPlayerStatsStore
    }

    override fun send(action: NBAPlayerStatsAction) {
        when (action) {
            is NBAPlayerStatsAction.InitData -> initData()
        }
    }
}