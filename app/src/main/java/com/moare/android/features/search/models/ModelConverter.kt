package com.moare.android.features.search.models

import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplay
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStatsDisplayModel
import com.moare.android.features.search.models.models.football.FBLeague
import com.moare.android.features.search.models.responsemodels.football.FBGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBGameScheduleResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBPlayerInfoResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBPlayerStandingsResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamInfoResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamStandingsResponseModel

class ModelConverter(
    val keywords: List<Keyword> = emptyList(),
    val entityInfo: List<EntityInfo> = emptyList()
) {
    val leagueId = entityInfo.firstOrNull()?.leagueId

    /* ---------------------
       football
       --------------------- */
    fun fbPlayerInfoConverter(response: FBPlayerInfoResponseModel): FBPlayerInfoDisplayModel {
        val info = response.info!!

        val stats = info.statistics.find { it.league.id == leagueId }

        val lastGameTeamPlayers = response.lastGame?.players?.find { it.team.id == entityInfo.firstOrNull()?.teamId }
        val lastGamePlayerStats = lastGameTeamPlayers?.players?.find { it.player.id == entityInfo.firstOrNull()?.playerId }?.statistics?.firstOrNull()

        return FBPlayerInfoDisplayModel(
            info = info.player,
            stats = stats,
            lastGame = response.lastGame,
            lastGamePlayerStats = lastGamePlayerStats,
            nextGame = response.nextGame
        )
    }

    fun fbPlayerStatsConverter(response: FBPlayerInfoResponseModel): FBPlayerStatsDisplayModel {
        val info = response.info!!

        val stats = info.statistics.find { it.league.id == leagueId }

        return FBPlayerStatsDisplayModel(
            player = info.player,
            team = stats?.team,
            stats = info.statistics
        )
    }

    fun fbPlayerStandingsConverter(response: FBPlayerStandingsResponseModel): FBPlayerStandingsDisplayModel {
        val standings: List<FBPlayerStandingsDisplay> = response.standings.mapNotNull { playerInfo ->
            val player = playerInfo.player
            val statsList = playerInfo.statistics

            for (item in statsList) {
                if (item.league.id == leagueId) {
                    return@mapNotNull FBPlayerStandingsDisplay(
                        player = player,
                        stats = item
                    )
                }
            }

            null
        }

        return FBPlayerStandingsDisplayModel(keywords, standings)
    }

    fun fbTeamInfoConverter(response: FBTeamInfoResponseModel): FBTeamInfoDisplayModel {
        val info = response.info!!

        val stats = info.statistics.find { it.league.id == leagueId }

        return FBTeamInfoDisplayModel(
            team = info.team,
            venue = info.venue,
            stats = stats,
            lastGame = response.lastGame,
            nextGame = response.nextGame
        )
    }

    fun fbTeamStatsConverter(response: FBTeamInfoResponseModel): FBTeamStatsDisplayModel {
        val info = response.info!!

        return FBTeamStatsDisplayModel(
            team = info.team,
            venue = info.venue,
            stats = info.statistics
        )
    }

    fun fbTeamStandingsConverter(response: FBTeamStandingsResponseModel): FBTeamStandingsDisplayModel {
        var league: FBLeague? = null

        val standings: List<FBTeamStandingsDisplay> = response.standings.mapNotNull { teamInfo ->
            val stats = teamInfo.statistics

            for (item in stats) {
                if (item.league.id == leagueId) {
                    // NOTE: Doesn't have league in the FBTeamStandingsDisplay. So add league independently
                    if (league == null) {
                        league = item.league
                    }

                    return@mapNotNull FBTeamStandingsDisplay(
                        team = item.team,
                        homeAwayStats = item.fixtures,
                        goalsFor = item.goals.teamGoalsFor.total,
                        goalsAgainst = item.goals.teamGoalsAgainst.total
                    )
                }
            }

            null
        }

        return FBTeamStandingsDisplayModel(keywords, league, standings)
    }

    fun fbTeamScheduleConverter(response: FBGameScheduleResponseModel): FBTeamScheduleDisplayModel {
        return FBTeamScheduleDisplayModel(response.schedule)
    }

    fun fbLeagueScheduleConverter(response: FBGameScheduleResponseModel): FBLeagueScheduleDisplayModel {
        val yearMonthList = response.scheduledMonths.map {
            val (year, month) = it.split("-")
            "${year.takeLast(2)}/$month"
        }

        return FBLeagueScheduleDisplayModel(yearMonthList, response.schedule)
    }

    fun fbGameStatsConverter(response: FBGameStatsResponseModel): FBGameStatsDisplayModel {
        return FBGameStatsDisplayModel(response.game!!)
    }

    /* ---------------------
       nba
       --------------------- */
//    fun nbaPlayerInfoConverter(responseModel: NBAPlayerInfoResponseModel): NBAPlayerInfoDisplayModel {
//        return NBAPlayerInfoDisplayModel("")
//    }
//
//    fun nbaPlayerStatsConverter(responseModel: NBAPlayerInfoResponseModel): NBAPlayerStatsDisplayModel {
//        return NBAPlayerStatsDisplayModel("")
//    }
//
//    fun nbaPlayerStandingsConverter(responseModel: NBAPlayerStandingsResponseModel): NBAPlayerStandingsDisplayModel {
//        return NBAPlayerStandingsDisplayModel("")
//    }
//
//    fun nbaTeamInfoConverter(responseModel: NBATeamInfoResponseModel): NBATeamInfoDisplayModel {
//        return NBATeamInfoDisplayModel("")
//    }
//
//    fun nbaTeamStatsConverter(responseModel: NBATeamInfoResponseModel): NBATeamStatsDisplayModel {
//        return NBATeamStatsDisplayModel("")
//    }
//
//    fun nbaTeamStandingsConverter(responseModel: NBATeamStandingsResponseModel): NBATeamStandingsDisplayModel {
//        return NBATeamStandingsDisplayModel("")
//    }
//
//    fun nbaGameScheduleConverter(responseModel: NBAGameScheduleResponseModel): NBAGameScheduleDisplayModel {
//        return NBAGameScheduleDisplayModel("")
//    }
//
//    fun nbaGameStatsConverter(responseModel: NBAGameStatsResponseModel): NBAGameStatsDisplayModel {
//        return NBAGameStatsDisplayModel("")
//    }
}