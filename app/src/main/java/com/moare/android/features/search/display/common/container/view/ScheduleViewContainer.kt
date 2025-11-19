package com.moare.android.features.search.display.common.container.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.moare.android.core.constants.StringConstants
import com.moare.android.features.search.display.common.container.state.ScheduleContainerActions
import com.moare.android.features.search.display.common.container.state.ScheduleContainerState
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.ui.components.CalendarList
import com.moare.android.ui.components.CalendarType
import com.moare.android.ui.components.CapsuleButton
import com.moare.android.ui.components.ProgressIndicator
import com.moare.android.ui.util.CenterColumn

@Composable
fun ScheduleViewContainer(
    state: ScheduleContainerState,
    actions: ScheduleContainerActions,
    titleContent: @Composable ColumnScope.() -> Unit,
    gameListContent: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val calendarState = state.calendarUiState
    val calendarActions = actions.calendarUiActions

    CenterColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        this.titleContent()

        // calendar
        if (calendarState != null && calendarActions != null && state.shouldShowCalendar) {
            CalendarList(
                calendarState.yearMonthList,
                CalendarType.YEARMONTH,
                calendarState.selectedYearMonthIndex,
                calendarState.yearMonthCalendarScrollTrigger,
                shouldAnimateSroll = calendarState.shouldAnimateScroll
            ) { yearMonth, index ->
                calendarActions.onSelectYearMonth(yearMonth, index)
            }

            CalendarList(
                calendarState.days,
                CalendarType.DAY,
                calendarState.selectedDayIndex,
                calendarState.dayCalendarScrollTrigger,
                shouldAnimateSroll = calendarState.shouldAnimateScroll
            ) { day, index ->
                calendarActions.onSelectDay(day, index)
            }
        }

        // all result open button
        if (state.shouldShowAllResultToggleButton) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Spacer(Modifier.weight(1f))

                if (state.shouldShowTournamentButton) {
                    CapsuleButton(
                        text = StringConstants.tournamentButtonText(state.leagueId),
                        color = Color.Gray
                    ) {
                        actions.tournamentButtonAction?.let { it() }
                    }
                }

                CapsuleButton(
                    text = if (state.isAllResultOpened) {
                        StringConstants.RESULT_HIDE
                    } else {
                        StringConstants.RESULT_OPEN
                    },
                    color = Color.Gray
                ) {
                    actions.allResultButtonAction()
                }
            }
        }

        // NOTE: In most situations, loading should be used in Box for smooth animation.
        Box {
            // loading
            this@CenterColumn.AnimatedVisibility(
                visible = state.displayDataState == ApiFetchState.Fetching,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProgressIndicator()
                }
            }

            // game list(schedule)
            this@CenterColumn.AnimatedVisibility(
                visible = if (state.shouldFetchSchedule) {
                    state.displayDataState == ApiFetchState.Success
                } else {
                    true
                }
            ) {
                this.gameListContent()
            }
        }

        // no result / error
        AnimatedVisibility(
            visible = state.displayDataState is ApiFetchState.Error,
        ) {
            val error = state.displayDataState as? ApiFetchState.Error
            error?.let {
                Text(error.message)
            }
        }

        // bottom empty space
        if (state.shouldFillBelow) {
            Spacer(Modifier.fillMaxSize())
        }
    }
}