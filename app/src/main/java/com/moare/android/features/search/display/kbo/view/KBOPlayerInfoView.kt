package com.moare.android.features.search.display.kbo.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.moare.android.core.constants.Constants
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.KBOUtil
import com.moare.android.core.util.TimeFormatType
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerInfoAction
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerInfoStore
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamInfoAction
import com.moare.android.features.search.display.search.viewmodel.SearchAction
import com.moare.android.features.search.display.search.viewmodel.SearchStore
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.StatsDivider
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.CenterRow

@Composable
fun KBOPlayerInfoView(
    searchStore: SearchStore,
    store: KBOPlayerInfoStore
) {
    InfoViewContainer(
        searchStore = searchStore,
        itemCount = 8,
        measureContent = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                KBOPlayerInfoFirstItem(
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                KBOPlayerInfoSecondItem(
                    searchStore = searchStore,
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                KBOPlayerInfoThirdItem(
                    store = store,
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
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                KBOPlayerInfoFifthItem(
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }
            }

            KBOPlayerInfoSixthItem(searchStore, store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            KBOPlayerInfoSeventhItem(searchStore, store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            KBOPlayerInfoEighthItem(searchStore, store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        },
        displayContent = {
            KBOPlayerInfoFirstItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOPlayerInfoSecondItem(
                searchStore = searchStore,
                store = store,
                isAniItem = true,
                itemSize = itemSizes[1],
                itemPosition = itemPositions[1],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOPlayerInfoThirdItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[2],
                itemPosition = itemPositions[2],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOPlayerInfoFourthItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[3],
                itemPosition = itemPositions[3],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOPlayerInfoFifthItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[4],
                itemPosition = itemPositions[4],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOPlayerInfoSixthItem(
                searchStore = searchStore,
                store = store,
                isAniItem = true,
                itemSize = itemSizes[5],
                itemPosition = itemPositions[5],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOPlayerInfoSeventhItem(
                searchStore = searchStore,
                store = store,
                isAniItem = true,
                itemSize = itemSizes[6],
                itemPosition = itemPositions[6],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOPlayerInfoEighthItem(
                searchStore = searchStore,
                store = store,
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
    store: KBOPlayerInfoStore,
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by store.displayModel.collectAsState()

    val player = displayModel.info

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

// logo, team, name
@Composable
fun KBOPlayerInfoSecondItem(
    searchStore: SearchStore,
    store: KBOPlayerInfoStore,
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by store.displayModel.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val player = displayModel.info

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
            searchStore.send(
                SearchAction.SearchById(
                    id = player.teamId.toString(),
                    season = displayModel.season,
                    category = "baseball",
                    dataType = "baseball_team_info",
                    leagueId = Constants.Ids.KBO
                )
            )
        }
    ) {
        // TODO: "소속팀" 라벨 표시 필요
        URLImage(
            url = KBOUtil.teamLogoUrl(player.teamId),
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = teamNameDic["full_${player.teamId}"] ?: "",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.alpha(contentsAlpha)
        )
    }
}

// jersey, position
@Composable
fun KBOPlayerInfoThirdItem(
    store: KBOPlayerInfoStore,
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by store.displayModel.collectAsState()

    val player = displayModel.info

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

        Text(
            text = buildAnnotatedString {
                append("포지션: ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                    append(player.position)
                }
            },
            fontSize = 15.sp,
            modifier = Modifier.alpha(contentsAlpha)
        )
    }
}

// career info
@Composable
fun KBOPlayerInfoFourthItem(
    store: KBOPlayerInfoStore,
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by store.displayModel.collectAsState()

    val player = displayModel.info

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
                append("드래프트: ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                    append(player.draftRound)
                }
            },
            fontSize = 15.sp,
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = buildAnnotatedString {
                append("경력: ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                    append("${KBOUtil.getFullYear(player.fromYear)}~현재 (${KBOUtil.calculateYear(player.fromYear)}년차)")
                }
            },
            fontSize = 15.sp,
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = buildAnnotatedString {
                append("연봉: ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                    append(KBOUtil.formatMoney(player.salary))
                }
            },
            fontSize = 15.sp,
            modifier = Modifier.alpha(contentsAlpha)
        )
    }
}

// birth, age, height, weight
@Composable
fun KBOPlayerInfoFifthItem(
    store: KBOPlayerInfoStore,
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by store.displayModel.collectAsState()

    val player = displayModel.info

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

// league stats
@Composable
fun KBOPlayerInfoSixthItem(
    searchStore: SearchStore,
    store: KBOPlayerInfoStore,
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by store.displayModel.collectAsState()

    val stats = displayModel.stats

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
            store.send(KBOPlayerInfoAction.ShowPlayerStats)
        }
    ) {
        BaseballLeagueTitle(
            url = KBOUtil.kboLogoUrl,
            leagueName = "KBO",
            leagueSeason = stats?.season ?: 2025,
            modifier = Modifier.alpha(contentsAlpha)
        )

        stats?.hitter?.let {
            CenterRow(
                modifier = Modifier
                    .alpha(contentsAlpha)
            ) {
                FBStatDataItem(
                    category = "경기수",
                    data = it.g,
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
                    data = it.hr,
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
                    data = it.sb,
                    customCategoryFontSize = 11,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        stats?.pitcher?.let {
            CenterRow(
                modifier = Modifier
                    .alpha(contentsAlpha)
            ) {
                FBStatDataItem(
                    category = "경기수",
                    data = it.g,
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
                    data = it.w,
                    customCategoryFontSize = 11,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
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

// last game
@Composable
fun KBOPlayerInfoSeventhItem(
    searchStore: SearchStore,
    store: KBOPlayerInfoStore,
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by store.displayModel.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val lastGame = displayModel.lastGame
    val lastGamePlayerHitterStats = displayModel.lastGamePlayerHitterStats
    val lastGamePlayerPitcherStats = displayModel.lastGamePlayerPitcherStats

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
            store.send(KBOPlayerInfoAction.ShowGameStats())
        }
    ) {
        Text(
            text = "최근경기",
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.alpha(contentsAlpha)
        )

        lastGame?.let {
            val homeTeamScore = it.lineScore?.home?.r?.toIntOrNull() ?: 0
            val awayTeamScore = it.lineScore?.away?.r?.toIntOrNull() ?: 0

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
                                text = teamNameDic["short_${it.gameInfo?.homeTeamId}"] ?: "",
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
                                text = teamNameDic["short_${it.gameInfo?.awayTeamId}"] ?: "",
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
                            text = CalendarUtil.formatDate(it.gameInfo?.date, TimeFormatType.AMPM_WITH_DAY_OF_WEEK_DATE),
                            fontSize = 15.sp
                        )
                    }
                }

                if (lastGamePlayerHitterStats != null && lastGamePlayerPitcherStats == null) {
                    CenterRow(
                        modifier = Modifier.weight(0.55f)
                    ) {
                        StatsDivider()
                        FBStatDataItem(
                            category = "타수",
                            data = lastGamePlayerHitterStats.ab.toString(),
                            customCategoryFontSize = 12,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "안타",
                            data = lastGamePlayerHitterStats.h.toString(),
                            customCategoryFontSize = 12,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "득점",
                            data = lastGamePlayerHitterStats.r.toString(),
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
                } else if (lastGamePlayerPitcherStats != null && lastGamePlayerHitterStats == null) {
                    CenterRow(
                        modifier = Modifier.weight(0.55f)
                    ) {
                        StatsDivider()
                        FBStatDataItem(
                            category = "이닝",
                            data = lastGamePlayerPitcherStats.ip,
                            customCategoryFontSize = 12,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "삼진",
                            data = lastGamePlayerPitcherStats.so,
                            customCategoryFontSize = 12,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "볼넷",
                            data = lastGamePlayerPitcherStats.bb,
                            customCategoryFontSize = 12,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
                        FBStatDataItem(
                            category = "실점",
                            data = lastGamePlayerPitcherStats.r,
                            customCategoryFontSize = 12,
                            modifier = Modifier.weight(1f)
                        )
                        StatsDivider()
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
    searchStore: SearchStore,
    store: KBOPlayerInfoStore,
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by store.displayModel.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val nextGame = displayModel.nextGame

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
            store.send(KBOPlayerInfoAction.ShowGameStats(false))
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
                    text = teamNameDic["short_${nextGame.gameInfo?.homeTeamId}"] ?: "",
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
                    text = teamNameDic["short_${nextGame.gameInfo?.awayTeamId}"] ?: "",
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = CalendarUtil.formatDate(nextGame.gameInfo?.date, TimeFormatType.AMPM_WITH_DAY_OF_WEEK_DATE),
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