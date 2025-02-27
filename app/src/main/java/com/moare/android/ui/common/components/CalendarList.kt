package com.moare.android.ui.common.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.util.DayInfo
import com.moare.android.ui.theme.MoareAndroidTheme
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar
import kotlinx.coroutines.delay
import java.time.DayOfWeek

enum class CalendarType {
    SEASON, YEARMONTH, MONTH, DAY
}

@Composable
fun <T> CalendarList(
    dateList: List<T>,
    calendarType: CalendarType,
    selectedIndex: Int,
    scrollTrigger: String = "",
    onItemSelected: (T, Int) -> Unit
) {
    /* ---------------------
       constants
       --------------------- */
    val itemWidth = when (calendarType) {
        CalendarType.SEASON -> 200.dp
        CalendarType.YEARMONTH -> 44.dp
        CalendarType.MONTH -> 30.dp
        CalendarType.DAY -> 20.dp
    }

    val hPadding = when (calendarType) {
        CalendarType.SEASON -> 20.dp
        CalendarType.YEARMONTH -> 10.dp
        CalendarType.MONTH -> 10.dp
        CalendarType.DAY -> 4.dp
    }

    val barYOffset = when (calendarType) {
        CalendarType.DAY -> 21.dp
        else -> 24.dp
    }

    /* ---------------------
       ui state
       --------------------- */
    val scrollState = rememberScrollState()
    val selectedItemPosition = with(LocalDensity.current) {
        ((itemWidth + (hPadding * 2)) * selectedIndex).toPx()
    }.toInt()

    /* ---------------------
       animation
       --------------------- */
    val barOffset by animateDpAsState(
        targetValue = 6.dp + getOffsetOfAniCapsuleBar(itemWidth = itemWidth + (hPadding * 2), barWidth = itemWidth, index = selectedIndex),
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(scrollTrigger) {
        scrollState.animateScrollTo(
            value = selectedItemPosition,
            animationSpec = tween(
                durationMillis = 500,
                easing = LinearOutSlowInEasing
            )
        )
    }

    /* ---------------------
       ui
       --------------------- */
    Box(
        Modifier.horizontalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp)
        ) {
            for ((index, value) in dateList.withIndex()) {
                CalendarItem(value, calendarType, itemWidth, hPadding) {
                    onItemSelected(value, index)
                }
            }
        }

        HCapsuleBar(
            modifier = Modifier
                .offset(x = barOffset)
                .padding(top = barYOffset, bottom = 2.dp),
            customWidth = itemWidth
        )
    }
}

@Composable
fun <T> CalendarItem(
    date: T,
    calendarType: CalendarType,
    itemWidth: Dp,
    hPadding: Dp,
    onItemSelected: () -> Unit
) {
    /* ---------------------
       constants
       --------------------- */
    val text = when (calendarType) {
        CalendarType.DAY -> (date as DayInfo).day.toString()
        else -> date as String
    }

    val dayOfWeek = when (calendarType) {
        CalendarType.DAY -> (date as DayInfo).displayName
        else -> ""
    }

    val isDisabled = when (calendarType) {
        CalendarType.DAY -> (date as DayInfo).isDataEmpty
        else -> false
    }

    /* ---------------------
       ui
       --------------------- */
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (isDisabled) {
            Modifier
                .padding(horizontal = hPadding)
                .width(itemWidth)
        } else {
            Modifier
                .padding(horizontal = hPadding)
                .width(itemWidth)
                .clickable {
                    onItemSelected()
                }
        }
    ) {
        Box(
            contentAlignment = Alignment.TopStart,
            modifier = Modifier.height(20.dp)
        ) {
            Text(
                text = text,
                color = if (isDisabled) Color.Gray else Color.Black
            )
        }

        if (calendarType == CalendarType.DAY) {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.height(24.dp)
            ) {
                Text(
                    text = dayOfWeek,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarListPreview() {
    MoareAndroidTheme {
        val dayList = listOf(
            DayInfo(day = 1, dayOfWeek = DayOfWeek.MONDAY, displayName = "월", isDataEmpty = false),
            DayInfo(day = 2, dayOfWeek = DayOfWeek.MONDAY, displayName = "화", isDataEmpty = false),
            DayInfo(day = 3, dayOfWeek = DayOfWeek.MONDAY, displayName = "수", isDataEmpty = false),
            DayInfo(day = 4, dayOfWeek = DayOfWeek.MONDAY, displayName = "목", isDataEmpty = false),
            DayInfo(day = 5, dayOfWeek = DayOfWeek.MONDAY, displayName = "금", isDataEmpty = false),
            DayInfo(day = 6, dayOfWeek = DayOfWeek.MONDAY, displayName = "토", isDataEmpty = false),
            DayInfo(day = 7, dayOfWeek = DayOfWeek.MONDAY, displayName = "일", isDataEmpty = false),
            DayInfo(day = 8, dayOfWeek = DayOfWeek.MONDAY, displayName = "월", isDataEmpty = false),
            DayInfo(day = 9, dayOfWeek = DayOfWeek.MONDAY, displayName = "화", isDataEmpty = false),
            DayInfo(day = 10, dayOfWeek = DayOfWeek.MONDAY, displayName = "수", isDataEmpty = false),
            DayInfo(day = 11, dayOfWeek = DayOfWeek.MONDAY, displayName = "목", isDataEmpty = false),
            DayInfo(day = 12, dayOfWeek = DayOfWeek.MONDAY, displayName = "금", isDataEmpty = false),
            DayInfo(day = 13, dayOfWeek = DayOfWeek.MONDAY, displayName = "토", isDataEmpty = false),
            DayInfo(day = 14, dayOfWeek = DayOfWeek.MONDAY, displayName = "일", isDataEmpty = false),
            DayInfo(day = 15, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
            DayInfo(day = 16, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
            DayInfo(day = 17, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
            DayInfo(day = 18, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
            DayInfo(day = 19, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
            DayInfo(day = 20, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
            DayInfo(day = 21, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
            DayInfo(day = 22, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
            DayInfo(day = 23, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
            DayInfo(day = 24, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
            DayInfo(day = 25, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
            DayInfo(day = 26, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
            DayInfo(day = 27, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        )

        val yearMonthList = listOf("24/06", "24/07", "24/08", "24/09", "24/10")

        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            CalendarList(
                yearMonthList,
                CalendarType.YEARMONTH,
                0) { yearMonth, index ->
            }

            CalendarList(
                dayList,
                CalendarType.DAY,
                0) { yearMonth, index ->
            }
        }
    }
}