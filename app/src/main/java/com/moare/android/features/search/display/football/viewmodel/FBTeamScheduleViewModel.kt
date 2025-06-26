package com.moare.android.features.search.display.football.viewmodel

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.DayInfo
import com.moare.android.features.search.display.common.viewmodel.BaseScheduleViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamScheduleDisplayModel
import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.models.models.football.FBGameForSchedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed class FBTeamScheduleIntent {
    data class InitData(val displayModel: FBTeamScheduleDisplayModel) : FBTeamScheduleIntent()
    data object ToggleAllResult : FBTeamScheduleIntent()
    data class UpdateResultOpenedState(val gameId: String, val isOpened: Boolean) : FBTeamScheduleIntent()
}

@HiltViewModel
class FBTeamScheduleViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseScheduleViewModel<FBTeamScheduleIntent, FBTeamScheduleDisplayModel>(nameProvider) {
    /* ---------------------
       data state
       --------------------- */
    private val _games = MutableStateFlow<List<FBGameForSchedule>>(emptyList())
    val games: StateFlow<List<FBGameForSchedule>> = _games

    /* ---------------------
       ui state
       --------------------- */
    private val _gameResultOpenedStateList = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val gameResultOpenedStateList: StateFlow<Map<String, Boolean>> = _gameResultOpenedStateList

    override fun send(intent: FBTeamScheduleIntent) {
        when (intent) {
            is FBTeamScheduleIntent.InitData -> initData(intent.displayModel)
            is FBTeamScheduleIntent.ToggleAllResult -> toggleAllResult()
            is FBTeamScheduleIntent.UpdateResultOpenedState -> updateResultOpenedState(intent.gameId, intent.isOpened)
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: FBTeamScheduleDisplayModel) {
        super.initData(displayModel)

        // init data
        _games.value = displayModel.games

        val gameResultOpenedStateList = games.value.associate {
            it.gameId to false
        }
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