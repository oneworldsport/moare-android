package com.moare.android.features.search.display.football.viewmodel

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.DayInfo
import com.moare.android.features.search.display.football.viewmodel.FBLeagueScheduleViewModel.Intent
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

    private val _gameResultOpenedStateList = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val gameResultOpenedStateList: StateFlow<Map<Int, Boolean>> = _gameResultOpenedStateList

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data object ToggleAllResult : Intent()
        data class UpdateResultOpenedState(val fixtureId: Int, val isOpened: Boolean) : Intent()
    }

    override fun send(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.ToggleAllResult -> toggleAllResult()
                is Intent.UpdateResultOpenedState -> updateResultOpenedState(intent.fixtureId, intent.isOpened)
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

            val gameResultOpenedStateList = games.value.associate {
                it.fixture.id to false
            }
            _gameResultOpenedStateList.emit(gameResultOpenedStateList)
        }
    }

    /* ---------------------
       implements
       --------------------- */
    private suspend fun toggleAllResult() {
        val newState = !isAllResultOpened.value
        _isAllResultOpened.emit(newState)
        _gameResultOpenedStateList.emit(gameResultOpenedStateList.value.mapValues { newState })
    }

    private suspend fun updateResultOpenedState(fixtureId: Int, isOpened: Boolean) {
        val newMap = gameResultOpenedStateList.value.toMutableMap()
        newMap[fixtureId] = isOpened
        _gameResultOpenedStateList.emit(newMap)
    }
}