package com.moare.android.features.search.display.mlb.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoViewModel
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerInfoIntent
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerInfoDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


sealed class MLBPlayerInfoIntent {
    data class InitData(val displayModel: MLBPlayerInfoDisplayModel) : MLBPlayerInfoIntent()
}

@HiltViewModel
class MLBPlayerInfoViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseInfoViewModel<MLBPlayerInfoIntent, MLBPlayerInfoDisplayModel>(nameProvider) {
    override fun send(intent: MLBPlayerInfoIntent) {
        when (intent) {
            is MLBPlayerInfoIntent.InitData -> initData(intent.displayModel)
        }
    }
}