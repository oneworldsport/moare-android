package com.moare.android.features.search.display.football.viewmodel

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.DayInfo
import com.moare.android.features.search.models.displaymodels.football.FBTeamScheduleDisplayModel
import com.moare.android.features.search.models.models.football.FBGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class FBTeamScheduleViewModel @Inject constructor(
) : MVIViewModel<FBTeamScheduleViewModel.Intent, FBTeamScheduleDisplayModel>() {
    /* ---------------------
       constants
       --------------------- */
    val itemHeight = 100.dp

    /* ---------------------
       data state
       --------------------- */
    private val _displayModel = MutableStateFlow<FBTeamScheduleDisplayModel?>(null)
    val displayModel: StateFlow<FBTeamScheduleDisplayModel?> = _displayModel

    private val _games = MutableStateFlow<List<FBGame>>(emptyList())
    val games: StateFlow<List<FBGame>> = _games

    /* ---------------------
       ui state
       --------------------- */
    private val _isAllResultOpened = MutableStateFlow(false)
    val isAllResultOpened: StateFlow<Boolean> = _isAllResultOpened

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data object ToggleAllResult : Intent()
    }

    override fun send(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.ToggleAllResult -> toggleAllResult()
            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: FBTeamScheduleDisplayModel) {
        viewModelScope.launch {
            _displayModel.emit(displayModel)
            _games.emit(displayModel.games)
        }
    }

    /* ---------------------
       implements
       --------------------- */
    private suspend fun toggleAllResult() {
        _isAllResultOpened.emit(!isAllResultOpened.value)
    }
}