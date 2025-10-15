package com.moare.android.features.search.display.common.container.state

import com.moare.android.features.search.models.ApiFetchState

data class ScheduleContainerState(
    val leagueId: Int,
    val shouldShowCalendar: Boolean = true,
    val shouldShowAllResultToggleButton: Boolean = true,
    val shouldFetchSchedule: Boolean = true,
    val displayDataState: ApiFetchState = ApiFetchState.Idle,
    val shouldFillBelow : Boolean = true,
    val calendarUiState: CalendarUiState? = null,
    val isAllResultOpened: Boolean = false,
    val shouldShowTournamentButton: Boolean = false
)

data class ScheduleContainerActions(
    val calendarUiActions: CalendarUiActions? = null,
    val allResultButtonAction: () -> Unit,
    val tournamentButtonAction: (() -> Unit)? = null
)
