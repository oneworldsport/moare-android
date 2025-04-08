package com.moare.android.features.search.display.nba.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.DayInfo
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBALeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.networking.SearchClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NBALeagueScheduleViewModel @Inject constructor(
    private val searchClient: SearchClient
) : MVIViewModel<NBALeagueScheduleViewModel.Intent, NBALeagueScheduleDisplayModel>() {
    /* ---------------------
       constants
       --------------------- */

    /* ---------------------
       data state
       --------------------- */
    private val _displayModel = MutableStateFlow<NBALeagueScheduleDisplayModel?>(null)
    val displayModel: StateFlow<NBALeagueScheduleDisplayModel?> = _displayModel

    private val _displayDataState = MutableStateFlow<ApiFetchState>(ApiFetchState.Idle)
    val displayDataState: StateFlow<ApiFetchState> = _displayDataState

    private val _yearMonthList = MutableStateFlow<List<String>>(emptyList())
    val yearMonthList: StateFlow<List<String>> = _yearMonthList

    private val _days = MutableStateFlow<List<DayInfo>>(emptyList())
    val days: StateFlow<List<DayInfo>> = _days

    private val _filteredGames = MutableStateFlow<Map<Int, List<NBAGame>>>(emptyMap())
    val filteredGames: StateFlow<Map<Int, List<NBAGame>>> = _filteredGames

    /* ---------------------
       ui state
       --------------------- */
    private val _selectedYearMonth = MutableStateFlow("")
    val selectedYearMonth: StateFlow<String> = _selectedYearMonth

    private val _selectedDay = MutableStateFlow<DayInfo?>(null)
    val selectedDay: StateFlow<DayInfo?> = _selectedDay

    private val _selectedYearMonthIndex = MutableStateFlow(0)
    val selectedYearMonthIndex: StateFlow<Int> = _selectedYearMonthIndex

    private val _selectedDayIndex = MutableStateFlow(0)
    val selectedDayIndex: StateFlow<Int> = _selectedDayIndex

    private val _yearMonthCalendarScrollTrigger = MutableStateFlow(UUID.randomUUID().toString())
    val yearMonthCalendarScrollTrigger: StateFlow<String> = _yearMonthCalendarScrollTrigger

    private val _dayCalendarScrollTrigger = MutableStateFlow(UUID.randomUUID().toString())
    val dayCalendarScrollTrigger: StateFlow<String> = _dayCalendarScrollTrigger

    private val _isAllResultOpened = MutableStateFlow(false)
    val isAllResultOpened: StateFlow<Boolean> = _isAllResultOpened

    private val _gameResultOpenedStateList = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val gameResultOpenedStateList: StateFlow<Map<String, Boolean>> = _gameResultOpenedStateList

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data class InitData(val displayModel: NBALeagueScheduleDisplayModel) : Intent()
        data class SelectYearMonth(val yearMonth: String, val selectedIndex: Int, val updateViewStack: (SportDecodableModel.NBALeagueSchedule) -> Unit) : Intent()
        data class SelectDay(val day: DayInfo, val selectedIndex: Int) : Intent()
        data object ToggleAllResult : Intent()
        data class UpdateResultOpenedState(val gameCode: String, val isOpened: Boolean) : Intent()
        data class UpdateGamesData(
            val nbaLeagueScheduleData: SportDecodableModel.NBALeagueSchedule,
            val nbaGameStatsData: SportDecodableModel.NBAGameStats,
            val updateViewStack: (SportDecodableModel.NBALeagueSchedule) -> Unit
        ) : Intent()
    }

    override fun send(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.InitData -> initData(intent.displayModel)
                is Intent.SelectYearMonth -> selectYearMonth(intent.yearMonth, intent.selectedIndex, intent.updateViewStack)
                is Intent.SelectDay -> selectDay(intent.day, intent.selectedIndex)
                is Intent.ToggleAllResult -> toggleAllResult()
                is Intent.UpdateResultOpenedState -> updateResultOpenedState(intent.gameCode, intent.isOpened)
                is Intent.UpdateGamesData -> updateGamesData(intent.nbaLeagueScheduleData, intent.nbaGameStatsData, intent.updateViewStack)
            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: NBALeagueScheduleDisplayModel) {
        viewModelScope.launch {
            // init with default value
            _displayModel.emit(null)
            _displayDataState.emit(ApiFetchState.Idle)
            _yearMonthList.emit(emptyList())
            _days.emit(emptyList())
            _filteredGames.emit(emptyMap())
            _selectedYearMonth.emit("")
            _selectedDay.emit(null)
            _selectedYearMonthIndex.emit(0)
            _selectedDayIndex.emit(0)
            _isAllResultOpened.emit(false)
            _gameResultOpenedStateList.emit(emptyMap())

            // init data
            _displayModel.emit(displayModel)
            _yearMonthList.emit(displayModel.yearMonthList)

            // select default yearMonth
            displayModel.games.firstOrNull()?.gameSummary?.date?.let {
                val defaultYearMonth = CalendarUtil.formatDate(it, TimeFormatType.YEAR_MONTH)
                val defaultYearMonthIndex = yearMonthList.value.withIndex().first{ (_, value) -> value == defaultYearMonth }
                _selectedYearMonth.emit(defaultYearMonth)
                _selectedYearMonthIndex.emit(defaultYearMonthIndex.index)
                _yearMonthCalendarScrollTrigger.emit(UUID.randomUUID().toString())
            }

            setDays(true)
        }
    }

    /* ---------------------
       implements
       --------------------- */
    private suspend fun selectYearMonth(yearMonth: String, selectedIndex: Int, updateViewStack: (SportDecodableModel.NBALeagueSchedule) -> Unit) {
        _selectedYearMonth.emit(yearMonth)
        _selectedYearMonthIndex.emit(selectedIndex)

        fetchGames(updateViewStack)
    }

    private suspend fun selectDay(day: DayInfo, selectedIndex: Int) {
        _selectedDay.emit(day)
        _selectedDayIndex.emit(selectedIndex)
    }

    private suspend fun toggleAllResult() {
        val newState = !isAllResultOpened.value
        _isAllResultOpened.emit(newState)
        _gameResultOpenedStateList.emit(gameResultOpenedStateList.value.mapValues { newState })
    }

    private suspend fun setDays(isInit: Boolean = false) {
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
                    if (game.gameSummary != null) {
                        CalendarUtil.isSameDate(game.gameSummary.date, selectedYearMonth.value, day.day)
                    } else {
                        false
                    }
                }

                isResultOpenedStateList.putAll((games ?: emptyList()).associate { (it.gameSummary?.gameCode ?: "") to isAllResultOpened.value })

                newFilteredGames[index] = games ?: emptyList()

                if (games?.isEmpty() == true) {
                    newDay.isDataEmpty = true
                }

                newDay
            }

            // ui operation order
            // 1. Set default 'isOpened' value as false to every games, before 'filteredGames' show.
            _gameResultOpenedStateList.emit(isResultOpenedStateList)

            // 2. Set days to days calendar.
            _days.emit(days)

            // 3. Move bar and scroll the days calendar.
            if (isInit) {
                // select default day
                val defaultDay = CalendarUtil.getDefaultDay(selectedYearMonth.value, days)
                defaultDay?.let {
                    _selectedDay.emit(defaultDay.second)
                    _selectedDayIndex.emit(defaultDay.first)
                    _dayCalendarScrollTrigger.emit(UUID.randomUUID().toString())
                }
            } else {
                // select first day that has games
                for ((index, day) in days.withIndex()) {
                    if (!day.isDataEmpty) {
                        _selectedDay.emit(day)
                        _selectedDayIndex.emit(index)
                        _dayCalendarScrollTrigger.emit(UUID.randomUUID().toString())
                        break
                    }
                }
            }

            // 4. Remove loading.
            _displayDataState.emit(ApiFetchState.Success)

            // 5. Show 'filteredGames'
            _filteredGames.emit(newFilteredGames)
        } // month?.let

        // added to prevent any gaps
        if (displayDataState.value != ApiFetchState.Success) {
            _displayDataState.emit(ApiFetchState.Success)
        }
    }

    private suspend fun fetchGames(updateViewStack: (SportDecodableModel.NBALeagueSchedule) -> Unit) {
        _displayDataState.emit(ApiFetchState.Fetching)

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

            if (result.data is SportDecodableModel.NBALeagueSchedule) {
                val data = result.data
                _displayModel.emit(data.displayModel)
                updateViewStack(data)
                setDays()
            }
        } catch (e: Exception) {
            _displayDataState.emit(ApiFetchState.Error("데이터를 불러오는데 실패하였습니다."))
            Log.e("dsdf", e.localizedMessage ?: "error")
        }
    }

    private suspend fun updateResultOpenedState(gameCode: String, isOpened: Boolean) {
        val newMap = gameResultOpenedStateList.value.toMutableMap()
        newMap[gameCode] = isOpened
        _gameResultOpenedStateList.emit(newMap)
    }

    private suspend fun updateGamesData(
        nbaLeagueScheduleData: SportDecodableModel.NBALeagueSchedule,
        nbaGameStatsData: SportDecodableModel.NBAGameStats,
        updateViewStack: (SportDecodableModel.NBALeagueSchedule) -> Unit
    ) {
        val newGames = nbaLeagueScheduleData.displayModel.games.map {
            if (it.gameSummary?.gameCode == nbaGameStatsData.displayModel.game.gameSummary?.gameCode) nbaGameStatsData.displayModel.game else it
        }

        var newData = nbaLeagueScheduleData
        newData.displayModel.games = newGames
        _displayModel.emit(newData.displayModel)

        val newFilteredGames = filteredGames.value.toMutableMap()
        newFilteredGames[selectedDayIndex.value] = newData.displayModel.games.filter { game ->
            if (game.gameSummary != null) {
                CalendarUtil.isSameDate(game.gameSummary.date, selectedYearMonth.value, selectedDayIndex.value + 1)
            } else {
                false
            }
        }

        _filteredGames.emit(newFilteredGames)

        updateViewStack(newData)
    }
}
































