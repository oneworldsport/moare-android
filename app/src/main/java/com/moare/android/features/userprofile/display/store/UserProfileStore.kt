package com.moare.android.features.userprofile.display.store

import androidx.lifecycle.viewModelScope
import com.moare.android.core.store.BaseStore
import com.moare.android.features.moat.models.FireCreateRequest
import com.moare.android.features.moat.models.MoatDetailResponse
import com.moare.android.features.moat.models.MoatListResponse
import com.moare.android.features.moat.models.MoatResponse
import com.moare.android.features.moat.models.TargetType
import com.moare.android.features.moat.networking.MoatClient
import com.moare.android.features.userprofile.display.UserProfileViewType
import com.moare.android.features.userprofile.models.UserProfileResponse
import com.moare.android.features.userprofile.models.UserProfileWithMoatsResponse
import com.moare.android.features.userprofile.networking.UserProfileClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.plus
import kotlin.math.max

sealed interface UserProfileAction {
    data object GetUserProfile : UserProfileAction
    data class SelectMoat(val isComment: Boolean = false, val moatId: String) : UserProfileAction

    data object ShowUserProfile : UserProfileAction
    data object ShowUserProfileUpdateForm : UserProfileAction

    data class ToggleFire(val targetId: String, val targetType: TargetType) : UserProfileAction
}

sealed interface UserProfileDelegate {
    data class Push(val viewType: UserProfileViewType, val moatId: String? = null, val userProfile: UserProfileResponse? = null) : UserProfileDelegate
}

class UserProfileStore @AssistedInject constructor(
    private val userProfileClient: UserProfileClient,
    private val moatClient: MoatClient,
    @Assisted val emitToParent: (UserProfileDelegate) -> Unit
) : BaseStore<UserProfileAction>() {

    @AssistedFactory
    interface Factory {
        fun create(
            emitToParent: (UserProfileDelegate) -> Unit
        ) : UserProfileStore
    }

    private val _userProfile = MutableStateFlow<UserProfileResponse?>(null)
    val userProfile: StateFlow<UserProfileResponse?> = _userProfile

    private val _moatListResponse = MutableStateFlow<MoatListResponse?>(null)
    val moatListResponse: StateFlow<MoatListResponse?> = _moatListResponse

    private val _userMoats = MutableStateFlow<List<MoatResponse>>(emptyList())
    val userMoats: StateFlow<List<MoatResponse>> = _userMoats

    private val _selectedMoat = MutableStateFlow<MoatDetailResponse?>(null)
    val selectedMoat: StateFlow<MoatDetailResponse?> = _selectedMoat

    private val _originalUserMoats = MutableStateFlow<List<MoatResponse>>(emptyList())
    val originalUserMoats: StateFlow<List<MoatResponse>> = _originalUserMoats

    private val _fireMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val fireMap: StateFlow<Map<String, Boolean>> = _fireMap

    private val _fireCountMap = MutableStateFlow<Map<String, Int>>(emptyMap())
    val fireCountMap: StateFlow<Map<String, Int>> = _fireCountMap

    override fun send(action: UserProfileAction) {
        when (action) {
            is UserProfileAction.GetUserProfile -> getUserProfile()
            is UserProfileAction.SelectMoat -> selectMoat(action.isComment, action.moatId)
            is UserProfileAction.ShowUserProfile -> showUserProfile()
            is UserProfileAction.ShowUserProfileUpdateForm -> showUserProfileUpdateForm()
            is UserProfileAction.ToggleFire -> toggleFire(action.targetId, action.targetType)

        }
    }

    private fun getUserProfile() {
        scope.launch {
            try {
                val result = userProfileClient.fetchUserProfile()

                setUserProfile(result)
            } catch (e: Exception) {

            }
        }
    }

    private fun setUserProfile(userProfile: UserProfileWithMoatsResponse) {
        _userProfile.value =  userProfile.userProfile
        _moatListResponse.value = userProfile.moatListResponse
        _userMoats.value = userProfile.moatListResponse?.moats ?: listOf()
        _originalUserMoats.value = userProfile.moatListResponse?.moats ?: listOf()
    }

    private fun selectMoat(isComment: Boolean, moatId: String) {
        if (selectedMoat.value != null) {
            emitToParent(UserProfileDelegate.Push(viewType = UserProfileViewType.MOAT_DETAIL, moatId = moatId))
        } else {
            scope.launch {
                try {
                    val result = moatClient.fetchMoatDetail(moatId)

                    updateSelectedMoat(result)
                } catch (e: Exception) {

                }
            }
        }
    }

    private fun updateSelectedMoat(moatDetailResponse: MoatDetailResponse) {
        _selectedMoat.value = moatDetailResponse
        _userMoats.value = userMoats.value.filter {
            it.moatId == moatDetailResponse.moat.moatId
        }
    }

    private fun showUserProfile() {
        _selectedMoat.value = null
        _userMoats.value = originalUserMoats.value
    }

    private fun showUserProfileUpdateForm() {
        emitToParent(UserProfileDelegate.Push(viewType = UserProfileViewType.PROFILE_UPDATE_FORM, userProfile = userProfile.value))
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
                    ?: userMoats.value.firstOrNull { it.moatId == targetId }?.fireCount
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
                    ?: userMoats.value.firstOrNull { it.moatId == targetId }?.fireCount
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