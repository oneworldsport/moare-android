package com.moare.android.features.search.display.tennis.store

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.store.BaseGameStatsStore
import com.moare.android.features.search.display.nba.store.NBAGameStatsAction
import com.moare.android.features.search.display.nba.store.NBAGameStatsDelegate
import com.moare.android.features.search.display.nba.store.NBAGameStatsStore
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.tennis.TennisGameStatsDisplayModel
import com.moare.android.features.search.networking.SearchClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

sealed interface TennisGameStatsAction {
    data object InitData : TennisGameStatsAction
}

sealed interface TennisGameStatsDelegate {

}

class TennisGameStatsStore @AssistedInject constructor(
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: TennisGameStatsDisplayModel,
    @Assisted val emitToParent: (TennisGameStatsDelegate) -> Unit
) : BaseGameStatsStore<TennisGameStatsAction, TennisGameStatsDisplayModel>(model, nameProvider) {
    @AssistedFactory
    interface Factory {
        fun create(
            model: TennisGameStatsDisplayModel,
            emitToParent: (TennisGameStatsDelegate) -> Unit
        ) : TennisGameStatsStore
    }

    override fun send(action: TennisGameStatsAction) {
//        when (action) {
//        }
    }

    override fun sortPlayers() {
        TODO("Not yet implemented")
    }

    override fun setPlayersTotalStats() {
        TODO("Not yet implemented")
    }
}