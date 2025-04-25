package com.moare.android.features.search.display.football.viewmodel

import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStatsDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FBPlayerStatsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : MVIViewModel<FBPlayerStatsViewModel.Intent, FBPlayerStatsDisplayModel>() {
    /* ---------------------
       constants
       --------------------- */

    /* ---------------------
       data state
       --------------------- */
    private val _displayModel = MutableStateFlow<FBPlayerStatsDisplayModel?>(null)
    val displayModel: StateFlow<FBPlayerStatsDisplayModel?> = _displayModel

    /* ---------------------
       ui state
       --------------------- */

    /* ---------------------
       etc
       --------------------- */
    var playerNameDictionary: Map<String, String> = emptyMap()
    var teamNameDictionary: Map<String, String> = emptyMap()

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data class InitData(val displayModel: FBPlayerStatsDisplayModel) : Intent()
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
    override fun initData(displayModel: FBPlayerStatsDisplayModel) {
        viewModelScope.launch {
            _displayModel.emit(displayModel)

            when (displayModel.leagueId) {
                Constants.Ids.EPL -> {
                    playerNameDictionary = nameProvider.getDictionary(Constants.Keys.EPL_PLAYER_DIC)
                    teamNameDictionary = nameProvider.getDictionary(Constants.Keys.EPL_TEAM_DIC)
                }
                Constants.Ids.LALIGA -> {
                    playerNameDictionary = nameProvider.getDictionary(Constants.Keys.LALIGA_PLAYER_DIC)
                    teamNameDictionary = nameProvider.getDictionary(Constants.Keys.LALIGA_TEAM_DIC)
                }
                Constants.Ids.BUNDESLIGA -> {
                    playerNameDictionary = nameProvider.getDictionary(Constants.Keys.BUNDESLIGA_PLAYER_DIC)
                    teamNameDictionary = nameProvider.getDictionary(Constants.Keys.BUNDESLIGA_TEAM_DIC)
                }
                Constants.Ids.LIGUE1 -> {
                    playerNameDictionary = nameProvider.getDictionary(Constants.Keys.LIGUE1_PLAYER_DIC)
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