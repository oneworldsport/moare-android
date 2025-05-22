package com.moare.android.features.search.display.mlb.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseStatsViewModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBPlayerStatsDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class MLBPlayerStatsIntent {
    data class InitData(val displayModel: MLBPlayerStatsDisplayModel) : MLBPlayerStatsIntent()
}

@HiltViewModel
class MLBPlayerStatsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseStatsViewModel<MLBPlayerStatsIntent, MLBPlayerStatsDisplayModel>(nameProvider) {
    /* ---------------------
       constants
       --------------------- */

    override fun send(intent: MLBPlayerStatsIntent) {
        when (intent) {
            is MLBPlayerStatsIntent.InitData -> initData(intent.displayModel)
        }
    }

    /* ---------------------
       implements
       --------------------- */
}