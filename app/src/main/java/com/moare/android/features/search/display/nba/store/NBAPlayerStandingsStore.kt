package com.moare.android.features.search.display.nba.store

import android.util.Log
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.CalendarUtil
import com.moare.android.features.search.display.common.store.BasePlayerStandingsStore
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.KeywordInfo
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStandingsDisplay
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStandingsDisplayModel
import com.moare.android.features.search.models.responsemodels.nba.NBAPlayerInfoResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBAPlayerStandingsResponseModel
import com.moare.android.features.search.networking.SearchClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface NBAPlayerStandingsAction {
    data object InitData : NBAPlayerStandingsAction
    data class SelectCategory(val index: Int, val category: String) : NBAPlayerStandingsAction
    data class ShowMoreStandings(val isUp: Boolean) : NBAPlayerStandingsAction
    data class ShowPlayerStats(val id: Int) : NBAPlayerStandingsAction
}

sealed interface NBAPlayerStandingsDelegate {
    data class ShowPlayerStats(val model: SportDecodableModel) : NBAPlayerStandingsDelegate
}

class NBAPlayerStandingsStore @AssistedInject constructor(
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: SportDecodableModel.NBAPlayerStandings,
    @Assisted val emitToParent: (NBAPlayerStandingsDelegate) -> Unit
) : BasePlayerStandingsStore<NBAPlayerStandingsAction, NBAPlayerStandingsResponseModel, NBAPlayerStandingsDisplayModel>(
    model.responseModel, model.displayModel, nameProvider
) {
    val dataItemHeight = 40.dp
    val itemWidth = 70.dp
    val firstCategoryItemWidth = 132.dp
    val firstCategoryItemHeight = 40.dp
    val secondCategoryItemHeight = 44.dp
    val firstCategoryFontSize = 15.sp
    val secondCategoryFontSize = 14.sp
    val dataFontSize = 15.sp
    val barWidth = 2.dp // TODO: Make it const
    private val fetchCategoryIndexList = listOf(5, 8, 11, 21, 23, 24, 26, 27)

    private var _filteredStandings = MutableStateFlow<List<NBAPlayerStandingsDisplay>>(emptyList())
    val filteredStandings: StateFlow<List<NBAPlayerStandingsDisplay>> = _filteredStandings

    var standings: List<NBAPlayerStandingsDisplay> = emptyList()

    @AssistedFactory
    interface Factory {
        fun create(
            model: SportDecodableModel.NBAPlayerStandings,
            emitToParent: (NBAPlayerStandingsDelegate) -> Unit
        ) : NBAPlayerStandingsStore
    }

    override fun send(action: NBAPlayerStandingsAction) {
        scope.launch {
            when (action) {
                is NBAPlayerStandingsAction.InitData -> initData()
                is NBAPlayerStandingsAction.SelectCategory -> selectCategory(action.index, action.category)
                is NBAPlayerStandingsAction.ShowMoreStandings -> addStandings(action.isUp)
                is NBAPlayerStandingsAction.ShowPlayerStats -> showPlayerStats(action.id)
            }
        }
    }

    override fun initData() {
        super.initData()

        // init with default value
        _filteredStandings.value = emptyList()
        standings = emptyList()

        // init data
        standings = displayModel.value.standings

        sortStandings()
    }

    override fun selectCategory(index: Int, category: String) {
        super.selectCategory(index, category)

        val previousSecondSelectedIndex = categorySelectedIndex.value

        when (previousSecondSelectedIndex) {
            in fetchCategoryIndexList -> fetchStandings(category) // 경기당(PG) 데이터가 아닌 카테고리에서 다른 카테고리를 눌렀을때는 무조건 fetch 해야함
            else -> when (index) { // 경기당(PG) 데이터인 카테고리에서 경기당 카테고리를 눌렀을때는 sort, 이외의 카테고리는 fetch
                in fetchCategoryIndexList -> fetchStandings(category)
                else -> sortStandings()
            }
        }
    }

    private fun sortStandings() {
        standings = when (categorySelectedIndex.value) {
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
            25 -> standings.sortedByDescending { it.stats.winsPct } // 승률 데이터도 경기당(PG) 데이터와 데이터가 같아 fetch 할 필요 없음
            else -> standings // 경기당(PG) 데이터 이외에는 sort 할 필요 없음
        }

        filterStandings()
    }

    override fun filterStandings() {
        // Get the first entity(player) matching in the standings.(Process works in the order of standings)
        val index = standings.indexOfFirst { player ->
            val entity = displayModel.value.entityInfo.find { it.playerId == player.player.personId }
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
        val startIndex = maxOf(0, (entityIndex.value ?: 0) - (rangeSize / 2) + 1)
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

        scope.launch {
            try {
                // TODO: Structure should be updated(Temporary code)
                val standingsKeyword = displayModel.value.keywords.first { it.id == "standings" }
                val keywords = listOf(standingsKeyword, Keyword(keyword = category, id = "", priority = 100))
                val entities = if (selectedEntity != null) listOf(selectedEntity!!) else emptyList()
                val keywordInfo = KeywordInfo(
                    keyword = category,
                    keywords = keywords,
                    entities = entities
                )

                val result = searchClient.fetchDataByKeyword(keywordInfo)

                if (result.data is SportDecodableModel.NBAPlayerStandings) {
                    _displayModel.value = result.data.displayModel
                    standings = result.data.displayModel.standings
                    sortStandings()
                }
            } catch (e: Exception) {
                _displayDataState.value = ApiFetchState.Error("데이터를 불러오는데 실패하였습니다.")
                Log.e("dsdf", e.localizedMessage ?: "error")
            }
        }
    }

    private fun showPlayerStats(id: Int) {
        scope.launch {
            // NOTE: For now nba player stats data in standings has all the stats, so doesn't has to fetchById like football.
            val player = responseModel.standings.find { player ->
                player.player.personId == id
            }
            val responseModel = NBAPlayerInfoResponseModel(info = player)

            val dataModel = SportDecodableModel.NBAPlayerStats(
                responseModel = responseModel,
                displayModel = ModelConverter.nbaPlayerStatsConverter(responseModel)
            )

            emitToParent(NBAPlayerStandingsDelegate.ShowPlayerStats(dataModel))
        }
    }
}






























