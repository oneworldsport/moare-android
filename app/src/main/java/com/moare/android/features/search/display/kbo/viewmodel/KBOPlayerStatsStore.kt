package com.moare.android.features.search.display.kbo.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseStatsStore
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerStatsDisplayModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

sealed interface KBOPlayerStatsAction {
    data object InitData : KBOPlayerStatsAction
}

class KBOPlayerStatsStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: KBOPlayerStatsDisplayModel
) : BaseStatsStore<KBOPlayerStatsAction, KBOPlayerStatsDisplayModel>(initial, nameProvider) {

    @AssistedFactory
    interface Factory {
        fun create(displayModel: KBOPlayerStatsDisplayModel) : KBOPlayerStatsStore
    }

    override fun send(action: KBOPlayerStatsAction) {
        when (action) {
            is KBOPlayerStatsAction.InitData -> initData()
        }
    }
}