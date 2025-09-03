package com.moare.android.features.search.display.football.viewmodel

import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.core.util.CalendarUtil
import com.moare.android.features.search.display.common.viewmodel.BaseStatsViewModel
import com.moare.android.features.search.models.displaymodels.football.FBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStatsDisplayModel
import com.moare.android.features.search.models.models.football.FBPlayerStats
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FBPlayerStatsIntent {
    data class InitData(val displayModel: FBPlayerStatsDisplayModel) : FBPlayerStatsIntent()
}

@HiltViewModel
class FBPlayerStatsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseStatsViewModel<FBPlayerStatsIntent, FBPlayerStatsDisplayModel>(nameProvider) {
    /* ---------------------
       data state
       --------------------- */
    private val _statsList = MutableStateFlow<List<FBPlayerStats>>(emptyList())
    val statsList: StateFlow<List<FBPlayerStats>> = _statsList

    override fun send(intent: FBPlayerStatsIntent) {
        when (intent) {
            is FBPlayerStatsIntent.InitData -> initData(intent.displayModel)
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: FBPlayerStatsDisplayModel) {
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