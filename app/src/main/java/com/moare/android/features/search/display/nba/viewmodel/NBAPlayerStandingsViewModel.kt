package com.moare.android.features.search.display.nba.viewmodel

import android.util.Log
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.core.util.CalendarUtil
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
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider
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
    private val fetchCategoryIndexList = listOf(5, 8, 11, 21, 23, 24, 26, 27)

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
    var playerNameDictionary: Map<String, String> = emptyMap()
    var teamNameDictionary: Map<String, String> = emptyMap()

    init {
        // TODO: object에서 EntryPoint를 통해 가져와 사용하는 방법은 지양해야함. 그렇다면 ViewModel마다 주입하지 않고 사용할 수 있는 더 나은 방법이 있을지 고민해볼 필요 있음.
        playerNameDictionary = nameProvider.getDictionary("nba_player")
        teamNameDictionary = nameProvider.getDictionary("nba_team")
    }

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data class InitData(val displayModel: NBAPlayerStandingsDisplayModel) : Intent()
        data class SelectFirstCategory(val index: Int) : Intent()
        data class SelectSecondCategory(val index: Int, val category: String) : Intent()
        data class ShowMoreStandings(val isUp: Boolean) : Intent()
        data object SortStandings : Intent()
    }

    override fun send(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.InitData -> initData(intent.displayModel)
                is Intent.SelectFirstCategory -> selectFirstCategory(intent.index)
                is Intent.SelectSecondCategory -> selectSecondCategory(intent.index, intent.category)
                is Intent.ShowMoreStandings -> addStandings(intent.isUp)
                is Intent.SortStandings -> sortStandings()
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

            sortStandings()
        }
    }

    /* ---------------------
       implements
       --------------------- */
    private suspend fun selectFirstCategory(index: Int) {
        val beforeSecondSelectedIndex = secondSelectedIndex.value

        shouldScrollCategory = true

        val attackCategoriesSize = StringConstants.NBA.playerStandingsAttackCategories.size
        val defendCategoriesSize = StringConstants.NBA.playerStandingsDefendCategories.size

        when (index) {
            0 -> {
                _secondSelectedIndex.emit(0)
            }
            1 -> {
                _secondSelectedIndex.emit(attackCategoriesSize)
            }
            2 -> {
                _secondSelectedIndex.emit(attackCategoriesSize + defendCategoriesSize)
            }
        }

        _firstSelectedIndex.emit(index)

        when (beforeSecondSelectedIndex) {
            in fetchCategoryIndexList -> fetchStandings(StringConstants.NBA.playerStandingsSecondCategories[beforeSecondSelectedIndex]) // 경기당(PG) 데이터 아닌 카테고리에서 first 카테고리를 눌렀을때는 fetch 해야함
            else -> sortStandings()
        }
    }

    private suspend fun selectSecondCategory(index: Int, category: String) {
        val beforeSecondSelectedIndex = secondSelectedIndex.value

        shouldScrollCategory = false
        _secondSelectedIndex.emit(index)

        val attackCategories = StringConstants.NBA.playerStandingsAttackCategories
        val defendCategories = StringConstants.NBA.playerStandingsDefendCategories

        when (index) {
            in attackCategories.indices -> _firstSelectedIndex.emit(0)
            in attackCategories.size until attackCategories.size + defendCategories.size -> _firstSelectedIndex.emit(1)
            else -> _firstSelectedIndex.emit(2)
        }

        when (beforeSecondSelectedIndex) {
            in fetchCategoryIndexList -> fetchStandings(StringConstants.NBA.playerStandingsSecondCategories[beforeSecondSelectedIndex]) // 경기당(PG) 데이터가 아닌 카테고리에서 다른 카테고리를 눌렀을때는 무조건 fetch 해야함
            else -> when (index) { // 경기당(PG) 데이터인 카테고리에서 경기당 카테고리를 눌렀을때는 sort, 이외의 카테고리는 fetch
                in fetchCategoryIndexList -> fetchStandings(category)
                else -> sortStandings()
            }
        }
    }

    private suspend fun sortStandings() {
        standings = when (secondSelectedIndex.value) {
            0 -> standings.sortedByDescending { it.stats.ptsPG }
            1 -> standings.sortedByDescending { it.stats.astPG }
            2 -> standings.sortedByDescending { it.stats.orebPG }
            3 -> standings.sortedByDescending { it.stats.fgaPG }
            4 -> standings.sortedByDescending { it.stats.fgmPG }
            6 -> standings.sortedByDescending { it.stats.fg3aPG }
            7 -> standings.sortedByDescending { it.stats.fg3mPG }
            9 -> standings.sortedByDescending { it.stats.ftaPG }
            10 -> standings.sortedByDescending { it.stats.ftmPG }
            12 -> standings.sortedByDescending { it.stats.drebPG }
            13 -> standings.sortedByDescending { it.stats.blkPG }
            14 -> standings.sortedByDescending { it.stats.stlPG }
            15 -> standings.sortedByDescending { it.stats.rebPG }
            16 -> standings.sortedByDescending { it.stats.tovPG }
            17 -> standings.sortedByDescending { it.stats.pfPG }
            18 -> standings.sortedByDescending { it.stats.pfdPG }
            19 -> standings.sortedByDescending { it.stats.blkaPG }
            20 -> standings.sortedByDescending { it.stats.plusMinusPG }
            22 -> standings.sortedByDescending { CalendarUtil.formatHourMinuteToMinutes(it.stats.minPG) }
            25 -> standings.sortedByDescending { it.stats.winsPct }
            else -> standings // 경기당(PG) 데이터 이외에는 sort 할필요 없음
        }

        filterStandings()
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
                sortStandings()
            }
        } catch (e: Exception) {
            _displayDataState.emit(ApiFetchState.Error("데이터를 불러오는데 실패하였습니다."))
            Log.e("dsdf", e.localizedMessage ?: "error")
        }
    }
}






























