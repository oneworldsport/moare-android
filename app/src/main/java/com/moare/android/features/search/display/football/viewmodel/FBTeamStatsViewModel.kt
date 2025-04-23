package com.moare.android.features.search.display.football.viewmodel

import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStatsDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FBTeamStatsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : MVIViewModel<FBTeamStatsViewModel.Intent, FBTeamStatsDisplayModel>() {
    /* ---------------------
       constants
       --------------------- */

    /* ---------------------
       data state
       --------------------- */
    private val _displayModel = MutableStateFlow<FBTeamStatsDisplayModel?>(null)
    val displayModel: StateFlow<FBTeamStatsDisplayModel?> = _displayModel

    /* ---------------------
       ui state
       --------------------- */

    /* ---------------------
       etc
       --------------------- */
    var teamNameDictionary: Map<String, String> = emptyMap()

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data class InitData(val displayModel: FBTeamStatsDisplayModel) : Intent()
    }

    override fun send(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.InitData -> initData(intent.displayModel)
            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: FBTeamStatsDisplayModel) {
        viewModelScope.launch {
            _displayModel.emit(displayModel)

            when (displayModel.leagueId) {
                Constants.Ids.EPL -> {
                    teamNameDictionary = nameProvider.getDictionary(Constants.Keys.EPL_TEAM_DIC)
                }
                Constants.Ids.LALIGA -> {
                    teamNameDictionary = nameProvider.getDictionary(Constants.Keys.LALIGA_TEAM_DIC)
                }
                Constants.Ids.BUNDESLIGA -> {
                    teamNameDictionary = nameProvider.getDictionary(Constants.Keys.BUNDESLIGA_TEAM_DIC)
                }
                Constants.Ids.LIGUE1 -> {
                    teamNameDictionary = nameProvider.getDictionary(Constants.Keys.LIGUE1_TEAM_DIC)
                }
                else -> {}
            }
        }
    }

    /* ---------------------
       implements
       --------------------- */
}