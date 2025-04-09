package com.moare.android.ui.common.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.svg.SvgDecoder
import com.moare.android.core.di.EntryPoint
import dagger.hilt.android.EntryPointAccessors

enum class URLImageSize {
    SMALL, MEDIUM, LARGE
}

@Composable
fun URLImage(
    url: String?,
    modifier: Modifier = Modifier,
    size: URLImageSize = URLImageSize.MEDIUM,
    customSize: Dp? = null,
    isSvg: Boolean = false
) {
    val imageLoader = EntryPointAccessors.fromApplication(
        LocalContext.current,
        EntryPoint::class.java
    ).imageLoader()

    val imageSize = customSize ?: when (size) {
        URLImageSize.SMALL -> 30.dp
        URLImageSize.MEDIUM -> 50.dp
        URLImageSize.LARGE -> 80.dp
    }

    if (isSvg) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = modifier
                .size(imageSize),
            contentScale = ContentScale.Fit,
            imageLoader = imageLoader
        )
    } else {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = modifier
                .size(imageSize),
            contentScale = ContentScale.Fit
        )
    }
}