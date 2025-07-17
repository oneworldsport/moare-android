package com.moare.android.features.search.display.nba.view

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
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.constants.Constants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.NBAUtil
import com.moare.android.core.util.toCm
import com.moare.android.core.util.toKg
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerInfoIntent
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerInfoViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerInfoDisplayModel
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.StatsDivider
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.CenterRow
import com.moare.android.ui.util.screenWidthDp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun NBAPlayerInfoView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    data: NBAPlayerInfoDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.NBAPlayerInfo) {
            nbaPlayerInfoViewModel.send(NBAPlayerInfoIntent.InitData(data))
        }
    }

    InfoViewContainer(
        itemCount = 9,
        measureContent = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                NBAPlayerInfoFirstItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                NBAPlayerInfoSecondItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                NBAPlayerInfoThirdItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                NBAPlayerInfoFourthItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                NBAPlayerInfoFifthItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                NBAPlayerInfoSixthItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }
            }

            NBAPlayerInfoSeventhItem { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            NBAPlayerInfoEighthItem { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            NBAPlayerInfoNinthItem { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        },
        displayContent = {
            NBAPlayerInfoFirstItem(
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBAPlayerInfoSecondItem(
                isAniItem = true,
                itemSize = itemSizes[1],
                itemPosition = itemPositions[1],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBAPlayerInfoThirdItem(
                isAniItem = true,
                itemSize = itemSizes[2],
                itemPosition = itemPositions[2],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBAPlayerInfoFourthItem(
                isAniItem = true,
                itemSize = itemSizes[3],
                itemPosition = itemPositions[3],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBAPlayerInfoFifthItem(
                isAniItem = true,
                itemSize = itemSizes[4],
                itemPosition = itemPositions[4],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBAPlayerInfoSixthItem(
                isAniItem = true,
                itemSize = itemSizes[5],
                itemPosition = itemPositions[5],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBAPlayerInfoSeventhItem(
                isAniItem = true,
                itemSize = itemSizes[6],
                itemPosition = itemPositions[6],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBAPlayerInfoEighthItem(
                isAniItem = true,
                itemSize = itemSizes[7],
                itemPosition = itemPositions[7],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBAPlayerInfoNinthItem(
                isAniItem = true,
                itemSize = itemSizes[8],
                itemPosition = itemPositions[8],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )
        }
    )
}

// photo, name
@Composable
fun NBAPlayerInfoFirstItem(
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.info

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
                url = NBAUtil.playerPhotoUrl(player.personId),
                modifier = Modifier.alpha(contentsAlpha)
            )

            Text(
                text = nbaPlayerInfoViewModel.playerNameDictionary[player.personId.toString()] ?: player.displayFirstLast,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(contentsAlpha)
            )

            Text(
                text = player.displayFirstLast,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                maxLines = 2,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}

// logo, team, name
@Composable
fun NBAPlayerInfoSecondItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.info

        MovingCapsuleItemContainer(
            isAniItem = isAniItem,
            itemSize = itemSize,
            itemPosition = itemPosition,
            aniPosition = aniPosition,
            updateItemPosition = { coordinates ->
                updateItemPosition?.let { it(1, coordinates) }
            },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = containerModifier,
            onClick = {
                searchViewModel.send(
                    SearchViewModel.Intent.SearchById(
                        id = player.teamId.toString(),
                        season = it.season,
                        category = "basketball",
                        dataType = "basketball_team_info",
                        leagueId = Constants.Ids.NBA
                    )
                )
            }
        ) {
            // TODO: "소속팀" 라벨 표시 필요
            URLImage(
                url = NBAUtil.teamLogoUrl(player.teamId),
                modifier = Modifier.alpha(contentsAlpha),
                isSvg = true
            )

            Text(
                text = nbaPlayerInfoViewModel.teamNameDictionary["full_${player.teamId}"] ?: "${player.teamCity} ${player.teamName}",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}

// jersey, position
@Composable
fun NBAPlayerInfoThirdItem(
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.info

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "등번호: ",
                    fontSize = 15.sp
                )

                Text(
                    text = player.jersey,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "포지션: ",
                    fontSize = 15.sp
                )

                Text(
                    text = player.position,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// from school/team, draft info, career info
@Composable
fun NBAPlayerInfoFourthItem(
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.info

        MovingCapsuleItemContainer(
            isAniItem = isAniItem,
            itemSize = itemSize,
            itemPosition = itemPosition,
            aniPosition = aniPosition,
            updateItemPosition = { coordinates ->
                updateItemPosition?.let { it(3, coordinates) }
            },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start,
            modifier = containerModifier
        ) {
            Column(
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "출신(학교 또는 팀): ",
                    fontSize = 15.sp
                )

                Text(
                    text = player.school,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "드래프트 순위/년도: ",
                    fontSize = 15.sp
                )

                Text(
                    text = "${player.draftNumber} / ${player.draftYear}",
                    fontWeight = FontWeight.Medium
                )
            }

            // TODO: 은퇴한 경우 -> 2010~2023(14시즌/은퇴)
            Text(
                text = buildAnnotatedString {
                    append("경력: ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                        append("${player.fromYear}~현재 (${player.seasonExp + 1}년차)")
                    }
                },
                fontSize = 15.sp,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}

// country, birth, age
@Composable
fun NBAPlayerInfoFifthItem(
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.info

        MovingCapsuleItemContainer(
            isAniItem = isAniItem,
            itemSize = itemSize,
            itemPosition = itemPosition,
            aniPosition = aniPosition,
            updateItemPosition = { coordinates ->
                updateItemPosition?.let { it(4, coordinates) }
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
                    text = "국적: ",
                    fontSize = 15.sp
                )

                Text(
                    text = player.country,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "출생: ",
                    fontSize = 15.sp
                )

                Text(
                    text = player.birthdate.split("T").firstOrNull() ?: "",
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "나이: ",
                    fontSize = 15.sp
                )

                Text(
                    text = "${CalendarUtil.calculateAge(player.birthdate)}",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// weight(kg/pound), height(cm/feet)
@Composable
fun NBAPlayerInfoSixthItem(
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.info
        val splittedPlayerHeight = player.height.split("-")
        val playerCmHeight = toCm(splittedPlayerHeight.firstOrNull()?.toIntOrNull() ?: 0, splittedPlayerHeight.lastOrNull()?.toIntOrNull() ?: 0).toInt()
        val playerKgWeight = (player.weight.toDoubleOrNull() ?: 0.0).toKg().toInt()

        MovingCapsuleItemContainer(
            isAniItem = isAniItem,
            itemSize = itemSize,
            itemPosition = itemPosition,
            aniPosition = aniPosition,
            updateItemPosition = { coordinates ->
                updateItemPosition?.let { it(5, coordinates) }
            },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start,
            modifier = containerModifier
        ) {
            Column(
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "키(cm/ft): ",
                    fontSize = 15.sp
                )

                Text(
                    text = "${playerCmHeight} / ${player.height}",
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "몸무게(kg/lb): ",
                    fontSize = 15.sp
                )

                Text(
                    text = "${playerKgWeight} / ${player.weight}",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// league stats
@Composable
fun NBAPlayerInfoSeventhItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val stats = it.stats

        MovingCapsuleItemContainer(
            isAniItem = isAniItem,
            itemSize = itemSize,
            itemPosition = itemPosition,
            aniPosition = aniPosition,
            updateItemPosition = { coordinates ->
                updateItemPosition?.let { it(6, coordinates) }
            },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(top = if (isAniItem) 0.dp else 12.dp),
            onClick = {
                searchViewModel.send(SearchViewModel.Intent.ShowPlayerStats(playerId = it.info.personId))
            }
        ) {
            NBATitle(
                leagueName = "NBA 정규시즌",
                leagueSeason = stats?.groupValue?.split("-")?.firstOrNull()?.toIntOrNull() ?: 2024,
                modifier = Modifier.alpha(contentsAlpha)
            )

            stats?.let {
                CenterRow(
                    modifier = Modifier
                        .alpha(contentsAlpha)
                ) {
                    FBStatDataItem(
                        category = "경기수",
                        data = stats.gp.toString(),
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "경기당 득점",
                        data = stats.ptsPG.toString(),
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "경기당 리바운드",
                        data = stats.rebPG.toString(),
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "경기당 어시스트",
                        data = stats.astPG.toString(),
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    // NOTE: StatsView에서 보여줘도 되지만 상세 기록을 보고 싶게 궁금증을 유발하는 용도의 데이터
                    FBStatDataItem(
                        category = "출전 경기 승률",
                        data = stats.winsPct.toString(),
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// last game
@Composable
fun NBAPlayerInfoEighthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val lastGame = it.lastGame
        val lastGamePlayerStats = it.lastGamePlayerStats

        MovingCapsuleItemContainer(
            isAniItem = isAniItem,
            itemSize = itemSize,
            itemPosition = itemPosition,
            aniPosition = aniPosition,
            updateItemPosition = { coordinates ->
                updateItemPosition?.let { it(7, coordinates) }
            },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(top = if (isAniItem) 0.dp else 12.dp),
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
                val homeTeam = lastGame.boxScoreTraditional?.homeTeam
                val awayTeam = lastGame.boxScoreTraditional?.awayTeam
                val homeTeamScore = lastGame.lineScore.find { it.teamId == homeTeam?.teamId }?.pts ?: 0
                val awayTeamScore = lastGame.lineScore.find { it.teamId == awayTeam?.teamId }?.pts ?: 0

                CenterRow(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .alpha(contentsAlpha)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .weight(0.45f)
                    ) {
                        CenterRow {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (homeTeam == null) "" else nbaPlayerInfoViewModel.teamNameDictionary["short_${homeTeam.teamId}"] ?: homeTeam.teamCity,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Light,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = " $homeTeamScore",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (homeTeamScore >= awayTeamScore) MaterialTheme.colors.primary else Color.Black
                                )
                            }

                            Text(
                                text = " - ",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "$awayTeamScore ",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (awayTeamScore >= homeTeamScore) MaterialTheme.colors.primary else Color.Black
                                )

                                Text(
                                    text = if (awayTeam == null) "" else nbaPlayerInfoViewModel.teamNameDictionary["short_${awayTeam.teamId}"] ?: awayTeam.teamCity,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Light,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = CalendarUtil.formatDate(lastGame.gameSummary?.date),
                                fontSize = 15.sp
                            )
                        }
                    }

                    CenterRow(
                        modifier = Modifier.weight(0.55f)
                    ) {
                        StatsDivider()
                        FBStatDataItem(
                            category = "출전시간",
                            data = "${
                                if (lastGamePlayerStats == null) {
                                    ""
                                } else {
                                    if (lastGamePlayerStats.position.isEmpty()) {
                                        "후보"
                                    } else {
                                        "선발"
                                    }
                                }
                            } / ${lastGamePlayerStats?.statistics?.minutes ?: ""}",
                            customCategoryFontSize = 12,
                            customDataFontSize = 15,
                            customWidth = 80.dp
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "득점",
                            data = (lastGamePlayerStats?.statistics?.points ?: 0).toString(),
                            customCategoryFontSize = 12,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "리바운드",
                            data = (lastGamePlayerStats?.statistics?.reboundsTotal ?: 0).toString(),
                            customCategoryFontSize = 12,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "어시스트",
                            data = (lastGamePlayerStats?.statistics?.assists ?: 0).toString(),
                            customCategoryFontSize = 12,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// next game
@Composable
fun NBAPlayerInfoNinthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    nbaPlayerInfoViewModel: NBAPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by nbaPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val nextGame = it.nextGame

        MovingCapsuleItemContainer(
            isAniItem = isAniItem,
            itemSize = itemSize,
            itemPosition = itemPosition,
            aniPosition = aniPosition,
            updateItemPosition = { coordinates ->
                updateItemPosition?.let { it(8, coordinates) }
            },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(top = if (isAniItem) 0.dp else 12.dp),
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
                val lastMeeting = nextGame.lastMeeting

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .alpha(contentsAlpha)
                ) {
                    Text(
                        text = if (lastMeeting?.lastGameHomeTeamId == null) "" else nbaPlayerInfoViewModel.teamNameDictionary["short_${lastMeeting.lastGameHomeTeamId}"] ?: lastMeeting.lastGameHomeTeamCity,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = " vs ",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.3f)
                    )

                    Text(
                        text = if (lastMeeting?.lastGameVisitorTeamId == null) "" else nbaPlayerInfoViewModel.teamNameDictionary["short_${lastMeeting.lastGameVisitorTeamId}"] ?: lastMeeting.lastGameVisitorTeamCity,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = CalendarUtil.formatDate(nextGame.gameSummary?.date),
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