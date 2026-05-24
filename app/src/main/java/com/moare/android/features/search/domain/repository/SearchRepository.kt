package com.moare.android.features.search.domain.repository

import com.moare.android.features.search.models.DataModel
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.KeywordInfo

interface SearchRepository {
    suspend fun fetchDataByQuery(query: String): DataModel
    suspend fun fetchDataByKeyword(keyword: KeywordInfo, season: Int? = null): DataModel
    suspend fun fetchLeagueSchedule(
        entity: EntityInfo,
        season: Int?,
        yearMonth: String?,
        day: Int? = null
    ): DataModel
    suspend fun fetchById(
        season: Int?,
        category: String,
        date: String? = null,
        dataType: String,
        leagueId: Int,
        id: String
    ): DataModel
}