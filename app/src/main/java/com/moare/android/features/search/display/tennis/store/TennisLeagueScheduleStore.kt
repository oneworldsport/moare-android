package com.moare.android.features.search.display.tennis.store

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.DayInfo
import com.moare.android.features.search.display.common.store.BaseScheduleStore
import com.moare.android.features.search.display.nba.store.NBALeagueScheduleAction
import com.moare.android.features.search.display.nba.store.NBALeagueScheduleStore
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBALeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.tennis.TennisLeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.nba.NBAGameForSchedule
import com.moare.android.features.search.networking.SearchClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface TennisLeagueScheduleAction {
    data object InitData : TennisLeagueScheduleAction
}

sealed interface TennisLeagueScheduleDelegate {
}

class TennisLeagueScheduleStore @AssistedInject constructor(
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: TennisLeagueScheduleDisplayModel,
    @Assisted val emitToParent: (TennisLeagueScheduleDelegate) -> Unit
) : BaseScheduleStore<TennisLeagueScheduleAction, TennisLeagueScheduleDisplayModel>(model, nameProvider) {
    @AssistedFactory
    interface Factory {
        fun create(
            model: TennisLeagueScheduleDisplayModel,
            emitToParent: (TennisLeagueScheduleDelegate) -> Unit
        ): TennisLeagueScheduleStore
    }

    override fun send(action: TennisLeagueScheduleAction) {
//        when (action) {
//        }
    }

    override fun toggleAllResult() {
        TODO("Not yet implemented")
    }
}