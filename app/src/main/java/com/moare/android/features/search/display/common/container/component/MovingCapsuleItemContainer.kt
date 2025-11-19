package com.moare.android.features.search.display.common.container.component

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.moare.android.ui.components.HCapsuleBar
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.nullableOptionalClickable
import com.moare.android.ui.util.nullableSize
import kotlin.math.roundToInt

@Composable
fun MovingCapsuleItemContainer(
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    startPosition: Offset = Offset.Zero,
    aniPosition: Boolean = true,
    updateItemPosition: ((LayoutCoordinates) -> Unit)? = null,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val effectiveStartPosition = if (isAniItem) startPosition else Offset.Zero
    val position = itemPosition ?: effectiveStartPosition
    val animatedPosition by animateOffsetAsState(
        targetValue = if (aniPosition) position else effectiveStartPosition,
        animationSpec = tween(1000),
    )

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
//            .optionalFillMaxWidth(!isAniItem)
            .onGloballyPositioned { coordinates ->
                if (!isAniItem && updateItemPosition != null) {
                    updateItemPosition(coordinates)
                }
            }
            .nullableSize(itemSize)
            .offset {
                IntOffset(
                    animatedPosition.x.roundToInt(),
                    animatedPosition.y.roundToInt()
                )
            }
            .nullableOptionalClickable(apply = isAniItem, onClick = onClick)
    ) {
        HCapsuleBar()

        CenterColumn(
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            this.content()
        }
    }
}