package com.moare.android.features.search.display.kbo.store

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.store.BaseStatsStore
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStatsDisplayModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

sealed interface KBOTeamStatsAction {
    data object InitData : KBOTeamStatsAction
}

class KBOTeamStatsStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: KBOTeamStatsDisplayModel
) : BaseStatsStore<KBOTeamStatsAction, KBOTeamStatsDisplayModel>(initial, nameProvider) {

    @AssistedFactory
    interface Factory {
        fun create(displayModel: KBOTeamStatsDisplayModel) : KBOTeamStatsStore
    }

    override fun send(action: KBOTeamStatsAction) {
        when (action) {
            is KBOTeamStatsAction.InitData -> initData()
        }
    }
}