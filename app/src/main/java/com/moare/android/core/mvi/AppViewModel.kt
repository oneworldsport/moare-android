package com.moare.android.core.mvi

import android.app.Activity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
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
import com.moare.android.features.search.display.football.store.FBTournamentDelegate
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
import com.moare.android.features.search.display.tennis.store.TennisGameStatsAction
import com.moare.android.features.search.display.tennis.store.TennisGameStatsDelegate
import com.moare.android.features.search.display.tennis.store.TennisGameStatsStore
import com.moare.android.features.search.display.tennis.store.TennisLeagueScheduleAction
import com.moare.android.features.search.display.tennis.store.TennisLeagueScheduleDelegate
import com.moare.android.features.search.display.tennis.store.TennisLeagueScheduleStore
import com.moare.android.features.search.display.tennis.store.TennisTournamentAction
import com.moare.android.features.search.display.tennis.store.TennisTournamentDelegate
import com.moare.android.features.search.display.tennis.store.TennisTournamentStore
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
    data class FBTournament(override val id: ViewId, val store: FBTournamentStore) : StackItem

    data class NBAPlayerInfo(override val id: ViewId, val store: NBAPlayerInfoStore) : StackItem
    data class NBAPlayerStats(override val id: ViewId, val store: NBAPlayerStatsStore) : StackItem
    data class NBAPlayerStandings(override val id: ViewId, val store: NBAPlayerStandingsStore) : StackItem
    data class NBATeamInfo(override val id: ViewId, val store: NBATeamInfoStore) : StackItem
    data class NBATeamStats(override val id: ViewId, val store: NBATeamStatsStore) : StackItem
    data class NBATeamStandings(override val id: ViewId, val store: NBATeamStandingsStore) : StackItem
    data class NBALeagueSchedule(override val id: ViewId, val store: NBALeagueScheduleStore) : StackItem
    data class NBAGameStats(override val id: ViewId, val store: NBAGameStatsStore) : StackItem
    data class NBATournament(override val id: ViewId, val store: NBATournamentStore) : StackItem

    data class MLBPlayerInfo(override val id: ViewId, val store: MLBPlayerInfoStore) : StackItem
    data class MLBPlayerStats(override val id: ViewId, val store: MLBPlayerStatsStore) : StackItem
    data class MLBTeamInfo(override val id: ViewId, val store: MLBTeamInfoStore) : StackItem
    data class MLBTeamStats(override val id: ViewId, val store: MLBTeamStatsStore) : StackItem
    data class MLBTeamStandings(override val id: ViewId, val store: MLBTeamStandingsStore) : StackItem
    data class MLBLeagueSchedule(override val id: ViewId, val store: MLBLeagueScheduleStore) : StackItem
    data class MLBGameStats(override val id: ViewId, val store: MLBGameStatsStore) : StackItem
    data class MLBTournament(override val id: ViewId, val store: MLBTournamentStore) : StackItem

    data class KBOPlayerInfo(override val id: ViewId, val store: KBOPlayerInfoStore) : StackItem
    data class KBOPlayerStats(override val id: ViewId, val store: KBOPlayerStatsStore) : StackItem
    data class KBOTeamInfo(override val id: ViewId, val store: KBOTeamInfoStore) : StackItem
    data class KBOTeamStats(override val id: ViewId, val store: KBOTeamStatsStore) : StackItem
    data class KBOTeamStandings(override val id: ViewId, val store: KBOTeamStandingsStore) : StackItem
    data class KBOLeagueSchedule(override val id: ViewId, val store: KBOLeagueScheduleStore) : StackItem
    data class KBOGameStats(override val id: ViewId, val store: KBOGameStatsStore) : StackItem
    data class KBOTournament(override val id: ViewId, val store: KBOTournamentStore) : StackItem

    data class TennisLeagueSchedule(override val id: ViewId, val store: TennisLeagueScheduleStore) : StackItem
    data class TennisGameStats(override val id: ViewId, val store: TennisGameStatsStore) : StackItem
    data class TennisTournament(override val id: ViewId, val store: TennisTournamentStore) : StackItem
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

    private val tennisLeagueScheduleFactory: TennisLeagueScheduleStore.Factory,
    private val tennisGameStatsFactory: TennisGameStatsStore.Factory,
    private val tennisTournamentFactory: TennisTournamentStore.Factory
) : ViewModel() {
    private val _stack = MutableStateFlow<List<StackItem>>(emptyList())
    val stack: StateFlow<List<StackItem>> = _stack

    private val _queryList = MutableStateFlow<List<String>>(emptyList())
    val queryList: StateFlow<List<String>> = _queryList

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
                if (searchStore.query.value.text.isEmpty()) {
                    if (searchStore.focusState.value) {
                        searchStore.send(SearchAction.ToggleFocusState(false))
                    } else {
                        // close app
                        activity?.finishAffinity()
                    }
                } else {
                    searchStore.send(SearchAction.UpdateTextField(TextFieldValue("")))
                }
            } else {
                // If searchBar is Opened and there are stack, don't pop and show the previous view.
                searchStore.send(SearchAction.ToggleSearchBar)
            }
        } else {
            _didPop.value = true
            // NOTE: FBGameStats로 뒤로갔을때(FBLeagueSchedule -> FBGameStats인 경우) includesPreviousView가 true여야 하지만 false여도
            // 그냥 FBGameStats 화면이 잘 나오기 때문에 상관없음
            _includesPreviousView.value = false

            val lastItem = stack.value.lastOrNull()

            _stack.update { current ->
                current.dropLast(1)
            }

            val poppedQuery = queryList.value.lastOrNull()
            _queryList.update { current ->
                current.dropLast(1)
            }

            val lastQuery = queryList.value.lastOrNull() ?: poppedQuery ?: ""

            lastItem?.let {
                dispose(lastItem)
            }

            searchStore.send(SearchAction.PopView(stack.value.isEmpty(), lastQuery))
        }
    }

    fun push() {

    }

    private fun onSearchDelegate(delegate: SearchDelegate) {
        when (delegate) {
            is SearchDelegate.Push -> {
                val id = ViewId()

                when (val model = delegate.model) {
                    // football
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
                    is SportDecodableModel.FBTournament -> {
                        val store = fbTournamentFactory.create(model.displayModel) { delegate ->
                            onFBTournamentDelegate(delegate)
                        }
                        store.send(FBTournamentAction.InitData)
                        _stack.update { it + StackItem.FBTournament(id, store) }
                    }

                    // nba
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
                    is SportDecodableModel.NBATournament -> {
                        val store = nbaTournamentFactory.create(model.displayModel) { delegate ->
                            onNBATournamentDelegate(delegate)
                        }
                        store.send(NBATournamentAction.InitData)
                        _stack.update { it + StackItem.NBATournament(id, store) }
                    }

                    // mlb
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
                    is SportDecodableModel.MLBTournament -> {
                        val store = mlbTournamentFactory.create(model.displayModel) { delegate ->
                            onMLBTournamentDelegate(delegate)
                        }
                        store.send(MLBTournamentAction.InitData)
                        _stack.update { it + StackItem.MLBTournament(id, store) }
                    }

                    // kbo
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
                    is SportDecodableModel.KBOTournament -> {
                        val store = kboTournamentFactory.create(model.displayModel) { delegate ->
                            onKBOTournamentDelegate(delegate)
                        }
                        store.send(KBOTournamentAction.InitData)
                        _stack.update { it + StackItem.KBOTournament(id, store) }
                    }

                    // tennis
                    is SportDecodableModel.TennisLeagueSchedule -> {
                        val store = tennisLeagueScheduleFactory.create(model.displayModel) { delegate ->
                            onTennisLeagueScheduleDelegate(delegate)
                        }
                        store.send(TennisLeagueScheduleAction.InitData)
                        _stack.update { it + StackItem.TennisLeagueSchedule(id, store) }
                    }
                    is SportDecodableModel.TennisGameStats -> {
                        val store = tennisGameStatsFactory.create(model.displayModel) { delegate ->
                            onTennisGameStatsDelegate(id, delegate)
                        }
                        store.send(TennisGameStatsAction.InitData)
                        _stack.update { it + StackItem.TennisGameStats(id, store) }
                    }
                    is SportDecodableModel.TennisTournament -> {
                        val store = tennisTournamentFactory.create(model.displayModel) { delegate ->
                            onTennisTournamentDelegate(delegate)
                        }
                        store.send(TennisTournamentAction.InitData)
                        _stack.update { it + StackItem.TennisTournament(id, store) }
                    }

                    else -> {}
                }

                _queryList.update { it + searchStore.query.value.text }
            }
        }
    }

    // football
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

        _queryList.update { it + searchStore.query.value.text }
    }

    private fun onFBPlayerStandingsDelegate(delegate: FBPlayerStandingsDelegate) {
        when (delegate) {
            is FBPlayerStandingsDelegate.ShowPlayerStats -> {
                val store = fbPlayerStatsFactory.create(delegate.model.displayModel)
                store.send(FBPlayerStatsAction.InitData)
                _stack.update { it + StackItem.FBPlayerStats(ViewId(), store) }
            }
        }

        _queryList.update { it + searchStore.query.value.text }
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

        _queryList.update { it + searchStore.query.value.text }
    }

    private fun onFBTeamStandingsDelegate(delegate: FBTeamStandingsDelegate) {
        when (delegate) {
            is FBTeamStandingsDelegate.ShowTeamStats -> {
                val store = fbTeamStatsFactory.create((delegate.model as SportDecodableModel.FBTeamStats).displayModel)
                store.send(FBTeamStatsAction.InitData)
                _stack.update { it + StackItem.FBTeamStats(ViewId(), store) }
            }
        }

        _queryList.update { it + searchStore.query.value.text }
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
                // NOTE: 원래는 store.send()를 먼저하고 _stack.update{}를 해주었는데, FBGameStatsAction.InitData에서 FBGameStatsDelegate.RefreshGame을 실행해주고 있어,
                // _stack.update{}가 되기 이전에 onFBGameStatsDelegate의 FBGameStatsDelegate.RefreshGame이 실행되어서 문제가 있어, store.send()가 먼저 실행되게 순서를 바꿔줌.
                // TODO: 그냥 이 순서대로 하는게 나을수도 있어 다른곳도 다 이렇게 바꾸는거 고려.
                _stack.update { it + StackItem.FBGameStats(id, store) }
                store.send(FBGameStatsAction.InitData)
            }
            is FBLeagueScheduleDelegate.ShowTournament -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = fbTournamentFactory.create(delegate.model.displayModel) { delegate ->
                    onFBTournamentDelegate(delegate)
                }
                store.send(FBTournamentAction.InitData)
                _stack.update { it + StackItem.FBTournament(id, store) }
            }
            is FBLeagueScheduleDelegate.ShowTeamStandings -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = fbTeamStandingsFactory.create(delegate.model) { delegate ->
                    onFBTeamStandingsDelegate(delegate)
                }
                store.send(FBTeamStandingsAction.InitData)
                _stack.update { it + StackItem.FBTeamStandings(id, store) }
            }
        }

        _queryList.update { it + searchStore.query.value.text }
    }

    private fun onFBGameStatsDelegate(id: ViewId, delegate: FBGameStatsDelegate) {
        when (delegate) {
            is FBGameStatsDelegate.RefreshGame -> {
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

    private fun onFBTournamentDelegate(delegate: FBTournamentDelegate) {
        val id = ViewId()

        when (delegate) {
            is FBTournamentDelegate.ShowLeagueSchedule -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = fbLeagueScheduleFactory.create(delegate.model.displayModel) { delegate ->
                    onFBLeagueScheduleDelegate(delegate)
                }
                store.send(FBLeagueScheduleAction.InitData)
                _stack.update { it + StackItem.FBLeagueSchedule(id, store) }
            }
            is FBTournamentDelegate.ShowGameStats -> {
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
                _stack.update { it + StackItem.FBGameStats(id, store) }
                store.send(FBGameStatsAction.InitData)
            }
        }

        _queryList.update { it + searchStore.query.value.text }
    }

    // nba
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

        _queryList.update { it + searchStore.query.value.text }
    }

    private fun onNBAPlayerStandingsDelegate(delegate: NBAPlayerStandingsDelegate) {
        when (delegate) {
            is NBAPlayerStandingsDelegate.ShowPlayerStats -> {
                val store = nbaPlayerStatsFactory.create((delegate.model as SportDecodableModel.NBAPlayerStats).displayModel)
                store.send(NBAPlayerStatsAction.InitData)
                _stack.update { it + StackItem.NBAPlayerStats(ViewId(), store) }
            }
        }

        _queryList.update { it + searchStore.query.value.text }
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

        _queryList.update { it + searchStore.query.value.text }
    }

    private fun onNBATeamStandingsDelegate(delegate: NBATeamStandingsDelegate) {
        when (delegate) {
            is NBATeamStandingsDelegate.ShowTeamStats -> {
                val store = nbaTeamStatsFactory.create((delegate.model as SportDecodableModel.NBATeamStats).displayModel)
                store.send(NBATeamStatsAction.InitData)
                _stack.update { it + StackItem.NBATeamStats(ViewId(), store) }
            }
        }

        _queryList.update { it + searchStore.query.value.text }
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
            is NBALeagueScheduleDelegate.ShowTournament -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = nbaTournamentFactory.create(delegate.model.displayModel) { delegate ->
                    onNBATournamentDelegate(delegate)
                }
                store.send(NBATournamentAction.InitData)
                _stack.update { it + StackItem.NBATournament(id, store) }
            }
            is NBALeagueScheduleDelegate.ShowTeamStandings -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = nbaTeamStandingsFactory.create(delegate.model) { delegate ->
                    onNBATeamStandingsDelegate(delegate)
                }
                store.send(NBATeamStandingsAction.InitData)
                _stack.update { it + StackItem.NBATeamStandings(id, store) }
            }
        }

        _queryList.update { it + searchStore.query.value.text }
    }

    private fun onNBAGameStatsDelegate(id: ViewId, delegate: NBAGameStatsDelegate) {
        // TODO: 파라미터 id와 새로생성한 ViewId() 구분해 사용해야함.
        when (delegate) {
            is NBAGameStatsDelegate.RefreshGame -> {
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
                _stack.update { it + StackItem.NBALeagueSchedule(id, store) }
            }
        }

        _queryList.update { it + searchStore.query.value.text }
    }

    // mlb
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

        _queryList.update { it + searchStore.query.value.text }
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

        _queryList.update { it + searchStore.query.value.text }
    }

    private fun onMLBTeamStandingsDelegate(delegate: MLBTeamStandingsDelegate) {
        when (delegate) {
            is MLBTeamStandingsDelegate.ShowTeamStats -> {
                val store = mlbTeamStatsFactory.create((delegate.model as SportDecodableModel.MLBTeamStats).displayModel)
                store.send(MLBTeamStatsAction.InitData)
                _stack.update { it + StackItem.MLBTeamStats(ViewId(), store) }
            }
        }

        _queryList.update { it + searchStore.query.value.text }
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
            is MLBLeagueScheduleDelegate.ShowTournament -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = mlbTournamentFactory.create(delegate.model.displayModel) { delegate ->
                    onMLBTournamentDelegate(delegate)
                }
                store.send(MLBTournamentAction.InitData)
                _stack.update { it + StackItem.MLBTournament(id, store) }
            }
            is MLBLeagueScheduleDelegate.ShowTeamStandings -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = mlbTeamStandingsFactory.create(delegate.model) { delegate ->
                    onMLBTeamStandingsDelegate(delegate)
                }
                store.send(MLBTeamStandingsAction.InitData)
                _stack.update { it + StackItem.MLBTeamStandings(id, store) }
            }
        }

        _queryList.update { it + searchStore.query.value.text }
    }

    private fun onMLBGameStatsDelegate(id: ViewId, delegate: MLBGameStatsDelegate) {
        when (delegate) {
            is MLBGameStatsDelegate.RefreshGame -> {
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
                _stack.update { it + StackItem.MLBLeagueSchedule(id, store) }
            }
        }

        _queryList.update { it + searchStore.query.value.text }
    }

    // kbo
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

        _queryList.update { it + searchStore.query.value.text }
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

        _queryList.update { it + searchStore.query.value.text }
    }

    private fun onKBOTeamStandingsDelegate(delegate: KBOTeamStandingsDelegate) {
        when (delegate) {
            is KBOTeamStandingsDelegate.ShowTeamStats -> {
                val store = kboTeamStatsFactory.create((delegate.model as SportDecodableModel.KBOTeamStats).displayModel)
                store.send(KBOTeamStatsAction.InitData)
                _stack.update { it + StackItem.KBOTeamStats(ViewId(), store) }
            }
        }

        _queryList.update { it + searchStore.query.value.text }
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
            is KBOLeagueScheduleDelegate.ShowTournament -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = kboTournamentFactory.create(delegate.model.displayModel) { delegate ->
                    onKBOTournamentDelegate(delegate)
                }
                store.send(KBOTournamentAction.InitData)
                _stack.update { it + StackItem.KBOTournament(id, store) }
            }
            is KBOLeagueScheduleDelegate.ShowTeamStandings -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = kboTeamStandingsFactory.create(delegate.model) { delegate ->
                    onKBOTeamStandingsDelegate(delegate)
                }
                store.send(KBOTeamStandingsAction.InitData)
                _stack.update { it + StackItem.KBOTeamStandings(id, store) }
            }
        }

        _queryList.update { it + searchStore.query.value.text }
    }

    private fun onKBOGameStatsDelegate(id: ViewId, delegate: KBOGameStatsDelegate) {
        when (delegate) {
            is KBOGameStatsDelegate.RefreshGame -> {
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
                _stack.update { it + StackItem.KBOLeagueSchedule(id, store) }
            }
        }

        _queryList.update { it + searchStore.query.value.text }
    }

    // tennis
    private fun onTennisLeagueScheduleDelegate(delegate: TennisLeagueScheduleDelegate) {
        val id = ViewId()

        when (delegate) {
            is TennisLeagueScheduleDelegate.ShowGameStats -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = tennisGameStatsFactory.create(delegate.model.displayModel) { delegate ->
                    onTennisGameStatsDelegate(id, delegate)
                }
                store.send(TennisGameStatsAction.InitData)
                _stack.update { it + StackItem.TennisGameStats(id, store) }
            }
            is TennisLeagueScheduleDelegate.ShowTournament -> {
                _didPop.value = false
                _includesPreviousView.value = false

                val store = tennisTournamentFactory.create(delegate.model.displayModel) { delegate ->
                    onTennisTournamentDelegate(delegate)
                }
                store.send(TennisTournamentAction.InitData)
                _stack.update { it + StackItem.TennisTournament(id, store) }
            }
        }

        _queryList.update { it + searchStore.query.value.text }
    }

    private fun onTennisGameStatsDelegate(id: ViewId, delegate: TennisGameStatsDelegate) {
    }

    private fun onTennisTournamentDelegate(delegate: TennisTournamentDelegate) {
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
            is StackItem.FBTournament -> item.store.dispose()

            is StackItem.NBAPlayerInfo -> item.store.dispose()
            is StackItem.NBAPlayerStats -> item.store.dispose()
            is StackItem.NBAPlayerStandings -> item.store.dispose()
            is StackItem.NBATeamInfo -> item.store.dispose()
            is StackItem.NBATeamStats -> item.store.dispose()
            is StackItem.NBATeamStandings -> item.store.dispose()
            is StackItem.NBALeagueSchedule -> item.store.dispose()
            is StackItem.NBAGameStats -> item.store.dispose()
            is StackItem.NBATournament -> item.store.dispose()

            is StackItem.MLBPlayerInfo -> item.store.dispose()
            is StackItem.MLBPlayerStats -> item.store.dispose()
            is StackItem.MLBTeamInfo -> item.store.dispose()
            is StackItem.MLBTeamStats -> item.store.dispose()
            is StackItem.MLBTeamStandings -> item.store.dispose()
            is StackItem.MLBLeagueSchedule -> item.store.dispose()
            is StackItem.MLBGameStats -> item.store.dispose()
            is StackItem.MLBTournament -> item.store.dispose()

            is StackItem.KBOPlayerInfo -> item.store.dispose()
            is StackItem.KBOPlayerStats -> item.store.dispose()
            is StackItem.KBOTeamInfo -> item.store.dispose()
            is StackItem.KBOTeamStats -> item.store.dispose()
            is StackItem.KBOTeamStandings -> item.store.dispose()
            is StackItem.KBOLeagueSchedule -> item.store.dispose()
            is StackItem.KBOGameStats -> item.store.dispose()
            is StackItem.KBOTournament -> item.store.dispose()

            is StackItem.TennisLeagueSchedule -> item.store.dispose()
            is StackItem.TennisGameStats -> item.store.dispose()
            is StackItem.TennisTournament -> item.store.dispose()
        }
    }
}