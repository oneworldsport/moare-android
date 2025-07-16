package com.moare.android.features.search.display.common.viewmodel

import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.DayInfo
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

abstract class BaseScheduleViewModel<I, T>(
    private val nameProvider: TranslatedNameProvider
) : MVIViewModel<I, T>() {
    /* ---------------------
       data state
       --------------------- */
    protected val _displayModel = MutableStateFlow<T?>(null)
    val displayModel: StateFlow<T?> = _displayModel

    protected val _displayDataState = MutableStateFlow<ApiFetchState>(ApiFetchState.Idle)
    val displayDataState: StateFlow<ApiFetchState> = _displayDataState

    protected val _yearMonthList = MutableStateFlow<List<String>>(emptyList())
    val yearMonthList: StateFlow<List<String>> = _yearMonthList

    protected val _days = MutableStateFlow<List<DayInfo>>(emptyList())
    val days: StateFlow<List<DayInfo>> = _days

    /* ---------------------
       ui state
       --------------------- */
    protected val _selectedYearMonth = MutableStateFlow("")
    val selectedYearMonth: StateFlow<String> = _selectedYearMonth

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

    /* ---------------------
       etc
       --------------------- */
    var teamNameDictionary: Map<String, String> = emptyMap()

    override fun initData(displayModel: T) {
        // init with default value
        _displayDataState.value = ApiFetchState.Idle
        _yearMonthList.value = emptyList()
        _days.value = emptyList()

        _selectedYearMonth.value = ""
        _selectedDay.value = null
        _selectedYearMonthIndex.value = 0
        _selectedDayIndex.value = 0
        _isAllResultOpened.value = false

        // init data
        _displayModel.value = displayModel

        if (displayModel is SportDisplayModel) {
            loadDictionaries(displayModel.leagueId)
        }
    }

    private fun loadDictionaries(leagueId: Int) {
        when (leagueId) {
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
            Constants.Ids.SERIEA -> {
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.SERIEA_TEAM_DIC)
            }
            Constants.Ids.NBA -> {
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.NBA_TEAM_DIC)
            }
            Constants.Ids.KBO -> {
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.KBO_TEAM_DIC)
            }
            Constants.Ids.MLB -> {
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.MLB_TEAM_DIC)
            }
            else -> {}
        }
    }

    open fun selectDay(day: DayInfo, selectedIndex: Int) {
        _selectedDay.value = day
        _selectedDayIndex.value = selectedIndex
    }

    open fun setDays(isInit: Boolean = false) {}

    open fun setDefaultYearMonth(date: String) {
        val defaultYearMonth = CalendarUtil.formatDate(date, TimeFormatType.YEAR_MONTH)
        val defaultYearMonthIndex = yearMonthList.value.withIndex().first{ (_, value) -> value == defaultYearMonth }
        _selectedYearMonth.value = defaultYearMonth
        _selectedYearMonthIndex.value = defaultYearMonthIndex.index
        _yearMonthCalendarScrollTrigger.value = UUID.randomUUID().toString()
    }

    abstract fun toggleAllResult()
}