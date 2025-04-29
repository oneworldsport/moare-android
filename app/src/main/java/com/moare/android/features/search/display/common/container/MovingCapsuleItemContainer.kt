package com.moare.android.features.search.display.common.container

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.nullableSize
import com.moare.android.ui.util.optionalFillMaxWidth
import kotlin.math.roundToInt

@Composable
fun MovingCapsuleItemContainer(
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    updateItemPosition: ((LayoutCoordinates) -> Unit)? = null,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val position = itemPosition ?: Offset.Zero
    val animatedPosition by animateOffsetAsState(
        targetValue = if (aniPosition) position else Offset.Zero,
        animationSpec = tween(1000),
    )

    CenterColumn(
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
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
    ) {
        this.content()
    }

}