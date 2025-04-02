package com.moare.android.features.search.display.nba.viewmodel

import androidx.compose.ui.unit.dp
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
    val itemHeight = 30.dp

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
        data class InitData(val displayModel: NBAPlayerInfoDisplayModel) : Intent()
    }

    override fun send(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.InitData -> initData(intent.displayModel)
            }
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

















