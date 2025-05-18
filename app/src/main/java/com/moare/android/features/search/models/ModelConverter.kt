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
import com.moare.android.features.search.models.displaymodels.kbo.KBOGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerStandingsDisplay
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBPlayerStandingsDisplay
import com.moare.android.features.search.models.displaymodels.mlb.MLBPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStatsDisplayModel
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
import com.moare.android.features.search.models.models.kbo.KBOGameHitterStats
import com.moare.android.features.search.models.models.kbo.KBOGamePitcherStats
import com.moare.android.features.search.models.responsemodels.football.FBGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBGameScheduleResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBPlayerInfoResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBPlayerStandingsResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamInfoResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamStandingsResponseModel
import com.moare.android.features.search.models.responsemodels.kbo.KBOGameScheduleResponseModel
import com.moare.android.features.search.models.responsemodels.kbo.KBOGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.kbo.KBOPlayerInfoResponseModel
import com.moare.android.features.search.models.responsemodels.kbo.KBOPlayerStandingsResponseModel
import com.moare.android.features.search.models.responsemodels.kbo.KBOTeamInfoResponseModel
import com.moare.android.features.search.models.responsemodels.kbo.KBOTeamStandingsResponseModel
import com.moare.android.features.search.models.responsemodels.mlb.MLBGameScheduleResponseModel
import com.moare.android.features.search.models.responsemodels.mlb.MLBGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.mlb.MLBPlayerInfoResponseModel
import com.moare.android.features.search.models.responsemodels.mlb.MLBPlayerStandingsResponseModel
import com.moare.android.features.search.models.responsemodels.mlb.MLBTeamInfoResponseModel
import com.moare.android.features.search.models.responsemodels.mlb.MLBTeamStandingsResponseModel
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
            keywords = keywords,
            entityInfo = entityInfo,
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
            keywords = keywords,
            entityInfo = entityInfo,
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

        return FBPlayerStandingsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.EPL,
            keywords = keywords,
            entityInfo = entityInfo,
            standings = standings
        )
    }

    fun fbTeamInfoConverter(response: FBTeamInfoResponseModel): FBTeamInfoDisplayModel {
        val info = response.info!!

        val stats = info.statistics.find { it.league.id == leagueId }

        return FBTeamInfoDisplayModel(
            leagueId = leagueId ?: Constants.Ids.EPL,
            keywords = keywords,
            entityInfo = entityInfo,
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
            keywords = keywords,
            entityInfo = entityInfo,
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

        return FBTeamStandingsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.EPL,
            keywords = keywords,
            entityInfo = entityInfo,
            league = league,
            standings = standings
        )
    }

    fun fbTeamScheduleConverter(response: FBGameScheduleResponseModel): FBTeamScheduleDisplayModel {
        return FBTeamScheduleDisplayModel(
            leagueId = leagueId ?: Constants.Ids.EPL,
            keywords = keywords,
            entityInfo = entityInfo,
            games = response.schedule
        )
    }

    fun fbLeagueScheduleConverter(response: FBGameScheduleResponseModel): FBLeagueScheduleDisplayModel {
        val yearMonthList = response.scheduledMonths.map {
            val (year, month) = it.split("-")
            "${year.takeLast(2)}/$month"
        }

        return FBLeagueScheduleDisplayModel(
            leagueId = leagueId ?: Constants.Ids.EPL,
            keywords = keywords,
            entityInfo = entityInfo,
            yearMonthList = yearMonthList,
            games = response.schedule
        )
    }

    fun fbGameStatsConverter(response: FBGameStatsResponseModel): FBGameStatsDisplayModel {
        return FBGameStatsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.EPL,
            keywords = keywords,
            entityInfo = entityInfo,
            game = response.game!!
        )
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
            keywords = keywords,
            entityInfo = entityInfo,
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
            keywords = keywords,
            entityInfo = entityInfo,
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

        return NBAPlayerStandingsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.NBA,
            keywords = keywords,
            entityInfo = entityInfo,
            standings = standings
        )
    }

    fun nbaTeamInfoConverter(response: NBATeamInfoResponseModel): NBATeamInfoDisplayModel {
        val info = response.info!!

        val stats = info.statistics.find { it.seasonType == "Regular Season" }

        return NBATeamInfoDisplayModel(
            leagueId = leagueId ?: Constants.Ids.NBA,
            keywords = keywords,
            entityInfo = entityInfo,
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
            keywords = keywords,
            entityInfo = entityInfo,
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

        return NBATeamStandingsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.NBA,
            keywords = keywords,
            entityInfo = entityInfo,
            standings = standings
        )
    }

    fun nbaTeamScheduleConverter(response: NBAGameScheduleResponseModel): NBATeamScheduleDisplayModel {
        return NBATeamScheduleDisplayModel(
            leagueId = leagueId ?: Constants.Ids.NBA,
            keywords = keywords,
            entityInfo = entityInfo,
            games = response.schedule
        )
    }

    fun nbaLeagueScheduleConverter(response: NBAGameScheduleResponseModel): NBALeagueScheduleDisplayModel {
        val yearMonthList = response.scheduledMonths.map {
            val (year, month) = it.split("-")
            "${year.takeLast(2)}/$month"
        }

        return NBALeagueScheduleDisplayModel(
            leagueId = leagueId ?: Constants.Ids.NBA,
            keywords = keywords,
            entityInfo = entityInfo,
            yearMonthList = yearMonthList,
            games = response.schedule,
        )
    }

    fun nbaGameStatsConverter(response: NBAGameStatsResponseModel): NBAGameStatsDisplayModel {
        return NBAGameStatsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.NBA,
            keywords = keywords,
            entityInfo = entityInfo,
            game = response.game!!
        )
    }

    fun nbaLeagueTournamentConverter(response: NBAGameScheduleResponseModel): NBALeagueScheduleDisplayModel {
        return NBALeagueScheduleDisplayModel(
            leagueId = leagueId ?: Constants.Ids.NBA,
            keywords = keywords,
            entityInfo = entityInfo,
            yearMonthList = emptyList(),
            games = response.schedule
        )
    }

    /* ---------------------
       kbo
       --------------------- */
    fun kboPlayerInfoConverter(response: KBOPlayerInfoResponseModel): KBOPlayerInfoDisplayModel {
        val info = response.info!!

        val stats = info.statistics.find { it.seasonType == "Regular Season" }

        val lastGame = response.lastGame
        val isHome = lastGame?.gameInfo?.homeTeamId?.toInt() == info.player.teamId

        var lastGamePlayerHitterStats: KBOGameHitterStats? = null
        var lastGamePlayerPitcherStats: KBOGamePitcherStats? = null

        if (isHome) {
            lastGamePlayerHitterStats = lastGame?.lineup?.home?.hitters?.find { it.playerName == info.player.name }
            if (lastGamePlayerHitterStats == null) {
                lastGamePlayerPitcherStats = lastGame?.lineup?.home?.pitchers?.find { it.playerName == info.player.name }
            }
        } else {
            lastGamePlayerHitterStats = lastGame?.lineup?.home?.hitters?.find { it.playerName == info.player.name }
            if (lastGamePlayerHitterStats == null) {
                lastGamePlayerPitcherStats = lastGame?.lineup?.home?.pitchers?.find { it.playerName == info.player.name }
            }
        }

        return KBOPlayerInfoDisplayModel(
            leagueId = leagueId ?: Constants.Ids.KBO,
            keywords = keywords,
            entityInfo = entityInfo,
            info = info.player,
            stats = stats,
            lastGame = response.lastGame,
            lastGamePlayerHitterStats = lastGamePlayerHitterStats,
            lastGamePlayerPitcherStats = lastGamePlayerPitcherStats,
            nextGame = response.nextGame
        )
    }

    fun kboPlayerStatsConverter(response: KBOPlayerInfoResponseModel): KBOPlayerStatsDisplayModel {
        val info = response.info!!

        return KBOPlayerStatsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.KBO,
            keywords = keywords,
            entityInfo = entityInfo,
            player = info.player,
            stats = info.statistics,
        )
    }

    fun kboPlayerStandingsConverter(response: KBOPlayerStandingsResponseModel): KBOPlayerStandingsDisplayModel {
        val standings: List<KBOPlayerStandingsDisplay> = response.standings.mapNotNull { playerInfo ->
            val player = playerInfo.player
            val statsList = playerInfo.statistics

            for (item in statsList) {
                if (item.seasonType == "Regular Season") {
                    return@mapNotNull KBOPlayerStandingsDisplay(
                        player = player,
                        stats = item
                    )
                }
            }

            null
        }

        return KBOPlayerStandingsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.KBO,
            keywords = keywords,
            entityInfo = entityInfo,
            standings = standings
        )
    }

    fun kboTeamInfoConverter(response: KBOTeamInfoResponseModel): KBOTeamInfoDisplayModel {
        val info = response.info!!

        // TODO: statistics에 season정보 추가
//        val stats = info.statistics.find { it.seasonType == "Regular Season" }

        return KBOTeamInfoDisplayModel(
            leagueId = leagueId ?: Constants.Ids.KBO,
            keywords = keywords,
            entityInfo = entityInfo,
            team = info.team,
            venue = info.venue,
            stats = info.statistics.firstOrNull(),
            lastGame = response.lastGame,
            nextGame = response.nextGame
        )
    }

    fun kboTeamStatsConverter(response: KBOTeamInfoResponseModel): KBOTeamStatsDisplayModel {
        val info = response.info!!

        return KBOTeamStatsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.KBO,
            keywords = keywords,
            entityInfo = entityInfo,
            team = info.team,
            venue = info.venue,
            stats = info.statistics
        )
    }

    fun kboTeamStandingsConverter(response: KBOTeamStandingsResponseModel): KBOTeamStandingsDisplayModel {
        val standings: List<KBOTeamStandingsDisplay> = response.standings.mapNotNull { teamInfo ->
//            val statsList = teamInfo.statistics
            val stats = teamInfo.statistics.firstOrNull()

//            for (item in statsList) {
//                if (item.seasonType == "Regular Season") {
                    return@mapNotNull KBOTeamStandingsDisplay(
                        team = teamInfo.team,
                        stats = stats!!
                    )
//                }
//            }

//            null
        }

        return KBOTeamStandingsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.KBO,
            keywords = keywords,
            entityInfo = entityInfo,
            standings = standings
        )
    }

//    fun kboTeamScheduleConverter(response: KBOGameScheduleResponseModel): KBOTeamScheduleDisplayModel {
//        return KBOTeamScheduleDisplayModel(
//            leagueId = leagueId ?: Constants.Ids.KBO,
//            keywords = keywords,
//            entityInfo = entityInfo,
//            games = response.schedule
//        )
//    }

    fun kboLeagueScheduleConverter(response: KBOGameScheduleResponseModel): KBOLeagueScheduleDisplayModel {
        val yearMonthList = response.scheduledMonths.map {
            val (year, month) = it.split("-")
            "${year.takeLast(2)}/$month"
        }

        return KBOLeagueScheduleDisplayModel(
            leagueId = leagueId ?: Constants.Ids.KBO,
            keywords = keywords,
            entityInfo = entityInfo,
            yearMonthList = yearMonthList,
            games = response.schedule,
        )
    }

    fun kboGameStatsConverter(response: KBOGameStatsResponseModel): KBOGameStatsDisplayModel {
        return KBOGameStatsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.KBO,
            keywords = keywords,
            entityInfo = entityInfo,
            game = response.game!!
        )
    }

    /* ---------------------
       mlb
       --------------------- */
    fun mlbPlayerInfoConverter(response: MLBPlayerInfoResponseModel): MLBPlayerInfoDisplayModel {
        val info = response.info!!

        val stats = info.statistics.find { it.type == "season" }
        val teamId: Int? = when {
            stats?.hitting != null -> stats.hitting.team.id
            stats?.fielding != null -> stats.fielding.team.id
            stats?.catching != null -> stats.catching.team.id
            stats?.pitching != null -> stats.pitching.team.id
            else -> null
        }

        val lastGamePlayerStats = if (response.lastGame?.teams?.home?.id == teamId) {
            response.lastGame?.boxScore?.teams?.home?.players?.get("ID${info.player.id}")
        } else if (response.lastGame?.teams?.away?.id == teamId) {
            response.lastGame?.boxScore?.teams?.away?.players?.get("ID${info.player.id}")
        } else {
            null
        }

        return MLBPlayerInfoDisplayModel(
            leagueId = leagueId ?: Constants.Ids.MLB,
            keywords = keywords,
            entityInfo = entityInfo,
            info = info.player,
            teamId = teamId,
            stats = stats,
            lastGame = response.lastGame,
            lastGamePlayerStats = lastGamePlayerStats,
            nextGame = response.nextGame
        )
    }

    fun mlbPlayerStatsConverter(response: MLBPlayerInfoResponseModel): MLBPlayerStatsDisplayModel {
        val info = response.info!!

        val stats = info.statistics.find { it.type == "season" }
        val teamId: Int? = when {
            stats?.hitting != null -> stats.hitting.team.id
            stats?.fielding != null -> stats.fielding.team.id
            stats?.catching != null -> stats.catching.team.id
            stats?.pitching != null -> stats.pitching.team.id
            else -> null
        }

        return MLBPlayerStatsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.MLB,
            keywords = keywords,
            entityInfo = entityInfo,
            player = info.player,
            teamId = teamId,
            stats = info.statistics,
        )
    }

    fun mlbPlayerStandingsConverter(response: MLBPlayerStandingsResponseModel): MLBPlayerStandingsDisplayModel {
        val standings: List<MLBPlayerStandingsDisplay> = response.standings.mapNotNull { playerInfo ->
            val player = playerInfo.player
            val statsList = playerInfo.statistics

            for (item in statsList) {
                if (item.type == "season") {
                    return@mapNotNull MLBPlayerStandingsDisplay(
                        player = player,
                        stats = item
                    )
                }
            }

            null
        }

        return MLBPlayerStandingsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.MLB,
            keywords = keywords,
            entityInfo = entityInfo,
            standings = standings
        )
    }

    fun mlbTeamInfoConverter(response: MLBTeamInfoResponseModel): MLBTeamInfoDisplayModel {
        val info = response.info!!

        // TODO: statistics에 season정보 추가
//        val stats = info.statistics.find { it.seasonType == "Regular Season" }

        return MLBTeamInfoDisplayModel(
            leagueId = leagueId ?: Constants.Ids.MLB,
            keywords = keywords,
            entityInfo = entityInfo,
            team = info.team,
            venue = info.venue,
            stats = info.statistics.firstOrNull(),
            lastGame = response.lastGame,
            nextGame = response.nextGame
        )
    }

    fun mlbTeamStatsConverter(response: MLBTeamInfoResponseModel): MLBTeamStatsDisplayModel {
        val info = response.info!!

        return MLBTeamStatsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.MLB,
            keywords = keywords,
            entityInfo = entityInfo,
            team = info.team,
            venue = info.venue,
            stats = info.statistics
        )
    }

    fun mlbTeamStandingsConverter(response: MLBTeamStandingsResponseModel): MLBTeamStandingsDisplayModel {
        val standings: List<MLBTeamStandingsDisplay> = response.standings.mapNotNull { teamInfo ->
//            val statsList = teamInfo.statistics
            val stats = teamInfo.statistics.firstOrNull()

//            for (item in statsList) {
//                if (item.seasonType == "Regular Season") {
            return@mapNotNull MLBTeamStandingsDisplay(
                team = teamInfo.team,
                stats = stats!!
            )
//                }
//            }

//            null
        }

        return MLBTeamStandingsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.MLB,
            keywords = keywords,
            entityInfo = entityInfo,
            standings = standings
        )
    }

//    fun mlbTeamScheduleConverter(response: MLBGameScheduleResponseModel): MLBTeamScheduleDisplayModel {
//        return MLBTeamScheduleDisplayModel(
//            leagueId = leagueId ?: Constants.Ids.MLB,
//            keywords = keywords,
//            entityInfo = entityInfo,
//            games = response.schedule
//        )
//    }

    fun mlbLeagueScheduleConverter(response: MLBGameScheduleResponseModel): MLBLeagueScheduleDisplayModel {
        val yearMonthList = response.scheduledMonths.map {
            val (year, month) = it.split("-")
            "${year.takeLast(2)}/$month"
        }

        return MLBLeagueScheduleDisplayModel(
            leagueId = leagueId ?: Constants.Ids.MLB,
            keywords = keywords,
            entityInfo = entityInfo,
            yearMonthList = yearMonthList,
            games = response.schedule,
        )
    }

    fun mlbGameStatsConverter(response: MLBGameStatsResponseModel): MLBGameStatsDisplayModel {
        return MLBGameStatsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.MLB,
            keywords = keywords,
            entityInfo = entityInfo,
            game = response.game!!
        )
    }
}