package com.moare.android.features.search.display.nba.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.EnNameTranslationUtils
import com.moare.android.core.util.TranslationType
import com.moare.android.core.util.toCm
import com.moare.android.core.util.toKg
import com.moare.android.features.search.display.components.FBStatDataItem
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerInfoViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerInfoDisplayModel
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.screenWidthDp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun NBAPlayerInfoView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    data: NBAPlayerInfoDisplayModel,
    center: State<Offset>
) {
    /* ---------------------
       ui state
       --------------------- */
    val density = LocalDensity.current
    val itemSizes = remember { mutableStateMapOf<Int, DpSize>() }

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    var parentOffset by remember { mutableStateOf(Offset.Zero) }
    val itemPositions = remember { mutableStateMapOf<Int, Offset>() }
    var aniPositions by remember { mutableStateOf(false) }
    var showContents by remember { mutableStateOf(false) }
    val contentsAlpha by animateFloatAsState(
        targetValue = if (showContents) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.NBAPlayerInfo) {
            nbaPlayerInfoViewModel.send(NBAPlayerInfoViewModel.Intent.InitData(data))
        }
    }

    LaunchedEffect(itemPositions) {
        if (itemPositions.size == 9) {
            aniPositions = true
            delay(1000)
            showContents = true
        }
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
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            // photo, name
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
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

                        itemSizes[0] = with(density) {
                            DpSize(itemSize.width.toDp(), itemSize.height.toDp())
                        }
                        itemPositions[0] =
                            Offset(centerX - center.value.x, centerY - center.value.y)
                    }
                    .widthIn(max = 130.dp)
            ) {
                NBAPlayerInfoFirstItem()
            }

            // logo, team, name
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
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

                        itemSizes[1] = with(density) {
                            DpSize(itemSize.width.toDp(), itemSize.height.toDp())
                        }
                        itemPositions[1] =
                            Offset(centerX - center.value.x, centerY - center.value.y)
                    }
                    .widthIn(max = 130.dp)
            ) {
                NBAPlayerInfoSecondItem()
            }

            // jersey, position
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        val itemSize = layoutCoordinates.size
                        val position = layoutCoordinates.positionInWindow()

                        val relativeX = position.x - parentOffset.x
                        val relativeY = position.y - parentOffset.y

                        // Calculate the center of the InfoItem
                        val centerX = relativeX + itemSize.width / 2f
                        val centerY = relativeY + itemSize.height / 2f

                        itemSizes[2] = with(density) {
                            DpSize(itemSize.width.toDp(), itemSize.height.toDp())
                        }
                        itemPositions[2] =
                            Offset(centerX - center.value.x, centerY - center.value.y)
                    }
                    .widthIn(max = 130.dp)
            ) {
                NBAPlayerInfoThirdItem()
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            // from school/team, draft info, career info
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
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

                        itemSizes[3] = with(density) {
                            DpSize(itemSize.width.toDp(), itemSize.height.toDp())
                        }
                        itemPositions[3] =
                            Offset(centerX - center.value.x, centerY - center.value.y)
                    }
                    .widthIn(max = 130.dp)
            ) {
                NBAPlayerInfoFourthItem()
            }

            // country, birth, age
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        val itemSize = layoutCoordinates.size
                        val position = layoutCoordinates.positionInWindow()

                        val relativeX = position.x - parentOffset.x
                        val relativeY = position.y - parentOffset.y

                        // Calculate the center of the InfoItem
                        val centerX = relativeX + itemSize.width / 2f
                        val centerY = relativeY + itemSize.height / 2f

                        itemSizes[4] = with(density) {
                            DpSize(itemSize.width.toDp(), itemSize.height.toDp())
                        }
                        itemPositions[4] =
                            Offset(centerX - center.value.x, centerY - center.value.y)
                    }
                    .widthIn(max = 130.dp)
            ) {
                NBAPlayerInfoFifthItem()
            }

            // weight(kg/pound), height(cm/feet)
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        val itemSize = layoutCoordinates.size
                        val position = layoutCoordinates.positionInWindow()

                        val relativeX = position.x - parentOffset.x
                        val relativeY = position.y - parentOffset.y

                        // Calculate the center of the InfoItem
                        val centerX = relativeX + itemSize.width / 2f
                        val centerY = relativeY + itemSize.height / 2f

                        itemSizes[5] = with(density) {
                            DpSize(itemSize.width.toDp(), itemSize.height.toDp())
                        }
                        itemPositions[5] =
                            Offset(centerX - center.value.x, centerY - center.value.y)
                    }
                    .widthIn(max = 130.dp)
            ) {
                NBAPlayerInfoSixthItem()
            }
        }

        // league stats
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 12.dp)
                .onGloballyPositioned { layoutCoordinates ->
                    val itemSize = layoutCoordinates.size
                    val position = layoutCoordinates.positionInWindow()

                    val relativeX = position.x - parentOffset.x
                    val relativeY = position.y - parentOffset.y

                    // Calculate the center of the InfoItem
                    val centerX = relativeX + itemSize.width / 2f
                    val centerY = relativeY + itemSize.height / 2f

                    itemSizes[6] = with(density) {
                        DpSize(itemSize.width.toDp(), itemSize.height.toDp())
                    }
                    itemPositions[6] = Offset(centerX - center.value.x, centerY - center.value.y)
                }
        ) {
            NBAPlayerInfoSeventhItem()
        }

        // last game
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 12.dp)
                .onGloballyPositioned { layoutCoordinates ->
                    val itemSize = layoutCoordinates.size
                    val position = layoutCoordinates.positionInWindow()

                    val relativeX = position.x - parentOffset.x
                    val relativeY = position.y - parentOffset.y

                    // Calculate the center of the InfoItem
                    val centerX = relativeX + itemSize.width / 2f
                    val centerY = relativeY + itemSize.height / 2f

                    itemSizes[7] = with(density) {
                        DpSize(itemSize.width.toDp(), itemSize.height.toDp())
                    }
                    itemPositions[7] = Offset(centerX - center.value.x, centerY - center.value.y)
                }
        ) {
            NBAPlayerInfoEighthItem()
        }

        // next game
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 12.dp)
                .onGloballyPositioned { layoutCoordinates ->
                    val itemSize = layoutCoordinates.size
                    val position = layoutCoordinates.positionInWindow()

                    val relativeX = position.x - parentOffset.x
                    val relativeY = position.y - parentOffset.y

                    // Calculate the center of the InfoItem
                    val centerX = relativeX + itemSize.width / 2f
                    val centerY = relativeY + itemSize.height / 2f

                    itemSizes[8] = with(density) {
                        DpSize(itemSize.width.toDp(), itemSize.height.toDp())
                    }
                    itemPositions[8] = Offset(centerX - center.value.x, centerY - center.value.y)
                }
        ) {
            NBAPlayerInfoNinethItem()
        }
    }

    /* ---------------------
       visible ui
       - with animation effect
       --------------------- */
    val firstPosition = itemPositions[0] ?: Offset.Zero
    val firstAnimatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) firstPosition else Offset.Zero,
        animationSpec = tween(1000),
    )
    val secondPosition = itemPositions[1] ?: Offset.Zero
    val secondAnimatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) secondPosition else Offset.Zero,
        animationSpec = tween(1000),
    )
    val thirdPosition = itemPositions[2] ?: Offset.Zero
    val thirdAnimatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) thirdPosition else Offset.Zero,
        animationSpec = tween(1000),
    )
    val fourthPosition = itemPositions[3] ?: Offset.Zero
    val fourthAnimatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) fourthPosition else Offset.Zero,
        animationSpec = tween(1000),
    )
    val fifthPosition = itemPositions[4] ?: Offset.Zero
    val fifthAnimatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) fifthPosition else Offset.Zero,
        animationSpec = tween(1000),
    )
    val sixthPosition = itemPositions[5] ?: Offset.Zero
    val sixthAnimatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) sixthPosition else Offset.Zero,
        animationSpec = tween(1000),
    )
    val seventhPosition = itemPositions[6] ?: Offset.Zero
    val seventhAnimatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) seventhPosition else Offset.Zero,
        animationSpec = tween(1000),
    )
    val eighthPosition = itemPositions[7] ?: Offset.Zero
    val eighthAnimatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) eighthPosition else Offset.Zero,
        animationSpec = tween(1000),
    )
    val ninethPosition = itemPositions[8] ?: Offset.Zero
    val ninethAnimatedPosition by animateOffsetAsState(
        targetValue = if (aniPositions) ninethPosition else Offset.Zero,
        animationSpec = tween(1000),
    )

    // photo, name
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(itemSizes[0] ?: DpSize(width = 130.dp, height = 150.dp))
            .offset {
                IntOffset(
                    firstAnimatedPosition.x.roundToInt(),
                    firstAnimatedPosition.y.roundToInt()
                )
            }
    ) {
        NBAPlayerInfoFirstItem(contentsAlpha = contentsAlpha)
    }

    // logo, team, name
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(itemSizes[1] ?: DpSize(width = 130.dp, height = 150.dp))
            .offset {
                IntOffset(
                    secondAnimatedPosition.x.roundToInt(),
                    secondAnimatedPosition.y.roundToInt()
                )
            }
    ) {
        NBAPlayerInfoSecondItem(contentsAlpha = contentsAlpha)
    }

    // jersey, position
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .size(itemSizes[2] ?: DpSize(width = 130.dp, height = 150.dp))
            .offset {
                IntOffset(
                    thirdAnimatedPosition.x.roundToInt(),
                    thirdAnimatedPosition.y.roundToInt()
                )
            }
    ) {
        NBAPlayerInfoThirdItem(contentsAlpha = contentsAlpha)
    }

    // from school/team, draft info, career info
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .size(itemSizes[3] ?: DpSize(width = 130.dp, height = 150.dp))
            .offset {
                IntOffset(
                    fourthAnimatedPosition.x.roundToInt(),
                    fourthAnimatedPosition.y.roundToInt()
                )
            }
    ) {
        NBAPlayerInfoFourthItem(contentsAlpha = contentsAlpha)
    }

    // country, birth, age
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .size(itemSizes[4] ?: DpSize(width = 130.dp, height = 150.dp))
            .offset {
                IntOffset(
                    fifthAnimatedPosition.x.roundToInt(),
                    fifthAnimatedPosition.y.roundToInt()
                )
            }
    ) {
        NBAPlayerInfoFifthItem(contentsAlpha = contentsAlpha)
    }

    // weight(kg/pound), height(cm/feet)
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .size(itemSizes[5] ?: DpSize(width = 130.dp, height = 150.dp))
            .offset {
                IntOffset(
                    sixthAnimatedPosition.x.roundToInt(),
                    sixthAnimatedPosition.y.roundToInt()
                )
            }
    ) {
        NBAPlayerInfoSixthItem(contentsAlpha = contentsAlpha)
    }

    // league stats
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(itemSizes[6] ?: DpSize(width = screenWidthDp(), height = 150.dp))
            .offset {
                IntOffset(
                    seventhAnimatedPosition.x.roundToInt(),
                    seventhAnimatedPosition.y.roundToInt()
                )
            }
            .clickable {
                displayModel?.let {
                    searchViewModel.send(SearchViewModel.Intent.ShowPlayerStats(playerId = it.info.personId))
                }
            }
    ) {
        NBAPlayerInfoSeventhItem(contentsAlpha = contentsAlpha)
    }

    // last game
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(itemSizes[7] ?: DpSize(width = screenWidthDp(), height = 150.dp))
            .offset {
                IntOffset(
                    eighthAnimatedPosition.x.roundToInt(),
                    eighthAnimatedPosition.y.roundToInt()
                )
            }
            .clickable {
                searchViewModel.send(SearchViewModel.Intent.ShowGameStats(gameType = "previous"))
            }
    ) {
        NBAPlayerInfoEighthItem(contentsAlpha = contentsAlpha)
    }

    // next game
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(itemSizes[8] ?: DpSize(width = screenWidthDp(), height = 150.dp))
            .offset {
                IntOffset(
                    ninethAnimatedPosition.x.roundToInt(),
                    ninethAnimatedPosition.y.roundToInt()
                )
            }
            .clickable {
                searchViewModel.send(SearchViewModel.Intent.ShowGameStats(gameType = "next"))
            }
    ) {
        NBAPlayerInfoNinethItem(contentsAlpha = contentsAlpha)
    }
}

@Composable
fun NBAPlayerInfoFirstItem(
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.info

        /* ---------------------
           ui
           --------------------- */
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            HCapsuleBar()
        }

        URLImage(
            url = player.playerPhoto,
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = player.krname,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = player.displayFirstLast,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            maxLines = 2,
            modifier = Modifier.alpha(contentsAlpha)
        )
    }
}

@Composable
fun NBAPlayerInfoSecondItem(
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.info

        /* ---------------------
           ui
           --------------------- */
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            HCapsuleBar()
        }

        // TODO: "소속팀" 제목 표시 필요
        URLImage(
            url = player.teamLogo,
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = "골든스테이트 워리어스",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.alpha(contentsAlpha)
        )
    }
}

@Composable
fun NBAPlayerInfoThirdItem(
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.info

        /* ---------------------
           ui
           --------------------- */
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            HCapsuleBar()
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "등번호: ",
                fontSize = 15.sp
            )

            Text(
                text = player.jersey,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "포지션: ",
                fontSize = 15.sp
            )

            Text(
                text = player.position,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun NBAPlayerInfoFourthItem(
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.info

        /* ---------------------
           ui
           --------------------- */
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            HCapsuleBar()
        }

        Column(
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "출신 (학교 또는 팀): ",
                fontSize = 15.sp
            )

            Text(
                text = player.school,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "드래프트 순위/년도: ",
                fontSize = 15.sp
            )

            Text(
                text = "${player.draftNumber} / ${player.draftYear}",
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "경력: ",
                fontSize = 15.sp
            )

            // TODO: 은퇴한 경우 -> 2010~2023(14시즌/은퇴)
            Text(
                text = "${player.fromYear}~현재 (${player.seasonExp + 1}년차)",
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun NBAPlayerInfoFifthItem(
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.info

        /* ---------------------
           ui
           --------------------- */
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            HCapsuleBar()
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "국적: ",
                fontSize = 15.sp
            )

            Text(
                text = player.country,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "출생: ",
                fontSize = 15.sp
            )

            Text(
                text = player.birthdate.split("T").firstOrNull() ?: "",
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "나이: ",
                fontSize = 15.sp
            )

            Text(
                text = "${CalendarUtil.calculateAge(player.birthdate)}",
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun NBAPlayerInfoSixthItem(
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.info
        val splittedPlayerHeight = player.height.split("-")
        val playerCmHeight = toCm(splittedPlayerHeight.firstOrNull()?.toIntOrNull() ?: 0, splittedPlayerHeight.lastOrNull()?.toIntOrNull() ?: 0).toInt()
        val playerKgWeight = (player.weight.toDoubleOrNull() ?: 0.0).toKg().toInt()

        /* ---------------------
           ui
           --------------------- */
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            HCapsuleBar()
        }

        Column(
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "키(cm/ft): ",
                fontSize = 15.sp
            )

            Text(
                text = "${playerCmHeight} / ${player.height}",
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "몸무게(kg/lb): ",
                fontSize = 15.sp
            )

            Text(
                text = "${playerKgWeight} / ${player.weight}",
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun NBAPlayerInfoSeventhItem(
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val stats = it.stats

        /* ---------------------
           ui
           --------------------- */
        HCapsuleBar()

        NBATitle(
            leagueName = "NBA 정규시즌",
            leagueSeason = stats?.groupValue?.split("-")?.firstOrNull()?.toIntOrNull() ?: 2024,
            modifier = Modifier.alpha(contentsAlpha)
        )

        stats?.let {
            Row(
                modifier = Modifier
                    .alpha(contentsAlpha)
            ) {
                FBStatDataItem(
                    category = "경기수",
                    data = stats.gp.toString(),
                    customCategoryFontSize = 11,
                    modifier = Modifier.weight(1f)
                )

                FBStatDataItem(
                    category = "경기당 득점",
                    data = stats.ptsPG.toString(),
                    customCategoryFontSize = 11,
                    modifier = Modifier.weight(1f)
                )

                FBStatDataItem(
                    category = "경기당 리바운드",
                    data = stats.rebPG.toString(),
                    customCategoryFontSize = 11,
                    modifier = Modifier.weight(1f)
                )

                FBStatDataItem(
                    category = "경기당 어시스트",
                    data = stats.astPG.toString(),
                    customCategoryFontSize = 11,
                    modifier = Modifier.weight(1f)
                )

                // NOTE: StatsView에서 보여줘도 되지만 상세 기록을 보고 싶게 궁금증을 유발하는 용도의 데이터
                FBStatDataItem(
                    category = "출전 경기 승률",
                    data = stats.winsPct.toString(),
                    customCategoryFontSize = 11,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun NBAPlayerInfoEighthItem(
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val lastGame = it.lastGame
        val lastGamePlayerStats = it.lastGamePlayerStats

        /* ---------------------
           ui
           --------------------- */
        HCapsuleBar()

        Text(
            text = "최근경기",
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.alpha(contentsAlpha)
        )

        lastGame?.let {
            val homeTeam = lastGame.boxScoreTraditional?.homeTeam
            val awayTeam = lastGame.boxScoreTraditional?.awayTeam
            val homeTeamScore = lastGame.lineScore.find { it.teamId == homeTeam?.teamId }?.pts
            val awayTeamScore = lastGame.lineScore.find { it.teamId == awayTeam?.teamId }?.pts

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .alpha(contentsAlpha)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(25.dp)
                    ) {
                        Text(
                            text = EnNameTranslationUtils.translateByDic(TranslationType.TEAM, input = homeTeam?.teamName ?: ""),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light,
                            maxLines = 1
                        )

                        Text(
                            text = (homeTeamScore ?: 0).toString(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = if ((homeTeamScore ?: 0) >= (awayTeamScore ?: 0)) MaterialTheme.colors.primary else Color.Black
                        )

                        Text(
                            text = " vs ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = (awayTeamScore ?: 0).toString(),
                            fontWeight = FontWeight.Medium,
                            color = if ((awayTeamScore ?: 0) >= (homeTeamScore ?: 0)) MaterialTheme.colors.primary else Color.Black
                        )

                        Text(
                            text = EnNameTranslationUtils.translateByDic(TranslationType.TEAM, input = awayTeam?.teamName ?: ""),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light,
                            maxLines = 1
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.height(nbaPlayerInfoViewModel.itemHeight)
                    ) {
                        Text(
                            text = CalendarUtil.formatDate(lastGame.gameSummary?.date ?: ""),
                            fontSize = 15.sp
                        )
                    }
                }

                FBStatDataItem(
                    category = "출전시간",
                    data = "${
                        if (lastGamePlayerStats == null) {
                            ""
                        } else {
                            if (lastGamePlayerStats.position.isEmpty()) {
                                "후보"
                            } else {
                                "선발"
                            }
                        }
                    } / ${lastGamePlayerStats?.statistics?.minutes ?: ""}",
                    customCategoryFontSize = 12,
                    customDataFontSize = 15,
                    customWidth = 80.dp
                )

                FBStatDataItem(
                    category = "득점",
                    data = (lastGamePlayerStats?.statistics?.points ?: 0).toString(),
                    customCategoryFontSize = 12,
                    modifier = Modifier.weight(1f)
                )

                FBStatDataItem(
                    category = "리바운드",
                    data = (lastGamePlayerStats?.statistics?.reboundsTotal ?: 0).toString(),
                    customCategoryFontSize = 12,
                    modifier = Modifier.weight(1f)
                )

                FBStatDataItem(
                    category = "어시스트",
                    data = (lastGamePlayerStats?.statistics?.assists ?: 0).toString(),
                    customCategoryFontSize = 12,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun NBAPlayerInfoNinethItem(
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val nextGame = it.nextGame

        /* ---------------------
           ui
           --------------------- */
        HCapsuleBar()

        Text(
            text = "다음경기",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.alpha(contentsAlpha)
        )

        nextGame?.let {
            val homeTeam = nextGame.lastMeeting?.lastGameHomeTeamName
            val awayTeam = nextGame.lastMeeting?.lastGameVisitorTeamName

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .alpha(contentsAlpha)
            ) {
                Text(
                    text = EnNameTranslationUtils.translateByDic(TranslationType.TEAM, input = homeTeam ?: ""),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = " vs ",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.3f)
                )

                Text(
                    text = EnNameTranslationUtils.translateByDic(TranslationType.TEAM, input = awayTeam ?: ""),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = CalendarUtil.formatDate(nextGame.gameSummary?.date ?: ""),
                fontSize = 15.sp,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}