package com.moare.android.core.util

import java.sql.Time
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

data class DayInfo(
    val day: Int,
    val dayOfWeek: DayOfWeek,
    val displayName: String,
    var isDataEmpty: Boolean = false
)

enum class TimeFormatType {
    AMPM, AMPM_WITH_DATE, YEAR_MONTH
}

object CalendarUtil {
    enum class DefaultYearMonthType {
        NEXT_YEARMONTH, CURRENT_YEARMONTH, PREVIOUS_YEARMONTH
    }

    fun getDaysInMonth(year: Int, month: Int, locale: Locale = Locale.KOREAN): List<DayInfo> {
        val yearMonth = YearMonth.of(year, month)
        val daysInMonth = yearMonth.lengthOfMonth()

        return (1..daysInMonth).map { day ->
            val date = yearMonth.atDay(day)
            val dayOfWeek = date.dayOfWeek

            DayInfo(
                day = day,
                dayOfWeek = dayOfWeek,
                displayName = dayOfWeek.getDisplayName(TextStyle.FULL, locale)
            )
        }
    }

    fun isSameDate(stringDate: String, selectedYearMonth: String, selectedDay: Int): Boolean {
        val yearMonth = selectedYearMonth.split("/")

        val selectedDate = LocalDate.of(("20" + yearMonth[0]).toInt(), yearMonth[1].toInt(), selectedDay)

        val stringLocalDate = ZonedDateTime.parse(stringDate)
            .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
            .toLocalDate()

        return stringLocalDate == selectedDate
    }

    fun formatDate(
        date: String,
        formatType: TimeFormatType = TimeFormatType.AMPM_WITH_DATE,
        zoneId: TimeZone = TimeZone.getTimeZone("Asia/Seoul")
    ): String {
//        val inputDateFormat = SimpleDateFormat("yyyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
//        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
//
//        val parsedDate = inputDateFormat.parse(date) ?: return ""
//
//        val outputDateFormat = SimpleDateFormat(
//            when (formatType) {
//                TimeFormatType.AMPM -> "a hh:mm"
//                TimeFormatType.AMPM_WITH_DATE -> "yyyy.MM.dd a hh:mm"
//                TimeFormatType.YEAR_MONTH -> "yy/MM"
//            },
//            Locale("ko", "KR")
//        )
//        outputDateFormat.timeZone = zoneId
//
//        return outputDateFormat.format(parsedDate)
        val offsetDateTime = OffsetDateTime.parse(date)

        val zonedDateTime = offsetDateTime.toInstant().atZone(zoneId.toZoneId())

        val formatter = DateTimeFormatter.ofPattern(
            when (formatType) {
                TimeFormatType.AMPM -> "a hh:mm"
                TimeFormatType.AMPM_WITH_DATE -> "yyyy.MM.dd a hh:mm"
                TimeFormatType.YEAR_MONTH -> "yy/MM"
            }, Locale("ko", "KR")
        )

        return zonedDateTime.format(formatter)
    }

    fun getDefaultDay(yearMonthList: List<String>, dayList: List<DayInfo>): Pair<Int, DayInfo>? {
        val defaultYearMonthType = getDefaultYearMonthType(yearMonthList)

        when (defaultYearMonthType) {
            DefaultYearMonthType.CURRENT_YEARMONTH -> {
                // return closest future day that has games.
                // If there are no matching day, get last day that has games from current month.
                val currentDate = LocalDate.now()
                val currentDay = currentDate.dayOfMonth

                val result = dayList.withIndex().firstOrNull { (_, value) ->
                    value.day >= currentDay && !value.isDataEmpty
                }

                if (result != null) {
                    return Pair(result.index, result.value)
                } else {
                    val result = dayList.withIndex().lastOrNull { (_, value) ->
                        !value.isDataEmpty
                    }

                    return if (result != null) Pair(result.index, result.value) else null
                }
            }
            DefaultYearMonthType.NEXT_YEARMONTH -> {
                // return first day that has games
                val result = dayList.withIndex().firstOrNull { (_, value) ->
                    !value.isDataEmpty
                }

                return if (result != null) Pair(result.index, result.value) else null
            }
            DefaultYearMonthType.PREVIOUS_YEARMONTH -> {
                // return last day that has games
                val result = dayList.withIndex().lastOrNull { (_, value) ->
                    !value.isDataEmpty
                }

                return if (result != null) Pair(result.index, result.value) else null
            }
        }
    }

    private fun getDefaultYearMonthType(yearMonthList: List<String>): DefaultYearMonthType {
        Locale.setDefault(Locale.KOREAN)

        val currentDate = LocalDate.now()
        val currentYear = currentDate.year % 100
        val currentMonth = currentDate.monthValue
//        val currentYearMonth = "%02d/%02d".format(currentYear, currentMonth)
//        val currentYearMonth = "${currentYear.toString().padStart(2, '0')}/${currentMonth.toString().padStart(2, '0')}"

        val sortedList = yearMonthList.map {
            val (year, month) = it.split("/").map { it.toInt() }
            year to month
        }.sortedWith(compareBy({ it.first }, { it.second })) // 연도 -> 월 순으로 정렬

        // 현재 날짜와 일치하는 값 찾기
        val currentDateMatch = sortedList.firstOrNull { (year, month) ->
            year == currentYear && month == currentMonth
        }

        // 현재 날짜보다 큰 값 찾기
        val futureDate = sortedList.firstOrNull { (year, month) ->
            (year > currentYear) || (year == currentYear && month >= currentMonth)
        }

        return when {
            currentDateMatch != null -> DefaultYearMonthType.CURRENT_YEARMONTH
            futureDate != null -> DefaultYearMonthType.NEXT_YEARMONTH
            else -> DefaultYearMonthType.PREVIOUS_YEARMONTH
        }
    }
}























