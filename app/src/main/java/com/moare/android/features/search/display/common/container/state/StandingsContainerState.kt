package com.moare.android.features.search.display.common.container.state

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moare.android.core.constants.StringConstants
import com.moare.android.features.search.models.ApiFetchState

data class StandingsContainerState(
    val displayDataState: ApiFetchState? = null,
    val firstCategoryItemHeight: Dp = 44.dp,
    val isTopPaddingOnHeader: Boolean = true
)

data class NewStandingsContainerState(
    val firstCategoryText: String = StringConstants.STANDINGS_FIRST_CATEGORY,
    val headerCategories: List<String>? = null,
    val firstCategories: List<String>? = null,
    val secondCategories: List<String>,
    val standings: List<StandingsItemState>,
    val headerCategorySelectedIndex: Int = 0,
    val firstCategorySelectedIndex: Int = 0,
    val secondCategorySelectedIndex: Int = 0,
    val highlightState: StandingsHighlightItemState? = null,
    val displayDataState: ApiFetchState? = null,
    val firstColumnWidth: Dp? = null,
    val columnWidthList: List<Dp> = emptyList(),
    val isGameStats: Boolean = false
)

data class StandingsItemState(
    val id: Int = 0,
    val isGameStats: Boolean = false,
    val imageUrl: String?,
    val name: String,
    val subName: String? = null,
    val extraInfo: String? = null,
    val extraSubInfo: String? = null,
    val isSvgLogo: Boolean = false,
    val dataList: List<String>
)

data class StandingsHighlightItemState(
    val itemIndex: Int?,
    val standingsStartIndex: Int
)

data class StandingsContainerActions(
    val headerCategoryButtonAction: ((Int) -> Unit)? = null,
    val firstCategoryButtonAction: ((Int) -> Unit)? = null,
    val secondCategoryButtonAction: (index: Int, category: String) -> Unit,
    val itemButtonAction: (Int) -> Unit
)