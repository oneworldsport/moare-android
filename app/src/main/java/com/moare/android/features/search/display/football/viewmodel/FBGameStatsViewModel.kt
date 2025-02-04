package com.moare.android.features.search.display.football.viewmodel

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.models.football.FBGameLineups
import com.moare.android.features.search.models.models.football.FBGamePlayerStats
import com.moare.android.features.search.models.models.football.FBPerson
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
    val categoryItemHeight = 30.dp
    val firstItemWidth = 120.dp
    val teamButtonWidth = 100.dp
    val itemWidth = 70.dp
    val barWidth = 2.dp
    val categoryFontSize = 14.sp
    val dataFontSize = 14.sp
    val firstCategory = "선수 이름"
    val firstCategoryList = listOf("공격지표", "수비지표", "공통지표")
    val secondCategoryList = listOf("득점", "어시스트", "공격포인트", "슈팅", "유효슈팅", "태클", "패스", "파울", "경고", "퇴장")
    val attackCategoryList = listOf("득점", "어시스트", "공격포인트", "슈팅", "유효슈팅")
    val defendCategoryList = listOf("태클", "패스")
    val commonCategoryList = listOf("파울", "경고", "퇴장")

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

        when (index) {
            0 -> _secondSelectedIndex.emit(0)
            1 -> _secondSelectedIndex.emit(attackCategoryList.size)
            2 -> _secondSelectedIndex.emit(attackCategoryList.size + defendCategoryList.size)
        }

        _firstSelectedIndex.emit(index)

        sortPlayers()
    }

    private suspend fun selectSecondCategory(index: Int) {
        shouldScrollCategory = false
        _secondSelectedIndex.emit(index)

        when (index) {
            in attackCategoryList.indices -> _firstSelectedIndex.emit(0)
            in attackCategoryList.size until attackCategoryList.size + defendCategoryList.size -> _firstSelectedIndex.emit(1)
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
            1 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.goals?.assists ?: 0 }
            2 -> playerStats.sortByDescending {
                (it.statistics.firstOrNull()?.goals?.total ?: 0) + (it.statistics.firstOrNull()?.goals?.assists ?: 0)
            }
            3 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.shots?.total ?: 0 }
            4 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.shots?.on ?: 0 }
            5 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.passes?.total ?: 0 }
            6 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.tackles?.total ?: 0 }
            7 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.fouls?.committed ?: 0 }
            8 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.cards?.yellow ?: 0 }
            9 -> playerStats.sortByDescending { it.statistics.firstOrNull()?.cards?.red ?: 0 }
        }

        _playersStats.emit(playerStats)
    }
}