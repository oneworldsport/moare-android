package com.moare.android.features.search.display.football.viewmodel

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.percentageOf
import com.moare.android.features.search.display.common.store.BaseGameStatsStore
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.models.football.FBGameLineups
import com.moare.android.features.search.models.models.football.FBGamePlayerStats
import com.moare.android.features.search.models.models.football.FBGamePlayerStatsDetail
import com.moare.android.features.search.models.models.football.FBGamePlayerStatsGames
import com.moare.android.features.search.models.models.football.FBPerson
import com.moare.android.features.search.models.models.football.FBPlayerStatsCards
import com.moare.android.features.search.models.models.football.FBPlayerStatsDribbles
import com.moare.android.features.search.models.models.football.FBPlayerStatsDuels
import com.moare.android.features.search.models.models.football.FBPlayerStatsFouls
import com.moare.android.features.search.models.models.football.FBPlayerStatsGoals
import com.moare.android.features.search.models.models.football.FBPlayerStatsPasses
import com.moare.android.features.search.models.models.football.FBPlayerStatsPenalty
import com.moare.android.features.search.models.models.football.FBPlayerStatsShots
import com.moare.android.features.search.models.models.football.FBPlayerStatsTackles
import com.moare.android.features.search.models.responsemodels.football.FBGameStatsResponseModel
import com.moare.android.features.search.networking.SearchClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface FBGameStatsAction {
    data object InitData : FBGameStatsAction
    data class SelectSecondCategory(val index: Int) : FBGameStatsAction
    data class SelectTeam(val index: Int) : FBGameStatsAction
    data class RefreshGame(val shouldFetch: Boolean = true) : FBGameStatsAction // NOTE: shouldFetch는 최초에 FBGameStats에 진입했을때 받은 데이터로 FBLeagueSchedule데이터 업데이트 해줄때 사용.
}

sealed interface FBGameStatsDelegate {
    data class RefreshGame(val model: SportDecodableModel.FBGameStats) : FBGameStatsDelegate
}

class FBGameStatsStore @AssistedInject constructor(
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: FBGameStatsDisplayModel,
    @Assisted val emitToParent: (FBGameStatsDelegate) -> Unit
) : BaseGameStatsStore<FBGameStatsAction, FBGameStatsDisplayModel>(model, nameProvider) {
    val dataItemHeight = 40.dp
    val categoryItemHeight = 34.dp
    val firstItemWidth = 120.dp
    val teamButtonWidth = 100.dp
    val itemWidth = 70.dp
    val barWidth = 2.dp
    val categoryFontSize = 14.sp
    val dataFontSize = 14.sp

    private val _playersStats = MutableStateFlow<List<FBGamePlayerStats>>(emptyList())
    val playerStats: StateFlow<List<FBGamePlayerStats>> = _playersStats

    private val _playersTotalStats = MutableStateFlow<FBGamePlayerStatsDetail?>(null)
    val playersTotalStats: StateFlow<FBGamePlayerStatsDetail?> = _playersTotalStats

    private val _lineups = MutableStateFlow<FBGameLineups?>(null)
    val lineups: StateFlow<FBGameLineups?> = _lineups

    private val _coach = MutableStateFlow<FBPerson?>(null)
    val coach: StateFlow<FBPerson?> = _coach

    @AssistedFactory
    interface Factory {
        fun create(
            model: FBGameStatsDisplayModel,
            emitToParent: (FBGameStatsDelegate) -> Unit
        ) : FBGameStatsStore
    }

    override fun send(action: FBGameStatsAction) {
        when (action) {
            is FBGameStatsAction.InitData -> initData()
            is FBGameStatsAction.SelectSecondCategory -> selectSecondCategory(action.index)
            is FBGameStatsAction.SelectTeam -> selectTeam(false, action.index)
            is FBGameStatsAction.RefreshGame -> refreshGame(action.shouldFetch)
        }
    }

    override fun initData() {
        super.initData()

        // init with default value
        _playersStats.value = emptyList()
        _playersTotalStats.value = null
        _lineups.value = null
        _coach.value = null

        selectTeam(false, 0)
    }

    override fun selectSecondCategory(index: Int) {
        super.selectSecondCategory(index)

        sortPlayers()
    }

    override fun selectTeam(isInit: Boolean, index: Int) {
        super.selectTeam(isInit, index)

        // set selected team's players stats
        val teamId = when (index) {
            0 -> displayModel.value.game.teams.home.id
            1 -> displayModel.value.game.teams.away.id
            else -> null
        }

        val playersStats = displayModel.value.game.players.find { teamId != null && it.team.id == teamId }?.players
        _playersStats.value = playersStats ?: emptyList()
        setPlayersTotalStats()

        // set selected team's coach, lineups
        val lineups = displayModel.value.game.lineups.find { teamId != null && it.team.id == teamId }
        _lineups.value = lineups
        _coach.value = lineups?.coach

        sortPlayers()
        refreshGame(false) // NOTE: 이걸 안해주면 새로고침 누르기 전에는 FBLeagueSchedule 데이터가 업데이트 안됨.
    }

    override fun sortPlayers() {
        val playerStats = playerStats.value.toMutableList()

        when (secondCategorySelectedIndex.value) {
            0 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.goals?.total ?: 0 }
            1 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.penalty?.scored ?: 0 }
            2 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.goals?.assists ?: 0 }
            3 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.shots?.total ?: 0 }
            4 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.shots?.on ?: 0 }
            5 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.passes?.key ?: 0 }
            6 -> if (playerStats.all { it.statistics.firstOrNull() != null }) {
                playerStats.sortByDescending {
                    val stats = it.statistics.firstOrNull()!!
                    stats.dribbles.success.percentageOf(stats.dribbles.attempts, 1)
                }
            }
            7 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.offsides ?: 0 }
            8 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.tackles?.total ?: 0 }
            9 -> if (playerStats.all { it.statistics.firstOrNull() != null }) {
                playerStats.sortByDescending {
                    val stats = it.statistics.firstOrNull()!!
                    stats.duels.won.percentageOf(stats.duels.total, 1)
                }
            }
            10 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.tackles?.interceptions ?: 0 }
            11 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.passes?.total ?: 0 }
            12 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.fouls?.drawn ?: 0 }
            13 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.fouls?.committed ?: 0 }
            14 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.cards?.yellow ?: 0 }
            15 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.cards?.red ?: 0 }
            16 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.games?.minutes ?: 0 }
            17 -> {}
        }

        _playersStats.value = playerStats

        setPlayersTotalStats()
    }

    override fun setPlayersTotalStats() {
//        playerStats.flatMap { it.statistics } -> 이게 정상적으로 동작했던 이유는 it.statistics의 item이 모두 하나였기 때문에. 하나 이상이었으면 값이 다르게 나왔을것.
        val playersTotalStats = playerStats.value.mapNotNull { it.statistics.firstOrNull() }
            .fold(
                FBGamePlayerStatsDetail(
                    games = FBGamePlayerStatsGames(),
                    shots = FBPlayerStatsShots(),
                    goals = FBPlayerStatsGoals(),
                    passes = FBPlayerStatsPasses(),
                    tackles = FBPlayerStatsTackles(),
                    duels = FBPlayerStatsDuels(),
                    dribbles = FBPlayerStatsDribbles(),
                    fouls = FBPlayerStatsFouls(),
                    cards = FBPlayerStatsCards(),
                    penalty = FBPlayerStatsPenalty()
                )
            ) { acc, stats ->
                FBGamePlayerStatsDetail(
                    games = FBGamePlayerStatsGames(),
                    shots = FBPlayerStatsShots(
                        _total = acc.shots.total + stats.shots.total,
                        _on = acc.shots.on + stats.shots.on
                    ),
                    goals = FBPlayerStatsGoals(
                        _total = acc.goals.total + stats.goals.total,
                        _assists = acc.goals.assists + stats.goals.assists
                    ),
                    passes = FBPlayerStatsPasses(
                        _total = acc.passes.total + stats.passes.total,
                        _key = acc.passes.key + stats.passes.key,
                        _accuracy = null
                    ),
                    tackles = FBPlayerStatsTackles(
                        _total = acc.tackles.total + stats.tackles.total,
                        _blocks = null,
                        _interceptions = acc.tackles.interceptions + stats.tackles.interceptions
                    ),
                    duels = FBPlayerStatsDuels(
                        _total = acc.duels.total + stats.duels.total,
                        _won = acc.duels.won + stats.duels.won
                    ),
                    dribbles = FBPlayerStatsDribbles(
                        _attempts = acc.dribbles.attempts + stats.dribbles.attempts,
                        _success = acc.dribbles.success + stats.dribbles.success,
                        _past = null
                    ),
                    fouls = FBPlayerStatsFouls(
                        _drawn = acc.fouls.drawn + stats.fouls.drawn,
                        _committed = acc.fouls.committed + stats.fouls.committed
                    ),
                    cards = FBPlayerStatsCards(
                        _yellow = acc.cards.yellow + stats.cards.yellow,
                        _yellowred = null,
                        _red = acc.cards.red + stats.cards.red
                    ),
                    penalty = FBPlayerStatsPenalty(
                        _won = null,
                        _commited = null,
                        _scored = acc.penalty.scored + stats.penalty.scored,
                        _missed = null,
                        _saved = null
                    ),
                    _offsides = acc.offsides + stats.offsides
                )
            }

        _playersTotalStats.value = playersTotalStats
    }

    private fun refreshGame(shouldFetch: Boolean) {
        if (shouldFetch) {
            scope.launch {
                val game = displayModel.value.game

                // TODO: Has to add loading
                val result = searchClient.fetchById(
                    season = displayModel.value.season,
                    category = "football",
                    date = game.fixture.date,
                    dataType = "football_game_stats",
                    leagueId = game.league.id,
                    id = game.fixture.id.toString()
                )

                if (result.data is SportDecodableModel.FBGameStats) {
                    _displayModel.value = result.data.displayModel
                    initData()
                    emitToParent(FBGameStatsDelegate.RefreshGame(result.data))
                }
            }
        } else {
            val responseModel = FBGameStatsResponseModel(game = displayModel.value.game)
            val dataModel = SportDecodableModel.FBGameStats(responseModel, displayModel.value)

            emitToParent(FBGameStatsDelegate.RefreshGame(dataModel))
        }
    }
}

















