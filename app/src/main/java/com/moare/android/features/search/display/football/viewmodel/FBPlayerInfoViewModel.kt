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
import com.moare.android.features.search.display.common.viewmodel.BaseInfoState
import com.moare.android.features.search.display.common.viewmodel.BaseInfoViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplayModel
import com.moare.android.features.search.models.models.football.FBPlayerInfo
import com.moare.android.features.search.models.responsemodels.football.FBPlayerInfoResponseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FBPlayerInfoIntent {
    data class InitData(val displayModel: FBPlayerInfoDisplayModel) : FBPlayerInfoIntent()
}

data class FBPlayerInfoState(
    val responseModel: FBPlayerInfoResponseModel,
    val baseInfo: BaseInfoState<FBPlayerInfoDisplayModel>
) {
    val displayModel get() = baseInfo.displayModel
    val playerNameDic get() = baseInfo.playerNameDic
    val teamNameDic get() = baseInfo.teamNameDic
}

sealed class FBPlayerInfoAction {
    data class InitData(val displayModel: FBPlayerInfoDisplayModel) : FBPlayerInfoAction()
}

class FBPlayerInfoReducer {
    fun reduce(
        state: FBPlayerInfoState,
        action: FBPlayerInfoAction
    ) {
        when (action) {
            is FBPlayerInfoAction.InitData -> {
                Log.d("sssss", "sssss")
            }
        }
    }
}


class FBPlayerInfoViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseInfoViewModel<FBPlayerInfoIntent, FBPlayerInfoDisplayModel>(nameProvider) {
    /* ---------------------
       constants
       --------------------- */
    val itemHeight = 30.dp

    override fun send(intent: FBPlayerInfoIntent) {
        when (intent) {
            is FBPlayerInfoIntent.InitData -> initData(intent.displayModel)
        }
    }

    /* ---------------------
       implements
       --------------------- */
}