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
import com.moare.android.features.search.display.common.viewmodel.BaseScheduleViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.models.models.football.FBGameForSchedule
import com.moare.android.features.search.models.models.football.FBGameInfoForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import com.moare.android.features.search.networking.SearchClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class FBLeagueScheduleIntent {
    data class InitData(val displayModel: FBLeagueScheduleDisplayModel) : FBLeagueScheduleIntent()
    data class SelectYearMonth(val yearMonth: String, val selectedIndex: Int, val updateViewStack: (SportDecodableModel.FBLeagueSchedule) -> Unit) : FBLeagueScheduleIntent()
    data class SelectDay(val day: DayInfo, val selectedIndex: Int) : FBLeagueScheduleIntent()
    data object ToggleAllResult : FBLeagueScheduleIntent()
    data class UpdateResultOpenedState(val gameId: String, val isOpened: Boolean) : FBLeagueScheduleIntent()
    data class UpdateGamesData(
        val fbLeagueScheduleData: SportDecodableModel.FBLeagueSchedule,
        val fbGameStatsData: SportDecodableModel.FBGameStats,
        val updateViewStack: (SportDecodableModel.FBLeagueSchedule) -> Unit
    ) : FBLeagueScheduleIntent()
}

@HiltViewModel
class FBLeagueScheduleViewModel @Inject constructor(
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider
) : BaseScheduleViewModel<FBLeagueScheduleIntent, FBLeagueScheduleDisplayModel>(nameProvider) {
    /* ---------------------
       data state
       --------------------- */
    private val _filteredGames = MutableStateFlow<Map<Int, List<FBGameForSchedule>>>(emptyMap())
    val filteredGames: StateFlow<Map<Int, List<FBGameForSchedule>>> = _filteredGames

    /* ---------------------
       ui state
       --------------------- */
    private val _gameResultOpenedStateList = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val gameResultOpenedStateList: StateFlow<Map<String, Boolean>> = _gameResultOpenedStateList


    override fun send(intent: FBLeagueScheduleIntent) {
        when (intent) {
            is FBLeagueScheduleIntent.InitData -> initData(intent.displayModel)
            is FBLeagueScheduleIntent.SelectYearMonth -> selectYearMonth(intent.yearMonth, intent.selectedIndex, intent.updateViewStack)
            is FBLeagueScheduleIntent.SelectDay -> selectDay(intent.day, intent.selectedIndex)
            is FBLeagueScheduleIntent.ToggleAllResult -> toggleAllResult()
            is FBLeagueScheduleIntent.UpdateResultOpenedState -> updateResultOpenedState(intent.gameId, intent.isOpened)
            is FBLeagueScheduleIntent.UpdateGamesData -> updateGamesData(intent.fbLeagueScheduleData, intent.fbGameStatsData, intent.updateViewStack)
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: FBLeagueScheduleDisplayModel) {
        super.initData(displayModel)

        // init with default value
        _filteredGames.value = emptyMap()
        _gameResultOpenedStateList.value = emptyMap()

        // init data
        _yearMonthList.value = displayModel.yearMonthList

        // select default yearMonth
        displayModel.games.firstOrNull()?.date?.let {
            val defaultYearMonth = CalendarUtil.formatDate(it, TimeFormatType.YEAR_MONTH)
            val defaultYearMonthIndex = yearMonthList.value.withIndex().first{ (_, value) -> value == defaultYearMonth }
            _selectedYearMonth.value = defaultYearMonth
            _selectedYearMonthIndex.value = defaultYearMonthIndex.index
            _yearMonthCalendarScrollTrigger.value = UUID.randomUUID().toString()
        }

        setDays(true)
    }

    /* ---------------------
       implements
       --------------------- */
    private fun selectYearMonth(yearMonth: String, selectedIndex: Int, updateViewStack: (SportDecodableModel.FBLeagueSchedule) -> Unit) {
        _selectedYearMonth.value = yearMonth
        _selectedYearMonthIndex.value = selectedIndex

        when (displayModel.value?.scheduleType) {
            ScheduleType.LEAGUE -> { fetchGames(updateViewStack) }
            ScheduleType.TEAM -> { setDays() }
            else -> {}
        }
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
                    CalendarUtil.isSameDate(game.date, selectedYearMonth.value, day.day)
                }

                isResultOpenedStateList.putAll((games ?: emptyList()).associate { it.gameId to isAllResultOpened.value })

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

    private fun fetchGames(updateViewStack: (SportDecodableModel.FBLeagueSchedule) -> Unit) {
        _displayDataState.value = ApiFetchState.Fetching

        viewModelScope.launch {
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

                val result = searchClient.fetchLeagueSchedule(entity, displayModel.value?.season, yearMonth)

                if (result.data is SportDecodableModel.FBLeagueSchedule) {
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

    private fun updateResultOpenedState(gameId: String, isOpened: Boolean) {
        val newMap = gameResultOpenedStateList.value.toMutableMap()
        newMap[gameId] = isOpened
        _gameResultOpenedStateList.value = newMap
    }

    private fun updateGamesData(
        fbLeagueScheduleData: SportDecodableModel.FBLeagueSchedule,
        fbGameStatsData: SportDecodableModel.FBGameStats,
        updateViewStack: (SportDecodableModel.FBLeagueSchedule) -> Unit
    ) {
        val game = fbGameStatsData.displayModel.game
        val newGames = fbLeagueScheduleData.displayModel.games.map {
            if (it.gameId == fbGameStatsData.displayModel.game.fixture.id.toString()) {
                ModelConverter().fbGameToGameScheduleConverter(game)
            } else it
        }

        var newData = fbLeagueScheduleData
        newData.displayModel.games = newGames
        _displayModel.value = newData.displayModel

        val newFilteredGames = filteredGames.value.toMutableMap()
        newFilteredGames[selectedDayIndex.value] = newData.displayModel.games.filter { game ->
            CalendarUtil.isSameDate(game.date, selectedYearMonth.value, selectedDayIndex.value + 1)
        }

        _filteredGames.value = newFilteredGames

        updateViewStack(newData)
    }
}






















