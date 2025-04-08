package com.moare.android.features.search.networking

import android.content.Context
import com.moare.android.core.networking.ApiHelper
import com.moare.android.features.search.models.DataModel
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.KeywordInfo
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

    suspend fun fetchLeagueSchedule(entity: EntityInfo, yearMonth: String): DataModel {
        val response = apiHelper.searchApi.getLeagueSchedule(entity, yearMonth)
        return DataModel.fromJson(response.string())
    }

    suspend fun fetchById(
        category: String,
        date: String? = null,
        dataType: String,
        leagueId: Int,
        id: Int
    ): DataModel {
        val response = apiHelper.searchApi.searchById(category, date, dataType, leagueId, id)
        return DataModel.fromJson(response.string())
    }

    fun fetchFromJson(context: Context, query: String): DataModel {
        val filePath = when (query) {
            "손흥민" -> {
                "football_player_info.json"
            }
            "토트넘" -> {
                "football_team_info.json"
            }
            "손흥민 순위" -> {
                "football_player_standings.json"
            }
            "손흥민 기록" -> {
                "football_player_stats.json"
            }
            "토트넘 순위" -> {
                "football_team_standings.json"
            }
            "토트넘 기록" -> {
                "football_team_stats.json"
            }
            "프리미어리그 일정" -> {
                "football_league_schedule.json"
            }
            "토트넘 뉴캐슬 기록" -> {
                "football_game_stats.json"
            }
            "토트넘 일정" -> {
                "football_team_schedule.json"
            }
            "커리" -> {
                "basketball_player_info.json"
            }
            "커리 기록" -> {
                "basketball_player_stats.json"
            }
            "커리 순위" -> {
                "basketball_player_standings.json"
            }
            "워리어스" -> {
                "basketball_team_info.json"
            }
            "워리어스 기록" -> {
                "basketball_team_stats.json"
            }
            else -> {
                "football_player_info.json"
            }
        }

        val inputStream = context.assets.open(filePath)
        val jsonContent = inputStream.bufferedReader().use { it.readText() }

//        val gson = GsonBuilder()
//            .registerTypeAdapter(DataModel::class.java, DataModelDeserializer())
//            .create()

//        return gson.fromJson(jsonContent, DataModel::class.java)

        return DataModel.fromJson(jsonContent)
    }
}