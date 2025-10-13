package com.moare.android.core.mvi

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.moare.android.features.search.display.football.viewmodel.FBGameStatsAction
import com.moare.android.features.search.display.football.viewmodel.FBGameStatsDelegate
import com.moare.android.features.search.display.football.viewmodel.FBGameStatsStore
import com.moare.android.features.search.display.football.viewmodel.FBLeagueScheduleAction
import com.moare.android.features.search.display.football.viewmodel.FBLeagueScheduleStore
import com.moare.android.features.search.display.football.viewmodel.FBLeagueScheduleDelegate
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoAction
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoDelegate
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoStore
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStandingsAction
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStandingsDelegate
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStandingsStore
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStatsAction
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStatsStore
import com.moare.android.features.search.display.football.viewmodel.FBTeamInfoAction
import com.moare.android.features.search.display.football.viewmodel.FBTeamInfoDelegate
import com.moare.android.features.search.display.football.viewmodel.FBTeamInfoStore
import com.moare.android.features.search.display.football.viewmodel.FBTeamStandingsAction
import com.moare.android.features.search.display.football.viewmodel.FBTeamStandingsDelegate
import com.moare.android.features.search.display.football.viewmodel.FBTeamStandingsStore
import com.moare.android.features.search.display.football.viewmodel.FBTeamStatsAction
import com.moare.android.features.search.display.football.viewmodel.FBTeamStatsStore
import com.moare.android.features.search.display.kbo.viewmodel.KBOGameStatsAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOGameStatsDelegate
import com.moare.android.features.search.display.kbo.viewmodel.KBOGameStatsStore
import com.moare.android.features.search.display.kbo.viewmodel.KBOLeagueScheduleAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOLeagueScheduleDelegate
import com.moare.android.features.search.display.kbo.viewmodel.KBOLeagueScheduleStore
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerInfoAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerInfoDelegate
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerInfoStore
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerStatsAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerStatsStore
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamInfoAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamInfoDelegate
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamInfoStore
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStandingsAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStandingsDelegate
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStandingsStore
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStatsAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamStatsStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBGameStatsAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBGameStatsDelegate
import com.moare.android.features.search.display.mlb.viewmodel.MLBGameStatsStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBLeagueScheduleAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBLeagueScheduleDelegate
import com.moare.android.features.search.display.mlb.viewmodel.MLBLeagueScheduleStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBPlayerInfoAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBPlayerInfoDelegate
import com.moare.android.features.search.display.mlb.viewmodel.MLBPlayerInfoStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBPlayerStatsAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBPlayerStatsStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamInfoAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamInfoDelegate
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamInfoStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStandingsAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStandingsDelegate
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStandingsStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStatsAction
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStatsStore
import com.moare.android.features.search.display.nba.viewmodel.NBAGameStatsAction
import com.moare.android.features.search.display.nba.viewmodel.NBAGameStatsDelegate
import com.moare.android.features.search.display.nba.viewmodel.NBAGameStatsStore
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueScheduleAction
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueScheduleDelegate
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueScheduleStore
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerInfoAction
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerInfoDelegate
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerInfoStore
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerStandingsAction
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerStandingsDelegate
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerStandingsStore
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerStatsAction
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerStatsStore
import com.moare.android.features.search.display.nba.viewmodel.NBATeamInfoAction
import com.moare.android.features.search.display.nba.viewmodel.NBATeamInfoDelegate
import com.moare.android.features.search.display.nba.viewmodel.NBATeamInfoStore
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStandingsAction
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStandingsDelegate
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStandingsStore
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStatsAction
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStatsStore
import com.moare.android.features.search.display.search.viewmodel.SearchAction
import com.moare.android.features.search.display.search.viewmodel.SearchDelegate
import com.moare.android.features.search.display.search.viewmodel.SearchStore
import com.moare.android.features.search.models.SportDecodableModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

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
    data class FBGameStats(override val id: ViewId, val store: FBGameStatsStore) : StackItem

    data class NBAPlayerInfo(override val id: ViewId, val store: NBAPlayerInfoStore) : StackItem
    data class NBAPlayerStats(override val id: ViewId, val store: NBAPlayerStatsStore) : StackItem
    data class NBAPlayerStandings(override val id: ViewId, val store: NBAPlayerStandingsStore) : StackItem
    data class NBATeamInfo(override val id: ViewId, val store: NBATeamInfoStore) : StackItem
    data class NBATeamStats(override val id: ViewId, val store: NBATeamStatsStore) : StackItem
    data class NBATeamStandings(override val id: ViewId, val store: NBATeamStandingsStore) : StackItem
    data class NBALeagueSchedule(override val id: ViewId, val store: NBALeagueScheduleStore) : StackItem
    data class NBAGameStats(override val id: ViewId, val store: NBAGameStatsStore) : StackItem

    data class MLBPlayerInfo(override val id: ViewId, val store: MLBPlayerInfoStore) : StackItem
    data class MLBPlayerStats(override val id: ViewId, val store: MLBPlayerStatsStore) : StackItem
    data class MLBTeamInfo(override val id: ViewId, val store: MLBTeamInfoStore) : StackItem
    data class MLBTeamStats(override val id: ViewId, val store: MLBTeamStatsStore) : StackItem
    data class MLBTeamStandings(override val id: ViewId, val store: MLBTeamStandingsStore) : StackItem
    data class MLBLeagueSchedule(override val id: ViewId, val store: MLBLeagueScheduleStore) : StackItem
    data class MLBGameStats(override val id: ViewId, val store: MLBGameStatsStore) : StackItem

    data class KBOPlayerInfo(override val id: ViewId, val store: KBOPlayerInfoStore) : StackItem
    data class KBOPlayerStats(override val id: ViewId, val store: KBOPlayerStatsStore) : StackItem
    data class KBOTeamInfo(override val id: ViewId, val store: KBOTeamInfoStore) : StackItem
    data class KBOTeamStats(override val id: ViewId, val store: KBOTeamStatsStore) : StackItem
    data class KBOTeamStandings(override val id: ViewId, val store: KBOTeamStandingsStore) : StackItem
    data class KBOLeagueSchedule(override val id: ViewId, val store: KBOLeagueScheduleStore) : StackItem
    data class KBOGameStats(override val id: ViewId, val store: KBOGameStatsStore) : StackItem
}

@HiltViewModel
class AppViewModel @Inject constructor(
    val searchFactory: SearchStore.Factory,
    private val fbPlayerInfoFactory: FBPlayerInfoStore.Factory,
    private val fbPlayerStatsFactory: FBPlayerStatsStore.Factory,
    private val fbPlayerStandingsFactory: FBPlayerStandingsStore.Factory,
    private val fbTeamInfoFactory: FBTeamInfoStore.Factory,
    private val fbTeamStatsFactory: FBTeamStatsStore.Factory,
    private val fbTeamStandingsFactory: FBTeamStandingsStore.Factory,
    private val fbLeagueScheduleFactory: FBLeagueScheduleStore.Factory,
    private val fbGameStatsFactory: FBGameStatsStore.Factory,

    private val nbaPlayerInfoFactory: NBAPlayerInfoStore.Factory,
    private val nbaPlayerStatsFactory: NBAPlayerStatsStore.Factory,
    private val nbaPlayerStandingsFactory: NBAPlayerStandingsStore.Factory,
    private val nbaTeamInfoFactory: NBATeamInfoStore.Factory,
    private val nbaTeamStatsFactory: NBATeamStatsStore.Factory,
    private val nbaTeamStandingsFactory: NBATeamStandingsStore.Factory,
    private val nbaLeagueScheduleFactory: NBALeagueScheduleStore.Factory,
    private val nbaGameStatsFactory: NBAGameStatsStore.Factory,

    private val mlbPlayerInfoFactory: MLBPlayerInfoStore.Factory,
    private val mlbPlayerStatsFactory: MLBPlayerStatsStore.Factory,
    private val mlbTeamInfoFactory: MLBTeamInfoStore.Factory,
    private val mlbTeamStatsFactory: MLBTeamStatsStore.Factory,
    private val mlbTeamStandingsFactory: MLBTeamStandingsStore.Factory,
    private val mlbLeagueScheduleFactory: MLBLeagueScheduleStore.Factory,
    private val mlbGameStatsFactory: MLBGameStatsStore.Factory,

    private val kboPlayerInfoFactory: KBOPlayerInfoStore.Factory,
    private val kboPlayerStatsFactory: KBOPlayerStatsStore.Factory,
    private val kboTeamInfoFactory: KBOTeamInfoStore.Factory,
    private val kboTeamStatsFactory: KBOTeamStatsStore.Factory,
    private val kboTeamStandingsFactory: KBOTeamStandingsStore.Factory,
    private val kboLeagueScheduleFactory: KBOLeagueScheduleStore.Factory,
    private val kboGameStatsFactory: KBOGameStatsStore.Factory
) : ViewModel() {
    private val _stack = MutableStateFlow<List<StackItem>>(emptyList())
    val stack: StateFlow<List<StackItem>> = _stack

    private val _didPop = MutableStateFlow(false)
    val didPop: StateFlow<Boolean> = _didPop

    private val _includesPreviousView = MutableStateFlow(false)
    val includesPreviousView: StateFlow<Boolean> = _includesPreviousView

    var searchStore: SearchStore = searchFactory.create { delegate ->
        onSearchDelegate(delegate)
    }

    fun pop(activity: Activity?) {
        if (!searchStore.searchState.value) {
            if (stack.value.isEmpty()) {
                // close app
                activity?.finishAffinity()
            } else {
                // If searchBar is Opened and there are stack, don't pop and show the previous view.
                searchStore.send(SearchAction.ToggleSearchBar)
            }
        } else {
            _didPop.value = true
            // NOTE: FBGameStats로 뒤로갔을때(FBLeagueSchedule -> FBGameStats인 경우) includesPreviousView가 true여야 하지만 false여도
            // 그냥 FBGameStats 화면이 잘 나오기 때문에 상관없음
            _includesPreviousView.value = false

            val lastItem = _stack.value.lastOrNull()

            _stack.update { current ->
                current.dropLast(1)
            }

            lastItem?.let {
                dispose(lastItem)

                // 뒤로가기 후 보여줄 화면이 없으면(마지막 화면을 뒤로가기 했을 경우) SearchBar를 toggle.
                if (stack.value.isEmpty()) {
                    searchStore.send(SearchAction.ToggleSearchBar)
                }
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
                        val store = fbPlayerInfoFactory.create(model) { delegate ->
                            onFBPlayerInfoDelegate(delegate)
                        }
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
                        val store = fbPlayerStandingsFactory.create(model) { delegate ->
                            onFBPlayerStandingsDelegate(delegate)
                        }
                        store.send(FBPlayerStandingsAction.InitData)
                        _stack.update { it + StackItem.FBPlayerStandings(id, store) }
                    }
                    is SportDecodableModel.FBTeamInfo -> {
                        val store = fbTeamInfoFactory.create(model) { delegate ->
                            onFBTeamInfoDelegate(delegate)
                        }
                        store.send(FBTeamInfoAction.InitData)
                        _stack.update { it + StackItem.FBTeamInfo(id, store) }
                    }
                    is SportDecodableModel.FBTeamStats -> {
                        val store = fbTeamStatsFactory.create(model.displayModel)
                        store.send(FBTeamStatsAction.InitData)
                        _stack.update { it + StackItem.FBTeamStats(id, store) }
                    }
                    is SportDecodableModel.FBTeamStandings -> {
                        val store = fbTeamStandingsFactory.create(model) { delegate ->
                            onFBTeamStandingsDelegate(delegate)
                        }
                        store.send(FBTeamStandingsAction.InitData)
                        _stack.update { it + StackItem.FBTeamStandings(id, store) }
                    }
                    is SportDecodableModel.FBLeagueSchedule -> {
                        val store = fbLeagueScheduleFactory.create(model.displayModel) { delegate ->
                            onFBLeagueScheduleDelegate(delegate)
                        }
                        store.send(FBLeagueScheduleAction.InitData)
                        _stack.update { it + StackItem.FBLeagueSchedule(id, store) }
                    }
                    is SportDecodableModel.FBGameStats -> {
                        val store = fbGameStatsFactory.create(model.displayModel) { delegate ->
                            onFBGameStatsDelegate(id, delegate)
                        }
                        store.send(FBGameStatsAction.InitData)
                        _stack.update { it + StackItem.FBGameStats(id, store) }
                    }

                    is SportDecodableModel.NBAPlayerInfo -> {
                        val store = nbaPlayerInfoFactory.create(model) { delegate ->
                            onNBAPlayerInfoDelegate(delegate)
                        }
                        store.send(NBAPlayerInfoAction.InitData)
                        _stack.update { it + StackItem.NBAPlayerInfo(id, store) }
                    }
                    is SportDecodableModel.NBAPlayerStats -> {
                        val store = nbaPlayerStatsFactory.create(model.displayModel)
                        store.send(NBAPlayerStatsAction.InitData)
                        _stack.update { it + StackItem.NBAPlayerStats(id, store) }
                    }
                    is SportDecodableModel.NBAPlayerStandings -> {
                        val store = nbaPlayerStandingsFactory.create(model) { delegate ->
                            onNBAPlayerStandingsDelegate(delegate)
                        }
                        store.send(NBAPlayerStandingsAction.InitData)
                        _stack.update { it + StackItem.NBAPlayerStandings(id, store) }
                    }
                    is SportDecodableModel.NBATeamInfo -> {
                        val store = nbaTeamInfoFactory.create(model) { delegate ->
                            onNBATeamInfoDelegate(delegate)
                        }
                        store.send(NBATeamInfoAction.InitData)
                        _stack.update { it + StackItem.NBATeamInfo(id, store) }
                    }
                    is SportDecodableModel.NBATeamStats -> {
                        val store = nbaTeamStatsFactory.create(model.displayModel)
                        store.send(NBATeamStatsAction.InitData)
                        _stack.update { it + StackItem.NBATeamStats(id, store) }
                    }
                    is SportDecodableModel.NBATeamStandings -> {
                        val store = nbaTeamStandingsFactory.create(model) { delegate ->
                            onNBATeamStandingsDelegate(delegate)
                        }
                        store.send(NBATeamStandingsAction.InitData)
                        _stack.update { it + StackItem.NBATeamStandings(id, store) }
                    }
                    is SportDecodableModel.NBALeagueSchedule -> {
                        val store = nbaLeagueScheduleFactory.create(model.displayModel) { delegate ->
                            onNBALeagueScheduleDelegate(delegate)
                        }
                        store.send(NBALeagueScheduleAction.InitData)
                        _stack.update { it + StackItem.NBALeagueSchedule(id, store) }
                    }
                    is SportDecodableModel.NBAGameStats -> {
                        val store = nbaGameStatsFactory.create(model.displayModel) { delegate ->
                            onNBAGameStatsDelegate(id, delegate)
                        }
                        store.send(NBAGameStatsAction.InitData)
                        _stack.update { it + StackItem.NBAGameStats(id, store) }
                    }

                    is SportDecodableModel.MLBPlayerInfo -> {
                        val store = mlbPlayerInfoFactory.create(model) { delegate ->
                            onMLBPlayerInfoDelegate(delegate)
                        }
                        store.send(MLBPlayerInfoAction.InitData)
                        _stack.update { it + StackItem.MLBPlayerInfo(id, store) }
                    }
                    is SportDecodableModel.MLBPlayerStats -> {
                        val store = mlbPlayerStatsFactory.create(model.displayModel)
                        store.send(MLBPlayerStatsAction.InitData)
                        _stack.update { it + StackItem.MLBPlayerStats(id, store) }
                    }
                    is SportDecodableModel.MLBTeamInfo -> {
                        val store = mlbTeamInfoFactory.create(model) { delegate ->
                            onMLBTeamInfoDelegate(delegate)
                        }
                        store.send(MLBTeamInfoAction.InitData)
                        _stack.update { it + StackItem.MLBTeamInfo(id, store) }
                    }
                    is SportDecodableModel.MLBTeamStats -> {
                        val store = mlbTeamStatsFactory.create(model.displayModel)
                        store.send(MLBTeamStatsAction.InitData)
                        _stack.update { it + StackItem.MLBTeamStats(id, store) }
                    }
                    is SportDecodableModel.MLBTeamStandings -> {
                        val store = mlbTeamStandingsFactory.create(model) { delegate ->
                            onMLBTeamStandingsDelegate(delegate)
                        }
                        store.send(MLBTeamStandingsAction.InitData)
                        _stack.update { it + StackItem.MLBTeamStandings(id, store) }
                    }
                    is SportDecodableModel.MLBLeagueSchedule -> {
                        val store = mlbLeagueScheduleFactory.create(model.displayModel) { delegate ->
                            onMLBLeagueScheduleDelegate(delegate)
                        }
                        store.send(MLBLeagueScheduleAction.InitData)
                        _stack.update { it + StackItem.MLBLeagueSchedule(id, store) }
                    }
                    is SportDecodableModel.MLBGameStats -> {
                        val store = mlbGameStatsFactory.create(model.displayModel) { delegate ->
                            onMLBGameStatsDelegate(id, delegate)
                        }
                        store.send(MLBGameStatsAction.InitData)
                        _stack.update { it + StackItem.MLBGameStats(id, store) }
                    }

                    is SportDecodableModel.KBOPlayerInfo -> {
                        val store = kboPlayerInfoFactory.create(model) { delegate ->
                            onKBOPlayerInfoDelegate(delegate)
                        }
                        store.send(KBOPlayerInfoAction.InitData)
                        _stack.update { it + StackItem.KBOPlayerInfo(id, store) }
                    }
                    is SportDecodableModel.KBOPlayerStats -> {
                        val store = kboPlayerStatsFactory.create(model.displayModel)
                        store.send(KBOPlayerStatsAction.InitData)
                        _stack.update { it + StackItem.KBOPlayerStats(id, store) }
                    }
                    is SportDecodableModel.KBOTeamInfo -> {
                        val store = kboTeamInfoFactory.create(model) { delegate ->
                            onKBOTeamInfoDelegate(delegate)
                        }
                        store.send(KBOTeamInfoAction.InitData)
                        _stack.update { it + StackItem.KBOTeamInfo(id, store) }
                    }
                    is SportDecodableModel.KBOTeamStats -> {
                        val store = kboTeamStatsFactory.create(model.displayModel)
                        store.send(KBOTeamStatsAction.InitData)
                        _stack.update { it + StackItem.KBOTeamStats(id, store) }
                    }
                    is SportDecodableModel.KBOTeamStandings -> {
                        val store = kboTeamStandingsFactory.create(model) { delegate ->
                            onKBOTeamStandingsDelegate(delegate)
                        }
                        store.send(KBOTeamStandingsAction.InitData)
                        _stack.update { it + StackItem.KBOTeamStandings(id, store) }
                    }
                    is SportDecodableModel.KBOLeagueSchedule -> {
                        val store = kboLeagueScheduleFactory.create(model.displayModel) { delegate ->
                            onKBOLeagueScheduleDelegate(delegate)
                        }
                        store.send(KBOLeagueScheduleAction.InitData)
                        _stack.update { it + StackItem.KBOLeagueSchedule(id, store) }
                    }
                    is SportDecodableModel.KBOGameStats -> {
                        val store = kboGameStatsFactory.create(model.displayModel) { delegate ->
                            onKBOGameStatsDelegate(id, delegate)
                        }
                        store.send(KBOGameStatsAction.InitData)
                        _stack.update { it + StackItem.KBOGameStats(id, store) }
                    }
                    else -> null
                }
            }
        }
    }

    private fun onFBPlayerInfoDelegate(delegate: FBPlayerInfoDelegate) {
        val id = ViewId()

        when (delegate) {
            is FBPlayerInfoDelegate.ShowPlayerStats -> {
                val store = fbPlayerStatsFactory.create(delegate.model.displayModel)
                store.send(FBPlayerStatsAction.InitData)
                _stack.update { it + StackItem.FBPlayerStats(id, store) }
            }
            is FBPlayerInfoDelegate.ShowGameStats -> {
                val store = fbGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onFBGameStatsDelegate(id, delegate)
                }
                store.send(FBGameStatsAction.InitData)
                _stack.update { it + StackItem.FBGameStats(id, store) }
            }
        }
    }

    private fun onFBPlayerStandingsDelegate(delegate: FBPlayerStandingsDelegate) {
        when (delegate) {
            is FBPlayerStandingsDelegate.ShowPlayerStats -> {
                val store = fbPlayerStatsFactory.create(delegate.model.displayModel)
                store.send(FBPlayerStatsAction.InitData)
                _stack.update { it + StackItem.FBPlayerStats(ViewId(), store) }
            }
        }
    }

    private fun onFBTeamInfoDelegate(delegate: FBTeamInfoDelegate) {
        val id = ViewId()

        when (delegate) {
            is FBTeamInfoDelegate.ShowTeamStats -> {
                val store = fbTeamStatsFactory.create(delegate.model.displayModel)
                store.send(FBTeamStatsAction.InitData)
                _stack.update { it + StackItem.FBTeamStats(id, store) }
            }
            is FBTeamInfoDelegate.ShowGameStats -> {
                val store = fbGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onFBGameStatsDelegate(id, delegate)
                }
                store.send(FBGameStatsAction.InitData)
                _stack.update { it + StackItem.FBGameStats(id, store) }
            }
        }
    }

    private fun onFBTeamStandingsDelegate(delegate: FBTeamStandingsDelegate) {
        when (delegate) {
            is FBTeamStandingsDelegate.ShowTeamStats -> {
                val store = fbTeamStatsFactory.create((delegate.model as SportDecodableModel.FBTeamStats).displayModel)
                store.send(FBTeamStatsAction.InitData)
                _stack.update { it + StackItem.FBTeamStats(ViewId(), store) }
            }
        }
    }

    private fun onFBLeagueScheduleDelegate(delegate: FBLeagueScheduleDelegate) {
        val id = ViewId()

        when (delegate) {
            is FBLeagueScheduleDelegate.ShowGameStats -> {
                _didPop.value = false
                // FBLeagueScheduleView에서 아이템 클릭으로 FBGameStatsView보여줄때 _includesPreviousView = true로 설정해 줘야 함.
                val lastItem = stack.value.lastOrNull()
                if (lastItem != null) {
                    if (lastItem is StackItem.FBLeagueSchedule) {
                        _includesPreviousView.value = true
                    }
                }

                val store = fbGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onFBGameStatsDelegate(id, delegate)
                }
                store.send(FBGameStatsAction.InitData)
                _stack.update { it + StackItem.FBGameStats(id, store) }
            }
        }
    }

    private fun onFBGameStatsDelegate(id: ViewId, delegate: FBGameStatsDelegate) {
        when (delegate) {
            is FBGameStatsDelegate.RefreshGame -> {
                _didPop.value = false
                _includesPreviousView.value = false

                // 현재 화면인 FBGameStats의 이전 화면인 FBLeagueSchedule을 찾아서 해당 Store에서 필요한 state를 업데이트 시킨다.
                val idx = stack.value.indexOfFirst { it.id == id }
                if (idx > 0) {
                    for (prev in stack.value.subList(0, idx).asReversed()) {
                        if (prev is StackItem.FBLeagueSchedule) {
                            prev.store.send(FBLeagueScheduleAction.UpdateStateByRefreshGame(delegate.model))
                            break
                        }
                    }
                }
            }
        }
    }

    private fun onNBAPlayerInfoDelegate(delegate: NBAPlayerInfoDelegate) {
        val id = ViewId()

        when (delegate) {
            is NBAPlayerInfoDelegate.ShowPlayerStats -> {
                val store = nbaPlayerStatsFactory.create(delegate.model.displayModel)
                store.send(NBAPlayerStatsAction.InitData)
                _stack.update { it + StackItem.NBAPlayerStats(id, store) }
            }
            is NBAPlayerInfoDelegate.ShowGameStats -> {
                val store = nbaGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onNBAGameStatsDelegate(id, delegate)
                }
                store.send(NBAGameStatsAction.InitData)
                _stack.update { it + StackItem.NBAGameStats(id, store) }
            }
        }
    }

    private fun onNBAPlayerStandingsDelegate(delegate: NBAPlayerStandingsDelegate) {
        when (delegate) {
            is NBAPlayerStandingsDelegate.ShowPlayerStats -> {
                val store = nbaPlayerStatsFactory.create((delegate.model as SportDecodableModel.NBAPlayerStats).displayModel)
                store.send(NBAPlayerStatsAction.InitData)
                _stack.update { it + StackItem.NBAPlayerStats(ViewId(), store) }
            }
        }
    }

    private fun onNBATeamInfoDelegate(delegate: NBATeamInfoDelegate) {
        val id = ViewId()

        when (delegate) {
            is NBATeamInfoDelegate.ShowTeamStats -> {
                val store = nbaTeamStatsFactory.create(delegate.model.displayModel)
                store.send(NBATeamStatsAction.InitData)
                _stack.update { it + StackItem.NBATeamStats(id, store) }
            }
            is NBATeamInfoDelegate.ShowGameStats -> {
                val store = nbaGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onNBAGameStatsDelegate(id, delegate)
                }
                store.send(NBAGameStatsAction.InitData)
                _stack.update { it + StackItem.NBAGameStats(id, store) }
            }
        }
    }

    private fun onNBATeamStandingsDelegate(delegate: NBATeamStandingsDelegate) {
        when (delegate) {
            is NBATeamStandingsDelegate.ShowTeamStats -> {
                val store = nbaTeamStatsFactory.create((delegate.model as SportDecodableModel.NBATeamStats).displayModel)
                store.send(NBATeamStatsAction.InitData)
                _stack.update { it + StackItem.NBATeamStats(ViewId(), store) }
            }
        }
    }

    private fun onNBALeagueScheduleDelegate(delegate: NBALeagueScheduleDelegate) {
        val id = ViewId()

        when (delegate) {
            is NBALeagueScheduleDelegate.ShowGameStats -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = nbaGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onNBAGameStatsDelegate(id, delegate)
                }
                store.send(NBAGameStatsAction.InitData)
                _stack.update { it + StackItem.NBAGameStats(id, store) }
            }
        }
    }

    private fun onNBAGameStatsDelegate(id: ViewId, delegate: NBAGameStatsDelegate) {
        when (delegate) {
            is NBAGameStatsDelegate.RefreshGame -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val idx = stack.value.indexOfFirst { it.id == id }
                if (idx > 0) {
                    for (prev in stack.value.subList(0, idx).asReversed()) {
                        if (prev is StackItem.NBALeagueSchedule) {
                            prev.store.send(NBALeagueScheduleAction.UpdateStateByRefreshGame(delegate.model))
                            break
                        }
                    }
                }
            }
        }
    }

    private fun onMLBPlayerInfoDelegate(delegate: MLBPlayerInfoDelegate) {
        val id = ViewId()

        when (delegate) {
            is MLBPlayerInfoDelegate.ShowPlayerStats -> {
                val store = mlbPlayerStatsFactory.create(delegate.model.displayModel)
                store.send(MLBPlayerStatsAction.InitData)
                _stack.update { it + StackItem.MLBPlayerStats(id, store) }
            }
            is MLBPlayerInfoDelegate.ShowGameStats -> {
                val store = mlbGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onMLBGameStatsDelegate(id, delegate)
                }
                store.send(MLBGameStatsAction.InitData)
                _stack.update { it + StackItem.MLBGameStats(id, store) }
            }
        }
    }

    private fun onMLBTeamInfoDelegate(delegate: MLBTeamInfoDelegate) {
        val id = ViewId()

        when (delegate) {
            is MLBTeamInfoDelegate.ShowTeamStats -> {
                val store = mlbTeamStatsFactory.create(delegate.model.displayModel)
                store.send(MLBTeamStatsAction.InitData)
                _stack.update { it + StackItem.MLBTeamStats(id, store) }
            }
            is MLBTeamInfoDelegate.ShowGameStats -> {
                val store = mlbGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onMLBGameStatsDelegate(id, delegate)
                }
                store.send(MLBGameStatsAction.InitData)
                _stack.update { it + StackItem.MLBGameStats(id, store) }
            }
        }
    }

    private fun onMLBTeamStandingsDelegate(delegate: MLBTeamStandingsDelegate) {
        when (delegate) {
            is MLBTeamStandingsDelegate.ShowTeamStats -> {
                val store = mlbTeamStatsFactory.create((delegate.model as SportDecodableModel.MLBTeamStats).displayModel)
                store.send(MLBTeamStatsAction.InitData)
                _stack.update { it + StackItem.MLBTeamStats(ViewId(), store) }
            }
        }
    }

    private fun onMLBLeagueScheduleDelegate(delegate: MLBLeagueScheduleDelegate) {
        val id = ViewId()

        when (delegate) {
            is MLBLeagueScheduleDelegate.ShowGameStats -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = mlbGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onMLBGameStatsDelegate(id, delegate)
                }
                store.send(MLBGameStatsAction.InitData)
                _stack.update { it + StackItem.MLBGameStats(id, store) }
            }
        }
    }

    private fun onMLBGameStatsDelegate(id: ViewId, delegate: MLBGameStatsDelegate) {
        when (delegate) {
            is MLBGameStatsDelegate.RefreshGame -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val idx = stack.value.indexOfFirst { it.id == id }
                if (idx > 0) {
                    for (prev in stack.value.subList(0, idx).asReversed()) {
                        if (prev is StackItem.MLBLeagueSchedule) {
                            prev.store.send(MLBLeagueScheduleAction.UpdateStateByRefreshGame(delegate.model))
                            break
                        }
                    }
                }
            }
        }
    }

    private fun onKBOPlayerInfoDelegate(delegate: KBOPlayerInfoDelegate) {
        val id = ViewId()

        when (delegate) {
            is KBOPlayerInfoDelegate.ShowPlayerStats -> {
                val store = kboPlayerStatsFactory.create(delegate.model.displayModel)
                store.send(KBOPlayerStatsAction.InitData)
                _stack.update { it + StackItem.KBOPlayerStats(id, store) }
            }
            is KBOPlayerInfoDelegate.ShowGameStats -> {
                val store = kboGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onKBOGameStatsDelegate(id, delegate)
                }
                store.send(KBOGameStatsAction.InitData)
                _stack.update { it + StackItem.KBOGameStats(id, store) }
            }
        }
    }

    private fun onKBOTeamInfoDelegate(delegate: KBOTeamInfoDelegate) {
        val id = ViewId()

        when (delegate) {
            is KBOTeamInfoDelegate.ShowTeamStats -> {
                val store = kboTeamStatsFactory.create(delegate.model.displayModel)
                store.send(KBOTeamStatsAction.InitData)
                _stack.update { it + StackItem.KBOTeamStats(id, store) }
            }
            is KBOTeamInfoDelegate.ShowGameStats -> {
                val store = kboGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onKBOGameStatsDelegate(id, delegate)
                }
                store.send(KBOGameStatsAction.InitData)
                _stack.update { it + StackItem.KBOGameStats(id, store) }
            }
        }
    }

    private fun onKBOTeamStandingsDelegate(delegate: KBOTeamStandingsDelegate) {
        when (delegate) {
            is KBOTeamStandingsDelegate.ShowTeamStats -> {
                val store = kboTeamStatsFactory.create((delegate.model as SportDecodableModel.KBOTeamStats).displayModel)
                store.send(KBOTeamStatsAction.InitData)
                _stack.update { it + StackItem.KBOTeamStats(ViewId(), store) }
            }
        }
    }

    private fun onKBOLeagueScheduleDelegate(delegate: KBOLeagueScheduleDelegate) {
        val id = ViewId()

        when (delegate) {
            is KBOLeagueScheduleDelegate.ShowGameStats -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = kboGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onKBOGameStatsDelegate(id, delegate)
                }
                store.send(KBOGameStatsAction.InitData)
                _stack.update { it + StackItem.KBOGameStats(id, store) }
            }
        }
    }

    private fun onKBOGameStatsDelegate(id: ViewId, delegate: KBOGameStatsDelegate) {
        when (delegate) {
            is KBOGameStatsDelegate.RefreshGame -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val idx = stack.value.indexOfFirst { it.id == id }
                if (idx > 0) {
                    for (prev in stack.value.subList(0, idx).asReversed()) {
                        if (prev is StackItem.KBOLeagueSchedule) {
                            prev.store.send(KBOLeagueScheduleAction.UpdateStateByRefreshGame(delegate.model))
                            break
                        }
                    }
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
            is StackItem.FBGameStats -> item.store.dispose()

            is StackItem.NBAPlayerInfo -> item.store.dispose()
            is StackItem.NBAPlayerStats -> item.store.dispose()
            is StackItem.NBAPlayerStandings -> item.store.dispose()
            is StackItem.NBATeamInfo -> item.store.dispose()
            is StackItem.NBATeamStats -> item.store.dispose()
            is StackItem.NBATeamStandings -> item.store.dispose()
            is StackItem.NBALeagueSchedule -> item.store.dispose()
            is StackItem.NBAGameStats -> item.store.dispose()

            is StackItem.MLBPlayerInfo -> item.store.dispose()
            is StackItem.MLBPlayerStats -> item.store.dispose()
            is StackItem.MLBTeamInfo -> item.store.dispose()
            is StackItem.MLBTeamStats -> item.store.dispose()
            is StackItem.MLBTeamStandings -> item.store.dispose()
            is StackItem.MLBLeagueSchedule -> item.store.dispose()
            is StackItem.MLBGameStats -> item.store.dispose()

            is StackItem.KBOPlayerInfo -> item.store.dispose()
            is StackItem.KBOPlayerStats -> item.store.dispose()
            is StackItem.KBOTeamInfo -> item.store.dispose()
            is StackItem.KBOTeamStats -> item.store.dispose()
            is StackItem.KBOTeamStandings -> item.store.dispose()
            is StackItem.KBOLeagueSchedule -> item.store.dispose()
            is StackItem.KBOGameStats -> item.store.dispose()
        }
    }
}