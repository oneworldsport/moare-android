package com.moare.android.features.search.display.football.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.R
import com.moare.android.core.util.EnNameTranslationUtils
import com.moare.android.core.util.TranslationType
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStatsStore
import com.moare.android.features.search.display.search.viewmodel.SearchStore
import com.moare.android.features.search.models.models.football.FBPlayerStats
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.StatsDivider
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow

@Composable
fun FBPlayerStatsView(
    searchStore: SearchStore,
    store: FBPlayerStatsStore
) {
    val statsList by store.statsList.collectAsState()

    InfoViewContainer(
        searchStore = searchStore,
        itemCount = statsList.size + 1,
        shouldShowMeasureContent = true,
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        measureContent = {
            FBPlayerStatsPlayerInfoItem(store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            FBPlayerStatsList(store) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        }, displayContent = {
            FBPlayerStatsPlayerInfoItem(
                store = store,
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                startPosition = startPosition,
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            FBPlayerStatsList(
                store = store,
                isAniItem = true,
                itemSizes = itemSizes,
                itemPositions = itemPositions,
                startPosition = startPosition,
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )
        }
    )
}

// player info
@Composable
fun FBPlayerStatsPlayerInfoItem(
    store: FBPlayerStatsStore,
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    startPosition: Offset = Offset.Zero,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 1f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by store.displayModel.collectAsState()
    val playerNameDic by store.playerNameDic.collectAsState()
    val teamNameDic by store.teamNameDic.collectAsState()

    val player = displayModel.player
    val team = displayModel.team

    var nationalityKrName by remember { mutableStateOf("") }

    LaunchedEffect(displayModel) {
        nationalityKrName = EnNameTranslationUtils.translateByDic(TranslationType.COUNTRY, input = player.nationality)
    }

    // TODO: startPosition 설정 필요
    MovingCapsuleItemContainer(
        isAniItem = isAniItem,
        itemSize = itemSize,
        itemPosition = itemPosition,
        startPosition = startPosition,
        aniPosition = aniPosition,
        updateItemPosition = { coordinates ->
            updateItemPosition?.let { it(0, coordinates) }
        },
        modifier = containerModifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 8.dp)
                .alpha(contentsAlpha)
        ) {
            URLImage(url = player.photo)

            // name
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 6.dp)
            ) {
                Text(
                    text = playerNameDic["${player.id}"] ?: player.name,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = player.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 2
                )
            }

            // nationality / team
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Row { // TODO: 가운데 정렬
                    // TODO: 나라 국기..?
                    Text(
                        text = "국적: ",
                        fontSize = 15.sp
                    )

                    Text(
                        text = nationalityKrName,
                        fontWeight = FontWeight.Medium
                    )
                }

                team?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "소속팀: ",
                            fontSize = 15.sp
                        )

                        URLImage(
                            url = team.logo,
                            customSize = 24.dp
                        )

                        Text(
                            text = teamNameDic["full_${team.id}"] ?: team.name,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// stats list
@Composable
fun FBPlayerStatsList(
    store: FBPlayerStatsStore,
    isAniItem: Boolean = false,
    itemSizes: Map<Int, DpSize>? = null,
    itemPositions: Map<Int, Offset>? = null,
    startPosition: Offset = Offset.Zero,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 1f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val statsList by store.statsList.collectAsState()

    for ((index, value) in statsList.withIndex()) {
        FBPlayerStatsListItem(
            store = store,
            index = index,
            data = value,
            isAniItem = isAniItem,
            itemSizes = itemSizes,
            itemPositions = itemPositions,
            startPosition = startPosition,
            aniPosition = aniPosition,
            contentsAlpha = contentsAlpha,
            containerModifier = containerModifier,
            updateItemPosition = updateItemPosition
        )
    }
}

@Composable
fun FBPlayerStatsListItem(
    store: FBPlayerStatsStore,
    index: Int,
    data: FBPlayerStats,
    isAniItem: Boolean,
    itemSizes: Map<Int, DpSize>?,
    itemPositions: Map<Int, Offset>?,
    startPosition: Offset,
    aniPosition: Boolean,
    contentsAlpha: Float,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)?
) {
    MovingCapsuleItemContainer(
        isAniItem = isAniItem,
        itemSize = itemSizes?.get(index + 1),
        itemPosition = itemPositions?.get(index + 1),
        startPosition = startPosition,
        aniPosition = aniPosition,
        updateItemPosition = { coordinates ->
            updateItemPosition?.let { it(index + 1, coordinates) }
        },
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = containerModifier
            .padding(top = if (isAniItem) 0.dp else 12.dp)
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        FBPlayerStatsItem(
            store = store,
            data = data,
            contentsAlpha = contentsAlpha
        )
    }
}

@Composable
fun FBPlayerStatsItem(
    store: FBPlayerStatsStore,
    data: FBPlayerStats,
    contentsAlpha: Float
) {
    val teamNameDic by store.teamNameDic.collectAsState()

    var attackStatsOpenState by remember { mutableStateOf(true) }
    var defendStatsOpenState by remember { mutableStateOf(false) }
    var commonStatsOpenState by remember { mutableStateOf(false) }

    /* ---------------------
       ui
       --------------------- */
    CenterColumn(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        // league / team
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 4.dp)
        ) {
            LeagueTitle(
                url = data.league.logo,
                leagueName = data.league.name,
                leagueSeason = data.league.season
            )

            Text(
                text = " - ",
                fontWeight = FontWeight.Medium
            )

            URLImage(
                url = data.team.logo,
                customSize = 24.dp
            )

            Text(
                text = teamNameDic["short_${data.team.id}"] ?: data.team.name,
                fontWeight = FontWeight.Medium
            )
        }

        // stats
        // TODO: 수비수, 골기퍼, 공격수 별로 데이터 노출 다르게
        CenterRow(
            modifier = Modifier
                .clickable { attackStatsOpenState = !attackStatsOpenState }
        ) {
            Text(
                text = "공격 기록",
                modifier = Modifier
                    .padding(end = 4.dp)
            )

            Icon(
                painter = painterResource(id = if (attackStatsOpenState) R.drawable.ic_round_arrow_drop_up_24 else R.drawable.ic_round_arrow_drop_down_24),
                contentDescription = null,
                tint = Color.Gray
            )
        }

        AnimatedVisibility(
            visible = attackStatsOpenState,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            CenterRow {
                FBStatDataItem(
                    category = "골",
                    data = data.goals.total.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "도움",
                    data = data.goals.assists.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "패널티 골",
                    data = data.penalty.scored.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "슈팅",
                    data = data.shots.total.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "유효 슈팅",
                    data = data.shots.on.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "드리블",
                    data = data.dribbles.attempts.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        CenterRow(
            modifier = Modifier
                .clickable { defendStatsOpenState = !defendStatsOpenState }
        ) {
            Text(
                text = "수비 기록",
                modifier = Modifier
                    .padding(end = 4.dp)
            )

            Icon(
                painter = painterResource(id = if (defendStatsOpenState) R.drawable.ic_round_arrow_drop_up_24 else R.drawable.ic_round_arrow_drop_down_24),
                contentDescription = null,
                tint = Color.Gray
            )
        }

        AnimatedVisibility(
            visible = defendStatsOpenState,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            CenterRow {
                FBStatDataItem(
                    category = "태클",
                    data = data.tackles.total.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "파울",
                    data = data.fouls.committed.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "경고",
                    data = data.cards.yellow.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "퇴장",
                    data = data.cards.red.toString(),
                    modifier = Modifier.weight(1f)
                )
//            EmptyStatDataItem(
//                modifier = Modifier.weight(1f)
//            )
            }
        }

        CenterRow(
            modifier = Modifier
                .clickable { commonStatsOpenState = !commonStatsOpenState }
        ) {
            Text(
                text = "공통 기록",
                modifier = Modifier
                    .padding(end = 4.dp)
            )

            Icon(
                painter = painterResource(id = if (commonStatsOpenState) R.drawable.ic_round_arrow_drop_up_24 else R.drawable.ic_round_arrow_drop_down_24),
                contentDescription = null,
                tint = Color.Gray
            )
        }

        AnimatedVisibility(
            visible = commonStatsOpenState,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            CenterRow {
                FBStatDataItem(
                    category = "출전 경기수",
                    data = data.games.appearences.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "패스",
                    data = data.passes.total.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "평균 평점",
                    data = data.games.rating.take(3),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}