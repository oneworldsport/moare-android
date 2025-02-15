package com.moare.android

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.DayInfo
import com.moare.android.core.util.MatchDescriptionConverter
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.display.football.viewmodel.FBGameStatsViewModel
import com.moare.android.features.search.display.football.viewmodel.FBLeagueScheduleViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.ui.common.components.CalendarList
import com.moare.android.ui.common.components.CalendarType
import com.moare.android.ui.common.components.CapsuleButton
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.theme.MoareAndroidTheme
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.util.UUID

@Composable
fun TestView() {
    val data = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")
    var test by remember { mutableStateOf(false) }

    val dayList = listOf(
        DayInfo(day = 1, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 2, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 3, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 4, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 5, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 6, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 7, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 8, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 9, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 10, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 11, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 12, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 13, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 14, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 15, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 16, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 17, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 18, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 19, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 20, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 21, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 22, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 23, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 24, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 25, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 26, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
        DayInfo(day = 27, dayOfWeek = DayOfWeek.MONDAY, displayName = "", isDataEmpty = false),
    )
    var selectedYearMotnth by remember { mutableStateOf(0) }
    var selectedDay by remember { mutableStateOf(9) }
    var shouldScrollCalendar by remember { mutableStateOf(UUID.randomUUID().toString()) }


    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Button(
            onClick = {
//                test = !test
            }
        ) {
            Text("test")
        }

        Text(
            text = "맨체스터 유나이티드",
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier
                .width(100.dp)
                .height(50.dp)
                .background(Color.Gray)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(132.dp)
                .padding(start = 8.dp)
                .height(40.dp)
//            .clickable {
//                searchViewModel.send(
//                    SearchViewModel.Intent.UpdateTextField(
//                        newValue = TextFieldValue(
//                            text = "손흥민"
//                        )
//                    )
//                )
//                searchViewModel.send(SearchViewModel.Intent.PerformSearch())
//            }
        ) {
            URLImage(
                url = "",
                customSize = 25.dp,
                modifier = Modifier.padding(end = 4.dp).background(Color.Cyan)
            )

            Text(
                text = "조슈아 지르크지",
                fontSize = 12.sp,
                maxLines = 2,
                modifier = Modifier.width(60.dp)
            )

            // TODO: goals, cards
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(30.dp)
                    .padding(start = 2.dp)
                    .background(Color.Yellow)
            ) {
                Text(
                    text = if (true) "선발" else "후보",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .alpha(if (true) 1f else 0.7f)
                )

                Text(
                    text = "G",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .alpha(0.7f)
                )
            }

            Spacer(Modifier.weight(1f))

            VCapsuleBar(modifier = Modifier.alpha(0.5f))
        }

        Text(
            text = "99sdffdsfdsfds",
            modifier = Modifier
                .width(100.dp)
                .height(50.dp)
                .padding(top = 20.dp)
//                .border(BorderStroke(1.dp, Color.Blue), RoundedCornerShape(20.dp))
                .drawBehind {
                    drawRoundRect(
                        color = Color.Red,
//                        size = Size(width = 150f, height = 100f),
                        cornerRadius = CornerRadius(16.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawRect(
                        color = Color.Blue,
//                        size = Size(width = 100f, height = 100f),
                        topLeft = Offset(x = 25f, y = 0f),
                    )
                },
        )

        var text by remember { mutableStateOf("") }

        Box(
            modifier = Modifier
                .height(56.dp) // Fix the height to avoid dynamic resizing
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp), // Add horizontal padding
            contentAlignment = Alignment.CenterStart // Vertically center text
        ) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(fontSize = 16.sp, lineHeight = 50.sp),
                modifier = Modifier.fillMaxWidth().background(Color.Gray).align(Alignment.Center)
            )
//            TextField(
//                value = text,
//                onValueChange = { text = it },
//                textStyle = TextStyle(fontSize = 16.sp, lineHeight = 50.sp),
//                modifier = Modifier.fillMaxWidth().align(Alignment.Center),
//                placeholder = {
//                    Text("abced")
//                }
//            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 10.dp)
        ) {
            Box(
                Modifier
                    .border(BorderStroke(2.dp, Color.Blue), RoundedCornerShape(20.dp))
//                    .drawBehind {
//                        drawRoundRect(
//                            color = Color.Red,
//                            cornerRadius = CornerRadius(16.dp.toPx()),
//                            style = Stroke(width = 4.dp.toPx())
//                        )
//                    }
                    .padding(horizontal = 10.dp)

            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "test",
                        modifier = Modifier.padding(vertical = 5.dp))
                    HCapsuleBar(color = Color.Red)
                }
            }

            Box(
                Modifier
                    .border(BorderStroke(2.dp, Color.Blue), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp)

            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "test",
                        modifier = Modifier.padding(vertical = 5.dp))
                    HCapsuleBar()
                }
            }
        }

        Row(
            Modifier.fillMaxWidth()
        ) {
            Text("(")
            Box(Modifier.background(Color.Blue).fillMaxWidth().weight(1f))
            Text(
                text = "test"
            )
            Text(")")
        }

        if (test) {
            SequentialListAnimation(
                items = data,
                itemHeight = 60.dp,
                aniDelay = 100, // 각 항목 간 딜레이
                aniDuration = 500 // 애니메이션 지속 시간
            )
        }
    }


    // border 종류
//        .border(BorderStroke(2.dp, MaterialTheme.colors.primary), RoundedCornerShape(20.dp))

    // FlowRow 속성
//        mainAxisSpacing = 10.dp,
//        crossAxisSpacing = 10.dp,

    // 가운데로 animation 주기위해 Box로 한번 더 감싸줌
//    Box(
//        contentAlignment = Alignment.Center,
//        modifier = Modifier
////            .width(200.dp)
////            .fillMaxWidth()
//            .height(40.dp)
//    ) {
//        Box(
//            contentAlignment = Alignment.CenterStart,
//            modifier = Modifier
////                .width(animationWidth)
//                .height(40.dp)
//                .clickable(
//                    onClick = onClick
//                )
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(40.dp)
//                    .border(
//                        BorderStroke(2.dp, MaterialTheme.colors.primary),
//                        RoundedCornerShape(20.dp)
//                    )
//            )
//
//            Box(
//                modifier = Modifier
//                    .padding(horizontal = 10.dp)
//                    .fillMaxWidth()
//                    .height(40.dp)
//                    .background(Color.White)
//            )
//
//            Text(
//                text = text,
//                modifier = Modifier
//                    .padding(horizontal = 12.dp)
//                    .onGloballyPositioned { coordinates ->
//                        textSize = coordinates.size
//                    }
//            )
//        }
//    }
}

@Composable
fun SequentialListAnimation(
    items: List<String>,
    itemHeight: Dp = 50.dp,
    aniDelay: Int = 100, // 밀리초 단위
    aniDuration: Int = 500 // 밀리초 단위
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(items) { index, item ->
            var isVisible by remember { mutableStateOf(false) }

            // 트리거된 애니메이션
            LaunchedEffect(key1 = index) {
                delay((index * aniDelay).toLong())
                isVisible = true
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = aniDuration)) +
                        slideInVertically(animationSpec = tween(durationMillis = aniDuration)) { fullHeight -> -fullHeight }
            ) {
                ListItem(item = item, height = itemHeight)
            }
        }
    }
}

@Composable
fun ListItem(item: String, height: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
//            .background(Color.LightGray)
            .padding(8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = item, color = Color.Black)
    }
}

@Preview(showBackground = true)
@Composable
fun TestViewPreview() {
    MoareAndroidTheme {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            TestView()
        }
    }
}