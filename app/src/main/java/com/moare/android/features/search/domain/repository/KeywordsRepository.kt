package com.moare.android.features.search.domain.repository

import com.moare.android.features.search.models.LeagueKeywords

interface KeywordsRepository {
    suspend fun fetchLeagueKeywords(): LeagueKeywords
}