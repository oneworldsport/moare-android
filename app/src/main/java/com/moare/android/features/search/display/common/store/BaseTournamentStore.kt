package com.moare.android.features.search.display.common.store

import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseTournamentStore<A, D: SportDisplayModel>(
    private val initial: D,
    private val nameProvider: TranslatedNameProvider,
    private val tournamentTeamsDeferred: CompletableDeferred<Map<String, List<Int?>>>
) {
    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    protected val _displayModel = MutableStateFlow(initial)
    val displayModel: StateFlow<D> = _displayModel

    protected val _teamNameDic = MutableStateFlow<Map<String, String>>(emptyMap())
    val teamNameDic: StateFlow<Map<String, String>> = _teamNameDic

    abstract fun send(action: A)

    open fun initData() {
        loadDictionaries(displayModel.value.leagueId)

        scope.launch {
            initTournamentTeams(tournamentTeamsDeferred.await())
        }
    }

    private fun loadDictionaries(leagueId: Int) {
        _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.FOOTBALL_TEAM_DIC)

        when (leagueId) {
            Constants.Ids.NBA -> _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.NBA_TEAM_DIC)
            Constants.Ids.MLB -> _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.MLB_TEAM_DIC)
            Constants.Ids.KBO -> _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.KBO_TEAM_DIC)
            else -> {}
        }
    }

    abstract fun initTournamentTeams(tournamentTeams: Map<String, List<Int?>>)

    open fun dispose() {
        scope.cancel()
    }
}