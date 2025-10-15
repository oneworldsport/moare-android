package com.moare.android.features.search.models.models.football

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FBTeam(
    val team: FBTeamInfo,
    val venue: FBVenue,
    val statistics: List<FBTeamStats> = emptyList()
)

@Serializable
data class FBTeamStats(
    val league: FBLeague,
    val team: FBTeamInfo,
    @SerialName("form") private val _form: String? = null,
    val fixtures: FBTeamStatsFixtures,
    val goals: FBTeamStatsGoals,
    val biggest: FBTeamStatsBiggest? = null,
    @SerialName("clean_sheet") val cleanSheet: FBHomeAwayIntStats? = null,
    @SerialName("failed_to_score") val failedToScore: FBHomeAwayIntStats? = null,
    val penalty: FBTeamStatsPenalty,
//    val lineups: List<FBTeamStatsLineups>?,
//    val cards: FBTeamStatsCards?
) {
    val form: String
        get() = _form ?: ""
}

@Serializable
data class FBTeamStatsFixtures(
    val played: FBHomeAwayIntStats,
    val wins: FBHomeAwayIntStats,
    val draws: FBHomeAwayIntStats,
    val loses: FBHomeAwayIntStats
)

@Serializable
data class FBTeamStatsGoals(
    @SerialName("for") val teamGoalsFor: FBTeamStatsGoalsDetail,
    @SerialName("against") val teamGoalsAgainst: FBTeamStatsGoalsDetail
)

@Serializable
data class FBTeamStatsGoalsDetail(
    val total: FBHomeAwayIntStats,
    val average: FBHomeAwayStringStats,
//    val minute: Map<String, FBTeamPercentageStats>?,
//    @SerialName("under_over") val underOver: Map<String, FBTeamUnderOverStats>?
)

@Serializable
data class FBTeamStatsBiggest(
    val streak: FBTeamStatsStreak,
    val wins: FBHomeAwayStringStats,
    val loses: FBHomeAwayStringStats,
    val goals: FBTeamStatsBiggestGoals
)

@Serializable
data class FBTeamStatsStreak(
    @SerialName("wins") private val _wins: Int? = null,
    @SerialName("draws") private val _draws: Int? = null,
    @SerialName("loses") private val _loses: Int? = null,
) {
    val wins: Int
        get() = _wins ?: 0

    val draws: Int
        get() = _draws ?: 0

    val loses: Int
        get() = _loses ?: 0
}

@Serializable
data class FBTeamStatsBiggestGoals(
    @SerialName("for") val teamBiggestGoalsFor: FBHomeAwayIntStats,
    @SerialName("against") val teamBiggestGoalsAgainst: FBHomeAwayIntStats
)

@Serializable
data class FBTeamStatsPenalty(
    val scored: FBTeamStatsPenaltyPercentage,
    val missed: FBTeamStatsPenaltyPercentage,
    @SerialName("total") private val _total: Int? = null,
) {
    val total: Int
        get() = _total ?: 0
}

@Serializable
data class FBTeamStatsPenaltyPercentage(
    @SerialName("total") private val _total: Int? = null,
    @SerialName("percentage") private val _percentage: String? = null
) {
    val total: Int
        get() = _total ?: 0

    val percentage: String
        get() = _percentage ?: ""
}

//@Serializable
//data class FBTeamStatsLineups(
//    val formation: String?,
//    val played: Int?
//)

// TODO: total 로만 받게 디비 데이터 및 모델 수정 필요
//@Serializable
//data class FBTeamStatsCards(
//    val yellow: Map<String, FBTeamPercentageStats>?,
//    val red: Map<String, FBTeamPercentageStats>?
//)

// football api response로 오는 standings 전용 모델들
@Serializable
data class FBTeamForStandings(
    // NOTE: 일단은 필요한 프로퍼티만 만들어놓음.
    val team: FBTeamInfo,
    val league: FBLeague? = null,
    val all: FBTeamStandingsGameStats,
    val home: FBTeamStandingsGameStats,
    val away: FBTeamStandingsGameStats,
    @SerialName("update") private val _update: String? = null
) {
    val update: String get() = _update ?: ""
}

@Serializable
data class FBTeamStandingsGameStats(
    @SerialName("played") private val _played: Int? = null,
    @SerialName("win") private val _win: Int? = null,
    @SerialName("draw") private val _draw: Int? = null,
    @SerialName("lose") private val _lose: Int? = null,
    val goals: FBTeamStandingsGoalStats,
) {
    val played: Int get() = _played ?: 0
    val win: Int get() = _win ?: 0
    val draw: Int get() = _draw ?: 0
    val lose: Int get() = _lose ?: 0
}

@Serializable
data class FBTeamStandingsGoalStats(
    @SerialName("for") private val _for: Int? = null,
    @SerialName("against") private val _against: Int? = null,
) {
    val goalsFor: Int get() = _for ?: 0
    val goalsAgainst: Int get() = _against ?: 0
}

























