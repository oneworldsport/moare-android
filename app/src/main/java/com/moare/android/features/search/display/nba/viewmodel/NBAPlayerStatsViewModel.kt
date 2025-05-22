package com.moare.android.features.search.display.nba.viewmodel

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.display.common.viewmodel.BaseStatsViewModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStatsDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NBAPlayerStatsIntent {
    data class InitData(val displayModel: NBAPlayerStatsDisplayModel) : NBAPlayerStatsIntent()
}

@HiltViewModel
class NBAPlayerStatsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseStatsViewModel<NBAPlayerStatsIntent, NBAPlayerStatsDisplayModel>(nameProvider) {
    /* ---------------------
       constants
       --------------------- */

    override fun send(intent: NBAPlayerStatsIntent) {
        when (intent) {
            is NBAPlayerStatsIntent.InitData -> initData(intent.displayModel)
        }
    }

    /* ---------------------
       implements
       --------------------- */
}