package com.moare.android.ui.common.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

enum class URLImageSize {
    SMALL, MEDIUM, LARGE
}

@Composable
fun URLImage(
    url: String?,
    modifier: Modifier = Modifier,
    size: URLImageSize = URLImageSize.MEDIUM,
    customSize: Dp? = null
) {
    val imageSize = customSize ?: when (size) {
        URLImageSize.SMALL -> 30.dp
        URLImageSize.MEDIUM -> 50.dp
        URLImageSize.LARGE -> 80.dp
    }

    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = modifier
            .size(imageSize),
        contentScale = ContentScale.Crop
    )
}