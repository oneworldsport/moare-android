package com.moare.android.features.search.display.football.view

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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.EnNameTranslationUtils
import com.moare.android.core.util.TranslationType
import com.moare.android.features.search.display.components.FBStatDataItem
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoIntent
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.util.convertPxToDp
import com.moare.android.ui.util.screenWidthDp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun FBPlayerInfoView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbPlayerInfoViewModel: FBPlayerInfoViewModel = hiltViewModel(),
    data: FBPlayerInfoDisplayModel
) {
    /* ---------------------
       ui state
       --------------------- */
    val density = LocalDensity.current
    val itemSizes = remember { mutableStateMapOf<Int, DpSize>() }

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by fbPlayerInfoViewModel.displayModel.collectAsState()

    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    var parentPosition by remember { mutableStateOf(Offset.Zero) }
    var parentCenter by remember { mutableStateOf(Offset.Zero) }
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
        if (poppedView == null || poppedView is SportDecodableModel.FBPlayerInfo) {
            fbPlayerInfoViewModel.send(FBPlayerInfoIntent.InitData(data))
        }
    }

    LaunchedEffect(itemPositions) {
        if (itemPositions.size == 6) {
            aniPositions = true
            delay(1000)
            showContents = true
        }
    }

    Box(
        contentAlignment = Alignment.Center,
    ) {
        /* ---------------------
           invisible ui
           - for position
           --------------------- */
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .onGloballyPositioned { parentCoordinates ->
                    parentPosition = parentCoordinates.positionInWindow()
                    parentCenter = Offset(
                        x = parentCoordinates.size.width / 2f,
                        y = parentCoordinates.size.height / 2f
                    )
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

                            val relativeX = position.x - parentPosition.x
                            val relativeY = position.y - parentPosition.y

                            // Calculate the center of the InfoItem
                            val centerX = relativeX + itemSize.width / 2f
                            val centerY = relativeY + itemSize.height / 2f

                            itemSizes[0] = with(density) {
                                DpSize(itemSize.width.toDp(), itemSize.height.toDp())
                            }
                            itemPositions[0] =
                                Offset(centerX - parentCenter.x, centerY - parentCenter.y)
                        }
                        .widthIn(max = 130.dp)
                ) {
                    FBPlayerInfoFirstItem()
                }

                // age, birth, nationality
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            val itemSize = layoutCoordinates.size
                            val position = layoutCoordinates.positionInWindow()

                            val relativeX = position.x - parentPosition.x
                            val relativeY = position.y - parentPosition.y

                            // Calculate the center of the InfoItem
                            val centerX = relativeX + itemSize.width / 2f
                            val centerY = relativeY + itemSize.height / 2f

                            itemSizes[1] = with(density) {
                                DpSize(itemSize.width.toDp(), itemSize.height.toDp())
                            }
                            itemPositions[1] =
                                Offset(centerX - parentCenter.x, centerY - parentCenter.y)
                        }
                        .widthIn(max = 130.dp)
                ) {
                    FBPlayerInfoSecondItem()
                }

                // weight, height
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            val itemSize = layoutCoordinates.size
                            val position = layoutCoordinates.positionInWindow()

                            val relativeX = position.x - parentPosition.x
                            val relativeY = position.y - parentPosition.y

                            // Calculate the center of the InfoItem
                            val centerX = relativeX + itemSize.width / 2f
                            val centerY = relativeY + itemSize.height / 2f

                            itemSizes[2] = with(density) {
                                DpSize(itemSize.width.toDp(), itemSize.height.toDp())
                            }
                            itemPositions[2] =
                                Offset(centerX - parentCenter.x, centerY - parentCenter.y)
                        }
                        .widthIn(max = 130.dp)
                ) {
                    FBPlayerInfoThirdItem()
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

                        val relativeX = position.x - parentPosition.x
                        val relativeY = position.y - parentPosition.y

                        // Calculate the center of the InfoItem
                        val centerX = relativeX + itemSize.width / 2f
                        val centerY = relativeY + itemSize.height / 2f

                        itemSizes[3] = with(density) {
                            DpSize(itemSize.width.toDp(), itemSize.height.toDp())
                        }
                        itemPositions[3] =
                            Offset(centerX - parentCenter.x, centerY - parentCenter.y)
                    }
            ) {
                FBPlayerInfoFourthItem()
            }

            // last game stats
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .onGloballyPositioned { layoutCoordinates ->
                        val itemSize = layoutCoordinates.size
                        val position = layoutCoordinates.positionInWindow()

                        val relativeX = position.x - parentPosition.x
                        val relativeY = position.y - parentPosition.y

                        // Calculate the center of the InfoItem
                        val centerX = relativeX + itemSize.width / 2f
                        val centerY = relativeY + itemSize.height / 2f

                        itemSizes[4] = with(density) {
                            DpSize(itemSize.width.toDp(), itemSize.height.toDp())
                        }
                        itemPositions[4] =
                            Offset(centerX - parentCenter.x, centerY - parentCenter.y)
                    }
            ) {
                FBPlayerInfoFifthItem()
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

                        val relativeX = position.x - parentPosition.x
                        val relativeY = position.y - parentPosition.y

                        // Calculate the center of the InfoItem
                        val centerX = relativeX + itemSize.width / 2f
                        val centerY = relativeY + itemSize.height / 2f

                        itemSizes[5] = with(density) {
                            DpSize(itemSize.width.toDp(), itemSize.height.toDp())
                        }
                        itemPositions[5] =
                            Offset(centerX - parentCenter.x, centerY - parentCenter.y)
                    }
            ) {
                FBPlayerInfoSixthItem()
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
            FBPlayerInfoFirstItem(contentsAlpha = contentsAlpha)
        }

        // age, birth, nationality
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .size(itemSizes[1] ?: DpSize(width = 130.dp, height = 150.dp))
                .offset {
                    IntOffset(
                        secondAnimatedPosition.x.roundToInt(),
                        secondAnimatedPosition.y.roundToInt()
                    )
                }
        ) {
            FBPlayerInfoSecondItem(contentsAlpha = contentsAlpha)
        }

        // weight, height
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
            FBPlayerInfoThirdItem(contentsAlpha = contentsAlpha)
        }

        // league stats
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(itemSizes[3] ?: DpSize(width = screenWidthDp(), height = 150.dp))
                .offset {
                    IntOffset(
                        fourthAnimatedPosition.x.roundToInt(),
                        fourthAnimatedPosition.y.roundToInt()
                    )
                }
                .clickable {
                    displayModel?.let {
                        searchViewModel.send(SearchViewModel.Intent.ShowPlayerStats(playerId = it.info.id))
                    }
                }
        ) {
            FBPlayerInfoFourthItem(contentsAlpha = contentsAlpha)
        }

        // last game stats
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(itemSizes[4] ?: DpSize(width = screenWidthDp(), height = 150.dp))
                .offset {
                    IntOffset(
                        fifthAnimatedPosition.x.roundToInt(),
                        fifthAnimatedPosition.y.roundToInt()
                    )
                }
                .clickable {
                    searchViewModel.send(SearchViewModel.Intent.ShowGameStats(gameType = "previous"))
                }
        ) {
            FBPlayerInfoFifthItem(contentsAlpha = contentsAlpha)
        }

        // next game stats
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(itemSizes[5] ?: DpSize(width = screenWidthDp(), height = 150.dp))
                .offset {
                    IntOffset(
                        sixthAnimatedPosition.x.roundToInt(),
                        sixthAnimatedPosition.y.roundToInt()
                    )
                }
                .clickable {
                    searchViewModel.send(SearchViewModel.Intent.ShowGameStats(gameType = "next"))
                }
        ) {
            FBPlayerInfoSixthItem(contentsAlpha = contentsAlpha)
        }
    }
}

@Composable
fun FBPlayerInfoFirstItem(
    fbPlayerInfoViewModel: FBPlayerInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by fbPlayerInfoViewModel.displayModel.collectAsState()

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
            url = player.photo,
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = fbPlayerInfoViewModel.playerNameDictionary["${player.id}"] ?: player.name,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = player.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            maxLines = 2,
            modifier = Modifier.alpha(contentsAlpha)
        )
    }
}

@Composable
fun FBPlayerInfoSecondItem(
    fbPlayerInfoViewModel: FBPlayerInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by fbPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.info

        var nationalityKrName by remember { mutableStateOf("") }

        LaunchedEffect(player) {
            nationalityKrName = EnNameTranslationUtils.translateByDic(TranslationType.COUNTRY, input = player.nationality)
        }

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
                text = nationalityKrName,
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
                text = player.birth.date,
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
                text = "${CalendarUtil.calculateAge(player.birth.date)}",
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FBPlayerInfoThirdItem(
    fbPlayerInfoViewModel: FBPlayerInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by fbPlayerInfoViewModel.displayModel.collectAsState()

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
                text = "키: ",
                fontSize = 15.sp
            )

            Text(
                text = player.height,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "몸무게: ",
                fontSize = 15.sp
            )

            Text(
                text = player.weight,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FBPlayerInfoFourthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbPlayerInfoViewModel: FBPlayerInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by fbPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val stats = it.stats
        val team = stats?.team
        val league = stats?.league

        /* ---------------------
           ui
           --------------------- */
        HCapsuleBar()

        league?.let {
            LeagueTitle(
                url = league.logo,
                leagueName = league.name,
                leagueSeason = league.season,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }

        Row(
            modifier = Modifier
                .alpha(contentsAlpha)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Text(
                    text = "소속팀",
                    fontSize = 15.sp
                )

                team?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(fbPlayerInfoViewModel.itemHeight)
                    ) {
                        URLImage(
                            url = team.logo,
                            modifier = Modifier.padding(end = 4.dp),
                            size = URLImageSize.SMALL
                        )

                        Text(
                            text = fbPlayerInfoViewModel.teamNameDictionary["full_${team.id}"] ?: team.name,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            stats?.let {
                FBStatDataItem(
                    category = "경기수",
                    data = stats.games.appearences.toString()
                )

                FBStatDataItem(
                    category = "골",
                    data = stats.goals.total.toString()
                )

                FBStatDataItem(
                    category = "도움",
                    data = stats.goals.assists.toString()
                )
            }
        }
    }
}

@Composable
fun FBPlayerInfoFifthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbPlayerInfoViewModel: FBPlayerInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by fbPlayerInfoViewModel.displayModel.collectAsState()

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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .alpha(contentsAlpha)
        ) {
            lastGame?.let {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = fbPlayerInfoViewModel.teamNameDictionary["short_${lastGame.teams.home.id}"] ?: lastGame.teams.home.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light,
                            maxLines = 1
                        )

                        Text(
                            text = lastGame.goals.home.toString(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = if ((lastGame.goals.home) >= (lastGame.goals.away)) MaterialTheme.colors.primary else Color.Black
                        )

                        Text(
                            text = " vs ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = lastGame.goals.away.toString(),
                            fontWeight = FontWeight.Medium,
                            color = if ((lastGame.goals.away) >= (lastGame.goals.home)) MaterialTheme.colors.primary else Color.Black
                        )

                        Text(
                            text = fbPlayerInfoViewModel.teamNameDictionary["short_${lastGame.teams.away.id}"] ?: lastGame.teams.away.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light,
                            maxLines = 1
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.height(fbPlayerInfoViewModel.itemHeight)
                    ) {
                        Text(
                            text = CalendarUtil.formatDate(lastGame.fixture.date),
                            fontSize = 15.sp
                        )
                    }
                }
            }

            lastGamePlayerStats?.let {
                FBStatDataItem(
                    category = "출전시간",
                    data = "${
                        if (lastGamePlayerStats.games.substitute) {
                            "후보"
                        } else {
                            "선발"
                        }
                    } / ${lastGamePlayerStats.games.minutes}분",
                    customWidth = 80.dp
                )

                FBStatDataItem(
                    category = "골",
                    data = lastGamePlayerStats.goals.total.toString()
                )

                FBStatDataItem(
                    category = "도움",
                    data = lastGamePlayerStats.goals.assists.toString()
                )
            }
        }
    }
}

@Composable
fun FBPlayerInfoSixthItem(
    fbPlayerInfoViewModel: FBPlayerInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by fbPlayerInfoViewModel.displayModel.collectAsState()

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .alpha(contentsAlpha)
            ) {
                Text(
                    text = fbPlayerInfoViewModel.teamNameDictionary["short_${nextGame.teams.home.id}"] ?: nextGame.teams.home.name,
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
                    text = fbPlayerInfoViewModel.teamNameDictionary["short_${nextGame.teams.away.id}"] ?: nextGame.teams.away.name,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = CalendarUtil.formatDate(nextGame.fixture.date),
                fontSize = 15.sp,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}