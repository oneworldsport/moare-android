package com.moare.android.features.search.display.common.viewmodel

import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseInfoViewModel<I, T>(
    private val nameProvider: TranslatedNameProvider
) : MVIViewModel<I, T>() {
    protected val _displayModel = MutableStateFlow<T?>(null)
    val displayModel: StateFlow<T?> = _displayModel

    var playerNameDictionary: Map<String, String> = emptyMap()
    var teamNameDictionary: Map<String, String> = emptyMap()

    override fun initData(displayModel: T) {
        _displayModel.value = displayModel

//        loadDictionaries(displayModel)
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
            Constants.Ids.NBA -> {
                playerNameDictionary = nameProvider.getDictionary(Constants.Keys.NBA_PLAYER_DIC)
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.NBA_TEAM_DIC)
            }
            else -> {}
        }
    }
}