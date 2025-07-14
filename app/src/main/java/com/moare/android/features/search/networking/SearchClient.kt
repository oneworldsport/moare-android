package com.moare.android.features.search.networking

import android.content.Context
import com.moare.android.core.networking.ApiHelper
import com.moare.android.core.util.CalendarUtil
import com.moare.android.features.search.models.DataModel
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.KeywordInfo
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.SportDisplayType
import dagger.hilt.android.qualifiers.ApplicationContext

//import com.moare.android.features.search.models.DataModelDeserializer

class SearchClient(
    private val context: Context,
    private val apiHelper: ApiHelper
) {
    suspend fun fetchDataByQuery(query: String): DataModel {
        val response = apiHelper.searchApi.searchByQuery(query)
        return DataModel.fromJson(response.string())
//        return fetchFromJson(context, "커리 순위")
    }

    suspend fun fetchDataByKeyword(keyword: KeywordInfo): DataModel {
        val response = apiHelper.searchApi.searchByKeyword(keyword)
        return DataModel.fromJson(response.string())
    }

    suspend fun fetchLeagueSchedule(entity: EntityInfo, season: Int?, yearMonth: String): DataModel {
        val response = apiHelper.searchApi.getLeagueSchedule(entity, season ?: CalendarUtil.currentYear, yearMonth)
        return DataModel.fromJson(response.string())
    }

    suspend fun fetchById(
        season: Int?,
        category: String,
        date: String? = null,
        dataType: String,
        leagueId: Int,
        id: String
    ): DataModel {
        val response = apiHelper.searchApi.searchById(season ?: CalendarUtil.currentYear, category, date, dataType, leagueId, id)
        return DataModel.fromJson(response.string())
    }

    fun fetchFromJson(context: Context, viewForTest: SportDisplayType): DataModel {
        val filePath = when (viewForTest) {
            // football
            SportDisplayType.FB_PLAYER_INFO -> { "football_player_info.json" }
            SportDisplayType.FB_PLAYER_STATS -> { "football_player_stats.json" }
            SportDisplayType.FB_PLAYER_STANDINGS -> { "football_player_standings.json" }
            SportDisplayType.FB_TEAM_INFO -> { "football_team_info.json" }
            SportDisplayType.FB_TEAM_STATS -> { "football_team_stats.json" }
            SportDisplayType.FB_TEAM_STANDINGS -> { "football_team_standings.json" }
            SportDisplayType.FB_TEAM_SCHEDULE -> { "football_team_schedule.json" }
            SportDisplayType.FB_LEAGUE_SCHEDULE -> { "football_league_schedule.json" }
            SportDisplayType.FB_GAME_STATS -> { "football_game_stats.json" }
            // nba
            SportDisplayType.NBA_PLAYER_INFO -> { "nba_player_info.json" }
            SportDisplayType.NBA_PLAYER_STATS -> { "nba_player_stats.json" }
            SportDisplayType.NBA_PLAYER_STANDINGS -> { "nba_player_standings.json" }
            SportDisplayType.NBA_TEAM_INFO -> { "nba_team_info.json" }
            SportDisplayType.NBA_TEAM_STATS -> { "nba_team_stats.json" }
            SportDisplayType.NBA_TEAM_STANDINGS -> { "nba_team_standings.json" }
            SportDisplayType.NBA_TEAM_SCHEDULE -> { "nba_team_schedule.json" }
            SportDisplayType.NBA_LEAGUE_SCHEDULE -> { "nba_league_schedule.json" }
            SportDisplayType.NBA_GAME_STATS -> { "nba_game_stats.json" }
            SportDisplayType.NBA_LEAGUE_TOURNAMENT -> { "nba_league_tournament.json" }
            // kbo
            SportDisplayType.KBO_PLAYER_INFO -> { "kbo_player_info.json" }
            SportDisplayType.KBO_PLAYER_STATS -> { "kbo_player_stats.json" }
            SportDisplayType.KBO_PLAYER_STANDINGS -> { "kbo_player_standings.json" }
            SportDisplayType.KBO_TEAM_INFO -> { "kbo_team_info.json" }
            SportDisplayType.KBO_TEAM_STATS -> { "kbo_team_stats.json" }
            SportDisplayType.KBO_TEAM_STANDINGS -> { "kbo_team_standings.json" }
            SportDisplayType.KBO_TEAM_SCHEDULE -> { "kbo_team_schedule.json" }
            SportDisplayType.KBO_LEAGUE_SCHEDULE -> { "kbo_league_schedule.json" }
            SportDisplayType.KBO_GAME_STATS -> { "kbo_game_stats.json" }
            // mlb
            SportDisplayType.MLB_PLAYER_INFO -> { "mlb_player_info.json" }
            SportDisplayType.MLB_PLAYER_STATS -> { "mlb_player_stats.json" }
            SportDisplayType.MLB_PLAYER_STANDINGS -> { "mlb_player_standings.json" }
            SportDisplayType.MLB_TEAM_INFO -> { "mlb_team_info.json" }
            SportDisplayType.MLB_TEAM_STATS -> { "mlb_team_stats.json" }
            SportDisplayType.MLB_TEAM_STANDINGS -> { "mlb_team_standings.json" }
            SportDisplayType.MLB_TEAM_SCHEDULE -> { "mlb_team_schedule.json" }
            SportDisplayType.MLB_LEAGUE_SCHEDULE -> { "mlb_league_schedule.json" }
            SportDisplayType.MLB_GAME_STATS -> { "mlb_game_stats.json" }
            else -> {
                "football_player_info.json"
            }
        }

        val inputStream = context.assets.open(filePath)
        val jsonContent = inputStream.bufferedReader().use { it.readText() }

        return DataModel.fromJson(jsonContent)
    }
}