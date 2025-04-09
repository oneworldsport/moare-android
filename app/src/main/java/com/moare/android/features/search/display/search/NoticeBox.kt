package com.moare.android.features.search.display.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.UIConstants
import com.moare.android.features.search.models.NoticeModel

@Composable
fun NoticeBox(
    notice: List<NoticeModel>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(UIConstants.CornerRadius.small))
            .height(100.dp)
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(10.dp)
        ) {
            for ((index, item) in notice.withIndex()) {
                Text(
                    text = if (index == 0) item.title else "\n${item.title}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )

                Text(
                    text = item.content,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
//            Text(
//                text = "• 챔피언스리그 24/25" +
//                        "\n• KBO 리그 2025" +
//                        "\n• MLB 2025",
//                fontSize = 12.sp,
//                color = Color.Gray
//            )
        }
    }
}