package com.moare.android.features.search.display.football.store

import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.store.BaseStatsStore
import com.moare.android.features.search.models.displaymodels.football.FBTeamStatsDisplayModel
import com.moare.android.features.search.models.models.football.FBTeamStats
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface FBTeamStatsAction {
    data object InitData : FBTeamStatsAction
}

class FBTeamStatsStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: FBTeamStatsDisplayModel
) : BaseStatsStore<FBTeamStatsAction, FBTeamStatsDisplayModel>(initial, nameProvider) {
    private val _statsList = MutableStateFlow<List<FBTeamStats>>(emptyList())
    val statsList: StateFlow<List<FBTeamStats>> = _statsList

    @AssistedFactory
    interface Factory {
        fun create(displayModel: FBTeamStatsDisplayModel) : FBTeamStatsStore
    }

    override fun send(action: FBTeamStatsAction) {
        when (action) {
            is FBTeamStatsAction.InitData -> initData()
        }
    }

    override fun initData() {
        super.initData()

        // 리그 기록을 제일 첫번째 아이템으로
        _statsList.value = displayModel.value.stats.sortedByDescending {
            Constants.Ids.FOOTBALL_LEAGUES.contains(it.league.id)
        }
    }
}