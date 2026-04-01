package com.moare.android.features.search.display.common.container.state

import androidx.compose.ui.unit.Dp

data class GameStatsContainerState(
    val shouldShowTitle: Boolean = true,
    val shouldShowGameContent: Boolean = true,
    val shouldShowStats: Boolean = true,
    val shouldShowCoach: Boolean = false,
    val shouldShowRefreshButton: Boolean = false,
    val teamCategories: List<GameStatsTeamState>,
    val coachState: GameStatsCoachState? = null,
    val teamCategorySelectedIndex: Int = 0,
    val firstColumnWidth: Dp? = null,
    val secondColumnWidth: Dp? = null,
    val gameDetailTitle: String = "",
    val gameDetailContent: String = "",
    val noStatsText: String? = null,

    val firstStatsTitle: String? = null,
    val firstStatsCategories: List<String>,
    val firstStatsCategorySelectedIndex: Int = 0,
    val firstStatsColumnWidthList: List<Dp> = emptyList(),
    val firstStatsPlayerList: List<StandingsItemState>,

    val secondStatsTitle: String? = null,
    val secondStatsCategories: List<String>? = null,
    val secondStatsCategorySelectedIndex: Int = 0,
    val secondStatsColumnWidthList: List<Dp> = emptyList(),
    val secondStatsPlayerList: List<StandingsItemState>? = null
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
    // TODO: 이름에서 Button 빼도 될 듯?
    val teamCategoryButtonAction: ((Int) -> Unit)? = null,
    var firstStatsTitleCategoryAction: (() -> Unit)? = null,
    val firstStatsCategoryButtonAction: ((Int) -> Unit),
    val secondStatsTitleCategoryAction: (() -> Unit)? = null,
    val secondStatsCategoryButtonAction: ((Int) -> Unit)? = null,
    val refreshButtonAction: () -> Unit,
    val isRefreshing: Boolean
)
