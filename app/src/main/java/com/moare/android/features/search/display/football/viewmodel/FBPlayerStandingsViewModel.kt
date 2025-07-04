package com.moare.android.features.search.display.football.viewmodel

import android.util.Log
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.viewmodel.BasePlayerStandingsViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.KeywordInfo
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplay
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplayModel
import com.moare.android.features.search.networking.SearchClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FBPlayerStandingsIntent {
    data class InitData(val displayModel: FBPlayerStandingsDisplayModel) : FBPlayerStandingsIntent()
    data class SelectFirstCategory(val index: Int) : FBPlayerStandingsIntent()
    data class SelectSecondCategory(val index: Int, val category: String) : FBPlayerStandingsIntent()
    data class ShowMoreStandings(val isUp: Boolean) : FBPlayerStandingsIntent()
}

@HiltViewModel
class FBPlayerStandingsViewModel @Inject constructor(
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider
) : BasePlayerStandingsViewModel<FBPlayerStandingsIntent, FBPlayerStandingsDisplayModel>(nameProvider) {
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
    private var _filteredStandings = MutableStateFlow<List<FBPlayerStandingsDisplay>>(emptyList())
    val filteredStandings: StateFlow<List<FBPlayerStandingsDisplay>> = _filteredStandings

    /* ---------------------
       etc
       --------------------- */
    var standings: List<FBPlayerStandingsDisplay> = emptyList()

    override fun send(intent: FBPlayerStandingsIntent) {
        when (intent) {
            is FBPlayerStandingsIntent.InitData -> initData(intent.displayModel)
            is FBPlayerStandingsIntent.SelectFirstCategory -> selectFirstCategory(intent.index)
            is FBPlayerStandingsIntent.SelectSecondCategory -> selectSecondCategory(intent.index, intent.category)
            is FBPlayerStandingsIntent.ShowMoreStandings -> addStandings(intent.isUp)
        }
    }

    override fun initData(displayModel: FBPlayerStandingsDisplayModel) {
        super.initData(displayModel)

        // init with default value
        _filteredStandings.value = emptyList()
        standings = emptyList()

        // init data
        standings = displayModel.standings

        filterStandings()
    }

    /* ---------------------
       implements
       --------------------- */
    override fun selectFirstCategory(index: Int) {
        super.selectFirstCategory(index)

        val attackCategoriesSize = StringConstants.Football.PLAYER_STANDINGS_ATTACK_CATEGORIES.size
        val defendCategoriesSize = StringConstants.Football.PLAYER_STANDINGS_DEFEND_CATEGORIES.size

        var secondCategory = "득점"

        when (index) {
            0 -> {
                _secondCategorySelectedIndex.value = 0
                secondCategory = "득점"
            }
            1 -> {
                _secondCategorySelectedIndex.value = attackCategoriesSize
                secondCategory = "태클 시도"
            }
            2 -> {
                _secondCategorySelectedIndex.value = attackCategoriesSize + defendCategoriesSize
                secondCategory = "패스 시도"
            }
        }

        _firstSelectedIndex.value = index

        fetchStandings(secondCategory)
    }

    override fun selectSecondCategory(index: Int, category: String) {
        super.selectSecondCategory(index, category)

        val attackCategories = StringConstants.Football.PLAYER_STANDINGS_ATTACK_CATEGORIES
        val defendCategories = StringConstants.Football.PLAYER_STANDINGS_DEFEND_CATEGORIES

        when (index) {
            in attackCategories.indices -> _firstSelectedIndex.value = 0
            in attackCategories.size until attackCategories.size + defendCategories.size -> _firstSelectedIndex.value = 1
            else -> _firstSelectedIndex.value = 2
        }

        fetchStandings(category)
    }

    override fun filterStandings() {
        // Get the first entity(player) matching in the standings.(Process works in the order of standings)
        val index = standings.indexOfFirst { player ->
            val entity = displayModel.value?.entityInfo?.find { it.playerId == player.player.id }
            entity?.let {
                selectedEntity = it
            }
            entity != null
        }

        if (index != -1) {
            _entityIndex.value = index
        }

        // Get 20 items based on index
        val rangeSize = 20
        val startIndex = maxOf(0, index - (rangeSize / 2) + 1)
        val endIndex = minOf(standings.size, startIndex + rangeSize)

        val newStandings = standings.subList(startIndex, endIndex)

        filteredStandingsEndIndex = endIndex
        _filteredStandingsStartIndex.value = startIndex

        // remove loading
        _displayDataState.value = ApiFetchState.Success

        // show 'filteredStandings'
        _filteredStandings.value = newStandings
    }

    override fun addStandings(isUp: Boolean) {
        // get 10 more standings
        if (isUp) {
            val newStartIndex = maxOf(0, filteredStandingsStartIndex.value - 10)

            if (newStartIndex == filteredStandingsStartIndex.value) {
                return
            }

            val newStandings = standings.subList(newStartIndex, filteredStandingsEndIndex)

            _filteredStandingsStartIndex.value = newStartIndex
            _filteredStandings.value = newStandings
        } else {
            val newEndIndex = minOf(standings.size, filteredStandingsEndIndex + 10)

            if (newEndIndex == filteredStandingsEndIndex) {
                return
            }

            val newStandings = standings.subList(filteredStandingsStartIndex.value, newEndIndex)

            filteredStandingsEndIndex = newEndIndex
            _filteredStandings.value = newStandings
        }
    }

    override fun fetchStandings(category: String) {
        super.fetchStandings(category)

        viewModelScope.launch {
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

                if (result.data is SportDecodableModel.FBPlayerStandings) {
                    _displayModel.value = result.data.displayModel
                    standings = result.data.displayModel.standings
                    filterStandings()
                }
            } catch (e: Exception) {
                _displayDataState.value = ApiFetchState.Error("데이터를 불러오는데 실패하였습니다.")
                Log.e("dsdf", e.localizedMessage ?: "error")
            }
        }
    }
}