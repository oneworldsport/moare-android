package com.moare.android.features.search.display.nba.viewmodel

import android.util.Log
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.KeywordInfo
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStandingsDisplay
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStandingsDisplayModel
import com.moare.android.features.search.networking.SearchClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NBAPlayerStandingsViewModel @Inject constructor(
    private val searchClient: SearchClient
) : MVIViewModel<NBAPlayerStandingsViewModel.Intent, NBAPlayerStandingsDisplayModel>() {
    /* ---------------------
       constants
       --------------------- */
    val dataItemHeight = 40.dp
    val itemWidth = 70.dp
    val firstCategoryItemWidth = 132.dp
    val categoryItemHeight = 40.dp
    val categoryFontSize = 15.sp
    val dataFontSize = 15.sp
    val barWidth = 2.dp // TODO: Make it const

    /* ---------------------
       data state
       --------------------- */
    private var _displayModel = MutableStateFlow<NBAPlayerStandingsDisplayModel?>(null)
    val displayModel: StateFlow<NBAPlayerStandingsDisplayModel?> = _displayModel

    private val _displayDataState = MutableStateFlow<ApiFetchState>(ApiFetchState.Idle)
    val displayDataState: StateFlow<ApiFetchState> = _displayDataState

    private var _filteredStandings = MutableStateFlow<List<NBAPlayerStandingsDisplay>>(emptyList())
    val filteredStandings: StateFlow<List<NBAPlayerStandingsDisplay>> = _filteredStandings

    /* ---------------------
       ui state
       --------------------- */
    private var _firstSelectedIndex = MutableStateFlow(0)
    val firstSelectedIndex: StateFlow<Int> = _firstSelectedIndex

    private var _secondSelectedIndex = MutableStateFlow(0)
    val secondSelectedIndex: StateFlow<Int> = _secondSelectedIndex

    private var _isKeyword = MutableStateFlow(false)
    val isKeyword: StateFlow<Boolean> = _isKeyword

    private var _entityIndex = MutableStateFlow<Int?>(null)
    val entityIndex: StateFlow<Int?> = _entityIndex

    private var _filteredStandingsStartIndex = MutableStateFlow(0)
    val filteredStandingsStartIndex: StateFlow<Int> = _filteredStandingsStartIndex

    /* ---------------------
       etc
       --------------------- */
    var shouldScrollCategory = true
    var standings: List<NBAPlayerStandingsDisplay> = emptyList()
    private var selectedEntity: EntityInfo? = null
    private var filteredStandingsEndIndex = 0 // NOTE: one bigger then actual showing end item's index. Because of subList.

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data class InitData(val displayModel: NBAPlayerStandingsDisplayModel) : Intent()
        data class SelectFirstCategory(val index: Int) : Intent()
        data class SelectSecondCategory(val index: Int, val category: String) : Intent()
        data class ShowMoreStandings(val isUp: Boolean) : Intent()
    }

    override fun send(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.InitData -> initData(intent.displayModel)
                is Intent.SelectFirstCategory -> selectFirstCategory(intent.index)
                is Intent.SelectSecondCategory -> selectSecondCategory(intent.index, intent.category)
                is Intent.ShowMoreStandings -> addStandings(intent.isUp)
            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: NBAPlayerStandingsDisplayModel) {
        viewModelScope.launch {
            // init with default value
            _displayModel.emit(null)
            _displayDataState.emit(ApiFetchState.Idle)
            _filteredStandings.emit(emptyList())
            _firstSelectedIndex.emit(0)
            _secondSelectedIndex.emit(0)
            _isKeyword.emit(false)
            _entityIndex.emit(null)
            _filteredStandingsStartIndex.emit(0)
            shouldScrollCategory = true
            standings = emptyList()
            selectedEntity = null
            filteredStandingsEndIndex = 0

            // init data
            _displayModel.emit(displayModel)
            standings = displayModel.standings

            val keywords = displayModel.keywords
            if (keywords.isNotEmpty()) {
                // Check matching keyword in the order of categories, doesn't matter what keyword is in keywords
                val index = StringConstants.NBA.playerStandingsSecondCategories.indexOfFirst { category ->
                    val keyword = keywords.find { it.keyword == category }
                    keyword != null
                }

                if (index != -1) {
                    _secondSelectedIndex.emit(index)
                    _isKeyword.emit(true)
                }
            }

            filterStandings()
        }
    }

    /* ---------------------
       implements
       --------------------- */
    private suspend fun selectFirstCategory(index: Int) {
        shouldScrollCategory = true

        val attackCategoriesSize = StringConstants.NBA.playerStandingsAttackCategories.size
        val defendCategoriesSize = StringConstants.NBA.playerStandingsDefendCategories.size

        var secondCategory = "경기당 득점"

        when (index) {
            0 -> {
                _secondSelectedIndex.emit(0)
                secondCategory = "경기당 득점"
            }
            1 -> {
                _secondSelectedIndex.emit(attackCategoriesSize)
                secondCategory = "경기당 수비리바운드"
            }
            2 -> {
                _secondSelectedIndex.emit(attackCategoriesSize + defendCategoriesSize)
                secondCategory = "경기당 리바운드"
            }
        }

        _firstSelectedIndex.emit(index)

        fetchStandings(secondCategory)
    }

    private suspend fun selectSecondCategory(index: Int, category: String) {
        shouldScrollCategory = false
        _secondSelectedIndex.emit(index)

        val attackCategories = StringConstants.NBA.playerStandingsAttackCategories
        val defendCategories = StringConstants.NBA.playerStandingsDefendCategories

        when (index) {
            in attackCategories.indices -> _firstSelectedIndex.emit(0)
            in attackCategories.size until attackCategories.size + defendCategories.size -> _firstSelectedIndex.emit(1)
            else -> _firstSelectedIndex.emit(2)
        }

        fetchStandings(category)
    }

    private suspend fun filterStandings() {
        // Get the first entity(player) matching in the standings.(Process works in the order of standings)
        val index = standings.indexOfFirst { player ->
            val entity = displayModel.value?.entityInfo?.find { it.playerId == player.player.personId }
            entity?.let {
                selectedEntity = it
            }
            entity != null
        }

        if (index != -1) {
            _entityIndex.emit(index)
        }

        // Get 20 items based on index
        val rangeSize = 20
        val startIndex = maxOf(0, index - (rangeSize / 2) + 1)
        val endIndex = minOf(standings.size, startIndex + rangeSize)

        val newStandings = standings.subList(startIndex, endIndex)

        filteredStandingsEndIndex = endIndex
        _filteredStandingsStartIndex.emit(startIndex)

        // remove loading
        _displayDataState.emit(ApiFetchState.Success)

        // show 'filteredStandings'
        _filteredStandings.emit(newStandings)
    }

    private suspend fun addStandings(isUp: Boolean) {
        // get 10 more standings
        if (isUp) {
            val newStartIndex = maxOf(0, filteredStandingsStartIndex.value - 10)

            if (newStartIndex == filteredStandingsStartIndex.value) {
                return
            }

            val newStandings = standings.subList(newStartIndex, filteredStandingsEndIndex)

            _filteredStandingsStartIndex.emit(newStartIndex)
            _filteredStandings.emit(newStandings)
        } else {
            val newEndIndex = minOf(standings.size, filteredStandingsEndIndex + 10)

            if (newEndIndex == filteredStandingsEndIndex) {
                return
            }

            val newStandings = standings.subList(filteredStandingsStartIndex.value, newEndIndex)

            filteredStandingsEndIndex = newEndIndex
            _filteredStandings.emit(newStandings)
        }
    }

    private suspend fun fetchStandings(category: String) {
        _displayDataState.emit(ApiFetchState.Fetching)

        try {
            // TODO: Structure should be updated(Temporary code)
            val standingsKeyword = displayModel.value?.keywords?.first { it.id == "standings" }
            val keywords = listOf(standingsKeyword!!, Keyword(keyword = category, id = "", priority = 100))
            val entities = if (selectedEntity != null) listOf(selectedEntity!!) else emptyList()
            val keywordInfo = KeywordInfo(
                keyword = category,
                keywords = keywords,
                entities = entities
            )

            val result = searchClient.fetchDataByKeyword(keywordInfo)

            if (result.data is SportDecodableModel.NBAPlayerStandings) {
                _displayModel.emit(result.data.displayModel)
                standings = result.data.displayModel.standings
                filterStandings()
            }
        } catch (e: Exception) {
            _displayDataState.emit(ApiFetchState.Error("데이터를 불러오는데 실패하였습니다."))
            Log.e("dsdf", e.localizedMessage ?: "error")
        }
    }
}






























