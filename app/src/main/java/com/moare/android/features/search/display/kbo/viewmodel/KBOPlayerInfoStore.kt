package com.moare.android.features.search.display.kbo.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoStore
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoStore
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerInfoDisplayModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject


sealed interface KBOPlayerInfoAction {
    data object InitData : KBOPlayerInfoAction
}

class KBOPlayerInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: KBOPlayerInfoDisplayModel
) : BaseInfoStore<KBOPlayerInfoAction, KBOPlayerInfoDisplayModel>(initial, nameProvider) {

    @AssistedFactory
    interface Factory {
        fun create(displayModel: KBOPlayerInfoDisplayModel) : KBOPlayerInfoStore
    }

    override fun send(aciton: KBOPlayerInfoAction) {
        when (aciton) {
            is KBOPlayerInfoAction.InitData -> initData()
        }
    }
}