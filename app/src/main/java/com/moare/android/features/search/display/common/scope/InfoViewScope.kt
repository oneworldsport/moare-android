package com.moare.android.features.search.display.common.scope

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize

class InfoViewScope(
    val density: Density,
    val parentPosition: Offset,
    val parentCenter: Offset,
    val itemSizes: MutableMap<Int, DpSize>,
    val itemPositions: MutableMap<Int, Offset>, // STUDY: 참조 타입. 값 공유. setter 필요 X.
    val aniPositions: Boolean, // STUDY: 값 타입. 값 복사. 같은 값 공유하려면 setter(여기서는 updateAniPositions) 필요.
    val showContents: Boolean,
    val contentsAlpha: Float,
//    private val updateParentPosition: (Offset) -> Unit,
//    private val updateParentCenter: (Offset) -> Unit,
//    private val updateAniPositions: (Boolean) -> Unit,
//    private val updateShowContents: (Boolean) -> Unit,
    private val updateItemSizeInternal: (Int, DpSize) -> Unit,
    private val updateItemPositionInternal: (Int, Offset) -> Unit // STUDY: 하지만 참조 타입을 직접적으로 변경하는 개발방식은 일반적이지 않기 때문에, 참조 타입이어도 값 타입처럼 setter를 활용.
) {
    fun updateItemPosition(index: Int, coordinates: LayoutCoordinates) {
        val itemSize = coordinates.size
        val position = coordinates.positionInWindow()

        val relativeX = position.x - parentPosition.x
        val relativeY = position.y - parentPosition.y

        // Calculate the center of the InfoItem
        val centerX = relativeX + itemSize.width / 2f
        val centerY = relativeY + itemSize.height / 2f

        val itemDpSize = with(density) {
            DpSize(itemSize.width.toDp(), itemSize.height.toDp())
        }
        val itemPosition = Offset(centerX - parentCenter.x, centerY - parentCenter.y)

        updateItemSizeInternal(index, itemDpSize)
        updateItemPositionInternal(index, itemPosition)
    }

//    fun setItemPosition(index: Int, offset: Offset) {
//        updateItemPositionInternal(index, offset)
//    }
}