package com.moare.android.features.search.display.nba.viewmodel

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.rounded
import com.moare.android.features.search.display.common.viewmodel.BaseGameStatsViewModel
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
import com.moare.android.features.search.models.models.nba.NBABoxScoreTeamPlayer
import com.moare.android.features.search.models.models.nba.NBAGameBoxScoreStats
import com.moare.android.features.search.models.models.nba.NBALineScore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NBAGameStatsIntent {
    data class InitData(val displayModel: NBAGameStatsDisplayModel) : NBAGameStatsIntent()
    data class SelectFirstCategory(val index: Int) : NBAGameStatsIntent()
    data class SelectSecondCategory(val index: Int) : NBAGameStatsIntent()
    data class SelectTeam(val index: Int) : NBAGameStatsIntent()
}

@HiltViewModel
class NBAGameStatsViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : BaseGameStatsViewModel<NBAGameStatsIntent, NBAGameStatsDisplayModel>(nameProvider) {
    /* ---------------------
       constants
       --------------------- */
    val dataItemHeight = 40.dp
    val firstCategoryItemHeight = 34.dp
    val secondCategoryItemHeight = 40.dp
    val firstItemWidth = 120.dp
    val teamButtonWidth = 100.dp
    val itemWidth = 70.dp
    val barWidth = 2.dp
    val firstCategoryFontSize = 14.sp
    val secondCategoryFontSize = 13.sp
    val dataFontSize = 14.sp
    val lineScoreItemHeight = 50.dp

    /* ---------------------
       data state
       --------------------- */
    private val _homeTeamLineScore = MutableStateFlow<NBALineScore?>(null)
    val homeTeamLineScore: StateFlow<NBALineScore?> = _homeTeamLineScore

    private val _awayTeamLineScore = MutableStateFlow<NBALineScore?>(null)
    val awayTeamLineScore: StateFlow<NBALineScore?> = _awayTeamLineScore

    private val _playersStats = MutableStateFlow<List<NBABoxScoreTeamPlayer>>(emptyList())
    val playerStats: StateFlow<List<NBABoxScoreTeamPlayer>> = _playersStats

    private val _playersTotalStats = MutableStateFlow<NBAGameBoxScoreStats?>(null)
    val playersTotalStats: StateFlow<NBAGameBoxScoreStats?> = _playersTotalStats

    /* ---------------------
       etc
       --------------------- */
    private var homeTeamId = 0
    private var awayTeamId = 0

    override fun send(intent: NBAGameStatsIntent) {
        when (intent) {
            is NBAGameStatsIntent.InitData -> initData(intent.displayModel)
            is NBAGameStatsIntent.SelectFirstCategory -> selectFirstCategory(intent.index)
            is NBAGameStatsIntent.SelectSecondCategory -> selectSecondCategory(intent.index)
            is NBAGameStatsIntent.SelectTeam -> selectTeam(intent.index)
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: NBAGameStatsDisplayModel) {
        super.initData(displayModel)

        // init with default value
        _homeTeamLineScore.value = null
        _awayTeamLineScore.value = null
        _playersStats.value = emptyList()
        _playersTotalStats.value = null

        homeTeamId = 0
        awayTeamId = 0

        displayModel.game.gameSummary?.let {
            homeTeamId = it.homeTeamId
            awayTeamId = it.visitorTeamId
        }

        // set lineScore
        _homeTeamLineScore.value = displayModel.game.lineScore.find { it.teamId == homeTeamId }
        _awayTeamLineScore.value = displayModel.game.lineScore.find { it.teamId == awayTeamId }

        displayModel.game.boxScoreTraditional?.let {
            // set current(home) team's players stats
            selectTeam(0)
        }
    }

    /* ---------------------
       implements
       --------------------- */
    override fun selectFirstCategory(index: Int) {
        super.selectFirstCategory(index)

        val attackCategoriesSize = StringConstants.NBA.GAME_STATS_ATTACK_CATEGORIES.size
        val defendCategoriesSize = StringConstants.NBA.GAME_STATS_DEFEND_CATEGORIES.size

        when (index) {
            0 -> _secondSelectedIndex.value = 0
            1 -> _secondSelectedIndex.value = attackCategoriesSize
            2 -> _secondSelectedIndex.value = attackCategoriesSize + defendCategoriesSize
        }

        _firstSelectedIndex.value = index

        sortPlayers()
    }

    override fun selectSecondCategory(index: Int) {
        super.selectSecondCategory(index)

        val attackCategories = StringConstants.NBA.GAME_STATS_ATTACK_CATEGORIES
        val defendCategories = StringConstants.NBA.GAME_STATS_DEFEND_CATEGORIES

        when (index) {
            in attackCategories.indices -> _firstSelectedIndex.value = 0
            in attackCategories.size until attackCategories.size + defendCategories.size -> _firstSelectedIndex.value = 1
            else -> _firstSelectedIndex.value = 2
        }

        sortPlayers()
    }

    override fun selectTeam(index: Int) {
        super.selectTeam(index)

        // set selected team's players stats
        _playersStats.value = if (index == 0) {
            displayModel.value?.game?.boxScoreTraditional?.homeTeam?.players ?: emptyList()
        } else {
            displayModel.value?.game?.boxScoreTraditional?.awayTeam?.players ?: emptyList()
        }

        setPlayersTotalStats()
        sortPlayers()
    }

    override fun sortPlayers() {
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
            19 -> playerStats.sortedByDescending { CalendarUtil.formatHourMinuteToMinutes(it.statistics.minutes) }
            else -> {}
        }

        _playersStats.value = playerStats
    }

    override fun setPlayersTotalStats() {
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

        _playersTotalStats.value = playersTotalStats
    }
}












