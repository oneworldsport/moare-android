package com.moare.android.features.search.display.mlb.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.moare.android.core.constants.Constants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.KBOUtil
import com.moare.android.core.util.MLBUtil
import com.moare.android.core.util.TimeFormatType
import com.moare.android.core.util.toCm
import com.moare.android.core.util.toKg
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerInfoIntent
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerInfoViewModel
import com.moare.android.features.search.display.mlb.viewmodel.MLBPlayerInfoIntent
import com.moare.android.features.search.display.mlb.viewmodel.MLBPlayerInfoViewModel
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerInfoViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBPlayerInfoDisplayModel
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.StatsDivider
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.CenterRow

@Composable
fun MLBPlayerInfoView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbPlayerInfoViewModel: MLBPlayerInfoViewModel = hiltViewModel(),
    data: MLBPlayerInfoDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.MLBPlayerInfo) {
            mlbPlayerInfoViewModel.send(MLBPlayerInfoIntent.InitData(data))
        }
    }

    InfoViewContainer(
        itemCount = 8,
        measureContent = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                MLBPlayerInfoFirstItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                MLBPlayerInfoSecondItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                MLBPlayerInfoThirdItem(
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
                MLBPlayerInfoFourthItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                MLBPlayerInfoFifthItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }
            }

            MLBPlayerInfoSixthItem { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            MLBPlayerInfoSeventhItem { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            MLBPlayerInfoEighthItem { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        },
        displayContent = {
            MLBPlayerInfoFirstItem(
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            MLBPlayerInfoSecondItem(
                isAniItem = true,
                itemSize = itemSizes[1],
                itemPosition = itemPositions[1],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            MLBPlayerInfoThirdItem(
                isAniItem = true,
                itemSize = itemSizes[2],
                itemPosition = itemPositions[2],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            MLBPlayerInfoFourthItem(
                isAniItem = true,
                itemSize = itemSizes[3],
                itemPosition = itemPositions[3],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            MLBPlayerInfoFifthItem(
                isAniItem = true,
                itemSize = itemSizes[4],
                itemPosition = itemPositions[4],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            MLBPlayerInfoSixthItem(
                isAniItem = true,
                itemSize = itemSizes[5],
                itemPosition = itemPositions[5],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            MLBPlayerInfoSeventhItem(
                isAniItem = true,
                itemSize = itemSizes[6],
                itemPosition = itemPositions[6],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            MLBPlayerInfoEighthItem(
                isAniItem = true,
                itemSize = itemSizes[7],
                itemPosition = itemPositions[7],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )
        }
    )
}

// photo, name
@Composable
fun MLBPlayerInfoFirstItem(
    mlbPlayerInfoViewModel: MLBPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbPlayerInfoViewModel.displayModel.collectAsState()

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
                url = MLBUtil.playerPhotoUrl(player.id),
                modifier = Modifier.alpha(contentsAlpha)
            )

            Text(
                text = mlbPlayerInfoViewModel.playerNameDictionary["${player.id}"] ?: player.fullName,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(contentsAlpha)
            )

            Text(
                text = player.fullName,
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
fun MLBPlayerInfoSecondItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbPlayerInfoViewModel: MLBPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
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
                        id = it.teamId.toString(),
                        season = it.season,
                        category = "baseball",
                        dataType = "baseball_team_info",
                        leagueId = Constants.Ids.MLB
                    )
                )
            }
        ) {
            // TODO: "소속팀" 라벨 표시 필요
            URLImage(
                url = MLBUtil.teamLogoUrl(it.teamId),
                modifier = Modifier.alpha(contentsAlpha),
                isSvg = true
            )

            Text(
                text = mlbPlayerInfoViewModel.teamNameDictionary["full_${it.teamId}"] ?: "",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}

// jersey, position, debut
@Composable
fun MLBPlayerInfoThirdItem(
    mlbPlayerInfoViewModel: MLBPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbPlayerInfoViewModel.displayModel.collectAsState()

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
                    text = player.primaryNumber,
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
                    text = MLBUtil.getPositionName(player.primaryPosition.abbreviation),
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "데뷔년도: ",
                    fontSize = 15.sp
                )

                Text(
                    text = player.mlbDebutDate.split("-").firstOrNull() ?: "2025",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// country, birth, age
@Composable
fun MLBPlayerInfoFourthItem(
    mlbPlayerInfoViewModel: MLBPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbPlayerInfoViewModel.displayModel.collectAsState()

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
            Text(
                text = buildAnnotatedString {
                    append("국적: ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                        append(player.birthCountry)
                    }
                },
                fontSize = 15.sp,
                modifier = Modifier.alpha(contentsAlpha)
            )

            Text(
                text = buildAnnotatedString {
                    append("출생: ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                        append(player.birthDate)
                    }
                },
                fontSize = 15.sp,
                modifier = Modifier.alpha(contentsAlpha)
            )

            Text(
                text = buildAnnotatedString {
                    append("나이: ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                        append("${CalendarUtil.calculateAge(player.birthDate)}")
                    }
                },
                fontSize = 15.sp,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}

// weight(kg/pound), height(cm/feet)
@Composable
fun MLBPlayerInfoFifthItem(
    mlbPlayerInfoViewModel: MLBPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.info
        val playerKgWeight = player.weight.toKg().toInt()

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
            Text(
                text = buildAnnotatedString {
                    append("키(cm/ft): ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                        append("${MLBUtil.changeToCm(player.height)} / ${player.height}")
                    }
                },
                fontSize = 15.sp,
                modifier = Modifier.alpha(contentsAlpha)
            )

            Text(
                text = buildAnnotatedString {
                    append("몸무게(kg/lb): ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                        append("${playerKgWeight} / ${player.weight}")
                    }
                },
                fontSize = 15.sp,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}

// league stats
@Composable
fun MLBPlayerInfoSixthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbPlayerInfoViewModel: MLBPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val stats = it.stats
        val season = stats?.fielding?.season
            ?: stats?.hitting?.season
            ?: stats?.pitching?.season
            ?: stats?.catching?.season

        MovingCapsuleItemContainer(
            isAniItem = isAniItem,
            itemSize = itemSize,
            itemPosition = itemPosition,
            aniPosition = aniPosition,
            updateItemPosition = { coordinates ->
                updateItemPosition?.let { it(5, coordinates) }
            },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(top = if (isAniItem) 0.dp else 12.dp),
            onClick = {
                searchViewModel.send(SearchViewModel.Intent.ShowPlayerStats(playerId = it.info.id))
            }
        ) {
            BaseballLeagueTitle(
                url = MLBUtil.mlbLogoUrl,
                leagueName = "MLB",
                leagueSeason = season?.toIntOrNull() ?: 2025,
                modifier = Modifier.alpha(contentsAlpha)
            )

            stats?.hitting?.stat?.let {
                CenterRow(
                    modifier = Modifier
                        .alpha(contentsAlpha)
                ) {
                    FBStatDataItem(
                        category = "경기수",
                        data = it.gamesPlayed.toString(),
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "타율",
                        data = it.avg,
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "홈런",
                        data = it.homeRuns.toString(),
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "ops",
                        data = it.ops,
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "도루",
                        data = it.stolenBases.toString(),
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            stats?.pitching?.stat?.let {
                CenterRow(
                    modifier = Modifier
                        .alpha(contentsAlpha)
                ) {
                    FBStatDataItem(
                        category = "경기수",
                        data = it.gamesPitched.toString(),
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "평균자책점",
                        data = it.era,
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "피안타율",
                        data = it.avg,
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "승리",
                        data = it.wins.toString(),
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )
                    StatsDivider()
                    FBStatDataItem(
                        category = "이닝당 평균 투구수",
                        data = it.pitchesPerInning,
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
fun MLBPlayerInfoSeventhItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbPlayerInfoViewModel: MLBPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val lastGame = it.lastGame
        val lastGamePlayerHitterStats = it.lastGamePlayerStats?.stats?.batting
        val lastGamePlayerPitcherStats = it.lastGamePlayerStats?.stats?.pitching

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
                val homeTeamScore = it.linescore?.teams?.home?.runs ?: 0
                val awayTeamScore = it.linescore?.teams?.away?.runs ?: 0

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
                                    text = mlbPlayerInfoViewModel.teamNameDictionary["short_${it.teams.home.id}"] ?: "",
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
                                    text = mlbPlayerInfoViewModel.teamNameDictionary["short_${it.teams.away.id}"] ?: "",
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
                                text = CalendarUtil.formatDate(it.gameInfo.gameDate, TimeFormatType.AMPM_WITH_DAY_OF_WEEK_DATE),
                                fontSize = 15.sp
                            )
                        }
                    }

                    // NOTE: lastGamePlayerHitterStats, lastGamePlayerPitcherStats가 null인 경우는 없어서 안에 있는 기본 데이터로 해당 선수 기록 보여줘야할지 판단
                    if (lastGamePlayerHitterStats?._atBats != null && lastGamePlayerPitcherStats?._numberOfPitches == null) {
                        CenterRow(
                            modifier = Modifier.weight(0.55f)
                        ) {
                            StatsDivider()
                            FBStatDataItem(
                                category = "타수",
                                data = lastGamePlayerHitterStats.atBats.toString(),
                                customCategoryFontSize = 12,
                                modifier = Modifier.weight(1f)
                            )
                            StatsDivider()
                            FBStatDataItem(
                                category = "안타",
                                data = lastGamePlayerHitterStats.hits.toString(),
                                customCategoryFontSize = 12,
                                modifier = Modifier.weight(1f)
                            )
                            StatsDivider()
                            FBStatDataItem(
                                category = "득점",
                                data = lastGamePlayerHitterStats.runs.toString(),
                                customCategoryFontSize = 12,
                                modifier = Modifier.weight(1f)
                            )
                            StatsDivider()
                            FBStatDataItem(
                                category = "타점",
                                data = lastGamePlayerHitterStats.rbi.toString(),
                                customCategoryFontSize = 12,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else if (lastGamePlayerPitcherStats?._numberOfPitches != null && lastGamePlayerHitterStats?._atBats == null) {
                        CenterRow(
                            modifier = Modifier.weight(0.55f)
                        ) {
                            StatsDivider()
                            FBStatDataItem(
                                category = "이닝",
                                data = lastGamePlayerPitcherStats.inningsPitched,
                                customCategoryFontSize = 12,
                                modifier = Modifier.weight(1f)
                            )
                            StatsDivider()
                            FBStatDataItem(
                                category = "삼진",
                                data = lastGamePlayerPitcherStats.strikeOuts.toString(),
                                customCategoryFontSize = 12,
                                modifier = Modifier.weight(1f)
                            )
                            StatsDivider()
                            FBStatDataItem(
                                category = "볼넷",
                                data = lastGamePlayerPitcherStats.baseOnBalls.toString(),
                                customCategoryFontSize = 12,
                                modifier = Modifier.weight(1f)
                            )
                            StatsDivider()
                            FBStatDataItem(
                                category = "실점",
                                data = lastGamePlayerPitcherStats.runs.toString(),
                                customCategoryFontSize = 12,
                                modifier = Modifier.weight(1f)
                            )
                            StatsDivider()
                            FBStatDataItem(
                                category = "자책점",
                                data = lastGamePlayerPitcherStats.earnedRuns.toString(),
                                customCategoryFontSize = 12,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// next game
@Composable
fun MLBPlayerInfoEighthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbPlayerInfoViewModel: MLBPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val nextGame = it.nextGame

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
                        .padding(horizontal = 4.dp)
                        .alpha(contentsAlpha)
                ) {
                    Text(
                        text = mlbPlayerInfoViewModel.teamNameDictionary["short_${nextGame.teams.home.id}"] ?: "",
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
                        text = mlbPlayerInfoViewModel.teamNameDictionary["short_${nextGame.teams.away.id}"] ?: "",
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start,
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