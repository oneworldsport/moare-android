package com.moare.android.features.search.display.kbo.view

import androidx.compose.foundation.layout.Arrangement
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
import com.moare.android.core.util.KBOUtil
import com.moare.android.core.util.OutputTimeFormatType
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.kbo.store.KBOTeamInfoAction
import com.moare.android.features.search.display.kbo.store.KBOTeamInfoStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.StatsDivider
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.CenterRow

@Composable
fun KBOTeamInfoView(
    searchStore: SearchStore,
    store: KBOTeamInfoStore
) {
    InfoViewContainer(
        searchStore = searchStore,
        itemCount = 6,
        measureContent = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                KBOTeamInfoFirstItem(
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                KBOTeamInfoSecondItem(
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                KBOTeamInfoThirdItem(
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }
            }

            KBOTeamInfoFourthItem(searchStore, store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            ) {
                KBOTeamInfoFifthItem(
                    searchStore = searchStore,
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                KBOTeamInfoSixthItem(
                    searchStore = searchStore,
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }
            }
        },
        displayContent = {
            KBOTeamInfoFirstItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOTeamInfoSecondItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[1],
                itemPosition = itemPositions[1],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOTeamInfoThirdItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[2],
                itemPosition = itemPositions[2],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOTeamInfoFourthItem(
                searchStore = searchStore,
                store = store,
                isAniItem = true,
                itemSize = itemSizes[3],
                itemPosition = itemPositions[3],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOTeamInfoFifthItem(
                searchStore = searchStore,
                store = store,
                isAniItem = true,
                itemSize = itemSizes[4],
                itemPosition = itemPositions[4],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOTeamInfoSixthItem(
                searchStore = searchStore,
                store = store,
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
fun KBOTeamInfoFirstItem(
    store: KBOTeamInfoStore,
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

    val team = displayModel.team

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
            url = KBOUtil.teamLogoUrl(team.id),
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = teamNameDic["full_${team.id}"] ?: team.teamName,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.alpha(contentsAlpha)
        )
    }
}

// founded, city, coach
@Composable
fun KBOTeamInfoSecondItem(
    store: KBOTeamInfoStore,
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by store.displayModel.collectAsState()

    val team = displayModel.team

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
                text = team.yearFounded.toString(),
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = buildAnnotatedString {
                append("연고지: ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                    append(team.city)
                }
            },
            fontSize = 15.sp,
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = buildAnnotatedString {
                append("감독: ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                    append(team.coach)
                }
            },
            fontSize = 15.sp,
            modifier = Modifier.alpha(contentsAlpha)
        )
    }
}

// venue
@Composable
fun KBOTeamInfoThirdItem(
    store: KBOTeamInfoStore,
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

    val team = displayModel.team
    val venue = displayModel.venue

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
                    append(teamNameDic["venue_${team.id}"] ?: venue.name)
                }
            },
            fontSize = 15.sp,
            modifier = Modifier.alpha(contentsAlpha)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "좌석수: ",
                fontSize = 15.sp
            )

            Text(
                text = venue.capacity.toString(),
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "개장: ",
                fontSize = 15.sp
            )

            Text(
                text = venue.opened.toString(),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// league stats
@Composable
fun KBOTeamInfoFourthItem(
    searchStore: SearchStore,
    store: KBOTeamInfoStore,
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by store.displayModel.collectAsState()

    val team = displayModel.team
    val stats = displayModel.stats

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
            store.send(KBOTeamInfoAction.ShowTeamStats)
        }
    ) {
        BaseballLeagueTitle(
            url = KBOUtil.kboLogoUrl,
            leagueName = "KBO",
            leagueSeason = stats?.season ?: 2025,
            modifier = Modifier.alpha(contentsAlpha)
        )

        stats?.let {
            CenterRow(
                modifier = Modifier
                    .alpha(contentsAlpha)
            ) {
                FBStatDataItem(
                    category = "순위",
                    data = it.rankData.rank,
                    customCategoryFontSize = 13,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "승",
                    data = it.rankData.wins,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "패",
                    data = it.rankData.losses,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "무",
                    data = it.rankData.draws,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "타율",
                    data = it.hitterData.avg,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// last game stats
@Composable
fun KBOTeamInfoFifthItem(
    searchStore: SearchStore,
    store: KBOTeamInfoStore,
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
            store.send(KBOTeamInfoAction.ShowGameStats())
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
                        text = teamNameDic["short_${it.gameInfo?.homeTeamId}"] ?: "",
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
                        text = "$awayTeamScore ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = if ((awayTeamScore) >= (homeTeamScore)) MaterialTheme.colors.primary else Color.Black
                    )

                    Text(
                        text = teamNameDic["short_${it.gameInfo?.awayTeamId}"] ?: "",
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = CalendarUtil.formatDate(it.gameInfo?.date, outputFormatType = OutputTimeFormatType.AMPM_WITH_DAY_OF_WEEK_DATE),
                fontSize = 15.sp,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}

// next game stats
@Composable
fun KBOTeamInfoSixthItem(
    searchStore: SearchStore,
    store: KBOTeamInfoStore,
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
            updateItemPosition?.let { it(5, coordinates) }
        },
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = containerModifier,
        onClick = {
            store.send(KBOTeamInfoAction.ShowGameStats(false))
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
                    text = teamNameDic["short_${nextGame.gameInfo?.homeTeamId}"] ?: "",
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
                    text = teamNameDic["short_${nextGame.gameInfo?.awayTeamId}"] ?: "",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = CalendarUtil.formatDate(nextGame.gameInfo?.date, outputFormatType = OutputTimeFormatType.AMPM_WITH_DAY_OF_WEEK_DATE),
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