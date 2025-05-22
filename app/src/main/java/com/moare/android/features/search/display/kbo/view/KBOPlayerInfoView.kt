package com.moare.android.features.search.display.kbo.view

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.KBOUtil
import com.moare.android.core.util.NBAUtil
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerInfoIntent
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerInfoViewModel
import com.moare.android.features.search.display.nba.view.NBAPlayerInfoEighthItem
import com.moare.android.features.search.display.nba.view.NBAPlayerInfoFifthItem
import com.moare.android.features.search.display.nba.view.NBAPlayerInfoFirstItem
import com.moare.android.features.search.display.nba.view.NBAPlayerInfoFourthItem
import com.moare.android.features.search.display.nba.view.NBAPlayerInfoNinthItem
import com.moare.android.features.search.display.nba.view.NBAPlayerInfoSecondItem
import com.moare.android.features.search.display.nba.view.NBAPlayerInfoSeventhItem
import com.moare.android.features.search.display.nba.view.NBAPlayerInfoSixthItem
import com.moare.android.features.search.display.nba.view.NBAPlayerInfoThirdItem
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerInfoIntent
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerInfoViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerInfoDisplayModel
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.URLImage

@Composable
fun KBOPlayerInfoView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboPlayerInfoViewModel: KBOPlayerInfoViewModel = hiltViewModel(),
    data: KBOPlayerInfoDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.KBOPlayerInfo) {
            kboPlayerInfoViewModel.send(KBOPlayerInfoIntent.InitData(data))
        }
    }

    InfoViewContainer(
        itemCount = 8,
        measureContent = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                KBOPlayerInfoFirstItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                KBOPlayerInfoSecondItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                KBOPlayerInfoThirdItem(
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
                KBOPlayerInfoFourthItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                KBOPlayerInfoFifthItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }
            }

            KBOPlayerInfoSixthItem { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            KBOPlayerInfoSeventhItem { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            KBOPlayerInfoEighthItem { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        },
        displayContent = {
            KBOPlayerInfoFirstItem(
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOPlayerInfoSecondItem(
                isAniItem = true,
                itemSize = itemSizes[1],
                itemPosition = itemPositions[1],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOPlayerInfoThirdItem(
                isAniItem = true,
                itemSize = itemSizes[2],
                itemPosition = itemPositions[2],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOPlayerInfoFourthItem(
                isAniItem = true,
                itemSize = itemSizes[3],
                itemPosition = itemPositions[3],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOPlayerInfoFifthItem(
                isAniItem = true,
                itemSize = itemSizes[4],
                itemPosition = itemPositions[4],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOPlayerInfoSixthItem(
                isAniItem = true,
                itemSize = itemSizes[5],
                itemPosition = itemPositions[5],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOPlayerInfoSeventhItem(
                isAniItem = true,
                itemSize = itemSizes[6],
                itemPosition = itemPositions[6],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOPlayerInfoEighthItem(
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
fun KBOPlayerInfoFirstItem(
    kboPlayerInfoViewModel: KBOPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by kboPlayerInfoViewModel.displayModel.collectAsState()

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
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                HCapsuleBar()
            }

            URLImage(
                url = KBOUtil.playerPhotoUrl(player.id),
                modifier = Modifier.alpha(contentsAlpha)
            )

            Text(
                text = player.name,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}

// logo, team, name
@Composable
fun KBOPlayerInfoSecondItem(
    kboPlayerInfoViewModel: KBOPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by kboPlayerInfoViewModel.displayModel.collectAsState()

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
            modifier = containerModifier
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                HCapsuleBar()
            }

            // TODO: "소속팀" 라벨 표시 필요
            URLImage(
                url = KBOUtil.teamLogoUrl(player.teamId),
                modifier = Modifier.alpha(contentsAlpha),
                isSvg = true
            )

            Text(
                text = kboPlayerInfoViewModel.teamNameDictionary["full_${player.teamId}"] ?: "",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}

// jersey, position
@Composable
fun KBOPlayerInfoThirdItem(
    kboPlayerInfoViewModel: KBOPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by kboPlayerInfoViewModel.displayModel.collectAsState()

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
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                HCapsuleBar()
            }

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

// career info
@Composable
fun KBOPlayerInfoFourthItem(
    kboPlayerInfoViewModel: KBOPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by kboPlayerInfoViewModel.displayModel.collectAsState()

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
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                HCapsuleBar()
            }

            Column(
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "드래프트: ",
                    fontSize = 15.sp
                )

                Text(
                    text = player.draftRound,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "경력: ",
                    fontSize = 15.sp
                )

                Text(
                    text = "${KBOUtil.getFullYear(player.fromYear)}~현재 (${KBOUtil.calculateYear(player.fromYear)}년차)",
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "연봉: ",
                    fontSize = 15.sp
                )

                Text(
                    text = KBOUtil.formatMoney(player.salary),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// birth, age, height, weight
@Composable
fun KBOPlayerInfoFifthItem(
    kboPlayerInfoViewModel: KBOPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by kboPlayerInfoViewModel.displayModel.collectAsState()

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
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                HCapsuleBar()
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
                    text = player.birthdate,
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "키: ",
                    fontSize = 15.sp
                )

                Text(
                    text = player.height,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "몸무게: ",
                    fontSize = 15.sp
                )

                Text(
                    text = player.weight,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// league stats
@Composable
fun KBOPlayerInfoSixthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboPlayerInfoViewModel: KBOPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by kboPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val stats = it.stats

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
            HCapsuleBar()

            BaseballLeagueTitle(
                url = KBOUtil.kboLogoUrl,
                leagueName = "KBO",
                leagueSeason = stats?.season ?: 2025,
                modifier = Modifier.alpha(contentsAlpha)
            )

            stats?.hitter?.let {
                Row(
                    modifier = Modifier
                        .alpha(contentsAlpha)
                ) {
                    FBStatDataItem(
                        category = "경기수",
                        data = it.g,
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )

                    FBStatDataItem(
                        category = "타율",
                        data = it.avg,
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )

                    FBStatDataItem(
                        category = "홈런",
                        data = it.hr,
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )

                    FBStatDataItem(
                        category = "ops",
                        data = it.ops,
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )

                    FBStatDataItem(
                        category = "도루",
                        data = it.sb,
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            stats?.pitcher?.let {
                Row(
                    modifier = Modifier
                        .alpha(contentsAlpha)
                ) {
                    FBStatDataItem(
                        category = "경기수",
                        data = it.g,
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )

                    FBStatDataItem(
                        category = "평균자책점",
                        data = it.era,
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )

                    FBStatDataItem(
                        category = "피안타율",
                        data = it.avg,
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )

                    FBStatDataItem(
                        category = "승리",
                        data = it.w,
                        customCategoryFontSize = 11,
                        modifier = Modifier.weight(1f)
                    )

                    FBStatDataItem(
                        category = "경기당 평균 투구수",
                        data = "${it.npsPG}",
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
fun KBOPlayerInfoSeventhItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboPlayerInfoViewModel: KBOPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by kboPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val lastGame = it.lastGame
        val lastGamePlayerHitterStats = it.lastGamePlayerHitterStats
        val lastGamePlayerPitcherStats = it.lastGamePlayerPitcherStats

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
            HCapsuleBar()

            Text(
                text = "최근경기",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(contentsAlpha)
            )

            lastGame?.let {
                val homeTeamScore = it.lineScore.home.r.toInt()
                val awayTeamScore = it.lineScore.away.r.toInt()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .alpha(contentsAlpha)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(25.dp)
                        ) {
                            Text(
                                text = kboPlayerInfoViewModel.teamNameDictionary["short_${it.gameInfo?.homeTeamId}"] ?: "",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Light,
                                maxLines = 1
                            )

                            Text(
                                text = homeTeamScore.toString(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (homeTeamScore >= awayTeamScore) MaterialTheme.colors.primary else Color.Black
                            )

                            Text(
                                text = " vs ",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = awayTeamScore.toString(),
                                fontWeight = FontWeight.Medium,
                                color = if (awayTeamScore >= homeTeamScore) MaterialTheme.colors.primary else Color.Black
                            )

                            Text(
                                text = kboPlayerInfoViewModel.teamNameDictionary["short_${it.gameInfo?.awayTeamId}"] ?: "",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Light,
                                maxLines = 1
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(
                                text = CalendarUtil.formatDate(it.gameInfo?.date),
                                fontSize = 15.sp
                            )
                        }
                    }

                    if (lastGamePlayerHitterStats != null && lastGamePlayerPitcherStats == null) {
                        FBStatDataItem(
                            category = "타수",
                            data = lastGamePlayerHitterStats.ab,
                            customCategoryFontSize = 12,
                            modifier = Modifier.weight(1f)
                        )

                        FBStatDataItem(
                            category = "안타",
                            data = lastGamePlayerHitterStats.h,
                            customCategoryFontSize = 12,
                            modifier = Modifier.weight(1f)
                        )

                        FBStatDataItem(
                            category = "득점",
                            data = lastGamePlayerHitterStats.r,
                            customCategoryFontSize = 12,
                            modifier = Modifier.weight(1f)
                        )

                        FBStatDataItem(
                            category = "타점",
                            data = lastGamePlayerHitterStats.rbi,
                            customCategoryFontSize = 12,
                            modifier = Modifier.weight(1f)
                        )
                    } else if (lastGamePlayerPitcherStats != null && lastGamePlayerHitterStats == null) {
                        FBStatDataItem(
                            category = "이닝",
                            data = lastGamePlayerPitcherStats.ip,
                            customCategoryFontSize = 12,
                            modifier = Modifier.weight(1f)
                        )

                        FBStatDataItem(
                            category = "삼진",
                            data = lastGamePlayerPitcherStats.so,
                            customCategoryFontSize = 12,
                            modifier = Modifier.weight(1f)
                        )

                        FBStatDataItem(
                            category = "볼넷",
                            data = lastGamePlayerPitcherStats.bb,
                            customCategoryFontSize = 12,
                            modifier = Modifier.weight(1f)
                        )

                        FBStatDataItem(
                            category = "실점",
                            data = lastGamePlayerPitcherStats.r,
                            customCategoryFontSize = 12,
                            modifier = Modifier.weight(1f)
                        )

                        FBStatDataItem(
                            category = "자책점",
                            data = lastGamePlayerPitcherStats.er,
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
fun KBOPlayerInfoEighthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboPlayerInfoViewModel: KBOPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by kboPlayerInfoViewModel.displayModel.collectAsState()

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
            HCapsuleBar()

            Text(
                text = "다음경기",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(contentsAlpha)
            )

            nextGame?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .alpha(contentsAlpha)
                ) {
                    Text(
                        text = kboPlayerInfoViewModel.teamNameDictionary["short_${it.gameInfo?.homeTeamId}"] ?: "",
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
                        text = kboPlayerInfoViewModel.teamNameDictionary["short_${it.gameInfo?.awayTeamId}"] ?: "",
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = CalendarUtil.formatDate(it.gameInfo?.date),
                    fontSize = 15.sp,
                    modifier = Modifier.alpha(contentsAlpha)
                )
            }
        }
    }
}