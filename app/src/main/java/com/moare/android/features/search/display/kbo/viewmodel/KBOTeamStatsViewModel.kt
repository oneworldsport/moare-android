package com.moare.android.features.search.display.kbo.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseStatsViewModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStatsDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class KBOTeamStatsIntent {
    data class InitData(val displayModel: KBOTeamStatsDisplayModel) : KBOTeamStatsIntent()
}

@HiltViewModel
class KBOTeamStatsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseStatsViewModel<KBOTeamStatsIntent, KBOTeamStatsDisplayModel>(nameProvider) {
    /* ---------------------
       constants
       --------------------- */

    override fun send(intent: KBOTeamStatsIntent) {
        when (intent) {
            is KBOTeamStatsIntent.InitData -> initData(intent.displayModel)
        }
    }

    /* ---------------------
       implements
       --------------------- */
}