package com.moare.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moare.android.core.util.TokenManager
import com.moare.android.features.moat.display.MoatStackDelegate
import com.moare.android.features.moat.display.MoatStackItem
import com.moare.android.features.moat.display.MoatStackStore
import com.moare.android.features.search.display.SearchStackDelegate
import com.moare.android.features.search.display.SearchStackStore
import com.moare.android.features.sign.display.store.SignDelegate
import com.moare.android.features.sign.display.store.SignStore
import com.moare.android.features.userprofile.display.UserProfileStackDelegate
import com.moare.android.features.userprofile.display.UserProfileStackStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class AppViewModel @Inject constructor(
    private val searchStackFactory: SearchStackStore.Factory,
    private val moatStackFactory: MoatStackStore.Factory,
    private val userProfileStackFactory: UserProfileStackStore.Factory,
    private val signFactory: SignStore.Factory,
//    private val signStoreProvider: Provider<SignStore>

    private val tokenManager: TokenManager,
) : ViewModel() {
    private val _signStore = MutableStateFlow<SignStore?>(null)
    val signStore: StateFlow<SignStore?> = _signStore

    val searchStackStore: SearchStackStore
    val moatStackStore: MoatStackStore
    val userProfileStackStore: UserProfileStackStore

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    init {
        // TODO: 나중에 viewModelScope detach 고려해야함.
        searchStackStore = searchStackFactory.create(viewModelScope) { delegate ->
            onSearchStackDelegate(delegate)
        }
        moatStackStore = moatStackFactory.create(viewModelScope) { delegate ->
            onMoatStackDelegate(delegate)
        }
        userProfileStackStore = userProfileStackFactory.create(viewModelScope) { delegate ->
            onUserProfileStackDelegate(delegate)
        }
    }

    private fun onSearchStackDelegate(delegate: SearchStackDelegate) {

    }

    private fun onMoatStackDelegate(delegate: MoatStackDelegate) {
        when (delegate) {
            is MoatStackDelegate.InitSignStore -> ensureSignStore()
            is MoatStackDelegate.Login -> {
                _userId.value = delegate.userId
                clearSignStore()
            }
        }
    }

    private fun onUserProfileStackDelegate(delegate: UserProfileStackDelegate) {
        when (delegate) {
            is UserProfileStackDelegate.InitSignStore -> ensureSignStore()
            is UserProfileStackDelegate.Login -> {
                _userId.value = delegate.userId
                clearSignStore()
            }
        }
    }

    private fun onSignDelegate(delegate: SignDelegate) {
        when (delegate) {
            is SignDelegate.Login -> {
                viewModelScope.launch {
                    tokenManager.updateTokens(delegate.access, delegate.refresh, delegate.id)
                }

                _userId.value = delegate.userId
                clearSignStore()
            }
        }
    }

    private fun onUserSettingsDelegate() {

    }

    private fun ensureSignStore() {
        if (signStore.value == null) {
//            _signStore.value = signStoreProvider.get()
            _signStore.value = signFactory.create { delegate -> onSignDelegate(delegate) }
        }
    }

    private fun clearSignStore() {
        _signStore.value = null
    }
}





























