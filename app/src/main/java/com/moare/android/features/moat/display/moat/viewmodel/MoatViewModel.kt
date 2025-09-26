package com.moare.android.features.moat.display.moat.viewmodel

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moare.android.features.moat.models.MoatCreateRequest
import com.moare.android.features.moat.models.MoatDetailResponse
import com.moare.android.features.moat.models.MoatListRequest
import com.moare.android.features.moat.models.MoatListResponse
import com.moare.android.features.moat.models.MoatResponse
import com.moare.android.features.moat.networking.MoatClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
sealed class MoatIntent {
    data object GetTimelineMoats : MoatIntent()
    data class SelectMoat(val isComment: Boolean = false, val moatId: String) : MoatIntent()
    data class CreateMoat(val content: String) : MoatIntent()
    data class AddViewStack(val moatViewType: MoatViewType) : MoatIntent()
    data object Goback : MoatIntent()
}

enum class MoatViewType {
    TIMELINE, DETAIL, FORM // CREATE_FORM, UPDATE_FORM
}

@HiltViewModel
class MoatViewModel @Inject constructor(
    private val moatClient: MoatClient,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    // 로그인
    val idToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[stringPreferencesKey("idToken")]
        }
    val accessToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[stringPreferencesKey("accessToken")]
        }
    val refreshToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[stringPreferencesKey("refreshToken")]
        }

    // moat
    private val _currentViewType = MutableStateFlow(MoatViewType.TIMELINE)
    val currentViewType: StateFlow<MoatViewType> = _currentViewType

    private val _viewStack = MutableStateFlow<List<MoatViewType>>(emptyList())
    val viewStack: StateFlow<List<MoatViewType>> = _viewStack

    private val _poppedView = MutableStateFlow<MoatViewType?>(null)
    val poppedView: StateFlow<MoatViewType?> = _poppedView

    private val _moatListResponse = MutableStateFlow<MoatListResponse?>(null)
    val moatListResponse: StateFlow<MoatListResponse?> = _moatListResponse

    private val _originalTimelineMoats = MutableStateFlow<List<MoatResponse>>(emptyList())
    val originalTimelineMoats: StateFlow<List<MoatResponse>> = _originalTimelineMoats

    private val _timelineMoats = MutableStateFlow<List<MoatResponse>>(emptyList())
    val timelineMoats: StateFlow<List<MoatResponse>> = _timelineMoats

    private val _selectedMoat = MutableStateFlow<MoatDetailResponse?>(null)
    val selectedMoat: StateFlow<MoatDetailResponse?> = _selectedMoat

    fun send(intent: MoatIntent) {
        viewModelScope.launch {
            when (intent) {
                is MoatIntent.GetTimelineMoats -> getTimelineMoats()
                is MoatIntent.SelectMoat -> selectMoat(intent.isComment, intent.moatId)
                is MoatIntent.CreateMoat -> createMoat(intent.content)
                is MoatIntent.AddViewStack -> addViewStack(intent.moatViewType)
                is MoatIntent.Goback -> goback()
            }
        }
    }

    private suspend fun getTimelineMoats() {
        val body = MoatListRequest(nextToken = moatListResponse.value?.nextToken)

        try {
            val result = moatClient.fetchTimelineMoats(body)

            updateTimelineMoats(result)
        } catch (e: Exception) {

        }
    }

    private suspend fun selectMoat(isComment: Boolean = false, moatId: String) {
        try {
            val result = moatClient.fetchMoatDetail(moatId)

            updateSelectedMoat(isComment, result)

            // TODO: 화면 먼저 보여주고 결과 띄워야하기때문에 실행시점 고민 필요
            addViewStack(MoatViewType.DETAIL)
        } catch (e: Exception) {

        }
    }

    private suspend fun createMoat(content: String) {
        val moat = _selectedMoat.value
        val currentViewType = _currentViewType.value
        val moatListResponse = _moatListResponse.value
        val originalTimeLineMoats = _originalTimelineMoats.value

        if (currentViewType == MoatViewType.DETAIL && moat != null) {
            val moatRequest = MoatCreateRequest(content, listOf("#축구"), moat.moat.moatId)

            try {
                val result = moatClient.createMoat(moatRequest)

                var comments = (moat.commentListResponse?.moats ?: emptyList())
                    .toMutableList()
                    .apply { add(result) }

                var commentListResponse = moat.commentListResponse

                commentListResponse?.moats = comments

                var newMoatDetail = moat

                newMoatDetail.commentListResponse = commentListResponse

                updateSelectedMoat(false, newMoatDetail)
            } catch (e: Exception) {

            }
        } else if (currentViewType == MoatViewType.FORM) {
            val moatRequest = MoatCreateRequest(content, listOf("#축구"))

            try {
                val result = moatClient.createMoat(moatRequest)

                goback()

                if (moatListResponse != null) {
                    var timelineMoats = originalTimeLineMoats
                    timelineMoats.toMutableList().apply { add(result) }

                    var moatList = moatListResponse
                    moatList.moats = timelineMoats

                    updateTimelineMoats(moatList)
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun updateTimelineMoats(moatListResponse: MoatListResponse) {
        _moatListResponse.value = moatListResponse
        _originalTimelineMoats.value = moatListResponse.moats
        _timelineMoats.value = moatListResponse.moats
    }

    private fun updateSelectedMoat(isComment: Boolean, moatDetailResponse: MoatDetailResponse) {
        if (isComment) {
            _selectedMoat.value = moatDetailResponse
            _timelineMoats.value = listOf(moatDetailResponse.moat)
        } else {
            _selectedMoat.value = moatDetailResponse
            _timelineMoats.value = _timelineMoats.value.filter {
                it.moatId == moatDetailResponse.moat.moatId
            }
        }
    }

    private fun addViewStack(viewType: MoatViewType) {
        _viewStack.update { it + viewType }
        _currentViewType.value = viewType
    }

    private fun goback() {
        var popLastView:MoatViewType? = null

        _viewStack.update { list ->
            popLastView = list.lastOrNull()
            if (popLastView == null) {
                list
            } else {
                list.dropLast(1)
            }
        }

        val lastView = popLastView

        _poppedView.value = lastView

        val viewToShow = _viewStack.value.lastOrNull()

        if (viewToShow != null) {
            when (viewToShow) {
                MoatViewType.DETAIL -> _currentViewType.value = viewToShow // TODO: 이전 selectedMoat를 다 저장해서 처리해줘야함.
                MoatViewType.FORM -> _currentViewType.value = viewToShow
                else -> {}
            }
        } else {
            // 뒤로갈 뷰가 없는 경우. 즉, 메인 화면으로 이동하는 경우.
            _currentViewType.value = MoatViewType.TIMELINE

            _selectedMoat.value = null
            _timelineMoats.value = _originalTimelineMoats.value
        }
    }
}