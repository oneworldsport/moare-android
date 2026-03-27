package com.moare.android.features.search.display.common.container.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.InputTimeFormatType
import com.moare.android.core.util.OutputTimeFormatType
import com.moare.android.features.search.display.common.container.state.ScheduleContainerActions
import com.moare.android.features.search.display.common.container.state.ScheduleContainerState
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.ui.common.components.CalendarList
import com.moare.android.ui.common.components.CalendarType
import com.moare.android.ui.common.components.CapsuleButton
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.ProgressIndicator
import com.moare.android.ui.util.CenterBox
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar

@Composable
fun ScheduleViewContainer(
    state: ScheduleContainerState,
    actions: ScheduleContainerActions,
    titleContent: @Composable ColumnScope.() -> Unit,
    gameListContent: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val calendarState = state.calendarUiState
    val calendarActions = actions.calendarUiActions

    val relatedLeaguesButtonWidth = 80.dp

    val isSameYearMonth = remember(calendarState) {
        calendarState?.let {
            val selectedYearMonth = calendarState.yearMonthList.getOrNull(calendarState.selectedYearMonthIndex)
            selectedYearMonth?.let {
                CalendarUtil.isSameYearMonth(selectedYearMonth)
            }
        } ?: false
    }

    val relatedLeaguesScrollState = rememberScrollState()
    val relatedLeaguesBarOffset by animateDpAsState(
        targetValue = getOffsetOfAniCapsuleBar(itemWidth = relatedLeaguesButtonWidth, barWidth = relatedLeaguesButtonWidth, spacing = 10.dp, index = state.selectedRelatedLeagueIndex),
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            this.titleContent()
        }

        // period
        state.startDate?.let { startDate ->
            state.endDate?.let { endDate ->
                val start = CalendarUtil.formatDate(startDate, InputTimeFormatType.DATE_ONLY,
                    OutputTimeFormatType.YEAR_MONTH_DAY_KR)
                val end = CalendarUtil.formatDate(endDate, InputTimeFormatType.DATE_ONLY,
                    OutputTimeFormatType.YEAR_MONTH_DAY_KR)
                Text(
                    text = "$start ~ $end",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
//                        .padding(vertical = 2.dp)
                )
            }
        }

        // related leagues
        if (state.relatedLeagues.isNotEmpty()) {
            Row(
                Modifier.horizontalScroll(relatedLeaguesScrollState)
            ) {
                Column(
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        for ((index, league) in state.relatedLeagues.withIndex()) {
                            Text(
                                text = league,
                                textAlign = TextAlign.Center,
                                fontSize = 17.sp,
                                modifier = Modifier
                                    .width(relatedLeaguesButtonWidth)
                                    .clickable {
                                    actions.relatedLeagueButtonAction?.let { it(index) }
                                }
                            )
                        }
                    }

                    HCapsuleBar(
                        customWidth = relatedLeaguesButtonWidth,
                        modifier = Modifier
                            .offset(x = relatedLeaguesBarOffset)
                    )
                }
            }
        }

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
                shouldAnimateSroll = calendarState.shouldAnimateScroll,
                containsToday = isSameYearMonth
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

                if (state.shouldShowTournamentOrTeamStandingsButton) {
                    CapsuleButton(
                        text = StringConstants.tournamentOrStandingsText(state.leagueId),
                        color = Color.Gray
                    ) {
                        actions.tournamentOrteamStandingsButtonAction()
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
            this@Column.AnimatedVisibility(
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
            this@Column.AnimatedVisibility(
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