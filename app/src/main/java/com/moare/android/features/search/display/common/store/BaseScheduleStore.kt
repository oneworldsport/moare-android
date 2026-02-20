package com.moare.android.features.search.display.common.store

import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.DayInfo
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

abstract class BaseScheduleStore<A, T: SportDisplayModel>(
    private val initial: T,
    private val nameProvider: TranslatedNameProvider
) {
    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    protected val _displayModel = MutableStateFlow(initial)
    val displayModel: StateFlow<T> = _displayModel

    protected val _displayDataState = MutableStateFlow<ApiFetchState>(ApiFetchState.Idle)
    val displayDataState: StateFlow<ApiFetchState> = _displayDataState

    protected val _teamNameDic = MutableStateFlow<Map<String, String>>(emptyMap())
    val teamNameDic: StateFlow<Map<String, String>> = _teamNameDic

    protected val _yearMonthList = MutableStateFlow<List<String>>(emptyList())
    val yearMonthList: StateFlow<List<String>> = _yearMonthList

    protected val _days = MutableStateFlow<List<DayInfo>>(emptyList())
    val days: StateFlow<List<DayInfo>> = _days

    protected val _selectedYearMonth = MutableStateFlow("")
    val selectedYearMonth: StateFlow<String> = _selectedYearMonth

    protected val _selectedMonth = MutableStateFlow(0)
    val selectedMonth: StateFlow<Int> = _selectedMonth

    protected val _selectedDay = MutableStateFlow<DayInfo?>(null)
    val selectedDay: StateFlow<DayInfo?> = _selectedDay

    protected val _selectedYearMonthIndex = MutableStateFlow(0)
    val selectedYearMonthIndex: StateFlow<Int> = _selectedYearMonthIndex

    protected val _selectedDayIndex = MutableStateFlow(0)
    val selectedDayIndex: StateFlow<Int> = _selectedDayIndex

    protected val _yearMonthCalendarScrollTrigger = MutableStateFlow(UUID.randomUUID().toString())
    val yearMonthCalendarScrollTrigger: StateFlow<String> = _yearMonthCalendarScrollTrigger

    protected val _dayCalendarScrollTrigger = MutableStateFlow(UUID.randomUUID().toString())
    val dayCalendarScrollTrigger: StateFlow<String> = _dayCalendarScrollTrigger

    protected val _isAllResultOpened = MutableStateFlow(false)
    val isAllResultOpened: StateFlow<Boolean> = _isAllResultOpened

    abstract fun send(action: A)

    open fun initData() {
        // init with default value
        _displayDataState.value = ApiFetchState.Idle
        _yearMonthList.value = emptyList()
        _days.value = emptyList()

        _selectedYearMonth.value = ""
        _selectedMonth.value = 0
        _selectedDay.value = null
        _selectedYearMonthIndex.value = 0
        _selectedDayIndex.value = 0
        _isAllResultOpened.value = false

        loadDictionaries(displayModel.value.leagueId)
    }

    private fun loadDictionaries(leagueId: Int) {
        _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.FOOTBALL_TEAM_DIC)

        when (leagueId) {
            Constants.Ids.NBA -> {
                _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.NBA_TEAM_DIC)
            }
            Constants.Ids.KBO -> {
                _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.KBO_TEAM_DIC)
            }
            Constants.Ids.MLB -> {
                _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.MLB_TEAM_DIC)
            }
            else -> {}
        }
    }

    open fun selectDay(day: DayInfo, selectedIndex: Int) {
        _selectedDay.value = day
        _selectedDayIndex.value = selectedIndex
    }

    open fun setDays(isInit: Boolean = false) {}

    open fun selectYearMonth(yearMonth: String, selectedIndex: Int, isInit: Boolean) {
        _selectedYearMonth.value = yearMonth
        _selectedYearMonthIndex.value = selectedIndex

        val monthStr = yearMonth.split("/").lastOrNull()
        _selectedMonth.value = monthStr?.toIntOrNull() ?: 0
    }

    open fun setDefaultYearMonth(date: String) {
        val defaultYearMonth = CalendarUtil.formatDate(date, TimeFormatType.YEAR_MONTH)
        val defaultYearMonthIndex = yearMonthList.value.withIndex().first{ (_, value) -> value == defaultYearMonth }.index

        selectYearMonth(defaultYearMonth, defaultYearMonthIndex, true)

        _yearMonthCalendarScrollTrigger.value = UUID.randomUUID().toString()
    }

    abstract fun toggleAllResult()

    open fun dispose() {
        scope.cancel()
    }
}