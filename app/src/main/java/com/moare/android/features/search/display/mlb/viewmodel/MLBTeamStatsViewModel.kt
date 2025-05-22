package com.moare.android.features.search.display.mlb.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseStatsViewModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStatsDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class MLBTeamStatsIntent {
    data class InitData(val displayModel: MLBTeamStatsDisplayModel) : MLBTeamStatsIntent()
}

@HiltViewModel
class MLBTeamStatsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseStatsViewModel<MLBTeamStatsIntent, MLBTeamStatsDisplayModel>(nameProvider) {
    /* ---------------------
       constants
       --------------------- */

    override fun send(intent: MLBTeamStatsIntent) {
        when (intent) {
            is MLBTeamStatsIntent.InitData -> initData(intent.displayModel)
        }
    }

    /* ---------------------
       implements
       --------------------- */
}