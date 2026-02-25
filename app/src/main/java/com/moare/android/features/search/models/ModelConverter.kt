package com.moare.android.features.search.models

import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplay
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTournamentDisplayModel
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
import com.moare.android.features.search.models.displaymodels.kbo.KBOTournamentDisplayModel
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
import com.moare.android.features.search.models.displaymodels.mlb.MLBTournamentDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBALeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStandingsDisplay
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATournamentDisplayModel
import com.moare.android.features.search.models.displaymodels.tennis.TennisGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.tennis.TennisLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.tennis.TennisPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.tennis.TennisTournamentDisplayModel
import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.models.models.football.FBGameForSchedule
import com.moare.android.features.search.models.models.football.FBGameInfoForSchedule
import com.moare.android.features.search.models.models.football.FBHomeAwayIntStats
import com.moare.android.features.search.models.models.football.FBLeague
import com.moare.android.features.search.models.models.football.FBTeamStatsFixtures
import com.moare.android.features.search.models.models.kbo.KBOGame
import com.moare.android.features.search.models.models.kbo.KBOGameForSchedule
import com.moare.android.features.search.models.models.kbo.KBOGameHitterStats
import com.moare.android.features.search.models.models.kbo.KBOGameInfo
import com.moare.android.features.search.models.models.kbo.KBOGameInfoForSchedule
import com.moare.android.features.search.models.models.kbo.KBOGamePitcherStats
import com.moare.android.features.search.models.models.mlb.MLBGame
import com.moare.android.features.search.models.models.mlb.MLBGameData
import com.moare.android.features.search.models.models.mlb.MLBGameForSchedule
import com.moare.android.features.search.models.models.mlb.MLBGameInfo
import com.moare.android.features.search.models.models.mlb.MLBGameInfoForSchedule
import com.moare.android.features.search.models.models.mlb.MLBGameStatus
import com.moare.android.features.search.models.models.mlb.MLBGameTeamDetail
import com.moare.android.features.search.models.models.mlb.MLBGameTeams
import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBAGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.FBGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBGameScheduleResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBPlayerInfoResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBPlayerStandingsResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamInfoResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamStandingsResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamStandingsSource
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
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
import com.moare.android.features.search.models.responsemodels.nba.NBAGameListResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBAGameScheduleResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBAGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBAPlayerInfoResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBAPlayerStandingsResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBATeamInfoResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBATeamStandingsResponseModel
import com.moare.android.features.search.models.responsemodels.tennis.TennisGameScheduleResponseModel
import com.moare.android.features.search.models.responsemodels.tennis.TennisGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.tennis.TennisPlayerStandingsResponseModel

object ModelConverter {
    var keywords: List<Keyword> = emptyList()
        private set

    var entityInfo: List<EntityInfo> = emptyList()
        private set

    var leagueId: Int? = null
        private set

    var season: Int = CalendarUtil.currentYear
        private set

    fun configure(
        keywords: List<Keyword>,
        entityInfo: List<EntityInfo>,
        season: Int
    ) {
        this.keywords = keywords
        this.entityInfo = entityInfo
        this.leagueId = ModelConverter.entityInfo.firstOrNull()?.leagueId
        this.season = season
    }

    // football
    fun fbPlayerInfoConverter(response: FBPlayerInfoResponseModel): FBPlayerInfoDisplayModel {
        val info = response.info!!

        val stats = info.statistics.find { it.league.id == leagueId }

        val lastGameTeamPlayers = response.lastGame?.players?.find { it.team.id == entityInfo.firstOrNull()?.teamId }
        val lastGamePlayerStats = lastGameTeamPlayers?.players?.find { it.player.id == entityInfo.firstOrNull()?.playerId }?.statistics?.firstOrNull()

        return FBPlayerInfoDisplayModel(
            leagueId = leagueId ?: Constants.Ids.EPL,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
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
            season = season,
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
            season = season,
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
            season = season,
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
            season = season,
            team = info.team,
            venue = info.venue,
            stats = info.statistics
        )
    }

    fun fbTeamStandingsConverter(response: FBTeamStandingsResponseModel): FBTeamStandingsDisplayModel {
        var league: FBLeague? = null
        var standingsDisplay: List<FBTeamStandingsDisplay> = emptyList()

        if (response.standings is FBTeamStandingsSource.Db) {
            standingsDisplay = response.standings.teams.mapNotNull { teamInfo ->
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
                            goalsAgainst = item.goals.teamGoalsAgainst.total,
                            rank = item.rank,
                            points = item.points
                        )
                    }
                }

                null
            }
        } else if (response.standings is FBTeamStandingsSource.External) {
            league = response.standings.teams.firstOrNull()?.league

            standingsDisplay = response.standings.teams.mapNotNull { teamInfo ->
                val all = teamInfo.all
                val home = teamInfo.home
                val away = teamInfo.away
                val homeAwayStats = FBTeamStatsFixtures(
                    played = FBHomeAwayIntStats(home.played, away.played, all.played),
                    wins = FBHomeAwayIntStats(home.win, away.win, all.win),
                    draws = FBHomeAwayIntStats(home.draw, away.draw, all.draw),
                    loses = FBHomeAwayIntStats(home.lose, away.lose, all.lose)
                )

                return@mapNotNull FBTeamStandingsDisplay(
                    team = teamInfo.team,
                    homeAwayStats = homeAwayStats,
                    goalsFor = FBHomeAwayIntStats(home.goals.goalsFor, away.goals.goalsFor, all.goals.goalsFor),
                    goalsAgainst = FBHomeAwayIntStats(home.goals.goalsAgainst, away.goals.goalsAgainst, all.goals.goalsAgainst),
                    rank = teamInfo.rank,
                    points = teamInfo.points
                )
            }
        }

        return FBTeamStandingsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.EPL,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
            league = league,
            standings = standingsDisplay
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
            season = season,
            scheduleType = response.scheduleType,
            yearMonthList = yearMonthList,
            games = response.schedule
        )
    }

    fun fbGameStatsConverter(response: FBGameStatsResponseModel): FBGameStatsDisplayModel {
        val game = response.game!!
        return FBGameStatsDisplayModel(
            leagueId = game.league.id,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
            game = game
        )
    }

    fun fbTournamentConverter(response: FBGameScheduleResponseModel): FBTournamentDisplayModel {
        return FBTournamentDisplayModel(
            leagueId = leagueId ?: Constants.Ids.EPL,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
            scheduleType = response.scheduleType ?: ScheduleType.TOURNAMENT_DRAW,
            games = response.schedule
        )
    }

    // nba
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
            season = season,
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
            season = season,
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
            season = season,
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
            season = season,
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
            season = season,
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
            season = season,
            standings = standings
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
            season = season,
            scheduleType = response.scheduleType ?: ScheduleType.LEAGUE,
            yearMonthList = yearMonthList,
            games = response.schedule,
        )
    }

    fun nbaGameStatsConverter(response: NBAGameStatsResponseModel): NBAGameStatsDisplayModel {
        return NBAGameStatsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.NBA,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
            game = response.game!!
        )
    }

    fun nbaTournamentConverter(response: NBAGameScheduleResponseModel): NBATournamentDisplayModel {
        return NBATournamentDisplayModel(
            leagueId = leagueId ?: Constants.Ids.NBA,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
            scheduleType = response.scheduleType ?: ScheduleType.TOURNAMENT_BRACKET,
            games = response.schedule
        )
    }

    // kbo
    fun kboPlayerInfoConverter(response: KBOPlayerInfoResponseModel): KBOPlayerInfoDisplayModel {
        val info = response.info!!

        val stats = info.statistics.find { it.seasonType == "Regular Season" }

        val lastGame = response.lastGame
        val isHome = lastGame?.gameInfo?.homeTeamId == info.player.teamId

        val lastGamePlayerHitterStats: KBOGameHitterStats?
        var lastGamePlayerPitcherStats: KBOGamePitcherStats? = null

        if (isHome) {
            lastGamePlayerHitterStats = lastGame?.lineup?.home?.hitters?.find { it.id == info.player.id }
            if (lastGamePlayerHitterStats == null) {
                lastGamePlayerPitcherStats = lastGame?.lineup?.home?.pitchers?.find { it.id == info.player.id }
            }
        } else {
            lastGamePlayerHitterStats = lastGame?.lineup?.away?.hitters?.find { it.id == info.player.id }
            if (lastGamePlayerHitterStats == null) {
                lastGamePlayerPitcherStats = lastGame?.lineup?.away?.pitchers?.find { it.id == info.player.id }
            }
        }

        return KBOPlayerInfoDisplayModel(
            leagueId = leagueId ?: Constants.Ids.KBO,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
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
            season = season,
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
            season = season,
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
            season = season,
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
            season = season,
            team = info.team,
            venue = info.venue,
            stats = info.statistics
        )
    }

    fun kboTeamStandingsConverter(response: KBOTeamStandingsResponseModel): KBOTeamStandingsDisplayModel {
        val standings: List<KBOTeamStandingsDisplay> = response.standings.map { teamInfo ->
//            val statsList = teamInfo.statistics
            val stats = teamInfo.statistics.firstOrNull()

//            for (item in statsList) {
//                if (item.seasonType == "Regular Season") {
            return@map KBOTeamStandingsDisplay(
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
            season = season,
            standings = standings
        )
    }

    fun kboLeagueScheduleConverter(response: KBOGameScheduleResponseModel): KBOLeagueScheduleDisplayModel {
        val yearMonthList = response.scheduledMonths.map {
            val (year, month) = it.split("-")
            "${year.takeLast(2)}/$month"
        }

        return KBOLeagueScheduleDisplayModel(
            leagueId = leagueId ?: Constants.Ids.KBO,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
            scheduleType = response.scheduleType ?: ScheduleType.LEAGUE,
            yearMonthList = yearMonthList,
            games = response.schedule,
        )
    }

    fun kboGameStatsConverter(response: KBOGameStatsResponseModel): KBOGameStatsDisplayModel {
        return KBOGameStatsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.KBO,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
            game = response.game!!
        )
    }

    fun kboTournamentConverter(response: KBOGameScheduleResponseModel): KBOTournamentDisplayModel {
        return KBOTournamentDisplayModel(
            leagueId = leagueId ?: Constants.Ids.KBO,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
            scheduleType = response.scheduleType ?: ScheduleType.TOURNAMENT_BRACKET,
            games = response.schedule
        )
    }

    // mlb
    fun mlbPlayerInfoConverter(response: MLBPlayerInfoResponseModel): MLBPlayerInfoDisplayModel {
        val info = response.info!!

        val stats = info.statistics.find { it.type == "season" }
        val teamId: Int? = when {
            stats?.hitting?.team != null -> stats.hitting.team.id
            stats?.fielding != null -> stats.fielding.team.id
            stats?.catching != null -> stats.catching.team.id
            stats?.pitching != null -> stats.pitching.team.id
            else -> null
        }

        val lastGamePlayerStats = if (response.lastGame?.teams?.home?.id == teamId) {
            response.lastGame?.boxscore?.teams?.home?.players?.get("ID${info.player.id}")
        } else if (response.lastGame?.teams?.away?.id == teamId) {
            response.lastGame?.boxscore?.teams?.away?.players?.get("ID${info.player.id}")
        } else {
            null
        }

        return MLBPlayerInfoDisplayModel(
            leagueId = leagueId ?: Constants.Ids.MLB,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
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
            stats?.hitting != null -> stats.hitting.team?.id
            stats?.fielding != null -> stats.fielding.team.id
            stats?.catching != null -> stats.catching.team.id
            stats?.pitching != null -> stats.pitching.team.id
            else -> null
        }

        return MLBPlayerStatsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.MLB,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
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
            season = season,
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
            season = season,
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
            season = season,
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
            season = season,
            standings = standings
        )
    }

    fun mlbLeagueScheduleConverter(response: MLBGameScheduleResponseModel): MLBLeagueScheduleDisplayModel {
        val yearMonthList = response.scheduledMonths.map {
            val (year, month) = it.split("-")
            "${year.takeLast(2)}/$month"
        }

        return MLBLeagueScheduleDisplayModel(
            leagueId = leagueId ?: Constants.Ids.MLB,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
            scheduleType = response.scheduleType ?: ScheduleType.LEAGUE,
            yearMonthList = yearMonthList,
            games = response.schedule,
        )
    }

    fun mlbGameStatsConverter(response: MLBGameStatsResponseModel): MLBGameStatsDisplayModel {
        return MLBGameStatsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.MLB,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
            game = response.game!!
        )
    }

    fun mlbTournamentConverter(response: MLBGameScheduleResponseModel): MLBTournamentDisplayModel {
        return MLBTournamentDisplayModel(
            leagueId = leagueId ?: Constants.Ids.MLB,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
            scheduleType = response.scheduleType ?: ScheduleType.TOURNAMENT_BRACKET,
            games = response.schedule
        )
    }

    // tennis
    fun tennisPlayerStandingsConverter(response: TennisPlayerStandingsResponseModel): TennisPlayerStandingsDisplayModel {
//        val standings: List<NBAPlayerStandingsDisplay> = response.standings.mapNotNull { playerInfo ->
//            val player = playerInfo.player
//            val statsList = playerInfo.statistics
//
//            for (item in statsList) {
//                if (item.seasonType == "Regular Season") {
//                    return@mapNotNull NBAPlayerStandingsDisplay(
//                        player = player,
//                        stats = item
//                    )
//                }
//            }
//
//            null
//        }

        return TennisPlayerStandingsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.NBA,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
//            standings = standings
        )
    }

    fun tennisLeagueScheduleConverter(response: TennisGameScheduleResponseModel): TennisLeagueScheduleDisplayModel {
        val yearMonthList = response.scheduledMonths.map {
            val (year, month) = it.split("-")
            "${year.takeLast(2)}/$month"
        }

        return TennisLeagueScheduleDisplayModel(
            leagueId = leagueId ?: Constants.Ids.AUS_OPEN_M_SINGLE,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
            scheduleType = response.scheduleType,
            yearMonthList = yearMonthList,
            startDate = response.startDate,
            endDate = response.endDate,
            relatedLeagueIds = response.relatedLeagueIds,
            games = response.schedule,
        )
    }

    fun tennisGameStatsConverter(response: TennisGameStatsResponseModel): TennisGameStatsDisplayModel {
        return TennisGameStatsDisplayModel(
            leagueId = leagueId ?: Constants.Ids.AUS_OPEN_M_SINGLE,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
            game = response.game!!
        )
    }

    fun tennisTournamentConverter(response: TennisGameScheduleResponseModel): TennisTournamentDisplayModel {
        return TennisTournamentDisplayModel(
            leagueId = leagueId ?: Constants.Ids.AUS_OPEN_M_SINGLE,
            keywords = keywords,
            entityInfo = entityInfo,
            season = season,
            scheduleType = response.scheduleType,
            games = response.schedule
        )
    }

    // Not used in DataModel
    fun fbGameToGameScheduleConverter(game: FBGame): FBGameForSchedule {
        val date = game.fixture.date.split("+").firstOrNull()
        val homeTeamId = game.teams.home.id
        val awayTeamId = game.teams.away.id
        val homeTeamScore = game.goals.home
        val awayTeamScore = game.goals.away
        val gameInfo = FBGameInfoForSchedule(
            _round = game.league.round,
            status = game.fixture.status,
            _homeTeamPenaltyScore = game.score.penalty._home, // TODO: Optional이 필요해서 임시로 _home, _away 사용. 추후 개선 필요.
            _awayTeamPenaltyScore = game.score.penalty._away
        )

        return FBGameForSchedule(
            _itemKey = if (date != null) "${date}#${game.fixture.id}" else "",
            _homeTeamId = homeTeamId,
            _awayTeamId = awayTeamId,
            _homeTeamScore = homeTeamScore,
            _awayTeamScore = awayTeamScore,
            _gameStatus = game.fixture.status.short,
            gameInfo = gameInfo
        )
    }

    fun fbGameDisplayToLeagueScheduleDisplayConverter(
        gameStatsDisplayModel: FBGameStatsDisplayModel,
        leagueScheduleDisplayModel: FBLeagueScheduleDisplayModel
    ): FBLeagueScheduleDisplayModel {
        val game = gameStatsDisplayModel.game
        val newGames = leagueScheduleDisplayModel.games.map {
            if (it.gameId == game.fixture.id.toString()) {
                fbGameToGameScheduleConverter(game)
            } else {
                it
            }
        }

        return leagueScheduleDisplayModel.copy(games = newGames)
    }

    fun nbaGameListToGameScheduleListConverter(gameList: List<NBAGame>): List<NBAGameForSchedule> {
        return gameList.mapNotNull {
            nbaGameToGameScheduleConverter(game = it)
        }
    }

    fun nbaGameToGameScheduleConverter(game: NBAGame): NBAGameForSchedule {
        val gameSummary = game.gameSummary
        val date = gameSummary?.gameDate?.split("+")?.firstOrNull()
        val homeTeamId = gameSummary?.homeTeamId
        val awayTeamId = gameSummary?.awayTeamId
        val homeTeamScore = game.lineScore?.firstOrNull { it.teamId == homeTeamId }?.pts ?: 0
        val awayTeamScore = game.lineScore?.firstOrNull { it.teamId == awayTeamId }?.pts ?: 0

        return NBAGameForSchedule(
            _itemKey = if (date != null) "${date}#${gameSummary.gameId}" else "",
            _homeTeamId = homeTeamId,
            _awayTeamId = awayTeamId,
            _homeTeamScore = homeTeamScore,
            _awayTeamScore = awayTeamScore,
            _gameStatus = gameSummary?.gameStatus?.toString(),
            gameInfo = gameSummary
        )
    }

    fun nbaGameDisplayToLeagueScheduleDisplayConverter(
        gameStatsDisplayModel: NBAGameStatsDisplayModel,
        leagueScheduleDisplayModel: NBALeagueScheduleDisplayModel
    ): NBALeagueScheduleDisplayModel {
        val game = gameStatsDisplayModel.game
        val newGames = leagueScheduleDisplayModel.games.map {
            if (it.gameId == game.gameSummary?.gameId) {
                nbaGameToGameScheduleConverter(game)
            } else {
                it
            }
        }

        return leagueScheduleDisplayModel.copy(games = newGames)
    }

    fun mlbGameToGameScheduleConverter(game: MLBGame): MLBGameForSchedule {
        val date = game.gameInfo.gameDate.split("+").firstOrNull()
        val homeTeamId = game.teams.home.id
        val awayTeamId = game.teams.away.id
        val homeTeamScore = game.linescore?.teams?.home?.runs
        val awayTeamScore = game.linescore?.teams?.away?.runs
        val gameInfo = MLBGameInfoForSchedule(_currentInning = "${game.linescore?.currentInning ?: 1}회${if (game.linescore?.isTopInning ?: true) "초" else "말"}")

        return MLBGameForSchedule(
            _itemKey = if (date != null) "${date}#${game.game.id}" else "",
            _homeTeamId = homeTeamId,
            _awayTeamId = awayTeamId,
            _homeTeamScore = homeTeamScore,
            _awayTeamScore = awayTeamScore,
            _gameStatus = game.status.detailedState,
            gameInfo = gameInfo
        )
    }

    fun mlbGameScheduleToGameConverter(game: MLBGameForSchedule): MLBGame {
        val gameData = MLBGameData(_id = game.gameId)
        val gameInfo = MLBGameInfo(_gameDate = game.date)
        val status = MLBGameStatus(_detailedState = game.gameStatus)
        val teams = MLBGameTeams(away = MLBGameTeamDetail(_id = game.awayTeamId), home = MLBGameTeamDetail(_id = game.homeTeamId))

        return MLBGame(
            game = gameData,
            gameInfo = gameInfo,
            status = status,
            teams = teams
        )
    }

    fun mlbGameDisplayToLeagueScheduleDisplayConverter(
        gameStatsDisplayModel: MLBGameStatsDisplayModel,
        leagueScheduleDisplayModel: MLBLeagueScheduleDisplayModel
    ): MLBLeagueScheduleDisplayModel {
        val game = gameStatsDisplayModel.game
        val newGames = leagueScheduleDisplayModel.games.map {
            if (it.gameId == game.game.pk.toString()) {
                mlbGameToGameScheduleConverter(game)
            } else {
                it
            }
        }

        return leagueScheduleDisplayModel.copy(games = newGames)
    }

    fun kboGameToGameScheduleConverter(game: KBOGame): KBOGameForSchedule {
        val date = game.gameInfo?.date?.split("+")?.firstOrNull()
        val homeTeamId = game.gameInfo?.homeTeamId ?: 0
        val awayTeamId = game.gameInfo?.awayTeamId ?: 0
        val homeTeamScore = game.lineScore?.home?.r ?: "0"
        val awayTeamScore = game.lineScore?.away?.r ?: "0"
        val gameInfo = KBOGameInfoForSchedule(_currentInning = game.lineScore?.currentInning, _seriesDescription = game.gameInfo?.seriesDescription)

        return KBOGameForSchedule(
            _itemKey = if (date != null) "${date}#${game.gameInfo.gameId}" else "",
            _homeTeamId = homeTeamId,
            _awayTeamId = awayTeamId,
            _homeTeamScore = homeTeamScore.toIntOrNull(),
            _awayTeamScore = awayTeamScore.toIntOrNull(),
            _gameStatus = game.gameInfo?.gameStatus,
            gameInfo = gameInfo
        )
    }

    fun kboGameScheduleToGameConverter(game: KBOGameForSchedule): KBOGame {
        return KBOGame(
            gameInfo = KBOGameInfo(
                _awayTeamId = game.awayTeamId,
                _date = game.date,
                _gameId = game.gameId,
                _homeTeamId = game.homeTeamId,
                _gameStatus = game.gameStatus
            ),
            lineScore = null,
            lineup = null
        )
    }

    fun kboGameDisplayToLeagueScheduleDisplayConverter(
        gameStatsDisplayModel: KBOGameStatsDisplayModel,
        leagueScheduleDisplayModel: KBOLeagueScheduleDisplayModel
    ): KBOLeagueScheduleDisplayModel {
        val game = gameStatsDisplayModel.game
        val itemKey = "${game.gameInfo?.date?.split("+")?.firstOrNull() ?: ""}#${game.gameInfo?.gameId ?: ""}"
        val newGames = leagueScheduleDisplayModel.games.map {
            if (it.itemKey == itemKey) {
                kboGameToGameScheduleConverter(game)
            } else {
                it
            }
        }

        return leagueScheduleDisplayModel.copy(games = newGames)
    }
}





































