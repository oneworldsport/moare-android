package com.moare.android.features.search.display.football.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.util.FBUtil
import com.moare.android.core.util.percentageOf
import com.moare.android.features.search.display.common.container.state.GameStatsCoachState
import com.moare.android.features.search.display.common.container.state.GameStatsContainerActions
import com.moare.android.features.search.display.common.container.state.GameStatsContainerState
import com.moare.android.features.search.display.common.container.state.GameStatsTeamState
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.GameStatsViewContainer
import com.moare.android.features.search.display.football.store.FBGameStatsAction
import com.moare.android.features.search.display.football.store.FBGameStatsStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.ui.common.components.FBLeagueTitleForGameStats

@Composable
fun FBGameStatsView(
    searchStore: SearchStore,
    store: FBGameStatsStore,
    isCombinedView: Boolean = false
) {
    val horizontalScrollState = rememberScrollState()

    val displayModel by store.displayModel.collectAsState()
    val coach by store.coach.collectAsState()
    val firstCategorySelectedIndex by store.firstCategorySelectedIndex.collectAsState()
    val selectedTeamIndex by store.teamCategorySelectedIndex.collectAsState()
    val playerStats by store.playerStats.collectAsState()
    val playersTotalStats by store.playersTotalStats.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()
    val playerNameDic by store.playerNameDic.collectAsState()
    val isRefreshing by store.isRefreshing.collectAsState()

    val game = displayModel.game

    val teamIds = listOf(game.teams.home.id, game.teams.away.id)
    val teamCategories = teamIds.map {
        GameStatsTeamState(
            name = teamNameDic["short_${it}"] ?: "",
            imageUrl = FBUtil.teamLogoUrl(it)
        )
    }

    val playerList = playerStats.mapNotNull { player ->
        val stats = player.statistics.firstOrNull()
        val playerId = player.player.id

        stats?.let { stats ->
            StandingsItemState(
                id = playerId,
                isGameStats = true,
                imageUrl = player.player.photo,
                name = playerNameDic["${playerId}"] ?: player.player.name,
                extraInfo = if (player.isStarter) "선발" else "후보",
                extraSubInfo = player.position ?: "",
                dataList = listOf(
                    stats.games.minutes.toString(),
                    stats.goals.total.toString(),
                    stats.penalty.scored.toString(),
                    stats.goals.assists.toString(),
                    "",
                    stats.shots.total.toString(),
                    stats.shots.on.toString(),
                    stats.passes.total.toString(),
                    "${stats.dribbles.success}/${stats.dribbles.attempts}(${stats.dribbles.success.percentageOf(stats.dribbles.attempts, 1)}%)",
                    "",
                    stats.tackles.total.toString(),
                    "${stats.duels.won}/${stats.duels.total}(${stats.duels.won.percentageOf(stats.duels.total, 1)}%)",
                    stats.tackles.interceptions.toString(),
                    "",
                    stats.offsides.toString(),
                    stats.fouls.drawn.toString(),
                    stats.fouls.committed.toString(),
                    stats.cards.yellow.toString(),
                    stats.cards.red.toString()
                )
            )
        }
    }
    val columnWidthList = listOf(80.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 70.dp, 70.dp, 100.dp, 50.dp, 70.dp, 100.dp, 70.dp, 50.dp, 80.dp, 70.dp, 70.dp, 50.dp, 50.dp)
    val gameDetailTitle = "장소: \n심판: "
    val gameDetailContent = buildString {
        append("${teamNameDic["venue_${game.teams.home.id}"] ?: game.fixture.venue.name}\n")
        append(game.fixture.referee)
    }

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
    val firstSelectedCategoryPosition = with(LocalDensity.current) {
        val attackCategoriesSize = StringConstants.Football.GAME_STATS_ATTACK_CATEGORIES.size
        val defendCategoriesSize = StringConstants.Football.GAME_STATS_DEFEND_CATEGORIES.size

        if (firstCategorySelectedIndex in 0 until attackCategoriesSize) {
            (store.itemWidth * firstCategorySelectedIndex).toPx()
        } else if (firstCategorySelectedIndex in attackCategoriesSize until attackCategoriesSize + defendCategoriesSize) {
            ((store.itemWidth * firstCategorySelectedIndex) + store.barWidth).toPx()
        } else {
            ((store.itemWidth * firstCategorySelectedIndex) + (store.barWidth * 2)).toPx()
        }
    }.toInt()

    // scroll to category that matches with the keyword,
    // and when first category list's item is selected by click
    LaunchedEffect(firstCategorySelectedIndex) {
        if (store.shouldScrollCategory) {
            horizontalScrollState.animateScrollTo(
                value = firstSelectedCategoryPosition,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearOutSlowInEasing
                )
            )
        }
    }

    GameStatsViewContainer(
        state = GameStatsContainerState(
            shouldShowTitle = !isCombinedView,
            shouldShowGameContent = !isCombinedView,
            shouldShowStats = game.fixture.status.short != StringConstants.Football.GAME_NOT_STARTED,
            shouldShowCoach = true,
            shouldShowRefreshButton = StringConstants.Football.GAME_LIVE_LIST.contains(game.fixture.status.short),
            teamCategories = teamCategories,
            firstStatsCategories = StringConstants.Football.GAME_STATS_CATEGORIES,
            coachState = GameStatsCoachState(
                name = coach?.name,
                imageUrl = coach?.photo
            ),
            teamCategorySelectedIndex = selectedTeamIndex,
            firstStatsCategorySelectedIndex = firstCategorySelectedIndex,
            firstStatsColumnWidthList = columnWidthList,
            firstStatsPlayerList = playerList,
            gameDetailTitle = gameDetailTitle,
            gameDetailContent = gameDetailContent
        ),
        actions = GameStatsContainerActions(
            teamCategoryButtonAction = { index ->
                store.send(FBGameStatsAction.SelectTeam(index))
            },
            firstStatsTitleCategoryAction = {
                store.send(FBGameStatsAction.SelectTitleCategory)
            },
            firstStatsCategoryButtonAction = { index ->
                store.send(FBGameStatsAction.SelectFirstCategory(index))
            },
            refreshButtonAction = {
                store.send(FBGameStatsAction.RefreshGame())
            },
            isRefreshing = isRefreshing
        ),
        titleContent = {
            FBLeagueTitleForGameStats(
                url = game.league.logo,
                leagueName = game.league.name,
                leagueSeason = game.league.season,
                description = game.league.round
            )
        },
        gameContent = {
            FBLeagueScheduleListItem(
                searchStore = searchStore,
                store = null,
                data = ModelConverter.fbGameToGameScheduleConverter(game),
                leagueId = displayModel.leagueId,
                teamNameDic = teamNameDic
            )
        }
    )
}

        // team total stats
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center,
//            modifier = Modifier
//                .width(132.dp)
//                .height(store.dataItemHeight)
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




















