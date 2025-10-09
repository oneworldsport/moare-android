package com.moare.android.features.search.display.kbo.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoStore
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoStore
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamInfoDisplayModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject


sealed interface KBOTeamInfoAction {
    data object InitData : KBOTeamInfoAction
}

class KBOTeamInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: KBOTeamInfoDisplayModel
) : BaseInfoStore<KBOTeamInfoAction, KBOTeamInfoDisplayModel>(initial, nameProvider) {

    @AssistedFactory
    interface Factory {
        fun create(displayModel: KBOTeamInfoDisplayModel) : KBOTeamInfoStore
    }

    override fun send(action: KBOTeamInfoAction) {
        when (action) {
            is KBOTeamInfoAction.InitData -> initData()
        }
    }
}