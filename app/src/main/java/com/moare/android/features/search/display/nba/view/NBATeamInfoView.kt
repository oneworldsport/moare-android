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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.NBAUtil
import com.moare.android.features.search.display.components.FBStatDataItem
import com.moare.android.features.search.display.nba.viewmodel.NBATeamInfoViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamInfoDisplayModel
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.screenWidthDp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun NBATeamInfoView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaTeamInfoViewModel: NBATeamInfoViewModel = hiltViewModel(),
    data: NBATeamInfoDisplayModel
) {
    /* ---------------------
       ui state
       --------------------- */
    val density = LocalDensity.current
    val itemSizes = remember { mutableStateMapOf<Int, DpSize>() }

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by nbaTeamInfoViewModel.displayModel.collectAsState()

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
        if (poppedView == null || poppedView is SportDecodableModel.NBATeamInfo) {
            nbaTeamInfoViewModel.send(NBATeamInfoViewModel.Intent.InitData(data))
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
                // logo, team, name
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
                    NBATeamInfoFirstItem()
                }

                // founded, state and city, conference and division
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
                    NBATeamInfoSecondItem()
                }

                // venue
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
                    NBATeamInfoThirdItem()
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
                NBATeamInfoFourthItem()
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            ) {
                // last game stats
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
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
                    NBATeamInfoFifthItem()
                }

                // next game stats
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
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
                        .widthIn(max = screenWidthDp() / 2)
                ) {
                    NBATeamInfoSixthItem()
                }
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

        // logo, name
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
            NBATeamInfoFirstItem(contentsAlpha = contentsAlpha)
        }

        // founded, state, city
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
            NBATeamInfoSecondItem(contentsAlpha = contentsAlpha)
        }

        // venue
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
            NBATeamInfoThirdItem(contentsAlpha = contentsAlpha)
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
                        searchViewModel.send(SearchViewModel.Intent.ShowTeamStats(teamId = it.team.id))
                    }
                }
        ) {
            NBATeamInfoFourthItem(contentsAlpha = contentsAlpha)
        }

        // last game stats
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(itemSizes[4] ?: DpSize(width = screenWidthDp() / 2, height = 150.dp))
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
            NBATeamInfoFifthItem(contentsAlpha = contentsAlpha)
        }

        // next game stats
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(itemSizes[5] ?: DpSize(width = screenWidthDp() / 2, height = 150.dp))
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
            NBATeamInfoSixthItem(contentsAlpha = contentsAlpha)
        }
    }
}

@Composable
fun NBATeamInfoFirstItem(
    nbaTeamInfoViewModel: NBATeamInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by nbaTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val team = it.team

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
            url = NBAUtil.teamLogoUrl(team.id),
            modifier = Modifier.alpha(contentsAlpha),
            isSvg = true
        )

        Text(
            text = nbaTeamInfoViewModel.teamNameDictionary["full_${team.id}"] ?: team.fullName,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = team.fullName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            maxLines = 2,
            modifier = Modifier.alpha(contentsAlpha)
        )
    }
}

@Composable
fun NBATeamInfoSecondItem(
    nbaTeamInfoViewModel: NBATeamInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by nbaTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val team = it.team

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
                text = "창단연도: ",
                fontSize = 15.sp
            )

            Text(
                text = team.yearFounded.toString(),
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "연고지: ",
                fontSize = 15.sp
            )

            Text(
                text = team.state,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "컨퍼런스/디비전: ",
                fontSize = 15.sp
            )

            Text(
                text = "${team.teamConference} / ${team.teamDivision}",
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun NBATeamInfoThirdItem(
    nbaTeamInfoViewModel: NBATeamInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by nbaTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val team = it.team
        val venue = it.venue

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
                text = "홈구장: ",
                fontSize = 15.sp
            )

            Text(
                text = nbaTeamInfoViewModel.teamNameDictionary["venue_${team.id}"] ?: venue.name,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "좌석수: ",
                fontSize = 15.sp
            )

            Text(
                text = venue.capacity.toString(),
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "개장: ",
                fontSize = 15.sp
            )

            Text(
                text = venue.opened.toString(),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun NBATeamInfoFourthItem(
    nbaTeamInfoViewModel: NBATeamInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by nbaTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val team = it.team
        val stats = it.stats

        /* ---------------------
           ui
           --------------------- */
        HCapsuleBar()

        NBATitle(
            leagueName = "NBA 정규시즌",
            leagueSeason = stats?.groupValue?.split("-")?.firstOrNull()?.toIntOrNull() ?: 2024,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .alpha(contentsAlpha)
        )

        stats?.let {
            Row(
                modifier = Modifier
                    .alpha(contentsAlpha)
            ) {
                FBStatDataItem(
                    category = "서부 컨퍼런스 순위",
                    data = team.confRank.toString(),
                    customCategoryFontSize = 13,
                    modifier = Modifier.weight(1f)
                )

                FBStatDataItem(
                    category = "승",
                    data = stats.wins.toString(),
                    modifier = Modifier.weight(1f)
                )

                FBStatDataItem(
                    category = "패",
                    data = stats.losses.toString(),
                    modifier = Modifier.weight(1f)
                )

                FBStatDataItem(
                    category = "경기당 득점",
                    data = stats.ptsPG.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun NBATeamInfoFifthItem(
    nbaTeamInfoViewModel: NBATeamInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by nbaTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val lastGame = it.lastGame

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
            val homeTeamScore = lastGame.lineScore.find { it.teamId == homeTeam?.teamId }?.pts ?: 0
            val awayTeamScore = lastGame.lineScore.find { it.teamId == awayTeam?.teamId }?.pts ?: 0

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .alpha(contentsAlpha)
            ) {
                Text(
                    text = if (homeTeam == null) "" else nbaTeamInfoViewModel.teamNameDictionary["short_${homeTeam.teamId}"] ?: homeTeam.teamCity,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = (homeTeamScore).toString(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.4f),
                    color = if ((homeTeamScore) >= (awayTeamScore)) MaterialTheme.colors.primary else Color.Black
                )

                Text(
                    text = " vs ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.3f)
                )

                Text(
                    text = (awayTeamScore).toString(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.4f),
                    color = if ((awayTeamScore) >= (homeTeamScore)) MaterialTheme.colors.primary else Color.Black
                )

                Text(
                    text = if (awayTeam == null) "" else nbaTeamInfoViewModel.teamNameDictionary["short_${awayTeam.teamId}"] ?: awayTeam.teamCity,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = CalendarUtil.formatDate(lastGame.gameSummary?.date),
                fontSize = 15.sp,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}

@Composable
fun NBATeamInfoSixthItem(
    nbaTeamInfoViewModel: NBATeamInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by nbaTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val nextGame = it.nextGame

        /* ---------------------
           ui
           --------------------- */
        HCapsuleBar()

        Text(
            text = "다음경기",
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.alpha(contentsAlpha)
        )

        nextGame?.let {
            val lastMeeting = nextGame.lastMeeting

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .alpha(contentsAlpha)
            ) {
                Text(
                    text = if (lastMeeting?.lastGameHomeTeamId == null) "" else nbaTeamInfoViewModel.teamNameDictionary["short_${lastMeeting.lastGameHomeTeamId}"] ?: lastMeeting.lastGameHomeTeamCity,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = " vs ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.3f)
                )

                Text(
                    text = if (lastMeeting?.lastGameVisitorTeamId == null) "" else nbaTeamInfoViewModel.teamNameDictionary["short_${lastMeeting.lastGameVisitorTeamId}"] ?: lastMeeting.lastGameVisitorTeamCity,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = CalendarUtil.formatDate(nextGame.gameSummary?.date),
                fontSize = 15.sp,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}





























