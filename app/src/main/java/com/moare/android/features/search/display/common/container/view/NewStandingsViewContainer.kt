package com.moare.android.features.search.display.common.container.view

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.moare.android.features.search.display.common.container.component.StandingsFirstCategoryItem
import com.moare.android.features.search.display.common.container.component.StandingsRankItem
import com.moare.android.features.search.display.common.container.state.NewStandingsContainerState
import com.moare.android.features.search.display.common.container.state.StandingsContainerActions
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.ProgressIndicator
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterBox
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.convertPxToDp
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar
import com.moare.android.ui.util.screenWidthDp

@Composable
fun NewStandingsViewContainer(
    state: NewStandingsContainerState,
    actions: StandingsContainerActions,
    verticalScrollState: ScrollState = rememberScrollState(),
    shouldUseCustomListContent: Boolean = false,
    modifier: Modifier = Modifier,
    titleContent: @Composable ColumnScope.() -> Unit = {},
    customListContent: @Composable ColumnScope.(hScrollState: ScrollState) -> Unit = {}
) {
    val horizontalScrollState = rememberScrollState()
    val defaultColumnWidth = 100.dp

    val highlightState = state.highlightState
    val columnWidthList = state.columnWidthList
    val columnTotalWidth: Dp = if (columnWidthList.isNotEmpty()) {
        columnWidthList.fold(0.dp) { acc, dp -> acc + dp }
    } else {
        defaultColumnWidth * state.secondCategories.size
    }

    val headerCategoryBarOffset by animateDpAsState(
        targetValue = getOffsetOfAniCapsuleBar(itemWidth = screenWidthDp() / (state.headerCategories?.size ?: 2), index = state.headerCategorySelectedIndex),
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )
    val secondCategoryBarOffset by animateDpAsState(
        targetValue = if (columnWidthList.isNotEmpty()) {
            getOffsetOfAniCapsuleBar(itemWidths = columnWidthList, index = state.secondCategorySelectedIndex)
        } else {
            getOffsetOfAniCapsuleBar(itemWidth = defaultColumnWidth, index = state.secondCategorySelectedIndex)
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    CenterColumn(
        // NOTE: If set fillMaxSize, AnimatedVisibility works fine on first show.
        // But if fillMaxSize not set, AnimatedVisibility doesn't work on first show.
        // Not sure why yet
//        modifier = modifier.fillMaxSize()
        modifier = modifier
    ) {
        // league title
        this.titleContent()

        // header category
        state.headerCategories?.let { headerCategories ->
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(42.dp)
                ) {
                    for ((index, item) in headerCategories.withIndex()) {
                        Text(
                            text = item,
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clickable {
                                    actions.headerCategoryButtonAction?.let { it(index) }
                                }
                        )

                        if (index == 0) {
                            VCapsuleBar(modifier = Modifier.alpha(0.5f))
                        }
                    }
                }

                HCapsuleBar(
                    modifier = Modifier
                        .offset(x = headerCategoryBarOffset)
                )
            }
        }

        Row {
            StandingsFirstCategoryItem(text = state.firstCategoryText, width = state.firstColumnWidth)

            Row(
                Modifier.horizontalScroll(horizontalScrollState)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for ((index, item) in state.secondCategories.withIndex()) {
                            CenterBox(
                                height = 42.dp,
                                modifier = Modifier
                                    .clickable {
                                        actions.secondCategoryButtonAction(index, item)
                                    }
                            ) {
                                Text(
                                    text = item,
                                    textAlign = TextAlign.Center,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .width(columnWidthList.getOrNull(index) ?: defaultColumnWidth)
                                )
                            }
                        }
                    }

                    HCapsuleBar(
                        modifier = Modifier
                            .offset(x = secondCategoryBarOffset)
                    )
                }
            }
        }

        // loading
        AnimatedVisibility(
            visible = state.displayDataState == ApiFetchState.Fetching,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                ProgressIndicator()
            }
        }

        // standings data
        AnimatedVisibility(
            visible = if (state.displayDataState == null) {
                true
            } else {
                state.displayDataState == ApiFetchState.Success
            },
            // NOTE: Added for animation on first show. If not set, animation doesn't work on first show.
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Column(
                modifier = if (state.isGameStats) {
                    Modifier
                } else {
                    Modifier.verticalScroll(verticalScrollState)
                }
            ) {
                if (shouldUseCustomListContent) {
                    this.customListContent(horizontalScrollState)
                } else {
                    Row {
                        Column(
                            modifier = Modifier.padding(bottom = 10.dp)
                        ) {
                            for ((index, item) in state.standings.withIndex()) {
                                if (highlightState != null && highlightState.itemIndex == highlightState.standingsStartIndex + index) {
                                    Box(
                                        Modifier
                                            .width(132.dp)
                                            .height(1.dp)
                                            .background(Moare)
                                    )
                                }

                                StandingsRankItem(
                                    id = item.id,
                                    width = state.firstColumnWidth,
                                    isGameStats = item.isGameStats,
                                    rank = if (highlightState != null) highlightState.standingsStartIndex + index + 1 else index + 1,
                                    imageUrl = item.imageUrl,
                                    isSvgLogo = item.isSvgLogo,
                                    name = item.name,
                                    subName = item.subName,
                                    extraInfo = item.extraInfo,
                                    extraSubInfo = item.extraSubInfo,
                                    isLastItem = index == state.standings.size - 1,
                                    action = actions.itemButtonAction
                                )

                                if (highlightState != null && highlightState.itemIndex == highlightState.standingsStartIndex + index) {
                                    Box(
                                        Modifier
                                            .width(132.dp)
                                            .height(1.dp)
                                            .background(Moare)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.horizontalScroll(horizontalScrollState)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                for ((index, item) in state.standings.withIndex()) {
                                    if (highlightState != null && highlightState.itemIndex == highlightState.standingsStartIndex + index) {
                                        Box(
                                            Modifier
                                                .width(columnTotalWidth) // fillMaxWidth()로 했을때 선이 안보임
                                                .height(1.dp)
                                                .background(Moare)
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .height(40.dp)
                                    ) {
                                        for ((index, data) in item.dataList.withIndex()) {
                                            Text(
                                                text = data,
                                                textAlign = TextAlign.Center,
                                                fontSize = 15.sp,
                                                modifier = Modifier
                                                    .width(columnWidthList.getOrNull(index) ?: defaultColumnWidth)
                                            )
                                        }
                                    }

                                    if (highlightState != null && highlightState.itemIndex == highlightState.standingsStartIndex + index) {
                                        Box(
                                            Modifier
                                                .width(columnTotalWidth)
                                                .height(1.dp)
                                                .background(Moare)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}