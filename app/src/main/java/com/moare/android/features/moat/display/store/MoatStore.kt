package com.moare.android.features.moat.display.store

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.moare.android.core.store.BaseStore
import com.moare.android.features.moat.display.MoatViewType
import com.moare.android.features.moat.models.FireCreateRequest
import com.moare.android.features.moat.models.MoatCreateRequest
import com.moare.android.features.moat.models.MoatDetailResponse
import com.moare.android.features.moat.models.MoatListRequest
import com.moare.android.features.moat.models.MoatListResponse
import com.moare.android.features.moat.models.MoatResponse
import com.moare.android.features.moat.models.TargetType
import com.moare.android.features.moat.networking.MoatClient
import com.moare.android.features.search.display.mlb.store.MLBPlayerInfoDelegate
import com.moare.android.features.search.display.search.store.SearchDelegate
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

sealed interface MoatAction {
    data object GetTrendingMoats : MoatAction
    data class SelectMoat(val moatId: String) : MoatAction
    data class GetMoatDetail(val moatId: String) : MoatAction
    data class CreateMoat(val content: String) : MoatAction

    data object ShowTrending : MoatAction
    data object ShowMoatForm : MoatAction

    data class ToggleFire(val targetId: String, val targetType: TargetType) : MoatAction
}

sealed interface MoatDelegate {
    data class Push(val viewType: MoatViewType, val moatId: String? = null) : MoatDelegate
}

class MoatStore @AssistedInject constructor(
    private val moatClient: MoatClient,
    private val dataStore: DataStore<Preferences>,
    @Assisted val moatId: String?,
    @Assisted val emitToParent: (MoatDelegate) -> Unit
) : BaseStore<MoatAction>() {

    // TODO: 나중에 삭제
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

    @AssistedFactory
    interface Factory {
        fun create(
            moatId: String? = null,
            emitToParent: (MoatDelegate) -> Unit
        ) : MoatStore
    }

    var isDetail: Boolean = moatId != null // moatId가 있거나, selectedMoat가 있으면 detail 화면임. 사용하기 편하려고 만든 프로퍼티.

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

    override fun send(action: MoatAction) {
        when (action) {
            is MoatAction.GetTrendingMoats -> getTrendingMoats()
            is MoatAction.SelectMoat -> selectMoat(action.moatId)
            is MoatAction.GetMoatDetail -> getMoatDetail(action.moatId)
            is MoatAction.CreateMoat -> createMoat(action.content)
            is MoatAction.ShowTrending -> showTrending()
            is MoatAction.ShowMoatForm -> showMoatForm()
            is MoatAction.ToggleFire -> toggleFire(action.targetId, action.targetType)
        }
    }

    private fun getTrendingMoats() {
        val body = MoatListRequest(
            sportTags = listOf("축구", "야구"),
            nextToken = moatListResponse.value?.nextToken
        )
        scope.launch {
            try {
                val result = moatClient.fetchTrendingMoats(body)

                _moatListResponse.value = result
                _originalTrendingMoats.value = result.moats
                _trendingMoats.value = result.moats
            } catch (e: Exception) {
                Log.e("moats/trending", e.localizedMessage)
            }
        }
    }

    private fun selectMoat(moatId: String) {
        scope.launch {
            if (isDetail) {
                emitToParent(MoatDelegate.Push(viewType = MoatViewType.DETAIL, moatId = moatId))
            } else {
                getMoatDetail(moatId)
            }
        }
    }

    private fun getMoatDetail(moatId: String) {
        scope.launch {
            val result = moatClient.fetchMoatDetail(moatId)

            updateSelectedMoat(result)
        }
    }

    private fun createMoat(content: String) {
        val moat = _selectedMoat.value

        scope.launch {
            if (isDetail && moat != null) {
                val moatRequest = MoatCreateRequest(content, listOf("축구"), moat.moat.moatId)

                try {
                    val result = moatClient.createMoat(moatRequest)

                    val comments = (moat.commentListResponse?.moats ?: emptyList())
                        .toMutableList()
                        .apply { add(result) }

                    val commentListResponse = moat.commentListResponse
                    commentListResponse?.moats = comments

                    val newMoatDetail = moat
                    newMoatDetail.commentListResponse = commentListResponse

                    updateSelectedMoat(newMoatDetail)
                } catch (e: Exception) {

                }
            }

//            else if (currentViewType == MoatViewType.CREATE_FORM) {
//                val moatRequest = MoatCreateRequest(content, listOf("#축구"))
//
//                try {
//                    val result = moatClient.createMoat(moatRequest)
//
//                    goback()
//
//                    if (moatListResponse != null) {
//                        var trendingMoats = originalTrendingMoats
//                        trendingMoats.toMutableList().apply { add(result) }
//
//                        var moatList = moatListResponse
//                        moatList.moats = trendingMoats
//
//                        updateTrendingMoats(moatList)
//                    }
//                } catch (e: Exception) {
//
//                }
//            }
        }
    }

    private fun updateSelectedMoat(moatDetailResponse: MoatDetailResponse) {
        _selectedMoat.value = moatDetailResponse
        isDetail = true

        if (isDetail) {
            _trendingMoats.value = listOf(moatDetailResponse.moat)
        } else {
            _trendingMoats.value = _trendingMoats.value.filter {
                it.moatId == moatDetailResponse.moat.moatId
            }
        }
    }

    private fun showTrending() {
        _selectedMoat.value = null
        isDetail = false
        _trendingMoats.value = originalTrendingMoats.value
    }

    private fun showMoatForm() {
        emitToParent(MoatDelegate.Push(viewType = MoatViewType.CREATE_FORM))
    }

    private fun setFireMap(targetId: String, result: Boolean) {
        _fireMap.update { it ->
            it + (targetId to result)
        }
    }

    private fun createFire(targetId: String, targetType: TargetType) {
        val fireCreateRequest = FireCreateRequest(targetId = targetId, targetType = targetType)

        scope.launch {
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
    }

    private fun deleteFire(targetId: String) {
        scope.launch {
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
    }

    private fun toggleFire(targetId: String, targetType: TargetType) {
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