package com.moare.android.features.search.display.kbo.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoStore
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoStore
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamInfoDisplayModel
import com.moare.android.features.search.models.responsemodels.kbo.KBOTeamInfoResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject


sealed interface KBOTeamInfoAction {
    data object InitData : KBOTeamInfoAction
}

class KBOTeamInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: SportDecodableModel.KBOTeamInfo
) : BaseInfoStore<KBOTeamInfoAction, KBOTeamInfoResponseModel, KBOTeamInfoDisplayModel>(
    model.responseModel, model.displayModel, nameProvider
) {

    @AssistedFactory
    interface Factory {
        fun create(model: SportDecodableModel.KBOTeamInfo) : KBOTeamInfoStore
    }

    override fun send(action: KBOTeamInfoAction) {
        when (action) {
            is KBOTeamInfoAction.InitData -> initData()
        }
    }
}