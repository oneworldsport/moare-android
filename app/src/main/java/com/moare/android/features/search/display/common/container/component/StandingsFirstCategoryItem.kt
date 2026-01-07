package com.moare.android.features.search.display.common.container.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.StringConstants
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.util.nullableConditionalClickable

@Composable
fun StandingsFirstCategoryItem(
    text: String = StringConstants.STANDINGS_FIRST_CATEGORY,
    width: Dp? = null,
    height: Dp = 44.dp,
    onClick: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(width ?: 132.dp)
            .height(height)
            .nullableConditionalClickable(onClick = onClick)
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        VCapsuleBar(
            modifier = Modifier.alpha(0.5f),
            bottomRound = false
        )
    }
}