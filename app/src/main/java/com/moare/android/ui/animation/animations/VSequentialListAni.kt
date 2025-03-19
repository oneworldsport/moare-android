package com.moare.android.ui.animation.animations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun <T> VSequentialListAni(
    items: List<T>,
    modifier: Modifier = Modifier,
    aniDelay: Int = 100,
    aniDuration: Int = 500,
    content: @Composable (Int, T) -> Unit
) {
//    LazyColumn(
//        modifier = modifier.fillMaxWidth(),
//        userScrollEnabled = false
//    ) {
//        itemsIndexed(items) { index, item ->
//            var isVisible by remember { mutableStateOf(false) }
//
//            LaunchedEffect(index) {
//                delay((index * aniDelay).toLong())
//                isVisible = true
//            }
//
//            AnimatedVisibility(
//                visible = isVisible,
//                enter = fadeIn(animationSpec = tween(durationMillis = aniDuration)) +
//                        slideInVertically(animationSpec = tween(durationMillis = aniDuration)) { fullHeight -> -fullHeight }
//            ) {
//                content(item)
//            }
//        }
//    }
    Column (
        modifier = modifier.fillMaxWidth(),
    ) {
        for ((index, value) in items.withIndex()) {
            var isVisible by remember { mutableStateOf(false) }

            LaunchedEffect(index) {
                delay((index * aniDelay).toLong())
                isVisible = true
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = aniDuration)) +
                        slideInVertically(animationSpec = tween(durationMillis = aniDuration)) { fullHeight -> -fullHeight }
            ) {
                content(index, value)
            }
        }
    }
}