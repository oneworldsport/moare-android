package com.moare.android.features.search.display.common.container.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.R
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.dropFirstWord
import com.moare.android.features.search.display.common.container.component.StandingsFirstCategoryItem
import com.moare.android.features.search.display.common.container.component.StandingsRankItem
import com.moare.android.features.search.display.common.container.state.GameStatsContainerActions
import com.moare.android.features.search.display.common.container.state.GameStatsContainerState
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.HDivider
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.util.CenterBox
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow
import com.moare.android.ui.util.conditionalClickable
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameStatsViewContainer(
    state: GameStatsContainerState,
    actions: GameStatsContainerActions,
    titleContent: @Composable ColumnScope.() -> Unit,
    gameContent: @Composable ColumnScope.() -> Unit
) {
    val coachState = state.coachState
    val columnWidthList = state.firstStatsColumnWidthList
    val defaultColumnWidth = 100.dp
//    val columnTotalWidth: Dp = if (columnWidthList.isNotEmpty()) {
//        columnWidthList.fold(0.dp) { acc, dp -> acc + dp }
//    } else {
//        defaultColumnWidth * state.firstStatsCategories.size
//    }
    val secondStatsColumnWidthList = state.secondStatsColumnWidthList

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    val secondStatsHorizontalScrollState = rememberScrollState()

    val density = LocalDensity.current
    var teamButtonWidth by remember { mutableStateOf(0.dp) }
    var isGameDetailVisible by remember { mutableStateOf(false) }

    val teamCategoryBarOffset by animateDpAsState(
        targetValue = if (state.teamCategorySelectedIndex == 0) {
            getOffsetOfAniCapsuleBar(itemWidth = teamButtonWidth)
        } else {
            2.dp + getOffsetOfAniCapsuleBar(itemWidth = teamButtonWidth, index = state.teamCategorySelectedIndex)
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    val firstStatsCategoryBarOffset by animateDpAsState(
        targetValue = if (state.firstStatsCategorySelectedIndex < 0) {
            val firstColumnWidth = state.firstColumnWidth ?: 132.dp
            -(firstColumnWidth / 2) - 10.dp
        } else if (columnWidthList.isNotEmpty()) {
            getOffsetOfAniCapsuleBar(itemWidths = columnWidthList, index = state.firstStatsCategorySelectedIndex)
        } else {
            getOffsetOfAniCapsuleBar(itemWidth = defaultColumnWidth, index = state.firstStatsCategorySelectedIndex)
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    val secondStatsCategoryBarOffset by animateDpAsState(
        targetValue = if (secondStatsColumnWidthList.isNotEmpty()) {
            getOffsetOfAniCapsuleBar(itemWidths = secondStatsColumnWidthList, index = state.secondStatsCategorySelectedIndex)
        } else {
            getOffsetOfAniCapsuleBar(itemWidth = defaultColumnWidth, index = state.secondStatsCategorySelectedIndex)
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    val gameDetailButtonAlpha by animateFloatAsState(
        targetValue = if (isGameDetailVisible) 0f else 0.6f,
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    CenterColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.shouldShowTitle) {
            this.titleContent()
        }

        if (state.shouldShowGameContent) {
            this.gameContent()
        }

        HDivider()

        Box {
            // NOTE: stickyHeader 사용해야해서 StandingsViewContainer 사용안하고 동일한 코드지만 여기서 직접 작성함.
            LazyColumn {
                if (state.shouldShowStats) {
                    item {
                        CenterRow {
                            // team button
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .height(42.dp)
                                ) {
                                    for ((index, item) in state.teamCategories.withIndex()) {
                                        CenterRow(
                                            modifier = Modifier
                                                .weight(1f)
                                                .onGloballyPositioned { layoutCoordinates ->
                                                    with(density) {
                                                        teamButtonWidth =
                                                            layoutCoordinates.size.width.toDp()
                                                    }
                                                }
                                                .clickable {
                                                    actions.teamCategoryButtonAction?.let { it(index) }
                                                }
                                        ) {
                                            URLImage(
                                                url = item.imageUrl,
                                                size = URLImageSize.SMALL
                                            )

                                            Text(
                                                text = item.name,
                                                textAlign = TextAlign.Center,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        }

                                        if (index == 0) {
                                            VCapsuleBar(modifier = Modifier.alpha(0.5f))
                                        }
                                    }
                                }

                                HCapsuleBar(
                                    modifier = Modifier
                                        .offset(x = teamCategoryBarOffset)
                                )
                            }

                            CenterColumn {
                                // game detail info button
                                CenterRow(
                                    modifier = Modifier
                                        .alpha(gameDetailButtonAlpha)
                                        .padding(bottom = 6.dp)
                                        .clickable {
                                            isGameDetailVisible = true
                                        }
                                        .border(
                                            BorderStroke(1.dp, Color.Gray),
                                            RoundedCornerShape(5.dp)
                                        )
                                        .padding(vertical = 2.dp, horizontal = 4.dp)
                                ) {
                                    Text(
                                        text = "경기 상세 정보",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center,
                                    )

                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_round_arrow_back_24),
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier
                                            .padding(start = 2.dp)
                                            .size(12.dp)
                                    )
                                }
//                                AnimatedVisibility(
//                                    visible = !isGameDetailVisible,
////                                enter = fadeIn() + expandHorizontally(),
//                                    exit = fadeOut() + shrinkHorizontally()
//                                ) {
//
//                                }

                                // refresh button
                                if (state.shouldShowRefreshButton) {
                                    // TODO: Make it component
                                    Box(
                                        Modifier
//                                .padding(end = UIConstants.Padding.DEFAULT_H_PADDING)
                                            .alpha(0.6f)
                                            .border(
                                                BorderStroke(1.dp, Color.Gray),
                                                RoundedCornerShape(10.dp)
                                            )
                                            .padding(2.dp)
                                            .clickable {
                                                actions.refreshButtonAction()
                                            }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_round_refresh_24),
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (state.shouldShowCoach) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp)
                            ) {
                                Text(
                                    text = "감독: ",
                                    fontSize = 15.sp
                                )

                                URLImage(
                                    url = coachState?.imageUrl,
                                    customSize = 23.dp
                                )

                                Text(
                                    text = coachState?.name ?: "",
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }

                    /* ---------------------
                       players stats
                       --------------------- */
                    state.firstStatsTitle?.let {
                        item {
                            Row {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(132.dp)
                                ) {
                                    Text(
                                        text = state.firstStatsTitle,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    HCapsuleBar()
                                }

                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }

                    stickyHeader {
                        Row(
                            modifier = Modifier.background(Color.White)
                        ) {
                            Box(
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                StandingsFirstCategoryItem(text = StringConstants.GAME_STATS_FIRST_CATEGORY, width = state.firstColumnWidth, onClick = actions.firstStatsTitleCategoryAction)

                                HCapsuleBar(
                                    modifier = Modifier.alpha(if (state.firstStatsCategorySelectedIndex < 0) 1f else 0f)
                                )
                            }

                            Row(
                                Modifier.horizontalScroll(horizontalScrollState)
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        for ((index, item) in state.firstStatsCategories.withIndex()) {
                                            CenterBox(
                                                height = 42.dp,
                                                modifier = Modifier
                                                    .conditionalClickable(item.isNotBlank()) {
                                                        actions.firstStatsCategoryButtonAction(index)
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
                                            .offset(x = firstStatsCategoryBarOffset)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row {
                            Column(
                                modifier = Modifier.padding(bottom = 10.dp)
                            ) {
                                for ((index, item) in state.firstStatsPlayerList.withIndex()) {
                                    StandingsRankItem(
                                        id = item.id,
                                        width = state.firstColumnWidth,
                                        shouldShowRank = item.numInfo != null,
                                        shouldShowExtraInfo = true,
                                        rank = item.numInfo ?: 0,
                                        imageUrl = item.imageUrl,
                                        isSvgLogo = item.isSvgLogo,
                                        name = item.name.dropFirstWord(),
                                        subName = item.subName,
                                        extraInfo = item.extraInfo,
                                        extraSubInfo = item.extraSubInfo,
                                        isLastItem = index == state.firstStatsPlayerList.size - 1,
                                        action = {}
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.horizontalScroll(horizontalScrollState)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    for ((index, item) in state.firstStatsPlayerList.withIndex()) {
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
                                    }
                                }
                            }
                        }
                    }

                    // 보여줄 Stats list가 두개인 경우의 두번째 Stats list. ex) KBO, MLB의 투수 기록
                    state.secondStatsCategories?.let {
                        state.secondStatsTitle?.let {
                            item {
                                Row {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(132.dp)
                                    ) {
                                        Text(
                                            text = state.secondStatsTitle,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium
                                        )

                                        HCapsuleBar()
                                    }

                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }

                        stickyHeader {
                            Row(
                                modifier = Modifier.background(Color.White)
                            ) {
                                StandingsFirstCategoryItem(text = StringConstants.GAME_STATS_FIRST_CATEGORY)

                                Row(
                                    Modifier.horizontalScroll(secondStatsHorizontalScrollState)
                                ) {
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            for ((index, item) in state.secondStatsCategories.withIndex()) {
                                                CenterBox(
                                                    height = 42.dp,
                                                    modifier = Modifier
                                                        .clickable {
                                                            actions.secondStatsCategoryButtonAction?.let { it(index) }
                                                        }
                                                ) {
                                                    Text(
                                                        text = item,
                                                        textAlign = TextAlign.Center,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        modifier = Modifier
                                                            .width(secondStatsColumnWidthList.getOrNull(index) ?: defaultColumnWidth)
                                                    )
                                                }
                                            }
                                        }

                                        HCapsuleBar(
                                            modifier = Modifier
                                                .offset(x = secondStatsCategoryBarOffset)
                                        )
                                    }
                                }
                            }
                        }

                        state.secondStatsPlayerList?.let {
                            item {
                                Row {
                                    Column(
                                        modifier = Modifier.padding(bottom = 10.dp)
                                    ) {
                                        for ((index, item) in state.secondStatsPlayerList.withIndex()) {
                                            StandingsRankItem(
                                                id = item.id,
                                                shouldShowRank = item.numInfo != null,
                                                shouldShowExtraInfo = true,
                                                imageUrl = item.imageUrl,
                                                isSvgLogo = item.isSvgLogo,
                                                name = item.name,
                                                subName = item.subName,
                                                extraInfo = item.extraInfo,
                                                extraSubInfo = item.extraSubInfo,
                                                isLastItem = index == state.secondStatsPlayerList.size - 1,
                                                action = {}
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.horizontalScroll(secondStatsHorizontalScrollState)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            for ((index, item) in state.secondStatsPlayerList.withIndex()) {
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
                                                                .width(secondStatsColumnWidthList.getOrNull(index) ?: defaultColumnWidth)
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
                } else {
                    item {
                        Text(
                            text = state.noStatsText ?: "경기 시작 후 데이터가 업데이트됩니다.",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )

//                        Spacer(Modifier.weight(1f))
                    }
                }
            } // LazyColumn

            // game detail info
            this@CenterColumn.AnimatedVisibility(
                visible = isGameDetailVisible,
                enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it })
            ) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        Modifier
                            .clickable {
                                isGameDetailVisible = false
                            }
                            .border(
                                BorderStroke(1.dp, Color.Gray),
                                RoundedCornerShape(5.dp)
                            )
                            .background(Color.White) // .padding(2.dp) 보다 아래에 있으면 padding공간은 background가 적용이 안됨
                            .padding(2.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_arrow_forward_ios_24),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .heightIn(min = 80.dp)
                            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(5.dp))
                            .background(Color.White)
                            .padding(vertical = 2.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = state.gameDetailTitle,
                            fontSize = 12.sp,
                            lineHeight = 20.sp, // NOTE: title과 content의 정렬을 맞추기 위해 추가
                            color = Color.Gray
                        )

                        Text(
                            text = state.gameDetailContent,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        } // Box
    }
}