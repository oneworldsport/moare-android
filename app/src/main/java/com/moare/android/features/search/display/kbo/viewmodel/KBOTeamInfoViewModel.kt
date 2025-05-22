package com.moare.android.features.search.display.kbo.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoViewModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamInfoDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


sealed class KBOTeamInfoIntent {
    data class InitData(val displayModel: KBOTeamInfoDisplayModel) : KBOTeamInfoIntent()
}

@HiltViewModel
class KBOTeamInfoViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseInfoViewModel<KBOTeamInfoIntent, KBOTeamInfoDisplayModel>(nameProvider) {
    override fun send(intent: KBOTeamInfoIntent) {
        when (intent) {
            is KBOTeamInfoIntent.InitData -> initData(intent.displayModel)
        }
    }
}