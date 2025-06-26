package com.moare.android.features.search.display.common.viewmodel

import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseGameStatsViewModel<I, T>(
    private val nameProvider: TranslatedNameProvider
) : MVIViewModel<I, T>() {
    /* ---------------------
       data state
       --------------------- */
    protected val _displayModel = MutableStateFlow<T?>(null)
    val displayModel: StateFlow<T?> = _displayModel

    protected val _displayDataState = MutableStateFlow<ApiFetchState>(ApiFetchState.Idle)
    val displayDataState: StateFlow<ApiFetchState> = _displayDataState

    /* ---------------------
       ui state
       --------------------- */
    protected var _firstCategorySelectedIndex = MutableStateFlow(0)
    val firstCategorySelectedIndex: StateFlow<Int> = _firstCategorySelectedIndex

    protected var _secondCategorySelectedIndex = MutableStateFlow(0)
    val secondCategorySelectedIndex: StateFlow<Int> = _secondCategorySelectedIndex

    protected var _selectedTeamIndex = MutableStateFlow(0)
    val selectedTeamIndex: StateFlow<Int> = _selectedTeamIndex

    /* ---------------------
       etc
       --------------------- */
    var shouldScrollCategory = false
    var playerNameDictionary: Map<String, String> = emptyMap()
    var teamNameDictionary: Map<String, String> = emptyMap()

    override fun initData(displayModel: T) {
        // init with default value
        _displayDataState.value = ApiFetchState.Idle

        _firstCategorySelectedIndex.value = 0
        _secondCategorySelectedIndex.value = 0
        _selectedTeamIndex.value = 0

        shouldScrollCategory = false

        // init data
        _displayModel.value = displayModel

        if (displayModel is SportDisplayModel) {
            loadDictionaries(displayModel.leagueId)
        }
    }

    private fun loadDictionaries(leagueId: Int) {
        when (leagueId) {
            Constants.Ids.EPL -> {
                playerNameDictionary = nameProvider.getDictionary(Constants.Keys.EPL_PLAYER_DIC)
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.EPL_TEAM_DIC)
            }
            Constants.Ids.LALIGA -> {
                playerNameDictionary = nameProvider.getDictionary(Constants.Keys.LALIGA_PLAYER_DIC)
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.LALIGA_TEAM_DIC)
            }
            Constants.Ids.BUNDESLIGA -> {
                playerNameDictionary = nameProvider.getDictionary(Constants.Keys.BUNDESLIGA_PLAYER_DIC)
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.BUNDESLIGA_TEAM_DIC)
            }
            Constants.Ids.LIGUE1 -> {
                playerNameDictionary = nameProvider.getDictionary(Constants.Keys.LIGUE1_PLAYER_DIC)
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.LIGUE1_TEAM_DIC)
            }
            Constants.Ids.SERIEA -> {
                playerNameDictionary = nameProvider.getDictionary(Constants.Keys.SERIEA_PLAYER_DIC)
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.SERIEA_TEAM_DIC)
            }
            Constants.Ids.NBA -> {
                playerNameDictionary = nameProvider.getDictionary(Constants.Keys.NBA_PLAYER_DIC)
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.NBA_TEAM_DIC)
            }
            Constants.Ids.KBO -> {
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.KBO_TEAM_DIC)
            }
            Constants.Ids.MLB -> {
                playerNameDictionary = nameProvider.getDictionary(Constants.Keys.MLB_PLAYER_DIC)
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.MLB_TEAM_DIC)
            }
            else -> {}
        }
    }

    open fun selectFirstCategory(index: Int) {
        shouldScrollCategory = true
    }

    open fun selectSecondCategory(index: Int) {
        shouldScrollCategory = false
        _secondCategorySelectedIndex.value = index
    }

    open fun selectTeam(index: Int) {
        _selectedTeamIndex.value = index
    }

    abstract fun sortPlayers()
    abstract fun setPlayersTotalStats()
}