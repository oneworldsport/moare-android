package com.moare.android.features.search.display.football.viewmodel

import androidx.lifecycle.viewModelScope
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamInfoDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FBTeamInfoViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : MVIViewModel<FBTeamInfoViewModel.Intent, FBTeamInfoDisplayModel>() {
    /* ---------------------
       constants
       --------------------- */

    /* ---------------------
       data state
       --------------------- */
    private val _displayModel = MutableStateFlow<FBTeamInfoDisplayModel?>(null)
    val displayModel: StateFlow<FBTeamInfoDisplayModel?> = _displayModel

    /* ---------------------
       ui state
       --------------------- */

    /* ---------------------
       etc
       --------------------- */
    var teamNameDictionary: Map<String, String> = emptyMap()

    init {
        teamNameDictionary = nameProvider.getDictionary("nba_team")
    }

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data class InitData(val displayModel: FBTeamInfoDisplayModel) : Intent()
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
    override fun initData(displayModel: FBTeamInfoDisplayModel) {
        viewModelScope.launch {
            _displayModel.emit(displayModel)
        }
    }

    /* ---------------------
       implements
       --------------------- */
}