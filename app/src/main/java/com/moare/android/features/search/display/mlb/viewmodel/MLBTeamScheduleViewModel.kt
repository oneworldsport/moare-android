package com.moare.android.features.search.display.mlb.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseScheduleViewModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamScheduleDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBGameForSchedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed class MLBTeamScheduleIntent {
    data class InitData(val displayModel: MLBTeamScheduleDisplayModel) : MLBTeamScheduleIntent()
    data object ToggleAllResult : MLBTeamScheduleIntent()
    data class UpdateResultOpenedState(val gameId: String, val isOpened: Boolean) : MLBTeamScheduleIntent()
}

@HiltViewModel
class MLBTeamScheduleViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseScheduleViewModel<MLBTeamScheduleIntent, MLBTeamScheduleDisplayModel>(nameProvider) {
    /* ---------------------
       data state
       --------------------- */
    private val _games = MutableStateFlow<List<MLBGameForSchedule>>(emptyList())
    val games: StateFlow<List<MLBGameForSchedule>> = _games

    /* ---------------------
       ui state
       --------------------- */
    private val _gameResultOpenedStateList = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val gameResultOpenedStateList: StateFlow<Map<String, Boolean>> = _gameResultOpenedStateList

    override fun send(intent: MLBTeamScheduleIntent) {
        when (intent) {
            is MLBTeamScheduleIntent.InitData -> initData(intent.displayModel)
            is MLBTeamScheduleIntent.ToggleAllResult -> toggleAllResult()
            is MLBTeamScheduleIntent.UpdateResultOpenedState -> updateResultOpenedState(intent.gameId, intent.isOpened)
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: MLBTeamScheduleDisplayModel) {
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