package com.moare.android.ui.common.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moare.android.core.util.DayInfo
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar
import kotlinx.coroutines.delay

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
    Column(
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
                .padding(top = 4.dp)
                .offset(x = barOffset),
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
        Text(
            text = text,
            color = if (isDisabled) Color.Gray else Color.Black,
            modifier = Modifier
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun CalendarListPreview() {
//    MoareAndroidTheme {
//        Column (
//            modifier = Modifier.fillMaxSize(),
////            contentAlignment = Alignment.Center
//            verticalArrangement = Arrangement.Center
//        ) {
//            CalendarList(
//                dateList = listOf("99/99", "24/2", "24/3", "24/4", "24/5", "24/6", "24/7", "24/8", "24/9","24/10", "24/11", "24/12"),
//                calendarType = CalendarType.YEARMONTH
//            )
//
////            CalendarList(
////                dateList = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9","10", "11", "99"),
////                calendarType = CalendarType.DAY
////            )
//        }
//    }
//}