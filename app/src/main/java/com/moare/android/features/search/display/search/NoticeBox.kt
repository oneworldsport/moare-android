package com.moare.android.features.search.display.search

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.moare.android.R
import com.moare.android.core.constants.UIConstants
import com.moare.android.features.search.models.NoticeModel
import com.moare.android.ui.util.CenterRow
import com.moare.android.ui.util.nullableHeight
import com.moare.android.ui.util.nullableMaxHeight

@Composable
fun NoticeBox(
    noticeList: List<NoticeModel>,
    height: Dp,
    modifier: Modifier = Modifier,
    onHeightChange: (Dp) -> Unit
) {
    val density = LocalDensity.current
    val maxHeight = 240.dp

    Box(
        modifier
            .onGloballyPositioned { layoutCoordinates ->
                with(density) {
                    onHeightChange(min(layoutCoordinates.size.height.toDp(), maxHeight))
                }
            }
            .heightIn(max = maxHeight)
            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(UIConstants.CornerRadius.SMALL))
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(6.dp)
        ) {
            noticeList.forEachIndexed { index, notice ->
                Text(
                    text = if (index == 0) notice.title else "\n${notice.title}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                notice.sports?.forEach { sport ->
                    NoticeSection(sport.category, sport.content)
                }
            }
        }
    }
}

@Composable
fun NoticeSection(
    category: String,
    content: String
) {
    var isOpened by remember { mutableStateOf(false) }

    CenterRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable { isOpened = !isOpened }
    ) {
        Text(
            text = category,
            fontSize = 12.sp,
            color = Color.Gray
        )

        Box(
            Modifier
                .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(10.dp))
        ) {
            Icon(
                painter = painterResource(id = if (isOpened) R.drawable.ic_round_arrow_drop_up_24 else R.drawable.ic_round_arrow_drop_down_24),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.height(17.dp).width(24.dp)
            )
        }
    }

    if (isOpened) {
        Text(
            text = content,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun SearchExampleBox(
    text: String,
    modifier: Modifier = Modifier,
    onHeightChange: (Dp) -> Unit
) {
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                with(density) {
                    onHeightChange(layoutCoordinates.size.height.toDp())
                }
            }
            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(UIConstants.CornerRadius.SMALL))
            .padding(6.dp)

    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}














