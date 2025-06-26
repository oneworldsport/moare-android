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