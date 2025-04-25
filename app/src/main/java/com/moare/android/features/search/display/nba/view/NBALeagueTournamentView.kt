package com.moare.android.features.search.display.nba.view

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.R
import com.moare.android.core.constants.UIConstants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.NBAUtil
import com.moare.android.core.util.displayOrDash
import com.moare.android.features.search.display.nba.viewmodel.NBALeagueTournamentViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBALeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow

@Composable
fun NBALeagueTournamentView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaLeagueTournamentViewModel: NBALeagueTournamentViewModel = hiltViewModel(),
    data: NBALeagueScheduleDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.NBALeagueTournament) {
            nbaLeagueTournamentViewModel.send(NBALeagueTournamentViewModel.Intent.InitData(data))
        }
    }

    /* ---------------------
       ui
       --------------------- */
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .horizontalScroll(rememberScrollState())
            .verticalScroll(rememberScrollState())
    ) {
        NBALeagueTournamentMainContainer()
    }
}

@Composable
fun NBALeagueTournamentMainContainer(
    nbaLeagueTournamentViewModel: NBALeagueTournamentViewModel = hiltViewModel()
) {
    /* ---------------------
        viewmodel state
        --------------------- */
    val westFirstRoundFirstGameList by nbaLeagueTournamentViewModel.westFirstRoundFirstGameList.collectAsState()
    val westFirstRoundFirstGameFirstTeamId by nbaLeagueTournamentViewModel.westFirstRoundFirstGameFirstTeamId.collectAsState()
    val westFirstRoundFirstGameSecondTeamId by nbaLeagueTournamentViewModel.westFirstRoundFirstGameSecondTeamId.collectAsState()
    val westFirstRoundSecondGameList by nbaLeagueTournamentViewModel.westFirstRoundSecondGameList.collectAsState()
    val westFirstRoundSecondGameFirstTeamId by nbaLeagueTournamentViewModel.westFirstRoundSecondGameFirstTeamId.collectAsState()
    val westFirstRoundSecondGameSecondTeamId by nbaLeagueTournamentViewModel.westFirstRoundSecondGameSecondTeamId.collectAsState()
    val westFirstRoundThirdGameList by nbaLeagueTournamentViewModel.westFirstRoundThirdGameList.collectAsState()
    val westFirstRoundThirdGameFirstTeamId by nbaLeagueTournamentViewModel.westFirstRoundThirdGameFirstTeamId.collectAsState()
    val westFirstRoundThirdGameSecondTeamId by nbaLeagueTournamentViewModel.westFirstRoundThirdGameSecondTeamId.collectAsState()
    val westFirstRoundFourthGameList by nbaLeagueTournamentViewModel.westFirstRoundFourthGameList.collectAsState()
    val westFirstRoundFourthGameFirstTeamId by nbaLeagueTournamentViewModel.westFirstRoundFourthGameFirstTeamId.collectAsState()
    val westFirstRoundFourthGameSecondTeamId by nbaLeagueTournamentViewModel.westFirstRoundFourthGameSecondTeamId.collectAsState()
    val eastFirstRoundFirstGameList by nbaLeagueTournamentViewModel.eastFirstRoundFirstGameList.collectAsState()
    val eastFirstRoundFirstGameFirstTeamId by nbaLeagueTournamentViewModel.eastFirstRoundFirstGameFirstTeamId.collectAsState()
    val eastFirstRoundFirstGameSecondTeamId by nbaLeagueTournamentViewModel.eastFirstRoundFirstGameSecondTeamId.collectAsState()
    val eastFirstRoundSecondGameList by nbaLeagueTournamentViewModel.eastFirstRoundSecondGameList.collectAsState()
    val eastFirstRoundSecondGameFirstTeamId by nbaLeagueTournamentViewModel.eastFirstRoundSecondGameFirstTeamId.collectAsState()
    val eastFirstRoundSecondGameSecondTeamId by nbaLeagueTournamentViewModel.eastFirstRoundSecondGameSecondTeamId.collectAsState()
    val eastFirstRoundThirdGameList by nbaLeagueTournamentViewModel.eastFirstRoundThirdGameList.collectAsState()
    val eastFirstRoundThirdGameFirstTeamId by nbaLeagueTournamentViewModel.eastFirstRoundThirdGameFirstTeamId.collectAsState()
    val eastFirstRoundThirdGameSecondTeamId by nbaLeagueTournamentViewModel.eastFirstRoundThirdGameSecondTeamId.collectAsState()
    val eastFirstRoundFourthGameList by nbaLeagueTournamentViewModel.eastFirstRoundFourthGameList.collectAsState()
    val eastFirstRoundFourthGameFirstTeamId by nbaLeagueTournamentViewModel.eastFirstRoundFourthGameFirstTeamId.collectAsState()
    val eastFirstRoundFourthGameSecondTeamId by nbaLeagueTournamentViewModel.eastFirstRoundFourthGameSecondTeamId.collectAsState()
    val westSecondRoundFirstGameList by nbaLeagueTournamentViewModel.westSecondRoundFirstGameList.collectAsState()
    val westSecondRoundFirstGameFirstTeamId by nbaLeagueTournamentViewModel.westSecondRoundFirstGameFirstTeamId.collectAsState()
    val westSecondRoundFirstGameSecondTeamId by nbaLeagueTournamentViewModel.westSecondRoundFirstGameSecondTeamId.collectAsState()
    val westSecondRoundSecondGameList by nbaLeagueTournamentViewModel.westSecondRoundSecondGameList.collectAsState()
    val westSecondRoundSecondGameFirstTeamId by nbaLeagueTournamentViewModel.westSecondRoundSecondGameFirstTeamId.collectAsState()
    val westSecondRoundSecondGameSecondTeamId by nbaLeagueTournamentViewModel.westSecondRoundSecondGameSecondTeamId.collectAsState()
    val eastSecondRoundFirstGameList by nbaLeagueTournamentViewModel.eastSecondRoundFirstGameList.collectAsState()
    val eastSecondRoundFirstGameFirstTeamId by nbaLeagueTournamentViewModel.eastSecondRoundFirstGameFirstTeamId.collectAsState()
    val eastSecondRoundFirstGameSecondTeamId by nbaLeagueTournamentViewModel.eastSecondRoundFirstGameSecondTeamId.collectAsState()
    val eastSecondRoundSecondGameList by nbaLeagueTournamentViewModel.eastSecondRoundSecondGameList.collectAsState()
    val eastSecondRoundSecondGameFirstTeamId by nbaLeagueTournamentViewModel.eastSecondRoundSecondGameFirstTeamId.collectAsState()
    val eastSecondRoundSecondGameSecondTeamId by nbaLeagueTournamentViewModel.eastSecondRoundSecondGameSecondTeamId.collectAsState()
    val westFinalRoundGameList by nbaLeagueTournamentViewModel.westFinalRoundGameList.collectAsState()
    val westFinalRoundGameFirstTeamId by nbaLeagueTournamentViewModel.westFinalRoundGameFirstTeamId.collectAsState()
    val westFinalRoundGameSecondTeamId by nbaLeagueTournamentViewModel.westFinalRoundGameSecondTeamId.collectAsState()
    val eastFinalRoundGameList by nbaLeagueTournamentViewModel.eastFinalRoundGameList.collectAsState()
    val eastFinalRoundGameFirstTeamId by nbaLeagueTournamentViewModel.eastFinalRoundGameFirstTeamId.collectAsState()
    val eastFinalRoundGameSecondTeamId by nbaLeagueTournamentViewModel.eastFinalRoundGameSecondTeamId.collectAsState()
    val finalRoundGameList by nbaLeagueTournamentViewModel.finalRoundGameList.collectAsState()
    val finalRoundGameFirstTeamId by nbaLeagueTournamentViewModel.finalRoundGameFirstTeamId.collectAsState()
    val finalRoundGameSecondTeamId by nbaLeagueTournamentViewModel.finalRoundGameSecondTeamId.collectAsState()

    CenterRow {
        // western conference
        Column {
            // western 1 round - first game
            CenterRow {
                NBALeagueTournamentRoundContainer(
                    gameList = westFirstRoundFirstGameList,
                    firstTeamId = westFirstRoundFirstGameFirstTeamId,
                    secondTeamId = westFirstRoundFirstGameSecondTeamId
                )
            }

            // western 2 round - first game
            CenterRow {
                Spacer(Modifier.width(nbaLeagueTournamentViewModel.secondRoundContainerSpace))

                NBALeagueTournamentRoundContainer(
                    gameList = westSecondRoundFirstGameList,
                    firstTeamId = westSecondRoundFirstGameFirstTeamId,
                    secondTeamId = westSecondRoundFirstGameSecondTeamId
                )
            }

            // western 1 round - second game
            CenterRow {
                NBALeagueTournamentRoundContainer(
                    gameList = westFirstRoundSecondGameList,
                    firstTeamId = westFirstRoundSecondGameFirstTeamId,
                    secondTeamId = westFirstRoundSecondGameSecondTeamId,
                    isUp = true
                )
            }

            // western final round
            CenterRow {
                Spacer(Modifier.width(nbaLeagueTournamentViewModel.finalRoundContainerSpace))

                NBALeagueTournamentRoundContainer(
                    gameList = westFinalRoundGameList,
                    firstTeamId = westFinalRoundGameFirstTeamId,
                    secondTeamId = westFinalRoundGameSecondTeamId,
                    isUp = true,
                    isFinal = true
                )
            }

            // western 1 round - third game
            CenterRow {
                NBALeagueTournamentRoundContainer(
                    gameList = westFirstRoundThirdGameList,
                    firstTeamId = westFirstRoundThirdGameFirstTeamId,
                    secondTeamId = westFirstRoundThirdGameSecondTeamId,
                )
            }

            // western 2 round - second game
            CenterRow {
                Spacer(Modifier.width(nbaLeagueTournamentViewModel.secondRoundContainerSpace))

                NBALeagueTournamentRoundContainer(
                    gameList = westSecondRoundSecondGameList,
                    firstTeamId = westSecondRoundSecondGameFirstTeamId,
                    secondTeamId = westSecondRoundSecondGameSecondTeamId,
                    isUp = true
                )
            }

            // western 1 round - fourth game
            CenterRow {
                NBALeagueTournamentRoundContainer(
                    gameList = westFirstRoundFourthGameList,
                    firstTeamId = westFirstRoundFourthGameFirstTeamId,
                    secondTeamId = westFirstRoundFourthGameSecondTeamId,
                    isUp = true
                )
            }
        }

        // final round
        NBALeagueTournamentFinalContainer(
            gameList = finalRoundGameList,
            firstTeamId = finalRoundGameFirstTeamId,
            secondTeamId = finalRoundGameSecondTeamId
        )

        // eastern conference
        Column {
            // eastern 1 round - first game
            CenterRow {
                NBALeagueTournamentRoundContainer(
                    gameList = eastFirstRoundFirstGameList,
                    firstTeamId = eastFirstRoundFirstGameFirstTeamId,
                    secondTeamId = eastFirstRoundFirstGameSecondTeamId,
                    isLeft = false
                )
            }

            // eastern 2 round - first game
            CenterRow {
                NBALeagueTournamentRoundContainer(
                    gameList = eastSecondRoundFirstGameList,
                    firstTeamId = eastSecondRoundFirstGameFirstTeamId,
                    secondTeamId = eastSecondRoundFirstGameSecondTeamId,
                    isLeft = false
                )

                Spacer(Modifier.width(nbaLeagueTournamentViewModel.secondRoundContainerSpace))
            }

            // eastern 1 round - second game
            CenterRow {
                NBALeagueTournamentRoundContainer(
                    gameList = eastFirstRoundSecondGameList,
                    firstTeamId = eastFirstRoundSecondGameFirstTeamId,
                    secondTeamId = eastFirstRoundSecondGameSecondTeamId,
                    isUp = true,
                    isLeft = false
                )
            }

            // eastern final round
            CenterRow {
                NBALeagueTournamentRoundContainer(
                    gameList = eastFinalRoundGameList,
                    firstTeamId = eastFinalRoundGameFirstTeamId,
                    secondTeamId = eastFinalRoundGameSecondTeamId,
                    isUp = true,
                    isLeft = false,
                    isFinal = true
                )

                Spacer(Modifier.width(nbaLeagueTournamentViewModel.finalRoundContainerSpace))
            }

            // eastern 1 round - third game
            CenterRow {
                NBALeagueTournamentRoundContainer(
                    gameList = eastFirstRoundThirdGameList,
                    firstTeamId = eastFirstRoundThirdGameFirstTeamId,
                    secondTeamId = eastFirstRoundThirdGameSecondTeamId,
                    isLeft = false
                )
            }

            // eastern 2 round - second game
            CenterRow {
                NBALeagueTournamentRoundContainer(
                    gameList = eastSecondRoundSecondGameList,
                    firstTeamId = eastSecondRoundSecondGameFirstTeamId,
                    secondTeamId = eastSecondRoundSecondGameSecondTeamId,
                    isUp = true,
                    isLeft = false
                )

                Spacer(Modifier.width(nbaLeagueTournamentViewModel.secondRoundContainerSpace))
            }

            // eastern 1 round - fourth game
            CenterRow {
                NBALeagueTournamentRoundContainer(
                    gameList = eastFirstRoundFourthGameList,
                    firstTeamId = eastFirstRoundFourthGameFirstTeamId,
                    secondTeamId = eastFirstRoundFourthGameSecondTeamId,
                    isUp = true,
                    isLeft = false
                )
            }
        }
    }
}

@Composable
fun NBALeagueTournamentRoundContainer(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaLeagueTournamentViewModel: NBALeagueTournamentViewModel = hiltViewModel(),
    gameList: List<NBAGame>?,
    firstTeamId: Int?,
    secondTeamId: Int?,
    isUp: Boolean = false,
    isLeft: Boolean = true,
    isFinal: Boolean = false
) {
    val recordTextHeight = 30.dp

    val density = LocalDensity.current

    var isScoreOpened by remember { mutableStateOf(false) }
    var itemSize by remember { mutableStateOf(DpSize.Zero) }

    val seriesGame = nbaLeagueTournamentViewModel.getGameSeries(gameList)
    val firstTeamRecord = if (seriesGame?.seasonSeries?.homeTeamId == firstTeamId) {
        seriesGame?.seasonSeries?.homeTeamWins
    } else {
        seriesGame?.seasonSeries?.homeTeamLosses
    }
    val secondTeamRecord = if (seriesGame?.seasonSeries?.homeTeamId == secondTeamId) {
        seriesGame?.seasonSeries?.homeTeamWins
    } else {
        seriesGame?.seasonSeries?.homeTeamLosses
    }

    CenterRow {
        if (!isLeft) {
            CenterRow(
                modifier = Modifier.height(itemSize.height)
            ) {
                if (!isFinal) {
                    CenterColumn {
                        Box(
                            Modifier
                                .weight(1f)
                                .width(1.dp)
                                .clip(RectangleShape)
                                .alpha(if (isUp) 0.7f else 0f)
                                .background(Color.Gray)
                        )
                        Box(
                            Modifier
                                .weight(1f)
                                .width(1.dp)
                                .clip(RectangleShape)
                                .alpha(if (isUp) 0f else 0.7f)
                                .background(Color.Gray)
                        )
                    }
                }

                NBALeagueTournamentHBar(nbaLeagueTournamentViewModel.hBarWidth)

                Box(
                    Modifier
                        .padding(vertical = recordTextHeight / 2)
                        .fillMaxHeight()
                        .width(1.dp)
                        .clip(RectangleShape)
                        .alpha(0.7f)
                        .background(Color.Gray)
                )

                CenterColumn {
                    CenterRow(
                        modifier = Modifier.height(recordTextHeight)
                    ) {
                        NBALeagueTournamentHBar(nbaLeagueTournamentViewModel.hBarWidth)

                        Text(
                            text = firstTeamRecord.displayOrDash,
                            color = firstTeamRecord?.let { first ->
                                secondTeamRecord?.let { second ->
                                    if (first >= second) MaterialTheme.colors.primary else Color.Black
                                }
                            } ?: Color.Black
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    CenterRow(
                        modifier = Modifier.height(recordTextHeight)
                    ) {
                        NBALeagueTournamentHBar(nbaLeagueTournamentViewModel.hBarWidth)

                        Text(
                            text = secondTeamRecord.displayOrDash,
                            color = firstTeamRecord?.let { first ->
                                secondTeamRecord?.let { second ->
                                    if (second >= first) MaterialTheme.colors.primary else Color.Black
                                }
                            } ?: Color.Black
                        )
                    }
                }
            }
        }

        CenterColumn(
            modifier = Modifier
                .width(nbaLeagueTournamentViewModel.infoContainerWidth)
                .onGloballyPositioned { layoutCoordinates ->
                    val size = layoutCoordinates.size
                    with(density) {
                        itemSize = DpSize(size.width.toDp(), size.height.toDp())
                    }
                }
                .clickable {
//                    searchViewModel.send(SearchViewModel.Intent.)
                }
        ) {
            CenterRow(
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Text(
                    text = nbaLeagueTournamentViewModel.teamNameDictionary["short_${firstTeamId}"] ?: "-",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                URLImage(
                    url = NBAUtil.teamLogoUrl(firstTeamId),
                    size = URLImageSize.SMALL,
                    isSvg = true
                )
            }

            if (isScoreOpened) {
                gameList?.let { gameList ->
                    for ((index, item) in gameList.withIndex()) {
                        val firstTeamPts = item.lineScore.find { it.teamId == firstTeamId }?.pts
                        val secondTeamPts = item.lineScore.find { it.teamId == secondTeamId }?.pts

                        item.gameSummary?.let { gameSummary ->
                            NBALeagueTournamentScoreContainer(
                                index = index + 1,
                                date = gameSummary.date,
                                firstTeamPts = firstTeamPts,
                                secondTeamPts = secondTeamPts
                            )
                        }
                    }
                }

                CenterRow(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .alpha(0.7f)
                        .clickable { isScoreOpened = !isScoreOpened }
                ) {
                    Text(
                        text = "경기결과 숨기기",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Box(
                        Modifier
                            .padding(start = 3.dp)
                            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_arrow_drop_up_24),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }
            } else {
                CenterRow(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .alpha(0.7f)
                        .clickable { isScoreOpened = !isScoreOpened }
                ) {
                    Text(
                        text = "경기결과 보기",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Box(
                        Modifier
                            .padding(start = 3.dp)
                            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_arrow_drop_down_24),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }
            }

            CenterRow(
                modifier = Modifier.padding(top = 6.dp)
            ) {
                Text(
                    text = nbaLeagueTournamentViewModel.teamNameDictionary["short_${secondTeamId}"] ?: "-",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                URLImage(
                    url = NBAUtil.teamLogoUrl(secondTeamId),
                    size = URLImageSize.SMALL,
                    isSvg = true
                )
            }
        }

        if (isLeft) {
            CenterRow(
                modifier = Modifier.height(itemSize.height)
            ) {
                CenterColumn {
                    CenterRow(
                        modifier = Modifier.height(recordTextHeight)
                    ) {
                        Text(
                            text = firstTeamRecord.displayOrDash,
                            color = firstTeamRecord?.let { first ->
                                secondTeamRecord?.let { second ->
                                    if (first >= second) MaterialTheme.colors.primary else Color.Black
                                }
                            } ?: Color.Black
                        )

                        NBALeagueTournamentHBar(nbaLeagueTournamentViewModel.hBarWidth)
                    }

                    Spacer(Modifier.weight(1f))

                    CenterRow(
                        modifier = Modifier.height(recordTextHeight)
                    ) {
                        Text(
                            text = secondTeamRecord.displayOrDash,
                            color = firstTeamRecord?.let { first ->
                                secondTeamRecord?.let { second ->
                                    if (second >= first) MaterialTheme.colors.primary else Color.Black
                                }
                            } ?: Color.Black
                        )

                        NBALeagueTournamentHBar(nbaLeagueTournamentViewModel.hBarWidth)
                    }
                }

                Box(
                    Modifier
                        .padding(vertical = recordTextHeight / 2)
                        .fillMaxHeight()
                        .width(1.dp)
                        .clip(RectangleShape)
                        .alpha(0.7f)
                        .background(Color.Gray)
                )

                NBALeagueTournamentHBar(nbaLeagueTournamentViewModel.hBarWidth)

                if (!isFinal) {
                    CenterColumn {
                        Box(
                            Modifier
                                .weight(1f)
                                .width(1.dp)
                                .clip(RectangleShape)
                                .alpha(if (isUp) 0.7f else 0f)
                                .background(Color.Gray)
                        )
                        Box(
                            Modifier
                                .weight(1f)
                                .width(1.dp)
                                .clip(RectangleShape)
                                .alpha(if (isUp) 0f else 0.7f)
                                .background(Color.Gray)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NBALeagueTournamentScoreContainer(
    nbaLeagueTournamentViewModel: NBALeagueTournamentViewModel = hiltViewModel(),
    index: Int,
    date: String,
    firstTeamPts: Int?,
    secondTeamPts: Int?
) {
    CenterColumn {
        Text(
            text = "Game ${index} - ${CalendarUtil.formatDate(date).split(" ").firstOrNull() ?: ""}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(top = 4.dp)
        )

        CenterRow {
            Text(
                text = firstTeamPts.displayOrDash,
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(30.dp),
                color = firstTeamPts?.let { first ->
                    secondTeamPts?.let { second ->
                        if (first >= second) MaterialTheme.colors.primary else Color.Black
                    }
                } ?: Color.Black
            )

            Text("vs")

            Text(
                text = secondTeamPts.displayOrDash,
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(30.dp),
                color = firstTeamPts?.let { first ->
                    secondTeamPts?.let { second ->
                        if (second >= first) MaterialTheme.colors.primary else Color.Black
                    }
                } ?: Color.Black
            )
        }
    }
}

@Composable
fun NBALeagueTournamentFinalContainer(
    nbaLeagueTournamentViewModel: NBALeagueTournamentViewModel = hiltViewModel(),
    gameList: List<NBAGame>?,
    firstTeamId: Int?,
    secondTeamId: Int?,
) {

    var isScoreOpened by remember { mutableStateOf(false) }

    val seriesGame = nbaLeagueTournamentViewModel.getGameSeries(gameList)
    val firstTeamRecord = if (seriesGame?.seasonSeries?.homeTeamId == firstTeamId) {
        seriesGame?.seasonSeries?.homeTeamWins
    } else {
        seriesGame?.seasonSeries?.homeTeamLosses
    }
    val secondTeamRecord = if (seriesGame?.seasonSeries?.homeTeamId == secondTeamId) {
        seriesGame?.seasonSeries?.homeTeamWins
    } else {
        seriesGame?.seasonSeries?.homeTeamLosses
    }

    CenterColumn {
        CenterRow(
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.End
            ) {
                CenterColumn(
                    modifier = Modifier.width(80.dp)
                ) {
                    URLImage(
                        url = NBAUtil.teamLogoUrl(firstTeamId),
                        size = URLImageSize.SMALL,
                        isSvg = true
                    )

                    Text(
                        text = nbaLeagueTournamentViewModel.teamNameDictionary["short_${firstTeamId}"] ?: "-",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Text(
                text = firstTeamRecord.displayOrDash,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(30.dp),
                color = firstTeamRecord?.let { first ->
                    secondTeamRecord?.let { second ->
                        if (first >= second) MaterialTheme.colors.primary else Color.Black
                    }
                } ?: Color.Black
            )

            Text(
                text = "vs",
                modifier = Modifier.width(30.dp)
            )

            Text(
                text = secondTeamRecord.displayOrDash,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(30.dp),
                color = firstTeamRecord?.let { first ->
                    secondTeamRecord?.let { second ->
                        if (second >= first) MaterialTheme.colors.primary else Color.Black
                    }
                } ?: Color.Black
            )

            Row(
                horizontalArrangement = Arrangement.Start
            ) {
                CenterColumn(
                    modifier = Modifier.width(80.dp)
                ) {
                    URLImage(
                        url = NBAUtil.teamLogoUrl(secondTeamId),
                        size = URLImageSize.SMALL,
                        isSvg = true
                    )

                    Text(
                        text = nbaLeagueTournamentViewModel.teamNameDictionary["short_${secondTeamId}"] ?: "-",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (isScoreOpened) {
            gameList?.let { gameList ->
                for ((index, item) in gameList.withIndex()) {
                    val firstTeamPts = item.lineScore.find { it.teamId == firstTeamId }?.pts
                    val secondTeamPts = item.lineScore.find { it.teamId == secondTeamId }?.pts

                    item.gameSummary?.let { gameSummary ->
                        NBALeagueTournamentFinalScoreContainer(
                            index = index + 1,
                            date = gameSummary.date,
                            firstTeamPts = firstTeamPts,
                            secondTeamPts = secondTeamPts
                        )
                    }
                }
            }

            CenterRow(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .alpha(0.7f)
                    .clickable { isScoreOpened = !isScoreOpened }
            ) {
                Text(
                    text = "경기결과 숨기기",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Box(
                    Modifier
                        .padding(start = 3.dp)
                        .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_arrow_drop_up_24),
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        } else {
            CenterRow(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .alpha(0.7f)
                    .clickable { isScoreOpened = !isScoreOpened }
            ) {
                Text(
                    text = "경기결과 보기",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Box(
                    Modifier
                        .padding(start = 3.dp)
                        .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_arrow_drop_down_24),
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun NBALeagueTournamentFinalScoreContainer(
    nbaLeagueTournamentViewModel: NBALeagueTournamentViewModel = hiltViewModel(),
    index: Int,
    date: String,
    firstTeamPts: Int?,
    secondTeamPts: Int?
) {
    CenterColumn {
        Text(
            text = "Game ${index} - ${CalendarUtil.formatDate(date).split(" ").firstOrNull() ?: ""}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(top = 4.dp)
        )

        CenterRow {
            Text(
                text = firstTeamPts.displayOrDash,
                textAlign = TextAlign.End,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .width(110.dp)
                    .padding(end = 10.dp),
                color = firstTeamPts?.let { first ->
                    secondTeamPts?.let { second ->
                        if (first >= second) MaterialTheme.colors.primary else Color.Black
                    }
                } ?: Color.Black
            )

            Text(
                text = "vs",
                modifier = Modifier.width(30.dp)
            )

            Text(
                text = secondTeamPts.displayOrDash,
                textAlign = TextAlign.Start,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .width(110.dp)
                    .padding(start = 10.dp),
                color = firstTeamPts?.let { first ->
                    secondTeamPts?.let { second ->
                        if (second >= first) MaterialTheme.colors.primary else Color.Black
                    }
                } ?: Color.Black
            )
        }
    }
}

@Composable
fun NBALeagueTournamentHBar(width: Dp) {
    Box(
        Modifier
            .size(width = width, height = 1.dp)
            .background(Color.Gray)
            .alpha(0.7f)
    )
}





















