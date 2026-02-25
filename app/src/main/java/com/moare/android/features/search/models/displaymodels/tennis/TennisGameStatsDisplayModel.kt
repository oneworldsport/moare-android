package com.moare.android.features.search.models.displaymodels.tennis

import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.models.models.tennis.TennisGame
import kotlinx.serialization.Serializable

@Serializable
data class TennisGameStatsDisplayModel(
    override val leagueId: Int,
    override val keywords: List<Keyword> = emptyList(),
    override val entityInfo: List<EntityInfo> = emptyList(),
    override val season: Int,
    val game: TennisGame,
    var leagueKrName: String = "",
    var roundName: String = ""
) : SportDisplayModel