package com.moare.android.features.userprofile.display

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moare.android.features.moat.display.AccessTokenState
import com.moare.android.features.moat.display.MoatStackItem
import com.moare.android.features.moat.display.MoatViewType
import com.moare.android.features.moat.display.store.MoatAction
import com.moare.android.features.moat.display.store.MoatStore
import com.moare.android.features.search.display.ViewId
import com.moare.android.features.sign.networking.SignClient
import com.moare.android.features.userprofile.display.store.UserProfileAction
import com.moare.android.features.userprofile.display.store.UserProfileDelegate
import com.moare.android.features.userprofile.display.store.UserProfileStore
import dagger.hilt.android.lifecycle.HiltViewModel
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
    //    data class ProfileUpdateForm(override val id: ViewId, val store: ) : UserProfileStackItem
//    data class ProfileImageEdit(override val id: ViewId, val store: ) : UserProfileStackItem
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

@HiltViewModel
class UserProfileStackViewModel @Inject constructor(
    private val signClient: SignClient,
    private val userProfileFactory: UserProfileStore.Factory,
    private val moatFactory: MoatStore.Factory,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val _stack = MutableStateFlow<List<UserProfileStackItem>>(emptyList())
    val stack: StateFlow<List<UserProfileStackItem>> = _stack

    private val _isBootstrapped = MutableStateFlow(false)
    val isBootstrapped: StateFlow<Boolean> = _isBootstrapped

    // TODO: 임시 코드
    val accessToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[stringPreferencesKey("accessToken")]
        }

    val accessTokenState: StateFlow<AccessTokenState> =
        accessToken
            .map< String?, AccessTokenState> { token ->
                AccessTokenState.Loaded(token)
            }
            .stateIn(
                scope = viewModelScope,
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
//                            val store = moatFactory.create { delegate ->
//
//                            }
//                            _stack.update { it + UserProfileStackItem.MoatDetail(id, store) }
                        }
                    }
                    else -> {}
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

        viewModelScope.launch {
            try {
                val result = signClient.bootstrapSession()

                if (result.success == true && stack.value.isEmpty()) {
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
        }
    }
}
























