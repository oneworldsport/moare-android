package com.moare.android.features.search.display.football.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.R
import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.constants.UIConstants
import com.moare.android.core.util.FBUtil
import com.moare.android.core.util.MatchDescriptionConverter
import com.moare.android.core.util.percentageOf
import com.moare.android.core.util.rounded
import com.moare.android.features.search.display.common.container.state.GameStatsCoachState
import com.moare.android.features.search.display.common.container.state.GameStatsContainerActions
import com.moare.android.features.search.display.common.container.state.GameStatsContainerState
import com.moare.android.features.search.display.common.container.state.GameStatsTeamState
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.GameStatsViewContainer
import com.moare.android.features.search.display.football.viewmodel.FBGameStatsIntent
import com.moare.android.features.search.display.football.viewmodel.FBGameStatsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamScheduleDisplayModel
import com.moare.android.features.search.models.models.football.FBGamePlayerStatsDetail
import com.moare.android.features.search.models.models.football.FBPerson
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.HCapsuleBarSize
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.VCapsuleBar
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

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by fbGameStatsViewModel.displayModel.collectAsState()
    val coach by fbGameStatsViewModel.coach.collectAsState()
    val firstSelectedIndex by fbGameStatsViewModel.firstCategorySelectedIndex.collectAsState()
    val secondCategorySelectedIndex by fbGameStatsViewModel.secondCategorySelectedIndex.collectAsState()
    val selectedTeamIndex by fbGameStatsViewModel.selectedTeamIndex.collectAsState()
    val playerStats by fbGameStatsViewModel.playerStats.collectAsState()
    val lineups by fbGameStatsViewModel.lineups.collectAsState()
    val teamNameDic = fbGameStatsViewModel.teamNameDictionary
    val playerNameDic = fbGameStatsViewModel.playerNameDictionary

    val displayModels by searchViewModel.displayModels.collectAsState()
    val poppedView by searchViewModel.poppedView.collectAsState()
    val fbLeagueScheduleModel = displayModels[SportDisplayType.FB_LEAGUE_SCHEDULE] as? FBLeagueScheduleDisplayModel
    val fbTeamScheduleModel = displayModels[SportDisplayType.FB_TEAM_SCHEDULE] as? FBTeamScheduleDisplayModel

    val teamIds = listOf(displayModel?.game?.teams?.home?.id, displayModel?.game?.teams?.away?.id)
    val teamCategories = teamIds.map {
        GameStatsTeamState(
            name = teamNameDic["short_${it}"] ?: "",
            imageUrl = FBUtil.teamLogoUrl(it)
        )
    }

    val playerList = playerStats.mapNotNull { player ->
        val stats = player.statistics.firstOrNull()
        val playerId = player.player.id

        var isStarter = false
        var position = ""

        lineups?.let {
            for (item in it.startXI) {
                if (playerId == item.player.id) {
                    isStarter = true
                    position = item.player.pos
                    return@let
                }
            }

            for (item in it.substitutes) {
                if (playerId == item.player.id) {
                    isStarter = false
                    position = item.player.pos
                    return@let
                }
            }
        }

        stats?.let { stats ->
            StandingsItemState(
                id = playerId,
                isGameStats = true,
                imageUrl = player.player.photo,
                name = playerNameDic["${playerId}"] ?: player.player.name,
                extraInfo = if (isStarter) "선발" else "후보",
                extraSubInfo = position,
                dataList = listOf(
                    stats.goals.total.toString(),
                    stats.penalty.scored.toString(),
                    stats.goals.assists.toString(),
                    stats.shots.total.toString(),
                    stats.shots.on.toString(),
                    stats.passes.key.toString(),
                    "${stats.dribbles.success}/${stats.dribbles.attempts}(${stats.dribbles.success.percentageOf(stats.dribbles.attempts, 1)}%)",
                    stats.offsides.toString(),
                    stats.tackles.total.toString(),
                    "${stats.duels.won}/${stats.duels.total}(${stats.duels.won.percentageOf(stats.duels.total, 1)}%)",
                    stats.tackles.interceptions.toString(),
                    stats.passes.total.toString(),
                    stats.fouls.drawn.toString(),
                    stats.fouls.committed.toString(),
                    stats.cards.yellow.toString(),
                    stats.cards.red.toString(),
                    stats.games.minutes.toString(),
                    stats.games.rating
                )
            )
        }
    }
    val columnWidthList = listOf(50.dp, 50.dp, 50.dp, 50.dp, 60.dp, 50.dp, 80.dp, 70.dp, 70.dp, 80.dp, 60.dp, 60.dp, 60.dp, 50.dp, 50.dp, 50.dp, 80.dp, 50.dp)
    val gameDetailInfo = "심판: ${displayModel?.game?.fixture?.referee}"

    /* ---------------------
       etc
       --------------------- */
    val secondSelectedCategoryPosition = with(LocalDensity.current) {
        val attackCategoriesSize = StringConstants.Football.GAME_STATS_ATTACK_CATEGORIES.size
        val defendCategoriesSize = StringConstants.Football.GAME_STATS_DEFEND_CATEGORIES.size

        if (secondCategorySelectedIndex in 0 until attackCategoriesSize) {
            (fbGameStatsViewModel.itemWidth * secondCategorySelectedIndex).toPx()
        } else if (secondCategorySelectedIndex in attackCategoriesSize until attackCategoriesSize + defendCategoriesSize) {
            ((fbGameStatsViewModel.itemWidth * secondCategorySelectedIndex) + fbGameStatsViewModel.barWidth).toPx()
        } else {
            ((fbGameStatsViewModel.itemWidth * secondCategorySelectedIndex) + (fbGameStatsViewModel.barWidth * 2)).toPx()
        }
    }.toInt()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.FBGameStats) {
            fbGameStatsViewModel.send(FBGameStatsIntent.InitData(data))
        }
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

    GameStatsViewContainer(
        state = GameStatsContainerState(
            shouldShowTitle = fbLeagueScheduleModel == null && fbTeamScheduleModel == null,
            shouldShowGameItem = fbLeagueScheduleModel == null && fbTeamScheduleModel == null,
            shouldShowStats = displayModel?.game?.fixture?.status?.short != "NS",
            shouldShowCoach = true,
            teamCategories = teamCategories,
            secondCategories = StringConstants.Football.GAME_STATS_SECOND_CATEGORIES,
            coachState = GameStatsCoachState(
                name = coach?.name,
                imageUrl = coach?.photo
            ),
            teamCategorySelectedIndex = selectedTeamIndex,
            secondCategorySelectedIndex = secondCategorySelectedIndex,
            columnWidthList = columnWidthList,
            playerList = playerList,
            gameDetailInfo = gameDetailInfo
        ),
        actions = GameStatsContainerActions(
            teamCategoryButtonAction = { index ->
                fbGameStatsViewModel.send(FBGameStatsIntent.SelectTeam(index))
            },
            secondCategoryButtonAction = { index ->
                fbGameStatsViewModel.send(FBGameStatsIntent.SelectSecondCategory(index))
            },
            refreshButtonAction = {
                searchViewModel.send(SearchViewModel.Intent.RefreshGame(category = "football"))
            }
        ),
        titleContent = {
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
            }
        },
        gameContent = {
            displayModel?.game?.let { game ->
                FBLeagueScheduleListItem(
                    data = ModelConverter().fbGameToGameScheduleConverter(game),
                    teamNameDic = teamNameDic
                )
            }
        }
    )
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
                    FBGameStatsTeamButton(
                        team = fbGameStatsViewModel.teamNameDictionary["short_${it.game.teams.home.id}"] ?: it.game.teams.home.name,
                        index = 0
                    )

                    VCapsuleBar(modifier = Modifier.alpha(0.5f))

                    // away
                    FBGameStatsTeamButton(
                        team = fbGameStatsViewModel.teamNameDictionary["short_${it.game.teams.away.id}"] ?: it.game.teams.away.name,
                        index = 1
                    )
                }


                HCapsuleBar(
                    modifier = Modifier.offset(x = barOffset),
                    size = HCapsuleBarSize.MEDIUM
                )
            }

            // refresh button
            if (it.game.fixture.status.short != Constants.FBGameStatus.NOT_STARTED &&
                it.game.fixture.status.short != Constants.FBGameStatus.FINISHED) {
                Row {
                    Spacer(Modifier.weight(1f))

                    // TODO: Make it component
                    Box(
                        Modifier
                            .padding(end = UIConstants.Padding.DEFAULT_H_PADDING)
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
                fbGameStatsViewModel.send(FBGameStatsIntent.SelectTeam(index))
            }
            .width(fbGameStatsViewModel.teamButtonWidth)
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
    var isStarter by remember { mutableStateOf(false) }
    var position by remember { mutableStateOf("") }

    LaunchedEffect(data) {
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
            text = fbGameStatsViewModel.playerNameDictionary["${data.id}"] ?: data.name,
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
    val attackCategoriesSize = StringConstants.Football.GAME_STATS_ATTACK_CATEGORIES.size
    val defendCategoriesSize = StringConstants.Football.GAME_STATS_DEFEND_CATEGORIES.size

    /* ---------------------
       viewmodel state
       --------------------- */
    val playerStats by fbGameStatsViewModel.playerStats.collectAsState()
    val playersTotalStats by fbGameStatsViewModel.playersTotalStats.collectAsState()

    Column {
        for (item in playerStats) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(fbGameStatsViewModel.dataItemHeight)
            ) {
                for (index in 0 until StringConstants.Football.GAME_STATS_SECOND_CATEGORIES.size) {
                    item.statistics.first().let {
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
            for (index in 0 until StringConstants.Football.GAME_STATS_SECOND_CATEGORIES.size) {
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




















