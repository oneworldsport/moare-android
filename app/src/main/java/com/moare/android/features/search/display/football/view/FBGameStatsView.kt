package com.moare.android.features.search.display.football.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.FBUtil
import com.moare.android.core.util.MatchDescriptionConverter
import com.moare.android.core.util.percentageOf
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
import com.moare.android.ui.common.components.LeagueTitle

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
    val playersTotalStats by fbGameStatsViewModel.playersTotalStats.collectAsState()
    val teamNameDic = fbGameStatsViewModel.teamNameDictionary
    val playerNameDic = fbGameStatsViewModel.playerNameDictionary

    val displayModels by searchViewModel.displayModels.collectAsState()
    val poppedView by searchViewModel.poppedView.collectAsState()
    val fbLeagueScheduleModel = displayModels[SportDisplayType.FB_LEAGUE_SCHEDULE] as? FBLeagueScheduleDisplayModel

    val teamIds = listOf(displayModel?.game?.teams?.home?.id, displayModel?.game?.teams?.away?.id)
    val teamCategories = teamIds.map {
        GameStatsTeamState(
            name = teamNameDic["short_${it}"] ?: "",
            imageUrl = FBUtil.teamLogoUrl(it)
        )
    }

    var playerList = playerStats.mapNotNull { player ->
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
    val gameDetailTitle = "심판: "
    val gameDetailContent = "${displayModel?.game?.fixture?.referee}"

    // TODO: 나중에 다른 GameStatsView도 playersTotalStats 작업이 되면 StandingsRankItem 수정한 후 아래 주석 추가.
//    playersTotalStats?.let { totalStats ->
//        val newPlayerList = playerList.toMutableList()
//        newPlayerList.add(
//            StandingsItemState(
//                isGameStats = true,
//                imageUrl = null,
//                name = "합계(팀 기록)",
//                dataList = listOf(
//                    totalStats.goals.total.toString(),
//                    totalStats.penalty.scored.toString(),
//                    totalStats.goals.assists.toString(),
//                    totalStats.shots.total.toString(),
//                    totalStats.shots.on.toString(),
//                    totalStats.passes.key.toString(),
//                    "${totalStats.dribbles.success}/${totalStats.dribbles.attempts}(${totalStats.dribbles.success.percentageOf(totalStats.dribbles.attempts, 1)}%)",
//                    totalStats.offsides.toString(),
//                    totalStats.tackles.total.toString(),
//                    "${totalStats.duels.won}/${totalStats.duels.total}(${totalStats.duels.won.percentageOf(totalStats.duels.total, 1)}%)",
//                    totalStats.tackles.interceptions.toString(),
//                    totalStats.passes.total.toString(),
//                    totalStats.fouls.drawn.toString(),
//                    totalStats.fouls.committed.toString(),
//                    totalStats.cards.yellow.toString(),
//                    totalStats.cards.red.toString(),
//                    "",
//                    ""
//                )
//            )
//        )
//
//        playerList = newPlayerList
//    }

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
            shouldShowTitle = fbLeagueScheduleModel == null,
            shouldShowGameItem = fbLeagueScheduleModel == null,
            shouldShowStats = displayModel?.game?.fixture?.status?.short != StringConstants.Football.GAME_NOT_STARTED,
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
            gameDetailTitle = gameDetailTitle,
            gameDetailContent = gameDetailContent
        ),
        actions = GameStatsContainerActions(
            teamCategoryButtonAction = { index ->
                fbGameStatsViewModel.send(FBGameStatsIntent.SelectTeam(index))
            },
            secondCategoryButtonAction = { index ->
                fbGameStatsViewModel.send(FBGameStatsIntent.SelectSecondCategory(index))
            },
            refreshButtonAction = {
                displayModel?.let {
                    searchViewModel.send(SearchViewModel.Intent.RefreshGame(season = it.season, category = "football"))
                }
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

        // team total stats
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center,
//            modifier = Modifier
//                .width(132.dp)
//                .height(fbGameStatsViewModel.dataItemHeight)
//        ) {
//            Text(
//                text = "합계(팀 기록)",
//                fontSize = 12.sp,
//                maxLines = 2,
//                modifier = Modifier.weight(1f),
//                textAlign = TextAlign.Center
//            )
//
//            VCapsuleBar(modifier = Modifier.alpha(0.5f))
//        }




















