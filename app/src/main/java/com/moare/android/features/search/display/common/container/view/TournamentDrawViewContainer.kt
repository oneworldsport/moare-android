package com.moare.android.features.search.display.common.container.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.features.search.display.common.container.component.TournamentSingleGameItem
import com.moare.android.features.search.display.common.container.state.TournamentContainerAction
import com.moare.android.features.search.display.common.container.state.TournamentDrawContainerState
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.util.CenterColumn
import java.time.Instant

@Composable
fun <T> TournamentDrawViewContainer(
    state: TournamentDrawContainerState<T>,
    action: TournamentContainerAction<T>
) {
    // zoom
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var contentSize by remember { mutableStateOf(IntSize.Zero) }

    val minScale = 0.3f
    val maxScale = 1.3f

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(minScale, maxScale)

        val newOffset = clampOffset(
            offset = offset + panChange,
            scale = newScale,
            container = containerSize,
            content = contentSize
        )

        scale = newScale
        offset = newOffset
    }

    // 처음 오픈 시 좌측 상단으로 offset설정
    var initialized by remember { mutableStateOf(false) }
    LaunchedEffect(containerSize, contentSize) {
        if (!initialized &&
            containerSize != IntSize.Zero &&
            contentSize != IntSize.Zero
        ) {
            offset = clampOffset(
                offset = Offset(
                    x = (contentSize.width - containerSize.width) / 2f,
                    y = (contentSize.height - containerSize.height) / 2f
                ),
                scale = scale,
                container = containerSize,
                content = contentSize
            )
            initialized = true
        }
    }

    Box(
        modifier = Modifier
            .onSizeChanged { containerSize = it }
            .clipToBounds()
            .transformable(state = transformableState)
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize(unbounded = true)
                .onSizeChanged { contentSize = it }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                }
                .padding(horizontal = 10.dp)
        ) {
            state.gameListTuple.forEachIndexed { roundIndex, item ->
                val title = item.title
                // 1. 중첩 배열인 gameList를(nil을 제거하고) 펼쳐서 1차원 배열로 만든다.
                // 2. tournament_teams.json에 들어간 id 순서대로 경기가 배치되어 있기 때문에 날짜순으로 정렬을 해준다.
                val gameList = item.gameList.filterNotNull().flatten().sortedBy { it.parsedDate ?: Instant.MAX }

                CenterColumn {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(250.dp)
                    )
                    HCapsuleBar(
                        modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
                    )

                    gameList.forEachIndexed { _, game ->
                        if (state.isSeries) {
                            // TODO: 추첨인데 시리즈인 경우가 생기면 작업
                        } else {
                            TournamentSingleGameItem(
                                leagueId = state.leagueId,
                                game = game,
                                teamNameDic = state.teamNameDic,
                                selectGame = action.selectGame,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }
                }

                if (roundIndex != state.gameListTuple.size - 1) {
                    // TODO: Scroll 화면에서는 .fillMaxHeight() 안먹힘. 다른 방법 생각해 봐야함.
//                VCapsuleBar(
//                    modifier = Modifier
//                        .padding(top = 40.dp, bottom = 12.dp)
//                        .alpha(0.5f)
//                )
                }
            }
        }
    }
}