package com.moare.android.features.search.display.football.viewmodel

import android.util.Log
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.store.BasePlayerStandingsStore
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.KeywordInfo
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplay
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplayModel
import com.moare.android.features.search.models.responsemodels.football.FBPlayerStandingsResponseModel
import com.moare.android.features.search.networking.SearchClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface FBPlayerStandingsAction {
    data object InitData : FBPlayerStandingsAction
    data class SelectCategory(val index: Int, val category: String) : FBPlayerStandingsAction
    data class ShowMoreStandings(val isUp: Boolean) : FBPlayerStandingsAction
    data class ShowPlayerStats(val id: Int) : FBPlayerStandingsAction
}

sealed interface FBPlayerStandingsDelegate {
    data class ShowPlayerStats(val model: SportDecodableModel.FBPlayerStats) : FBPlayerStandingsDelegate
}

class FBPlayerStandingsStore @AssistedInject constructor(
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: SportDecodableModel.FBPlayerStandings,
    @Assisted val emitToParent: (FBPlayerStandingsDelegate) -> Unit
) : BasePlayerStandingsStore<FBPlayerStandingsAction, FBPlayerStandingsResponseModel, FBPlayerStandingsDisplayModel>(
    model.responseModel, model.displayModel, nameProvider
) {
    val dataItemHeight = 40.dp
    val categoryItemHeight = 40.dp
    val firstCategoryItemWidth = 132.dp
    val itemWidth = 70.dp
    val barWidth = 2.dp
    val categoryFontSize = 15.sp
    val dataFontSize = 15.sp

    private var _filteredStandings = MutableStateFlow<List<FBPlayerStandingsDisplay>>(emptyList())
    val filteredStandings: StateFlow<List<FBPlayerStandingsDisplay>> = _filteredStandings

    var standings: List<FBPlayerStandingsDisplay> = emptyList()

    @AssistedFactory
    interface Factory {
        fun create(
            model: SportDecodableModel.FBPlayerStandings,
            emitToParent: (FBPlayerStandingsDelegate) -> Unit
        ) : FBPlayerStandingsStore
    }

    override fun send(action: FBPlayerStandingsAction) {
        when (action) {
            is FBPlayerStandingsAction.InitData -> initData()
            is FBPlayerStandingsAction.SelectCategory -> selectCategory(action.index, action.category)
            is FBPlayerStandingsAction.ShowMoreStandings -> addStandings(action.isUp)
            is FBPlayerStandingsAction.ShowPlayerStats -> showPlayerStats(action.id)
        }
    }

    override fun initData() {
        super.initData()

        // init with default value
        _filteredStandings.value = emptyList()
        standings = emptyList()

        // init data
        standings = displayModel.value.standings

        filterStandings()
    }

    override fun selectCategory(index: Int, category: String) {
        super.selectCategory(index, category)

        fetchStandings(category)
    }

    override fun filterStandings() {
        // Get the first entity(player) matching in the standings.(Process works in the order of standings)
        val index = standings.indexOfFirst { player ->
            val entity = displayModel.value.entityInfo.find { it.playerId == player.player.id }
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

    private fun showPlayerStats(id: Int) {
        scope.launch {
            // TODO: Has to add loading
            val result = searchClient.fetchById(
                season = displayModel.value.season,
                category = "football",
                dataType = "football_player_stats",
                leagueId = displayModel.value.leagueId,
                id = id.toString()
            )

            if (result.data is SportDecodableModel.FBPlayerStats) {
                emitToParent(FBPlayerStandingsDelegate.ShowPlayerStats(result.data))
            }
        }
    }
}