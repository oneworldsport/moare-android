package com.moare.android.features.search.display.nba.viewmodel

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.display.common.viewmodel.BaseInfoViewModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamInfoDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NBATeamInfoIntent {
    data class InitData(val displayModel: NBATeamInfoDisplayModel) : NBATeamInfoIntent()
}

@HiltViewModel
class NBATeamInfoViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseInfoViewModel<NBATeamInfoIntent, NBATeamInfoDisplayModel>(nameProvider) {
    /* ---------------------
       constants
       --------------------- */

    override fun send(intent: NBATeamInfoIntent) {
        when (intent) {
            is NBATeamInfoIntent.InitData -> initData(intent.displayModel)
        }
    }

    /* ---------------------
       implements
       --------------------- */
}