package com.moare.android.features.search.display.common.container.state

import androidx.compose.ui.unit.Dp

data class GameStatsContainerState(
    val shouldShowTitle: Boolean = true,
    val shouldShowGameItem: Boolean = true,
    val shouldShowStats: Boolean = true,
    val shouldShowCoach: Boolean = false,
    val shouldShowRefreshButton: Boolean = false,
    val teamCategories: List<GameStatsTeamState>,
    val firstCategories: List<String>? = null,
    val secondCategories: List<String>,
    val coachState: GameStatsCoachState? = null,
    val teamCategorySelectedIndex: Int = 0,
    val firstCategorySelectedIndex: Int = 0,
    val secondCategorySelectedIndex: Int = 0,
    val columnWidthList: List<Dp> = emptyList(),
    val playerList: List<StandingsItemState>,
    val gameDetailTitle: String = "",
    val gameDetailContent: String = ""
)

data class GameStatsTeamState(
    val name: String,
    val imageUrl: String?
)

data class GameStatsCoachState(
    val name: String?,
    val imageUrl: String? = null
)

data class GameStatsContainerActions(
    val teamCategoryButtonAction: ((Int) -> Unit)? = null,
    val firstCategoryButtonAction: ((Int) -> Unit)? = null,
    val secondCategoryButtonAction: (Int) -> Unit,
    val refreshButtonAction: () -> Unit
)
