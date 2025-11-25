package com.moare.android.features.userprofile.display.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.moare.android.features.userprofile.display.UserProfileStackAction
import com.moare.android.features.userprofile.display.UserProfileViewType
import com.moare.android.features.userprofile.display.store.UserProfileImageEditAction
import com.moare.android.features.userprofile.display.store.UserProfileImageEditStore
import com.moare.android.ui.components.BackButton

@Composable
fun UserProfileImageEditView(
    store: UserProfileImageEditStore
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val context = LocalContext.current

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val minScale = 1f
    val maxScale = 4f

    val screenMinDp = minOf(configuration.screenWidthDp, configuration.screenHeightDp).dp
    val sideDp = screenMinDp * 0.9f
    val cropSizePx = with(density) { sideDp.roundToPx() }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton {
                store.send(UserProfileImageEditAction.GoBack)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "완료",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable {
                        store.send(UserProfileImageEditAction.Complete(context, scale, offset, cropSizePx))
                    }
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .size(sideDp)
                    .background(Color.Black)
            ) {
                val boxWidth = constraints.maxWidth.toFloat()
                val boxHeight = constraints.maxHeight.toFloat()

                fun clampOffset(raw: Offset, scale: Float): Offset {
                    val maxX = (boxWidth * (scale - 1f)) / 2f
                    val maxY = (boxHeight * (scale - 1f)) / 2f
                    return Offset(
                        x = raw.x.coerceIn(-maxX, maxX),
                        y = raw.y.coerceIn(-maxY, maxY)
                    )
                }

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clipToBounds()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                val newScale = (scale * zoom).coerceIn(minScale, maxScale)
                                val newOffset = clampOffset(offset + pan, newScale)
                                scale = newScale
                                offset = newOffset
                            }
                        }
                ) {
                    AsyncImage(
                        model = store.uri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offset.x
                                translationY = offset.y
                            }
                    )

                    DimmingOverlay(Modifier.matchParentSize())
                }
            }
        }
    }
}

@Composable
fun DimmingOverlay(
    modifier: Modifier
) {
    Canvas(
        modifier = modifier
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    ) {
        val radius = minOf(size.width, size.height) / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        drawRect(Color.Black.copy(alpha = 0.3f))

        drawCircle(
            color = Color.Transparent,
            radius = radius,
            center = center,
            blendMode = BlendMode.Clear
        )
    }
}






















