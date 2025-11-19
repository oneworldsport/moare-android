package com.moare.android.ui.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moare.android.ui.theme.Moare

@Composable
fun BackButton(
    size: Dp = 30.dp,
    onClick: () -> Unit
) {
    Icon(
        imageVector = Icons.Rounded.ArrowBackIosNew,
        contentDescription = null,
        modifier = Modifier
            .size(size)
            .padding(start = 8.dp)
            .clickable(onClick = onClick),
        tint = Moare
    )
}