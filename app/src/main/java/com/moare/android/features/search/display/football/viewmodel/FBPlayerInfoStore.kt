package com.moare.android.features.search.display.football.viewmodel

import androidx.compose.ui.unit.dp
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoStore
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.responsemodels.football.FBPlayerInfoResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

sealed interface FBPlayerInfoAction {
    data object InitData : FBPlayerInfoAction
}

class FBPlayerInfoStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: FBPlayerInfoDisplayModel
) : BaseInfoStore<FBPlayerInfoAction, FBPlayerInfoDisplayModel>(initial, nameProvider) {
    val itemHeight = 30.dp

    @AssistedFactory
    interface Factory {
        fun create(displayModel: FBPlayerInfoDisplayModel) : FBPlayerInfoStore
    }

    override fun send(action: FBPlayerInfoAction) {
        when (action) {
            is FBPlayerInfoAction.InitData -> initData()
        }
    }
}