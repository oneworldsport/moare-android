package com.moare.android.features.moat.display

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moare.android.features.moat.display.store.MoatAction
import com.moare.android.features.moat.display.store.MoatDelegate
import com.moare.android.features.moat.display.store.MoatFormStore
import com.moare.android.features.moat.display.store.MoatStore
import com.moare.android.features.search.display.ViewId
import com.moare.android.features.sign.networking.SignClient
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

sealed interface MoatStackItem {
    val id: ViewId

    data class Trending(override val id: ViewId, val store: MoatStore) : MoatStackItem
    data class Detail(override val id: ViewId, val store: MoatStore) : MoatStackItem
    data class CreateForm(override val id: ViewId, val store: MoatFormStore) : MoatStackItem
    data class UpdateForm(override val id: ViewId, val store: MoatFormStore) : MoatStackItem
}

enum class MoatViewType {
    TRENDING, DETAIL, CREATE_FORM, UPDATE_FORM, USER_PROFILE
}

sealed interface MoatStackAction {
    data class Push(val viewType: MoatViewType) : MoatStackAction
    data object Pop : MoatStackAction
    data object EmptyStack : MoatStackAction

    data object BootstrapSession : MoatStackAction
    }

// NOTE: dataStore에서 token을 가져올때 초기에는 값이 없고 나중에 값이 가져와지는데, 초기에 값이 없을때 LaunchedEffect가 실행되는 문제가 있어서 만들어줌.
sealed class AccessTokenState {
    data object Loading : AccessTokenState()
    data class Loaded(val token: String?) : AccessTokenState()
}

@HiltViewModel
class MoatStackViewModel @Inject constructor(
    private val signClient: SignClient,
    private val moatFactory: MoatStore.Factory,
    private val moatFormFactory: MoatFormStore.Factory,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val _stack = MutableStateFlow<List<MoatStackItem>>(emptyList())
    val stack: StateFlow<List<MoatStackItem>> = _stack

    private val _isBootstrapped = MutableStateFlow(false)
    val isBootstrapped: StateFlow<Boolean> = _isBootstrapped

    // TODO: 임시 코드
    val accessToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[stringPreferencesKey("accessToken")]
        }

    val accessTokenState: StateFlow<AccessTokenState> =
        accessToken
            .map< String?, AccessTokenState > { token ->
                AccessTokenState.Loaded(token)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AccessTokenState.Loading
            )

    fun send(action: MoatStackAction) {
        when (action) {
            is MoatStackAction.Push -> push(action.viewType)
            is MoatStackAction.Pop -> pop()
            is MoatStackAction.BootstrapSession -> bootstrapSession()
            is MoatStackAction.EmptyStack -> emptyStack()
        }
    }

    private fun push(viewType: MoatViewType) {
        val id = ViewId()

        when (viewType) {
            MoatViewType.TRENDING -> {
                val store = moatFactory.create { delegate ->
                    onTrendingDelegate(delegate)
                }
                _stack.update { it + MoatStackItem.Trending(id, store) }
            }
            else -> {}
        }
    }

    private fun onTrendingDelegate(delegate: MoatDelegate) {
        val id = ViewId()

        when(delegate) {
            is MoatDelegate.Push -> {
                when (delegate.viewType) {
                    MoatViewType.DETAIL -> {
                        val store = moatFactory.create(moatId = delegate.moatId) { delegate ->
                            onDetailDelegate(delegate)
                        }
                        _stack.update { it + MoatStackItem.Detail(id, store) }
                    }
                    MoatViewType.CREATE_FORM -> {
                        val store = moatFormFactory.create()
                        _stack.update { it + MoatStackItem.CreateForm(id, store) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun onDetailDelegate(delegate: MoatDelegate) {
        val id = ViewId()

        when(delegate) {
            is MoatDelegate.Push -> {
                when (delegate.viewType) {
                    MoatViewType.DETAIL -> {
                        val store = moatFactory.create(moatId = delegate.moatId) { delegate ->
                            onDetailDelegate(delegate)
                        }
                        _stack.update { it + MoatStackItem.Detail(id, store) }
                    }
                    MoatViewType.CREATE_FORM -> {
                        val store = moatFormFactory.create()
                        _stack.update { it + MoatStackItem.CreateForm(id, store) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun pop() {
        // MoatViewType이 TRENDING이면 MoatStore의 ShowTrending을 실행하고, DETAIL이면 기본 뒤로가기 동작을 실행한다
        stack.value.lastOrNull()?.let { lastItem ->
            if (lastItem is MoatStackItem.Trending) {
                lastItem.store.send(MoatAction.ShowTrending)
            }
        }

        if (stack.value.size > 1) {
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
                    push(MoatViewType.TRENDING)
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun emptyStack() {
        _stack.value = emptyList()
    }
}
































