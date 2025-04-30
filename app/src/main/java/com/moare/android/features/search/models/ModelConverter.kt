package com.moare.android.features.search.models

import com.moare.android.core.constants.Constants
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
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBALeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStandingsDisplay
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStatsDisplayModel
import com.moare.android.features.search.models.models.football.FBLeague
import com.moare.android.features.search.models.responsemodels.football.FBGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBGameScheduleResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBPlayerInfoResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBPlayerStandingsResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamInfoResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamStandingsResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBAGameScheduleResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBAGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBAPlayerInfoResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBAPlayerStandingsResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBATeamInfoResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBATeamStandingsResponseModel

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
            leagueId = leagueId ?: Constants.Ids.EPL,
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
            leagueId = leagueId ?: Constants.Ids.EPL,
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

        return FBPlayerStandingsDisplayModel(leagueId ?: Constants.Ids.EPL, keywords, entityInfo, standings)
    }

    fun fbTeamInfoConverter(response: FBTeamInfoResponseModel): FBTeamInfoDisplayModel {
        val info = response.info!!

        val stats = info.statistics.find { it.league.id == leagueId }

        return FBTeamInfoDisplayModel(
            leagueId = leagueId ?: Constants.Ids.EPL,
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
            leagueId = leagueId ?: Constants.Ids.EPL,
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

        return FBTeamStandingsDisplayModel(leagueId ?: Constants.Ids.EPL, keywords, league, standings)
    }

    fun fbTeamScheduleConverter(response: FBGameScheduleResponseModel): FBTeamScheduleDisplayModel {
        return FBTeamScheduleDisplayModel(leagueId ?: Constants.Ids.EPL, response.schedule)
    }

    fun fbLeagueScheduleConverter(response: FBGameScheduleResponseModel): FBLeagueScheduleDisplayModel {
        val yearMonthList = response.scheduledMonths.map {
            val (year, month) = it.split("-")
            "${year.takeLast(2)}/$month"
        }

        return FBLeagueScheduleDisplayModel(leagueId ?: Constants.Ids.EPL, yearMonthList, response.schedule, entityInfo)
    }

    fun fbGameStatsConverter(response: FBGameStatsResponseModel): FBGameStatsDisplayModel {
        return FBGameStatsDisplayModel(leagueId ?: Constants.Ids.EPL, response.game!!)
    }

    /* ---------------------
       nba
       --------------------- */
    fun nbaPlayerInfoConverter(response: NBAPlayerInfoResponseModel): NBAPlayerInfoDisplayModel {
        val info = response.info!!

        val stats = info.statistics.find { it.seasonType == "Regular Season" }

        val lastGameTeam = if (response.lastGame?.boxScoreTraditional?.homeTeamId == entityInfo.firstOrNull()?.teamId) {
            response.lastGame?.boxScoreTraditional?.homeTeam
        } else {
            response.lastGame?.boxScoreTraditional?.awayTeam
        }

        val lastGamePlayerStats = lastGameTeam?.players?.find { it.personId == entityInfo.firstOrNull()?.playerId }

        return NBAPlayerInfoDisplayModel(
            leagueId = leagueId ?: Constants.Ids.NBA,
            info = info.player,
            stats = stats,
            lastGame = response.lastGame,
            lastGamePlayerStats = lastGamePlayerStats,
            nextGame = response.nextGame
        )
    }

    fun nbaPlayerStatsConverter(response: NBAPlayerInfoResponseModel): NBAPlayerStatsDisplayModel {
        val info = response.info!!

        return NBAPlayerStatsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.NBA,
            player = info.player,
            stats = info.statistics,
        )
    }

    fun nbaPlayerStandingsConverter(response: NBAPlayerStandingsResponseModel): NBAPlayerStandingsDisplayModel {
        val standings: List<NBAPlayerStandingsDisplay> = response.standings.mapNotNull { playerInfo ->
            val player = playerInfo.player
            val statsList = playerInfo.statistics

            for (item in statsList) {
                if (item.seasonType == "Regular Season") {
                    return@mapNotNull NBAPlayerStandingsDisplay(
                        player = player,
                        stats = item
                    )
                }
            }

            null
        }

        return NBAPlayerStandingsDisplayModel(leagueId ?: Constants.Ids.NBA, keywords, entityInfo, standings)
    }

    fun nbaTeamInfoConverter(response: NBATeamInfoResponseModel): NBATeamInfoDisplayModel {
        val info = response.info!!

        val stats = info.statistics.find { it.seasonType == "Regular Season" }

        return NBATeamInfoDisplayModel(
            leagueId = leagueId ?: Constants.Ids.NBA,
            team = info.team,
            venue = info.venue,
            stats = stats,
            lastGame = response.lastGame,
            nextGame = response.nextGame
        )
    }

    fun nbaTeamStatsConverter(response: NBATeamInfoResponseModel): NBATeamStatsDisplayModel {
        val info = response.info!!

        return NBATeamStatsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.NBA,
            team = info.team,
            venue = info.venue,
            stats = info.statistics
        )
    }

    fun nbaTeamStandingsConverter(response: NBATeamStandingsResponseModel): NBATeamStandingsDisplayModel {
        val standings: List<NBATeamStandingsDisplay> = response.standings.mapNotNull { teamInfo ->
            val statsList = teamInfo.statistics

            for (item in statsList) {
                if (item.seasonType == "Regular Season") {
                    return@mapNotNull NBATeamStandingsDisplay(
                        team = teamInfo.team,
                        stats = item
                    )
                }
            }

            null
        }

        return NBATeamStandingsDisplayModel(leagueId ?: Constants.Ids.NBA, keywords, entityInfo, standings)
    }

    fun nbaTeamScheduleConverter(response: NBAGameScheduleResponseModel): NBATeamScheduleDisplayModel {
        return NBATeamScheduleDisplayModel(leagueId ?: Constants.Ids.NBA, response.schedule)
    }

    fun nbaLeagueScheduleConverter(response: NBAGameScheduleResponseModel): NBALeagueScheduleDisplayModel {
        val yearMonthList = response.scheduledMonths.map {
            val (year, month) = it.split("-")
            "${year.takeLast(2)}/$month"
        }

        return NBALeagueScheduleDisplayModel(leagueId ?: Constants.Ids.NBA, yearMonthList, response.schedule, entityInfo)
    }

    fun nbaGameStatsConverter(response: NBAGameStatsResponseModel): NBAGameStatsDisplayModel {
        return NBAGameStatsDisplayModel(leagueId ?: Constants.Ids.NBA, response.game!!)
    }

    fun nbaLeagueTournamentConverter(response: NBAGameScheduleResponseModel): NBALeagueScheduleDisplayModel {
        return NBALeagueScheduleDisplayModel(leagueId ?: Constants.Ids.NBA, emptyList(), response.schedule, entityInfo)
    }
}