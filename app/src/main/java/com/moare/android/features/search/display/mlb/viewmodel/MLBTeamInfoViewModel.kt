package com.moare.android.features.search.display.mlb.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoViewModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamInfoDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class MLBTeamInfoIntent {
    data class InitData(val displayModel: MLBTeamInfoDisplayModel) : MLBTeamInfoIntent()
}

@HiltViewModel
class MLBTeamInfoViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseInfoViewModel<MLBTeamInfoIntent, MLBTeamInfoDisplayModel>(nameProvider) {
    override fun send(intent: MLBTeamInfoIntent) {
        when (intent) {
            is MLBTeamInfoIntent.InitData -> initData(intent.displayModel)
        }
    }
}