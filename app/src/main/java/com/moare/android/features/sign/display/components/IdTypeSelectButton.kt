package com.moare.android.features.sign.display.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.ui.components.HCapsuleBar
import com.moare.android.ui.components.VCapsuleBar
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar
import com.moare.android.ui.util.screenWidthDp

@Composable
fun IdTypeSelectButton(
    selectedIndex: Int,
    action: (Int) -> Unit
) {
    val buttonLabels = listOf("이메일", "전화번호")

    val buttonWidth = (screenWidthDp() / 2) - 8.dp // horizontal padding 8.dp
    val buttonBarOffset by animateDpAsState(
        targetValue = if (selectedIndex == 0) {
            getOffsetOfAniCapsuleBar(itemWidth = buttonWidth)
        } else {
            2.dp + getOffsetOfAniCapsuleBar(itemWidth = buttonWidth, index = selectedIndex)
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(37.dp)
        ) {
            for ((index, item) in buttonLabels.withIndex()) {
                Text(
                    text = item,
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 37.sp,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            action(index)
                        }
                )

                if (index == 0) {
                    VCapsuleBar(modifier = Modifier.alpha(0.5f))
                }
            }
        }

        HCapsuleBar(
            modifier = Modifier
                .offset(x = buttonBarOffset)
        )
    }
}