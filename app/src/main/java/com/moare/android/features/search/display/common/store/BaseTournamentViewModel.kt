package com.moare.android.features.search.display.common.store

import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseTournamentViewModel<I, T>(
    private val nameProvider: TranslatedNameProvider
): MVIViewModel<I, T>()  {
    protected val _displayModel = MutableStateFlow<T?>(null)
    val displayModel: StateFlow<T?> = _displayModel

    var teamNameDic: Map<String, String> = emptyMap()
    var tournamentTeams: Map<String, List<Int>> = emptyMap()

    override fun initData(displayModel: T) {
        _displayModel.value = displayModel

        if (displayModel is SportDisplayModel) {
            loadDictionaries(displayModel.leagueId)
        }
    }

    private fun loadDictionaries(leagueId: Int) {
        when (leagueId) {
            in Constants.Ids.FOOTBALL_LEAGUES, in Constants.Ids.FOOTBALL_TOURNAMENT_LEAGUES -> {
                teamNameDic = nameProvider.getDictionary(Constants.Keys.FOOTBALL_TEAM_DIC)
            }
        }
    }
}