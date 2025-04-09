package com.moare.android.features.search.display.nba.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.R
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.constants.UIConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.NBAUtil
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.display.nba.viewmodel.NBAGameStatsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
import com.moare.android.features.search.models.models.nba.NBABoxScoreTeamPlayer
import com.moare.android.features.search.models.models.nba.NBAGameBoxScoreStats
import com.moare.android.features.search.models.models.nba.NBALineScore
import com.moare.android.ui.common.components.CapsuleButton
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.HCapsuleBarSize
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.RoundedBorderText
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar

@Composable
fun NBAGameStatsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaGameStatsViewModel: NBAGameStatsViewModel = hiltViewModel(),
    data: NBAGameStatsDisplayModel
) {
    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by nbaGameStatsViewModel.displayModel.collectAsState()
    val firstSelectedIndex by nbaGameStatsViewModel.firstSelectedIndex.collectAsState()
    val secondSelectedIndex by nbaGameStatsViewModel.secondSelectedIndex.collectAsState()

    val season = displayModel?.game?.gameSummary?.season

    val nbaLeagueScheduleData by searchViewModel.nbaLeagueScheduleData.collectAsState()
    val nbaTeamScheduleData by searchViewModel.nbaTeamScheduleData.collectAsState()
    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       etc
       --------------------- */
    val secondSelectedCategoryPosition = with(LocalDensity.current) {
        val attackCategoriesSize = StringConstants.NBA.gameStatsAttackCategories.size
        val defendCategoriesSize = StringConstants.NBA.gameStatsDefendCategories.size

        if (secondSelectedIndex in 0 until attackCategoriesSize) {
            (nbaGameStatsViewModel.itemWidth * secondSelectedIndex).toPx()
        } else if (secondSelectedIndex in attackCategoriesSize until attackCategoriesSize + defendCategoriesSize) {
            ((nbaGameStatsViewModel.itemWidth * secondSelectedIndex) + nbaGameStatsViewModel.barWidth).toPx()
        } else {
            ((nbaGameStatsViewModel.itemWidth * secondSelectedIndex) + (nbaGameStatsViewModel.barWidth * 2)).toPx()
        }
    }.toInt()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.NBAGameStats) {
            nbaGameStatsViewModel.send(NBAGameStatsViewModel.Intent.InitData(data))
        }
    }

    LaunchedEffect(Unit) {
        searchViewModel.send(SearchViewModel.Intent.RefreshGame(category = "basketball"))
    }

    // scroll to category that matches with the keyword,
    // and when first category list's item is selected by click
    LaunchedEffect(firstSelectedIndex) {
        if (nbaGameStatsViewModel.shouldScrollCategory) {
            horizontalScrollState.animateScrollTo(
                value = secondSelectedCategoryPosition,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearOutSlowInEasing
                )
            )
        }
    }

    /* ---------------------
       ui
       --------------------- */
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        /* ---------------------
           game title, info
           --------------------- */
        Row(
            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            NBATitle(
                leagueName = "NBA",
                leagueSeason = season?.split("-")?.firstOrNull()?.toIntOrNull() ?: 2024
            )

            Text(
                text = " - 정규시즌",
                fontSize = 14.sp
            )
        }

        NBAGameStatsScoreInfoItem()

        Box(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .height(1.dp)
                .clip(RoundedCornerShape(10.dp))
                .padding(horizontal = UIConstants.Padding.defaultHPadding)
                .background(MaterialTheme.colors.primary)
        )

        if (displayModel?.game?.gameSummary?.gameStatusId != 1) {
            /* ---------------------
               team select button
               --------------------- */
            NBAGameStatsTeamButtonAdditionalInfoContainer()

            /* ---------------------
               players stats
               --------------------- */
            // category
            Row(
                modifier = Modifier.padding(top = 6.dp)
            ) {
                NBAGameStatsFirstCategoryItem()

                Row(
                    Modifier.horizontalScroll(horizontalScrollState)
                ) {
                    Column {
                        NBAGameStatsFirstCategoryList()
                        NBAGameStatsSecondCategoryList()
                    }
                }
            }

            // stats data
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                Row {
                    NBAGameStatsFirstDataList()

                    Row(
                        Modifier.horizontalScroll(horizontalScrollState)
                    ) {
                        NBAGameStatsDataList()
                    }
                }
            }
        } else {
            Text(
                text = "경기 시작 후 데이터가 업데이트됩니다.",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )

            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun NBAGameStatsScoreInfoItem(
    nbaGameStatsViewModel: NBAGameStatsViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by nbaGameStatsViewModel.displayModel.collectAsState()
    val homeTeamLineScore by nbaGameStatsViewModel.homeTeamLineScore.collectAsState()
    val awayTeamLineScore by nbaGameStatsViewModel.awayTeamLineScore.collectAsState()

    val game = displayModel?.game
    val homeTeamId = game?.gameSummary?.homeTeamId
    val awayTeamId = game?.gameSummary?.visitorTeamId

    /* ---------------------
       constants
       --------------------- */
    val gameStatusText = when (game?.gameSummary?.gameStatusId) {
        1 -> StringConstants.gameNotStartedStr
        2 -> if (homeTeamLineScore?.ptsOt3 != null) {
            StringConstants.NBA.gameOt3
        } else if (homeTeamLineScore?.ptsOt2 != null) {
            StringConstants.NBA.gameOt2
        } else if (homeTeamLineScore?.ptsOt1 != null) {
            StringConstants.NBA.gameOt1
        } else if (homeTeamLineScore?.ptsQtr4 != null) {
            StringConstants.NBA.gameQtr4
        } else if (homeTeamLineScore?.ptsQtr3 != null) {
            StringConstants.NBA.gameQtr3
        } else if (homeTeamLineScore?.ptsQtr2 != null) {
            StringConstants.NBA.gameQtr2
        } else if (homeTeamLineScore?.ptsQtr1 != null) {
            StringConstants.NBA.gameQtr1
        } else {
            ""
        }
        3 -> StringConstants.gameFinishedStr
        else -> ""
    }

    val gameStatusColor = if (game?.gameSummary?.gameStatusId == 2) {
        MaterialTheme.colors.primary
    } else {
        Color.Gray
    }

    /* ---------------------
       ui
       --------------------- */
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = UIConstants.Padding.defaultHPadding)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(0.4f)
                .padding(top = 26.dp) // for NBAGameStatsLineScoreTitle. TODO: NBATitle 과의 간격 줄이고 싶음
        ) {
            URLImage(
                url = if (homeTeamId != null) NBAUtil.teamLogoUrl(homeTeamId) else "",
                size = URLImageSize.SMALL,
                isSvg = true
            )

            Row {
                // TODO: RoundedBorderText 는 왼쪽 정렬, 팀 이름은 가운데 정렬 하고 싶음
                RoundedBorderText(
                    text = "홈",
                    fontSize = 11.sp,
                    radius = 4.dp,
                    textColor = Moare,
                    borderColor = Moare
                )
                Text(
                    text = nbaGameStatsViewModel.teamNameDictionary["short_$homeTeamId"] ?: "",
                    fontSize = 13.sp,
                    maxLines = 2,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // game status
            CapsuleButton(
                text = gameStatusText,
                color = gameStatusColor,
                isDisabled = true,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {}

            Row {
                RoundedBorderText(
                    text = "원정",
                    fontSize = 11.sp,
                    radius = 4.dp,
                    textColor = Color.Gray,
                    borderColor = Color.Gray
                )
                Text(
                    text = nbaGameStatsViewModel.teamNameDictionary["short_$awayTeamId"] ?: "",
                    fontSize = 13.sp,
                    maxLines = 2,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            URLImage(
                url = if (awayTeamId != null) NBAUtil.teamLogoUrl(awayTeamId) else "",
                size = URLImageSize.SMALL,
                isSvg = true
            )
        }

        homeTeamLineScore?.let { home ->
            awayTeamLineScore?.let { away ->
                NBAGameStatsLineScoreContainer(
                    homeTeamLineScore = home,
                    awayTeamLineScore = away,
                    modifier = Modifier.height(127.dp).weight(1f) // 25 + 1 + 50 + 1 + 50
                )
            }
        }
    }
}

@Composable
fun NBAGameStatsLineScoreContainer(
    nbaGameStatsViewModel: NBAGameStatsViewModel = hiltViewModel(),
    homeTeamLineScore: NBALineScore,
    awayTeamLineScore: NBALineScore,
    modifier: Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Box(Modifier.height(26.dp)) // Empty space to position pts to same line with linescore

                Box(
                    modifier = Modifier.height(nbaGameStatsViewModel.lineScoreItemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = homeTeamLineScore.pts.toString(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(start = 4.dp, end = 8.dp)
                            .width(30.dp),
                        color = if (homeTeamLineScore.pts >= awayTeamLineScore.pts) MaterialTheme.colors.primary else Color.Black
                    )
                }
            }

            Column(
                Modifier.weight(1f)
            ) {
                NBAGameStatsLineScoreTitle(homeTeamLineScore)

                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Gray)
                        .alpha(0.5f)
                )

                NBAGameStatsLineScoreItem(lineScore = homeTeamLineScore)
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Gray)
                .alpha(0.5f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.height(nbaGameStatsViewModel.lineScoreItemHeight)
        ) {
            Text(
                text = awayTeamLineScore.pts.toString(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(start = 4.dp, end = 8.dp)
                    .width(30.dp),
                color = if (awayTeamLineScore.pts >= homeTeamLineScore.pts) MaterialTheme.colors.primary else Color.Black
            )

            NBAGameStatsLineScoreItem(lineScore = awayTeamLineScore)
        }
    }
}

@Composable
fun NBAGameStatsLineScoreTitle(
    lineScore: NBALineScore
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().height(25.dp)
    ) {
        VCapsuleBar(modifier = Modifier.alpha(0.5f))
        Text(
            text = "1쿼터",
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        VCapsuleBar(modifier = Modifier.alpha(0.5f))
        Text(
            text = "2쿼터",
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        VCapsuleBar(modifier = Modifier.alpha(0.5f))
        Text(
            text = "3쿼터",
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        VCapsuleBar(modifier = Modifier.alpha(0.5f))
        Text(
            text = "4쿼터",
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )

        if (lineScore.ptsOt1 != 0) {
            VCapsuleBar(modifier = Modifier.alpha(0.5f))
            Text(
                text = "연장 1쿼터",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
        }

        if (lineScore.ptsOt2 != 0) {
            VCapsuleBar(modifier = Modifier.alpha(0.5f))
            Text(
                text = "연장 2쿼터",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
        }

        if (lineScore.ptsOt3 != 0) {
            VCapsuleBar(modifier = Modifier.alpha(0.5f))
            Text(
                text = "연장 3쿼터",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun NBAGameStatsLineScoreItem(
    nbaGameStatsViewModel: NBAGameStatsViewModel = hiltViewModel(),
    lineScore: NBALineScore
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().height(nbaGameStatsViewModel.lineScoreItemHeight)
    ) {
        VCapsuleBar(modifier = Modifier.alpha(0.5f))
        Text(
            text = lineScore.ptsQtr1.toString(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        VCapsuleBar(modifier = Modifier.alpha(0.5f))
        Text(
            text = lineScore.ptsQtr2.toString(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        VCapsuleBar(modifier = Modifier.alpha(0.5f))
        Text(
            text = lineScore.ptsQtr3.toString(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        VCapsuleBar(modifier = Modifier.alpha(0.5f))
        Text(
            text = lineScore.ptsQtr4.toString(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        // TODO: 홈, 원정 둘중에 하나는 0이 아닌데 다른 팀은 0일때 0인팀의 UI가 깨짐
        if (lineScore.ptsOt1 != 0) {
            VCapsuleBar(modifier = Modifier.alpha(0.5f))
            Text(
                text = lineScore.ptsOt1.toString(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }

        if (lineScore.ptsOt2 != 0) {
            VCapsuleBar(modifier = Modifier.alpha(0.5f))
            Text(
                text = lineScore.ptsOt2.toString(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }

        if (lineScore.ptsOt3 != 0) {
            VCapsuleBar(modifier = Modifier.alpha(0.5f))
            Text(
                text = lineScore.ptsOt3.toString(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun NBAGameStatsTeamButtonAdditionalInfoContainer(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaGameStatsViewModel: NBAGameStatsViewModel = hiltViewModel()
) {
    val density = LocalDensity.current
    var teamButtonWidth by remember { mutableStateOf(0.dp) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by nbaGameStatsViewModel.displayModel.collectAsState()
    val selectedIndex by nbaGameStatsViewModel.selectedTeamIndex.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val barOffset by animateDpAsState(
        targetValue = if (selectedIndex == 0) {
            getOffsetOfAniCapsuleBar(itemWidth = teamButtonWidth, barWidth = 50.dp)
        } else {
            2.dp + getOffsetOfAniCapsuleBar(itemWidth = teamButtonWidth, barWidth = 50.dp, index = selectedIndex)
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    displayModel?.let { displayModel ->
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(50.dp)
                ) {
                    // home
                    NBAGameStatsTeamButton(
                        team = nbaGameStatsViewModel.teamNameDictionary["short_${displayModel.game.gameSummary?.homeTeamId}"] ?: "",
                        index = 0,
                        modifier = Modifier
                            .weight(1f)
                            .onGloballyPositioned { layoutCoordinates ->
                                with(density) {
                                    teamButtonWidth = layoutCoordinates.size.width.toDp()
                                }
                            }
                    )

                    VCapsuleBar(modifier = Modifier.alpha(0.5f))

                    // away
                    NBAGameStatsTeamButton(
                        team = nbaGameStatsViewModel.teamNameDictionary["short_${displayModel.game.gameSummary?.visitorTeamId}"] ?: "",
                        index = 1,
                        modifier = Modifier
                            .weight(1f)
                    )
                }


                HCapsuleBar(
                    modifier = Modifier.offset(x = barOffset),
                    size = HCapsuleBarSize.MEDIUM
                )
            }

            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.weight(0.4f)
            ) {
                // refresh button
                Box(
                    Modifier
                        .padding(end = 4.dp)
                        .alpha(0.6f)
                        .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(10.dp))
                        .padding(2.dp)
                        .clickable {
                            searchViewModel.send(SearchViewModel.Intent.RefreshGame(category = "basketball"))
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_refresh_24),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = "날짜: ${CalendarUtil.formatDate(displayModel.game.gameSummary?.date).split(" ")[0]}",
                        fontSize = 12.sp,
                    )

                    Text(
                        text = CalendarUtil.formatDate(displayModel.game.gameSummary?.date, TimeFormatType.AMPM),
                        fontSize = 12.sp
                    )

                    Text(
                        text = "장소: ${nbaGameStatsViewModel.teamNameDictionary["venue_${displayModel.game.gameSummary?.homeTeamId}"]}" ?: "",
                        fontSize = 12.sp,
                    )

                    Text(
                        text = "관중수: ${displayModel.game.gameInfo?.attendance ?: 0}",
                        fontSize = 12.sp,
                    )

                    Text(
                        text = "심판:",
                        fontSize = 12.sp,
                    )

                    for ((index, value) in displayModel.game.officials.withIndex()) {
                        Text(
                            text = "• ${value.firstName + value.lastName}",
                            fontSize = 12.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

// TODO: Make it Component
@Composable
fun NBAGameStatsTeamButton(
    nbaGameStatsViewModel: NBAGameStatsViewModel = hiltViewModel(),
    team: String,
    index: Int,
    modifier: Modifier
) {
    Text(
        text = team,
        textAlign = TextAlign.Center,
        maxLines = 2,
        modifier = modifier
            .clickable {
                nbaGameStatsViewModel.send(NBAGameStatsViewModel.Intent.SelectTeam(index))
            }
//            .width(nbaGameStatsViewModel.teamButtonWidth)
    )
}

@Composable
fun NBAGameStatsFirstCategoryItem(
    nbaGameStatsViewModel: NBAGameStatsViewModel = hiltViewModel()
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(nbaGameStatsViewModel.firstCategoryItemHeight + nbaGameStatsViewModel.secondCategoryItemHeight)
    ) {
        Text(
            text = StringConstants.gameStatsFirstCategory,
            fontSize = nbaGameStatsViewModel.firstCategoryFontSize,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(130.dp)
        )

        VCapsuleBar(modifier = Modifier.alpha(0.5f))
    }
}

@Composable
fun NBAGameStatsFirstCategoryList(
    nbaGameStatsViewModel: NBAGameStatsViewModel = hiltViewModel()
) {
    /* ---------------------
       constants
       --------------------- */
    val attackCategoriesSize = StringConstants.NBA.gameStatsAttackCategories.size
    val defendCategoriesSize = StringConstants.NBA.gameStatsDefendCategories.size
    val commonCategoriesSize = StringConstants.NBA.gameStatsCommonCategories.size

    /* ---------------------
       viewmodel state
       --------------------- */
    val selectedIndex by nbaGameStatsViewModel.firstSelectedIndex.collectAsState()

    val itemWidth = nbaGameStatsViewModel.itemWidth
    val barWidth = nbaGameStatsViewModel.barWidth

    /* ---------------------
       animation
       --------------------- */
    val barOffset by animateDpAsState(
        targetValue = if (selectedIndex == 0) {
            getOffsetOfAniCapsuleBar(
                itemWidth = itemWidth * attackCategoriesSize,
                barWidth = 80.dp
            )
        } else if (selectedIndex == 1) {
            (itemWidth * attackCategoriesSize) + barWidth + getOffsetOfAniCapsuleBar(
                itemWidth = itemWidth * defendCategoriesSize,
                barWidth = 80.dp
            )
        } else {
            (itemWidth * attackCategoriesSize) + (barWidth * 2) + (itemWidth * defendCategoriesSize) + getOffsetOfAniCapsuleBar(
                itemWidth = itemWidth * commonCategoriesSize,
                barWidth = 80.dp
            )
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(nbaGameStatsViewModel.firstCategoryItemHeight - 2.dp)
    ) {
        for ((index, value) in StringConstants.statsFirstCategories.withIndex()) {
            NBAGameStatsFirstCategoryListItem(
                category = value,
                index = index
            )

            if (index != StringConstants.statsFirstCategories.size - 1) {
                VCapsuleBar(modifier = Modifier.alpha(0.5f))
            }
        }
    }

    HCapsuleBar(
        modifier = Modifier
            .offset(x = barOffset),
        size = HCapsuleBarSize.LARGE
    )
}

@Composable
fun NBAGameStatsFirstCategoryListItem(
    nbaGameStatsViewModel: NBAGameStatsViewModel = hiltViewModel(),
    category: String,
    index: Int
) {
    val itemWidth = nbaGameStatsViewModel.itemWidth

    Text(
        text = category,
        fontSize = nbaGameStatsViewModel.firstCategoryFontSize,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .width(
                if (index == 0) {
                    (itemWidth * StringConstants.NBA.gameStatsAttackCategories.size)
                } else if (index == 1) {
                    (itemWidth * StringConstants.NBA.gameStatsDefendCategories.size)
                } else {
                    (itemWidth * StringConstants.NBA.gameStatsCommonCategories.size)
                }
            )
            .clickable {
                nbaGameStatsViewModel.send(
                    NBAGameStatsViewModel.Intent.SelectFirstCategory(index)
                )
            }
    )
}

@Composable
fun NBAGameStatsSecondCategoryList(
    nbaGameStatsViewModel: NBAGameStatsViewModel = hiltViewModel()
) {
    /* ---------------------
       constants
       --------------------- */
    val attackCategoriesSize = StringConstants.NBA.gameStatsAttackCategories.size
    val defendCategoriesSize = StringConstants.NBA.gameStatsDefendCategories.size

    /* ---------------------
       viewmodel state
       --------------------- */
    val selectedIndex by nbaGameStatsViewModel.secondSelectedIndex.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val barOffset by animateDpAsState(
        targetValue = if (selectedIndex in 0 until attackCategoriesSize) {
            getOffsetOfAniCapsuleBar(itemWidth = nbaGameStatsViewModel.itemWidth, index = selectedIndex)
        } else if (selectedIndex in attackCategoriesSize until attackCategoriesSize + defendCategoriesSize) {
            getOffsetOfAniCapsuleBar(itemWidth = nbaGameStatsViewModel.itemWidth, index = selectedIndex) + nbaGameStatsViewModel.barWidth
        } else {
            getOffsetOfAniCapsuleBar(itemWidth = nbaGameStatsViewModel.itemWidth, index = selectedIndex) + (nbaGameStatsViewModel.barWidth * 2)
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(nbaGameStatsViewModel.secondCategoryItemHeight - 2.dp)
    ) {
        for ((index, value) in StringConstants.NBA.gameStatsSecondCategories.withIndex()) {
            NBAGameStatsSecondCategoryListItem(
                category = value,
                index = index
            )

            if (index == attackCategoriesSize - 1 || index == (attackCategoriesSize + defendCategoriesSize - 1)) {
                VCapsuleBar(modifier = Modifier.alpha(0.5f))
            }
        }
    }

    HCapsuleBar(
        modifier = Modifier
            .offset(x = barOffset)
    )
}

@Composable
fun NBAGameStatsSecondCategoryListItem(
    nbaGameStatsViewModel: NBAGameStatsViewModel = hiltViewModel(),
    category: String,
    index: Int
) {
    Text(
        text = category,
        textAlign = TextAlign.Center,
        fontSize = nbaGameStatsViewModel.secondCategoryFontSize,
        fontWeight = FontWeight.Medium,
        maxLines = 2,
        modifier = Modifier
            .width(nbaGameStatsViewModel.itemWidth)
            .clickable {
                nbaGameStatsViewModel.send(NBAGameStatsViewModel.Intent.SelectSecondCategory(index))
            }
    )
}

@Composable
fun NBAGameStatsFirstDataList(
    nbaGameStatsViewModel: NBAGameStatsViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val playerStats by nbaGameStatsViewModel.playerStats.collectAsState()

    Column {
        for (item in playerStats) {
            NBAGameStatsFirstDataListItem(data = item)
        }

        // team total stats
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .width(132.dp)
                .height(nbaGameStatsViewModel.dataItemHeight)
        ) {
            Text(
                text = "합계(팀 기록)",
                fontSize = 12.sp,
                maxLines = 2,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            VCapsuleBar(modifier = Modifier.alpha(0.5f))
        }
    }
}

@Composable
fun NBAGameStatsFirstDataListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaGameStatsViewModel: NBAGameStatsViewModel = hiltViewModel(),
    data: NBABoxScoreTeamPlayer
) {
    val playerKrName = nbaGameStatsViewModel.playerNameDictionary[(data.firstName + " " + data.familyName).lowercase()]
        ?: (data.firstName + " " + data.familyName)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(132.dp)
            .padding(start = 8.dp)
            .height(nbaGameStatsViewModel.dataItemHeight)
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
            url = NBAUtil.playerPhotoUrl(data.personId),
            customSize = 25.dp,
            modifier = Modifier.padding(end = 4.dp)
        )

        Text(
            text = playerKrName,
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
        ) {
            Text(
                text = if (data.position.isNotBlank()) "선발" else "후보",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier
                    .alpha(if (data.position.isNotBlank()) 1f else 0.7f)
            )

            Text(
                text = data.position,
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier
                    .alpha(0.7f)
            )
        }

        Spacer(Modifier.weight(1f))

        VCapsuleBar(modifier = Modifier.alpha(0.5f))
    }
}

@Composable
fun NBAGameStatsDataList(
    nbaGameStatsViewModel: NBAGameStatsViewModel = hiltViewModel()
) {
    /* ---------------------
       constants
       --------------------- */
    val attackCategoriesSize = StringConstants.NBA.gameStatsAttackCategories.size
    val defendCategoriesSize = StringConstants.NBA.gameStatsDefendCategories.size

    /* ---------------------
       viewmodel state
       --------------------- */
    val playerStats by nbaGameStatsViewModel.playerStats.collectAsState()
    val playersTotalStats by nbaGameStatsViewModel.playersTotalStats.collectAsState()

    Column {
        for (item in playerStats) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(nbaGameStatsViewModel.dataItemHeight)
            ) {
                for (index in 0 until StringConstants.NBA.gameStatsSecondCategories.size) {
                    NBAGameStatsDataListItem(
                        data = item.statistics,
                        index = index
                    )

                    if (index == attackCategoriesSize - 1 || index == (attackCategoriesSize + defendCategoriesSize)) {
                        VCapsuleBar(modifier = Modifier.alpha(0f))
                    }
                }
            }
        }

        // team total stats
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(nbaGameStatsViewModel.dataItemHeight)
        ) {
            for (index in 0 until StringConstants.NBA.gameStatsSecondCategories.size) {
                playersTotalStats?.let {
                    NBAGameStatsDataListItem(
                        data = it,
                        index = index,
                        isTotalStats = true
                    )
                }

                if (index == attackCategoriesSize - 1 || index == (attackCategoriesSize + defendCategoriesSize)) {
                    VCapsuleBar(modifier = Modifier.alpha(0f))
                }
            }
        }
    }
}

@Composable
fun NBAGameStatsDataListItem(
    nbaGameStatsViewModel: NBAGameStatsViewModel = hiltViewModel(),
    data: NBAGameBoxScoreStats,
    index: Int,
    isTotalStats: Boolean = false
) {
    val intDataText = when (index) {
        0 -> "${data.points}"
        1 -> "${data.assists}"
        2 -> "${data.reboundsOffensive}"
        3 -> "${data.fieldGoalsAttempted}"
        4 -> "${data.fieldGoalsMade}"
        5 -> "${data.fieldGoalsPercentage}"
        6 ->  "${data.threePointersAttempted})"
        7 -> "${data.threePointersMade}"
        8 -> "${data.threePointersPercentage}"
        9 -> "${data.freeThrowsAttempted})"
        10 -> "${data.freeThrowsMade}"
        11 -> "${data.freeThrowsPercentage}"
        12 -> "${data.reboundsDefensive}"
        13 -> "${data.blocks}"
        14 -> "${data.steals}"
        15 -> "${data.reboundsTotal}"
        16 -> "${data.turnovers}"
        17 -> "${data.foulsPersonal}"
        18 -> "${data.plusMinusPoints}"
        19 -> if (isTotalStats) "" else data.minutes
        else -> ""
    }

//    val fontSize = when (index) {
//        6, 9 -> 11.sp
//        else -> nbaGameStatsViewModel.dataFontSize
//    }

    Text(
        text = intDataText,
        textAlign = TextAlign.Center,
        fontSize = nbaGameStatsViewModel.dataFontSize,
        maxLines = 2,
        modifier = Modifier
            .width(nbaGameStatsViewModel.itemWidth)
    )
}
















