package com.moare.android.core.mvi

import androidx.lifecycle.ViewModel
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoAction
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoStore
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStatsViewModel
import com.moare.android.features.search.display.football.viewmodel.FBTeamInfoAction
import com.moare.android.features.search.display.football.viewmodel.FBTeamInfoStore
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerInfoAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerInfoStore
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamInfoAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamInfoStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBPlayerInfoAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBPlayerInfoStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamInfoAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamInfoStore
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerInfoAction
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerInfoStore
import com.moare.android.features.search.display.nba.viewmodel.NBATeamInfoAction
import com.moare.android.features.search.display.nba.viewmodel.NBATeamInfoStore
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

    data class FBPlayerInfo(override val id: ViewId, val store: FBPlayerInfoStore) : StackItem
    data class FBPlayerStats(override val id: ViewId, val store: FBPlayerStatsViewModel, val displayModel: FBPlayerStatsDisplayModel) : StackItem
    data class FBTeamInfo(override val id: ViewId, val store: FBTeamInfoStore) : StackItem

    data class NBAPlayerInfo(override val id: ViewId, val store: NBAPlayerInfoStore) : StackItem
    data class NBATeamInfo(override val id: ViewId, val store: NBATeamInfoStore) : StackItem

    data class MLBPlayerInfo(override val id: ViewId, val store: MLBPlayerInfoStore) : StackItem
    data class MLBTeamInfo(override val id: ViewId, val store: MLBTeamInfoStore) : StackItem

    data class KBOPlayerInfo(override val id: ViewId, val store: KBOPlayerInfoStore) : StackItem
    data class KBOTeamInfo(override val id: ViewId, val store: KBOTeamInfoStore) : StackItem
}

@HiltViewModel
class AppViewModel @Inject constructor(
    val searchFactory: SearchViewModel.Factory,
    private val fbPlayerInfoFactory: FBPlayerInfoStore.Factory,
    private val fbPlayerStatsProvider: Provider<FBPlayerStatsViewModel>,
    private val fbTeamInfoFactory: FBTeamInfoStore.Factory,

    private val nbaPlayerInfoFactory: NBAPlayerInfoStore.Factory,
    private val nbaTeamInfoFactory: NBATeamInfoStore.Factory,

    private val mlbPlayerInfoFactory: MLBPlayerInfoStore.Factory,
    private val mlbTeamInfoFactory: MLBTeamInfoStore.Factory,

    private val kboPlayerInfoFactory: KBOPlayerInfoStore.Factory,
    private val kboTeamInfoFactory: KBOTeamInfoStore.Factory,
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

//            dispose()
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
                        val store = fbPlayerInfoFactory.create(model.displayModel)
                        // TODO: InitData 여기서 하는게 나은지 확인 필요.
                        store.send(FBPlayerInfoAction.InitData)
                        _stack.update { it + StackItem.FBPlayerInfo(id, store) }
                    }
                    is SportDecodableModel.FBPlayerStats -> {
                        val store = fbPlayerStatsProvider.get()
                        _stack.update { it + StackItem.FBPlayerStats(id, store, model.displayModel) }
                    }
                    is SportDecodableModel.FBTeamInfo -> {
                        val store = fbTeamInfoFactory.create(model.displayModel)
                        store.send(FBTeamInfoAction.InitData)
                        _stack.update { it + StackItem.FBTeamInfo(id, store) }
                    }

                    is SportDecodableModel.NBAPlayerInfo -> {
                        val store = nbaPlayerInfoFactory.create(model.displayModel)
                        store.send(NBAPlayerInfoAction.InitData)
                        _stack.update { it + StackItem.NBAPlayerInfo(id, store) }
                    }
                    is SportDecodableModel.NBATeamInfo -> {
                        val store = nbaTeamInfoFactory.create(model.displayModel)
                        store.send(NBATeamInfoAction.InitData)
                        _stack.update { it + StackItem.NBATeamInfo(id, store) }
                    }

                    is SportDecodableModel.MLBPlayerInfo -> {
                        val store = mlbPlayerInfoFactory.create(model.displayModel)
                        store.send(MLBPlayerInfoAction.InitData)
                        _stack.update { it + StackItem.MLBPlayerInfo(id, store) }
                    }
                    is SportDecodableModel.MLBTeamInfo -> {
                        val store = mlbTeamInfoFactory.create(model.displayModel)
                        store.send(MLBTeamInfoAction.InitData)
                        _stack.update { it + StackItem.MLBTeamInfo(id, store) }
                    }

                    is SportDecodableModel.KBOPlayerInfo -> {
                        val store = kboPlayerInfoFactory.create(model.displayModel)
                        store.send(KBOPlayerInfoAction.InitData)
                        _stack.update { it + StackItem.KBOPlayerInfo(id, store) }
                    }
                    is SportDecodableModel.KBOTeamInfo -> {
                        val store = kboTeamInfoFactory.create(model.displayModel)
                        store.send(KBOTeamInfoAction.InitData)
                        _stack.update { it + StackItem.KBOTeamInfo(id, store) }
                    }
                    else -> null
                }
            }
        }
    }

    private fun dispose(item: StackItem) {
        when (item) {
            is StackItem.FBPlayerInfo -> item.store.dispose()
//            is StackItem.FBPlayerStats -> item.store.dispose()
            is StackItem.FBTeamInfo -> item.store.dispose()

            is StackItem.NBAPlayerInfo -> item.store.dispose()
            is StackItem.NBATeamInfo -> item.store.dispose()

            is StackItem.MLBPlayerInfo -> item.store.dispose()
            is StackItem.MLBTeamInfo -> item.store.dispose()

            is StackItem.KBOPlayerInfo -> item.store.dispose()
            is StackItem.KBOTeamInfo -> item.store.dispose()
            else -> null
        }
    }
}