package com.moare.android.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class HCapsuleBarSize {
    SMALL, MEDIUM, LARGE
}

@Composable
fun HCapsuleBar(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    size: HCapsuleBarSize = HCapsuleBarSize.SMALL,
    customWidth: Dp? = null,
) {
    val barSize = customWidth ?: when (size) {
        HCapsuleBarSize.SMALL -> 20.dp
        HCapsuleBarSize.MEDIUM -> 50.dp
        HCapsuleBarSize.LARGE -> 80.dp
    }

    Box(
        modifier
            .width(barSize)
            .height(2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color)
    )
}

@Composable
fun VCapsuleBar(
    modifier: Modifier = Modifier,
    color: Color = Color.Gray,
    customHeight: Dp? = null,
    customWidth: Dp? = null,
    topRound: Boolean = true,
    bottomRound: Boolean = true
) {
    val cornerShape = RoundedCornerShape(
        topStart = if (topRound) 10.dp else 0.dp,
        topEnd = if (topRound) 10.dp else 0.dp,
        bottomStart = if (bottomRound) 10.dp else 0.dp,
        bottomEnd = if (bottomRound) 10.dp else 0.dp
    )
    val width: Dp = customWidth ?: 2.dp

    Box(
        if (customHeight != null) {
            modifier
                .height(customHeight)
                .width(width)
                .clip(cornerShape)
                .background(color)
        } else {
            modifier
                .width(width)
                .fillMaxHeight()
                .clip(cornerShape)
                .background(color)
        }
    )
}

@Composable
fun StatsDivider() {
    VCapsuleBar(
        modifier = Modifier.alpha(0.5f),
        customHeight = 40.dp,
        customWidth = 1.dp
    )
}

@Composable
fun HDivider(
    modifier: Modifier = Modifier,
    height: Dp = 1.dp,
    color: Color = MaterialTheme.colors.primary,
    alpha: Float = 1f
) {
    Box(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(height)
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .alpha(alpha)
    )
}


























