package com.moare.android.features.search.data.networking

import android.content.Context
import com.moare.android.core.networking.ApiHelper
import com.moare.android.core.util.CalendarUtil
import com.moare.android.features.search.models.DataModel
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.KeywordInfo
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.SportDisplayType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

//import com.moare.android.features.search.models.DataModelDeserializer

class SearchClient @Inject constructor(
    private val apiHelper: ApiHelper
) {
    suspend fun fetchDataByQuery(query: String): DataModel {
        val response = apiHelper.searchApi.searchByQuery(query)
        return DataModel.fromJson(response.string())
//        return fetchFromJson(context, "커리 순위")
    }

    suspend fun fetchDataByKeyword(keyword: KeywordInfo, season: Int? = null): DataModel {
        val response = apiHelper.searchApi.searchByKeyword(keyword, season)
        return DataModel.fromJson(response.string())
    }

    suspend fun fetchLeagueSchedule(entity: EntityInfo, season: Int?, yearMonth: String?, day: Int? = null): DataModel {
        val response = apiHelper.searchApi.getLeagueSchedule(entity, season ?: CalendarUtil.currentYear, yearMonth, day)
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
}