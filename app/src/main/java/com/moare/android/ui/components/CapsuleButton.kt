package com.moare.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.ui.theme.MoareAndroidTheme
import com.moare.android.ui.util.optionalClickable

@Composable
fun CapsuleButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    isDisabled: Boolean = false,
    borderWidth: Dp = 1.dp,
    fontSize: Int = 12,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = fontSize.sp,
        color = color,
        textAlign = TextAlign.Center,
        modifier = modifier
            .border(BorderStroke(borderWidth, color), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .optionalClickable(!isDisabled, onClick)
    )
}

@Preview(showBackground = true)
@Composable
fun CapsuleButtonPreview() {
    MoareAndroidTheme {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CapsuleButton("경기 종료") {

            }
        }
    }
}