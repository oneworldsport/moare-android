package com.moare.android.features.search.display.nba.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseStatsStore
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStatsDisplayModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

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