package com.moare.android.core.util

import android.util.Log
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.Period
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import kotlin.math.min

data class DayInfo(
    val day: Int,
    val dayOfWeek: DayOfWeek,
    val displayName: String,
    var isDataEmpty: Boolean = false
)

enum class TimeFormatType {
    AMPM, AMPM_WITH_DATE, AMPM_WITH_DAY_OF_WEEK_DATE, YEAR_MONTH
}

object CalendarUtil {
    val currentYear = LocalDate.now().year

    init {
        Locale.setDefault(Locale.KOREAN)
    }

    enum class DefaultYearMonthType {
        NEXT_YEARMONTH, CURRENT_YEARMONTH, PREVIOUS_YEARMONTH
    }

    fun getDaysInMonth(year: Int, month: Int, locale: Locale = Locale.KOREAN): List<DayInfo> {
        val yearMonth = YearMonth.of(year, month)
        val daysInMonth = yearMonth.lengthOfMonth()

        val koreanDaysOfWeek = mapOf(
            DayOfWeek.MONDAY to "월",
            DayOfWeek.TUESDAY to "화",
            DayOfWeek.WEDNESDAY to "수",
            DayOfWeek.THURSDAY to "목",
            DayOfWeek.FRIDAY to "금",
            DayOfWeek.SATURDAY to "토",
            DayOfWeek.SUNDAY to "일"
        )

        return (1..daysInMonth).map { day ->
            val date = yearMonth.atDay(day)
            val dayOfWeek = date.dayOfWeek

            DayInfo(
                day = day,
                dayOfWeek = dayOfWeek,
                displayName = koreanDaysOfWeek[dayOfWeek] ?: dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
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
        date: String?,
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

        if (!date.isNullOrEmpty()) {
            // NOTE: OffsetDateTime.parse()는 ISO-8601 표준 지원. "2025-04-02T09:00:00Z" 와 "2025-04-02T09:00:00+00:00" 은 둘다 동일한 의미의 ISO-8601 포맷
            val offsetDateTime = OffsetDateTime.parse(date)

            val zonedDateTime = offsetDateTime.toInstant().atZone(zoneId.toZoneId())

            val formatter = DateTimeFormatter.ofPattern(
                when (formatType) {
                    TimeFormatType.AMPM -> "a hh:mm"
                    TimeFormatType.AMPM_WITH_DATE -> "yyyy.MM.dd a hh:mm"
                    TimeFormatType.AMPM_WITH_DAY_OF_WEEK_DATE -> "yyyy.MM.dd (E) a hh:mm"
                    TimeFormatType.YEAR_MONTH -> "yy/MM"
                }, Locale("ko", "KR")
            )

            return zonedDateTime.format(formatter)
        } else {
            return ""
        }
    }

    fun getDefaultDay(yearMonth: String, dayList: List<DayInfo>): Pair<Int, DayInfo>? {
        val defaultYearMonthType = getDefaultYearMonthType(yearMonth)

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

    private fun getDefaultYearMonthType(yearMonth: String): DefaultYearMonthType {
        val currentDate = LocalDate.now()
        val currentYear = currentDate.year % 100
        val currentMonth = currentDate.monthValue
        val totalCurrentYearMonth = currentYear * 12 + currentMonth

        val (year, month) = yearMonth.split("/")
        val totalYearMonth = year.toInt() * 12 + month.toInt()

        return when {
            totalYearMonth == totalCurrentYearMonth -> DefaultYearMonthType.CURRENT_YEARMONTH
            totalYearMonth > totalCurrentYearMonth -> DefaultYearMonthType.NEXT_YEARMONTH
            else -> DefaultYearMonthType.PREVIOUS_YEARMONTH
        }
    }

    fun calculateAge(birthDate: String): Int {
        var date = birthDate

        if (date.contains("T")) {
            date = date.split("T").first()
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = LocalDate.parse(date, formatter)
        val today = LocalDate.now()

        return Period.between(formattedDate, today).years
    }

    fun formatMinutesToHourMinute(min: Int): String {
        val hours = min / 60
        val minutes = min % 60
        return "$hours:$minutes"
    }

    fun formatHourMinuteToMinutes(time: String): Int {
        if (time.contains(":")) {
            val timeArr = time.split(":")
            val hours = timeArr.first().toInt()
            val minutes = timeArr.last().toInt()
            return (hours * 60) + minutes
        } else {
            return 0
        }
    }

    fun isUpcomingDay(date: String): Boolean {
        val gameDate = OffsetDateTime.parse(date).toLocalDate()
        val today = LocalDate.now()
        return !gameDate.isBefore(today) // 오늘이거나 미래 날짜면 true
    }

    fun timeAgoString(dateString: String): String {
        val formatter = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            Locale("ko", "KR")
        )

        val date = LocalDateTime.parse(dateString, formatter).atZone(ZoneId.of("Asia/Seoul"))

        val now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))

        val diff = Duration.between(date, now).seconds

        return when {
            diff < 60 -> "방금 전"
            diff < 3600 -> "${diff / 60}분 전"
            diff < 86400 -> "${diff / 3600}시간 전"
            diff < 604800 -> "${diff / 86400}일 전"
            diff < 2419200 -> "${diff / 604800}주 전"
            diff < 31536000 -> "${diff / 2419200}개월 전"
            else -> "${diff / 31536000}년 전"
        }
    }
}























