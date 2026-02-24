package com.moare.android.features.search.display.nba.view

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
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.NBAUtil
import com.moare.android.core.util.TimeFormatType
import com.moare.android.core.util.toCm
import com.moare.android.core.util.toKg
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.nba.store.NBAPlayerInfoAction
import com.moare.android.features.search.display.nba.store.NBAPlayerInfoStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.StatsDivider
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.CenterRow

@Composable
fun NBAPlayerInfoView(
    searchStore: SearchStore,
    store: NBAPlayerInfoStore
) {
    InfoViewContainer(
        searchStore =searchStore,
        itemCount = 9,
        measureContent = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                NBAPlayerInfoFirstItem(
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                NBAPlayerInfoSecondItem(
                    searchStore =searchStore,
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                NBAPlayerInfoThirdItem(
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
                NBAPlayerInfoFourthItem(
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                NBAPlayerInfoFifthItem(
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                NBAPlayerInfoSixthItem(
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }
            }

            NBAPlayerInfoSeventhItem(searchStore, store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            NBAPlayerInfoEighthItem(searchStore, store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            NBAPlayerInfoNinthItem(searchStore, store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        },
        displayContent = {
            NBAPlayerInfoFirstItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBAPlayerInfoSecondItem(
                searchStore = searchStore,
                store = store,
                isAniItem = true,
                itemSize = itemSizes[1],
                itemPosition = itemPositions[1],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBAPlayerInfoThirdItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[2],
                itemPosition = itemPositions[2],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBAPlayerInfoFourthItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[3],
                itemPosition = itemPositions[3],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBAPlayerInfoFifthItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[4],
                itemPosition = itemPositions[4],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBAPlayerInfoSixthItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[5],
                itemPosition = itemPositions[5],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBAPlayerInfoSeventhItem(
                searchStore = searchStore,
                store = store,
                isAniItem = true,
                itemSize = itemSizes[6],
                itemPosition = itemPositions[6],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBAPlayerInfoEighthItem(
                searchStore = searchStore,
                store = store,
                isAniItem = true,
                itemSize = itemSizes[7],
                itemPosition = itemPositions[7],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBAPlayerInfoNinthItem(
                searchStore = searchStore,
                store = store,
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
    store: NBAPlayerInfoStore,
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by store.displayModel.collectAsState()
    val playerNameDic by store.playerNameDic.collectAsState()

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
            url = NBAUtil.playerPhotoUrl(player.personId),
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = playerNameDic[player.personId.toString()] ?: player.displayFirstLast,
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

// logo, team, name
@Composable
fun NBAPlayerInfoSecondItem(
    searchStore: SearchStore,
    store: NBAPlayerInfoStore,
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
        modifier = containerModifier
    ) {
        // TODO: "소속팀" 라벨 표시 필요
        URLImage(
            url = NBAUtil.teamLogoUrl(player.teamId),
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = teamNameDic["full_${player.teamId}"] ?: "${player.teamCity} ${player.teamName}",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.alpha(contentsAlpha)
        )
    }
}

// jersey, position
@Composable
fun NBAPlayerInfoThirdItem(
    store: NBAPlayerInfoStore,
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

// from school/team, draft info, career info
@Composable
fun NBAPlayerInfoFourthItem(
    store: NBAPlayerInfoStore,
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

// country, birth, age
@Composable
fun NBAPlayerInfoFifthItem(
    store: NBAPlayerInfoStore,
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

// weight(kg/pound), height(cm/feet)
@Composable
fun NBAPlayerInfoSixthItem(
    store: NBAPlayerInfoStore,
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

// league stats
@Composable
fun NBAPlayerInfoSeventhItem(
    searchStore: SearchStore,
    store: NBAPlayerInfoStore,
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
            updateItemPosition?.let { it(6, coordinates) }
        },
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .padding(top = if (isAniItem) 0.dp else 12.dp),
        onClick = {
            store.send(NBAPlayerInfoAction.ShowPlayerStats)
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

// last game
@Composable
fun NBAPlayerInfoEighthItem(
    searchStore: SearchStore,
    store: NBAPlayerInfoStore,
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
    val lastGamePlayerStats = displayModel.lastGamePlayerStats

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
            store.send(NBAPlayerInfoAction.ShowGameStats())
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
            val homeTeamScore = lastGame.lineScore?.find { it.teamId == homeTeam?.teamId }?.pts ?: 0
            val awayTeamScore = lastGame.lineScore?.find { it.teamId == awayTeam?.teamId }?.pts ?: 0

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
                                text = if (homeTeam == null) "" else teamNameDic["short_${homeTeam.teamId}"] ?: homeTeam.teamCity,
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
                                text = if (awayTeam == null) "" else teamNameDic["short_${awayTeam.teamId}"] ?: awayTeam.teamCity,
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
                            text = CalendarUtil.formatDate(lastGame.gameSummary?.gameDate, TimeFormatType.AMPM_WITH_DAY_OF_WEEK_DATE),
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

// next game
@Composable
fun NBAPlayerInfoNinthItem(
    searchStore: SearchStore,
    store: NBAPlayerInfoStore,
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
            updateItemPosition?.let { it(8, coordinates) }
        },
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .padding(top = if (isAniItem) 0.dp else 12.dp),
        onClick = {
            store.send(NBAPlayerInfoAction.ShowGameStats(false))
        }
    ) {
        Text(
            text = "다음경기",
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.alpha(contentsAlpha)
        )

        if (nextGame != null) {
            val homeTeamId = nextGame.gameSummary?.homeTeamId
            val awayTeamId = nextGame.gameSummary?.awayTeamId

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .alpha(contentsAlpha)
            ) {
                Text(
                    text = if (homeTeamId == null) "" else teamNameDic["short_${homeTeamId}"] ?: "",
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
                    text = if (awayTeamId == null) "" else teamNameDic["short_${awayTeamId}"] ?: "",
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = CalendarUtil.formatDate(nextGame.gameSummary?.gameDate, TimeFormatType.AMPM_WITH_DAY_OF_WEEK_DATE),
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