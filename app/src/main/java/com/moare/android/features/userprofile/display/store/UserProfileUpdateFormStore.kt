package com.moare.android.features.userprofile.display.store

import android.content.Context
import android.net.Uri
import com.moare.android.core.store.BaseStore
import com.moare.android.core.util.AWSUtils
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.sign.models.UserHandleReserveRequest
import com.moare.android.features.sign.networking.SignClient
import com.moare.android.features.userprofile.display.UserProfileViewType
import com.moare.android.features.userprofile.models.UserProfileResponse
import com.moare.android.features.userprofile.models.UserProfileUpdateRequest
import com.moare.android.features.userprofile.networking.UserProfileClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

sealed interface UserProfileUpdateFormAction {
    data class ShowImageEdit(val uri: Uri) : UserProfileUpdateFormAction
    data class CheckUserHandle(val text: String) : UserProfileUpdateFormAction
    data class UpdateBio(val text: String) : UserProfileUpdateFormAction
    data class UpdateSportsInterests(val sport: String) : UserProfileUpdateFormAction
    data class Submit(val context: Context) : UserProfileUpdateFormAction
    data object Cancel : UserProfileUpdateFormAction

    // 외부 실행
    data class UpdatePreviewImage(val key: String, val file: File) : UserProfileUpdateFormAction
}

sealed interface UserProfileUpdateFormDelegate {
    data class Push(val viewType: UserProfileViewType, val uri: Uri, val userId: String) : UserProfileUpdateFormDelegate
    data class Pop(val userProfile: UserProfileResponse? = null) : UserProfileUpdateFormDelegate
}

class UserProfileUpdateFormStore @AssistedInject constructor(
    private val userProfileClient: UserProfileClient,
    private val signClient: SignClient,
    @Assisted val userProfile: UserProfileResponse,
    @Assisted val emitToParent: (UserProfileUpdateFormDelegate) -> Unit
) : BaseStore<UserProfileUpdateFormAction>() {

    private val _sportsInterests = MutableStateFlow(userProfile.sportsInterests)
    val sportsInterests: StateFlow<List<String>> = _sportsInterests

    private val _tempImageUrl = MutableStateFlow<String?>(null)
    val tempImageUrl: StateFlow<String?> = _tempImageUrl

    private val _userHandleCheckState = MutableStateFlow<ApiFetchState>(ApiFetchState.Idle)
    val userHandleCheckState: StateFlow<ApiFetchState> = _userHandleCheckState

    private val _isUserHandleTextFieldEnabled = MutableStateFlow(true)
    val isUserHandleTextFieldEnabled: StateFlow<Boolean> = _isUserHandleTextFieldEnabled

    private val _userHandleText = MutableStateFlow(userProfile.userHandle)
    val userHandleText: StateFlow<String> = _userHandleText

    private val _bioText = MutableStateFlow(userProfile.bio ?: "")
    val bioText: StateFlow<String> = _bioText

    private val userProfileUpdate = UserProfileUpdateRequest()
    private var tempFile: File? = null

    @AssistedFactory
    interface Factory {
        fun create(
            userProfile: UserProfileResponse,
            emitToParent: (UserProfileUpdateFormDelegate) -> Unit
        ) : UserProfileUpdateFormStore
    }

    override fun send(action: UserProfileUpdateFormAction) {
        when (action) {
            is UserProfileUpdateFormAction.ShowImageEdit -> showImageEdit(action.uri)
            is UserProfileUpdateFormAction.CheckUserHandle -> checkUserHandle(action.text)
            is UserProfileUpdateFormAction.UpdateBio -> updateBio(action.text)
            is UserProfileUpdateFormAction.UpdateSportsInterests -> updateSportsInterests(action.sport)
            is UserProfileUpdateFormAction.Submit -> submit(action.context)
            is UserProfileUpdateFormAction.Cancel -> cancel()
            is UserProfileUpdateFormAction.UpdatePreviewImage -> updatePreviewImage(action.key, action.file)
        }
    }

    private fun showImageEdit(uri: Uri) {
        emitToParent(UserProfileUpdateFormDelegate.Push(UserProfileViewType.PROFILE_IMAGE_EDIT, uri, userProfile.userId))
    }

    private fun checkUserHandle(text: String) {
        _userHandleText.value = text

        scope.launch {
            updateUserHandleCheckState(ApiFetchState.Idle)

            // TODO: 유효성 검사
            if (text.isBlank() || text == userProfile.userHandle) {
                userProfileUpdate.userHandle = null
                return@launch
            }

            delay(2000)

            updateUserHandleCheckState(ApiFetchState.Fetching)

            delay(3000)

            try {
                val result = signClient.checkUserHandle(text)
                updateUserHandleCheckState(if (result.success) ApiFetchState.Success else ApiFetchState.Error(result.message), text)
            } catch (e: Exception) {

            }
        }
    }

    private fun updateUserHandleCheckState(checkState: ApiFetchState, newUserHandle: String? = null) {
        _userHandleCheckState.value = checkState
        _isUserHandleTextFieldEnabled.value = checkState != ApiFetchState.Fetching

        newUserHandle?.let {
            if (checkState == ApiFetchState.Success) {
                userProfileUpdate.userHandle = newUserHandle

                scope.launch {
                    try {
                        val body = UserHandleReserveRequest(newUserHandle)
                        val result = signClient.reserveUserHandle(body)
                    } catch (e: Exception) {

                    }
                }
            }
        }
    }

    private fun updateBio(text: String) {
        _bioText.value = text

        if (userProfile.bio != text) {
            userProfileUpdate.bio = text
        } else {
            userProfileUpdate.bio = null
        }
    }

    private fun updateSportsInterests(sport: String) {
        if (sportsInterests.value.contains(sport)) {
            _sportsInterests.update { it - sport }
        } else {
            _sportsInterests.update { it + sport }
        }

        if (sportsInterests.value.toSet() != userProfile.sportsInterests.toSet()) {
            userProfileUpdate.sportsInterests = sportsInterests.value
        } else {
            userProfileUpdate.sportsInterests = null
        }
    }

    private fun submit(context: Context) {
        if (
            userProfileUpdate.userHandle == null &&
            userProfileUpdate.bio == null &&
            userProfileUpdate.sportsInterests.isNullOrEmpty() &&
            tempImageUrl.value == null
        ) {
            // 바뀐값이 하나도 없으면 그냥 return
            // TODO: 버튼 비활성화로 애초에 실행이 안되게 수정 필요
            return
        }

        scope.launch {
            if (tempImageUrl.value != null) {
                // 이미지 업로드
                // TODO: tempFile null일때 예외 처리
                tempFile?.let { tempFile ->
                    AWSUtils.uploadImage(context, tempFile, "profiles/${userProfile.userId}/profile.jpg",
                        onProgress = {

                        }, onComplete = { result ->
                            result.onSuccess {
                                // TODO: delete file
                                updateProfile(it)
                            }.onFailure {

                            }
                        }
                    )
                }
            } else {
                updateProfile()
            }
        }
    }

    private fun updateProfile(key: String? = null) {
        userProfileUpdate.profileImageUrl = key
        scope.launch {
            try {
                val result = userProfileClient.updateUserProfile(userProfileUpdate)

                emitToParent(UserProfileUpdateFormDelegate.Pop(result))
            } catch (e: Exception) {

            }
        }
    }

    private fun cancel() {
        emitToParent(UserProfileUpdateFormDelegate.Pop())
    }

    private fun updatePreviewImage(key: String, file: File) {
        _tempImageUrl.value = "https://moare-sns-profile-images.s3.ap-northeast-2.amazonaws.com/$key"
        tempFile = file
    }
}


























