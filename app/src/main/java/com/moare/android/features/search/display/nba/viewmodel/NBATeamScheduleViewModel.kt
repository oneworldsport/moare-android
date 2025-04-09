package com.moare.android.features.search.display.nba.viewmodel

import androidx.lifecycle.viewModelScope
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamScheduleDisplayModel
import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.models.models.nba.NBAGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NBATeamScheduleViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : MVIViewModel<NBATeamScheduleViewModel.Intent, NBATeamScheduleDisplayModel>() {
    /* ---------------------
       data state
       --------------------- */
    private val _displayModel = MutableStateFlow<NBATeamScheduleDisplayModel?>(null)
    val displayModel: StateFlow<NBATeamScheduleDisplayModel?> = _displayModel

    private val _games = MutableStateFlow<List<NBAGame>>(emptyList())
    val games: StateFlow<List<NBAGame>> = _games

    /* ---------------------
       ui state
       --------------------- */
    private val _isAllResultOpened = MutableStateFlow(false)
    val isAllResultOpened: StateFlow<Boolean> = _isAllResultOpened

    private val _gameResultOpenedStateList = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val gameResultOpenedStateList: StateFlow<Map<String, Boolean>> = _gameResultOpenedStateList

    /* ---------------------
       etc
       --------------------- */
    var teamNameDictionary: Map<String, String> = emptyMap()

    init {
        teamNameDictionary = nameProvider.getDictionary("nba_team")
    }

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data class InitData(val displayModel: NBATeamScheduleDisplayModel) : Intent()
        data object ToggleAllResult : Intent()
        data class UpdateResultOpenedState(val gameCode: String, val isOpened: Boolean) : Intent()
    }

    override fun send(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.InitData -> initData(intent.displayModel)
                is Intent.ToggleAllResult -> toggleAllResult()
                is Intent.UpdateResultOpenedState -> updateResultOpenedState(intent.gameCode, intent.isOpened)
            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: NBATeamScheduleDisplayModel) {
        viewModelScope.launch {
            // init with default value
            _isAllResultOpened.emit(false)

            // init data
            _displayModel.emit(displayModel)
            _games.emit(displayModel.games)

            val gameResultOpenedStateList = games.value.associate { (it.gameSummary?.gameCode ?: "") to false }
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

    private suspend fun updateResultOpenedState(gameCode: String, isOpened: Boolean) {
        val newMap = gameResultOpenedStateList.value.toMutableMap()
        newMap[gameCode] = isOpened
        _gameResultOpenedStateList.emit(newMap)
    }
}