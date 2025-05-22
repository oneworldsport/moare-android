package com.moare.android.features.search.display.kbo.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseStatsViewModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerStatsDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class KBOPlayerStatsIntent {
    data class InitData(val displayModel: KBOPlayerStatsDisplayModel) : KBOPlayerStatsIntent()
}

@HiltViewModel
class KBOPlayerStatsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseStatsViewModel<KBOPlayerStatsIntent, KBOPlayerStatsDisplayModel>(nameProvider) {
    /* ---------------------
       constants
       --------------------- */

    override fun send(intent: KBOPlayerStatsIntent) {
        when (intent) {
            is KBOPlayerStatsIntent.InitData -> initData(intent.displayModel)
        }
    }

    /* ---------------------
       implements
       --------------------- */
}