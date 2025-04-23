package com.moare.android.features.search.display.football.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.amazonaws.services.translate.AmazonTranslateClient
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.models.football.FBPlayerInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FBPlayerInfoViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : MVIViewModel<FBPlayerInfoViewModel.Intent, FBPlayerInfoDisplayModel>() {
    /* ---------------------
       constants
       --------------------- */
    val itemHeight = 30.dp

    /* ---------------------
       data state
       --------------------- */
    private val _displayModel = MutableStateFlow<FBPlayerInfoDisplayModel?>(null)
    val displayModel: StateFlow<FBPlayerInfoDisplayModel?> = _displayModel

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
        data class InitData(val displayModel: FBPlayerInfoDisplayModel) : Intent()
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
    override fun initData(displayModel: FBPlayerInfoDisplayModel) {
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