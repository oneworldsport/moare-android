package com.moare.android.features.search.display.kbo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.constants.UIConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.KBOUtil
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.display.common.container.state.GameStatsContainerActions
import com.moare.android.features.search.display.common.container.state.GameStatsContainerState
import com.moare.android.features.search.display.common.container.state.GameStatsTeamState
import com.moare.android.features.search.display.common.container.state.StandingsItemState
import com.moare.android.features.search.display.common.container.view.GameStatsViewContainer
import com.moare.android.features.search.display.kbo.viewmodel.KBOGameStatsIntent
import com.moare.android.features.search.display.kbo.viewmodel.KBOGameStatsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOGameStatsDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOGameLineScore
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.CapsuleButton
import com.moare.android.ui.common.components.RoundedBorderText
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.common.components.VCapsuleBar
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterColumn

@Composable
fun KBOGameStatsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboGameStatsViewModel: KBOGameStatsViewModel = hiltViewModel(),
    data: KBOGameStatsDisplayModel
) {
    /* ---------------------
       ui state
       --------------------- */
    val horizontalScrollState = rememberScrollState()

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by kboGameStatsViewModel.displayModel.collectAsState()
    val firstCategorySelectedIndex by kboGameStatsViewModel.firstCategorySelectedIndex.collectAsState()
    val secondCategorySelectedIndex by kboGameStatsViewModel.secondCategorySelectedIndex.collectAsState()
    val selectedTeamIndex by kboGameStatsViewModel.selectedTeamIndex.collectAsState()
    val teamHitters by kboGameStatsViewModel.teamHitters.collectAsState()
    val teamPitchers by kboGameStatsViewModel.teamPitchers.collectAsState()
    val teamNameDic = kboGameStatsViewModel.teamNameDictionary

    val game = displayModel?.game

    val poppedView by searchViewModel.poppedView.collectAsState()

    val teamIds = listOf(displayModel?.game?.gameInfo?.homeTeamId, displayModel?.game?.gameInfo?.awayTeamId)
    val teamCategories = teamIds.map {
        GameStatsTeamState(
            name = teamNameDic["short_${it}"] ?: "",
            imageUrl = KBOUtil.teamLogoUrl(it)
        )
    }

    val hitterList: List<StandingsItemState> = teamHitters.map {
        StandingsItemState(
            numInfo = it.battingNumber,
            imageUrl = KBOUtil.playerPhotoUrl(it.id),
            name = it.name,
            extraInfo = it.position
                .replace("#", "•")
                .replace("지명타자", "지명"),
            dataList = listOf(
                it.ab.toString(),
                it.h.toString(),
//                it.doubles.toString(), // live 제공 X
                it.homeRuns.toString(),
                it.rbi.toString(),
                it.r.toString(),
                it.baseOnBalls.toString(),
                it.strikeOuts.toString(),
                it.groundIntoDoublePlay.toString(),
//                it.hitByPitch.toString() // live 제공 X
            )
        )
    }
    val pitcherList: List<StandingsItemState> = teamPitchers.map {
        StandingsItemState(
            imageUrl = KBOUtil.playerPhotoUrl(it.id),
            name = it.name,
            dataList = listOf(
                it.ip, it.r, it.er, it.bb, it.so, it.h
            )
        )
    }

    val columnWidthList = listOf(50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp)
    val secondStatsColumnWidthList = listOf(50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp)
    val gameDetailTitle = "날짜: \n\n장소: "
    val gameDetailContent = buildString {
        append("${CalendarUtil.formatDate(displayModel?.game?.gameInfo?.date).split(" ").firstOrNull() ?: ""}\n")
        append("${CalendarUtil.formatDate(displayModel?.game?.gameInfo?.date, TimeFormatType.AMPM)}\n")
        append(teamNameDic["venue_${displayModel?.game?.gameInfo?.homeTeamId}"] ?: "")
    }

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.KBOGameStats) {
            kboGameStatsViewModel.send(KBOGameStatsIntent.InitData(data))
        }
    }

    GameStatsViewContainer(
        state = GameStatsContainerState(
            shouldShowStats = game?.gameInfo?.gameStatus?.toIntOrNull() == StringConstants.KBO.GAME_LIVE || game?.gameInfo?.gameStatus?.toIntOrNull() == StringConstants.KBO.GAME_FINAL,
            shouldShowRefreshButton = game?.gameInfo?.gameStatus?.toIntOrNull() == StringConstants.KBO.GAME_LIVE,
            teamCategories = teamCategories,
            secondCategories = StringConstants.KBO.GAME_STATS_HITTING_CATEGORIES,
            teamCategorySelectedIndex = selectedTeamIndex,
            secondCategorySelectedIndex = firstCategorySelectedIndex,
            firstColumnWidth = 150.dp,
            columnWidthList = columnWidthList,
            playerList = hitterList,
            gameDetailTitle = gameDetailTitle,
            gameDetailContent = gameDetailContent,
            noStatsText = if (game?.gameInfo?.gameStatus?.toIntOrNull() == StringConstants.KBO.GAME_CANCELED) "취소된 경기입니다." else null,
            firstStatsTitle = "타자",
            secondStatsTitle = "투수",
            secondStatsCategories = StringConstants.KBO.GAME_STATS_PITCHING_CATEGORIES,
            secondStatsCategorySelectedIndex = secondCategorySelectedIndex,
            secondStatsColumnWidthList = secondStatsColumnWidthList,
            secondStatsPlayerList = pitcherList,
        ),
        actions = GameStatsContainerActions(
            teamCategoryButtonAction = { index ->
                kboGameStatsViewModel.send(KBOGameStatsIntent.SelectTeam(index))
            },
            secondCategoryButtonAction = { index ->
                kboGameStatsViewModel.send(KBOGameStatsIntent.SelectFirstCategory(index))
            },
            refreshButtonAction = {
                displayModel?.let {
                    searchViewModel.send(SearchViewModel.Intent.RefreshGame(season = it.season, category = "baseball"))
                }
            },
            secondStatsCategoryButtonAction = { index ->
                kboGameStatsViewModel.send(KBOGameStatsIntent.SelectSecondCategory(index))
            }
        ),
        titleContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = UIConstants.Padding.DEFAULT_H_PADDING)
            ) {
                BaseballLeagueTitle(
                    url = KBOUtil.kboLogoUrl,
                    leagueName = "KBO",
                    leagueSeason = displayModel?.season
                )

                Spacer(Modifier.weight(1f))
            }
        },
        gameContent = {
            if (
                game?.gameInfo?.gameStatus?.toIntOrNull() == StringConstants.KBO.GAME_SCHEDULED ||
                game?.gameInfo?.gameStatus?.toIntOrNull() == StringConstants.KBO.GAME_CANCELED
            ) {
                KBOLeagueScheduleListItem(
                    data = ModelConverter().kboGameToGameScheduleConverter(game),
                    teamNameDic = teamNameDic
                )
            } else {
                KBOGameStatsScoreInfoItem()
            }
        }
    )
}

@Composable
fun KBOGameStatsScoreInfoItem(
    kboGameStatsViewModel: KBOGameStatsViewModel = hiltViewModel()
) {
    val density = LocalDensity.current
    var borderTextWidth by remember { mutableStateOf(0.dp) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by kboGameStatsViewModel.displayModel.collectAsState()
    val game = displayModel?.game
    val homeTeamId = game?.gameInfo?.homeTeamId
    val awayTeamId = game?.gameInfo?.awayTeamId
    val gameStatus = game?.gameInfo?.gameStatus?.toIntOrNull() ?: 0
    val teamNameDic = kboGameStatsViewModel.teamNameDictionary

    /* ---------------------
       constants
       --------------------- */
    val gameStatusText = when (gameStatus) {
        StringConstants.KBO.GAME_SCHEDULED -> StringConstants.GAME_NOT_STARTED_STR
        StringConstants.KBO.GAME_LIVE -> game?.lineScore?.currentInning ?: StringConstants.GAME_LIVE_STR
        StringConstants.KBO.GAME_FINAL -> StringConstants.GAME_FINISHED_STR
        StringConstants.KBO.GAME_CANCELED -> StringConstants.GAME_CANCELED_STR
        else -> ""
    }

    val gameStatusColor = if (gameStatus == StringConstants.KBO.GAME_LIVE) {
        MaterialTheme.colors.primary
    } else {
        Color.Gray
    }

    /* ---------------------
       ui
       --------------------- */
    Row(
        verticalAlignment = Alignment.Bottom,
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
                modifier = Modifier.fillMaxWidth()
            ) {
                RoundedBorderText(
                    text = "원정",
                    fontSize = 11.sp,
                    radius = 4.dp,
                    textColor = Color.Gray,
                    borderColor = Color.Gray,
                    modifier = Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            with(density) {
                                borderTextWidth = layoutCoordinates.size.width.toDp()
                            }
                        }
                )
                URLImage(
                    url = KBOUtil.teamLogoUrl(awayTeamId),
                    size = URLImageSize.SMALL,
                    isSvg = true
                )
                Text(
                    text = teamNameDic["short_$awayTeamId"] ?: "",
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }

            CapsuleButton(
                text = gameStatusText,
                color = gameStatusColor,
                isDisabled = true,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {}

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.width(borderTextWidth)
                ) {
                    RoundedBorderText(
                        text = "홈",
                        fontSize = 11.sp,
                        radius = 4.dp,
                        textColor = Moare,
                        borderColor = Moare
                    )
                }
                URLImage(
                    url = KBOUtil.teamLogoUrl(homeTeamId),
                    size = URLImageSize.SMALL,
                    isSvg = true
                )
                Text(
                    text = teamNameDic["short_$homeTeamId"] ?: "",
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }
        }

        KBOGameStatsLineScoreContainer()
    }
}

@Composable
fun RowScope.KBOGameStatsLineScoreContainer(
    kboGameStatsViewModel: KBOGameStatsViewModel = hiltViewModel()
) {
    val displayModel by kboGameStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val game = it.game

        game.lineScore?.let { lineScore ->
            val homeTeamLineScore = lineScore.home.r.toIntOrNull() ?: 0
            val awayTeamLineScore = lineScore.away.r.toIntOrNull() ?: 0

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
                        text = awayTeamLineScore.toString(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 50.sp,
                        modifier = Modifier
                            .padding(start = 4.dp, end = 8.dp)
                            .width(30.dp),
                        color = if (awayTeamLineScore >= homeTeamLineScore) MaterialTheme.colors.primary else Color.Black
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
                        text = homeTeamLineScore.toString(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 50.sp,
                        modifier = Modifier
                            .padding(start = 4.dp, end = 8.dp)
                            .width(30.dp),
                        color = if (homeTeamLineScore >= awayTeamLineScore) MaterialTheme.colors.primary else Color.Black
                    )
                }

                Column(
                    Modifier.weight(1f)
                ) {
                    KBOGameStatsLineScoreTitle(lineScore.away)

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Gray)
                            .alpha(0.5f)
                    )

                    KBOGameStatsLineScoreItem(lineScore = lineScore.away)

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Gray)
                            .alpha(0.5f)
                    )

                    KBOGameStatsLineScoreItem(lineScore = lineScore.home)
                }
            }
        }
    }
}

@Composable
fun KBOGameStatsLineScoreTitle(
    lineScore: KBOGameLineScore
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(25.dp)
    ) {
        for (index in 1..12) {
            if (index < 10 ||
                (index == 10 && lineScore.inning10 != "-") ||
                (index == 11 && lineScore.inning11 != "-")
            ) {
                VCapsuleBar(modifier = Modifier.alpha(0.5f))
                Text(
                    text = "$index",
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun KBOGameStatsLineScoreItem(
    kboGameStatsViewModel: KBOGameStatsViewModel = hiltViewModel(),
    lineScore: KBOGameLineScore
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(kboGameStatsViewModel.lineScoreItemHeight)
    ) {
        for (index in 0 until 11) {
            if (index < 9 ||
                (index == 9 && lineScore.inning10 != "-") ||
                (index == 10 && lineScore.inning11 != "-")
            ) {
                VCapsuleBar(modifier = Modifier.alpha(0.5f))
                Text(
                    text = lineScore.innings[index],
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}