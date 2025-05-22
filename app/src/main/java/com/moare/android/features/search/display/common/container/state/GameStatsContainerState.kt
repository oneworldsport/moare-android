package com.moare.android.features.search.display.common.container.state

import androidx.compose.ui.unit.Dp
import com.moare.android.features.search.models.ApiFetchState

data class GameStatsContainerState(
    val shouldShowTitle: Boolean = true,
    val shouldShowGameItem: Boolean = true,
    val shouldShowStats: Boolean = true,
    val shouldShowCoach: Boolean = true,
    val firstCategoryItemHeight: Dp
)
