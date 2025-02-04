package com.moare.android.features.search.display.football.viewmodel

import androidx.lifecycle.viewModelScope
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStatsDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FBPlayerStatsViewModel @Inject constructor(
) : MVIViewModel<FBPlayerStatsViewModel.Intent, FBPlayerStatsDisplayModel>() {
    /* ---------------------
       constants
       --------------------- */

    /* ---------------------
       data state
       --------------------- */
    private val _displayModel = MutableStateFlow<FBPlayerStatsDisplayModel?>(null)
    val displayModel: StateFlow<FBPlayerStatsDisplayModel?> = _displayModel

    /* ---------------------
       ui state
       --------------------- */

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {

    }

    override fun send(intent: Intent) {
        viewModelScope.launch {
//            when (intent) {
//
//            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: FBPlayerStatsDisplayModel) {
        viewModelScope.launch {
            _displayModel.emit(displayModel)
        }
    }

    /* ---------------------
       implements
       --------------------- */
}