package com.moare.android.features.search.display.football.view

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
import androidx.compose.ui.layout.approachLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.R
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.constants.UIConstants
import com.moare.android.core.util.EnNameTranslationUtils
import com.moare.android.core.util.MatchDescriptionConverter
import com.moare.android.core.util.TranslationType
import com.moare.android.core.util.percentageOf
import com.moare.android.features.search.display.football.viewmodel.FBGameStatsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.models.football.FBGamePlayerStats
import com.moare.android.features.search.models.models.football.FBGamePlayerStatsDetail
import com.moare.android.features.search.models.models.football.FBGamePlayerStatsGames
import com.moare.android.features.search.models.models.football.FBPerson
import com.moare.android.features.search.models.models.football.FBPlayerStatsCards
import com.moare.android.features.search.models.models.football.FBPlayerStatsDribbles
import com.moare.android.features.search.models.models.football.FBPlayerStatsDuels
import com.moare.android.features.search.models.models.football.FBPlayerStatsFouls
import com.moare.android.features.search.models.models.football.FBPlayerStatsGoals
import com.moare.android.features.search.models.models.football.FBPlayerStatsPasses
import com.moare.android.features.search.models.models.football.FBPlayerStatsPenalty
import com.moare.android.features.search.models.models.football.FBPlayerStatsShots
import com.moare.android.features.search.models.models.football.FBPlayerStatsTackles
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.HCapsuleBarSize
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.util.convertDpToPx
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar

@Composable
fun FBGameStatsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbGameStatsViewModel: FBGameStatsViewModel = hiltViewModel(),
    data: FBGameStatsDisplayModel
) {
    /* ---------------------
       constants
       --------------------- */

    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()
    var coachKrName by remember { mutableStateOf("") }

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by fbGameStatsViewModel.displayModel.collectAsState()
    val coach by fbGameStatsViewModel.coach.collectAsState()
    val firstSelectedIndex by fbGameStatsViewModel.firstSelectedIndex.collectAsState()
    val secondSelectedIndex by fbGameStatsViewModel.secondSelectedIndex.collectAsState()

    val fbLeagueScheduleData by searchViewModel.fbLeagueScheduleData.collectAsState()
    val fbTeamScheduleData by searchViewModel.fbTeamScheduleData.collectAsState()
    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       etc
       --------------------- */
    val secondSelectedCategoryPosition = with(LocalDensity.current) {
        val attackCategoriesSize = StringConstants.Football.gameStatsAttackCategories.size
        val defendCategoriesSize = StringConstants.Football.gameStatsDefendCategories.size

        if (secondSelectedIndex in 0 until attackCategoriesSize) {
            (fbGameStatsViewModel.itemWidth * secondSelectedIndex).toPx()
        } else if (secondSelectedIndex in attackCategoriesSize until attackCategoriesSize + defendCategoriesSize) {
            ((fbGameStatsViewModel.itemWidth * secondSelectedIndex) + fbGameStatsViewModel.barWidth).toPx()
        } else {
            ((fbGameStatsViewModel.itemWidth * secondSelectedIndex) + (fbGameStatsViewModel.barWidth * 2)).toPx()
        }
    }.toInt()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.FBGameStats) {
            fbGameStatsViewModel.send(FBGameStatsViewModel.Intent.InitData(data))
        }
    }

    LaunchedEffect(coach) {
        coachKrName = EnNameTranslationUtils.translateByAWS(coach?.name)
    }

    LaunchedEffect(Unit) {
        searchViewModel.send(SearchViewModel.Intent.RefreshGame(category = "football"))
    }

    // scroll to category that matches with the keyword,
    // and when first category list's item is selected by click
    LaunchedEffect(firstSelectedIndex) {
        if (fbGameStatsViewModel.shouldScrollCategory) {
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
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        /* ---------------------
           game title, info
           - hides when game selected by schedule
           --------------------- */
        if (fbLeagueScheduleData == null && fbTeamScheduleData == null) {
            displayModel?.game?.let { game ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LeagueTitle(
                        url = game.league.logo,
                        leagueName = game.league.name,
                        leagueSeason = game.league.season
                    )

                    Text(
                        text = " - " + MatchDescriptionConverter.convert(descriptionType = MatchDescriptionConverter.DescriptionType.ROUND_WITHOUT_DASH, input = game.league.round),
                        fontSize = 14.sp
                    )
                }

                FBLeagueScheduleListItem(data = game)
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .clip(RoundedCornerShape(10.dp))
                .padding(horizontal = UIConstants.Padding.defaultHPadding)
                .background(MaterialTheme.colors.primary)
        )

        if (displayModel?.game?.fixture?.status?.short != "NS") {
            /* ---------------------
               team select button
               --------------------- */
            FBGameStatsTeamButtonContainer()

            /* ---------------------
               coach
               --------------------- */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = "감독: ",
                    fontSize = 15.sp
                )

                URLImage(
                    url = coach?.photo,
                    customSize = 23.dp
                )

                Text(
                    text = coachKrName,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            /* ---------------------
               players stats
               --------------------- */
            Box {
                // category
                Row {
                    FBGameStatsFirstCategoryItem()

                    Row(
                        Modifier.horizontalScroll(horizontalScrollState)
                    ) {
                        Column {
                            FBGameStatsFirstCategoryList()
                            FBGameStatsSecondCategoryList()
                        }
                    }
                }

                // stats data
                Column(
                    modifier = Modifier
                        .padding(top = fbGameStatsViewModel.categoryItemHeight * 2)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row {
                        FBGameStatsFirstDataList()

                        Row(
                            Modifier.horizontalScroll(horizontalScrollState)
                        ) {
                            FBGameStatsDataList()
                        }
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
fun FBGameStatsTeamButtonContainer(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbGameStatsViewModel: FBGameStatsViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by fbGameStatsViewModel.displayModel.collectAsState()
    val selectedIndex by fbGameStatsViewModel.selectedTeamIndex.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val barOffset by animateDpAsState(
        targetValue = if (selectedIndex == 0) {
            getOffsetOfAniCapsuleBar(itemWidth = fbGameStatsViewModel.teamButtonWidth, barWidth = 50.dp)
        } else {
            2.dp + getOffsetOfAniCapsuleBar(itemWidth = fbGameStatsViewModel.teamButtonWidth, barWidth = 50.dp, index = selectedIndex)
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    displayModel?.let {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(50.dp)
                ) {
                    // home
                    FBGameStatsTeamButton(team = EnNameTranslationUtils.translateByDic(TranslationType.TEAM, input = it.game.teams.home.name), index = 0)

                    VCapsuleBar(modifier = Modifier.alpha(0.5f))

                    // away
                    FBGameStatsTeamButton(team = EnNameTranslationUtils.translateByDic(TranslationType.TEAM, input = it.game.teams.away.name), index = 1)
                }


                HCapsuleBar(
                    modifier = Modifier.offset(x = barOffset),
                    size = HCapsuleBarSize.MEDIUM
                )
            }

            // refresh button
            Row {
                Spacer(Modifier.weight(1f))

                Box(
                    Modifier
                        .padding(end = UIConstants.Padding.defaultHPadding)
                        .alpha(0.6f)
                        .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(10.dp))
                        .padding(2.dp)
                        .clickable {
                            searchViewModel.send(SearchViewModel.Intent.RefreshGame(category = "football"))
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_refresh_24),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }

}

@Composable
fun FBGameStatsTeamButton(
    fbGameStatsViewModel: FBGameStatsViewModel = hiltViewModel(),
    team: String,
    index: Int
) {
    Text(
        text = team,
        textAlign = TextAlign.Center,
        maxLines = 2,
        modifier = Modifier
            .clickable {
                fbGameStatsViewModel.send(FBGameStatsViewModel.Intent.SelectTeam(index))
            }
            .width(fbGameStatsViewModel.teamButtonWidth)
    )
}

@Composable
fun FBGameStatsFirstCategoryItem(
    fbGameStatsViewModel: FBGameStatsViewModel = hiltViewModel()
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(fbGameStatsViewModel.categoryItemHeight * 2)
    ) {
        Text(
            text = StringConstants.Football.gameStatsFirstCategory,
            fontSize = fbGameStatsViewModel.categoryFontSize,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(130.dp)
        )

        VCapsuleBar(modifier = Modifier.alpha(0.5f))
    }
}

@Composable
fun FBGameStatsFirstCategoryList(
    fbGameStatsViewModel: FBGameStatsViewModel = hiltViewModel()
) {
    /* ---------------------
       constants
       --------------------- */
    val attackCategoriesSize = StringConstants.Football.gameStatsAttackCategories.size
    val defendCategoriesSize = StringConstants.Football.gameStatsDefendCategories.size
    val etcCategoriesSize = StringConstants.Football.gameStatsEtcCategories.size

    /* ---------------------
       viewmodel state
       --------------------- */
    val selectedIndex by fbGameStatsViewModel.firstSelectedIndex.collectAsState()

    val itemWidth = fbGameStatsViewModel.itemWidth
    val barWidth = fbGameStatsViewModel.barWidth

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
                itemWidth = itemWidth * etcCategoriesSize,
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
            .height(fbGameStatsViewModel.categoryItemHeight - 2.dp)
    ) {
        for ((index, value) in StringConstants.Football.statsFirstCategories.withIndex()) {
            FBGameStatsFirstCategoryListItem(
                category = value,
                index = index
            )

            if (index != StringConstants.Football.statsFirstCategories.size - 1) {
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
fun FBGameStatsFirstCategoryListItem(
    fbGameStatsViewModel: FBGameStatsViewModel = hiltViewModel(),
    category: String,
    index: Int
) {
    val itemWidth = fbGameStatsViewModel.itemWidth

    Text(
        text = category,
        fontSize = fbGameStatsViewModel.categoryFontSize,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .width(
                if (index == 0) {
                    (itemWidth * StringConstants.Football.gameStatsAttackCategories.size)
                } else if (index == 1) {
                    (itemWidth * StringConstants.Football.gameStatsDefendCategories.size)
                } else {
                    (itemWidth * StringConstants.Football.gameStatsEtcCategories.size)
                }
            )
            .clickable {
                fbGameStatsViewModel.send(
                    FBGameStatsViewModel.Intent.SelectFirstCategory(index)
                )
            }
    )
}

// parent component: Column
@Composable
fun FBGameStatsSecondCategoryList(
    fbGameStatsViewModel: FBGameStatsViewModel = hiltViewModel()
) {
    /* ---------------------
       constants
       --------------------- */
    val attackCategoriesSize = StringConstants.Football.gameStatsAttackCategories.size
    val defendCategoriesSize = StringConstants.Football.gameStatsDefendCategories.size

    /* ---------------------
       viewmodel state
       --------------------- */
    val selectedIndex by fbGameStatsViewModel.secondSelectedIndex.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val barOffset by animateDpAsState(
        targetValue = if (selectedIndex in 0 until attackCategoriesSize) {
            getOffsetOfAniCapsuleBar(itemWidth = fbGameStatsViewModel.itemWidth, index = selectedIndex)
        } else if (selectedIndex in attackCategoriesSize until attackCategoriesSize + defendCategoriesSize) {
            getOffsetOfAniCapsuleBar(itemWidth = fbGameStatsViewModel.itemWidth, index = selectedIndex) + fbGameStatsViewModel.barWidth
        } else {
            getOffsetOfAniCapsuleBar(itemWidth = fbGameStatsViewModel.itemWidth, index = selectedIndex) + (fbGameStatsViewModel.barWidth * 2)
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(fbGameStatsViewModel.categoryItemHeight - 2.dp)
    ) {
        for ((index, value) in StringConstants.Football.gameStatsSecondCategories.withIndex()) {
            FBGameStatsSecondCategoryListItem(
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
fun FBGameStatsSecondCategoryListItem(
    fbGameStatsViewModel: FBGameStatsViewModel = hiltViewModel(),
    category: String,
    index: Int
) {
    val fontSize = when (index) {
        6, 9 -> 11.sp
        16 -> 12.sp
        else -> fbGameStatsViewModel.dataFontSize
    }

    Text(
        text = category,
        textAlign = TextAlign.Center,
        fontSize = fontSize,
        fontWeight = FontWeight.Medium,
        maxLines = 2,
        modifier = Modifier
            .width(fbGameStatsViewModel.itemWidth)
            .clickable {
                fbGameStatsViewModel.send(FBGameStatsViewModel.Intent.SelectSecondCategory(index))
            }
    )
}

@Composable
fun FBGameStatsFirstDataList(
    fbGameStatsViewModel: FBGameStatsViewModel = hiltViewModel()
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val playerStats by fbGameStatsViewModel.playerStats.collectAsState()

    Column {
        for (value in playerStats) {
            FBGameStatsFirstDataListItem(data = value.player)
        }

        // team total stats
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .width(132.dp)
                .height(fbGameStatsViewModel.dataItemHeight)
        ) {
            Text(
                text = "팀 총합", // 팀 기록?
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
fun FBGameStatsFirstDataListItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbGameStatsViewModel: FBGameStatsViewModel = hiltViewModel(),
    data: FBPerson
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val lineups by fbGameStatsViewModel.lineups.collectAsState()

    /* ---------------------
       ui state
       --------------------- */
    var playerKrName by remember { mutableStateOf("") }
    var isStarter by remember { mutableStateOf(false) }
    var position by remember { mutableStateOf("") }

    LaunchedEffect(data) {
        playerKrName = EnNameTranslationUtils.translateByAWS(data.name)

        lineups?.let {
            // starter
            for (player in it.startXI) {
                if (data.id == player.player.id) {
                    isStarter = true
                    position = player.player.pos
                    return@LaunchedEffect
                }
            }

            // substitute
            for (player in it.substitutes) {
                if (data.id == player.player.id) {
                    isStarter = false
                    position = player.player.pos
                    return@LaunchedEffect
                }
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(132.dp)
            .padding(start = 8.dp)
            .height(fbGameStatsViewModel.dataItemHeight)
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
            url = data.photo,
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
                text = if (isStarter) "선발" else "후보",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier
                    .alpha(if (isStarter) 1f else 0.7f)
            )

            Text(
                text = position,
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
fun FBGameStatsDataList(
    fbGameStatsViewModel: FBGameStatsViewModel = hiltViewModel()
) {
    /* ---------------------
       constants
       --------------------- */
    val attackCategoriesSize = StringConstants.Football.gameStatsAttackCategories.size
    val defendCategoriesSize = StringConstants.Football.gameStatsDefendCategories.size

    /* ---------------------
       viewmodel state
       --------------------- */
    val playerStats by fbGameStatsViewModel.playerStats.collectAsState()
    val playersTotalStats by fbGameStatsViewModel.playersTotalStats.collectAsState()

    Column {
        for (value in playerStats) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(fbGameStatsViewModel.dataItemHeight)
            ) {
                for (index in 0 until StringConstants.Football.gameStatsSecondCategories.size) {
                    value.statistics.first().let {
                        FBGameStatsDataListItem(
                            data = it,
                            index = index
                        )
                    }

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
                .height(fbGameStatsViewModel.dataItemHeight)
        ) {
            for (index in 0 until StringConstants.Football.gameStatsSecondCategories.size) {
                playersTotalStats?.let {
                    FBGameStatsDataListItem(
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
fun FBGameStatsDataListItem(
    fbGameStatsViewModel: FBGameStatsViewModel = hiltViewModel(),
    data: FBGamePlayerStatsDetail,
    index: Int,
    isTotalStats: Boolean = false
) {
    val intDataText = when (index) {
        0 -> "${data.goals.total}"
        1 -> "${data.penalty.scored}"
        2 -> "${data.goals.assists}"
        3 -> "${data.shots.total}"
        4 -> "${data.shots.on}"
        5 -> "${data.passes.key}"
        6 ->  "${data.dribbles.success}/${data.dribbles.attempts}(${data.dribbles.success.percentageOf(data.dribbles.attempts, 1)}%)"
        7 -> "${data.offsides}"
        8 -> "${data.tackles.total}"
        9 -> "${data.duels.won}/${data.duels.total}(${data.duels.won.percentageOf(data.duels.total, 1)}%)"
        10 -> "${data.tackles.interceptions}"
        11 -> "${data.passes.total}"
        12 -> "${data.fouls.drawn}"
        13 -> "${data.fouls.committed}"
        14 -> "${data.cards.yellow}"
        15 -> "${data.cards.red}"
        16 ->  if (isTotalStats) "" else "${data.games.minutes}"
        17 -> if (isTotalStats) "" else data.games.rating
        else -> ""
    }

    val fontSize = when (index) {
        6, 9 -> 11.sp
        else -> fbGameStatsViewModel.dataFontSize
    }

    Text(
        text = intDataText,
        textAlign = TextAlign.Center,
        fontSize = fontSize,
        maxLines = 2,
        modifier = Modifier
            .width(fbGameStatsViewModel.itemWidth)
    )
}




















