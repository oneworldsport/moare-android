package com.moare.android.features.search.display.football.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.util.EnNameTranslationUtils
import com.moare.android.core.util.TranslationType
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.components.FBStatDataItem
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStatsIntent
import com.moare.android.features.search.display.football.viewmodel.FBPlayerStatsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStatsDisplayModel
import com.moare.android.features.search.models.models.football.FBPlayerStats
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.URLImage

@Composable
fun FBPlayerStatsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbPlayerStatsViewModel: FBPlayerStatsViewModel = hiltViewModel(),
    data: FBPlayerStatsDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by fbPlayerStatsViewModel.displayModel.collectAsState()
    val statsList = displayModel?.stats

    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.FBPlayerStats) {
            fbPlayerStatsViewModel.send(FBPlayerStatsIntent.InitData(data))
        }
    }

    InfoViewContainer(
        itemCount = (statsList?.size ?: 0) + 1,
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        measureContent = {
            FBPlayerStatsPlayerInfoItem { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            FBPlayerStatsList { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        }, displayContent = {
//            val startPosition = if (parentCenter != Offset.Zero) {
//                Offset(x = 0f, y = -parentCenter.y + (screenHeightPx() / 2))
//            } else {
//                Offset.Zero
//            }

            FBPlayerStatsPlayerInfoItem(
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
//                startPosition = startPosition,
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            FBPlayerStatsList(
                isAniItem = true,
                itemSizes = itemSizes,
                itemPositions = itemPositions,
//                startPosition = startPosition,
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )
        }
    )
}

// player info
@Composable
fun FBPlayerStatsPlayerInfoItem(
    fbPlayerStatsViewModel: FBPlayerStatsViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    startPosition: Offset = Offset.Zero,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by fbPlayerStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.player
        val team = it.team

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
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = containerModifier.fillMaxWidth()
        ) {
            HCapsuleBar()

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .alpha(contentsAlpha)
            ) {
                URLImage(url = player.photo)

                // name
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = fbPlayerStatsViewModel.playerNameDictionary["${player.id}"] ?: player.name,
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
                    verticalArrangement = Arrangement.spacedBy(4.dp)
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
                                text = fbPlayerStatsViewModel.teamNameDictionary["full_${team.id}"] ?: team.name,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// stats list
@Composable
fun FBPlayerStatsList(
    fbPlayerStatsViewModel: FBPlayerStatsViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSizes: Map<Int, DpSize>? = null,
    itemPositions: Map<Int, Offset>? = null,
    startPosition: Offset = Offset.Zero,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by fbPlayerStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val statsList = it.stats

        /* ---------------------
           ui
           --------------------- */
        for ((index, value) in statsList.withIndex()) {
            FBPlayerStatsListItem(
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
}

@Composable
fun FBPlayerStatsListItem(
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
            .padding(horizontal = if (isAniItem) 0.dp else 4.dp)
            .fillMaxWidth()
    ) {
        FBPlayerStatsItem(
            data = data,
            contentsAlpha = contentsAlpha
        )
    }
}

@Composable
fun FBPlayerStatsItem(
    fbPlayerStatsViewModel: FBPlayerStatsViewModel = hiltViewModel(),
    data: FBPlayerStats,
    contentsAlpha: Float = 1f
) {
    /* ---------------------
       ui
       --------------------- */
    HCapsuleBar()

    // league / team
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = 4.dp)
            .alpha(contentsAlpha)
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
            text = fbPlayerStatsViewModel.teamNameDictionary["short_${data.team.id}"] ?: data.team.name,
            fontWeight = FontWeight.Medium
        )
    }

    // stats
    // TODO: 수비수, 골기퍼, 공격수 별로 데이터 노출 다르게
    Row(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "출전 경기수",
            data = data.games.appearences.toString(),
            customCategoryFontSize = 14,
            customWidth = 70.dp
        )
//            FBPlayerStatsDataItem(
//                category = "선발 출전",
//                data =
//            )
        FBStatDataItem(
            category = "평균 평점",
            data = data.games.rating.take(3),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "골",
            data = data.goals.total.toString()
        )
        FBStatDataItem(
            category = "패널티 골",
            data = data.penalty.scored.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "도움",
            data = data.goals.assists.toString(),
            customWidth = 70.dp
        )
    }

    Row(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "슈팅",
            data = data.shots.total.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "유효 슈팅",
            data = data.shots.on.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "패스",
            data = data.passes.total.toString()
        )
        FBStatDataItem(
            category = "태클",
            data = data.tackles.total.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "드리블",
            data = data.dribbles.attempts.toString(),
            customWidth = 70.dp
        )
    }

    Row(
        modifier = Modifier
            .padding(bottom = 6.dp)
            .alpha(contentsAlpha)
    ) {
        FBStatDataItem(
            category = "파울",
            data = data.fouls.committed.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "경고",
            data = data.cards.yellow.toString(),
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "퇴장",
            data = data.cards.red.toString()
        )
        FBStatDataItem(
            category = "",
            data = "",
            customWidth = 70.dp
        )
        FBStatDataItem(
            category = "",
            data = "",
            customWidth = 70.dp
        )
    }
}