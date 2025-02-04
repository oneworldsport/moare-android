package com.moare.android.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
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
    customHeight: Dp? = null
) {
    Box(
        if (customHeight != null) {
            modifier
                .height(customHeight)
                .width(2.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color)
        } else {
            modifier
                .width(2.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(10.dp))
                .background(color)
        }
    )
}