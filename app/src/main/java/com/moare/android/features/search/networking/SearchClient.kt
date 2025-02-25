package com.moare.android.features.search.networking

import android.content.Context
import com.moare.android.core.networking.ApiHelper
import com.moare.android.features.search.models.DataModel
import com.moare.android.features.search.models.AutoComplete
import com.moare.android.features.search.models.TrendingKeyword

//import com.moare.android.features.search.models.DataModelDeserializer

class SearchClient(
    private val apiHelper: ApiHelper
) {
    suspend fun fetchDataByQuery(context: Context, query: String): DataModel {
        val response = apiHelper.searchApi.searchByQuery(query)
        return DataModel.fromJson(response.string())
//        return getJson(context, query)
    }

    suspend fun fetchDataByKeyword(keyword: TrendingKeyword): DataModel {
        val response = apiHelper.searchApi.searchByKeyword(keyword)
        return DataModel.fromJson(response.string())
    }

    suspend fun fetchLeagueSchedule(leagueId: Int, yearMonth: String): DataModel {
        val response = apiHelper.searchApi.getLeagueSchedule(leagueId, yearMonth)
        return DataModel.fromJson(response.string())
    }

    suspend fun fetchGameInfo(category: String, date: String, leagueId: Int, fixtureId: Int): DataModel {
        val response = apiHelper.searchApi.fetchGameInfo(category, date, leagueId, fixtureId)
        return DataModel.fromJson(response.string())
    }

    fun getJson(context: Context, query: String): DataModel {
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