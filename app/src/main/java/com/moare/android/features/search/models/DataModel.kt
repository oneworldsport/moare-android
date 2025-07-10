package com.moare.android.features.search.models

import com.moare.android.core.constants.Constants
import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBALeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATournamentDisplayModel
import com.moare.android.features.search.models.responsemodels.football.FBGameScheduleResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBGameStatsResponseModel
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
import com.moare.android.features.search.models.responsemodels.nba.NBAGameListResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBAGameScheduleResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBAGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBAPlayerInfoResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBAPlayerStandingsResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBATeamInfoResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBATeamStandingsResponseModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

//@Serializable(with = DataModelDeserializer::class)
@Serializable
data class DataModel(
    val dataType: String,
    val keywords: List<Keyword>,
    val entityInfo: List<EntityInfo>,
    val data: SportDecodableModel
) {
    companion object {
        fun fromJson(jsonString: String): DataModel {
            val jsonObject = Json.parseToJsonElement(jsonString).jsonObject
            val dataType = jsonObject["dataType"]?.jsonPrimitive?.content ?: "unknown"

            val json = Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            }

            // TODO: data, keywords, entityInfo null 일때 처리
            val keywords: List<Keyword> = json.decodeFromJsonElement(jsonObject["keywords"]!!)
            val entityInfo: List<EntityInfo> = json.decodeFromJsonElement(jsonObject["entityInfo"]!!)
            val leagueId = entityInfo.firstOrNull()?.leagueId

            // TODO: entityInfo 로 nba 인지 basketball 인지 판단?
            val modelConverter = ModelConverter(keywords, entityInfo)

            val data = when (dataType) {
                // football
                "football_player_info" -> {
                    val responseModel: FBPlayerInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                    if (responseModel.info == null) {
                        SportDecodableModel.NoResult
                    } else {
                        val displayModel = modelConverter.fbPlayerInfoConverter(responseModel)
                        SportDecodableModel.FBPlayerInfo(responseModel, displayModel)
                    }
                }
                "football_player_stats" -> {
                    val responseModel: FBPlayerInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                    if (responseModel.info == null) {
                        SportDecodableModel.NoResult
                    } else {
                        val displayModel = modelConverter.fbPlayerStatsConverter(responseModel)
                        SportDecodableModel.FBPlayerStats(responseModel, displayModel)
                    }
                }
                "football_player_standings" -> {
                    val responseModel: FBPlayerStandingsResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                    if (responseModel.standings.isEmpty()) {
                        SportDecodableModel.NoResult
                    } else {
                        val displayModel = modelConverter.fbPlayerStandingsConverter(responseModel)
                        SportDecodableModel.FBPlayerStandings(responseModel, displayModel)
                    }
                }
                "football_team_info" -> {
                    val responseModel: FBTeamInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                    if (responseModel.info == null) {
                        SportDecodableModel.NoResult
                    } else {
                        val displayModel = modelConverter.fbTeamInfoConverter(responseModel)
                        SportDecodableModel.FBTeamInfo(responseModel, displayModel)
                    }
                }
                "football_team_stats" -> {
                    val responseModel: FBTeamInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                    if (responseModel.info == null) {
                        SportDecodableModel.NoResult
                    } else {
                        val displayModel = modelConverter.fbTeamStatsConverter(responseModel)
                        SportDecodableModel.FBTeamStats(responseModel, displayModel)
                    }
                }
                "football_team_standings" -> {
                    val responseModel: FBTeamStandingsResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                    if (responseModel.standings.isEmpty()) {
                        SportDecodableModel.NoResult
                    } else {
                        val displayModel = modelConverter.fbTeamStandingsConverter(responseModel)
                        SportDecodableModel.FBTeamStandings(responseModel, displayModel)
                    }
                }
                "football_league_schedule" -> {
                    val responseModel: FBGameScheduleResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
                    val displayModel = modelConverter.fbLeagueScheduleConverter(responseModel)
                    SportDecodableModel.FBLeagueSchedule(responseModel, displayModel)
                }
                "football_game_stats" -> {
                    val responseModel: FBGameStatsResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                    if (responseModel.game == null) {
                        SportDecodableModel.NoResult
                    } else {
                        val displayModel = modelConverter.fbGameStatsConverter(responseModel)
                        SportDecodableModel.FBGameStats(responseModel, displayModel)
                    }
                }

                // basketball
                "basketball_player_info" -> {
                    val responseModel: NBAPlayerInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                    if (responseModel.info == null) {
                        SportDecodableModel.NoResult
                    } else {
                        val displayModel = modelConverter.nbaPlayerInfoConverter(responseModel)
                        SportDecodableModel.NBAPlayerInfo(responseModel, displayModel)
                    }
                }
                "basketball_player_stats" -> {
                    val responseModel: NBAPlayerInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                    if (responseModel.info == null) {
                        SportDecodableModel.NoResult
                    } else {
                        val displayModel = modelConverter.nbaPlayerStatsConverter(responseModel)
                        SportDecodableModel.NBAPlayerStats(responseModel, displayModel)
                    }
                }
                "basketball_player_standings" -> {
                    val responseModel: NBAPlayerStandingsResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                    if (responseModel.standings.isEmpty()) {
                        SportDecodableModel.NoResult
                    } else {
                        val displayModel = modelConverter.nbaPlayerStandingsConverter(responseModel)
                        SportDecodableModel.NBAPlayerStandings(responseModel, displayModel)
                    }
                }
                "basketball_team_info" -> {
                    val responseModel: NBATeamInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                    if (responseModel.info == null) {
                        SportDecodableModel.NoResult
                    } else {
                        val displayModel = modelConverter.nbaTeamInfoConverter(responseModel)
                        SportDecodableModel.NBATeamInfo(responseModel, displayModel)
                    }
                }
                "basketball_team_stats" -> {
                    val responseModel: NBATeamInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                    if (responseModel.info == null) {
                        SportDecodableModel.NoResult
                    } else {
                        val displayModel = modelConverter.nbaTeamStatsConverter(responseModel)
                        SportDecodableModel.NBATeamStats(responseModel, displayModel)
                    }
                }
                "basketball_team_standings" -> {
                    val responseModel: NBATeamStandingsResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                    if (responseModel.standings.isEmpty()) {
                        SportDecodableModel.NoResult
                    } else {
                        val displayModel = modelConverter.nbaTeamStandingsConverter(responseModel)
                        SportDecodableModel.NBATeamStandings(responseModel, displayModel)
                    }
                }
                "basketball_team_schedule" -> {
                    val responseModel: NBAGameScheduleResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
                    val displayModel = modelConverter.nbaTeamScheduleConverter(responseModel)
                    SportDecodableModel.NBATeamSchedule(responseModel, displayModel)
                }
                "basketball_league_schedule" -> {
                    val responseModel: NBAGameScheduleResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
                    val displayModel = modelConverter.nbaLeagueScheduleConverter(responseModel)
                    SportDecodableModel.NBALeagueSchedule(responseModel, displayModel)
                }
                "basketball_game_stats" -> {
                    val responseModel: NBAGameStatsResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                    if (responseModel.game == null) {
                        SportDecodableModel.NoResult
                    } else {
                        val displayModel = modelConverter.nbaGameStatsConverter(responseModel)
                        SportDecodableModel.NBAGameStats(responseModel, displayModel)
                    }
                }
                "basketball_league_tournament" -> {
                    val responseModel: NBAGameListResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
                    val displayModel = modelConverter.nbaLeagueTournamentConverter(responseModel)
                    SportDecodableModel.NBALeagueTournament(responseModel, displayModel)
                }

                // baseball
                "baseball_player_info" -> {
                    if (leagueId == Constants.Ids.KBO) {
                        val responseModel: KBOPlayerInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                        if (responseModel.info == null) {
                            SportDecodableModel.NoResult
                        } else {
                            val displayModel = modelConverter.kboPlayerInfoConverter(responseModel)
                            SportDecodableModel.KBOPlayerInfo(responseModel, displayModel)
                        }
                    } else if (leagueId == Constants.Ids.MLB) {
                        val responseModel: MLBPlayerInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                        if (responseModel.info == null) {
                            SportDecodableModel.NoResult
                        } else {
                            val displayModel = modelConverter.mlbPlayerInfoConverter(responseModel)
                            SportDecodableModel.MLBPlayerInfo(responseModel, displayModel)
                        }
                    } else {
                        SportDecodableModel.NoResult
                    }
                }
                "baseball_player_stats" -> {
                    if (leagueId == Constants.Ids.KBO) {
                        val responseModel: KBOPlayerInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                        if (responseModel.info == null) {
                            SportDecodableModel.NoResult
                        } else {
                            val displayModel = modelConverter.kboPlayerStatsConverter(responseModel)
                            SportDecodableModel.KBOPlayerStats(responseModel, displayModel)
                        }
                    } else if (leagueId == Constants.Ids.MLB) {
                        val responseModel: MLBPlayerInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                        if (responseModel.info == null) {
                            SportDecodableModel.NoResult
                        } else {
                            val displayModel = modelConverter.mlbPlayerStatsConverter(responseModel)
                            SportDecodableModel.MLBPlayerStats(responseModel, displayModel)
                        }
                    } else {
                        SportDecodableModel.NoResult
                    }
                }
                "baseball_player_standings" -> {
                    if (leagueId == Constants.Ids.KBO) {
                        val responseModel: KBOPlayerStandingsResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                        // NOTE: temporary annotation for view preparing advise text
//                        if (responseModel.standings.isEmpty()) {
//                            SportDecodableModel.NoResult
//                        } else {
                            val displayModel = modelConverter.kboPlayerStandingsConverter(responseModel)
                            SportDecodableModel.KBOPlayerStandings(responseModel, displayModel)
//                        }
                    } else if (leagueId == Constants.Ids.MLB) {
                        val responseModel: MLBPlayerStandingsResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

//                        if (responseModel.standings.isEmpty()) {
//                            SportDecodableModel.NoResult
//                        } else {
                            val displayModel = modelConverter.mlbPlayerStandingsConverter(responseModel)
                            SportDecodableModel.MLBPlayerStandings(responseModel, displayModel)
//                        }
                    } else {
                        SportDecodableModel.NoResult
                    }
                }
                "baseball_team_info" -> {
                    if (leagueId == Constants.Ids.KBO) {
                        val responseModel: KBOTeamInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                        if (responseModel.info == null) {
                            SportDecodableModel.NoResult
                        } else {
                            val displayModel = modelConverter.kboTeamInfoConverter(responseModel)
                            SportDecodableModel.KBOTeamInfo(responseModel, displayModel)
                        }
                    } else if (leagueId == Constants.Ids.MLB) {
                        val responseModel: MLBTeamInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                        if (responseModel.info == null) {
                            SportDecodableModel.NoResult
                        } else {
                            val displayModel = modelConverter.mlbTeamInfoConverter(responseModel)
                            SportDecodableModel.MLBTeamInfo(responseModel, displayModel)
                        }
                    } else {
                        SportDecodableModel.NoResult
                    }
                }
                "baseball_team_stats" -> {
                    if (leagueId == Constants.Ids.KBO) {
                        val responseModel: KBOTeamInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                        if (responseModel.info == null) {
                            SportDecodableModel.NoResult
                        } else {
                            val displayModel = modelConverter.kboTeamStatsConverter(responseModel)
                            SportDecodableModel.KBOTeamStats(responseModel, displayModel)
                        }
                    } else if (leagueId == Constants.Ids.MLB) {
                        val responseModel: MLBTeamInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                        if (responseModel.info == null) {
                            SportDecodableModel.NoResult
                        } else {
                            val displayModel = modelConverter.mlbTeamStatsConverter(responseModel)
                            SportDecodableModel.MLBTeamStats(responseModel, displayModel)
                        }
                    } else {
                        SportDecodableModel.NoResult
                    }
                }
                "baseball_team_standings" -> {
                    if (leagueId == Constants.Ids.KBO) {
                        val responseModel: KBOTeamStandingsResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                        if (responseModel.standings.isEmpty()) {
                            SportDecodableModel.NoResult
                        } else {
                            val displayModel = modelConverter.kboTeamStandingsConverter(responseModel)
                            SportDecodableModel.KBOTeamStandings(responseModel, displayModel)
                        }
                    } else if (leagueId == Constants.Ids.MLB) {
                        val responseModel: MLBTeamStandingsResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                        if (responseModel.standings.isEmpty()) {
                            SportDecodableModel.NoResult
                        } else {
                            val displayModel = modelConverter.mlbTeamStandingsConverter(responseModel)
                            SportDecodableModel.MLBTeamStandings(responseModel, displayModel)
                        }
                    } else {
                        SportDecodableModel.NoResult
                    }
                }
                "baseball_team_schedule" -> {
                    if (leagueId == Constants.Ids.KBO) {
                        val responseModel: KBOGameScheduleResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
                        val displayModel = modelConverter.kboTeamScheduleConverter(responseModel)
                        SportDecodableModel.KBOTeamSchedule(responseModel, displayModel)
                    } else if (leagueId == Constants.Ids.MLB) {
                        val responseModel: MLBGameScheduleResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
                        val displayModel = modelConverter.mlbTeamScheduleConverter(responseModel)
                        SportDecodableModel.MLBTeamSchedule(responseModel, displayModel)
                    } else {
                        SportDecodableModel.NoResult
                    }
                }
                "baseball_league_schedule" -> {
                    if (leagueId == Constants.Ids.KBO) {
                        val responseModel: KBOGameScheduleResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
                        val displayModel = modelConverter.kboLeagueScheduleConverter(responseModel)
                        SportDecodableModel.KBOLeagueSchedule(responseModel, displayModel)
                    } else if (leagueId == Constants.Ids.MLB) {
                        val responseModel: MLBGameScheduleResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
                        val displayModel = modelConverter.mlbLeagueScheduleConverter(responseModel)
                        SportDecodableModel.MLBLeagueSchedule(responseModel, displayModel)
                    } else {
                        SportDecodableModel.NoResult
                    }
                }
                "baseball_game_stats" -> {
                    if (leagueId == Constants.Ids.KBO) {
                        val responseModel: KBOGameStatsResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                        if (responseModel.game == null) {
                            SportDecodableModel.NoResult
                        } else {
                            val displayModel = modelConverter.kboGameStatsConverter(responseModel)
                            SportDecodableModel.KBOGameStats(responseModel, displayModel)
                        }
                    } else if (leagueId == Constants.Ids.MLB) {
                        val responseModel: MLBGameStatsResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                        if (responseModel.game == null) {
                            SportDecodableModel.NoResult
                        } else {
                            val displayModel = modelConverter.mlbGameStatsConverter(responseModel)
                            SportDecodableModel.MLBGameStats(responseModel, displayModel)
                        }
                    } else {
                        SportDecodableModel.NoResult
                    }
                }

                else -> SportDecodableModel.NoResult
            }

            return DataModel(dataType, keywords, entityInfo, data)
        }
    }
}

@Serializable
data class EntityInfo(
    val entityId: Int,
    val entityName: String,
    val category: String,
    val entityType: String,
    val leagueId: Int,
    val teamId: Int? = null,
    val playerId: Int? = null
)

@Serializable
data class Keyword(
    val keyword: String,
    val id: String,
    val priority: Int
)

enum class SportDisplayType {
    // football
    FB_PLAYER_INFO, FB_PLAYER_STATS, FB_PLAYER_STANDINGS, FB_TEAM_INFO, FB_TEAM_STATS, FB_TEAM_STANDINGS, FB_TEAM_SCHEDULE, FB_LEAGUE_SCHEDULE, FB_GAME_STATS,
    // nba
    NBA_PLAYER_INFO, NBA_PLAYER_STATS, NBA_PLAYER_STANDINGS, NBA_TEAM_INFO, NBA_TEAM_STATS, NBA_TEAM_STANDINGS, NBA_TEAM_SCHEDULE, NBA_LEAGUE_SCHEDULE, NBA_GAME_STATS, NBA_LEAGUE_TOURNAMENT,
    // kbo
    KBO_PLAYER_INFO, KBO_PLAYER_STATS, KBO_PLAYER_STANDINGS, KBO_TEAM_INFO, KBO_TEAM_STATS, KBO_TEAM_STANDINGS, KBO_TEAM_SCHEDULE, KBO_LEAGUE_SCHEDULE, KBO_GAME_STATS,
    // mlb
    MLB_PLAYER_INFO, MLB_PLAYER_STATS, MLB_PLAYER_STANDINGS, MLB_TEAM_INFO, MLB_TEAM_STATS, MLB_TEAM_STANDINGS, MLB_TEAM_SCHEDULE, MLB_LEAGUE_SCHEDULE, MLB_GAME_STATS,
    UNKNOWN
}

@Serializable
sealed class SportDecodableModel {
    // football
    @Serializable
    data class FBPlayerInfo(
        val responseModel: FBPlayerInfoResponseModel,
        val displayModel: FBPlayerInfoDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class FBPlayerStats(
        var responseModel: FBPlayerInfoResponseModel,
        var displayModel: FBPlayerStatsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class FBPlayerStandings(
        val responseModel: FBPlayerStandingsResponseModel,
        val displayModel: FBPlayerStandingsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class FBTeamInfo(
        val responseModel: FBTeamInfoResponseModel,
        val displayModel: FBTeamInfoDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class FBTeamStats(
        val responseModel: FBTeamInfoResponseModel,
        val displayModel: FBTeamStatsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class FBTeamStandings(
        val responseModel: FBTeamStandingsResponseModel,
        val displayModel: FBTeamStandingsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class FBLeagueSchedule(
        val responseModel: FBGameScheduleResponseModel,
        val displayModel: FBLeagueScheduleDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class FBGameStats(
        val responseModel: FBGameStatsResponseModel,
        var displayModel: FBGameStatsDisplayModel
    ) : SportDecodableModel()

    // nba
    @Serializable
    data class NBAPlayerInfo(
        val responseModel: NBAPlayerInfoResponseModel,
        val displayModel: NBAPlayerInfoDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class NBAPlayerStats(
        val responseModel: NBAPlayerInfoResponseModel,
        val displayModel: NBAPlayerStatsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class NBAPlayerStandings(
        val responseModel: NBAPlayerStandingsResponseModel,
        val displayModel: NBAPlayerStandingsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class NBATeamInfo(
        val responseModel: NBATeamInfoResponseModel,
        val displayModel: NBATeamInfoDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class NBATeamStats(
        val responseModel: NBATeamInfoResponseModel,
        val displayModel: NBATeamStatsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class NBATeamStandings(
        val responseModel: NBATeamStandingsResponseModel,
        val displayModel: NBATeamStandingsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class NBATeamSchedule(
        val responseModel: NBAGameScheduleResponseModel,
        val displayModel: NBATeamScheduleDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class NBALeagueSchedule(
        val responseModel: NBAGameScheduleResponseModel,
        val displayModel: NBALeagueScheduleDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class NBAGameStats(
        val responseModel: NBAGameStatsResponseModel,
        val displayModel: NBAGameStatsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class NBALeagueTournament(
        val responseModel: NBAGameListResponseModel,
        val displayModel: NBATournamentDisplayModel
    ) : SportDecodableModel()

    // kbo
    @Serializable
    data class KBOPlayerInfo(
        val responseModel: KBOPlayerInfoResponseModel,
        val displayModel: KBOPlayerInfoDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class KBOPlayerStats(
        val responseModel: KBOPlayerInfoResponseModel,
        val displayModel: KBOPlayerStatsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class KBOPlayerStandings(
        val responseModel: KBOPlayerStandingsResponseModel,
        val displayModel: KBOPlayerStandingsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class KBOTeamInfo(
        val responseModel: KBOTeamInfoResponseModel,
        val displayModel: KBOTeamInfoDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class KBOTeamStats(
        val responseModel: KBOTeamInfoResponseModel,
        val displayModel: KBOTeamStatsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class KBOTeamStandings(
        val responseModel: KBOTeamStandingsResponseModel,
        val displayModel: KBOTeamStandingsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class KBOTeamSchedule(
        val responseModel: KBOGameScheduleResponseModel,
        val displayModel: KBOTeamScheduleDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class KBOLeagueSchedule(
        val responseModel: KBOGameScheduleResponseModel,
        val displayModel: KBOLeagueScheduleDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class KBOGameStats(
        val responseModel: KBOGameStatsResponseModel,
        val displayModel: KBOGameStatsDisplayModel
    ) : SportDecodableModel()

    // mlb
    @Serializable
    data class MLBPlayerInfo(
        val responseModel: MLBPlayerInfoResponseModel,
        val displayModel: MLBPlayerInfoDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class MLBPlayerStats(
        val responseModel: MLBPlayerInfoResponseModel,
        val displayModel: MLBPlayerStatsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class MLBPlayerStandings(
        val responseModel: MLBPlayerStandingsResponseModel,
        val displayModel: MLBPlayerStandingsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class MLBTeamInfo(
        val responseModel: MLBTeamInfoResponseModel,
        val displayModel: MLBTeamInfoDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class MLBTeamStats(
        val responseModel: MLBTeamInfoResponseModel,
        val displayModel: MLBTeamStatsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class MLBTeamStandings(
        val responseModel: MLBTeamStandingsResponseModel,
        val displayModel: MLBTeamStandingsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class MLBTeamSchedule(
        val responseModel: MLBGameScheduleResponseModel,
        val displayModel: MLBTeamScheduleDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class MLBLeagueSchedule(
        val responseModel: MLBGameScheduleResponseModel,
        val displayModel: MLBLeagueScheduleDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class MLBGameStats(
        val responseModel: MLBGameStatsResponseModel,
        val displayModel: MLBGameStatsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data object NoResult : SportDecodableModel()
}