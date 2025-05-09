package com.moare.android.features.search.display.common.container.state

import com.moare.android.core.util.DayInfo

data class CalendarUiState(
    val yearMonthList: List<String>,
    val days: List<DayInfo>,
    val selectedYearMonthIndex: Int,
    val selectedDayIndex: Int,
    val yearMonthCalendarScrollTrigger: String,
    val dayCalendarScrollTrigger: String
)

data class CalendarUiActions(
    val onSelectYearMonth: (String, Int) -> Unit,
    val onSelectDay: (DayInfo, Int) -> Unit
)
