package com.moare.android.features.search.models.displaymodels

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword

interface SportDisplayModel {
    val leagueId: Int
    val keywords: List<Keyword>
    val entityInfo: List<EntityInfo>
    val season: Int
}

// TODO: 나중에 현재 파일 이름을 변경하던가 or 아래 protocol을 다른곳으로 이동
interface Rankable<T> {
    val displayRank: Int
    fun withDisplayRank(rank: Int): T
}