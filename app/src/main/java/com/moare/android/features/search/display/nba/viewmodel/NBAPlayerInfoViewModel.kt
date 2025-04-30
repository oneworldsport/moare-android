package com.moare.android.features.search.display.nba.viewmodel

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.display.common.viewmodel.BaseInfoViewModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerInfoDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NBAPlayerInfoIntent {
    data class InitData(val displayModel: NBAPlayerInfoDisplayModel) : NBAPlayerInfoIntent()
}

@HiltViewModel
class NBAPlayerInfoViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseInfoViewModel<NBAPlayerInfoIntent, NBAPlayerInfoDisplayModel>(nameProvider) {
    /* ---------------------
       constants
       --------------------- */
    val itemHeight = 30.dp

    override fun send(intent: NBAPlayerInfoIntent) {
        when (intent) {
            is NBAPlayerInfoIntent.InitData -> initData(intent.displayModel)
        }
    }

    /* ---------------------
       implements
       --------------------- */
}

















