package com.moare.android.core.util

object MatchDescriptionConverter {
    // NOTE: tournament is added later when schedule is decided
    private val descriptionList = listOf(
        // Premier League
        "Regular Season",

        // Champions League
        "1st Qualifying Round",
        "2nd Qualifying Round",
        "3rd Qualifying Round",
        "Play-offs",
        "League Stage - 1",
        "League Stage - 2",
        "League Stage - 3",
        "League Stage - 4",
        "League Stage - 5",
        "League Stage - 6",
        "League Stage - 7",
        "League Stage - 8",

        // FA Cup
        "Extra Preliminary Round",
        "Extra Preliminary Round Replays",
        "Preliminary Round",
        "Preliminary Round Replays",
        "1st Round Qualifying",
        "1st Round Qualifying Replays",
        "2nd Round Qualifying",
        "2nd Round Qualifying Replays",
        "3rd Round Qualifying",
        "3rd Round Qualifying Replays",
        "4th Round Qualifying",
        "4th Round Qualifying Replays",
        "1st Round",
        "2nd Round",
        "3rd Round",

        // EFL Trophy
        "Group Stage - 1",
        "Group Stage - 2",
        "Group Stage - 3",
        "Group Stage - 4",
        "Group Stage - 5",
        "Group Stage - 6",
        "Group Stage - 7",
        "Group Stage - 8",
        "Group Stage - 9",
        "Group Stage - 10",
        "2nd Round"
    )

    private val translationMap = mapOf(
        "Regular Season" to "정규 시즌"
    )

    fun convert(input: String): String {
        // TODO: 패턴 더 추가해서 함수로 작성
        val dashNumberPattern = Regex("""- (\d+)$""") // "- 숫자" 패턴
        val isDashNumberFormat = dashNumberPattern.containsMatchIn(input)

        var result = input

        // translate known english phrases to korean
        for ((english, korean) in translationMap) {
            if (result.contains(english, ignoreCase = true)) {
                result = result.replace(english, korean, ignoreCase = true)
                break
            }
        }

        // replace "- 숫자" pattern with "숫자라운드"
        if (isDashNumberFormat) {
            result = dashNumberPattern.replace(result) { matchResult ->
                "${matchResult.groupValues[1]}라운드"
            }
        }

        return result
    }
}























