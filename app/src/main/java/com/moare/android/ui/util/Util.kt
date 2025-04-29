package com.moare.android.ui.util

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
    index: Int = 0 // if it's 0, just get the bar offset from current item's start
): Dp {
    return (itemWidth * index) + ((itemWidth - barWidth) / 2)
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
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
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

































