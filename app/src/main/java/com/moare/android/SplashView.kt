package com.moare.android

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moare.android.ui.theme.MoareAndroidTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashView(
    onComplete: () -> Unit
) {
    /* ---------------------
       constants
       --------------------- */
    val firstY = 40f
    val topX = 42f
    val topY = 11f
    val bottomX = 24f
    val bottomY = 40f

    /* ---------------------
       animation
       --------------------- */
    val offset1 = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val offset2 = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val offset3 = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val offset4 = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val offset5 = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    var isCircleVisible by remember { mutableStateOf(true) }
    var isLogoVisble by remember { mutableStateOf(false) }

    val firstOpenDelay: Long = 400
    val ofssetAniDuration = 500
    val fadeInOutAniDuration = 500
    val fadeOutAniDuration = 400
    val animationEasing = LinearOutSlowInEasing

//    val offset1 = remember { Animatable(Offset(0f, -firstY), Offset.VectorConverter) }
//    val offset2 = remember { Animatable(Offset(topX, -topY), Offset.VectorConverter) }
//    val offset3 = remember { Animatable(Offset(bottomX, bottomY), Offset.VectorConverter) }
//    val offset4 = remember { Animatable(Offset(-bottomX, bottomY), Offset.VectorConverter) }
//    val offset5 = remember { Animatable(Offset(-topX, -topY), Offset.VectorConverter) }

    LaunchedEffect(Unit) {
        delay(firstOpenDelay)

        offset1.animateTo(
            Offset(0f, -firstY),
            tween(ofssetAniDuration, easing = animationEasing)
        )
    }
    LaunchedEffect(Unit) {
        delay(firstOpenDelay)

        offset2.animateTo(
            Offset(topX, -topY),
            tween(ofssetAniDuration, easing = animationEasing)
        )
    }
    LaunchedEffect(Unit) {
        delay(firstOpenDelay)

        offset3.animateTo(
            Offset(bottomX, bottomY),
            tween(ofssetAniDuration, easing = animationEasing)
        )
    }
    LaunchedEffect(Unit) {
        delay(firstOpenDelay)

        offset4.animateTo(
            Offset(-bottomX, bottomY),
            tween(ofssetAniDuration, easing = animationEasing)
        )
    }
    LaunchedEffect(Unit) {
        delay(firstOpenDelay)

        offset5.animateTo(
            Offset(-topX, -topY),
            tween(ofssetAniDuration, easing = animationEasing)
        )

        // NOTE: ofssetAniDuration.toLong()으로 했을때 뭔가 더 delay가 있이 보여서 더 적게줌
        delay((ofssetAniDuration - 100).toLong())

        isCircleVisible = false
        isLogoVisble = true

        // NOTE: faede animation 끝나고 200ms동안 멈춤
        delay((fadeInOutAniDuration + 200).toLong())

        isLogoVisble = false

        delay(fadeOutAniDuration.toLong())

        onComplete()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isCircleVisible,
            exit = fadeOut(tween(fadeInOutAniDuration)),
        ) {
            Box(
                Modifier.fillMaxSize()
            ) {
                Box(
                    Modifier
                        .offset(x = offset1.value.x.dp, y = offset1.value.y.dp)
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(6.dp, MaterialTheme.colors.primary, CircleShape)
                        .background(Color.Transparent)
                        .align(Alignment.Center)
                )
                Box(
                    Modifier
                        .offset(x = offset2.value.x.dp, y = offset2.value.y.dp)
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(6.dp, MaterialTheme.colors.primary, CircleShape)
                        .background(Color.Transparent)
                        .align(Alignment.Center)
                )
                Box(
                    Modifier
                        .offset(x = offset3.value.x.dp, y = offset3.value.y.dp)
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(6.dp, MaterialTheme.colors.primary, CircleShape)
                        .background(Color.Transparent)
                        .align(Alignment.Center)
                )
                Box(
                    Modifier
                        .offset(x = offset4.value.x.dp, y = offset4.value.y.dp)
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(6.dp, MaterialTheme.colors.primary, CircleShape)
                        .background(Color.Transparent)
                        .align(Alignment.Center)
                )
                Box(
                    Modifier
                        .offset(x = offset5.value.x.dp, y = offset5.value.y.dp)
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(6.dp, MaterialTheme.colors.primary, CircleShape)
                        .background(Color.Transparent)
                        .align(Alignment.Center)
                )
            }
        }

        AnimatedVisibility(
            visible = isLogoVisble,
            enter = fadeIn(tween(fadeInOutAniDuration)),
            exit = fadeOut(tween(fadeOutAniDuration)),
        ) {
            FlowerShape(firstY, topX, topY, bottomX, bottomY)
        }
    }
}

@Composable
fun FlowerShape(
    firstY: Float,
    topX: Float,
    topY: Float,
    bottomX: Float,
    bottomY: Float
) {
    val color = MaterialTheme.colors.primary

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = 33.dp.toPx()

        val petalCenters = listOf(
            Offset(centerX, centerY - firstY.dp.toPx()),
            Offset(centerX + topX.dp.toPx(), centerY - topY.dp.toPx()),
            Offset(centerX + bottomX.dp.toPx(), centerY + bottomY.dp.toPx()),
            Offset(centerX - bottomX.dp.toPx(), centerY + bottomY.dp.toPx()),
            Offset(centerX - topX.dp.toPx(), centerY - topY.dp.toPx())
        )

//        val petalAngleStart = listOf(-170f, -110f, -30f, 40f, 110f)
//        val petalAngleEnd = listOf(-10f, 70f, 140f, 210f, 290f)
        val petalAngles = listOf(
            -180f to -0f,
            -110f to 78f,
            -40f to 140f,
            40f to 220f,
            102f to 290f
        )

        petalCenters.forEachIndexed { index, center ->
            drawArc(
                color = color, // Outline color
                startAngle = petalAngles[index].first,
                sweepAngle = petalAngles[index].second - petalAngles[index].first,
                useCenter = false, // Ensures it's an arc and not a filled shape
                style = Stroke(width = 6.dp.toPx()), // Outline stroke width
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashViewPreview() {
    MoareAndroidTheme {
        SplashView({})
    }
}