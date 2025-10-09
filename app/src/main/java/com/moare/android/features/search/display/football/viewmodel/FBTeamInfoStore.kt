package com.moare.android.features.search.display.football.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoStore
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamInfoDisplayModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed interface FBTeamInfoAction {
    data object InitData : FBTeamInfoAction
}

class FBTeamInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: FBTeamInfoDisplayModel
) : BaseInfoStore<FBTeamInfoAction, FBTeamInfoDisplayModel>(initial, nameProvider) {

    @AssistedFactory
    interface Factory {
        fun create(displayModel: FBTeamInfoDisplayModel) : FBTeamInfoStore
    }

    override fun send(action: FBTeamInfoAction) {
        when (action) {
            is FBTeamInfoAction.InitData -> initData()
        }
    }
}