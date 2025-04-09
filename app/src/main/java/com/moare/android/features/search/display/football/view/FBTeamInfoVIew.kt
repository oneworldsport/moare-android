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
import com.moare.android.core.util.EnNameTranslationUtils
import com.moare.android.core.util.TranslationType
import com.moare.android.features.search.display.components.FBStatDataItem
import com.moare.android.features.search.display.football.viewmodel.FBTeamInfoViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamInfoDisplayModel
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.screenWidthDp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun FBTeamInfoView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbTeamInfoViewModel: FBTeamInfoViewModel = hiltViewModel(),
    data: FBTeamInfoDisplayModel
) {
    /* ---------------------
       ui state
       --------------------- */
    val density = LocalDensity.current
    val itemSizes = remember { mutableStateMapOf<Int, DpSize>() }

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by fbTeamInfoViewModel.displayModel.collectAsState()

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
        if (poppedView == null || poppedView is SportDecodableModel.FBTeamInfo) {
            fbTeamInfoViewModel.send(FBTeamInfoViewModel.Intent.InitData(data))
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
                // logo, name
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
                    FBTeamInfoFirstItem()
                }

                // founded, city, country
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
                    FBTeamInfoSecondItem()
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
                    FBTeamInfoThirdItem()
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
                FBTeamInfoFourthItem()
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
                    FBTeamInfoFifthItem()
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
                    FBTeamInfoSixthItem()
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
            FBTeamInfoFirstItem(contentsAlpha = contentsAlpha)
        }

        // founded, city, country
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
            FBTeamInfoSecondItem(contentsAlpha = contentsAlpha)
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
            FBTeamInfoThirdItem(contentsAlpha = contentsAlpha)
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
            FBTeamInfoFourthItem(contentsAlpha = contentsAlpha)
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
            FBTeamInfoFifthItem(contentsAlpha = contentsAlpha)
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
            FBTeamInfoSixthItem(contentsAlpha = contentsAlpha)
        }
    }
}

@Composable
fun FBTeamInfoFirstItem(
    fbTeamInfoViewModel: FBTeamInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by fbTeamInfoViewModel.displayModel.collectAsState()

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
            url = team.logo,
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = EnNameTranslationUtils.translateByDic(TranslationType.TEAM, false, team.name),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = team.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            maxLines = 2,
            modifier = Modifier.alpha(contentsAlpha)
        )
    }
}

@Composable
fun FBTeamInfoSecondItem(
    fbTeamInfoViewModel: FBTeamInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by fbTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val team = it.team
        val venue = it.venue

        var countryKrName by remember { mutableStateOf("") }

        LaunchedEffect(team) {
            countryKrName = EnNameTranslationUtils.translateByDic(TranslationType.COUNTRY, input = team.country)
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
                text = "창단연도: ",
                fontSize = 15.sp
            )

            Text(
                text = team.founded.toString(),
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "연고지: ",
                fontSize = 15.sp
            )

            Text(
                text = venue.city,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "소속나라: ",
                fontSize = 15.sp
            )

            Text(
                text = countryKrName,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FBTeamInfoThirdItem(
    fbTeamInfoViewModel: FBTeamInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by fbTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
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
                text = venue.name,
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
    }
}

@Composable
fun FBTeamInfoFourthItem(
    fbTeamInfoViewModel: FBTeamInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by fbTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val stats = it.stats
        val league = it.stats?.league

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

        stats?.let {
            Row(
                modifier = Modifier
                    .alpha(contentsAlpha)
            ) {
                FBStatDataItem(
                    category = "승",
                    data = stats.fixtures.wins.total.toString()
                )

                FBStatDataItem(
                    category = "무",
                    data = stats.fixtures.draws.total.toString()
                )

                FBStatDataItem(
                    category = "패",
                    data = stats.fixtures.loses.total.toString()
                )

                FBStatDataItem(
                    category = "득점",
                    data = stats.goals.teamGoalsFor.total.total.toString()
                )

                FBStatDataItem(
                    category = "실점",
                    data = stats.goals.teamGoalsAgainst.total.total.toString()
                )
            }
        }
    }
}

@Composable
fun FBTeamInfoFifthItem(
    fbTeamInfoViewModel: FBTeamInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by fbTeamInfoViewModel.displayModel.collectAsState()

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .alpha(contentsAlpha)
            ) {
                Text(
                    text = EnNameTranslationUtils.translateByDic(TranslationType.TEAM, input = lastGame.teams.home.name),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = lastGame.goals.home.toString(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.3f),
                    color = if ((lastGame.goals.home) >= (lastGame.goals.away)) MaterialTheme.colors.primary else Color.Black
                )

                Text(
                    text = " vs ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.3f)
                )

                Text(
                    text = lastGame.goals.away.toString(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.3f),
                    color = if ((lastGame.goals.away) >= (lastGame.goals.home)) MaterialTheme.colors.primary else Color.Black
                )

                Text(
                    text = EnNameTranslationUtils.translateByDic(TranslationType.TEAM, input = lastGame.teams.away.name),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = CalendarUtil.formatDate(lastGame.fixture.date),
                fontSize = 15.sp,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}

@Composable
fun FBTeamInfoSixthItem(
    fbTeamInfoViewModel: FBTeamInfoViewModel = hiltViewModel(),
    contentsAlpha: Float = 1f
) {
    val displayModel by fbTeamInfoViewModel.displayModel.collectAsState()

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .alpha(contentsAlpha)
            ) {
                Text(
                    text = EnNameTranslationUtils.translateByDic(TranslationType.TEAM, input = nextGame.teams.home.name),
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
                    text = EnNameTranslationUtils.translateByDic(TranslationType.TEAM, input = nextGame.teams.away.name),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
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














