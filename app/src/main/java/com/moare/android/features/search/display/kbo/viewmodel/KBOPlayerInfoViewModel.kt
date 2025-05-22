package com.moare.android.features.search.display.kbo.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseInfoViewModel
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerInfoIntent
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerInfoDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


sealed class KBOPlayerInfoIntent {
    data class InitData(val displayModel: KBOPlayerInfoDisplayModel) : KBOPlayerInfoIntent()
}

@HiltViewModel
class KBOPlayerInfoViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseInfoViewModel<KBOPlayerInfoIntent, KBOPlayerInfoDisplayModel>(nameProvider) {
    override fun send(intent: KBOPlayerInfoIntent) {
        when (intent) {
            is KBOPlayerInfoIntent.InitData -> initData(intent.displayModel)
        }
    }
}