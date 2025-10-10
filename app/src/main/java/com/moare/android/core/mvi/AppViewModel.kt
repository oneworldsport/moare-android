package com.moare.android.core.mvi

import androidx.lifecycle.ViewModel
import com.moare.android.features.search.display.football.viewmodel.FBLeagueScheduleAction
import com.moare.android.features.search.display.football.viewmodel.FBLeagueScheduleStore
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoAction
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoStore
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStandingsAction
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStandingsStore
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStatsAction
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStatsStore
import com.moare.android.features.search.display.football.viewmodel.FBTeamInfoAction
import com.moare.android.features.search.display.football.viewmodel.FBTeamInfoStore
import com.moare.android.features.search.display.football.viewmodel.FBTeamStandingsAction
import com.moare.android.features.search.display.football.viewmodel.FBTeamStandingsStore
import com.moare.android.features.search.display.football.viewmodel.FBTeamStatsAction
import com.moare.android.features.search.display.football.viewmodel.FBTeamStatsStore
import com.moare.android.features.search.display.kbo.viewmodel.KBOLeagueScheduleAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOLeagueScheduleStore
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerInfoAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerInfoStore
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerStatsAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerStatsStore
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamInfoAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamInfoStore
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStandingsAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStandingsStore
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStatsAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStatsStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBLeagueScheduleAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBLeagueScheduleStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBPlayerInfoAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBPlayerInfoStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBPlayerStatsAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBPlayerStatsStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamInfoAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamInfoStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStandingsAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStandingsStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStatsAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStatsStore
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueScheduleAction
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueScheduleStore
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerInfoAction
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerInfoStore
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerStandingsAction
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerStandingsStore
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerStatsAction
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerStatsStore
import com.moare.android.features.search.display.nba.viewmodel.NBATeamInfoAction
import com.moare.android.features.search.display.nba.viewmodel.NBATeamInfoStore
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStandingsAction
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStandingsStore
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStatsAction
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStatsStore
import com.moare.android.features.search.display.search.viewmodel.SearchDelegate
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
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
    data class FBPlayerStats(override val id: ViewId, val store: FBPlayerStatsStore) : StackItem
    data class FBPlayerStandings(override val id: ViewId, val store: FBPlayerStandingsStore) : StackItem
    data class FBTeamInfo(override val id: ViewId, val store: FBTeamInfoStore) : StackItem
    data class FBTeamStats(override val id: ViewId, val store: FBTeamStatsStore) : StackItem
    data class FBTeamStandings(override val id: ViewId, val store: FBTeamStandingsStore) : StackItem
    data class FBLeagueSchedule(override val id: ViewId, val store: FBLeagueScheduleStore) : StackItem

    data class NBAPlayerInfo(override val id: ViewId, val store: NBAPlayerInfoStore) : StackItem
    data class NBAPlayerStats(override val id: ViewId, val store: NBAPlayerStatsStore) : StackItem
    data class NBAPlayerStandings(override val id: ViewId, val store: NBAPlayerStandingsStore) : StackItem
    data class NBATeamInfo(override val id: ViewId, val store: NBATeamInfoStore) : StackItem
    data class NBATeamStats(override val id: ViewId, val store: NBATeamStatsStore) : StackItem
    data class NBATeamStandings(override val id: ViewId, val store: NBATeamStandingsStore) : StackItem
    data class NBALeagueSchedule(override val id: ViewId, val store: NBALeagueScheduleStore) : StackItem

    data class MLBPlayerInfo(override val id: ViewId, val store: MLBPlayerInfoStore) : StackItem
    data class MLBPlayerStats(override val id: ViewId, val store: MLBPlayerStatsStore) : StackItem
    data class MLBTeamInfo(override val id: ViewId, val store: MLBTeamInfoStore) : StackItem
    data class MLBTeamStats(override val id: ViewId, val store: MLBTeamStatsStore) : StackItem
    data class MLBTeamStandings(override val id: ViewId, val store: MLBTeamStandingsStore) : StackItem
    data class MLBLeagueSchedule(override val id: ViewId, val store: MLBLeagueScheduleStore) : StackItem

    data class KBOPlayerInfo(override val id: ViewId, val store: KBOPlayerInfoStore) : StackItem
    data class KBOPlayerStats(override val id: ViewId, val store: KBOPlayerStatsStore) : StackItem
    data class KBOTeamInfo(override val id: ViewId, val store: KBOTeamInfoStore) : StackItem
    data class KBOTeamStats(override val id: ViewId, val store: KBOTeamStatsStore) : StackItem
    data class KBOTeamStandings(override val id: ViewId, val store: KBOTeamStandingsStore) : StackItem
    data class KBOLeagueSchedule(override val id: ViewId, val store: KBOLeagueScheduleStore) : StackItem
}

@HiltViewModel
class AppViewModel @Inject constructor(
    val searchFactory: SearchViewModel.Factory,
    private val fbPlayerInfoFactory: FBPlayerInfoStore.Factory,
    private val fbPlayerStatsFactory: FBPlayerStatsStore.Factory,
    private val fbPlayerStandingsFactory: FBPlayerStandingsStore.Factory,
    private val fbTeamInfoFactory: FBTeamInfoStore.Factory,
    private val fbTeamStatsFactory: FBTeamStatsStore.Factory,
    private val fbTeamStandingsFactory: FBTeamStandingsStore.Factory,
    private val fbLeagueScheduleFactory: FBLeagueScheduleStore.Factory,

    private val nbaPlayerInfoFactory: NBAPlayerInfoStore.Factory,
    private val nbaPlayerStatsFactory: NBAPlayerStatsStore.Factory,
    private val nbaPlayerStandingsFactory: NBAPlayerStandingsStore.Factory,
    private val nbaTeamInfoFactory: NBATeamInfoStore.Factory,
    private val nbaTeamStatsFactory: NBATeamStatsStore.Factory,
    private val nbaTeamStandingsFactory: NBATeamStandingsStore.Factory,
    private val nbaLeagueScheduleFactory: NBALeagueScheduleStore.Factory,

    private val mlbPlayerInfoFactory: MLBPlayerInfoStore.Factory,
    private val mlbPlayerStatsFactory: MLBPlayerStatsStore.Factory,
    private val mlbTeamInfoFactory: MLBTeamInfoStore.Factory,
    private val mlbTeamStatsFactory: MLBTeamStatsStore.Factory,
    private val mlbTeamStandingsFactory: MLBTeamStandingsStore.Factory,
    private val mlbLeagueScheduleFactory: MLBLeagueScheduleStore.Factory,

    private val kboPlayerInfoFactory: KBOPlayerInfoStore.Factory,
    private val kboPlayerStatsFactory: KBOPlayerStatsStore.Factory,
    private val kboTeamInfoFactory: KBOTeamInfoStore.Factory,
    private val kboTeamStatsFactory: KBOTeamStatsStore.Factory,
    private val kboTeamStandingsFactory: KBOTeamStandingsStore.Factory,
    private val kboLeagueScheduleFactory: KBOLeagueScheduleStore.Factory,
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
                        val store = fbPlayerStatsFactory.create(model.displayModel)
                        store.send(FBPlayerStatsAction.InitData)
                        _stack.update { it + StackItem.FBPlayerStats(id, store) }
                    }
                    is SportDecodableModel.FBPlayerStandings -> {
                        val store = fbPlayerStandingsFactory.create(model.displayModel)
                        store.send(FBPlayerStandingsAction.InitData)
                        _stack.update { it + StackItem.FBPlayerStandings(id, store) }
                    }
                    is SportDecodableModel.FBTeamInfo -> {
                        val store = fbTeamInfoFactory.create(model.displayModel)
                        store.send(FBTeamInfoAction.InitData)
                        _stack.update { it + StackItem.FBTeamInfo(id, store) }
                    }
                    is SportDecodableModel.FBTeamStats -> {
                        val store = fbTeamStatsFactory.create(model.displayModel)
                        store.send(FBTeamStatsAction.InitData)
                        _stack.update { it + StackItem.FBTeamStats(id, store) }
                    }
                    is SportDecodableModel.FBTeamStandings -> {
                        val store = fbTeamStandingsFactory.create(model.displayModel)
                        store.send(FBTeamStandingsAction.InitData)
                        _stack.update { it + StackItem.FBTeamStandings(id, store) }
                    }
                    is SportDecodableModel.FBLeagueSchedule -> {
                        val store = fbLeagueScheduleFactory.create(model.displayModel)
                        store.send(FBLeagueScheduleAction.InitData)
                        _stack.update { it + StackItem.FBLeagueSchedule(id, store) }
                    }

                    is SportDecodableModel.NBAPlayerInfo -> {
                        val store = nbaPlayerInfoFactory.create(model.displayModel)
                        store.send(NBAPlayerInfoAction.InitData)
                        _stack.update { it + StackItem.NBAPlayerInfo(id, store) }
                    }
                    is SportDecodableModel.NBAPlayerStats -> {
                        val store = nbaPlayerStatsFactory.create(model.displayModel)
                        store.send(NBAPlayerStatsAction.InitData)
                        _stack.update { it + StackItem.NBAPlayerStats(id, store) }
                    }
                    is SportDecodableModel.NBAPlayerStandings -> {
                        val store = nbaPlayerStandingsFactory.create(model.displayModel)
                        store.send(NBAPlayerStandingsAction.InitData)
                        _stack.update { it + StackItem.NBAPlayerStandings(id, store) }
                    }
                    is SportDecodableModel.NBATeamInfo -> {
                        val store = nbaTeamInfoFactory.create(model.displayModel)
                        store.send(NBATeamInfoAction.InitData)
                        _stack.update { it + StackItem.NBATeamInfo(id, store) }
                    }
                    is SportDecodableModel.NBATeamStats -> {
                        val store = nbaTeamStatsFactory.create(model.displayModel)
                        store.send(NBATeamStatsAction.InitData)
                        _stack.update { it + StackItem.NBATeamStats(id, store) }
                    }
                    is SportDecodableModel.NBATeamStandings -> {
                        val store = nbaTeamStandingsFactory.create(model.displayModel)
                        store.send(NBATeamStandingsAction.InitData)
                        _stack.update { it + StackItem.NBATeamStandings(id, store) }
                    }
                    is SportDecodableModel.NBALeagueSchedule -> {
                        val store = nbaLeagueScheduleFactory.create(model.displayModel)
                        store.send(NBALeagueScheduleAction.InitData)
                        _stack.update { it + StackItem.NBALeagueSchedule(id, store) }
                    }

                    is SportDecodableModel.MLBPlayerInfo -> {
                        val store = mlbPlayerInfoFactory.create(model.displayModel)
                        store.send(MLBPlayerInfoAction.InitData)
                        _stack.update { it + StackItem.MLBPlayerInfo(id, store) }
                    }
                    is SportDecodableModel.MLBPlayerStats -> {
                        val store = mlbPlayerStatsFactory.create(model.displayModel)
                        store.send(MLBPlayerStatsAction.InitData)
                        _stack.update { it + StackItem.MLBPlayerStats(id, store) }
                    }
                    is SportDecodableModel.MLBTeamInfo -> {
                        val store = mlbTeamInfoFactory.create(model.displayModel)
                        store.send(MLBTeamInfoAction.InitData)
                        _stack.update { it + StackItem.MLBTeamInfo(id, store) }
                    }
                    is SportDecodableModel.MLBTeamStats -> {
                        val store = mlbTeamStatsFactory.create(model.displayModel)
                        store.send(MLBTeamStatsAction.InitData)
                        _stack.update { it + StackItem.MLBTeamStats(id, store) }
                    }
                    is SportDecodableModel.MLBTeamStandings -> {
                        val store = mlbTeamStandingsFactory.create(model.displayModel)
                        store.send(MLBTeamStandingsAction.InitData)
                        _stack.update { it + StackItem.MLBTeamStandings(id, store) }
                    }
                    is SportDecodableModel.MLBLeagueSchedule -> {
                        val store = mlbLeagueScheduleFactory.create(model.displayModel)
                        store.send(MLBLeagueScheduleAction.InitData)
                        _stack.update { it + StackItem.MLBLeagueSchedule(id, store) }
                    }

                    is SportDecodableModel.KBOPlayerInfo -> {
                        val store = kboPlayerInfoFactory.create(model.displayModel)
                        store.send(KBOPlayerInfoAction.InitData)
                        _stack.update { it + StackItem.KBOPlayerInfo(id, store) }
                    }
                    is SportDecodableModel.KBOPlayerStats -> {
                        val store = kboPlayerStatsFactory.create(model.displayModel)
                        store.send(KBOPlayerStatsAction.InitData)
                        _stack.update { it + StackItem.KBOPlayerStats(id, store) }
                    }
                    is SportDecodableModel.KBOTeamInfo -> {
                        val store = kboTeamInfoFactory.create(model.displayModel)
                        store.send(KBOTeamInfoAction.InitData)
                        _stack.update { it + StackItem.KBOTeamInfo(id, store) }
                    }
                    is SportDecodableModel.KBOTeamStats -> {
                        val store = kboTeamStatsFactory.create(model.displayModel)
                        store.send(KBOTeamStatsAction.InitData)
                        _stack.update { it + StackItem.KBOTeamStats(id, store) }
                    }
                    is SportDecodableModel.KBOTeamStandings -> {
                        val store = kboTeamStandingsFactory.create(model.displayModel)
                        store.send(KBOTeamStandingsAction.InitData)
                        _stack.update { it + StackItem.KBOTeamStandings(id, store) }
                    }
                    is SportDecodableModel.KBOLeagueSchedule -> {
                        val store = kboLeagueScheduleFactory.create(model.displayModel)
                        store.send(KBOLeagueScheduleAction.InitData)
                        _stack.update { it + StackItem.KBOLeagueSchedule(id, store) }
                    }
                    else -> null
                }
            }
        }
    }

    private fun dispose(item: StackItem) {
        when (item) {
            is StackItem.FBPlayerInfo -> item.store.dispose()
            is StackItem.FBPlayerStats -> item.store.dispose()
            is StackItem.FBPlayerStandings -> item.store.dispose()
            is StackItem.FBTeamInfo -> item.store.dispose()
            is StackItem.FBTeamStats -> item.store.dispose()
            is StackItem.FBTeamStandings -> item.store.dispose()
            is StackItem.FBLeagueSchedule -> item.store.dispose()

            is StackItem.NBAPlayerInfo -> item.store.dispose()
            is StackItem.NBAPlayerStats -> item.store.dispose()
            is StackItem.NBAPlayerStandings -> item.store.dispose()
            is StackItem.NBATeamInfo -> item.store.dispose()
            is StackItem.NBATeamStats -> item.store.dispose()
            is StackItem.NBATeamStandings -> item.store.dispose()
            is StackItem.NBALeagueSchedule -> item.store.dispose()

            is StackItem.MLBPlayerInfo -> item.store.dispose()
            is StackItem.MLBPlayerStats -> item.store.dispose()
            is StackItem.MLBTeamInfo -> item.store.dispose()
            is StackItem.MLBTeamStats -> item.store.dispose()
            is StackItem.MLBTeamStandings -> item.store.dispose()
            is StackItem.MLBLeagueSchedule -> item.store.dispose()

            is StackItem.KBOPlayerInfo -> item.store.dispose()
            is StackItem.KBOPlayerStats -> item.store.dispose()
            is StackItem.KBOTeamInfo -> item.store.dispose()
            is StackItem.KBOTeamStats -> item.store.dispose()
            is StackItem.KBOTeamStandings -> item.store.dispose()
            is StackItem.KBOLeagueSchedule -> item.store.dispose()
        }
    }
}