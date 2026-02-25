package com.moare.android.features.search.display.tennis.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.constants.UIConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.NBAUtil
import com.moare.android.core.util.OutputTimeFormatType
import com.moare.android.core.util.Util
import com.moare.android.core.util.displayOrDash
import com.moare.android.core.util.format3
import com.moare.android.features.search.display.common.container.state.GameStatsContainerActions
import com.moare.android.features.search.display.common.container.state.GameStatsContainerState
import com.moare.android.features.search.display.common.container.state.GameStatsTeamState
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.GameStatsViewContainer
import com.moare.android.features.search.display.nba.store.NBAGameStatsAction
import com.moare.android.features.search.display.nba.store.NBAGameStatsStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.display.tennis.store.TennisGameStatsAction
import com.moare.android.features.search.display.tennis.store.TennisGameStatsStore
import com.moare.android.features.search.display.tennis.store.TennisLeagueScheduleStore
import com.moare.android.features.search.models.models.nba.NBALineScore
import com.moare.android.ui.common.components.CapsuleButton
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.HDivider
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.RoundedBorderText
import com.moare.android.ui.common.components.TennisTournamentTitle
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow
import com.moare.android.ui.util.getOffsetOfAniCapsuleBar
import kotlin.collections.lastIndex

@Composable
fun TennisGameStatsView(
    searchStore: SearchStore,
    store: TennisGameStatsStore
) {
    val displayModel by store.displayModel.collectAsState()
    val teamCategorySelectedIndex by store.teamCategorySelectedIndex.collectAsState()
    val isRefreshing by store.isRefreshing.collectAsState()

    val game = displayModel.game
    val gameInfo = game.gameInfo
    val statusCode = game.gameInfo.status?.code ?: 0

    val teamCategories = listOf(
        GameStatsTeamState(
            name = "득점 흐름",
            imageUrl = null
        ),
        GameStatsTeamState(
            name = "선수 기록",
            imageUrl = null
        )
    )

    val gameDetailTitle = "날짜: \n\n도시: \n경기장: \n코트 종류: "
    val gameDetailContent = buildString {
        append("${CalendarUtil.formatDate(gameInfo.gameDate).split(" ").firstOrNull() ?: ""}\n")
        append("${CalendarUtil.formatDate(gameInfo.gameDate, outputFormatType = OutputTimeFormatType.AMPM)}\n")
        append("${gameInfo.venue?.city?.name ?: ""}\n")
        append("${gameInfo.venue?.name ?: ""}\n")
        append("${StringConstants.Tennis.groundTypeKr(gameInfo.groundType)}\n")
    }

    GameStatsViewContainer(
        state = GameStatsContainerState(
            shouldShowStats = statusCode != Constants.GameStatus.Tennis.NOT_STARTED,
            shouldShowRefreshButton = statusCode in Constants.GameStatus.Tennis.LIVE_LIST,
            teamCategories = teamCategories,
            teamCategorySelectedIndex = teamCategorySelectedIndex,
            gameDetailTitle = gameDetailTitle,
            gameDetailContent = gameDetailContent,
            firstStatsCategories = emptyList(),
            firstStatsPlayerList = emptyList()
        ),
        actions = GameStatsContainerActions(
            teamCategoryButtonAction = { index ->
                store.send(TennisGameStatsAction.SelectTeam(index))
            },
            firstStatsCategoryButtonAction = { index ->
            },
            refreshButtonAction = {
                store.send(TennisGameStatsAction.RefreshGame())
            },
            isRefreshing = isRefreshing
        ),
        shouldUseCustomStatsContent = true,
        titleContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = UIConstants.Padding.DEFAULT_H_PADDING)
            ) {
                TennisTournamentTitle(displayModel.leagueId, displayModel.season)

                Text(
                    text = " | ",
                    fontSize = 14.sp
                )

                Text(
                    text = "${displayModel.leagueKrName} ${displayModel.roundName}",
                    fontSize = 14.sp
                )

                Spacer(Modifier.weight(1f))
            }
        },
        gameContent = {
            TennisGameStatsScoreInfoContainer(store)
        },
        customStatsContent = {
            if (teamCategorySelectedIndex == 0) {
                TennisGameStatsPointByPointContainer(store)
            } else {
                TennisGameStatsPlayerStatsContainer(store)
            }
        }
    )
}

@Composable
fun TennisGameStatsScoreInfoContainer(
    store: TennisGameStatsStore
) {
    val displayModel by store.displayModel.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val leagueId = displayModel.leagueId
    val gameInfo = displayModel.game.gameInfo
    val homeTeam = gameInfo.homeTeam
    val awayTeam = gameInfo.awayTeam
    val gameStatus = gameInfo.status?.code ?: Constants.GameStatus.Tennis.NOT_STARTED
    val homeTeamDefaultName = if (store.isDoubles) homeTeam?.name ?: "" else homeTeam?.shortName ?: ""
    val awayTeamDefaultName = if (store.isDoubles) awayTeam?.name ?: "" else awayTeam?.shortName ?: ""

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = UIConstants.Padding.DEFAULT_H_PADDING)
    ) {
        CenterColumn(
            modifier = Modifier
                .weight(0.4f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                URLImage(
                    url = Util.teamLogoUrl(leagueId, homeTeam?.id),
                    size = URLImageSize.SMALL
                )

                Text(
                    text = teamNameDic["short_${homeTeam?.id}"] ?: homeTeamDefaultName,
                    fontSize = 13.sp,
                    maxLines = 2
                )

                if (gameInfo.isGameFinished && gameInfo.isHomeWinner) {
                    RoundedBorderText(
                        text = "승",
                        fontSize = 11.sp,
                        radius = 4.dp,
                        textColor = Moare,
                        borderColor = Moare
                    )
                }
            }

            CapsuleButton(
                text = Constants.GameStatus.tennisGameStatusText(gameStatus),
                color = Constants.GameStatus.gameStatusColor(leagueId, gameStatus.toString()),
                isDisabled = true,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {}

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                URLImage(
                    url = Util.teamLogoUrl(leagueId, awayTeam?.id),
                    size = URLImageSize.SMALL
                )

                Text(
                    text = teamNameDic["short_${awayTeam?.id}"] ?: awayTeamDefaultName,
                    fontSize = 13.sp,
                    maxLines = 2
                )

                if (gameInfo.isGameFinished && !gameInfo.isHomeWinner) {
                    RoundedBorderText(
                        text = "승",
                        fontSize = 11.sp,
                        radius = 4.dp,
                        textColor = Color.Gray,
                        borderColor = Color.Gray
                    )
                }
            }
        }

        TennisGameStatsSetScoreContainer(store = store)
    }
}

@Composable
fun RowScope.TennisGameStatsSetScoreContainer(
    store: TennisGameStatsStore
) {
    val displayModel by store.displayModel.collectAsState()

    val gameInfo = displayModel.game.gameInfo
    val homeScore = gameInfo.homeScore?.display
    val awayScore = gameInfo.awayScore?.display

    Row(
        modifier = Modifier
            .height(127.dp) // 25 + 1 + 50 + 1 + 50
            .weight(1f)
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(
                text = homeScore.displayOrDash,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                lineHeight = 50.sp,
                modifier = Modifier
                    .padding(start = 4.dp, end = 8.dp)
                    .width(30.dp),
                color = homeScore?.let { homeScore ->
                    awayScore?.let { awayScore ->
                        if (homeScore >= awayScore) Moare else Color.Black
                    }
                } ?: Color.Black
            )

            Box(
                Modifier
                    .width(42.dp) // 30 + 8 + 4
                    .height(1.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Gray)
                    .alpha(0.5f)
            )

            Text(
                text = awayScore.displayOrDash,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                lineHeight = 50.sp,
                modifier = Modifier
                    .padding(start = 4.dp, end = 8.dp)
                    .width(30.dp),
                color = homeScore?.let { homeScore ->
                    awayScore?.let { awayScore ->
                        if (awayScore >= homeScore) Moare else Color.Black
                    }
                } ?: Color.Black
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            TennisGameStatsSetScoreTitle(store)

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Gray)
                    .alpha(0.5f)
            )

            TennisGameStatsSetScoreItem(store)
        }
    }
}

@Composable
fun TennisGameStatsSetScoreTitle(
    store: TennisGameStatsStore
) {
    val displayModel by store.displayModel.collectAsState()

    val gameInfo = displayModel.game.gameInfo

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(25.dp)
    ) {
        for (index in 1 .. gameInfo.defaultPeriodCount) {
            VCapsuleBar(modifier = Modifier.alpha(0.5f))
            Text(
                text = "${index}세트",
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TennisGameStatsSetScoreItem(
    store: TennisGameStatsStore
) {
    val displayModel by store.displayModel.collectAsState()

    val gameInfo = displayModel.game.gameInfo
    val homeSetScore = gameInfo.homeScore
    val awaySetScore = gameInfo.awayScore

    CenterColumn() {
        CenterRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            for (index in 0 until gameInfo.defaultPeriodCount) {
                val homePeriodScore = homeSetScore?.periods?.getOrNull(index)
                val awayPeriodScore = awaySetScore?.periods?.getOrNull(index)
                val homeTieBreakScore = homeSetScore?.periodsTieBreak?.getOrNull(index)
                val isWinner = if (homePeriodScore == 7) {
                    true
                } else {
                    if (homePeriodScore != null && awayPeriodScore != null) {
                        (homePeriodScore == 6) && (homePeriodScore - awayPeriodScore >= 2)
                    } else {
                        false
                    }
                }

                VCapsuleBar(modifier = Modifier.alpha(0.5f))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = homePeriodScore.displayOrDash,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = if (isWinner) Moare else Color.Black
                    )

                    homeTieBreakScore?.let {
                        Text(
                            text = homeTieBreakScore.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.End,
                            color = if (isWinner) Moare else Color.Black,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .fillMaxSize()
                                .padding(top = 4.dp, end = 4.dp)
                        )
                    }
                }
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

        CenterRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            for (index in 0 until gameInfo.defaultPeriodCount) {
                val homePeriodScore = homeSetScore?.periods?.getOrNull(index)
                val awayPeriodScore = awaySetScore?.periods?.getOrNull(index)
                val awayTieBreakScore = awaySetScore?.periodsTieBreak?.getOrNull(index)
                val isWinner = if (awayPeriodScore == 7) {
                    true
                } else {
                    if (homePeriodScore != null && awayPeriodScore != null) {
                        (awayPeriodScore == 6) && (awayPeriodScore - homePeriodScore >= 2)
                    } else {
                        false
                    }
                }

                VCapsuleBar(modifier = Modifier.alpha(0.5f))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = homePeriodScore.displayOrDash,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = if (isWinner) Moare else Color.Black
                    )

                    awayTieBreakScore?.let {
                        Text(
                            text = awayTieBreakScore.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.End,
                            color = if (isWinner) Moare else Color.Black,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .align(Alignment.TopEnd)
                                .fillMaxSize()
                                .padding(top = 4.dp, end = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TennisGameStatsPointByPointContainer(
    store: TennisGameStatsStore
) {
    val density = LocalDensity.current
    val horizontalScrollState = rememberScrollState()
    var selectedSetIndex by remember { mutableStateOf(0) }
    var setButtonWidth by remember { mutableStateOf(0.dp) }
    val setBarXOffset by animateDpAsState(
        targetValue = getOffsetOfAniCapsuleBar(itemWidth = setButtonWidth, spacing = 2.dp, index = selectedSetIndex),
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    val displayModel by store.displayModel.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val game = displayModel.game
    val gameInfo = game.gameInfo
    val homeTeam = gameInfo.homeTeam
    val awayTeam = gameInfo.awayTeam
    val homeTeamDefaultName = if (store.isDoubles) homeTeam?.name ?: "" else homeTeam?.shortName ?: ""
    val awayTeamDefaultName = if (store.isDoubles) awayTeam?.name ?: "" else awayTeam?.shortName ?: ""
    val pointByPoint = game.pointByPoint ?: emptyList()
    val selectedSet = pointByPoint.firstOrNull { it.set == selectedSetIndex + 1 }
    val selectedGames = (selectedSet?.games ?: emptyList()).sortedBy { it.game }

    CenterColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Column {
            CenterRow(
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                for (index in 0 until pointByPoint.size) {
                    val homePeriodScore = gameInfo.homeScore?.periods?.getOrNull(index)

                    homePeriodScore?.let {
                        Text(
                            text = "${index + 1}세트",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedSetIndex = index
                                }
                                .onGloballyPositioned { layoutCoordinates ->
                                    with(density) {
                                        setButtonWidth = layoutCoordinates.size.width.toDp()
                                    }
                                }
                        )

                        if (index != pointByPoint.size - 1) {
                            VCapsuleBar(
                                customHeight = 20.dp,
                                modifier = Modifier.alpha(0.5f)
                            )
                        }
                    }
                }
            }

            HCapsuleBar(
                modifier = Modifier
                    .offset(x = setBarXOffset)
            )
        }

        CenterRow {
            CenterColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (index in 0 until selectedGames.size) {
                    CenterRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text(
                            text = "Game${index + 1}",
                            fontSize = 14.sp
                        )

                        VCapsuleBar(
                            customWidth = 1.dp,
                            modifier = Modifier.alpha(0.5f)
                        )
                    }

                    if (index != selectedGames.size - 1) {
                        HDivider(
                            modifier = Modifier
                                .width(1.dp)
                                .alpha(0f)
                        )
                    }
                }
            }

            Row(
                Modifier.horizontalScroll(horizontalScrollState)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    for ((index, game) in selectedGames.withIndex()) {
                        val score = game.score
                        val points = game.points ?: emptyList()

                        CenterColumn(
                            modifier = Modifier.height(50.dp)
                        ) {
                            CenterRow(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = teamNameDic["short_${homeTeam?.id}"] ?: homeTeamDefaultName,
                                    fontSize = 14.sp,
                                    modifier = Modifier.width(70.dp)
                                )

                                score?.let {
                                    val text = if (score.isTieBreak) {
                                        ""
                                    } else {
                                        if (score.isHomeServing) {
                                            "S"
                                        } else if (score.isHomeWinner) {
                                            "B"
                                        } else {
                                            ""
                                        }
                                    }

                                    Text(
                                        text = text,
                                        fontSize = 14.sp,
                                        color = Moare,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.width(20.dp)
                                    )

                                    Text(
                                        text = "${score.homeScore}",
                                        fontSize = 14.sp,
                                        fontWeight = if (score.isHomeWinner) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.width(30.dp)
                                    )
                                }

                                VCapsuleBar(
                                    customWidth = 1.dp,
                                    modifier = Modifier.alpha(0.5f)
                                )

                                for ((index, point) in points.withIndex()) {
                                    Text(
                                        text = point.homePoint,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.width(30.dp)
                                    )

                                    if (index == points.size - 1 && score?.isGameFinished == true) {
                                        val text = if (score?.isHomeWinner == true) "G" else point.homePoint
                                        val color = if (score?.isHomeWinner == true) Moare else Color.Black

                                        Text(
                                            text = text,
                                            fontSize = 14.sp,
                                            color = color,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.width(30.dp)
                                        )
                                    }
                                }
                            }

                            CenterRow(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = teamNameDic["short_${awayTeam?.id}"] ?: awayTeamDefaultName,
                                    fontSize = 14.sp,
                                    modifier = Modifier.width(70.dp)
                                )

                                score?.let {
                                    val text = if (score.isTieBreak) {
                                        ""
                                    } else {
                                        if (!score.isHomeServing) {
                                            "S"
                                        } else if (score.isAwayWinner) {
                                            "B"
                                        } else {
                                            ""
                                        }
                                    }

                                    Text(
                                        text = text,
                                        fontSize = 14.sp,
                                        color = Moare,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.width(20.dp)
                                    )

                                    Text(
                                        text = "${score.awayScore}",
                                        fontSize = 14.sp,
                                        fontWeight = if (score.isAwayWinner) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.width(30.dp)
                                    )
                                }

                                VCapsuleBar(
                                    customWidth = 1.dp,
                                    modifier = Modifier.alpha(0.5f)
                                )

                                for ((index, point) in points.withIndex()) {
                                    Text(
                                        text = point.awayPoint,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.width(30.dp)
                                    )

                                    if (index == points.size - 1 && score?.isGameFinished == true) {
                                        val text = if (score?.isAwayWinner == true) "G" else point.awayPoint
                                        val color = if (score?.isAwayWinner == true) Moare else Color.Black

                                        Text(
                                            text = text,
                                            fontSize = 14.sp,
                                            color = color,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.width(30.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // TODO: ScrollView라 .weight(1f) 줘도 안보이는듯. TournamentDrawViewContainer에서도 비슷한 증상 겪은적있음.
                        if (index != selectedGames.size - 1) {
                            CenterRow {
//                                Spacer(Modifier.width(112.dp).height(1.dp)) // 70 + 20 + 30 - 8(padding)
                                HDivider(color = Color.Gray, modifier = Modifier.alpha(0.5f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TennisGameStatsPlayerStatsContainer(
    store: TennisGameStatsStore
) {
    val density = LocalDensity.current
    var textWidth by remember { mutableStateOf(0.dp) }

    val displayModel by store.displayModel.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val leagueId = displayModel.leagueId
    val game = displayModel.game
    val gameInfo = game.gameInfo
    val homeTeam = gameInfo.homeTeam
    val awayTeam = gameInfo.awayTeam
    val homeTeamDefaultName = if (store.isDoubles) homeTeam?.name ?: "" else homeTeam?.shortName ?: ""
    val awayTeamDefaultName = if (store.isDoubles) awayTeam?.name ?: "" else awayTeam?.shortName ?: ""
    val displayStats = (game.statistics ?: emptyList()).flatMap { it.itemsForDisplay() }

    CenterColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        CenterRow() {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                URLImage(
                    url = Util.teamLogoUrl(leagueId, homeTeam?.id)
                )

                Text(
                    text = teamNameDic["short_${homeTeam?.id}"] ?: homeTeamDefaultName,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(1f)
            ) {
                URLImage(
                    url = Util.teamLogoUrl(leagueId, awayTeam?.id)
                )

                Text(
                    text = teamNameDic["short_${awayTeam?.id}"] ?: awayTeamDefaultName,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
                )
            }
        }

        for (stat in displayStats) {
            CenterRow(
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = stat.home,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Box {
                    Text(
                        text = stat.krname,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(textWidth)
                    )

                    // 폭 측정용(보이지 않음)
                    Text(
                        text = stat.krname,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.alpha(0f).onGloballyPositioned { layoutCoordinates ->
                            with(density) {
                                textWidth = max(textWidth, layoutCoordinates.size.width.toDp())
                            }
                        }
                    )
                }

                Text(
                    text = stat.away,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}





















