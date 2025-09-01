package com.moare.android.features.search.display.football.viewmodel

import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.display.common.viewmodel.BaseStatsViewModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStatsDisplayModel
import com.moare.android.features.search.models.models.football.FBPlayerStats
import com.moare.android.features.search.models.models.football.FBTeamStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FBTeamStatsIntent {
    data class InitData(val displayModel: FBTeamStatsDisplayModel) : FBTeamStatsIntent()
}

@HiltViewModel
class FBTeamStatsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseStatsViewModel<FBTeamStatsIntent, FBTeamStatsDisplayModel>(nameProvider) {
    /* ---------------------
       data state
       --------------------- */
    private val _statsList = MutableStateFlow<List<FBTeamStats>>(emptyList())
    val statsList: StateFlow<List<FBTeamStats>> = _statsList

    override fun send(intent: FBTeamStatsIntent) {
        when (intent) {
            is FBTeamStatsIntent.InitData -> initData(intent.displayModel)
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: FBTeamStatsDisplayModel) {
        super.initData(displayModel)

        // 리그 기록을 제일 첫번째 아이템으로
        _statsList.value = displayModel.stats.sortedByDescending {
            Constants.Ids.FOOTBALL_LEAGUES.contains(it.league.id)
        }
    }

    /* ---------------------
       implements
       --------------------- */
}