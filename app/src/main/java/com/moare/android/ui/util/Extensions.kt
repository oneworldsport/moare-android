package com.moare.android.ui.util

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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