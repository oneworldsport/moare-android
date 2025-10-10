package com.moare.android.features.search.display.common.viewmodel

import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseGameStatsStore<A, T: SportDisplayModel>(
    private val initial: T,
    private val nameProvider: TranslatedNameProvider
) {
    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    protected val _displayModel = MutableStateFlow(initial)
    val displayModel: StateFlow<T> = _displayModel

    protected val _displayDataState = MutableStateFlow<ApiFetchState>(ApiFetchState.Idle)
    val displayDataState: StateFlow<ApiFetchState> = _displayDataState

    protected val _playerNameDic = MutableStateFlow<Map<String, String>>(emptyMap())
    val playerNameDic: StateFlow<Map<String, String>> = _playerNameDic

    protected val _teamNameDic = MutableStateFlow<Map<String, String>>(emptyMap())
    val teamNameDic: StateFlow<Map<String, String>> = _teamNameDic

    protected var _firstCategorySelectedIndex = MutableStateFlow(0)
    val firstCategorySelectedIndex: StateFlow<Int> = _firstCategorySelectedIndex

    protected var _secondCategorySelectedIndex = MutableStateFlow(0)
    val secondCategorySelectedIndex: StateFlow<Int> = _secondCategorySelectedIndex

    protected var _teamCategorySelectedIndex = MutableStateFlow(0)
    val teamCategorySelectedIndex: StateFlow<Int> = _teamCategorySelectedIndex

    var shouldScrollCategory = false

    abstract fun send(action: A)

    open fun initData() {
        // init with default value
        _displayDataState.value = ApiFetchState.Idle

        _firstCategorySelectedIndex.value = 0
        _secondCategorySelectedIndex.value = 0
        _teamCategorySelectedIndex.value = 0

        shouldScrollCategory = false

        loadDictionaries(displayModel.value.leagueId)
    }

    private fun loadDictionaries(leagueId: Int) {
        _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.FOOTBALL_TEAM_DIC)

        when (leagueId) {
            Constants.Ids.EPL -> {
                _playerNameDic.value = nameProvider.getDictionary(Constants.Keys.EPL_PLAYER_DIC)
            }
            Constants.Ids.LALIGA -> {
                _playerNameDic.value = nameProvider.getDictionary(Constants.Keys.LALIGA_PLAYER_DIC)
            }
            Constants.Ids.BUNDESLIGA -> {
                _playerNameDic.value = nameProvider.getDictionary(Constants.Keys.BUNDESLIGA_PLAYER_DIC)
            }
            Constants.Ids.LIGUE1 -> {
                _playerNameDic.value = nameProvider.getDictionary(Constants.Keys.LIGUE1_PLAYER_DIC)
            }
            Constants.Ids.SERIEA -> {
                _playerNameDic.value = nameProvider.getDictionary(Constants.Keys.SERIEA_PLAYER_DIC)
            }
            Constants.Ids.MLS -> {
                _playerNameDic.value = nameProvider.getDictionary(Constants.Keys.MLS_PLAYER_DIC)
            }
            Constants.Ids.NBA -> {
                _playerNameDic.value = nameProvider.getDictionary(Constants.Keys.NBA_PLAYER_DIC)
                _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.NBA_TEAM_DIC)
            }
            Constants.Ids.KBO -> {
                _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.KBO_TEAM_DIC)
            }
            Constants.Ids.MLB -> {
                _playerNameDic.value = nameProvider.getDictionary(Constants.Keys.MLB_PLAYER_DIC)
                _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.MLB_TEAM_DIC)
            }
            else -> {}
        }
    }

    open fun selectFirstCategory(index: Int) {
        shouldScrollCategory = false
        _firstCategorySelectedIndex.value = index
    }

    open fun selectSecondCategory(index: Int) {
        shouldScrollCategory = false
        _secondCategorySelectedIndex.value = index
    }

    open fun selectTeam(index: Int) {
        _teamCategorySelectedIndex.value = index
    }

    abstract fun sortPlayers()
    abstract fun setPlayersTotalStats()

    open fun dispose() {
        scope.cancel()
    }
}