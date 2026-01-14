package com.moare.android.features.userprofile.display

import androidx.lifecycle.viewModelScope
import com.moare.android.core.util.TokenManager
import com.moare.android.features.moat.display.AccessTokenState
import com.moare.android.features.moat.display.MoatStackDelegate
import com.moare.android.features.moat.display.MoatStackStore
import com.moare.android.features.moat.display.store.MoatStore
import com.moare.android.features.search.display.ViewId
import com.moare.android.features.sign.networking.SignClient
import com.moare.android.features.userprofile.display.store.UserProfileAction
import com.moare.android.features.userprofile.display.store.UserProfileDelegate
import com.moare.android.features.userprofile.display.store.UserProfileImageEditDelegate
import com.moare.android.features.userprofile.display.store.UserProfileImageEditStore
import com.moare.android.features.userprofile.display.store.UserProfileStore
import com.moare.android.features.userprofile.display.store.UserProfileUpdateFormAction
import com.moare.android.features.userprofile.display.store.UserProfileUpdateFormDelegate
import com.moare.android.features.userprofile.display.store.UserProfileUpdateFormStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface UserProfileStackItem {
    val id: ViewId

    data class UserProfile(override val id: ViewId, val store: UserProfileStore) : UserProfileStackItem
    data class MoatDetail(override val id: ViewId, val store: MoatStore) : UserProfileStackItem
    data class ProfileUpdateForm(override val id: ViewId, val store: UserProfileUpdateFormStore) : UserProfileStackItem
    data class ProfileImageEdit(override val id: ViewId, val store: UserProfileImageEditStore) : UserProfileStackItem
}

enum class UserProfileViewType {
    USER_PROFILE, MOAT_DETAIL, PROFILE_UPDATE_FORM, PROFILE_IMAGE_EDIT
}

sealed interface UserProfileStackAction {
    data class Push(val viewType: UserProfileViewType) : UserProfileStackAction
    data object Pop : UserProfileStackAction
    data object EmptyStack : UserProfileStackAction

    data object BootstrapSession : UserProfileStackAction
}

sealed interface UserProfileStackDelegate {
    data object InitSignStore : UserProfileStackDelegate
    data class Login(val userId: String) : UserProfileStackDelegate
}

class UserProfileStackStore @AssistedInject constructor(
    private val signClient: SignClient,
    private val userProfileFactory: UserProfileStore.Factory,
    private val moatFactory: MoatStore.Factory,
    private val profileUpdateFormFactory: UserProfileUpdateFormStore.Factory,
    private val profileImageEditFactory: UserProfileImageEditStore.Factory,
    private val tokenManager: TokenManager,
    @Assisted private val scope: CoroutineScope,
    @Assisted private val emitToParent: (UserProfileStackDelegate) -> Unit
) {
    @AssistedFactory
    interface Factory {
        fun create(
            scope: CoroutineScope,
            emitToParent: (UserProfileStackDelegate) -> Unit
        ) : UserProfileStackStore
    }

    private val _stack = MutableStateFlow<List<UserProfileStackItem>>(emptyList())
    val stack: StateFlow<List<UserProfileStackItem>> = _stack

    private val _isBootstrapped = MutableStateFlow(false)
    val isBootstrapped: StateFlow<Boolean> = _isBootstrapped

    // TODO: 임시 코드
    val accessTokenFlow: Flow<String?> = tokenManager.accessTokenFlow

    val accessTokenState: StateFlow<AccessTokenState> =
        tokenManager.accessTokenFlow
            .map< String?, AccessTokenState > { token ->
                AccessTokenState.Loaded(token)
            }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AccessTokenState.Loading
            )

    fun send(action: UserProfileStackAction) {
        when (action) {
            is UserProfileStackAction.Push -> push(action.viewType)
            is UserProfileStackAction.Pop -> pop()
            is UserProfileStackAction.EmptyStack -> emptyStack()
            is UserProfileStackAction.BootstrapSession -> bootstrapSession()
        }
    }

    private fun push(viewType: UserProfileViewType) {
        val id = ViewId()

        when (viewType) {
            UserProfileViewType.USER_PROFILE -> {
                val store = userProfileFactory.create { delegate ->
                    onUserProfileDelegate(delegate)
                }
                _stack.update { it + UserProfileStackItem.UserProfile(id, store) }
            }
            else -> {}
        }
    }

    private fun onUserProfileDelegate(delegate: UserProfileDelegate) {
        val id = ViewId()

        when (delegate) {
            is UserProfileDelegate.Push -> {
                when (delegate.viewType) {
                    UserProfileViewType.MOAT_DETAIL -> {
                        val store = moatFactory.create { delegate ->

                        }
                        _stack.update { it + UserProfileStackItem.MoatDetail(id, store) }
                    }
                    UserProfileViewType.PROFILE_UPDATE_FORM -> {
                        delegate.userProfile?.let {
                            val store = profileUpdateFormFactory.create(it) { delegate ->
                                onProfileUpdateFormDelegate(delegate)
                            }
                            _stack.update { it + UserProfileStackItem.ProfileUpdateForm(id, store) }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun onProfileUpdateFormDelegate(delegate: UserProfileUpdateFormDelegate) {
        val id = ViewId()

        when (delegate) {
            is UserProfileUpdateFormDelegate.Push -> {
                when (delegate.viewType) {
                    UserProfileViewType.PROFILE_IMAGE_EDIT -> {
                        val store = profileImageEditFactory.create(delegate.uri, delegate.userId) { delegate ->
                            onProfileImageEditDelegate(delegate)
                        }
                        _stack.update { it + UserProfileStackItem.ProfileImageEdit(id, store) }
                    }
                    else -> {}
                }
            }
            is UserProfileUpdateFormDelegate.Pop -> {
                _stack.value.lastOrNull()?.let {
                    dispose(it)
                }
                _stack.update { current ->
                    current.dropLast(1)
                }

                delegate.userProfile?.let { userProfile ->
                    stack.value.lastOrNull()?.let { lastItem ->
                        if (lastItem is UserProfileStackItem.UserProfile) {
                            lastItem.store.send(UserProfileAction.UpdateProfile(userProfile))
                        }
                    }
                }
            }
        }
    }

    private fun onProfileImageEditDelegate(delegate: UserProfileImageEditDelegate) {
        val id = ViewId()

        when (delegate) {
            is UserProfileImageEditDelegate.Pop -> {
                _stack.value.lastOrNull()?.let {
                    dispose(it)
                }
                _stack.update { current ->
                    current.dropLast(1)
                }

                delegate.key?.let { key ->
                    delegate.file?.let { file ->
                        stack.value.lastOrNull()?.let { lastItem ->
                            if (lastItem is UserProfileStackItem.ProfileUpdateForm) {
                                lastItem.store.send(UserProfileUpdateFormAction.UpdatePreviewImage(key, file))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun pop() {
        stack.value.lastOrNull()?.let { lastItem ->
            if (lastItem is UserProfileStackItem.UserProfile) {
                lastItem.store.send(UserProfileAction.ShowUserProfile)
                return
            }
        }

        if (stack.value.size > 1) {
            // TODO: 함수로 정리
            _stack.value.lastOrNull()?.let {
                dispose(it)
            }
            _stack.update { current ->
                current.dropLast(1)
            }
        }
    }

    private fun bootstrapSession() {
        _isBootstrapped.value = true

        scope.launch {
            try {
                val result = signClient.bootstrapSession()

                if (stack.value.isEmpty()) {
                    push(UserProfileViewType.USER_PROFILE)
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun emptyStack() {
        _stack.value = emptyList()
    }

    private fun dispose(item: UserProfileStackItem) {
        when (item) {
            is UserProfileStackItem.UserProfile -> item.store.dispose()
            is UserProfileStackItem.MoatDetail -> item.store.dispose()
            is UserProfileStackItem.ProfileUpdateForm -> item.store.dispose()
            is UserProfileStackItem.ProfileImageEdit -> item.store.dispose()
        }
    }
}
























