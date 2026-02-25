package com.moare.android.features.search.display.tennis.store

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.container.state.GameListEntry
import com.moare.android.features.search.display.common.store.BaseTournamentStore
import com.moare.android.features.search.display.nba.store.NBATournamentAction
import com.moare.android.features.search.display.nba.store.NBATournamentStore
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBATournamentDisplayModel
import com.moare.android.features.search.models.displaymodels.tennis.TennisTournamentDisplayModel
import com.moare.android.features.search.models.models.nba.NBAGameForSchedule
import com.moare.android.features.search.models.models.nba.NBAGameSummary
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface TennisTournamentAction {
    data object InitData: TennisTournamentAction
}

sealed interface TennisTournamentDelegate {
}

class TennisTournamentStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    private val tournamentTeamsDeferred: CompletableDeferred<Map<String, List<Int?>>>,
    @Assisted val model: TennisTournamentDisplayModel,
    @Assisted val emitToParent: (TennisTournamentDelegate) -> Unit
) : BaseTournamentStore<TennisTournamentAction, TennisTournamentDisplayModel>(
    model, nameProvider, tournamentTeamsDeferred
) {
    @AssistedFactory
    interface Factory {
        fun create(
            model: TennisTournamentDisplayModel,
            emitToParent: (TennisTournamentDelegate) -> Unit
        ): TennisTournamentStore
    }

    override fun send(action: TennisTournamentAction) {
//        when (action) {
//        }
    }

    override fun initTournamentTeams(tournamentTeams: Map<String, List<Int?>>) {
        TODO("Not yet implemented")
    }
}