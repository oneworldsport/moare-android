package com.moare.android.features.search.display.football.viewmodel

import android.util.Log
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.DayInfo
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.networking.SearchClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FBLeagueScheduleViewModel @Inject constructor(
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider
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

    private val _displayDataState = MutableStateFlow<ApiFetchState>(ApiFetchState.Idle)
    val displayDataState: StateFlow<ApiFetchState> = _displayDataState

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

    private val _yearMonthCalendarScrollTrigger = MutableStateFlow(UUID.randomUUID().toString())
    val yearMonthCalendarScrollTrigger: StateFlow<String> = _yearMonthCalendarScrollTrigger

    private val _dayCalendarScrollTrigger = MutableStateFlow(UUID.randomUUID().toString())
    val dayCalendarScrollTrigger: StateFlow<String> = _dayCalendarScrollTrigger

    private val _isAllResultOpened = MutableStateFlow(false)
    val isAllResultOpened: StateFlow<Boolean> = _isAllResultOpened

    private val _gameResultOpenedStateList = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val gameResultOpenedStateList: StateFlow<Map<Int, Boolean>> = _gameResultOpenedStateList

    /* ---------------------
       etc
       --------------------- */
    var teamNameDictionary: Map<String, String> = emptyMap()

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data class InitData(val displayModel: FBLeagueScheduleDisplayModel) : Intent()
        data class SelectYearMonth(val yearMonth: String, val selectedIndex: Int, val updateViewStack: (SportDecodableModel.FBLeagueSchedule) -> Unit) : Intent()
        data class SelectDay(val day: DayInfo, val selectedIndex: Int) : Intent()
        data object ToggleAllResult : Intent()
        data class UpdateResultOpenedState(val fixtureId: Int, val isOpened: Boolean) : Intent()
        data class UpdateGamesData(
            val fbLeagueScheduleData: SportDecodableModel.FBLeagueSchedule,
            val fbGameStatsData: SportDecodableModel.FBGameStats,
            val updateViewStack: (SportDecodableModel.FBLeagueSchedule) -> Unit
        ) : Intent()
    }

    override fun send(intent: Intent) {
        viewModelScope.launch {
            when (intent) {
                is Intent.InitData -> initData(intent.displayModel)
                is Intent.SelectYearMonth -> selectYearMonth(intent.yearMonth, intent.selectedIndex, intent.updateViewStack)
                is Intent.SelectDay -> selectDay(intent.day, intent.selectedIndex)
                is Intent.ToggleAllResult -> toggleAllResult()
                is Intent.UpdateResultOpenedState -> updateResultOpenedState(intent.fixtureId, intent.isOpened)
                is Intent.UpdateGamesData -> updateGamesData(intent.fbLeagueScheduleData, intent.fbGameStatsData, intent.updateViewStack)
            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: FBLeagueScheduleDisplayModel) {
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

            when (displayModel.leagueId) {
                Constants.Ids.EPL -> {
                    teamNameDictionary = nameProvider.getDictionary(Constants.Keys.EPL_TEAM_DIC)
                }
                Constants.Ids.LALIGA -> {
                    teamNameDictionary = nameProvider.getDictionary(Constants.Keys.LALIGA_TEAM_DIC)
                }
                Constants.Ids.BUNDESLIGA -> {
                    teamNameDictionary = nameProvider.getDictionary(Constants.Keys.BUNDESLIGA_TEAM_DIC)
                }
                Constants.Ids.LIGUE1 -> {
                    teamNameDictionary = nameProvider.getDictionary(Constants.Keys.LIGUE1_TEAM_DIC)
                }
                else -> {}
            }

            // select default yearMonth
            displayModel.games.firstOrNull()?.fixture?.date?.let {
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
    private suspend fun selectYearMonth(yearMonth: String, selectedIndex: Int, updateViewStack: (SportDecodableModel.FBLeagueSchedule) -> Unit) {
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

            val isResultOpenedStateList = emptyMap<Int, Boolean>().toMutableMap()
            val newFilteredGames = filteredGames.value.toMutableMap()

            days = days.mapIndexedNotNull { index, day ->
                var newDay = day

                val games = displayModel.value?.games?.filter { game ->
                    CalendarUtil.isSameDate(game.fixture.date, selectedYearMonth.value, day.day)
                }

                isResultOpenedStateList.putAll((games ?: emptyList()).associate { it.fixture.id to isAllResultOpened.value })

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

    private suspend fun fetchGames(updateViewStack: (SportDecodableModel.FBLeagueSchedule) -> Unit) {
        _displayDataState.emit(ApiFetchState.Fetching)

        try {
            val selectedYearMonth = selectedYearMonth.value.split("/")
            val yearMonth = selectedYearMonth[0] + selectedYearMonth[1]

            val entity = displayModel.value?.entityInfo?.firstOrNull() ?: EntityInfo(
                entityId = 39,
                entityName = "프리미어리그",
                category = "football",
                entityType = "league",
                leagueId = 39
            )

            val result = searchClient.fetchLeagueSchedule(entity, yearMonth)

            if (result.data is SportDecodableModel.FBLeagueSchedule) {
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

    private suspend fun updateResultOpenedState(fixtureId: Int, isOpened: Boolean) {
        val newMap = gameResultOpenedStateList.value.toMutableMap()
        newMap[fixtureId] = isOpened
        _gameResultOpenedStateList.emit(newMap)
    }

    private suspend fun updateGamesData(
        fbLeagueScheduleData: SportDecodableModel.FBLeagueSchedule,
        fbGameStatsData: SportDecodableModel.FBGameStats,
        updateViewStack: (SportDecodableModel.FBLeagueSchedule) -> Unit
    ) {
        val newGames = fbLeagueScheduleData.displayModel.games.map {
            if (it.fixture.id == fbGameStatsData.displayModel.game.fixture.id) fbGameStatsData.displayModel.game else it
        }

        var newData = fbLeagueScheduleData
        newData.displayModel.games = newGames
        _displayModel.emit(newData.displayModel)

        val newFilteredGames = filteredGames.value.toMutableMap()
        newFilteredGames[selectedDayIndex.value] = newData.displayModel.games.filter { game ->
            CalendarUtil.isSameDate(game.fixture.date, selectedYearMonth.value, selectedDayIndex.value + 1)
        }

        _filteredGames.emit(newFilteredGames)

        updateViewStack(newData)
    }
}






















