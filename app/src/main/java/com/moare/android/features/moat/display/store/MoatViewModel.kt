package com.moare.android.features.moat.display.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moare.android.features.moat.models.FireCreateRequest
import com.moare.android.features.moat.models.MoatCreateRequest
import com.moare.android.features.moat.models.MoatDetailResponse
import com.moare.android.features.moat.models.MoatListRequest
import com.moare.android.features.moat.models.MoatListResponse
import com.moare.android.features.moat.models.MoatResponse
import com.moare.android.features.moat.models.TargetType
import com.moare.android.features.moat.networking.MoatClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

sealed class MoatIntent {
    data object GetTrendingMoats : MoatIntent()
    data class SelectMoat(val isComment: Boolean = false, val moatId: String) : MoatIntent()
    data class CreateMoat(val content: String) : MoatIntent()
    data class AddViewStack(val moatViewType: MoatViewType) : MoatIntent()
    data object Goback : MoatIntent()
    data class CheckFire(val targetId: String) : MoatIntent()
    data class ToggleFire(val targetId: String, val targetType: TargetType) : MoatIntent()
}

enum class MoatViewType {
    TRENDING, DETAIL, FORM // CREATE_FORM, UPDATE_FORM
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
    private val _currentViewType = MutableStateFlow(MoatViewType.TRENDING)
    val currentViewType: StateFlow<MoatViewType> = _currentViewType

    private val _viewStack = MutableStateFlow<List<MoatViewType>>(emptyList())
    val viewStack: StateFlow<List<MoatViewType>> = _viewStack

    private val _poppedView = MutableStateFlow<MoatViewType?>(null)
    val poppedView: StateFlow<MoatViewType?> = _poppedView

    private val _moatListResponse = MutableStateFlow<MoatListResponse?>(null)
    val moatListResponse: StateFlow<MoatListResponse?> = _moatListResponse

    private val _originalTrendingMoats = MutableStateFlow<List<MoatResponse>>(emptyList())
    val originalTrendingMoats: StateFlow<List<MoatResponse>> = _originalTrendingMoats

    private val _trendingMoats = MutableStateFlow<List<MoatResponse>>(emptyList())
    val trendingMoats: StateFlow<List<MoatResponse>> = _trendingMoats

    private val _selectedMoat = MutableStateFlow<MoatDetailResponse?>(null)
    val selectedMoat: StateFlow<MoatDetailResponse?> = _selectedMoat

    private val _fireMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val fireMap: StateFlow<Map<String, Boolean>> = _fireMap

    private val _fireCountMap = MutableStateFlow<Map<String, Int>>(emptyMap())
    val fireCountMap: StateFlow<Map<String, Int>> = _fireCountMap

    fun send(intent: MoatIntent) {
        viewModelScope.launch {
            when (intent) {
                is MoatIntent.GetTrendingMoats -> getTrendingMoats()
                is MoatIntent.SelectMoat -> selectMoat(intent.isComment, intent.moatId)
                is MoatIntent.CreateMoat -> createMoat(intent.content)
                is MoatIntent.AddViewStack -> addViewStack(intent.moatViewType)
                is MoatIntent.Goback -> goback()
                is MoatIntent.CheckFire -> checkFire(intent.targetId)
                is MoatIntent.ToggleFire -> toggleFire(intent.targetId, intent.targetType)
            }
        }
    }

    private suspend fun getTrendingMoats() {
        val body = MoatListRequest(nextToken = moatListResponse.value?.nextToken)

        try {
            val result = moatClient.fetchTrendingMoats(body)

            updateTrendingMoats(result)
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
        val originalTrendingMoats = _originalTrendingMoats.value

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
                    var trendingMoats = originalTrendingMoats
                    trendingMoats.toMutableList().apply { add(result) }

                    var moatList = moatListResponse
                    moatList.moats = trendingMoats

                    updateTrendingMoats(moatList)
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun updateTrendingMoats(moatListResponse: MoatListResponse) {
        _moatListResponse.value = moatListResponse
        _originalTrendingMoats.value = moatListResponse.moats
        _trendingMoats.value = moatListResponse.moats
    }

    private fun updateSelectedMoat(isComment: Boolean, moatDetailResponse: MoatDetailResponse) {
        if (isComment) {
            _selectedMoat.value = moatDetailResponse
            _trendingMoats.value = listOf(moatDetailResponse.moat)
        } else {
            _selectedMoat.value = moatDetailResponse
            _trendingMoats.value = _trendingMoats.value.filter {
                it.moatId == moatDetailResponse.moat.moatId
            }
        }
    }

    private fun addViewStack(viewType: MoatViewType) {
        _viewStack.update { it + viewType }
        _currentViewType.value = viewType
    }

    private fun goback() {
        var popLastView: MoatViewType? = null

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
            _currentViewType.value = MoatViewType.TRENDING

            _selectedMoat.value = null
            _trendingMoats.value = _originalTrendingMoats.value
        }
    }

    private suspend fun checkFire(targetId: String) {
        try {
            val result = moatClient.checkFire(targetId)

            setFireMap(targetId, result)
        } catch (e: Exception) {

        }
    }

    private fun setFireMap(targetId: String, result: Boolean) {
        _fireMap.update { it ->
            it + (targetId to result)
        }
    }

    private suspend fun createFire(targetId: String, targetType: TargetType) {
        val fireCreateRequest = FireCreateRequest(targetId = targetId, targetType = targetType)

        try {
            moatClient.createFire(fireCreateRequest)
        } catch (e: Exception) {
            // 파이어를 눌렀지만 서버로 전송이 안 된 경우
            _fireMap.update { it ->
                it + (targetId to false)
            }

            val firstFireCount = fireCountMap.value[targetId]
                ?: trendingMoats.value.firstOrNull { it.moatId == targetId }?.fireCount
                ?: 0

            _fireCountMap.update { it ->
                it + (targetId to max(0, firstFireCount - 1))
            }
        }
    }

    private suspend fun deleteFire(targetId: String) {
        try {
            moatClient.deleteFire(targetId)
        } catch (e: Exception) {
            // 파이어를 취소했지만 서버로 전송이 안 된 경우
            _fireMap.update {
                it + (targetId to true)
            }

            val firstFireCount = fireCountMap.value[targetId]
                ?: trendingMoats.value.firstOrNull { it.moatId == targetId }?.fireCount
                ?: 0

            _fireCountMap.update {
                it + (targetId to (firstFireCount + 1))
            }
        }
    }

    private suspend fun toggleFire(targetId: String, targetType: TargetType) {
        val isFired = fireMap.value[targetId] ?: false
        _fireMap.update {
            it + (targetId to !isFired)
        }

        val firstFireCount = fireCountMap.value[targetId]
            ?: trendingMoats.value.firstOrNull { it.moatId == targetId }?.fireCount
            ?: 0

        val updateFireCount = if (isFired) {
            max(0, firstFireCount - 1)
        } else {
            firstFireCount + 1
        }

        _fireCountMap.update {
            it + (targetId to updateFireCount)
        }

        if (isFired) {
            _fireMap.update {
                it + (targetId to false)
            }

            deleteFire(targetId)
        } else {
            _fireMap.update {
                it + (targetId to true)
            }

            createFire(targetId, targetType)
        }
    }
}