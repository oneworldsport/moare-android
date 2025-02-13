package com.moare.android.features.search.display.football.viewmodel

import android.util.Log
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.DayInfo
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.networking.SearchClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FBLeagueScheduleViewModel @Inject constructor(
    private val searchClient: SearchClient
) : MVIViewModel<FBLeagueScheduleViewModel.Intent, FBLeagueScheduleDisplayModel>() {
    /* ---------------------
       constants
       --------------------- */
    val itemHeight = 100.dp

    /* ---------------------
       data state
       --------------------- */
    private val _displayModel = MutableStateFlow<FBLeagueScheduleDisplayModel?>(null)
    val displayModel: StateFlow<FBLeagueScheduleDisplayModel?> = _displayModel

    private val _yearMonthList = MutableStateFlow<List<String>>(emptyList())
    val yearMonthList: StateFlow<List<String>> = _yearMonthList

    private val _days = MutableStateFlow<List<DayInfo>>(emptyList())
    val days: StateFlow<List<DayInfo>> = _days

    private val _filteredGames = MutableStateFlow<Map<Int, List<FBGame>>>(emptyMap())
    val filteredGames: StateFlow<Map<Int, List<FBGame>>> = _filteredGames

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

    private val _calendarScrollTrigger = MutableStateFlow(UUID.randomUUID().toString())
    val calendarScrollTrigger: StateFlow<String> = _calendarScrollTrigger

    private val _isAllResultOpened = MutableStateFlow(false)
    val isAllResultOpened: StateFlow<Boolean> = _isAllResultOpened

    private val _gameResultOpenedStateList = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val gameResultOpenedStateList: StateFlow<Map<Int, Boolean>> = _gameResultOpenedStateList

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data class SelectYearMonth(val yearMonth: String, val selectedIndex: Int) : Intent()
        data class SelectDay(val day: DayInfo, val selectedIndex: Int) : Intent()
        data object ToggleAllResult : Intent()
        data class UpdateResultOpenedState(val fixtureId: Int, val isOpened: Boolean) : Intent()
    }

    override fun send(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.SelectYearMonth -> selectYearMonth(intent.yearMonth, intent.selectedIndex)
                is Intent.SelectDay -> selectDay(intent.day, intent.selectedIndex)
                is Intent.ToggleAllResult -> toggleAllResult()
                is Intent.UpdateResultOpenedState -> updateResultOpenedState(intent.fixtureId, intent.isOpened)
            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: FBLeagueScheduleDisplayModel) {
        viewModelScope.launch {
            _displayModel.emit(displayModel)
            _yearMonthList.emit(displayModel.yearMonthList)
            _selectedYearMonth.emit(yearMonthList.value.firstOrNull() ?: "")

            setDays()
        }
    }

    /* ---------------------
       implements
       --------------------- */
    private suspend fun selectYearMonth(yearMonth: String, selectedIndex: Int) {
        _selectedYearMonth.emit(yearMonth)
        _selectedYearMonthIndex.emit(selectedIndex)

        fetchGames()
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

    private suspend fun setDays() {
        // set filtered games to each day
        val month = selectedYearMonth.value.split("/").last().toInt()
        var days = CalendarUtil.getDaysInMonth(2024, month)
        val isResultOpenedStateList = emptyMap<Int, Boolean>().toMutableMap()

        days = days.mapIndexedNotNull { index, day ->
            var newDay = day

            val games = displayModel.value?.games?.filter { game ->
                CalendarUtil.isSameDate(game.fixture.date, selectedYearMonth.value, day.day)
            }

            isResultOpenedStateList.putAll((games ?: emptyList()).associate { it.fixture.id to false })

            val newFilteredGames = filteredGames.value.toMutableMap()
            newFilteredGames[index] = games ?: emptyList()
            _filteredGames.emit(newFilteredGames)



            if (games?.isEmpty() == true) {
                newDay.isDataEmpty = true
            }

            newDay
        }

        _days.emit(days)

        // set default isOpened value as false to every games
        _gameResultOpenedStateList.emit(isResultOpenedStateList)

        // set first day that has games as selected
        for ((index, day) in days.withIndex()) {
            if (!day.isDataEmpty) {
                _selectedDay.emit(day)
                _selectedDayIndex.emit(index)
                _calendarScrollTrigger.emit(UUID.randomUUID().toString())
                break
            }
        }
    }

    private suspend fun fetchGames() {
        try {
            val selectedYearMonth = selectedYearMonth.value.split("/")
            val yearMonth = selectedYearMonth[0] + selectedYearMonth[1]

            // TODO: temporary leagueId
            val result = searchClient.fetchLeagueSchedule("39", yearMonth)

            if (result.data is SportDecodableModel.FBLeagueSchedule) {
                val data = result.data
                _displayModel.emit(data.displayModel)
                setDays()
            }
        } catch (e: Exception) {
            Log.e("dsdf", e.localizedMessage ?: "error")
        }
    }

    private suspend fun updateResultOpenedState(fixtureId: Int, isOpened: Boolean) {
        val newMap = gameResultOpenedStateList.value.toMutableMap()
        newMap[fixtureId] = isOpened
        _gameResultOpenedStateList.emit(newMap)
    }
}






















