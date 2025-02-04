package com.moare.android.features.search.display.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.R
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.ui.animation.components.RoundedRectPath
import com.moare.android.ui.animation.components.RoundedRectWithPathAni
import com.moare.android.ui.util.convertDpToPx
import com.moare.android.ui.util.convertPxToDp
import com.moare.android.ui.util.screenWidthDp
import kotlinx.coroutines.delay

@Composable
fun AnimatingSearchBar(
    viewModel: SearchViewModel = hiltViewModel(),
    modifier: Modifier
) {
    /* ---------------------
       constants
       --------------------- */
    val barWidth = screenWidthDp() - 32.dp
    val barHeight = 50.dp
    val strokeWidth = convertDpToPx(3.dp)
    val cornerRadius = CornerRadius(50f)

    val disabledColor = Color.Gray
    val enabledColor = MaterialTheme.colors.primary

    /* ---------------------
       ui state
       --------------------- */
    val firstOpened = remember { mutableStateOf(false) }
    var firstOpenedValue by firstOpened
    var aniBarVisibleState by remember { mutableStateOf(true) }

    var textSize by remember { mutableStateOf(IntSize.Zero) }
    val textWidthDp = convertPxToDp(pxValue = textSize.width.toFloat())

    /* ---------------------
       viewmodel state
       --------------------- */
    val focusRequester by viewModel.focusRequester.collectAsState()
    val focusState by viewModel.focusState.collectAsState()
    val query by viewModel.query.collectAsState()
    val searchState by viewModel.searchState.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val textFieldWidthState by animateDpAsState(
        targetValue = if (searchState) {
            textWidthDp
        } else {
            if (firstOpenedValue) {
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
    LaunchedEffect(firstOpenedValue) {
        if (firstOpenedValue) {
            viewModel.send(SearchViewModel.Intent.UpdateTextFieldVisibleState(true))

            delay(1000)

            viewModel.send(SearchViewModel.Intent.ToggleFocusState)
            aniBarVisibleState = false
        }
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
                .padding(horizontal = 16.dp)
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
            Box {
                Text(
                    text = query.text,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Default
                    ),
                    modifier = Modifier
                        .alpha(0f)
                        .background(Color.Red)
                        .onGloballyPositioned { coordinates ->
                            textSize = coordinates.size
                        }
                )

                BasicTextField(
                    value = query,
                    onValueChange = { newValue ->
                        viewModel.send(SearchViewModel.Intent.UpdateTextField(newValue))
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Default
                    ),
                    enabled = if (searchState) false else true,
                    modifier = Modifier
                        .width(textFieldWidthState)
                        .focusRequester(focusRequester)
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_search_24),
                contentDescription = "ic_search_24",
                modifier = Modifier
                    .padding(start = if (firstOpenedValue) 10.dp else 0.dp)
                    .size(24.dp)
                    .clickable {
                        if (!firstOpenedValue) {
                            firstOpenedValue = true
                        } else {
                            if (searchState) {
                                viewModel.send(SearchViewModel.Intent.ToggleSearchBar)
                            } else {
                                viewModel.send(SearchViewModel.Intent.PerformSearch())
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
                drawPath = firstOpened
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