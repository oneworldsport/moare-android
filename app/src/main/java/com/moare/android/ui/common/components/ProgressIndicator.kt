package com.moare.android.ui.common.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProgressIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.size(30.dp),
        strokeWidth = 2.dp
    )
}