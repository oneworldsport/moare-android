package com.moare.android.features.userprofile.display.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.moare.android.ui.components.HCapsuleBar
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow
import com.moare.android.ui.util.clickableCapsuleRippleEffect
import com.moare.android.ui.util.screenWidthDp

@Composable
fun SportsSelectForm(
    sportsInterests: List<String>,
    onItemSelect: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var isSearchBarOpened by remember { mutableStateOf(false) }
    var filteredSportList by remember { mutableStateOf(listOf("축구", "야구", "농구", "테니스", "F1", "배구", "골프", "미식축구", "럭비", "MMA", "복싱", "하키", "수영", "육상", "배드민턴", "스키")) }
    val focusRequester = remember { FocusRequester() }

    val sportList = listOf("축구", "야구", "농구", "테니스", "F1", "배구", "골프", "미식축구", "럭비", "MMA", "복싱", "하키", "수영", "육상", "배드민턴", "스키")

    Column {
        CenterRow(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
        ) {
            AnimatedVisibility(
                visible = isSearchBarOpened,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = "닫기",
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable {
                            text = ""
                            isSearchBarOpened = false
                        }
                )
            }

            BasicTextField(
                value = text,
                onValueChange = { newValue ->
                    text = newValue
                    filteredSportList = if (text.isEmpty()) {
                        sportList
                    } else {
                        sportList.filter { it.contains(text) }
                    }
                },
                textStyle = TextStyle(fontSize = 15.sp),
                maxLines = 1,
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterEnd) {
                        this@CenterRow.AnimatedVisibility(
                            visible = isSearchBarOpened
                        ) {
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 24.dp),
                            ) {
                                if (text.isEmpty()) {
                                    Text(
                                        text = " 스포츠 검색",
                                        fontSize = 15.sp,
                                        color = Color.Gray
                                    )
                                }
                                innerTextField()
                            }
                        }

                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                isSearchBarOpened = true
                                focusRequester.requestFocus()
                            }
                        )
                    }
                },
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(size = 20.dp)
                    )
                    .border(BorderStroke(2.dp, Moare), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .height(35.dp)
            )

//            CenterRow(
//                modifier = Modifier
//                    .background(
//                        color = Color.White,
//                        shape = RoundedCornerShape(size = 20.dp)
//                    )
//                    .border(BorderStroke(2.dp, Moare), RoundedCornerShape(20.dp))
//                    .padding(horizontal = 10.dp, vertical = 4.dp)
//                    .height(35.dp)
//            ) {
//
//            }
        }

        LazyHorizontalGrid(
            rows = GridCells.Fixed(2),
            contentPadding = PaddingValues(end = 30.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .height(80.dp)
                .offset(y = if (isSearchBarOpened) 8.dp else -35.dp)
        ) {
            items(filteredSportList) { item ->
                val isSelected = sportsInterests.contains(item)

                CenterColumn(
                    modifier = Modifier
                        .border(
                            BorderStroke(2.dp, if (isSelected) Moare else Color.Transparent),
                            RoundedCornerShape(20.dp)
                        )
                        .clickableCapsuleRippleEffect {
                            onItemSelect(item)
                        }
                        .padding(horizontal = 10.dp)
                ) {
                   HCapsuleBar(
                       color = Color.Gray,
                       modifier = Modifier.alpha(if (isSelected) 0f else 0.8f)
                   )

                    Text(
                        text = item,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}




















