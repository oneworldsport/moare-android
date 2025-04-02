package com.moare.android.features.search.display.nba.viewmodel

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamInfoDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NBATeamInfoViewModel @Inject constructor(
) : MVIViewModel<NBATeamInfoViewModel.Intent, NBATeamInfoDisplayModel>() {
    /* ---------------------
       constants
       --------------------- */

    /* ---------------------
       data state
       --------------------- */
    private val _displayModel = MutableStateFlow<NBATeamInfoDisplayModel?>(null)
    val displayModel: StateFlow<NBATeamInfoDisplayModel?> = _displayModel

    /* ---------------------
       ui state
       --------------------- */

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data class InitData(val displayModel: NBATeamInfoDisplayModel) : Intent()
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
    override fun initData(displayModel: NBATeamInfoDisplayModel) {
        viewModelScope.launch {
            _displayModel.emit(displayModel)
        }
    }

    /* ---------------------
       implements
       --------------------- */
}