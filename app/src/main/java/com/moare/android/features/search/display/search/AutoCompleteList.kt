package com.moare.android.features.search.display.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.ui.util.convertPxToDp
import com.moare.android.ui.util.screenWidthDp
import kotlinx.coroutines.delay

@Composable
fun AutoCompleteList(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = hiltViewModel(),
    onItemSelected: (String) -> Unit
) {
    var selectedQuery by remember { mutableStateOf("") }

    /* ---------------------
       viewmodel state
       --------------------- */
    val autoCompleteList by searchViewModel.autoCompleteList.collectAsState()

    LaunchedEffect(autoCompleteList) {
        selectedQuery = ""
    }

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        items(autoCompleteList) { query ->
            AutoCompleteListItem(
                text = query,
                selectedQuery = selectedQuery,
                onItemSelected = {
                    selectedQuery = query
                    onItemSelected(query)
                }
            )
        }
    }
}

@Composable
fun AutoCompleteListItem(
    text: String,
    selectedQuery: String,
    onItemSelected: () -> Unit
) {
    var textSize by remember { mutableStateOf(IntSize.Zero) }
    val textWidthDp = convertPxToDp(pxValue = textSize.width.toFloat())
    // 28.dp = border(4.dp) + textSize.width + Text's horizontalPadding
    val shrunkWidth = 20.dp + textWidthDp

    var isClicked by remember { mutableStateOf(false) }
    var itemVisibleState by remember { mutableStateOf(true) }

    val animationWidth by animateDpAsState(
        targetValue = if (isClicked) shrunkWidth else screenWidthDp(),
        animationSpec = tween(1000),
        label = ""
    )

    LaunchedEffect(selectedQuery) {
        if (selectedQuery.isNotBlank() && selectedQuery != text) {
            itemVisibleState = false
        }
    }

    LaunchedEffect(isClicked) {
        if (isClicked) {
            onItemSelected()
            delay(1000)
            itemVisibleState = false
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedVisibility(
            visible = itemVisibleState,
            exit = fadeOut(animationSpec = tween(durationMillis = 500)) + shrinkVertically(animationSpec = tween(durationMillis = 1000))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .width(animationWidth)
                    .clickable {
                        isClicked = true
                    }
            ) {
                Text(
                    text = "(",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.width(10.dp)
                )

                Text(
                    text = text,
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            textSize = coordinates.size
                        }
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text = ")",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.width(10.dp)
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SearchListItemPreview() {
//    MoareAndroidTheme {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            AutoCompleteList (
//                selectedItem = null,
//                onItemSelected = {
//
//            })
//        }
//    }
//}

