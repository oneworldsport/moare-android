package com.moare.android.features.moat.display.components

import android.util.Log
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.util.CalendarUtil
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.optionalClickable

enum class MoatType {
    TRENDING, DETAIL, COMMENT, USER_PROFILE
}

@Composable
fun MoatItem(
    moatType: MoatType,
    isButtonDisabled: Boolean = false,
    title: String? = null,
    content: String,
    hashtagList: List<String>? = null,
    fireCount: Int,
    commentCount: Int,
    userHandle: String,
    createdAt: String,
    timeAgo: String = CalendarUtil.timeAgoString(createdAt),
    settingTapped: () -> Unit,
    fired: Boolean,
    fireTapped: (Boolean) -> Unit,
    action: () -> Unit,
    modifier: Modifier = Modifier
) {
    val height: Dp
    val titleFontSize: TextUnit
    val contentFontSize: TextUnit
    val profileImageSize: Dp
    val userHandleFontSize: TextUnit
    val timeFontSize: TextUnit
    val iconSize: Dp
    val iconFontSize: TextUnit
    val iconCountFontSize: TextUnit

    var isSideBarShowing by remember { mutableStateOf(true) }

    when (moatType) {
        MoatType.TRENDING -> {
            height = 110.dp // 댓글 숫자가 잘려서 조금 키움
            titleFontSize = 18.sp
            contentFontSize = 18.sp
            profileImageSize = 25.dp
            userHandleFontSize = 16.sp
            timeFontSize = 15.sp
            iconSize = 17.dp
            iconFontSize = 17.sp
            iconCountFontSize = 12.sp
        }
        MoatType.DETAIL -> {
            height = 160.dp
            titleFontSize = 18.sp
            contentFontSize = 16.sp
            profileImageSize = 25.dp
            userHandleFontSize = 16.sp
            timeFontSize = 15.sp
            iconSize = 17.dp
            iconFontSize = 17.sp
            iconCountFontSize = 12.sp
        }
        MoatType.COMMENT -> {
            height = 100.dp // 댓글 숫자가 잘려서 조금 키움
            titleFontSize = 18.sp
            contentFontSize = 16.sp
            profileImageSize = 20.dp
            userHandleFontSize = 15.sp
            timeFontSize = 14.sp
            iconSize = 16.dp
            iconFontSize = 16.sp
            iconCountFontSize = 11.sp
        }
        MoatType.USER_PROFILE -> {
            height = 80.dp
            titleFontSize = 17.sp
            contentFontSize = 18.sp
            profileImageSize = 25.dp
            userHandleFontSize = 16.sp
            timeFontSize = 14.sp
            iconSize = 16.dp
            iconFontSize = 16.sp
            iconCountFontSize = 11.sp
        }
    }

    Box(
        modifier.height(height)
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .optionalClickable(moatType != MoatType.DETAIL, onClick = action)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Row(
                    Modifier.fillMaxWidth().weight(1f)
                ) {
                    Column(
                        Modifier.fillMaxHeight().weight(1f)
                    ) {
                        if (title != null) {
                            if (moatType != MoatType.DETAIL) {
                                Spacer(Modifier.weight(1f))
                            }
                            Row {
                                if (moatType == MoatType.COMMENT) null
                                else Text(
                                    text = title,
                                    fontSize = titleFontSize,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Start,
                                    maxLines = 2)
                            }
                        }

                        if (moatType == MoatType.DETAIL || moatType == MoatType.COMMENT) {
                            Text(
                                text = content,
                                Modifier.then(
                                    if (moatType == MoatType.DETAIL) Modifier
                                    else Modifier.fillMaxHeight().wrapContentHeight()
                                ),
                                fontSize = contentFontSize,
                                textAlign = TextAlign.Start
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        if (moatType != MoatType.COMMENT) {
                            if (moatType == MoatType.DETAIL) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                            Row(
                                Modifier.padding(bottom = 4.dp)
                            ) {
                                Row(
                                    Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    hashtagList?.forEach { item ->
                                        Text(
                                            text = item,
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                            textAlign = TextAlign.Center,
                                            overflow = TextOverflow.Ellipsis,
                                            color = Moare,
                                        )
                                    }
                                }

                                if (moatType == MoatType.USER_PROFILE) {
                                    Text(
                                        text = timeAgo,
                                        Modifier.padding(end = 6.dp),
                                        fontSize = timeFontSize,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        Modifier.fillMaxHeight(),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = if (moatType == MoatType.USER_PROFILE) Arrangement.Center else Arrangement.Bottom
                    ) {
                        Column(
                            horizontalAlignment =  Alignment.CenterHorizontally
                        ) {
                            if (moatType == MoatType.DETAIL) {
                                Text(
                                    text = "⋮" ,
                                    fontSize = iconFontSize,
                                    modifier = Modifier
                                        .clickable(
                                            enabled = true,
                                            onClick = {
                                                settingTapped()
                                                Log.d("click", "Tapped")
                                            }
                                        )
                                )
                                Spacer(Modifier.weight(1f))
                            }

                            Column(
                                Modifier.padding( bottom = if (moatType == MoatType.DETAIL) 8.dp else if (moatType == MoatType.COMMENT || moatType == MoatType.TRENDING) 0.dp else 4.dp ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                IconButton(
                                    onClick = { fireTapped(fired) },
                                    modifier = Modifier.size(48.dp)       // 터치 타겟 48dp 권장
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        Icon(
                                            if (fired) {
                                                Icons.Filled.LocalFireDepartment
                                            } else {
                                                Icons.Outlined.LocalFireDepartment
                                            },
                                            null,
                                            Modifier.size(iconSize)
                                        )

                                        Text(
                                            text = fireCount.toString(),
                                            fontSize = iconCountFontSize,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon (
                                    Icons.AutoMirrored.Outlined.Chat,
                                    null,
                                    Modifier.size(iconSize))

                                Text(
                                    text = commentCount.toString(),
                                    fontSize = iconCountFontSize,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                if (moatType != MoatType.USER_PROFILE) {
                    Row(
                        Modifier.fillMaxWidth().padding(if (moatType == MoatType.COMMENT) 0.dp else 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(profileImageSize)
                                .background(
                                    color = Moare,
                                    shape = CircleShape)
                        )

                        Text(
                            text = userHandle,
                            Modifier.padding(start = 6.dp),
                            fontSize = userHandleFontSize
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = timeAgo,
                            fontSize = timeFontSize,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

//@Preview
//@Composable
//fun preview() {
//    MoatItem(
//        moatType = MoatType.DETAIL,
//        title = "만약에 한글이야 이게 엄청 길어 그러면?",
//        content = "이게 내용이야",
//        hashtagList = listOf("#축구","#dfdfdf"),
//        fireCount = 0,
//        commentCount = 0,
//        userHandle = "test",
//        createdAt = "2025-08-16T20:10:00.666666",
//        action = {},
//        modifier = Modifier.fillMaxWidth()
//    )
//}