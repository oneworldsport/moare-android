package com.moare.android.features.moat.display

import androidx.lifecycle.viewModelScope
import com.moare.android.core.util.TokenManager
import com.moare.android.features.moat.display.store.MoatAction
import com.moare.android.features.moat.display.store.MoatDelegate
import com.moare.android.features.moat.display.store.MoatFormStore
import com.moare.android.features.moat.display.store.MoatStore
import com.moare.android.features.search.display.ViewId
import com.moare.android.features.sign.networking.SignClient
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
    data object InitSignStore : MoatStackAction
}

sealed interface MoatStackDelegate {
    data object InitSignStore : MoatStackDelegate
    data class Login(val userId: String) : MoatStackDelegate
}

// NOTE: dataStore에서 token을 가져올때 초기에는 값이 없고 나중에 값이 가져와지는데, 초기에 값이 없을때 LaunchedEffect가 실행되는 문제가 있어서 만들어줌.
sealed class AccessTokenState {
    data object Loading : AccessTokenState()
    data class Loaded(val token: String?) : AccessTokenState()
}

class MoatStackStore @AssistedInject constructor(
    private val signClient: SignClient,
    private val moatFactory: MoatStore.Factory,
    private val moatFormFactory: MoatFormStore.Factory,
    private val tokenManager: TokenManager,
    @Assisted private val scope: CoroutineScope,
    @Assisted private val emitToParent: (MoatStackDelegate) -> Unit
) {
    @AssistedFactory
    interface Factory {
        fun create(
            scope: CoroutineScope,
            emitToParent: (MoatStackDelegate) -> Unit
        ) : MoatStackStore
    }

    private val _stack = MutableStateFlow<List<MoatStackItem>>(emptyList())
    val stack: StateFlow<List<MoatStackItem>> = _stack

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

//    fun attach(scope: CoroutineScope) {
//        this.scope = scope
//    }

    // TODO: 임시 코드
    fun logout() {
        scope.launch {
            tokenManager.clearTokens()
        }
    }

    fun send(action: MoatStackAction) {
        when (action) {
            is MoatStackAction.Push -> push(action.viewType)
            is MoatStackAction.Pop -> pop()
            is MoatStackAction.EmptyStack -> emptyStack()
            is MoatStackAction.BootstrapSession -> bootstrapSession()
            is MoatStackAction.InitSignStore -> {
                emitToParent(MoatStackDelegate.InitSignStore)
            }
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

        when (delegate) {
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

        when (delegate) {
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

        scope.launch {
            try {
                val result = signClient.bootstrapSession()

                if (stack.value.isEmpty()) {
                    push(MoatViewType.TRENDING)
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun emptyStack() {
        _stack.value = emptyList()
    }

    private fun dispose(item: MoatStackItem) {
        when (item) {
            is MoatStackItem.Trending -> item.store.dispose()
            is MoatStackItem.Detail -> item.store.dispose()
            is MoatStackItem.CreateForm -> item.store.dispose()
            is MoatStackItem.UpdateForm -> item.store.dispose()
        }
    }
}
































