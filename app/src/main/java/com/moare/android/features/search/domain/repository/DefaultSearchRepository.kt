package com.moare.android.features.search.domain.repository

import com.moare.android.features.search.data.networking.SearchClient
import com.moare.android.features.search.data.repository.SearchRepository
import com.moare.android.features.search.models.DataModel
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.KeywordInfo
import javax.inject.Inject

class DefaultSearchRepository @Inject constructor(
    private val searchClient: SearchClient
) : SearchRepository {
    override suspend fun fetchDataByQuery(query: String): DataModel {
        return searchClient.fetchDataByQuery(query)
    }

    override suspend fun fetchDataByKeyword(
        keyword: KeywordInfo,
        season: Int?
    ): DataModel {
        return searchClient.fetchDataByKeyword(keyword, season)
    }

    override suspend fun fetchLeagueSchedule(
        entity: EntityInfo,
        season: Int?,
        yearMonth: String?,
        day: Int?
    ): DataModel {
        return searchClient.fetchLeagueSchedule(entity, season, yearMonth, day)
    }

    override suspend fun fetchById(
        season: Int?,
        category: String,
        date: String?,
        dataType: String,
        leagueId: Int,
        id: String
    ): DataModel {
        return searchClient.fetchById(season, category, date, dataType, leagueId, id)
    }
}