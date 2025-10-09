package com.moare.android.features.search.display.nba.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoStore
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerInfoDisplayModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

sealed interface NBAPlayerInfoAction {
    data object InitData : NBAPlayerInfoAction
}

class NBAPlayerInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: NBAPlayerInfoDisplayModel
) : BaseInfoStore<NBAPlayerInfoAction, NBAPlayerInfoDisplayModel>(initial, nameProvider) {

    @AssistedFactory
    interface Factory {
        fun create(displayModel: NBAPlayerInfoDisplayModel) : NBAPlayerInfoStore
    }

    override fun send(action: NBAPlayerInfoAction) {
        when (action) {
            is NBAPlayerInfoAction.InitData -> initData()
        }
    }
}

















