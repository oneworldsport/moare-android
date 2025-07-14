package com.moare.android.features.search.display.kbo.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.DayInfo
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.display.common.viewmodel.BaseScheduleViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOLeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOGame
import com.moare.android.features.search.models.models.kbo.KBOGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import com.moare.android.features.search.networking.SearchClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class KBOLeagueScheduleIntent {
    data class InitData(val displayModel: KBOLeagueScheduleDisplayModel) : KBOLeagueScheduleIntent()
    data class SelectYearMonth(val yearMonth: String, val selectedIndex: Int, val updateViewStack: (SportDecodableModel.KBOLeagueSchedule) -> Unit) : KBOLeagueScheduleIntent()
    data class SelectDay(val day: DayInfo, val selectedIndex: Int) : KBOLeagueScheduleIntent()
    data object ToggleAllResult : KBOLeagueScheduleIntent()
    data class UpdateResultOpenedState(val itemKey: String, val isOpened: Boolean) : KBOLeagueScheduleIntent() // NOTE: 더블헤더가 있는 날에 취소된 경기가 있으면 gameId가 같은 경우가 있어 gameId 대신에 itemKey를 사용
    data class UpdateGamesData(
        val kboLeagueScheduleData: SportDecodableModel.KBOLeagueSchedule,
        val kboGameStatsData: SportDecodableModel.KBOGameStats,
        val updateViewStack: (SportDecodableModel.KBOLeagueSchedule) -> Unit
    ) : KBOLeagueScheduleIntent()
}

@HiltViewModel
class KBOLeagueScheduleViewModel @Inject constructor(
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider
) : BaseScheduleViewModel<KBOLeagueScheduleIntent, KBOLeagueScheduleDisplayModel>(nameProvider) {
    /* ---------------------
       data state
       --------------------- */
    private val _filteredGames = MutableStateFlow<Map<Int, List<KBOGameForSchedule>>>(emptyMap())
    val filteredGames: StateFlow<Map<Int, List<KBOGameForSchedule>>> = _filteredGames

    /* ---------------------
       ui state
       --------------------- */
    private val _gameResultOpenedStateList = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val gameResultOpenedStateList: StateFlow<Map<String, Boolean>> = _gameResultOpenedStateList

    override fun send(intent: KBOLeagueScheduleIntent) {
        when (intent) {
            is KBOLeagueScheduleIntent.InitData -> initData(intent.displayModel)
            is KBOLeagueScheduleIntent.SelectYearMonth -> selectYearMonth(intent.yearMonth, intent.selectedIndex, intent.updateViewStack)
            is KBOLeagueScheduleIntent.SelectDay -> selectDay(intent.day, intent.selectedIndex)
            is KBOLeagueScheduleIntent.ToggleAllResult -> toggleAllResult()
            is KBOLeagueScheduleIntent.UpdateResultOpenedState -> updateResultOpenedState(intent.itemKey, intent.isOpened)
            is KBOLeagueScheduleIntent.UpdateGamesData -> updateGamesData(intent.kboLeagueScheduleData, intent.kboGameStatsData, intent.updateViewStack)
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: KBOLeagueScheduleDisplayModel) {
        super.initData(displayModel)

        // init with default value
        _filteredGames.value = emptyMap()
        _gameResultOpenedStateList.value = emptyMap()

        // init data
        _yearMonthList.value = displayModel.yearMonthList

        // select default yearMonth
        displayModel.games.firstOrNull()?.date?.let {
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
    private fun selectYearMonth(yearMonth: String, selectedIndex: Int, updateViewStack: (SportDecodableModel.KBOLeagueSchedule) -> Unit) {
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

                isResultOpenedStateList.putAll((games ?: emptyList()).associate { (it.itemKey) to isAllResultOpened.value })

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

    private fun fetchGames(updateViewStack: (SportDecodableModel.KBOLeagueSchedule) -> Unit) {
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

                if (result.data is SportDecodableModel.KBOLeagueSchedule) {
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

    private fun updateResultOpenedState(itemKey: String, isOpened: Boolean) {
        val newMap = gameResultOpenedStateList.value.toMutableMap()
        newMap[itemKey] = isOpened
        _gameResultOpenedStateList.value = newMap
    }

    private fun updateGamesData(
        kboLeagueScheduleData: SportDecodableModel.KBOLeagueSchedule,
        kboGameStatsData: SportDecodableModel.KBOGameStats,
        updateViewStack: (SportDecodableModel.KBOLeagueSchedule) -> Unit
    ) {
        val game = kboGameStatsData.displayModel.game
        val itemKey = "${game.gameInfo?.date?.split("+")?.firstOrNull() ?: ""}#${game.gameInfo?.gameId ?: ""}"
        val newGames = kboLeagueScheduleData.displayModel.games.map {
            if (it.itemKey == itemKey) ModelConverter().kboGameToGameScheduleConverter(game) else it
        }

        var newData = kboLeagueScheduleData
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