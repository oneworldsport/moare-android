package com.moare.android.features.search.display.mlb.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.MLBUtil
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamInfoIntent
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamInfoViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamInfoDisplayModel
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.StatsDivider
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.CenterRow

@Composable
fun MLBTeamInfoView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbTeamInfoViewModel: MLBTeamInfoViewModel = hiltViewModel(),
    data: MLBTeamInfoDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.MLBTeamInfo) {
            mlbTeamInfoViewModel.send(MLBTeamInfoIntent.InitData(data))
        }
    }

    InfoViewContainer(
        itemCount = 6,
        measureContent = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                MLBTeamInfoFirstItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                MLBTeamInfoSecondItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                MLBTeamInfoThirdItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }
            }

            MLBTeamInfoFourthItem { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            ) {
                MLBTeamInfoFifthItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                MLBTeamInfoSixthItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }
            }
        },
        displayContent = {
            MLBTeamInfoFirstItem(
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            MLBTeamInfoSecondItem(
                isAniItem = true,
                itemSize = itemSizes[1],
                itemPosition = itemPositions[1],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            MLBTeamInfoThirdItem(
                isAniItem = true,
                itemSize = itemSizes[2],
                itemPosition = itemPositions[2],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            MLBTeamInfoFourthItem(
                isAniItem = true,
                itemSize = itemSizes[3],
                itemPosition = itemPositions[3],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            MLBTeamInfoFifthItem(
                isAniItem = true,
                itemSize = itemSizes[4],
                itemPosition = itemPositions[4],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            MLBTeamInfoSixthItem(
                isAniItem = true,
                itemSize = itemSizes[5],
                itemPosition = itemPositions[5],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )
        }
    )
}

// logo, team, name
@Composable
fun MLBTeamInfoFirstItem(
    mlbTeamInfoViewModel: MLBTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val team = it.team

        MovingCapsuleItemContainer(
            isAniItem = isAniItem,
            itemSize = itemSize,
            itemPosition = itemPosition,
            aniPosition = aniPosition,
            updateItemPosition = { coordinates ->
                updateItemPosition?.let { it(0, coordinates) }
            },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = containerModifier
        ) {
            URLImage(
                url = MLBUtil.teamLogoUrl(team.id),
                modifier = Modifier.alpha(contentsAlpha),
                isSvg = true
            )

            Text(
                text = mlbTeamInfoViewModel.teamNameDictionary["full_${team.id}"] ?: team.teamName,
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
}

// founded, city, conference, division
@Composable
fun MLBTeamInfoSecondItem(
    mlbTeamInfoViewModel: MLBTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val team = it.team

        MovingCapsuleItemContainer(
            isAniItem = isAniItem,
            itemSize = itemSize,
            itemPosition = itemPosition,
            aniPosition = aniPosition,
            updateItemPosition = { coordinates ->
                updateItemPosition?.let { it(1, coordinates) }
            },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start,
            modifier = containerModifier
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "창단연도: ",
                    fontSize = 15.sp
                )

                Text(
                    text = team.firstYearOfPlay,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = buildAnnotatedString {
                    append("연고지: ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                        append(team.locationName)
                    }
                },
                fontSize = 15.sp,
                modifier = Modifier.alpha(contentsAlpha)
            )

            Column(
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "컨퍼런스/디비전: ",
                    fontSize = 15.sp
                )

                Text(
                    text = "${MLBUtil.leagueDivisionMap[team.league.id]} / ${MLBUtil.leagueDivisionMap[team.division.id]}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// venue
@Composable
fun MLBTeamInfoThirdItem(
    mlbTeamInfoViewModel: MLBTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val team = it.team
        val venue = it.venue

        MovingCapsuleItemContainer(
            isAniItem = isAniItem,
            itemSize = itemSize,
            itemPosition = itemPosition,
            aniPosition = aniPosition,
            updateItemPosition = { coordinates ->
                updateItemPosition?.let { it(2, coordinates) }
            },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start,
            modifier = containerModifier
        ) {
            Text(
                text = buildAnnotatedString {
                    append("홈구장: ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                        append(mlbTeamInfoViewModel.teamNameDictionary["venue_${team.id}"] ?: venue.name)
                    }
                },
                fontSize = 15.sp,
                modifier = Modifier.alpha(contentsAlpha)
            )

//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.alpha(contentsAlpha)
//            ) {
//                Text(
//                    text = "좌석수: ",
//                    fontSize = 15.sp
//                )
//
//                Text(
//                    text = venue.capacity.toString(),
//                    fontWeight = FontWeight.Medium
//                )
//            }

//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.alpha(contentsAlpha)
//            ) {
//                Text(
//                    text = "개장: ",
//                    fontSize = 15.sp
//                )
//
//                Text(
//                    text = venue.opened.toString(),
//                    fontWeight = FontWeight.Medium
//                )
//            }
        }
    }
}

// league stats
@Composable
fun MLBTeamInfoFourthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbTeamInfoViewModel: MLBTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val team = it.team
        val stats = it.stats

        MovingCapsuleItemContainer(
            isAniItem = isAniItem,
            itemSize = itemSize,
            itemPosition = itemPosition,
            aniPosition = aniPosition,
            updateItemPosition = { coordinates ->
                updateItemPosition?.let { it(3, coordinates) }
            },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(top = if (isAniItem) 0.dp else 12.dp),
            onClick = {
                searchViewModel.send(SearchViewModel.Intent.ShowTeamStats(teamId = it.team.id))
            }
        ) {
            BaseballLeagueTitle(
                url = MLBUtil.mlbLogoUrl,
                leagueName = "MLB",
                leagueSeason = team.season,
                modifier = Modifier
                    .alpha(contentsAlpha)
            )

            stats?.recordData?.let {
                CenterRow(
                    modifier = Modifier
                        .alpha(contentsAlpha)
                ) {
                    FBStatDataItem(
//                        category = "${team.division.name}디비전 순위",
                        category = "디비전 순위",
                        data = it.divisionRank,
                        customCategoryFontSize = 13,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "승",
                        data = it.wins.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "패",
                        data = it.losses.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "무",
                        data = it.leagueRecord.ties.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "타율",
                        data = stats.hitting?.avg ?: "0.0",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// last game stats
@Composable
fun MLBTeamInfoFifthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbTeamInfoViewModel: MLBTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val lastGame = it.lastGame

        MovingCapsuleItemContainer(
            isAniItem = isAniItem,
            itemSize = itemSize,
            itemPosition = itemPosition,
            aniPosition = aniPosition,
            updateItemPosition = { coordinates ->
                updateItemPosition?.let { it(4, coordinates) }
            },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = containerModifier,
            onClick = {
                searchViewModel.send(SearchViewModel.Intent.ShowGameStats(gameType = "previous"))
            }
        ) {
            Text(
                text = "최근경기",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(contentsAlpha)
            )

            lastGame?.let {
                val homeTeamScore = it.linescore.teams.home.runs
                val awayTeamScore = it.linescore.teams.away.runs

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .alpha(contentsAlpha)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = mlbTeamInfoViewModel.teamNameDictionary["short_${it.teams.home.id}"] ?: "",
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = " $homeTeamScore",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = if ((homeTeamScore) >= (awayTeamScore)) MaterialTheme.colors.primary else Color.Black
                        )
                    }

                    Text(
                        text = " - ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "$homeTeamScore ",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = if ((awayTeamScore) >= (homeTeamScore)) MaterialTheme.colors.primary else Color.Black
                        )

                        Text(
                            text = mlbTeamInfoViewModel.teamNameDictionary["short_${it.teams.away.id}"] ?: "",
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Text(
                    text = CalendarUtil.formatDate(it.gameInfo.gameDate, TimeFormatType.AMPM_WITH_DAY_OF_WEEK_DATE),
                    fontSize = 15.sp,
                    modifier = Modifier.alpha(contentsAlpha)
                )
            }
        }
    }
}

// next game stats
@Composable
fun MLBTeamInfoSixthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbTeamInfoViewModel: MLBTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val nextGame = it.nextGame

        MovingCapsuleItemContainer(
            isAniItem = isAniItem,
            itemSize = itemSize,
            itemPosition = itemPosition,
            aniPosition = aniPosition,
            updateItemPosition = { coordinates ->
                updateItemPosition?.let { it(5, coordinates) }
            },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = containerModifier,
            onClick = {
                searchViewModel.send(SearchViewModel.Intent.ShowGameStats(gameType = "next"))
            }
        ) {
            Text(
                text = "다음경기",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(contentsAlpha)
            )

            if (nextGame != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .alpha(contentsAlpha)
                ) {
                    Text(
                        text = mlbTeamInfoViewModel.teamNameDictionary["short_${nextGame.teams.home.id}"] ?: "",
                        fontSize = 15.sp,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "  vs  ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = mlbTeamInfoViewModel.teamNameDictionary["short_${nextGame.teams.away.id}"] ?: "",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = CalendarUtil.formatDate(nextGame.gameInfo.gameDate, TimeFormatType.AMPM_WITH_DAY_OF_WEEK_DATE),
                    fontSize = 15.sp,
                    modifier = Modifier.alpha(contentsAlpha)
                )
            } else {
                Text(
                    text = "예정된 경기가 없습니다.",
                    fontSize = 15.sp,
                    modifier = Modifier.alpha(contentsAlpha)
                )
            }
        }
    }
}