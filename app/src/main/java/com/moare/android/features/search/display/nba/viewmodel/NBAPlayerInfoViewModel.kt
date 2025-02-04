package com.moare.android.features.search.display.nba.viewmodel

import androidx.lifecycle.viewModelScope
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerInfoDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NBAPlayerInfoViewModel @Inject constructor(
) : MVIViewModel<NBAPlayerInfoViewModel.Intent, NBAPlayerInfoDisplayModel>() {
    /* ---------------------
       constants
       --------------------- */

    /* ---------------------
       data state
       --------------------- */
    private val _displayModel = MutableStateFlow<NBAPlayerInfoDisplayModel?>(null)
    val displayModel: StateFlow<NBAPlayerInfoDisplayModel?> = _displayModel

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
    override fun initData(displayModel: NBAPlayerInfoDisplayModel) {
        viewModelScope.launch {
            _displayModel.emit(displayModel)
        }
    }

    /* ---------------------
       implements
       --------------------- */
}

















