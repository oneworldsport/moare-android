package com.moare.android.features.search.display.nba.view

import androidx.compose.foundation.layout.Arrangement
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
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.football.viewmodel.FBTeamInfoAction
import com.moare.android.features.search.display.nba.viewmodel.NBAPlayerInfoAction
import com.moare.android.features.search.display.nba.viewmodel.NBATeamInfoAction
import com.moare.android.features.search.display.nba.viewmodel.NBATeamInfoStore
import com.moare.android.features.search.display.search.viewmodel.SearchAction
import com.moare.android.features.search.display.search.viewmodel.SearchStore
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.StatsDivider
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.CenterRow

@Composable
fun NBATeamInfoView(
    searchStore: SearchStore,
    store: NBATeamInfoStore
) {
    InfoViewContainer(
        searchStore = searchStore,
        itemCount = 6,
        measureContent = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                NBATeamInfoFirstItem(
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                NBATeamInfoSecondItem(
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                NBATeamInfoThirdItem(
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }
            }

            NBATeamInfoFourthItem(searchStore, store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            ) {
                NBATeamInfoFifthItem(
                    searchStore = searchStore,
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                NBATeamInfoSixthItem(
                    searchStore = searchStore,
                    store = store,
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }
            }
        },
        displayContent = {
            NBATeamInfoFirstItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBATeamInfoSecondItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[1],
                itemPosition = itemPositions[1],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBATeamInfoThirdItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[2],
                itemPosition = itemPositions[2],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBATeamInfoFourthItem(
                searchStore = searchStore,
                store = store,
                isAniItem = true,
                itemSize = itemSizes[3],
                itemPosition = itemPositions[3],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBATeamInfoFifthItem(
                searchStore = searchStore,
                store = store,
                isAniItem = true,
                itemSize = itemSizes[4],
                itemPosition = itemPositions[4],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            NBATeamInfoSixthItem(
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
fun NBATeamInfoFirstItem(
    store: NBATeamInfoStore,
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
            url = NBAUtil.teamLogoUrl(team.id),
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = teamNameDic["full_${team.id}"] ?: team.fullName,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = team.fullName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            maxLines = 2,
            modifier = Modifier.alpha(contentsAlpha)
        )
    }
}

// founded, state, city
@Composable
fun NBATeamInfoSecondItem(
    store: NBATeamInfoStore,
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
                    append(team.state)
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
                text = "${team.teamConference} / ${team.teamDivision}",
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// venue
@Composable
fun NBATeamInfoThirdItem(
    store: NBATeamInfoStore,
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "홈구장: ",
                fontSize = 15.sp
            )

            Text(
                text = teamNameDic["venue_${team.id}"] ?: venue.name,
                fontWeight = FontWeight.Medium
            )
        }

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
fun NBATeamInfoFourthItem(
    searchStore: SearchStore,
    store: NBATeamInfoStore,
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
            store.send(NBATeamInfoAction.ShowTeamStats)
        }
    ) {
        NBATitle(
            leagueName = "NBA 정규시즌",
            leagueSeason = stats?.groupValue?.split("-")?.firstOrNull()?.toIntOrNull() ?: 2024,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .alpha(contentsAlpha)
        )

        stats?.let {
            CenterRow(
                modifier = Modifier
                    .alpha(contentsAlpha)
                    .fillMaxWidth()
            ) {
                FBStatDataItem(
                    category = "${NBAUtil.translateEastWest(team.teamConference)} 컨퍼런스 순위",
                    data = team.confRank.toString(),
                    customCategoryFontSize = 13,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "승",
                    data = stats.wins.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "패",
                    data = stats.losses.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "경기당 득점",
                    data = stats.ptsPG.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// last game stats
@Composable
fun NBATeamInfoFifthItem(
    searchStore: SearchStore,
    store: NBATeamInfoStore,
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
            store.send(NBATeamInfoAction.ShowGameStats())
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
                        text = if (homeTeam == null) "" else teamNameDic["short_${homeTeam.teamId}"] ?: homeTeam.teamCity,
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
                        text = if (awayTeam == null) "" else teamNameDic["short_${awayTeam.teamId}"] ?: awayTeam.teamCity,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = CalendarUtil.formatDate(lastGame.gameSummary?.date, TimeFormatType.AMPM_WITH_DAY_OF_WEEK_DATE),
                fontSize = 15.sp,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}

// next game stats
@Composable
fun NBATeamInfoSixthItem(
    searchStore: SearchStore,
    store: NBATeamInfoStore,
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
            store.send(NBATeamInfoAction.ShowGameStats(false))
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
                    .alpha(contentsAlpha)
            ) {
                Text(
                    text = if (lastMeeting?.lastGameHomeTeamId == null) "" else teamNameDic["short_${lastMeeting.lastGameHomeTeamId}"] ?: lastMeeting.lastGameHomeTeamCity,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = " vs ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(0.3f)
                )

                Text(
                    text = if (lastMeeting?.lastGameVisitorTeamId == null) "" else teamNameDic["short_${lastMeeting.lastGameVisitorTeamId}"] ?: lastMeeting.lastGameVisitorTeamCity,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = CalendarUtil.formatDate(nextGame.gameSummary?.date, TimeFormatType.AMPM_WITH_DAY_OF_WEEK_DATE),
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





























