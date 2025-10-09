package com.moare.android.core.mvi

import androidx.lifecycle.ViewModel
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoAction
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoReducer
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoState
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoViewModel
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStatsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchDelegate
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStatsDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Provider

@JvmInline
value class ViewId(val value: String = java.util.UUID.randomUUID().toString())

sealed interface StackItem {
    val id: ViewId

    data class FBPlayerInfo(override val id: ViewId, val store: FBPlayerInfoViewModel, val displayModel: FBPlayerInfoDisplayModel) : StackItem
    data class FBPlayerStats(override val id: ViewId, val store: FBPlayerStatsViewModel, val displayModel: FBPlayerStatsDisplayModel) : StackItem
}

@HiltViewModel
class AppViewModel @Inject constructor(
    val searchFactory: SearchViewModel.Factory,
    private val fbPlayerInfoProvider: Provider<FBPlayerInfoViewModel>,
    private val fbPlayerStatsProvider: Provider<FBPlayerStatsViewModel>
) : ViewModel() {
    private val _stack = MutableStateFlow<List<StackItem>>(emptyList())
    val stack: StateFlow<List<StackItem>> = _stack

    var searchStore: SearchViewModel = searchFactory.create { delegate ->
        onSearchDelegate(delegate)
    }

    fun pop() {
        if (!searchStore.searchState.value) {
            if (stack.value.isNotEmpty()) {
//                searchStore.send()
            }
        } else {
            _stack.update { current ->
                current.dropLast(1)
            }
        }
    }

    fun push() {

    }

    private fun onSearchDelegate(delegate: SearchDelegate) {
        when (delegate) {
            is SearchDelegate.Push -> {
                val id = ViewId()

                when (val model = delegate.model) {
                    is SportDecodableModel.FBPlayerInfo -> {
                        val store = fbPlayerInfoProvider.get()
                        _stack.update { it + StackItem.FBPlayerInfo(id, store, model.displayModel) }
                    }
                    is SportDecodableModel.FBPlayerStats -> {
                        val store = fbPlayerStatsProvider.get()
                        _stack.update { it + StackItem.FBPlayerStats(id, store, model.displayModel) }
                    }
                    else -> null
                }
            }
        }
    }
}