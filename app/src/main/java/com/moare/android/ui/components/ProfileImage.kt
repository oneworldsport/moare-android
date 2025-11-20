package com.moare.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.moare.android.ui.theme.MoareAndroidTheme

enum class ProfileImageSize {
    SMALL, MEDIUM, BIG
}

@Composable
fun ProfileImage(
    url: String?,
    modifier: Modifier = Modifier,
    size: ProfileImageSize = ProfileImageSize.MEDIUM,
    customSize: Dp? = null
) {
    val imageSize = customSize ?: when (size) {
        ProfileImageSize.SMALL -> 30.dp
        ProfileImageSize.MEDIUM -> 50.dp
        ProfileImageSize.BIG -> 80.dp
    }

    if (url != null) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = modifier
                .size(imageSize)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        DefaultProfileImage(imageSize, modifier)
    }
}

@Composable
fun DefaultProfileImage(
    size: Dp,
    modifier: Modifier
) {
    Icon(
        imageVector = Icons.Outlined.AccountCircle,
        contentDescription = null,
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        tint = Color.Gray
    )
}

@Composable
fun UpdateFormProfileImage(
    url: String?,
    modifier: Modifier = Modifier,
    size: Dp
) {
    Box(contentAlignment = Alignment.BottomEnd) {
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = modifier
                    .size(size)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            DefaultProfileImage(size, modifier)
        }

        Box(
            modifier = Modifier
                .offset(x = -10.dp, y = -11.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(3.dp)
                )
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White)
        ) {
            Icon(
                imageVector = Icons.Outlined.Photo,
                contentDescription = null,
                modifier = Modifier
                    .alpha(0.6f),
                tint = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileImagePreview() {
    MoareAndroidTheme {
        UpdateFormProfileImage(url = null, size = 100.dp)
    }
}