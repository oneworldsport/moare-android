package com.moare.android.features.search.display.kbo.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseScheduleViewModel
import com.moare.android.features.search.display.nba.viewmodel.NBATeamScheduleIntent
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamScheduleDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOGameForSchedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed class KBOTeamScheduleIntent {
    data class InitData(val displayModel: KBOTeamScheduleDisplayModel) : KBOTeamScheduleIntent()
    data object ToggleAllResult : KBOTeamScheduleIntent()
    data class UpdateResultOpenedState(val itemKey: String, val isOpened: Boolean) : KBOTeamScheduleIntent()
}

@HiltViewModel
class KBOTeamScheduleViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseScheduleViewModel<KBOTeamScheduleIntent, KBOTeamScheduleDisplayModel>(nameProvider) {
    /* ---------------------
       data state
       --------------------- */
    private val _games = MutableStateFlow<List<KBOGameForSchedule>>(emptyList())
    val games: StateFlow<List<KBOGameForSchedule>> = _games

    /* ---------------------
       ui state
       --------------------- */
    private val _gameResultOpenedStateList = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val gameResultOpenedStateList: StateFlow<Map<String, Boolean>> = _gameResultOpenedStateList

    override fun send(intent: KBOTeamScheduleIntent) {
        when (intent) {
            is KBOTeamScheduleIntent.InitData -> initData(intent.displayModel)
            is KBOTeamScheduleIntent.ToggleAllResult -> toggleAllResult()
            is KBOTeamScheduleIntent.UpdateResultOpenedState -> updateResultOpenedState(intent.itemKey, intent.isOpened)
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: KBOTeamScheduleDisplayModel) {
        super.initData(displayModel)

        // init data
        _games.value = displayModel.games

        val gameResultOpenedStateList = games.value.associate { (it.itemKey) to false }
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

    private fun updateResultOpenedState(itemKey: String, isOpened: Boolean) {
        val newMap = gameResultOpenedStateList.value.toMutableMap()
        newMap[itemKey] = isOpened
        _gameResultOpenedStateList.value = newMap
    }
}