package com.moare.android.features.search.display.nba.store

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.store.BaseStatsStore
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStatsDisplayModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

sealed interface NBATeamStatsAction {
    data object InitData : NBATeamStatsAction
}

class NBATeamStatsStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: NBATeamStatsDisplayModel
) : BaseStatsStore<NBATeamStatsAction, NBATeamStatsDisplayModel>(initial, nameProvider) {

    @AssistedFactory
    interface Factory {
        fun create(displayModel: NBATeamStatsDisplayModel) : NBATeamStatsStore
    }

    override fun send(action: NBATeamStatsAction) {
        when (action) {
            is NBATeamStatsAction.InitData -> initData()
        }
    }
}