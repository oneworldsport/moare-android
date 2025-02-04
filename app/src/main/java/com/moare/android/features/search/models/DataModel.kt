package com.moare.android.features.search.models

import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAGameScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStatsDisplayModel
import com.moare.android.features.search.models.responsemodels.football.FBGameScheduleResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBGameStatsResponseModel
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

            // TODO: entityInfo 로 nba 인지 basketball 인지 판단?
            val modelConverter = ModelConverter(keywords, entityInfo)

            val data = when (dataType) {
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
                "football_team_schedule" -> {
                    val responseModel: FBGameScheduleResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
                    val displayModel = modelConverter.fbTeamScheduleConverter(responseModel)
                    SportDecodableModel.FBTeamSchedule(responseModel, displayModel)
                }
                "football_league_schedule" -> {
                    val responseModel: FBGameScheduleResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
                    val displayModel = modelConverter.fbLeagueScheduleConverter(responseModel)
                    SportDecodableModel.FBLeagueSchedule(responseModel, displayModel)
                }
                "football_game_stats" -> {
                    val responseModel: FBGameStatsResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)

                    if (responseModel.stats == null) {
                        SportDecodableModel.NoResult
                    } else {
                        val displayModel = modelConverter.fbGameStatsConverter(responseModel)
                        SportDecodableModel.FBGameStats(responseModel, displayModel)
                    }
                }
//                "basketball_player_info" -> {
//                    val responseModel: NBAPlayerInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
//                    val displayModel = modelConverter.nbaPlayerInfoConverter(responseModel)
//                    SportDecodableModel.NBAPlayerInfo(responseModel, displayModel)
//                }
//                "basketball_player_stats" -> {
//                    val responseModel: NBAPlayerInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
//                    val displayModel = modelConverter.nbaPlayerStatsConverter(responseModel)
//                    SportDecodableModel.NBAPlayerStats(responseModel, displayModel)
//                }
//                "basketball_player_standings" -> {
//                    val responseModel: NBAPlayerStandingsResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
//                    val displayModel = modelConverter.nbaPlayerStandingsConverter(responseModel)
//                    SportDecodableModel.NBAPlayerStandings(responseModel, displayModel)
//                }
//                "basketball_team_info" -> {
//                    val responseModel: NBATeamInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
//                    val displayModel = modelConverter.nbaTeamInfoConverter(responseModel)
//                    SportDecodableModel.NBATeamInfo(responseModel, displayModel)
//                }
//                "basketball_team_stats" -> {
//                    val responseModel: NBATeamInfoResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
//                    val displayModel = modelConverter.nbaTeamStatsConverter(responseModel)
//                    SportDecodableModel.NBATeamStats(responseModel, displayModel)
//                }
//                "basketball_team_standings" -> {
//                    val responseModel: NBATeamStandingsResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
//                    val displayModel = modelConverter.nbaTeamStandingsConverter(responseModel)
//                    SportDecodableModel.NBATeamStandings(responseModel, displayModel)
//                }
//                "basketball_game_schedule" -> {
//                    val responseModel: NBAGameScheduleResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
//                    val displayModel = modelConverter.nbaGameScheduleConverter(responseModel)
//                    SportDecodableModel.NBAGameSchedule(responseModel, displayModel)
//                }
//                "basketball_team_schedule" -> {
//                    val responseModel: NBAGameScheduleResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
//                    val displayModel = modelConverter.nbaGameScheduleConverter(responseModel)
//                    SportDecodableModel.NBAGameSchedule(responseModel, displayModel)
//                }
//                "basketball_game_stats" -> {
//                    val responseModel: NBAGameStatsResponseModel = json.decodeFromJsonElement(jsonObject["data"]!!)
//                    val displayModel = modelConverter.nbaGameStatsConverter(responseModel)
//                    SportDecodableModel.NBAGameStats(responseModel, displayModel)
//                }
                else -> SportDecodableModel.NoResult
            }

            return DataModel(dataType, keywords, entityInfo, data)
        }
    }
}

@Serializable
data class EntityInfo(
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
        val responseModel: FBPlayerInfoResponseModel,
        val displayModel: FBPlayerStatsDisplayModel
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
    data class FBTeamSchedule(
        val responseModel: FBGameScheduleResponseModel,
        val displayModel: FBTeamScheduleDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class FBLeagueSchedule(
        val responseModel: FBGameScheduleResponseModel,
        val displayModel: FBLeagueScheduleDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class FBGameStats(
        val responseModel: FBGameStatsResponseModel,
        val displayModel: FBGameStatsDisplayModel
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
    data class NBAGameSchedule(
        val responseModel: NBAGameScheduleResponseModel,
        val displayModel: NBAGameScheduleDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data class NBAGameStats(
        val responseModel: NBAGameStatsResponseModel,
        val displayModel: NBAGameStatsDisplayModel
    ) : SportDecodableModel()

    @Serializable
    data object NoResult : SportDecodableModel()
}