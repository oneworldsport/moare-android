package com.moare.android.features.search.display.football.viewmodel

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.core.util.percentageOf
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FBGameStatsViewModel @Inject constructor(
) : MVIViewModel<FBGameStatsViewModel.Intent, FBGameStatsDisplayModel>() {
    /* ---------------------
       constants
       --------------------- */
    val dataItemHeight = 40.dp
    val categoryItemHeight = 34.dp
    val firstItemWidth = 120.dp
    val teamButtonWidth = 100.dp
    val itemWidth = 70.dp
    val barWidth = 2.dp
    val categoryFontSize = 14.sp
    val dataFontSize = 14.sp

    /* ---------------------
       variables
       --------------------- */

    /* ---------------------
       data state
       --------------------- */
    private val _displayModel = MutableStateFlow<FBGameStatsDisplayModel?>(null)
    val displayModel: StateFlow<FBGameStatsDisplayModel?> = _displayModel

    private val _playersStats = MutableStateFlow<List<FBGamePlayerStats>>(emptyList())
    val playerStats: StateFlow<List<FBGamePlayerStats>> = _playersStats

    private val _playersTotalStats = MutableStateFlow<FBGamePlayerStatsDetail?>(null)
    val playersTotalStats: StateFlow<FBGamePlayerStatsDetail?> = _playersTotalStats

    private val _lineups = MutableStateFlow<FBGameLineups?>(null)
    val lineups: StateFlow<FBGameLineups?> = _lineups

    private val _coach = MutableStateFlow<FBPerson?>(null)
    val coach: StateFlow<FBPerson?> = _coach

    /* ---------------------
       ui state
       --------------------- */
    private var _firstSelectedIndex = MutableStateFlow(0)
    val firstSelectedIndex: StateFlow<Int> = _firstSelectedIndex

    private var _secondSelectedIndex = MutableStateFlow(0)
    val secondSelectedIndex: StateFlow<Int> = _secondSelectedIndex

    private var _selectedTeamIndex = MutableStateFlow(0)
    val selectedTeamIndex: StateFlow<Int> = _selectedTeamIndex

    // etc
    var shouldScrollCategory = false

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data class SelectFirstCategory(val index: Int) : Intent()
        data class SelectSecondCategory(val index: Int) : Intent()
        data class SelectTeam(val index: Int) : Intent()
    }

    override fun send(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.SelectFirstCategory -> selectFirstCategory(intent.index)
                is Intent.SelectSecondCategory -> selectSecondCategory(intent.index)
                is Intent.SelectTeam -> selectTeam(intent.index)
            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: FBGameStatsDisplayModel) {
        viewModelScope.launch {
            _displayModel.emit(displayModel)

            // set current(home) team's players stats
            val homeTeamId = displayModel.game.teams.home.id
            val playersStats = displayModel.game.players.find { it.team.id == homeTeamId }?.players
            _playersStats.emit(playersStats ?: emptyList())
            setPlayersTotalStats()

            // set current(home) team's coach, lineups
            val lineups = displayModel.game.lineups.find { it.team.id == homeTeamId }
            _lineups.emit(lineups)
            _coach.emit(lineups?.coach)

            sortPlayers()
        }
    }

    /* ---------------------
       implements
       --------------------- */
    private suspend fun selectFirstCategory(index: Int) {
        shouldScrollCategory = true

        val attackCategoriesSize = StringConstants.Football.gameStatsAttackCategories.size
        val defendCategoriesSize = StringConstants.Football.gameStatsDefendCategories.size

        when (index) {
            0 -> _secondSelectedIndex.emit(0)
            1 -> _secondSelectedIndex.emit(attackCategoriesSize)
            2 -> _secondSelectedIndex.emit(attackCategoriesSize + defendCategoriesSize)
        }

        _firstSelectedIndex.emit(index)

        sortPlayers()
    }

    private suspend fun selectSecondCategory(index: Int) {
        shouldScrollCategory = false
        _secondSelectedIndex.emit(index)

        val attackCategories = StringConstants.Football.gameStatsAttackCategories
        val defendCategories = StringConstants.Football.gameStatsDefendCategories

        when (index) {
            in attackCategories.indices -> _firstSelectedIndex.emit(0)
            in attackCategories.size until attackCategories.size + defendCategories.size -> _firstSelectedIndex.emit(1)
            else -> _firstSelectedIndex.emit(2)
        }

        sortPlayers()
    }

    private suspend fun selectTeam(index: Int) {
        _selectedTeamIndex.emit(index)

        // set selected team's players stats
        val teamId = when (index) {
            0 -> displayModel.value?.game?.teams?.home?.id
            1 -> displayModel.value?.game?.teams?.away?.id
            else -> null
        }

        val playersStats = displayModel.value?.game?.players?.find { teamId != null && it.team.id == teamId }?.players
        _playersStats.emit(playersStats ?: emptyList())
        setPlayersTotalStats()

        // set selected team's coach, lineups
        val lineups = displayModel.value?.game?.lineups?.find { teamId != null && it.team.id == teamId }
        _lineups.emit(lineups)
        _coach.emit(lineups?.coach)

        sortPlayers()
    }

    private suspend fun sortPlayers() {
        val playerStats = playerStats.value.toMutableList()

        when (secondSelectedIndex.value) {
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

        _playersStats.emit(playerStats)
        setPlayersTotalStats()
    }

    private suspend fun setPlayersTotalStats() {
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

        _playersTotalStats.emit(playersTotalStats)
    }
}