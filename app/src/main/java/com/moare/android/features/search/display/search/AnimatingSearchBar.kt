package com.moare.android.features.search.display.search

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.R
import com.moare.android.features.search.display.search.store.SearchAction
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.ui.animation.components.RoundedRectPath
import com.moare.android.ui.animation.components.RoundedRectWithPathAni
import com.moare.android.ui.util.CenterRow
import com.moare.android.ui.util.conditionalWeight
import com.moare.android.ui.util.convertDpToPx
import com.moare.android.ui.util.convertPxToDp
import com.moare.android.ui.util.screenWidthDp
import kotlinx.coroutines.delay

@Composable
fun AnimatingSearchBar(
    searchStore: SearchStore,
    modifier: Modifier
) {
    /* ---------------------
       constants
       --------------------- */
    val barWidth = screenWidthDp() - 32.dp
    val barHeight = 50.dp
    val strokeWidth = convertDpToPx(3.dp)
    val cornerRadius = CornerRadius(50f)
    val paddingForBackButton = 34.dp

    val disabledColor = Color.Gray
    val enabledColor = MaterialTheme.colors.primary

    /* ---------------------
       ui state
       --------------------- */
    var aniBarVisibleState by remember { mutableStateOf(true) } // always false after first open(animation)
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var textSize by remember { mutableStateOf(IntSize.Zero) }
    val textWidthDp = convertPxToDp(pxValue = textSize.width.toFloat())

    /* ---------------------
       viewmodel state
       --------------------- */
    val barFirstOpened by searchStore.barFirstOpened.collectAsState()
    val focusState by searchStore.focusState.collectAsState()
    val query by searchStore.query.collectAsState()
    val searchState by searchStore.searchState.collectAsState()
    val trendingKeywordList by searchStore.trendingKeywordList.collectAsState()
    val autoCompleteList by searchStore.autoCompleteList.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val textFieldWidthState by animateDpAsState(
        targetValue = if (searchState) {
            textWidthDp
        } else {
            if (barFirstOpened) {
                screenWidthDp() - (16 * 4 + 24 + 10).dp
            } else {
                0.dp
            }
        },
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearOutSlowInEasing
        )
    )

    /* ---------------------
       LaunchedEffect
       --------------------- */
    // after AniBar Animation make it invisible and change FocusState
    LaunchedEffect(barFirstOpened) {
        if (barFirstOpened) {
            delay(1000)

            searchStore.send(SearchAction.ToggleFocusState(true))
            aniBarVisibleState = false
        }
    }

    LaunchedEffect(focusState) {
        if (focusState) {
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(Unit) {
        // NOTE: 로고가 사라지면서 검색 아이콘이 나타나는 시간 1초 + 추가 0.2 wait
        delay(1200)
        searchStore.send(SearchAction.BarFirstOpen)
    }

    /* ---------------------
       ui
       --------------------- */
    Box(
        contentAlignment = if (aniBarVisibleState) Alignment.Center else Alignment.CenterStart,
        modifier = modifier
            .fillMaxWidth()
    ) {
        // SearchBar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = if (searchState) 8.dp + paddingForBackButton else 16.dp)
                .height(barHeight)
                .drawBehind {
                    val path = RoundedRectPath(
                        size.width, size.height, cornerRadius, strokeWidth
                    )

                    drawPath(
                        path = path,
                        color = if (searchState) disabledColor else {
                            if (aniBarVisibleState) Color.Transparent else enabledColor
                        },
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                .padding(horizontal = 16.dp)
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = conditionalWeight(Modifier, !aniBarVisibleState && !searchState) // anibar가 사라지고, 검색이 안됐을때만 weight(1f). 안그려면 TextField의 text가 길어질때 검색 icon이 사라짐.
            ) {
                Text(
                    text = query.text,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Default
                    ),
                    modifier = Modifier
                        .alpha(0f)
                        .onGloballyPositioned { coordinates ->
                            textSize = coordinates.size
                        }
                )

                BasicTextField(
                    value = query,
                    onValueChange = { newValue ->
                        if (newValue.text != query.text) { // prevents unnecessary triggers due to an unexpected behavior
                            searchStore.send(SearchAction.UpdateTextField(newValue))
                        }
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Default
                    ),
                    enabled = if (searchState) false else true,
                    readOnly = aniBarVisibleState, // to prevent focusing while first open animation
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (searchState) {
                                searchStore.send(SearchAction.ToggleSearchBar)
                            } else {
                                searchStore.send(SearchAction.ToggleAutoCompleteListVisibleState)
                                searchStore.send(
                                    SearchAction.PerformSearch(
                                        aniDuration = 1000
                                    )
                                )
                            }
                        }
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier
                                .fillMaxWidth(),
                        ) {
                            innerTextField()
                            if (!aniBarVisibleState) {
                                CenterRow {
                                    if (query.text.isEmpty()) {
                                        Text(
                                            text = " ${trendingKeywordList.firstOrNull() ?: ""}",
                                            color = Color.LightGray,
                                            style = TextStyle(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Normal,
                                                fontFamily = FontFamily.Default
                                            ),
                                            modifier = Modifier
                                        )
                                    } else if (!searchState) { // query가 비어있고, 검색이 안됐을때만 노출
                                        Spacer(Modifier.weight(1f))
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_round_add_circle_24),
                                            contentDescription = null,
                                            tint = Color.LightGray,
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .alpha(0.8f)
                                                .background(Color.White)
                                                .rotate(45f)
                                                .clickable {
                                                    searchStore.send(SearchAction.UpdateTextField(TextFieldValue("")))
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .width(textFieldWidthState)
                        .focusRequester(focusRequester)
                        .onFocusChanged { state ->
                            if (focusState != state.isFocused) {
                                searchStore.send(SearchAction.ToggleFocusState(state.isFocused))
                            }
                        }
                )

//                if (!aniBarVisibleState) {
//                    if (query.text.isEmpty()) {
//                        Text(
//                            text = " ${trendingKeywordList.firstOrNull() ?: ""}",
//                            color = Color.LightGray,
//                            style = TextStyle(
//                                fontSize = 16.sp,
//                                fontWeight = FontWeight.Normal,
//                                fontFamily = FontFamily.Default
//                            ),
//                            modifier = Modifier
//                        )
//                    }
//                }
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_rounded_search_24),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = if (barFirstOpened) 10.dp else 0.dp)
                    .size(24.dp)
                    .clickable {
                        if (!barFirstOpened) {
                            searchStore.send(SearchAction.BarFirstOpen)
                        } else {
                            if (searchState) {
                                searchStore.send(SearchAction.ToggleSearchBar)
                            } else {
                                searchStore.send(SearchAction.ToggleAutoCompleteListVisibleState)
                                searchStore.send(
                                    SearchAction.PerformSearch(
                                        aniDuration = 1000
                                    )
                                )
                            }
                        }
                    }
            )
        }

        // SearchBar with animation on first open
        if (aniBarVisibleState) {
            RoundedRectWithPathAni(
                width = barWidth,
                height = barHeight,
                cornerRadius = cornerRadius,
                strokeWidth = strokeWidth,
                drawPath = barFirstOpened
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    MoareAndroidTheme {
//    }
//}