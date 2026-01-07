package com.moare.android.ui.util

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.clickableCapsuleRippleEffect(
    onClick: () -> Unit
): Modifier {
    var isPressed by remember { mutableStateOf(false) }

    return this
        .border(
            BorderStroke(2.dp, if (isPressed) MaterialTheme.colors.primary else Color.Transparent),
            RoundedCornerShape(20.dp)
        )
        .padding(horizontal = 10.dp)
        .clickable(onClick = onClick)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    val isReleasedInside = tryAwaitRelease()
                    isPressed = false

                    if (isReleasedInside) {
                        onClick() // Only trigger click if released inside
                    }
                }
            )
        }
}

fun Modifier.conditionalFillMaxWidth(apply: Boolean): Modifier {
    return if (apply) this.fillMaxWidth() else this
}

fun Modifier.nullableMaxHeight(height: Dp?): Modifier {
    return height?.let { this.height(height) } ?: this.fillMaxHeight()
}

fun Modifier.nullableHeight(height: Dp?): Modifier {
    return height?.let { this.height(height) } ?: this
}

fun Modifier.conditionalClickable(apply: Boolean, onClick: (() -> Unit)): Modifier {
    return if (apply) this.clickable(onClick = onClick) else this
}

fun Modifier.nullableConditionalClickable(apply: Boolean = true, onClick: (() -> Unit)?): Modifier {
    return onClick?.let { if (apply) this.clickable(onClick = onClick) else this } ?: this
}

fun Modifier.conditionalBorder(
    enabled: Boolean,
    width: Dp = 1.dp,
    color: Color = Color.Black,
    shape: Shape = RoundedCornerShape(5.dp),
) = if (enabled) border(width, color, shape) else this

fun RowScope.conditionalWeight(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    weight: Float = 1f,
    fill: Boolean = true,
): Modifier = if (enabled) modifier.weight(weight, fill) else modifier

/**
 * Since this is not a Modifier extension, it must be used to create Modifier initially,
 * rather than chaining onto an existing Modifier.
 */
fun RowScope.optionalWeight(weight: Float, apply: Boolean): Modifier {
    return if (apply) Modifier.weight(weight) else Modifier
}

/**
 * Since this is not a Modifier extension, it must be used to create Modifier initially,
 * rather than chaining onto an existing Modifier.
 */
fun ColumnScope.optionalWeight(weight: Float, apply: Boolean): Modifier {
    return if (apply) Modifier.weight(weight) else Modifier
}

fun Modifier.nullableSize(size: DpSize?): Modifier {
    return size?.let { this.size(it) } ?: this
}

//fun RowScope.optionalWeight(weight: Float, apply: Boolean): Modifier {
//    return if (apply) Modifier.weight(weight) else Modifier
//}
//
//fun ColumnScope.optionalWeight(weight: Float, apply: Boolean): Modifier {
//    return if (apply) Modifier.weight(weight) else Modifier
//}

