package com.moare.android.features.search.display.common.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FBStatDataItem(
    category: String,
    data: String,
    customCategoryFontSize: Int? = null,
    customDataFontSize: Int? = null,
    customWidth: Dp? = null,
    customCategoryHeight: Dp? = null,
    modifier: Modifier = Modifier,
) {
    Column(
//        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(customWidth ?: 50.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.height(customCategoryHeight ?: 25.dp)
        ) {
            Text(
                text = category,
                fontSize = (customCategoryFontSize ?: 15).sp
            )
        }

        // NOTE: use Box to position text at center
        // if Text has height, the text inside cannot be positioned at center
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.height(30.dp)
        ) {
            Text(
                text = data,
                fontSize = (customDataFontSize ?: 16).sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}