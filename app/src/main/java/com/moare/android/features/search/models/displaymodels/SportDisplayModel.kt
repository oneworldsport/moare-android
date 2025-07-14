package com.moare.android.features.search.models.displaymodels

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword

interface SportDisplayModel {
    val leagueId: Int
    val keywords: List<Keyword>
    val entityInfo: List<EntityInfo>
    val season: Int
}