package com.moare.android.features.search.display.football.viewmodel

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplay
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplayModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FBPlayerStandingsViewModel @Inject constructor(
) : MVIViewModel<FBPlayerStandingsViewModel.Intent, FBPlayerStandingsDisplayModel>() {
    /* ---------------------
       constants
       --------------------- */
    val dataItemHeight = 40.dp
    val categoryItemHeight = 40.dp
    val firstCategoryItemWidth = 132.dp
    val itemWidth = 70.dp
    val barWidth = 2.dp
    val categoryFontSize = 15.sp
    val dataFontSize = 15.sp

    /* ---------------------
       data state
       --------------------- */
    private var _displayModel = MutableStateFlow<FBPlayerStandingsDisplayModel?>(null)
    val displayModel: StateFlow<FBPlayerStandingsDisplayModel?> = _displayModel

    private var _standings = MutableStateFlow<List<FBPlayerStandingsDisplay>>(emptyList())
    val standings: StateFlow<List<FBPlayerStandingsDisplay>> = _standings

    /* ---------------------
       ui state
       --------------------- */
    private var _firstSelectedIndex = MutableStateFlow(0)
    val firstSelectedIndex: StateFlow<Int> = _firstSelectedIndex

    private var _secondSelectedIndex = MutableStateFlow(0)
    val secondSelectedIndex: StateFlow<Int> = _secondSelectedIndex

    private var _isKeyword = MutableStateFlow(false)
    val isKeyword: StateFlow<Boolean> = _isKeyword

    /* ---------------------
       etc
       --------------------- */
    var shouldScrollCategory = true

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data class SelectFirstCategory(val index: Int) : Intent()
        data class SelectSecondCategory(val index: Int) : Intent()
        data object SortStandings : Intent()
    }

    override fun send(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.SelectFirstCategory -> selectFirstCategory(intent.index)
                is Intent.SelectSecondCategory -> selectSecondCategory(intent.index)
                is Intent.SortStandings -> sortStandings()
            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: FBPlayerStandingsDisplayModel) {
        viewModelScope.launch {
            _displayModel.emit(displayModel)
            _standings.emit(displayModel.standings)

            val keywords = displayModel.keywords
            if (keywords.isNotEmpty()) {
                val index = StringConstants.Football.playerStandingsSecondCategories.indexOfFirst { category ->
                    val keyword = keywords.find { it.keyword == category }
                    keyword != null
                }

                if (index != -1) {
                    _secondSelectedIndex.emit(index)
                    _isKeyword.emit(true)
                }
            }

            sortStandings()
        }
    }

    /* ---------------------
       implements
       --------------------- */
    private suspend fun selectFirstCategory(index: Int) {
        shouldScrollCategory = true

        val attackCategoriesSize = StringConstants.Football.playerStandingsAttackCategories.size
        val defendCategoriesSize = StringConstants.Football.playerStandingsDefendCategories.size

        when (index) {
            0 -> _secondSelectedIndex.emit(0)
            1 -> _secondSelectedIndex.emit(attackCategoriesSize)
            2 -> _secondSelectedIndex.emit(attackCategoriesSize + defendCategoriesSize)
        }

        _firstSelectedIndex.emit(index)

        sortStandings()
    }

    private suspend fun selectSecondCategory(index: Int) {
        shouldScrollCategory = false
        _secondSelectedIndex.emit(index)

        val attackCategories = StringConstants.Football.playerStandingsAttackCategories
        val defendCategories = StringConstants.Football.playerStandingsDefendCategories

        when (index) {
            in attackCategories.indices -> _firstSelectedIndex.emit(0)
            in attackCategories.size until attackCategories.size + defendCategories.size -> _firstSelectedIndex.emit(1)
            else -> _firstSelectedIndex.emit(2)
        }

        sortStandings()
    }

    private suspend fun sortStandings() {
        var standings = standings.value.toMutableList()

        when (secondSelectedIndex.value) {
            0 -> standings.sortByDescending { it.stats.goals.total }
            1 -> standings.sortByDescending { it.stats.goals.assists }
            2 -> standings.sortByDescending { it.stats.goals.total + it.stats.goals.assists }
            3 -> standings.sortByDescending { it.stats.shots.total }
            4 -> standings.sortByDescending { it.stats.shots.on }
            5 -> standings.sortByDescending { it.stats.passes.key }
            6 -> standings.sortByDescending { it.stats.dribbles.success }
            7 -> standings.sortByDescending { it.stats.penalty.scored }
            8 -> standings.sortByDescending { it.stats.tackles.total }
            9 -> standings.sortByDescending { it.stats.duels.won }
            10 -> standings.sortByDescending { it.stats.passes.total }
            11 -> standings.sortByDescending { it.stats.fouls.committed }
            12 -> standings.sortByDescending { it.stats.cards.yellow }
            13 -> standings.sortByDescending { it.stats.cards.red }
            14 -> standings.sortByDescending { it.stats.games.appearences }
            15 -> standings.sortByDescending { it.stats.games.lineups }
            16 -> standings.sortByDescending { it.stats.substitutes.substituteIn }
            17 -> standings.sortByDescending { it.stats.games.minutes }
            18 -> standings.sortByDescending { it.stats.games.rating.toDoubleOrNull() ?: 0.0 }
        }

        _standings.emit(standings.take(20))
    }
}