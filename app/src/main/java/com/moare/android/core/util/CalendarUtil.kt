package com.moare.android.core.util

import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.Locale
import java.util.TimeZone

data class DayInfo(
    val day: Int,
    val dayOfWeek: DayOfWeek,
    val displayName: String,
    var isDataEmpty: Boolean = false
)

enum class TimeFormatType {
    AMPM, AMPM_WITH_DATE
}

object CalendarUtil {
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
        val inputDateFormat = SimpleDateFormat("yyyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val parsedDate = inputDateFormat.parse(date) ?: return ""

        val outputDateFormat = SimpleDateFormat(
            if (formatType == TimeFormatType.AMPM) {
                "a hh:mm"
            } else {
                "yyyy.MM.dd a hh:mm"
            },
            Locale("ko", "KR")
        )
        outputDateFormat.timeZone = zoneId

        return outputDateFormat.format(parsedDate)
    }
}