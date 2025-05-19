package com.moare.android.features.search.display.common.container.state

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moare.android.features.search.models.ApiFetchState

data class StandingsContainerState(
    val displayDataState: ApiFetchState? = null,
    val firstCategoryItemHeight: Dp = 44.dp,
    val isTopPaddingOnHeader: Boolean = true
)