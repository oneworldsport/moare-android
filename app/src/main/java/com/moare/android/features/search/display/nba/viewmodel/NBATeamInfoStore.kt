package com.moare.android.features.search.display.nba.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoStore
import com.moare.android.features.search.models.displaymodels.nba.NBATeamInfoDisplayModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

sealed interface NBATeamInfoAction {
    data object InitData : NBATeamInfoAction
}

class NBATeamInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: NBATeamInfoDisplayModel
) : BaseInfoStore<NBATeamInfoAction, NBATeamInfoDisplayModel>(initial, nameProvider) {

    @AssistedFactory
    interface Factory {
        fun create(displayModel: NBATeamInfoDisplayModel) : NBATeamInfoStore
    }

    override fun send(action: NBATeamInfoAction) {
        when (action) {
            is NBATeamInfoAction.InitData -> initData()
        }
    }
}