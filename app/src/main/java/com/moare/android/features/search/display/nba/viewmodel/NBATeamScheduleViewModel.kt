package com.moare.android.features.search.display.nba.viewmodel

import androidx.lifecycle.viewModelScope
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.display.common.viewmodel.BaseScheduleViewModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamScheduleDisplayModel
import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBAGameForSchedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NBATeamScheduleIntent {
    data class InitData(val displayModel: NBATeamScheduleDisplayModel) : NBATeamScheduleIntent()
    data object ToggleAllResult : NBATeamScheduleIntent()
    data class UpdateResultOpenedState(val gameId: String, val isOpened: Boolean) : NBATeamScheduleIntent()
}

@HiltViewModel
class NBATeamScheduleViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseScheduleViewModel<NBATeamScheduleIntent, NBATeamScheduleDisplayModel>(nameProvider) {
    /* ---------------------
       data state
       --------------------- */
    private val _games = MutableStateFlow<List<NBAGameForSchedule>>(emptyList())
    val games: StateFlow<List<NBAGameForSchedule>> = _games

    /* ---------------------
       ui state
       --------------------- */
    private val _gameResultOpenedStateList = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val gameResultOpenedStateList: StateFlow<Map<String, Boolean>> = _gameResultOpenedStateList

    override fun send(intent: NBATeamScheduleIntent) {
        when (intent) {
            is NBATeamScheduleIntent.InitData -> initData(intent.displayModel)
            is NBATeamScheduleIntent.ToggleAllResult -> toggleAllResult()
            is NBATeamScheduleIntent.UpdateResultOpenedState -> updateResultOpenedState(intent.gameId, intent.isOpened)
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: NBATeamScheduleDisplayModel) {
        super.initData(displayModel)

        // init data
        _games.value = displayModel.games

        val gameResultOpenedStateList = games.value.associate { (it.gameId) to false }
        _gameResultOpenedStateList.value = gameResultOpenedStateList
    }

    /* ---------------------
       implements
       --------------------- */
    override fun toggleAllResult() {
        val newState = !isAllResultOpened.value
        _isAllResultOpened.value = newState
        _gameResultOpenedStateList.value = gameResultOpenedStateList.value.mapValues { newState }
    }

    private fun updateResultOpenedState(gameId: String, isOpened: Boolean) {
        val newMap = gameResultOpenedStateList.value.toMutableMap()
        newMap[gameId] = isOpened
        _gameResultOpenedStateList.value = newMap
    }
}