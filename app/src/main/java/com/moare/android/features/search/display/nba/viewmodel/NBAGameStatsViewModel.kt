package com.moare.android.features.search.display.nba.viewmodel

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.percentageOf
import com.moare.android.core.util.rounded
import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
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
import com.moare.android.features.search.models.models.nba.NBABoxScoreTeamPlayer
import com.moare.android.features.search.models.models.nba.NBAGameBoxScoreStats
import com.moare.android.features.search.models.models.nba.NBALineScore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NBAGameStatsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : MVIViewModel<NBAGameStatsViewModel.Intent, NBAGameStatsDisplayModel>() {
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
       data state
       --------------------- */
    private val _displayModel = MutableStateFlow<NBAGameStatsDisplayModel?>(null)
    val displayModel: StateFlow<NBAGameStatsDisplayModel?> = _displayModel

    private val _homeTeamLineScore = MutableStateFlow<NBALineScore?>(null)
    val homeTeamLineScore: StateFlow<NBALineScore?> = _homeTeamLineScore

    private val _awayTeamLineScore = MutableStateFlow<NBALineScore?>(null)
    val awayTeamLineScore: StateFlow<NBALineScore?> = _awayTeamLineScore

    private val _playersStats = MutableStateFlow<List<NBABoxScoreTeamPlayer>>(emptyList())
    val playerStats: StateFlow<List<NBABoxScoreTeamPlayer>> = _playersStats

    private val _playersTotalStats = MutableStateFlow<NBAGameBoxScoreStats?>(null)
    val playersTotalStats: StateFlow<NBAGameBoxScoreStats?> = _playersTotalStats

    /* ---------------------
       ui state
       --------------------- */
    private var _firstSelectedIndex = MutableStateFlow(0)
    val firstSelectedIndex: StateFlow<Int> = _firstSelectedIndex

    private var _secondSelectedIndex = MutableStateFlow(0)
    val secondSelectedIndex: StateFlow<Int> = _secondSelectedIndex

    private var _selectedTeamIndex = MutableStateFlow(0)
    val selectedTeamIndex: StateFlow<Int> = _selectedTeamIndex

    /* ---------------------
       etc
       --------------------- */
    var shouldScrollCategory = false
    var homeTeamId = 0
    var awayTeamId = 0
    var playerNameDictionary: Map<String, String> = emptyMap()
    var teamNameDictionary: Map<String, String> = emptyMap()

    init {
        playerNameDictionary = nameProvider.getDictionary("nba_player")
        teamNameDictionary = nameProvider.getDictionary("nba_team")
    }

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data class InitData(val displayModel: NBAGameStatsDisplayModel) : Intent()
        data class SelectFirstCategory(val index: Int) : Intent()
        data class SelectSecondCategory(val index: Int) : Intent()
        data class SelectTeam(val index: Int) : Intent()
    }

    override fun send(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.InitData -> initData(intent.displayModel)
                is Intent.SelectFirstCategory -> selectFirstCategory(intent.index)
                is Intent.SelectSecondCategory -> selectSecondCategory(intent.index)
                is Intent.SelectTeam -> selectTeam(intent.index)
            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: NBAGameStatsDisplayModel) {
        viewModelScope.launch {
            // init with default value
            _homeTeamLineScore.emit(null)
            _awayTeamLineScore.emit(null)
            _playersStats.emit(emptyList())
            _playersTotalStats.emit(null)
            _firstSelectedIndex.emit(0)
            _secondSelectedIndex.emit(0)
            _selectedTeamIndex.emit(0)
            shouldScrollCategory = false
            homeTeamId = 0
            awayTeamId = 0

            // init data
            _displayModel.emit(displayModel)

            displayModel.game.gameSummary?.let {
                homeTeamId = it.homeTeamId
                awayTeamId = it.visitorTeamId
            }

            // set lineScore
            _homeTeamLineScore.emit(displayModel.game.lineScore.find { it.teamId == homeTeamId })
            _awayTeamLineScore.emit(displayModel.game.lineScore.find { it.teamId == awayTeamId })

            displayModel.game.boxScoreTraditional?.let {
                // set current(home) team's players stats
                _playersStats.emit(it.homeTeam.players)
                setPlayersTotalStats()

                sortPlayers()
            }
        }
    }

    /* ---------------------
       implements
       --------------------- */
    private suspend fun selectFirstCategory(index: Int) {
        shouldScrollCategory = true

        val attackCategoriesSize = StringConstants.NBA.gameStatsAttackCategories.size
        val defendCategoriesSize = StringConstants.NBA.gameStatsDefendCategories.size

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

        val attackCategories = StringConstants.NBA.gameStatsAttackCategories
        val defendCategories = StringConstants.NBA.gameStatsDefendCategories

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
        _playersStats.emit(if (index == 0) {
            displayModel.value?.game?.boxScoreTraditional?.homeTeam?.players ?: emptyList()
        } else {
            displayModel.value?.game?.boxScoreTraditional?.awayTeam?.players ?: emptyList()
        })
        setPlayersTotalStats()

        sortPlayers()
    }

    private suspend fun sortPlayers() {
        val playerStats = playerStats.value.toMutableList()

        when (secondSelectedIndex.value) {
            0 -> playerStats.sortedByDescending { it.statistics.points }
            1 -> playerStats.sortedByDescending { it.statistics.assists }
            2 -> playerStats.sortedByDescending { it.statistics.reboundsOffensive }
            3 -> playerStats.sortedByDescending { it.statistics.fieldGoalsAttempted }
            4 -> playerStats.sortedByDescending { it.statistics.fieldGoalsMade }
            5 -> playerStats.sortedByDescending { it.statistics.fieldGoalsPercentage }
            6 -> playerStats.sortedByDescending { it.statistics.threePointersAttempted }
            7 -> playerStats.sortedByDescending { it.statistics.threePointersMade }
            8 -> playerStats.sortedByDescending { it.statistics.threePointersPercentage }
            9 -> playerStats.sortedByDescending { it.statistics.freeThrowsAttempted }
            10 -> playerStats.sortedByDescending { it.statistics.freeThrowsMade }
            11 -> playerStats.sortedByDescending { it.statistics.freeThrowsPercentage }
            12 -> playerStats.sortedByDescending { it.statistics.reboundsDefensive }
            13 -> playerStats.sortedByDescending { it.statistics.blocks }
            14 -> playerStats.sortedByDescending { it.statistics.steals }
            15 -> playerStats.sortedByDescending { it.statistics.reboundsTotal }
            16 -> playerStats.sortedByDescending { it.statistics.turnovers }
            17 -> playerStats.sortedByDescending { it.statistics.foulsPersonal }
            18 -> playerStats.sortedByDescending { it.statistics.plusMinusPoints }
            19 -> playerStats.sortedByDescending { it.statistics.minutes } // TODO: change
            else -> {}
        }

        _playersStats.emit(playerStats)
        setPlayersTotalStats()
    }

    private suspend fun setPlayersTotalStats() {
        val playersTotalStats = playerStats.value.map { it.statistics }
            .fold(
                NBAGameBoxScoreStats()
            ) { acc, stats ->
                NBAGameBoxScoreStats(
                    _assists = acc.assists + stats.assists,
                    _blocks = acc.blocks + stats.blocks,
                    _fieldGoalsAttempted = acc.fieldGoalsAttempted + stats.fieldGoalsAttempted,
                    _fieldGoalsMade = acc.fieldGoalsMade + stats.fieldGoalsMade,
                    _foulsPersonal = acc.foulsPersonal + stats.foulsPersonal,
                    _freeThrowsAttempted = acc.freeThrowsAttempted + stats.freeThrowsAttempted,
                    _freeThrowsMade = acc.freeThrowsMade + stats.freeThrowsMade,
                    _minutes = acc.minutes + stats.minutes,
                    _points = acc.points + stats.points,
                    _reboundsDefensive = acc.reboundsDefensive + stats.reboundsDefensive,
                    _reboundsOffensive = acc.reboundsOffensive + stats.reboundsOffensive,
                    _reboundsTotal = acc.reboundsTotal + stats.reboundsTotal,
                    _steals = acc.steals + stats.steals,
                    _threePointersAttempted = acc.freeThrowsAttempted + stats.threePointersAttempted,
                    _threePointersMade = acc.threePointersMade + stats.threePointersMade,
                    _turnovers = acc.turnovers + stats.turnovers,
                )
            }

        playersTotalStats.fieldGoalsPercentage = if (playersTotalStats.fieldGoalsAttempted > 0)
            (playersTotalStats.fieldGoalsMade.toDouble() / playersTotalStats.fieldGoalsAttempted).rounded(3) else 0.0

        playersTotalStats.freeThrowsPercentage = if (playersTotalStats.freeThrowsAttempted > 0)
            (playersTotalStats.freeThrowsMade.toDouble() / playersTotalStats.freeThrowsAttempted).rounded(3) else 0.0

        playersTotalStats.threePointersPercentage = if (playersTotalStats.threePointersAttempted > 0)
            (playersTotalStats.threePointersMade.toDouble() / playersTotalStats.threePointersAttempted).rounded(3) else 0.0

        playersTotalStats.plusMinusPoints = if (selectedTeamIndex.value == 0) {
            (homeTeamLineScore.value?.pts ?: 0) - (awayTeamLineScore.value?.pts ?: 0)
        } else {
            (awayTeamLineScore.value?.pts ?: 0) - (homeTeamLineScore.value?.pts ?: 0)
        }

        _playersTotalStats.emit(playersTotalStats)
    }
}












