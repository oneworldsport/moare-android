package com.moare.android.features.search.display.mlb.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.DayInfo
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.display.common.viewmodel.BaseScheduleViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBALeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBGame
import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.networking.SearchClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class MLBLeagueScheduleIntent {
    data class InitData(val displayModel: MLBLeagueScheduleDisplayModel) : MLBLeagueScheduleIntent()
    data class SelectYearMonth(val yearMonth: String, val selectedIndex: Int, val updateViewStack: (SportDecodableModel.MLBLeagueSchedule) -> Unit) : MLBLeagueScheduleIntent()
    data class SelectDay(val day: DayInfo, val selectedIndex: Int) : MLBLeagueScheduleIntent()
    data object ToggleAllResult : MLBLeagueScheduleIntent()
    data class UpdateResultOpenedState(val gameCode: String, val isOpened: Boolean) : MLBLeagueScheduleIntent()
    data class UpdateGamesData(
        val mlbLeagueScheduleData: SportDecodableModel.MLBLeagueSchedule,
        val mlbGameStatsData: SportDecodableModel.MLBGameStats,
        val updateViewStack: (SportDecodableModel.MLBLeagueSchedule) -> Unit
    ) : MLBLeagueScheduleIntent()
}

@HiltViewModel
class MLBLeagueScheduleViewModel @Inject constructor(
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider
) : BaseScheduleViewModel<MLBLeagueScheduleIntent, MLBLeagueScheduleDisplayModel>(nameProvider) {
    /* ---------------------
       data state
       --------------------- */
    private val _filteredGames = MutableStateFlow<Map<Int, List<MLBGame>>>(emptyMap())
    val filteredGames: StateFlow<Map<Int, List<MLBGame>>> = _filteredGames

    /* ---------------------
       ui state
       --------------------- */
    private val _gameResultOpenedStateList = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val gameResultOpenedStateList: StateFlow<Map<String, Boolean>> = _gameResultOpenedStateList

    override fun send(intent: MLBLeagueScheduleIntent) {
        when (intent) {
            is MLBLeagueScheduleIntent.InitData -> initData(intent.displayModel)
            is MLBLeagueScheduleIntent.SelectYearMonth -> selectYearMonth(intent.yearMonth, intent.selectedIndex, intent.updateViewStack)
            is MLBLeagueScheduleIntent.SelectDay -> selectDay(intent.day, intent.selectedIndex)
            is MLBLeagueScheduleIntent.ToggleAllResult -> toggleAllResult()
            is MLBLeagueScheduleIntent.UpdateResultOpenedState -> updateResultOpenedState(intent.gameCode, intent.isOpened)
            is MLBLeagueScheduleIntent.UpdateGamesData -> updateGamesData(intent.mlbLeagueScheduleData, intent.mlbGameStatsData, intent.updateViewStack)
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: MLBLeagueScheduleDisplayModel) {
        super.initData(displayModel)

        // init with default value
        _filteredGames.value = emptyMap()
        _gameResultOpenedStateList.value = emptyMap()

        // init data
        _yearMonthList.value = displayModel.yearMonthList

        // select default yearMonth
        displayModel.games.firstOrNull()?.gameInfo?.gameDate?.let {
            val defaultYearMonth = CalendarUtil.formatDate(it, TimeFormatType.YEAR_MONTH)
            val defaultYearMonthIndex = yearMonthList.value.withIndex().first { (_, value) -> value == defaultYearMonth }
            _selectedYearMonth.value = defaultYearMonth
            _selectedYearMonthIndex.value = defaultYearMonthIndex.index
            _yearMonthCalendarScrollTrigger.value = UUID.randomUUID().toString()
        }

        setDays(true)
    }

    /* ---------------------
       implements
       --------------------- */
    private fun selectYearMonth(yearMonth: String, selectedIndex: Int, updateViewStack: (SportDecodableModel.MLBLeagueSchedule) -> Unit) {
        _selectedYearMonth.value = yearMonth
        _selectedYearMonthIndex.value = selectedIndex

        fetchGames(updateViewStack)
    }

    override fun toggleAllResult() {
        val newState = !isAllResultOpened.value
        _isAllResultOpened.value = newState
        _gameResultOpenedStateList.value = gameResultOpenedStateList.value.mapValues { newState }
    }

    override fun setDays(isInit: Boolean) {
        // set filtered games to each day
        val yearMonth = selectedYearMonth.value.split("/")
        val year = ("20" + (yearMonth.firstOrNull() ?: "25")).toInt()
        val month = yearMonth.lastOrNull()?.toInt()

        month?.let {
            var days = CalendarUtil.getDaysInMonth(year, it)

            val isResultOpenedStateList = emptyMap<String, Boolean>().toMutableMap()
            val newFilteredGames = filteredGames.value.toMutableMap()

            days = days.mapIndexedNotNull { index, day ->
                var newDay = day

                val games = displayModel.value?.games?.filter { game ->
                    CalendarUtil.isSameDate(game.gameInfo.gameDate, selectedYearMonth.value, day.day)
                }

                isResultOpenedStateList.putAll((games ?: emptyList()).associate { (it.game.id) to isAllResultOpened.value })

                newFilteredGames[index] = games ?: emptyList()

                if (games?.isEmpty() == true) {
                    newDay.isDataEmpty = true
                }

                newDay
            }

            // ui operation order
            // 1. Set default 'isOpened' value as false to every games, before 'filteredGames' show.
            _gameResultOpenedStateList.value = isResultOpenedStateList

            // 2. Set days to days calendar.
            _days.value = days

            // 3. Move bar and scroll the days calendar.
            if (isInit) {
                // select default day
                val defaultDay = CalendarUtil.getDefaultDay(selectedYearMonth.value, days)
                defaultDay?.let {
                    _selectedDay.value = defaultDay.second
                    _selectedDayIndex.value = defaultDay.first
                    _dayCalendarScrollTrigger.value = UUID.randomUUID().toString()
                }
            } else {
                // select first day that has games
                for ((index, day) in days.withIndex()) {
                    if (!day.isDataEmpty) {
                        _selectedDay.value = day
                        _selectedDayIndex.value = index
                        _dayCalendarScrollTrigger.value = UUID.randomUUID().toString()
                        break
                    }
                }
            }

            // 4. Remove loading.
            _displayDataState.value = ApiFetchState.Success

            // 5. Show 'filteredGames'
            _filteredGames.value = newFilteredGames
        } // month?.let

        // added to prevent any gaps
        if (displayDataState.value != ApiFetchState.Success) {
            _displayDataState.value = ApiFetchState.Success
        }
    }

    private fun fetchGames(updateViewStack: (SportDecodableModel.MLBLeagueSchedule) -> Unit) {
        _displayDataState.value = ApiFetchState.Fetching

        viewModelScope.launch {
            try {
                val selectedYearMonth = selectedYearMonth.value.split("/")
                val yearMonth = selectedYearMonth[0] + selectedYearMonth[1]

                val entity = displayModel.value?.entityInfo?.firstOrNull() ?: EntityInfo(
                    entityId = 90001,
                    entityName = "NBA",
                    category = "basketball",
                    entityType = "league",
                    leagueId = 90001
                )

                val result = searchClient.fetchLeagueSchedule(entity, yearMonth)

                if (result.data is SportDecodableModel.MLBLeagueSchedule) {
                    val data = result.data
                    _displayModel.value = data.displayModel
                    updateViewStack(data)
                    setDays()
                }
            } catch (e: Exception) {
                _displayDataState.value = ApiFetchState.Error("데이터를 불러오는데 실패하였습니다.")
                Log.e("dsdf", e.localizedMessage ?: "error")
            }
        }
    }

    private fun updateResultOpenedState(gameCode: String, isOpened: Boolean) {
        val newMap = gameResultOpenedStateList.value.toMutableMap()
        newMap[gameCode] = isOpened
        _gameResultOpenedStateList.value = newMap
    }

    private fun updateGamesData(
        mlbLeagueScheduleData: SportDecodableModel.MLBLeagueSchedule,
        mlbGameStatsData: SportDecodableModel.MLBGameStats,
        updateViewStack: (SportDecodableModel.MLBLeagueSchedule) -> Unit
    ) {
        val newGames = mlbLeagueScheduleData.displayModel.games.map {
            if (it.game.id == mlbGameStatsData.displayModel.game.game.id) mlbGameStatsData.displayModel.game else it
        }

        var newData = mlbLeagueScheduleData
        newData.displayModel.games = newGames
        _displayModel.value = newData.displayModel

        val newFilteredGames = filteredGames.value.toMutableMap()
        newFilteredGames[selectedDayIndex.value] = newData.displayModel.games.filter { game ->
            CalendarUtil.isSameDate(game.gameInfo.gameDate, selectedYearMonth.value, selectedDayIndex.value + 1)
        }

        _filteredGames.value = newFilteredGames

        updateViewStack(newData)
    }
}