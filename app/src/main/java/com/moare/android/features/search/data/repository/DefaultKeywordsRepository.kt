package com.moare.android.features.search.data.repository

import com.moare.android.features.search.data.networking.KeywordsClient
import com.moare.android.features.search.domain.repository.KeywordsRepository
import com.moare.android.features.search.models.LeagueKeywords
import javax.inject.Inject

class DefaultKeywordsRepository @Inject constructor(
    private val keywordsClient: KeywordsClient
) : KeywordsRepository {
    override suspend fun fetchLeagueKeywords(): LeagueKeywords {
        return keywordsClient.fetchLeagueKeywords()
    }
}