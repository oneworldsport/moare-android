package com.moare.android.features.userprofile.display.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moare.android.features.moat.models.FireCreateRequest
import com.moare.android.features.moat.models.MoatDetailResponse
import com.moare.android.features.moat.models.MoatListResponse
import com.moare.android.features.moat.models.MoatResponse
import com.moare.android.features.moat.models.TargetType
import com.moare.android.features.moat.networking.MoatClient
import com.moare.android.features.userprofile.models.UserProfileResponse
import com.moare.android.features.userprofile.models.UserProfileWithMoatsResponse
import com.moare.android.features.userprofile.networking.UserProfileClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.plus
import kotlin.math.max

sealed class UserProfileIntent {
    data object GetUserProfile : UserProfileIntent()
    data class SelectMoat(val isComment: Boolean = false, val moatId: String) : UserProfileIntent()
    data object Goback : UserProfileIntent()
    data class CheckFire(val targetId: String) : UserProfileIntent()
    data class ToggleFire(val targetId: String, val targetType: TargetType) : UserProfileIntent()
}

enum class UserProfileViewType {
    USER_PROFILE, MOAT_DETAIL, PROFILE_UPDATE_FORM
}

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userProfileClient: UserProfileClient,
    private val moatClient: MoatClient
): ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfileResponse?>(null)
    val userProfile: StateFlow<UserProfileResponse?> = _userProfile

    private val _moatListResponse = MutableStateFlow<MoatListResponse?>(null)
    val moatListResponse: StateFlow<MoatListResponse?> = _moatListResponse

    private val _userMoats = MutableStateFlow<List<MoatResponse>>(emptyList())
    val userMoats: StateFlow<List<MoatResponse>> = _userMoats

    private val _currentViewType = MutableStateFlow(UserProfileViewType.USER_PROFILE)
    val currentViewType: StateFlow<UserProfileViewType> = _currentViewType

    private val _viewStack = MutableStateFlow<List<UserProfileViewType>>(emptyList())
    val viewStack: StateFlow<List<UserProfileViewType>> = _viewStack

    private val _poppedView = MutableStateFlow<UserProfileViewType?>(null)
    val poppedView: StateFlow<UserProfileViewType?> = _poppedView

    private val _selectedMoat = MutableStateFlow<MoatDetailResponse?>(null)
    val selectedMoat: StateFlow<MoatDetailResponse?> = _selectedMoat

    private val _originalUserMoats = MutableStateFlow<List<MoatResponse>>(emptyList())
    val originalUserMoats: StateFlow<List<MoatResponse>> = _originalUserMoats

    private val _fireMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val fireMap: StateFlow<Map<String, Boolean>> = _fireMap

    private val _fireCountMap = MutableStateFlow<Map<String, Int>>(emptyMap())
    val fireCountMap: StateFlow<Map<String, Int>> = _fireCountMap

    fun send(intent: UserProfileIntent) {
        viewModelScope.launch {
            when (intent) {
                is UserProfileIntent.GetUserProfile -> getUserProfile()
                is UserProfileIntent.SelectMoat -> selectMoat(intent.isComment, intent.moatId)
                is UserProfileIntent.Goback -> goBack()
                is UserProfileIntent.CheckFire -> checkFire(intent.targetId)
                is UserProfileIntent.ToggleFire -> toggleFire(intent.targetId, intent.targetType)

            }
        }
    }

    private suspend fun getUserProfile() {
        try {
            val result = userProfileClient.fetchUserProfile()

            setUserProfile(result)
        } catch (e: Exception) {

        }
    }

    // updateUserProfile

    private fun setUserProfile(userProfile: UserProfileWithMoatsResponse) {
        _userProfile.value =  userProfile.userProfile
        _moatListResponse.value = userProfile.moatListResponse
        _userMoats.value = userProfile.moatListResponse?.moats ?: listOf()
        _originalUserMoats.value = userProfile.moatListResponse?.moats ?: listOf()
    }

    private suspend fun selectMoat(isComment: Boolean, moatId: String) {
        try {
            val result = moatClient.fetchMoatDetail(moatId)

            updateSelectedMoat(isComment, result)

            addViewStack(UserProfileViewType.MOAT_DETAIL)
        } catch (e: Exception) {

        }
    }

    private fun updateSelectedMoat(isComment: Boolean, moatDetailResponse: MoatDetailResponse) {
        if (isComment) {
            _selectedMoat.value = moatDetailResponse
            _userMoats.value = listOf(moatDetailResponse.moat)
        } else {
            _selectedMoat.value = moatDetailResponse
            _userMoats.value = userMoats.value.filter {
                it.moatId == moatDetailResponse.moat.moatId
            }
        }
    }

    private fun addViewStack(viewType: UserProfileViewType) {
        _viewStack.value = viewStack.value + viewType
//        viewStack = if (viewStack.isNotEmpty()) viewStack.dropLast(1) else viewStack
        _currentViewType.value = viewType
    }

    private fun goBack() {
        val lastView = viewStack.value.lastOrNull()
        _poppedView.value = lastView

        val viewToShow = viewStack.value.dropLast(1).lastOrNull()

        if (viewToShow != null) {
            when (viewToShow) {
                UserProfileViewType.MOAT_DETAIL -> _currentViewType.value = viewToShow
                else -> {}
            }
        } else {
            _currentViewType.value = UserProfileViewType.USER_PROFILE

            _selectedMoat.value = null

            _userMoats.value = originalUserMoats.value
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
                ?: userMoats.value.firstOrNull { it.moatId == targetId }?.fireCount
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
                ?: userMoats.value.firstOrNull { it.moatId == targetId }?.fireCount
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
            ?: userMoats.value.firstOrNull { it.moatId == targetId }?.fireCount
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