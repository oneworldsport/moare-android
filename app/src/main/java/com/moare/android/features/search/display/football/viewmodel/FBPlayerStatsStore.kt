package com.moare.android.features.search.display.football.viewmodel

import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BaseStatsStore
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStatsDisplayModel
import com.moare.android.features.search.models.models.football.FBPlayerStats
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed interface FBPlayerStatsAction {
    data object InitData : FBPlayerStatsAction
}

class FBPlayerStatsStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: FBPlayerStatsDisplayModel
) : BaseStatsStore<FBPlayerStatsAction, FBPlayerStatsDisplayModel>(initial, nameProvider) {
    private val _statsList = MutableStateFlow<List<FBPlayerStats>>(emptyList())
    val statsList: StateFlow<List<FBPlayerStats>> = _statsList

    @AssistedFactory
    interface Factory {
        fun create(displayModel: FBPlayerStatsDisplayModel) : FBPlayerStatsStore
    }

    override fun send(action: FBPlayerStatsAction) {
        when (action) {
            is FBPlayerStatsAction.InitData -> initData()
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