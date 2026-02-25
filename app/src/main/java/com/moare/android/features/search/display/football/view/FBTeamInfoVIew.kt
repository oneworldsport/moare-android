package com.moare.android.features.search.display.football.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.moare.android.core.util.EnNameTranslationUtils
import com.moare.android.core.util.OutputTimeFormatType
import com.moare.android.core.util.TranslationType
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.football.store.FBTeamInfoAction
import com.moare.android.features.search.display.football.store.FBTeamInfoStore
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.ui.common.components.FBLeagueTitle
import com.moare.android.ui.common.components.StatsDivider
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.CenterRow

@Composable
fun FBTeamInfoView(
    searchStore: SearchStore,
    store: FBTeamInfoStore
) {
    InfoViewContainer(searchStore = searchStore, itemCount = 6, measureContent = {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            FBTeamInfoFirstItem(
                store = store,
                containerModifier = Modifier.weight(1f)
            ) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            FBTeamInfoSecondItem(
                store = store,
                containerModifier = Modifier.weight(1f)
            ) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            FBTeamInfoThirdItem(
                store = store,
                containerModifier = Modifier.weight(1f)
            ) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        }

        FBTeamInfoFourthItem(searchStore, store) { index, coordinates ->
            updateItemPosition(index, coordinates)
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(top = 12.dp)
        ) {
            FBTeamInfoFifthItem(
                searchStore = searchStore,
                store = store,
                containerModifier = Modifier.weight(1f)
            ) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            FBTeamInfoSixthItem(
                searchStore = searchStore,
                store = store,
                containerModifier = Modifier.weight(1f)
            ) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        }
    }, displayContent = {
        FBTeamInfoFirstItem(
            store = store,
            isAniItem = true,
            itemSize = itemSizes[0],
            itemPosition = itemPositions[0],
            aniPosition = aniPositions,
            contentsAlpha = contentsAlpha
        )

        FBTeamInfoSecondItem(
            store = store,
            isAniItem = true,
            itemSize = itemSizes[1],
            itemPosition = itemPositions[1],
            aniPosition = aniPositions,
            contentsAlpha = contentsAlpha
        )

        FBTeamInfoThirdItem(
            store = store,
            isAniItem = true,
            itemSize = itemSizes[2],
            itemPosition = itemPositions[2],
            aniPosition = aniPositions,
            contentsAlpha = contentsAlpha
        )

        FBTeamInfoFourthItem(
            searchStore = searchStore,
            store = store,
            isAniItem = true,
            itemSize = itemSizes[3],
            itemPosition = itemPositions[3],
            aniPosition = aniPositions,
            contentsAlpha = contentsAlpha
        )

        FBTeamInfoFifthItem(
            searchStore = searchStore,
            store = store,
            isAniItem = true,
            itemSize = itemSizes[4],
            itemPosition = itemPositions[4],
            aniPosition = aniPositions,
            contentsAlpha = contentsAlpha
        )

        FBTeamInfoSixthItem(
            searchStore = searchStore,
            store = store,
            isAniItem = true,
            itemSize = itemSizes[5],
            itemPosition = itemPositions[5],
            aniPosition = aniPositions,
            contentsAlpha = contentsAlpha
        )
    })
}

// logo, name
@Composable
fun FBTeamInfoFirstItem(
    store: FBTeamInfoStore,
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
            url = team.logo,
            modifier = Modifier.alpha(contentsAlpha)
        )

        Text(
            text = teamNameDic["full_${team.id}"] ?: team.name,
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

// founded, city, country
@Composable
fun FBTeamInfoSecondItem(
    store: FBTeamInfoStore,
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
    val venue = displayModel.venue

    var countryKrName by remember { mutableStateOf("") }

    LaunchedEffect(team) {
        countryKrName = EnNameTranslationUtils.translateByDic(TranslationType.COUNTRY, input = team.country)
    }

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
                text = team.founded.toString(),
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "연고지: ",
                fontSize = 15.sp
            )

            Text(
                text = venue.city,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.alpha(contentsAlpha)
        ) {
            Text(
                text = "소속나라: ",
                fontSize = 15.sp
            )

            Text(
                text = countryKrName,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// venue
@Composable
fun FBTeamInfoThirdItem(
    store: FBTeamInfoStore,
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
                    append(teamNameDic["venue_${displayModel.team.id}"] ?: venue.name)
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
    }
}

// league stats
@Composable
fun FBTeamInfoFourthItem(
    searchStore: SearchStore,
    store: FBTeamInfoStore,
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by store.displayModel.collectAsState()

    val stats = displayModel.stats
    val league = displayModel.stats?.league

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
            store.send(FBTeamInfoAction.ShowTeamStats)
        }
    ) {
        league?.let {
            FBLeagueTitle(
                url = league.logo,
                leagueName = league.name,
                leagueSeason = league.season,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }

        stats?.let {
            CenterRow(
                modifier = Modifier
                    .alpha(contentsAlpha)
                    .fillMaxWidth()
            ) {
                FBStatDataItem(
                    category = "승",
                    data = stats.fixtures.wins.total.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "무",
                    data = stats.fixtures.draws.total.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "패",
                    data = stats.fixtures.loses.total.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "득점",
                    data = stats.goals.teamGoalsFor.total.total.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "실점",
                    data = stats.goals.teamGoalsAgainst.total.total.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// last game stats
@Composable
fun FBTeamInfoFifthItem(
    searchStore: SearchStore,
    store: FBTeamInfoStore,
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
            store.send(FBTeamInfoAction.ShowGameStats())
        }
    ) {
        Text(
            text = "최근경기",
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.alpha(contentsAlpha)
        )

        lastGame?.let {
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
                        text = teamNameDic["short_${lastGame.teams.home.id}"] ?: lastGame.teams.home.name,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = " ${lastGame.goals.home}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = if ((lastGame.goals.home) >= (lastGame.goals.away)) MaterialTheme.colors.primary else Color.Black
                    )
                }

                Text(
                    text = " - ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${lastGame.goals.away} ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = if ((lastGame.goals.away) >= (lastGame.goals.home)) MaterialTheme.colors.primary else Color.Black
                    )

                    Text(
                        text = teamNameDic["short_${lastGame.teams.away.id}"] ?: lastGame.teams.away.name,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = CalendarUtil.formatDate(lastGame.fixture.date, outputFormatType = OutputTimeFormatType.AMPM_WITH_DAY_OF_WEEK_DATE),
                fontSize = 15.sp,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}

// next game stats
@Composable
fun FBTeamInfoSixthItem(
    searchStore: SearchStore,
    store: FBTeamInfoStore,
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
            store.send(FBTeamInfoAction.ShowGameStats(false))
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
                    text = teamNameDic["short_${nextGame.teams.home.id}"] ?: nextGame.teams.home.name,
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
                    text = teamNameDic["short_${nextGame.teams.away.id}"] ?: nextGame.teams.away.name,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = CalendarUtil.formatDate(nextGame.fixture.date, outputFormatType = OutputTimeFormatType.AMPM_WITH_DAY_OF_WEEK_DATE),
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














