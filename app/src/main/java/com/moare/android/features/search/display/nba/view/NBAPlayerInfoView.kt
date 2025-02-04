package com.moare.android.features.search.display.nba.view

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerInfoViewModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerInfoDisplayModel
import com.moare.android.features.search.models.models.nba.NBAPlayer
import kotlin.math.roundToInt

@Composable
fun NBAPlayerInfoView(
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    data: NBAPlayerInfoDisplayModel,
    center: State<Offset>
) {
    /* ---------------------
       ui state
       --------------------- */

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    val player = displayModel?.player

    /* ---------------------
       animation
       --------------------- */
    var parentOffset by remember { mutableStateOf(Offset.Zero) }
    val itemPositions = remember { mutableStateMapOf<Int, Offset>() }
    var aniPositions by remember { mutableStateOf(false) }

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(Unit) {
        // TODO: itemPositions가 다 추가되고 실행되는게 맞는지 확인
        aniPositions = true
    }
    LaunchedEffect(data) {
        nbaPlayerInfoViewModel.initData(data)
    }

    /* ---------------------
       invisible ui
       - for position
       --------------------- */
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .onGloballyPositioned { parentCoordinates ->
                val parentPosition = parentCoordinates.positionInWindow()
                parentOffset = parentPosition
            }
            .alpha(0f)
    ) {
        Row {
            // photo, name
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        // TODO: 함수로
                        val itemSize = layoutCoordinates.size
                        val position = layoutCoordinates.positionInWindow()

                        val relativeX = position.x - parentOffset.x
                        val relativeY = position.y - parentOffset.y

                        // Calculate the center of the InfoItem
                        val centerX = relativeX + itemSize.width / 2f
                        val centerY = relativeY + itemSize.height / 2f

                        itemPositions[0] = Offset(centerX - center.value.x, centerY - center.value.y)
                    }
            ) {
                NBAPlayerInfoFirstItem(player)
            }

            // age, birth, country
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        val itemSize = layoutCoordinates.size
                        val position = layoutCoordinates.positionInWindow()

                        val relativeX = position.x - parentOffset.x
                        val relativeY = position.y - parentOffset.y

                        // Calculate the center of the InfoItem
                        val centerX = relativeX + itemSize.width / 2f
                        val centerY = relativeY + itemSize.height / 2f

                        itemPositions[1] = Offset(centerX - center.value.x, centerY - center.value.y)
                    }
            ) {

            }

            // weight, height
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        val itemSize = layoutCoordinates.size
                        val position = layoutCoordinates.positionInWindow()

                        val relativeX = position.x - parentOffset.x
                        val relativeY = position.y - parentOffset.y

                        // Calculate the center of the InfoItem
                        val centerX = relativeX + itemSize.width / 2f
                        val centerY = relativeY + itemSize.height / 2f

                        itemPositions[2] = Offset(centerX - center.value.x, centerY - center.value.y)
                    }
            ) {

            }
        }

        // league stats
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 15.dp)
                .onGloballyPositioned { layoutCoordinates ->
                    val itemSize = layoutCoordinates.size
                    val position = layoutCoordinates.positionInWindow()

                    val relativeX = position.x - parentOffset.x
                    val relativeY = position.y - parentOffset.y

                    // Calculate the center of the InfoItem
                    val centerX = relativeX + itemSize.width / 2f
                    val centerY = relativeY + itemSize.height / 2f

                    itemPositions[3] = Offset(centerX - center.value.x, centerY - center.value.y)
                }
        ) {

        }

        // last game stats
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
                .onGloballyPositioned { layoutCoordinates ->
                    val itemSize = layoutCoordinates.size
                    val position = layoutCoordinates.positionInWindow()

                    val relativeX = position.x - parentOffset.x
                    val relativeY = position.y - parentOffset.y

                    // Calculate the center of the InfoItem
                    val centerX = relativeX + itemSize.width / 2f
                    val centerY = relativeY + itemSize.height / 2f

                    itemPositions[4] = Offset(centerX - center.value.x, centerY - center.value.y)
                }
        ) {

        }

        // next game
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
                .onGloballyPositioned { layoutCoordinates ->
                    val itemSize = layoutCoordinates.size
                    val position = layoutCoordinates.positionInWindow()

                    val relativeX = position.x - parentOffset.x
                    val relativeY = position.y - parentOffset.y

                    // Calculate the center of the InfoItem
                    val centerX = relativeX + itemSize.width / 2f
                    val centerY = relativeY + itemSize.height / 2f

                    itemPositions[5] = Offset(centerX - center.value.x, centerY - center.value.y)
                }
        ) {

        }
    }

    /* ---------------------
       visible ui
       - with animation effect
       --------------------- */
    val firstPosition = itemPositions[0] ?: Offset.Zero
    val firstAnimatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) firstPosition else Offset.Zero,
        animationSpec = tween(1500),
    )
    val secondPosition = itemPositions[1] ?: Offset.Zero
    val secondAnimatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) secondPosition else Offset.Zero,
        animationSpec = tween(1500),
    )
    val thirdPosition = itemPositions[2] ?: Offset.Zero
    val thirdAnimatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) thirdPosition else Offset.Zero,
        animationSpec = tween(1500),
    )
    val fourthPosition = itemPositions[3] ?: Offset.Zero
    val fourthAnimatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) fourthPosition else Offset.Zero,
        animationSpec = tween(1500),
    )
    val fifthPosition = itemPositions[4] ?: Offset.Zero
    val fifthAnimatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) fifthPosition else Offset.Zero,
        animationSpec = tween(1500),
    )
    val sixthPosition = itemPositions[5] ?: Offset.Zero
    val sixthAnimatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) sixthPosition else Offset.Zero,
        animationSpec = tween(1500),
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset {
                IntOffset(
                    firstAnimatedPosition.x.roundToInt(),
                    firstAnimatedPosition.y.roundToInt()
                )
            }
    ) {

    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset {
                IntOffset(
                    secondAnimatedPosition.x.roundToInt(),
                    secondAnimatedPosition.y.roundToInt()
                )
            }
    ) {

    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset {
                IntOffset(
                    thirdAnimatedPosition.x.roundToInt(),
                    thirdAnimatedPosition.y.roundToInt()
                )
            }
    ) {

    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset {
                IntOffset(
                    fourthAnimatedPosition.x.roundToInt(),
                    fourthAnimatedPosition.y.roundToInt()
                )
            }
    ) {

    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset {
                IntOffset(
                    fifthAnimatedPosition.x.roundToInt(),
                    fifthAnimatedPosition.y.roundToInt()
                )
            }
    ) {

    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset {
                IntOffset(
                    sixthAnimatedPosition.x.roundToInt(),
                    sixthAnimatedPosition.y.roundToInt()
                )
            }
    ) {

    }
}

@Composable
fun NBAPlayerInfoFirstItem(
    player: NBAPlayer?
) {

}

@Composable
fun NBAPlayerInfoSecondItem(
    player: NBAPlayer?
) {

}

@Composable
fun NBAPlayerInfoThirdItem(
    player: NBAPlayer?
) {

}

@Composable
fun NBAPlayerInfoFourthItem(

) {

}

@Composable
fun NBAPlayerInfoFifthItem() {

}

@Composable
fun NBAPlayerInfoSixthItem() {

}