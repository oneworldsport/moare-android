package com.moare.android.features.search.display.common.container.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.features.search.display.common.scope.InfoViewScope
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import kotlinx.coroutines.delay

/**
 * Composable container for InfoView.
 *
 * @param measureContent Invisible UI used only for measuring item positions.
 * @param displayContent Visible UI shown using animation with the measured positions.
 */
@Composable
fun InfoViewContainer(
    searchViewModel: SearchViewModel = hiltViewModel(),
    itemCount: Int,
    modifier: Modifier = Modifier,
    measureContent: @Composable InfoViewScope.() -> Unit,
    displayContent: @Composable InfoViewScope.() -> Unit
) {
    /* ---------------------
       ui state
       --------------------- */
    val density = LocalDensity.current
    var parentPosition by remember { mutableStateOf(Offset.Zero) }
    var parentCenter by remember { mutableStateOf(Offset.Zero) }
    val itemSizes = remember { mutableStateMapOf<Int, DpSize>() } // NOTE: parentCenter를 기준으로 이동해야할 각 아이템들의 offset
    val itemPositions = remember { mutableStateMapOf<Int, Offset>() }
    var aniPositions by remember { mutableStateOf(false) }
    var showContents by remember { mutableStateOf(false) }
    val contentsAlpha by animateFloatAsState(
        targetValue = if (showContents) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    /* ---------------------
       viewmodel state
       --------------------- */
    val poppedView by searchViewModel.poppedView.collectAsState()

    val scope = InfoViewScope(
        density = density,
        itemSizes = itemSizes,
        parentPosition = parentPosition,
        parentCenter = parentCenter,
        itemPositions = itemPositions,
        aniPositions = aniPositions,
        showContents = showContents,
        contentsAlpha = contentsAlpha,
        updateItemSizeInternal = { index, size -> itemSizes[index] = size },
        updateItemPositionInternal = { index, offset -> itemPositions[index] = offset }
//        updateParentPosition = { parentPosition = it },
//        updateParentCenter = { parentCenter = it },
//        updateAniPositions = { aniPositions = it },
//        updateShowContents = { showContents = it }
    )

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(itemPositions.size) {
        if (itemPositions.size == itemCount) {
            aniPositions = true
            delay(1000)
            showContents = true
        }
    }

    /* ---------------------
       ui
       --------------------- */
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .onGloballyPositioned { coordinates ->
                    parentPosition = coordinates.positionInWindow()
                    parentCenter = Offset(
                        x = coordinates.size.width / 2f,
                        y = coordinates.size.height / 2f
                    )
                }
                .alpha(0f)
        ) {
            scope.measureContent()
        }

        scope.displayContent()
    }
}












