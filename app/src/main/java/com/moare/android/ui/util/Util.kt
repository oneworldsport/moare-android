package com.moare.android.ui.util

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// TODO: Composable 이외에서도 사용할 수 있게 수정 필요
@Composable
fun convertDpToPx(dpValue: Dp): Float {
    val density = LocalDensity.current
    return with(density) { dpValue.toPx() }
}

@Composable
fun convertPxToDp(pxValue: Float): Dp {
    val density = LocalDensity.current
    return with(density) { pxValue.toDp() }
}

//fun convertPxToDp(pxValue: Float, density: Density): Dp {
//    return with(density) { pxValue.toDp() }
//}

@Composable
fun screenWidthDp(): Dp {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp.dp
}

@Composable
fun screenHeightDp(): Dp {
    val configuration = LocalConfiguration.current
    return configuration.screenHeightDp.dp
}
@Composable
fun screenWidthPx(): Float {
    return convertDpToPx(screenWidthDp())
}

@Composable
fun screenHeightPx(): Float {
    return convertDpToPx(screenHeightDp())
}

@Composable
fun getOffsetOfAniCapsuleBar(
    itemWidth: Dp,
    barWidth: Dp = 20.dp,
    spacing: Dp = 0.dp,
    index: Int = 0 // if it's 0, just get the bar offset from current item's start
): Dp {
    return (itemWidth * index) + ((itemWidth - barWidth) / 2) + (spacing * index)
}

/**
 * Calculate the CapsuleBar position for each item, considering different item widths.
 *
 * Example (barWidth = 20.dp):
 * - Index 0: (itemWidths[0] - 20) / 2
 * - Index 1: itemWidths[0] + (itemWidths[1] - 20) / 2
 * - Index 2: itemWidths[0] + itemWidths[1] + (itemWidths[2] - 20) / 2
 * - Index 3: itemWidths[0] + itemWidths[1] + itemWidths[2] + (itemWidths[3] - 20) / 2
 */
@Composable
fun getOffsetOfAniCapsuleBar(
    itemWidths: List<Dp>,
    barWidth: Dp = 20.dp,
    index: Int
): Dp {
    // 앞 아이템들의 width 합
    val totalPreviousWidth = itemWidths.take(index).fold(0.dp) { acc, dp -> acc + dp }

    // 현재 아이템의 위치에서 가운데에 bar를 정렬
    val centerOffset = ((itemWidths.getOrNull(index) ?: 0.dp) - barWidth) / 2

    return totalPreviousWidth + centerOffset
}

@Composable
fun rememberKeyboardVisibility(): State<Boolean> {
    val context = LocalContext.current
    val rootView = LocalView.current
    val isKeyboardVisible = remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.height
            val keypadHeight = screenHeight - rect.bottom
            isKeyboardVisible.value = keypadHeight > screenHeight * 0.15 // Adjust threshold if needed
        }

        rootView.viewTreeObserver.addOnGlobalLayoutListener(listener)

        onDispose {
            rootView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    return isKeyboardVisible
}

@Composable
fun CenterRow(
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        modifier = modifier,
        content = content
    )
}

@Composable
fun CenterColumn(
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        modifier = modifier,
        content = content
    )
}

// TODO: Text() 컴포넌트 수직 가운데 정렬할때 사용하려고 해당 함수 만들었는데, Text에 lineHeight 적용하니깐 수직 가운데 정렬 돼서, 해당 속성 적용하는걸로 리팩토링 필요.
@Composable
fun CenterBox(
    modifier: Modifier = Modifier,
    height: Dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.height(height),
        content = content
    )
}

@Composable
fun Refreshable(
    enabled: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (enabled) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
        ) { content() }
    } else {
        content()
    }
}

































