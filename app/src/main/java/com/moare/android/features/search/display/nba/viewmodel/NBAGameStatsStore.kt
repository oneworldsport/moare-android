package com.moare.android.features.search.display.nba.viewmodel

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.rounded
import com.moare.android.features.search.display.common.store.BaseGameStatsStore
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
import com.moare.android.features.search.models.models.nba.NBABoxScoreTeamPlayer
import com.moare.android.features.search.models.models.nba.NBAGameBoxScoreStats
import com.moare.android.features.search.models.models.nba.NBALineScore
import com.moare.android.features.search.models.responsemodels.nba.NBAGameStatsResponseModel
import com.moare.android.features.search.networking.SearchClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface NBAGameStatsAction {
    data object InitData : NBAGameStatsAction
    data class SelectSecondCategory(val index: Int) : NBAGameStatsAction
    data class SelectTeam(val index: Int) : NBAGameStatsAction
    data class RefreshGame(val shouldFetch: Boolean = true) : NBAGameStatsAction
}

sealed interface NBAGameStatsDelegate {
    data class RefreshGame(val model: SportDecodableModel.NBAGameStats) : NBAGameStatsDelegate
}

class NBAGameStatsStore @AssistedInject constructor(
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: NBAGameStatsDisplayModel,
    @Assisted val emitToParent: (NBAGameStatsDelegate) -> Unit
) : BaseGameStatsStore<NBAGameStatsAction, NBAGameStatsDisplayModel>(model, nameProvider) {
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

    private val _homeTeamLineScore = MutableStateFlow<NBALineScore?>(null)
    val homeTeamLineScore: StateFlow<NBALineScore?> = _homeTeamLineScore

    private val _awayTeamLineScore = MutableStateFlow<NBALineScore?>(null)
    val awayTeamLineScore: StateFlow<NBALineScore?> = _awayTeamLineScore

    private val _playersStats = MutableStateFlow<List<NBABoxScoreTeamPlayer>>(emptyList())
    val playerStats: StateFlow<List<NBABoxScoreTeamPlayer>> = _playersStats

    private val _playersTotalStats = MutableStateFlow<NBAGameBoxScoreStats?>(null)
    val playersTotalStats: StateFlow<NBAGameBoxScoreStats?> = _playersTotalStats

    private var homeTeamId = 0
    private var awayTeamId = 0

    @AssistedFactory
    interface Factory {
        fun create(
            model: NBAGameStatsDisplayModel,
            emitToParent: (NBAGameStatsDelegate) -> Unit
        ) : NBAGameStatsStore
    }

    override fun send(action: NBAGameStatsAction) {
        when (action) {
            is NBAGameStatsAction.InitData -> initData()
            is NBAGameStatsAction.SelectSecondCategory -> selectSecondCategory(action.index)
            is NBAGameStatsAction.SelectTeam -> selectTeam(false, action.index)
            is NBAGameStatsAction.RefreshGame -> refreshGame(action.shouldFetch)
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData() {
        super.initData()

        // init with default value
        _homeTeamLineScore.value = null
        _awayTeamLineScore.value = null
        _playersStats.value = emptyList()
        _playersTotalStats.value = null

        homeTeamId = 0
        awayTeamId = 0

        displayModel.value.game.gameSummary?.let {
            homeTeamId = it.homeTeamId
            awayTeamId = it.awayTeamId
        }

        // set lineScore
        _homeTeamLineScore.value = displayModel.value.game.lineScore?.find { it.teamId == homeTeamId }
        _awayTeamLineScore.value = displayModel.value.game.lineScore?.find { it.teamId == awayTeamId }

        displayModel.value.game.boxScoreTraditional?.let {
            // set current(home) team's players stats
            selectTeam(true, 0)
        }
    }

    override fun selectSecondCategory(index: Int) {
        super.selectSecondCategory(index)

        val attackCategories = StringConstants.NBA.GAME_STATS_ATTACK_CATEGORIES
        val defendCategories = StringConstants.NBA.GAME_STATS_DEFEND_CATEGORIES

        when (index) {
            in attackCategories.indices -> _firstCategorySelectedIndex.value = 0
            in attackCategories.size until attackCategories.size + defendCategories.size -> _firstCategorySelectedIndex.value = 1
            else -> _firstCategorySelectedIndex.value = 2
        }

        sortPlayers()
    }

    override fun selectTeam(isInit: Boolean, index: Int) {
        super.selectTeam(isInit, index)

        // set selected team's players stats
        _playersStats.value = if (index == 0) {
            displayModel.value.game.boxScoreTraditional?.homeTeam?.players ?: emptyList()
        } else {
            displayModel.value.game.boxScoreTraditional?.awayTeam?.players ?: emptyList()
        }

        setPlayersTotalStats()
        sortPlayers()
        if (isInit) {
            refreshGame(false)
        }
    }

    override fun sortPlayers() {
        val playerStats = playerStats.value.toMutableList()

        when (secondCategorySelectedIndex.value) {
            0 -> playerStats.sortByDescending { it.statistics.points }
            1 -> playerStats.sortByDescending { it.statistics.assists }
            2 -> playerStats.sortByDescending { it.statistics.reboundsOffensive }
            3 -> playerStats.sortByDescending { it.statistics.fieldGoalsAttempted }
            4 -> playerStats.sortByDescending { it.statistics.fieldGoalsMade }
            5 -> playerStats.sortByDescending { it.statistics.fieldGoalsPercentage }
            6 -> playerStats.sortByDescending { it.statistics.threePointersAttempted }
            7 -> playerStats.sortByDescending { it.statistics.threePointersMade }
            8 -> playerStats.sortByDescending { it.statistics.threePointersPercentage }
            9 -> playerStats.sortByDescending { it.statistics.freeThrowsAttempted }
            10 -> playerStats.sortByDescending { it.statistics.freeThrowsMade }
            11 -> playerStats.sortByDescending { it.statistics.freeThrowsPercentage }
            12 -> playerStats.sortByDescending { it.statistics.reboundsDefensive }
            13 -> playerStats.sortByDescending { it.statistics.blocks }
            14 -> playerStats.sortByDescending { it.statistics.steals }
            15 -> playerStats.sortByDescending { it.statistics.reboundsTotal }
            16 -> playerStats.sortByDescending { it.statistics.turnovers }
            17 -> playerStats.sortByDescending { it.statistics.foulsPersonal }
            18 -> playerStats.sortByDescending { it.statistics.plusMinusPoints }
            19 -> playerStats.sortByDescending { CalendarUtil.formatHourMinuteToMinutes(it.statistics.minutes) }
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

        playersTotalStats.plusMinusPoints = if (teamCategorySelectedIndex.value == 0) {
            (homeTeamLineScore.value?.pts ?: 0) - (awayTeamLineScore.value?.pts ?: 0)
        } else {
            (awayTeamLineScore.value?.pts ?: 0) - (homeTeamLineScore.value?.pts ?: 0)
        }

        _playersTotalStats.value = playersTotalStats
    }

    private fun refreshGame(shouldFetch: Boolean) {
        if (shouldFetch) {
            scope.launch {
                val game = displayModel.value.game
                val gameSummary = game.gameSummary
                val boxScoreTraditional = game.boxScoreTraditional

                gameSummary?.let {
                    boxScoreTraditional?.let {
                        // TODO: Has to add loading
                        val result = searchClient.fetchById(
                            season = displayModel.value.season,
                            category = "basketball",
                            date = gameSummary.gameDate,
                            dataType = "basketball_game_stats",
                            leagueId = Constants.Ids.NBA,
                            id = boxScoreTraditional.gameId
                        )

                        if (result.data is SportDecodableModel.NBAGameStats) {
                            _displayModel.value = result.data.displayModel
                            initData()
                            emitToParent(NBAGameStatsDelegate.RefreshGame(result.data))
                        }
                    }
                }
            }
        } else {
            val responseModel = NBAGameStatsResponseModel(game = displayModel.value.game)
            val dataModel = SportDecodableModel.NBAGameStats(responseModel, displayModel.value)

            emitToParent(NBAGameStatsDelegate.RefreshGame(dataModel))
        }
    }
}












