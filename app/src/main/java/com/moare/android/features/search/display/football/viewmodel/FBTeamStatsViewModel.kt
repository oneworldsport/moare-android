package com.moare.android.features.search.display.football.viewmodel

import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.display.common.viewmodel.BaseStatsViewModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStatsDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FBTeamStatsIntent {
    data class InitData(val displayModel: FBTeamStatsDisplayModel) : FBTeamStatsIntent()
}

@HiltViewModel
class FBTeamStatsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseStatsViewModel<FBTeamStatsIntent, FBTeamStatsDisplayModel>(nameProvider) {
    /* ---------------------
       constants
       --------------------- */

    override fun send(intent: FBTeamStatsIntent) {
        when (intent) {
            is FBTeamStatsIntent.InitData -> initData(intent.displayModel)
        }
    }

    /* ---------------------
       implements
       --------------------- */
}