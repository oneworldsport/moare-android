package com.moare.android.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
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

// TODO: CenterColumn이랑 CenterRow를 만들까?