package com.moare.android.features.search.display

import android.app.Activity
import com.moare.android.features.moat.display.MoatStackDelegate
import com.moare.android.features.moat.display.MoatStackStore
import com.moare.android.features.search.display.football.store.FBGameStatsAction
import com.moare.android.features.search.display.football.store.FBGameStatsDelegate
import com.moare.android.features.search.display.football.store.FBGameStatsStore
import com.moare.android.features.search.display.football.store.FBLeagueScheduleAction
import com.moare.android.features.search.display.football.store.FBLeagueScheduleStore
import com.moare.android.features.search.display.football.store.FBLeagueScheduleDelegate
import com.moare.android.features.search.display.football.store.FBPlayerInfoAction
import com.moare.android.features.search.display.football.store.FBPlayerInfoDelegate
import com.moare.android.features.search.display.football.store.FBPlayerInfoStore
import com.moare.android.features.search.display.football.store.FBPlayerStandingsAction
import com.moare.android.features.search.display.football.store.FBPlayerStandingsDelegate
import com.moare.android.features.search.display.football.store.FBPlayerStandingsStore
import com.moare.android.features.search.display.football.store.FBPlayerStatsAction
import com.moare.android.features.search.display.football.store.FBPlayerStatsStore
import com.moare.android.features.search.display.football.store.FBTeamInfoAction
import com.moare.android.features.search.display.football.store.FBTeamInfoDelegate
import com.moare.android.features.search.display.football.store.FBTeamInfoStore
import com.moare.android.features.search.display.football.store.FBTeamStandingsAction
import com.moare.android.features.search.display.football.store.FBTeamStandingsDelegate
import com.moare.android.features.search.display.football.store.FBTeamStandingsStore
import com.moare.android.features.search.display.football.store.FBTeamStatsAction
import com.moare.android.features.search.display.football.store.FBTeamStatsStore
import com.moare.android.features.search.display.football.store.FBTournamentAction
import com.moare.android.features.search.display.football.store.FBTournamentStore
import com.moare.android.features.search.display.kbo.store.KBOGameStatsAction
import com.moare.android.features.search.display.kbo.store.KBOGameStatsDelegate
import com.moare.android.features.search.display.kbo.store.KBOGameStatsStore
import com.moare.android.features.search.display.kbo.store.KBOLeagueScheduleAction
import com.moare.android.features.search.display.kbo.store.KBOLeagueScheduleDelegate
import com.moare.android.features.search.display.kbo.store.KBOLeagueScheduleStore
import com.moare.android.features.search.display.kbo.store.KBOPlayerInfoAction
import com.moare.android.features.search.display.kbo.store.KBOPlayerInfoDelegate
import com.moare.android.features.search.display.kbo.store.KBOPlayerInfoStore
import com.moare.android.features.search.display.kbo.store.KBOPlayerStatsAction
import com.moare.android.features.search.display.kbo.store.KBOPlayerStatsStore
import com.moare.android.features.search.display.kbo.store.KBOTeamInfoAction
import com.moare.android.features.search.display.kbo.store.KBOTeamInfoDelegate
import com.moare.android.features.search.display.kbo.store.KBOTeamInfoStore
import com.moare.android.features.search.display.kbo.store.KBOTeamStandingsAction
import com.moare.android.features.search.display.kbo.store.KBOTeamStandingsDelegate
import com.moare.android.features.search.display.kbo.store.KBOTeamStandingsStore
import com.moare.android.features.search.display.kbo.store.KBOTeamStatsAction
import com.moare.android.features.search.display.kbo.store.KBOTeamStatsStore
import com.moare.android.features.search.display.kbo.store.KBOTournamentAction
import com.moare.android.features.search.display.kbo.store.KBOTournamentDelegate
import com.moare.android.features.search.display.kbo.store.KBOTournamentStore
import com.moare.android.features.search.display.mlb.store.MLBGameStatsAction
import com.moare.android.features.search.display.mlb.store.MLBGameStatsDelegate
import com.moare.android.features.search.display.mlb.store.MLBGameStatsStore
import com.moare.android.features.search.display.mlb.store.MLBLeagueScheduleAction
import com.moare.android.features.search.display.mlb.store.MLBLeagueScheduleDelegate
import com.moare.android.features.search.display.mlb.store.MLBLeagueScheduleStore
import com.moare.android.features.search.display.mlb.store.MLBPlayerInfoAction
import com.moare.android.features.search.display.mlb.store.MLBPlayerInfoDelegate
import com.moare.android.features.search.display.mlb.store.MLBPlayerInfoStore
import com.moare.android.features.search.display.mlb.store.MLBPlayerStatsAction
import com.moare.android.features.search.display.mlb.store.MLBPlayerStatsStore
import com.moare.android.features.search.display.mlb.store.MLBTeamInfoAction
import com.moare.android.features.search.display.mlb.store.MLBTeamInfoDelegate
import com.moare.android.features.search.display.mlb.store.MLBTeamInfoStore
import com.moare.android.features.search.display.mlb.store.MLBTeamStandingsAction
import com.moare.android.features.search.display.mlb.store.MLBTeamStandingsDelegate
import com.moare.android.features.search.display.mlb.store.MLBTeamStandingsStore
import com.moare.android.features.search.display.mlb.store.MLBTeamStatsAction
import com.moare.android.features.search.display.mlb.store.MLBTeamStatsStore
import com.moare.android.features.search.display.mlb.store.MLBTournamentAction
import com.moare.android.features.search.display.mlb.store.MLBTournamentDelegate
import com.moare.android.features.search.display.mlb.store.MLBTournamentStore
import com.moare.android.features.search.display.nba.store.NBAGameStatsAction
import com.moare.android.features.search.display.nba.store.NBAGameStatsDelegate
import com.moare.android.features.search.display.nba.store.NBAGameStatsStore
import com.moare.android.features.search.display.nba.store.NBALeagueScheduleAction
import com.moare.android.features.search.display.nba.store.NBALeagueScheduleDelegate
import com.moare.android.features.search.display.nba.store.NBALeagueScheduleStore
import com.moare.android.features.search.display.nba.store.NBAPlayerInfoAction
import com.moare.android.features.search.display.nba.store.NBAPlayerInfoDelegate
import com.moare.android.features.search.display.nba.store.NBAPlayerInfoStore
import com.moare.android.features.search.display.nba.store.NBAPlayerStandingsAction
import com.moare.android.features.search.display.nba.store.NBAPlayerStandingsDelegate
import com.moare.android.features.search.display.nba.store.NBAPlayerStandingsStore
import com.moare.android.features.search.display.nba.store.NBAPlayerStatsAction
import com.moare.android.features.search.display.nba.store.NBAPlayerStatsStore
import com.moare.android.features.search.display.nba.store.NBATeamInfoAction
import com.moare.android.features.search.display.nba.store.NBATeamInfoDelegate
import com.moare.android.features.search.display.nba.store.NBATeamInfoStore
import com.moare.android.features.search.display.nba.store.NBATeamStandingsAction
import com.moare.android.features.search.display.nba.store.NBATeamStandingsDelegate
import com.moare.android.features.search.display.nba.store.NBATeamStandingsStore
import com.moare.android.features.search.display.nba.store.NBATeamStatsAction
import com.moare.android.features.search.display.nba.store.NBATeamStatsStore
import com.moare.android.features.search.display.nba.store.NBATournamentAction
import com.moare.android.features.search.display.nba.store.NBATournamentDelegate
import com.moare.android.features.search.display.nba.store.NBATournamentStore
import com.moare.android.features.search.display.search.store.SearchAction
import com.moare.android.features.search.display.search.store.SearchDelegate
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.models.SportDecodableModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@JvmInline
value class ViewId(val value: String = java.util.UUID.randomUUID().toString())

sealed interface SearchStackItem {
    val id: ViewId

    data class FBPlayerInfo(override val id: ViewId, val store: FBPlayerInfoStore) : SearchStackItem
    data class FBPlayerStats(override val id: ViewId, val store: FBPlayerStatsStore) : SearchStackItem
    data class FBPlayerStandings(override val id: ViewId, val store: FBPlayerStandingsStore) :
        SearchStackItem
    data class FBTeamInfo(override val id: ViewId, val store: FBTeamInfoStore) : SearchStackItem
    data class FBTeamStats(override val id: ViewId, val store: FBTeamStatsStore) : SearchStackItem
    data class FBTeamStandings(override val id: ViewId, val store: FBTeamStandingsStore) : SearchStackItem
    data class FBLeagueSchedule(override val id: ViewId, val store: FBLeagueScheduleStore) :
        SearchStackItem
    data class FBGameStats(override val id: ViewId, val store: FBGameStatsStore) : SearchStackItem
    data class FBTournament(override val id: ViewId, val store: FBTournamentStore) : SearchStackItem

    data class NBAPlayerInfo(override val id: ViewId, val store: NBAPlayerInfoStore) : SearchStackItem
    data class NBAPlayerStats(override val id: ViewId, val store: NBAPlayerStatsStore) : SearchStackItem
    data class NBAPlayerStandings(override val id: ViewId, val store: NBAPlayerStandingsStore) :
        SearchStackItem
    data class NBATeamInfo(override val id: ViewId, val store: NBATeamInfoStore) : SearchStackItem
    data class NBATeamStats(override val id: ViewId, val store: NBATeamStatsStore) : SearchStackItem
    data class NBATeamStandings(override val id: ViewId, val store: NBATeamStandingsStore) :
        SearchStackItem
    data class NBALeagueSchedule(override val id: ViewId, val store: NBALeagueScheduleStore) :
        SearchStackItem
    data class NBAGameStats(override val id: ViewId, val store: NBAGameStatsStore) : SearchStackItem
    data class NBATournament(override val id: ViewId, val store: NBATournamentStore) : SearchStackItem

    data class MLBPlayerInfo(override val id: ViewId, val store: MLBPlayerInfoStore) : SearchStackItem
    data class MLBPlayerStats(override val id: ViewId, val store: MLBPlayerStatsStore) : SearchStackItem
    data class MLBTeamInfo(override val id: ViewId, val store: MLBTeamInfoStore) : SearchStackItem
    data class MLBTeamStats(override val id: ViewId, val store: MLBTeamStatsStore) : SearchStackItem
    data class MLBTeamStandings(override val id: ViewId, val store: MLBTeamStandingsStore) :
        SearchStackItem
    data class MLBLeagueSchedule(override val id: ViewId, val store: MLBLeagueScheduleStore) :
        SearchStackItem
    data class MLBGameStats(override val id: ViewId, val store: MLBGameStatsStore) : SearchStackItem
    data class MLBTournament(override val id: ViewId, val store: MLBTournamentStore) : SearchStackItem

    data class KBOPlayerInfo(override val id: ViewId, val store: KBOPlayerInfoStore) : SearchStackItem
    data class KBOPlayerStats(override val id: ViewId, val store: KBOPlayerStatsStore) : SearchStackItem
    data class KBOTeamInfo(override val id: ViewId, val store: KBOTeamInfoStore) : SearchStackItem
    data class KBOTeamStats(override val id: ViewId, val store: KBOTeamStatsStore) : SearchStackItem
    data class KBOTeamStandings(override val id: ViewId, val store: KBOTeamStandingsStore) :
        SearchStackItem
    data class KBOLeagueSchedule(override val id: ViewId, val store: KBOLeagueScheduleStore) :
        SearchStackItem
    data class KBOGameStats(override val id: ViewId, val store: KBOGameStatsStore) : SearchStackItem
    data class KBOTournament(override val id: ViewId, val store: KBOTournamentStore) : SearchStackItem
}

sealed interface SearchStackDelegate {
}

class SearchStackStore @AssistedInject constructor(
    private val searchFactory: SearchStore.Factory,
    private val fbPlayerInfoFactory: FBPlayerInfoStore.Factory,
    private val fbPlayerStatsFactory: FBPlayerStatsStore.Factory,
    private val fbPlayerStandingsFactory: FBPlayerStandingsStore.Factory,
    private val fbTeamInfoFactory: FBTeamInfoStore.Factory,
    private val fbTeamStatsFactory: FBTeamStatsStore.Factory,
    private val fbTeamStandingsFactory: FBTeamStandingsStore.Factory,
    private val fbLeagueScheduleFactory: FBLeagueScheduleStore.Factory,
    private val fbGameStatsFactory: FBGameStatsStore.Factory,
    private val fbTournamentFactory: FBTournamentStore.Factory,

    private val nbaPlayerInfoFactory: NBAPlayerInfoStore.Factory,
    private val nbaPlayerStatsFactory: NBAPlayerStatsStore.Factory,
    private val nbaPlayerStandingsFactory: NBAPlayerStandingsStore.Factory,
    private val nbaTeamInfoFactory: NBATeamInfoStore.Factory,
    private val nbaTeamStatsFactory: NBATeamStatsStore.Factory,
    private val nbaTeamStandingsFactory: NBATeamStandingsStore.Factory,
    private val nbaLeagueScheduleFactory: NBALeagueScheduleStore.Factory,
    private val nbaGameStatsFactory: NBAGameStatsStore.Factory,
    private val nbaTournamentFactory: NBATournamentStore.Factory,

    private val mlbPlayerInfoFactory: MLBPlayerInfoStore.Factory,
    private val mlbPlayerStatsFactory: MLBPlayerStatsStore.Factory,
    private val mlbTeamInfoFactory: MLBTeamInfoStore.Factory,
    private val mlbTeamStatsFactory: MLBTeamStatsStore.Factory,
    private val mlbTeamStandingsFactory: MLBTeamStandingsStore.Factory,
    private val mlbLeagueScheduleFactory: MLBLeagueScheduleStore.Factory,
    private val mlbGameStatsFactory: MLBGameStatsStore.Factory,
    private val mlbTournamentFactory: MLBTournamentStore.Factory,

    private val kboPlayerInfoFactory: KBOPlayerInfoStore.Factory,
    private val kboPlayerStatsFactory: KBOPlayerStatsStore.Factory,
    private val kboTeamInfoFactory: KBOTeamInfoStore.Factory,
    private val kboTeamStatsFactory: KBOTeamStatsStore.Factory,
    private val kboTeamStandingsFactory: KBOTeamStandingsStore.Factory,
    private val kboLeagueScheduleFactory: KBOLeagueScheduleStore.Factory,
    private val kboGameStatsFactory: KBOGameStatsStore.Factory,
    private val kboTournamentFactory: KBOTournamentStore.Factory,

    @Assisted private val scope: CoroutineScope,
    @Assisted private val emitToParent: (SearchStackDelegate) -> Unit
) {
    @AssistedFactory
    interface Factory {
        fun create(
            scope: CoroutineScope,
            emitToParent: (SearchStackDelegate) -> Unit
        ) : SearchStackStore
    }

    private val _stack = MutableStateFlow<List<SearchStackItem>>(emptyList())
    val stack: StateFlow<List<SearchStackItem>> = _stack

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
                        _stack.update { it + SearchStackItem.FBPlayerInfo(id, store) }
                    }
                    is SportDecodableModel.FBPlayerStats -> {
                        val store = fbPlayerStatsFactory.create(model.displayModel)
                        store.send(FBPlayerStatsAction.InitData)
                        _stack.update { it + SearchStackItem.FBPlayerStats(id, store) }
                    }
                    is SportDecodableModel.FBPlayerStandings -> {
                        val store = fbPlayerStandingsFactory.create(model) { delegate ->
                            onFBPlayerStandingsDelegate(delegate)
                        }
                        store.send(FBPlayerStandingsAction.InitData)
                        _stack.update { it + SearchStackItem.FBPlayerStandings(id, store) }
                    }
                    is SportDecodableModel.FBTeamInfo -> {
                        val store = fbTeamInfoFactory.create(model) { delegate ->
                            onFBTeamInfoDelegate(delegate)
                        }
                        store.send(FBTeamInfoAction.InitData)
                        _stack.update { it + SearchStackItem.FBTeamInfo(id, store) }
                    }
                    is SportDecodableModel.FBTeamStats -> {
                        val store = fbTeamStatsFactory.create(model.displayModel)
                        store.send(FBTeamStatsAction.InitData)
                        _stack.update { it + SearchStackItem.FBTeamStats(id, store) }
                    }
                    is SportDecodableModel.FBTeamStandings -> {
                        val store = fbTeamStandingsFactory.create(model) { delegate ->
                            onFBTeamStandingsDelegate(delegate)
                        }
                        store.send(FBTeamStandingsAction.InitData)
                        _stack.update { it + SearchStackItem.FBTeamStandings(id, store) }
                    }
                    is SportDecodableModel.FBLeagueSchedule -> {
                        val store = fbLeagueScheduleFactory.create(model.displayModel) { delegate ->
                            onFBLeagueScheduleDelegate(delegate)
                        }
                        store.send(FBLeagueScheduleAction.InitData)
                        _stack.update { it + SearchStackItem.FBLeagueSchedule(id, store) }
                    }
                    is SportDecodableModel.FBGameStats -> {
                        val store = fbGameStatsFactory.create(model.displayModel) { delegate ->
                            onFBGameStatsDelegate(id, delegate)
                        }
                        store.send(FBGameStatsAction.InitData)
                        _stack.update { it + SearchStackItem.FBGameStats(id, store) }
                    }
                    is SportDecodableModel.FBTournament -> {
                        val store = fbTournamentFactory.create(model.displayModel)
                        store.send(FBTournamentAction.InitData)
                        _stack.update { it + SearchStackItem.FBTournament(id, store) }
                    }

                    is SportDecodableModel.NBAPlayerInfo -> {
                        val store = nbaPlayerInfoFactory.create(model) { delegate ->
                            onNBAPlayerInfoDelegate(delegate)
                        }
                        store.send(NBAPlayerInfoAction.InitData)
                        _stack.update { it + SearchStackItem.NBAPlayerInfo(id, store) }
                    }
                    is SportDecodableModel.NBAPlayerStats -> {
                        val store = nbaPlayerStatsFactory.create(model.displayModel)
                        store.send(NBAPlayerStatsAction.InitData)
                        _stack.update { it + SearchStackItem.NBAPlayerStats(id, store) }
                    }
                    is SportDecodableModel.NBAPlayerStandings -> {
                        val store = nbaPlayerStandingsFactory.create(model) { delegate ->
                            onNBAPlayerStandingsDelegate(delegate)
                        }
                        store.send(NBAPlayerStandingsAction.InitData)
                        _stack.update { it + SearchStackItem.NBAPlayerStandings(id, store) }
                    }
                    is SportDecodableModel.NBATeamInfo -> {
                        val store = nbaTeamInfoFactory.create(model) { delegate ->
                            onNBATeamInfoDelegate(delegate)
                        }
                        store.send(NBATeamInfoAction.InitData)
                        _stack.update { it + SearchStackItem.NBATeamInfo(id, store) }
                    }
                    is SportDecodableModel.NBATeamStats -> {
                        val store = nbaTeamStatsFactory.create(model.displayModel)
                        store.send(NBATeamStatsAction.InitData)
                        _stack.update { it + SearchStackItem.NBATeamStats(id, store) }
                    }
                    is SportDecodableModel.NBATeamStandings -> {
                        val store = nbaTeamStandingsFactory.create(model) { delegate ->
                            onNBATeamStandingsDelegate(delegate)
                        }
                        store.send(NBATeamStandingsAction.InitData)
                        _stack.update { it + SearchStackItem.NBATeamStandings(id, store) }
                    }
                    is SportDecodableModel.NBALeagueSchedule -> {
                        val store = nbaLeagueScheduleFactory.create(model.displayModel) { delegate ->
                            onNBALeagueScheduleDelegate(delegate)
                        }
                        store.send(NBALeagueScheduleAction.InitData)
                        _stack.update { it + SearchStackItem.NBALeagueSchedule(id, store) }
                    }
                    is SportDecodableModel.NBAGameStats -> {
                        val store = nbaGameStatsFactory.create(model.displayModel) { delegate ->
                            onNBAGameStatsDelegate(id, delegate)
                        }
                        store.send(NBAGameStatsAction.InitData)
                        _stack.update { it + SearchStackItem.NBAGameStats(id, store) }
                    }
                    is SportDecodableModel.NBATournament -> {
                        val store = nbaTournamentFactory.create(model.displayModel) { delegate ->
                            onNBATournamentDelegate(delegate)
                        }
                        store.send(NBATournamentAction.InitData)
                        _stack.update { it + SearchStackItem.NBATournament(id, store) }
                    }

                    is SportDecodableModel.MLBPlayerInfo -> {
                        val store = mlbPlayerInfoFactory.create(model) { delegate ->
                            onMLBPlayerInfoDelegate(delegate)
                        }
                        store.send(MLBPlayerInfoAction.InitData)
                        _stack.update { it + SearchStackItem.MLBPlayerInfo(id, store) }
                    }
                    is SportDecodableModel.MLBPlayerStats -> {
                        val store = mlbPlayerStatsFactory.create(model.displayModel)
                        store.send(MLBPlayerStatsAction.InitData)
                        _stack.update { it + SearchStackItem.MLBPlayerStats(id, store) }
                    }
                    is SportDecodableModel.MLBTeamInfo -> {
                        val store = mlbTeamInfoFactory.create(model) { delegate ->
                            onMLBTeamInfoDelegate(delegate)
                        }
                        store.send(MLBTeamInfoAction.InitData)
                        _stack.update { it + SearchStackItem.MLBTeamInfo(id, store) }
                    }
                    is SportDecodableModel.MLBTeamStats -> {
                        val store = mlbTeamStatsFactory.create(model.displayModel)
                        store.send(MLBTeamStatsAction.InitData)
                        _stack.update { it + SearchStackItem.MLBTeamStats(id, store) }
                    }
                    is SportDecodableModel.MLBTeamStandings -> {
                        val store = mlbTeamStandingsFactory.create(model) { delegate ->
                            onMLBTeamStandingsDelegate(delegate)
                        }
                        store.send(MLBTeamStandingsAction.InitData)
                        _stack.update { it + SearchStackItem.MLBTeamStandings(id, store) }
                    }
                    is SportDecodableModel.MLBLeagueSchedule -> {
                        val store = mlbLeagueScheduleFactory.create(model.displayModel) { delegate ->
                            onMLBLeagueScheduleDelegate(delegate)
                        }
                        store.send(MLBLeagueScheduleAction.InitData)
                        _stack.update { it + SearchStackItem.MLBLeagueSchedule(id, store) }
                    }
                    is SportDecodableModel.MLBGameStats -> {
                        val store = mlbGameStatsFactory.create(model.displayModel) { delegate ->
                            onMLBGameStatsDelegate(id, delegate)
                        }
                        store.send(MLBGameStatsAction.InitData)
                        _stack.update { it + SearchStackItem.MLBGameStats(id, store) }
                    }
                    is SportDecodableModel.MLBTournament -> {
                        val store = mlbTournamentFactory.create(model.displayModel) { delegate ->
                            onMLBTournamentDelegate(delegate)
                        }
                        store.send(MLBTournamentAction.InitData)
                        _stack.update { it + SearchStackItem.MLBTournament(id, store) }
                    }

                    is SportDecodableModel.KBOPlayerInfo -> {
                        val store = kboPlayerInfoFactory.create(model) { delegate ->
                            onKBOPlayerInfoDelegate(delegate)
                        }
                        store.send(KBOPlayerInfoAction.InitData)
                        _stack.update { it + SearchStackItem.KBOPlayerInfo(id, store) }
                    }
                    is SportDecodableModel.KBOPlayerStats -> {
                        val store = kboPlayerStatsFactory.create(model.displayModel)
                        store.send(KBOPlayerStatsAction.InitData)
                        _stack.update { it + SearchStackItem.KBOPlayerStats(id, store) }
                    }
                    is SportDecodableModel.KBOTeamInfo -> {
                        val store = kboTeamInfoFactory.create(model) { delegate ->
                            onKBOTeamInfoDelegate(delegate)
                        }
                        store.send(KBOTeamInfoAction.InitData)
                        _stack.update { it + SearchStackItem.KBOTeamInfo(id, store) }
                    }
                    is SportDecodableModel.KBOTeamStats -> {
                        val store = kboTeamStatsFactory.create(model.displayModel)
                        store.send(KBOTeamStatsAction.InitData)
                        _stack.update { it + SearchStackItem.KBOTeamStats(id, store) }
                    }
                    is SportDecodableModel.KBOTeamStandings -> {
                        val store = kboTeamStandingsFactory.create(model) { delegate ->
                            onKBOTeamStandingsDelegate(delegate)
                        }
                        store.send(KBOTeamStandingsAction.InitData)
                        _stack.update { it + SearchStackItem.KBOTeamStandings(id, store) }
                    }
                    is SportDecodableModel.KBOLeagueSchedule -> {
                        val store = kboLeagueScheduleFactory.create(model.displayModel) { delegate ->
                            onKBOLeagueScheduleDelegate(delegate)
                        }
                        store.send(KBOLeagueScheduleAction.InitData)
                        _stack.update { it + SearchStackItem.KBOLeagueSchedule(id, store) }
                    }
                    is SportDecodableModel.KBOGameStats -> {
                        val store = kboGameStatsFactory.create(model.displayModel) { delegate ->
                            onKBOGameStatsDelegate(id, delegate)
                        }
                        store.send(KBOGameStatsAction.InitData)
                        _stack.update { it + SearchStackItem.KBOGameStats(id, store) }
                    }
                    is SportDecodableModel.KBOTournament -> {
                        val store = kboTournamentFactory.create(model.displayModel) { delegate ->
                            onKBOTournamentDelegate(delegate)
                        }
                        store.send(KBOTournamentAction.InitData)
                        _stack.update { it + SearchStackItem.KBOTournament(id, store) }
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
                _stack.update { it + SearchStackItem.FBPlayerStats(id, store) }
            }
            is FBPlayerInfoDelegate.ShowGameStats -> {
                val store = fbGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onFBGameStatsDelegate(id, delegate)
                }
                store.send(FBGameStatsAction.InitData)
                _stack.update { it + SearchStackItem.FBGameStats(id, store) }
            }
        }
    }

    private fun onFBPlayerStandingsDelegate(delegate: FBPlayerStandingsDelegate) {
        when (delegate) {
            is FBPlayerStandingsDelegate.ShowPlayerStats -> {
                val store = fbPlayerStatsFactory.create(delegate.model.displayModel)
                store.send(FBPlayerStatsAction.InitData)
                _stack.update { it + SearchStackItem.FBPlayerStats(ViewId(), store) }
            }
        }
    }

    private fun onFBTeamInfoDelegate(delegate: FBTeamInfoDelegate) {
        val id = ViewId()

        when (delegate) {
            is FBTeamInfoDelegate.ShowTeamStats -> {
                val store = fbTeamStatsFactory.create(delegate.model.displayModel)
                store.send(FBTeamStatsAction.InitData)
                _stack.update { it + SearchStackItem.FBTeamStats(id, store) }
            }
            is FBTeamInfoDelegate.ShowGameStats -> {
                val store = fbGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onFBGameStatsDelegate(id, delegate)
                }
                store.send(FBGameStatsAction.InitData)
                _stack.update { it + SearchStackItem.FBGameStats(id, store) }
            }
        }
    }

    private fun onFBTeamStandingsDelegate(delegate: FBTeamStandingsDelegate) {
        when (delegate) {
            is FBTeamStandingsDelegate.ShowTeamStats -> {
                val store = fbTeamStatsFactory.create((delegate.model as SportDecodableModel.FBTeamStats).displayModel)
                store.send(FBTeamStatsAction.InitData)
                _stack.update { it + SearchStackItem.FBTeamStats(ViewId(), store) }
            }
        }
    }

    private fun onFBLeagueScheduleDelegate(delegate: FBLeagueScheduleDelegate) {
        val id = ViewId()

        when (delegate) {
            is FBLeagueScheduleDelegate.ShowGameStats -> {
                _didPop.value = false
                // FBLeagueScheduleView에서 아이템 클릭으로 FBGameStatsView보여줄때 _includesPreviousView = true로 설정해 줘야 함.
                // TODO: ?.let {} 문법으로 변경
                val lastItem = stack.value.lastOrNull()
                if (lastItem != null) {
                    if (lastItem is SearchStackItem.FBLeagueSchedule) {
                        _includesPreviousView.value = true
                    }
                }

                val store = fbGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onFBGameStatsDelegate(id, delegate)
                }
                // NOTE: 원래는 store.send()를 먼저하고 _stack.update{}를 해주었는데, FBGameStatsAction.InitData에서 FBGameStatsDelegate.RefreshGame을 실행해주고 있어,
                // _stack.update{}가 되기 이전에 onFBGameStatsDelegate의 FBGameStatsDelegate.RefreshGame이 실행되어서 문제가 있어, store.send()가 먼저 실행되게 순서를 바꿔줌.
                // TODO: 그냥 이 순서대로 하는게 나을수도 있어 다른곳도 다 이렇게 바꾸는거 고려.
                _stack.update { it + SearchStackItem.FBGameStats(id, store) }
                store.send(FBGameStatsAction.InitData)
            }
        }
    }

    private fun onFBGameStatsDelegate(id: ViewId, delegate: FBGameStatsDelegate) {
        when (delegate) {
            is FBGameStatsDelegate.RefreshGame -> {
                // 현재 화면인 FBGameStats의 이전 화면인 FBLeagueSchedule을 찾아서 해당 Store에서 필요한 state를 업데이트 시킨다.
                val idx = stack.value.indexOfFirst { it.id == id }
                if (idx > 0) {
                    for (prev in stack.value.subList(0, idx).asReversed()) {
                        if (prev is SearchStackItem.FBLeagueSchedule) {
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
                _stack.update { it + SearchStackItem.NBAPlayerStats(id, store) }
            }
            is NBAPlayerInfoDelegate.ShowGameStats -> {
                val store = nbaGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onNBAGameStatsDelegate(id, delegate)
                }
                store.send(NBAGameStatsAction.InitData)
                _stack.update { it + SearchStackItem.NBAGameStats(id, store) }
            }
        }
    }

    private fun onNBAPlayerStandingsDelegate(delegate: NBAPlayerStandingsDelegate) {
        when (delegate) {
            is NBAPlayerStandingsDelegate.ShowPlayerStats -> {
                val store = nbaPlayerStatsFactory.create((delegate.model as SportDecodableModel.NBAPlayerStats).displayModel)
                store.send(NBAPlayerStatsAction.InitData)
                _stack.update { it + SearchStackItem.NBAPlayerStats(ViewId(), store) }
            }
        }
    }

    private fun onNBATeamInfoDelegate(delegate: NBATeamInfoDelegate) {
        val id = ViewId()

        when (delegate) {
            is NBATeamInfoDelegate.ShowTeamStats -> {
                val store = nbaTeamStatsFactory.create(delegate.model.displayModel)
                store.send(NBATeamStatsAction.InitData)
                _stack.update { it + SearchStackItem.NBATeamStats(id, store) }
            }
            is NBATeamInfoDelegate.ShowGameStats -> {
                val store = nbaGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onNBAGameStatsDelegate(id, delegate)
                }
                store.send(NBAGameStatsAction.InitData)
                _stack.update { it + SearchStackItem.NBAGameStats(id, store) }
            }
        }
    }

    private fun onNBATeamStandingsDelegate(delegate: NBATeamStandingsDelegate) {
        when (delegate) {
            is NBATeamStandingsDelegate.ShowTeamStats -> {
                val store = nbaTeamStatsFactory.create((delegate.model as SportDecodableModel.NBATeamStats).displayModel)
                store.send(NBATeamStatsAction.InitData)
                _stack.update { it + SearchStackItem.NBATeamStats(ViewId(), store) }
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
                _stack.update { it + SearchStackItem.NBAGameStats(id, store) }
            }
            is NBALeagueScheduleDelegate.ShowTournament -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = nbaTournamentFactory.create(delegate.model.displayModel) { delegate ->
                    onNBATournamentDelegate(delegate)
                }
                store.send(NBATournamentAction.InitData)
                _stack.update { it + SearchStackItem.NBATournament(id, store) }
            }
        }
    }

    private fun onNBAGameStatsDelegate(id: ViewId, delegate: NBAGameStatsDelegate) {
        // TODO: 파라미터 id와 새로생성한 ViewId() 구분해 사용해야함.
        when (delegate) {
            is NBAGameStatsDelegate.RefreshGame -> {
                val idx = stack.value.indexOfFirst { it.id == id }
                if (idx > 0) {
                    for (prev in stack.value.subList(0, idx).asReversed()) {
                        if (prev is SearchStackItem.NBALeagueSchedule) {
                            prev.store.send(NBALeagueScheduleAction.UpdateStateByRefreshGame(delegate.model))
                            break
                        }
                    }
                }
            }
        }
    }

    private fun onNBATournamentDelegate(delegate: NBATournamentDelegate) {
        val id = ViewId()

        when (delegate) {
            is NBATournamentDelegate.ShowLeagueSchedule -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = nbaLeagueScheduleFactory.create(delegate.model.displayModel) { delegate ->
                    onNBALeagueScheduleDelegate(delegate)
                }
                store.send(NBALeagueScheduleAction.InitData)
                _stack.update { it + SearchStackItem.NBALeagueSchedule(id, store) }
            }
        }
    }

    private fun onMLBPlayerInfoDelegate(delegate: MLBPlayerInfoDelegate) {
        val id = ViewId()

        when (delegate) {
            is MLBPlayerInfoDelegate.ShowPlayerStats -> {
                val store = mlbPlayerStatsFactory.create(delegate.model.displayModel)
                store.send(MLBPlayerStatsAction.InitData)
                _stack.update { it + SearchStackItem.MLBPlayerStats(id, store) }
            }
            is MLBPlayerInfoDelegate.ShowGameStats -> {
                val store = mlbGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onMLBGameStatsDelegate(id, delegate)
                }
                store.send(MLBGameStatsAction.InitData)
                _stack.update { it + SearchStackItem.MLBGameStats(id, store) }
            }
        }
    }

    private fun onMLBTeamInfoDelegate(delegate: MLBTeamInfoDelegate) {
        val id = ViewId()

        when (delegate) {
            is MLBTeamInfoDelegate.ShowTeamStats -> {
                val store = mlbTeamStatsFactory.create(delegate.model.displayModel)
                store.send(MLBTeamStatsAction.InitData)
                _stack.update { it + SearchStackItem.MLBTeamStats(id, store) }
            }
            is MLBTeamInfoDelegate.ShowGameStats -> {
                val store = mlbGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onMLBGameStatsDelegate(id, delegate)
                }
                store.send(MLBGameStatsAction.InitData)
                _stack.update { it + SearchStackItem.MLBGameStats(id, store) }
            }
        }
    }

    private fun onMLBTeamStandingsDelegate(delegate: MLBTeamStandingsDelegate) {
        when (delegate) {
            is MLBTeamStandingsDelegate.ShowTeamStats -> {
                val store = mlbTeamStatsFactory.create((delegate.model as SportDecodableModel.MLBTeamStats).displayModel)
                store.send(MLBTeamStatsAction.InitData)
                _stack.update { it + SearchStackItem.MLBTeamStats(ViewId(), store) }
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
                _stack.update { it + SearchStackItem.MLBGameStats(id, store) }
            }
            is MLBLeagueScheduleDelegate.ShowTournament -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = mlbTournamentFactory.create(delegate.model.displayModel) { delegate ->
                    onMLBTournamentDelegate(delegate)
                }
                store.send(MLBTournamentAction.InitData)
                _stack.update { it + SearchStackItem.MLBTournament(id, store) }
            }
        }
    }

    private fun onMLBGameStatsDelegate(id: ViewId, delegate: MLBGameStatsDelegate) {
        when (delegate) {
            is MLBGameStatsDelegate.RefreshGame -> {
                val idx = stack.value.indexOfFirst { it.id == id }
                if (idx > 0) {
                    for (prev in stack.value.subList(0, idx).asReversed()) {
                        if (prev is SearchStackItem.MLBLeagueSchedule) {
                            prev.store.send(MLBLeagueScheduleAction.UpdateStateByRefreshGame(delegate.model))
                            break
                        }
                    }
                }
            }
        }
    }

    private fun onMLBTournamentDelegate(delegate: MLBTournamentDelegate) {
        val id = ViewId()

        when (delegate) {
            is MLBTournamentDelegate.ShowLeagueSchedule -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = mlbLeagueScheduleFactory.create(delegate.model.displayModel) { delegate ->
                    onMLBLeagueScheduleDelegate(delegate)
                }
                store.send(MLBLeagueScheduleAction.InitData)
                _stack.update { it + SearchStackItem.MLBLeagueSchedule(id, store) }
            }
        }
    }

    private fun onKBOPlayerInfoDelegate(delegate: KBOPlayerInfoDelegate) {
        val id = ViewId()

        when (delegate) {
            is KBOPlayerInfoDelegate.ShowPlayerStats -> {
                val store = kboPlayerStatsFactory.create(delegate.model.displayModel)
                store.send(KBOPlayerStatsAction.InitData)
                _stack.update { it + SearchStackItem.KBOPlayerStats(id, store) }
            }
            is KBOPlayerInfoDelegate.ShowGameStats -> {
                val store = kboGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onKBOGameStatsDelegate(id, delegate)
                }
                store.send(KBOGameStatsAction.InitData)
                _stack.update { it + SearchStackItem.KBOGameStats(id, store) }
            }
        }
    }

    private fun onKBOTeamInfoDelegate(delegate: KBOTeamInfoDelegate) {
        val id = ViewId()

        when (delegate) {
            is KBOTeamInfoDelegate.ShowTeamStats -> {
                val store = kboTeamStatsFactory.create(delegate.model.displayModel)
                store.send(KBOTeamStatsAction.InitData)
                _stack.update { it + SearchStackItem.KBOTeamStats(id, store) }
            }
            is KBOTeamInfoDelegate.ShowGameStats -> {
                val store = kboGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onKBOGameStatsDelegate(id, delegate)
                }
                store.send(KBOGameStatsAction.InitData)
                _stack.update { it + SearchStackItem.KBOGameStats(id, store) }
            }
        }
    }

    private fun onKBOTeamStandingsDelegate(delegate: KBOTeamStandingsDelegate) {
        when (delegate) {
            is KBOTeamStandingsDelegate.ShowTeamStats -> {
                val store = kboTeamStatsFactory.create((delegate.model as SportDecodableModel.KBOTeamStats).displayModel)
                store.send(KBOTeamStatsAction.InitData)
                _stack.update { it + SearchStackItem.KBOTeamStats(ViewId(), store) }
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
                _stack.update { it + SearchStackItem.KBOGameStats(id, store) }
            }
            is KBOLeagueScheduleDelegate.ShowTournament -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = kboTournamentFactory.create(delegate.model.displayModel) { delegate ->
                    onKBOTournamentDelegate(delegate)
                }
                store.send(KBOTournamentAction.InitData)
                _stack.update { it + SearchStackItem.KBOTournament(id, store) }
            }
        }
    }

    private fun onKBOGameStatsDelegate(id: ViewId, delegate: KBOGameStatsDelegate) {
        when (delegate) {
            is KBOGameStatsDelegate.RefreshGame -> {
                val idx = stack.value.indexOfFirst { it.id == id }
                if (idx > 0) {
                    for (prev in stack.value.subList(0, idx).asReversed()) {
                        if (prev is SearchStackItem.KBOLeagueSchedule) {
                            prev.store.send(KBOLeagueScheduleAction.UpdateStateByRefreshGame(delegate.model))
                            break
                        }
                    }
                }
            }
        }
    }

    private fun onKBOTournamentDelegate(delegate: KBOTournamentDelegate) {
        val id = ViewId()

        when (delegate) {
            is KBOTournamentDelegate.ShowLeagueSchedule -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = kboLeagueScheduleFactory.create(delegate.model.displayModel) { delegate ->
                    onKBOLeagueScheduleDelegate(delegate)
                }
                store.send(KBOLeagueScheduleAction.InitData)
                _stack.update { it + SearchStackItem.KBOLeagueSchedule(id, store) }
            }
        }
    }

    private fun dispose(item: SearchStackItem) {
        when (item) {
            is SearchStackItem.FBPlayerInfo -> item.store.dispose()
            is SearchStackItem.FBPlayerStats -> item.store.dispose()
            is SearchStackItem.FBPlayerStandings -> item.store.dispose()
            is SearchStackItem.FBTeamInfo -> item.store.dispose()
            is SearchStackItem.FBTeamStats -> item.store.dispose()
            is SearchStackItem.FBTeamStandings -> item.store.dispose()
            is SearchStackItem.FBLeagueSchedule -> item.store.dispose()
            is SearchStackItem.FBGameStats -> item.store.dispose()
            is SearchStackItem.FBTournament -> item.store.dispose()

            is SearchStackItem.NBAPlayerInfo -> item.store.dispose()
            is SearchStackItem.NBAPlayerStats -> item.store.dispose()
            is SearchStackItem.NBAPlayerStandings -> item.store.dispose()
            is SearchStackItem.NBATeamInfo -> item.store.dispose()
            is SearchStackItem.NBATeamStats -> item.store.dispose()
            is SearchStackItem.NBATeamStandings -> item.store.dispose()
            is SearchStackItem.NBALeagueSchedule -> item.store.dispose()
            is SearchStackItem.NBAGameStats -> item.store.dispose()
            is SearchStackItem.NBATournament -> item.store.dispose()

            is SearchStackItem.MLBPlayerInfo -> item.store.dispose()
            is SearchStackItem.MLBPlayerStats -> item.store.dispose()
            is SearchStackItem.MLBTeamInfo -> item.store.dispose()
            is SearchStackItem.MLBTeamStats -> item.store.dispose()
            is SearchStackItem.MLBTeamStandings -> item.store.dispose()
            is SearchStackItem.MLBLeagueSchedule -> item.store.dispose()
            is SearchStackItem.MLBGameStats -> item.store.dispose()
            is SearchStackItem.MLBTournament -> item.store.dispose()

            is SearchStackItem.KBOPlayerInfo -> item.store.dispose()
            is SearchStackItem.KBOPlayerStats -> item.store.dispose()
            is SearchStackItem.KBOTeamInfo -> item.store.dispose()
            is SearchStackItem.KBOTeamStats -> item.store.dispose()
            is SearchStackItem.KBOTeamStandings -> item.store.dispose()
            is SearchStackItem.KBOLeagueSchedule -> item.store.dispose()
            is SearchStackItem.KBOGameStats -> item.store.dispose()
            is SearchStackItem.KBOTournament -> item.store.dispose()
        }
    }
}