package com.moare.android.ui.animation.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

@Composable
fun RoundedRectWithPathAni(
    width: Dp,
    height: Dp,
    cornerRadius: CornerRadius,
    strokeWidth: Float,
    drawPath: Boolean
) {
    val focusedColor = MaterialTheme.colors.primary

    val animatedProgress by animateFloatAsState(
        targetValue = if (drawPath) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearOutSlowInEasing
        )
    )

    Canvas(
        modifier = Modifier
            .width(width)
            .height(height)
    ) {
        val pathWidth = size.width - strokeWidth
        val pathHeight = size.height - strokeWidth
        val halfStrokeWidth = strokeWidth / 2
        val cornerArcLength = (Math.PI * cornerRadius.x / 2).toFloat()

        val totalPathLength = pathWidth + pathHeight + 4 * cornerArcLength
        val currentProgressLength = totalPathLength * animatedProgress

        val insetRect = Rect(
            left = halfStrokeWidth,
            top = halfStrokeWidth,
            right = size.width - halfStrokeWidth,
            bottom = size.height - halfStrokeWidth
        )

        val rightPath = Path().apply {
            moveTo(insetRect.left + pathWidth / 2, insetRect.bottom)

            val bottomLength = minOf(pathWidth / 2 - cornerRadius.x, currentProgressLength)
            var accumulatedLength = bottomLength

            // Bottom side
            lineTo(insetRect.left + pathWidth / 2 + bottomLength, insetRect.bottom)

            // Bottom right corner
            if (currentProgressLength > accumulatedLength) {
                val bottomRightCornerLength =
                    minOf(cornerArcLength, currentProgressLength - accumulatedLength)
                arcTo(
                    rect = Rect(
                        insetRect.bottomRight.x - 2 * cornerRadius.x,
                        insetRect.bottomRight.y - 2 * cornerRadius.y,
                        insetRect.bottomRight.x,
                        insetRect.bottomRight.y
                    ),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = -90f * (bottomRightCornerLength / cornerArcLength),
                    forceMoveTo = false
                )
                accumulatedLength += bottomRightCornerLength
            }

            // Right side
            if (currentProgressLength > accumulatedLength) {
                val rightLength = minOf(
                    pathHeight - 2 * cornerRadius.y,
                    currentProgressLength - accumulatedLength
                )
                lineTo(
                    insetRect.topRight.x,
                    insetRect.bottomRight.y - cornerRadius.y - rightLength
                )
                accumulatedLength += rightLength
            }

            // Top right corner
            if (currentProgressLength > accumulatedLength) {
                val topRightCornerLength =
                    minOf(cornerArcLength, currentProgressLength - accumulatedLength)
                arcTo(
                    rect = Rect(
                        insetRect.topRight.x - 2 * cornerRadius.x,
                        insetRect.topRight.y,
                        insetRect.topRight.x,
                        insetRect.topRight.y + 2 * cornerRadius.y
                    ),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = -90f * (topRightCornerLength / cornerArcLength),
                    forceMoveTo = false
                )
                accumulatedLength += topRightCornerLength
            }

            // Top side
            if (currentProgressLength > accumulatedLength) {
                val topLength =
                    minOf(
                        pathWidth / 2 - cornerRadius.x,
                        currentProgressLength - accumulatedLength
                    )
                lineTo(
                    insetRect.bottomRight.x - cornerRadius.x - topLength,
                    insetRect.topRight.y
                )
                accumulatedLength += bottomLength
            }
        }

        val leftPath = Path().apply {
            moveTo(insetRect.left + pathWidth / 2, insetRect.bottom)

            val bottomLeftLength = minOf(pathWidth / 2 - cornerRadius.x, currentProgressLength)
            var accumulatedLeftLength = bottomLeftLength

            // Bottom side
            lineTo(insetRect.right - pathWidth / 2 - bottomLeftLength, insetRect.bottom)

            // Bottom left corner
            if (currentProgressLength > accumulatedLeftLength) {
                val bottomRightCornerLength = minOf(
                    cornerArcLength,
                    currentProgressLength - accumulatedLeftLength
                )
                arcTo(
                    rect = Rect(
                        insetRect.bottomLeft.x,
                        insetRect.bottomLeft.y - 2 * cornerRadius.y,
                        insetRect.bottomLeft.x + 2 * cornerRadius.x,
                        insetRect.bottomLeft.y
                    ),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = 90f * (bottomRightCornerLength / cornerArcLength),
                    forceMoveTo = false
                )
                accumulatedLeftLength += bottomRightCornerLength
            }

            // Left side
            if (currentProgressLength > accumulatedLeftLength) {
                val rightLength = minOf(
                    pathHeight - 2 * cornerRadius.y,
                    currentProgressLength - accumulatedLeftLength
                )
                lineTo(
                    insetRect.bottomLeft.x,
                    insetRect.bottomLeft.y - cornerRadius.y - rightLength
                )
                accumulatedLeftLength += rightLength
            }

            // Top right corner
            if (currentProgressLength > accumulatedLeftLength) {
                val topRightCornerLength = minOf(
                    cornerArcLength,
                    currentProgressLength - accumulatedLeftLength
                )
                arcTo(
                    rect = Rect(
                        insetRect.topLeft.x,
                        insetRect.topLeft.y,
                        insetRect.topLeft.x + 2 * cornerRadius.x,
                        insetRect.topLeft.y + 2 * cornerRadius.y
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f * (topRightCornerLength / cornerArcLength),
                    forceMoveTo = false
                )
                accumulatedLeftLength += topRightCornerLength
            }

            // Top side
            if (currentProgressLength > accumulatedLeftLength) {
                val topLength = minOf(
                    pathWidth / 2 - cornerRadius.x,
                    currentProgressLength - accumulatedLeftLength
                )
                lineTo(
                    insetRect.topLeft.x + cornerRadius.x + topLength,
                    insetRect.topLeft.y
                )
                accumulatedLeftLength += topLength
            }
        }

        if (drawPath) {
            drawPath(
                path = rightPath,
                color = focusedColor,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawPath(
                path = leftPath,
                color = focusedColor,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}

fun RoundedRectPath(
    width: Float,
    height: Float,
    cornerRadius: CornerRadius,
    strokeWidth: Float
): Path {
    val halfStrokeWidth = strokeWidth / 2

    val insetRect = Rect(
        left = halfStrokeWidth,
        top = halfStrokeWidth,
        right = width - halfStrokeWidth,
        bottom = height - halfStrokeWidth
    )

    return Path().apply {
        moveTo(insetRect.left + cornerRadius.x, insetRect.top)

        // Top side
        lineTo(insetRect.right - cornerRadius.x, insetRect.top)

        // Top-right corner
        arcTo(
            rect = Rect(
                insetRect.topRight.x - 2 * cornerRadius.x,
                insetRect.topRight.y,
                insetRect.topRight.x,
                insetRect.topRight.y + 2 * cornerRadius.y
            ),
            startAngleDegrees = 279f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Right side
        lineTo(insetRect.right, insetRect.bottom - cornerRadius.y)

        // Bottom-right corner
        arcTo(
            rect = Rect(
                insetRect.bottomRight.x - 2 * cornerRadius.x,
                insetRect.bottomRight.y - 2 * cornerRadius.y,
                insetRect.bottomRight.x,
                insetRect.bottomRight.y
            ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Bottom side
        lineTo(insetRect.left + cornerRadius.x, insetRect.bottom)

        // Bottom-left corner
        arcTo(
            rect = Rect(
                insetRect.bottomLeft.x,
                insetRect.bottomLeft.y - 2 * cornerRadius.y,
                insetRect.bottomLeft.x + 2 * cornerRadius.x,
                insetRect.bottomLeft.y
            ),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )

        // Left side
        lineTo(insetRect.left, insetRect.top + cornerRadius.y)

        // Top-left corner
        arcTo(
            rect = Rect(
                insetRect.topLeft.x,
                insetRect.topLeft.y,
                insetRect.topLeft.x + 2 * cornerRadius.x,
                insetRect.topLeft.y + 2 * cornerRadius.y
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
    }
}