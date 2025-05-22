package com.moare.android.features.search.display.mlb.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.moare.android.core.util.MLBUtil
import com.moare.android.core.util.NBAUtil
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStatsIntent
import com.moare.android.features.search.display.mlb.viewmodel.MLBTeamStatsViewModel
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStatsIntent
import com.moare.android.features.search.display.nba.viewmodel.NBATeamStatsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStatsDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBTeamStats
import com.moare.android.features.search.models.models.nba.NBATeamStats
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.URLImage

@Composable
fun MLBTeamStatsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbTeamStatsViewModel: MLBTeamStatsViewModel = hiltViewModel(),
    data: MLBTeamStatsDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by mlbTeamStatsViewModel.displayModel.collectAsState()
    val statsList = displayModel?.stats

    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.MLBTeamStats) {
            mlbTeamStatsViewModel.send(MLBTeamStatsIntent.InitData(data))
        }
    }

    InfoViewContainer(
        itemCount = (statsList?.size ?: 0) + 1,
        modifier = Modifier,
//            .verticalScroll(rememberScrollState()),
        measureContent = {
            MLBTeamStatsTeamInfoItem { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            MLBTeamStatsList { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        }, displayContent = {
            MLBTeamStatsTeamInfoItem(
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            MLBTeamStatsList(
                isAniItem = true,
                itemSizes = itemSizes,
                itemPositions = itemPositions,
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )
        }
    )
}

// team info
@Composable
fun MLBTeamStatsTeamInfoItem(
    mlbTeamStatsViewModel: MLBTeamStatsViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbTeamStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val team = it.team
        val venue = it.venue

        MovingCapsuleItemContainer(
            isAniItem = isAniItem,
            itemSize = itemSize,
            itemPosition = itemPosition,
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
                URLImage(
                    url = MLBUtil.teamLogoUrl(team.id),
                    isSvg = true
                )

                // name, state and city
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = mlbTeamStatsViewModel.teamNameDictionary["full_${team.id}"] ?: team.teamName,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = team.teamName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light,
                        maxLines = 2
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "연고지: ",
                            fontSize = 15.sp
                        )

                        Text(
                            text = team.locationName,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // venue, conference, division
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "홈구장: ",
                            fontSize = 15.sp
                        )

                        Text(
                            text = mlbTeamStatsViewModel.teamNameDictionary["venue_${team.id}"] ?: venue.name,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "리그: ",
                            fontSize = 15.sp
                        )

                        Text(
                            text = team.league.name,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "디비전: ",
                            fontSize = 15.sp
                        )

                        Text(
                            text = team.division.name,
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
fun MLBTeamStatsList(
    mlbTeamStatsViewModel: MLBTeamStatsViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSizes: Map<Int, DpSize>? = null,
    itemPositions: Map<Int, Offset>? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbTeamStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val statsList = it.stats

        /* ---------------------
           ui
           --------------------- */
        for ((index, value) in statsList.withIndex()) {
            MLBTeamStatsListItem(
                index = index,
                data = value,
                isAniItem = isAniItem,
                itemSizes = itemSizes,
                itemPositions = itemPositions,
                aniPosition = aniPosition,
                contentsAlpha = contentsAlpha,
                containerModifier = containerModifier,
                updateItemPosition = updateItemPosition
            )
        }
    }
}

@Composable
fun MLBTeamStatsListItem(
    index: Int,
    data: MLBTeamStats,
    isAniItem: Boolean,
    itemSizes: Map<Int, DpSize>?,
    itemPositions: Map<Int, Offset>?,
    aniPosition: Boolean,
    contentsAlpha: Float,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)?
) {
    MovingCapsuleItemContainer(
        isAniItem = isAniItem,
        itemSize = itemSizes?.get(index + 1),
        itemPosition = itemPositions?.get(index + 1),
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
        MLBTeamStatsItem(
            data = data,
            contentsAlpha = contentsAlpha
        )
    }
}

@Composable
fun MLBTeamStatsItem(
    mlbTeamStatsViewModel: MLBTeamStatsViewModel = hiltViewModel(),
    data: MLBTeamStats,
    contentsAlpha: Float
) {
    val displayModel by mlbTeamStatsViewModel.displayModel.collectAsState()
    val team = displayModel?.team
    val record = data.recordData
    val hitting = data.hitting
    val pitching = data.pitching
    val fielding = data.fielding
    val catching = data.catching

    /* ---------------------
       ui
       --------------------- */
    HCapsuleBar()

    team?.let {
        if (record != null && hitting != null && pitching != null && fielding != null && catching != null) {
            // league
            BaseballLeagueTitle(
                url = MLBUtil.mlbLogoUrl,
                leagueName = "MLB",
                leagueSeason = team.season,
                modifier = Modifier
                    .alpha(contentsAlpha)
            )

            // stats
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .alpha(contentsAlpha)
            ) {
                FBStatDataItem(
                    category = "${MLBUtil.leagueDivisionMap[team.league.id] ?: team.league.name} 순위",
                    data = record.divisionRank,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "승",
                    data = record.wins.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "패",
                    data = record.losses.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "경기수",
                    data = record.gamesPlayed.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "게임차",
                    data = record.gamesBack.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "승률",
                    data = record.winningPercentage,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                FBStatDataItem(
                    category = "타율",
                    data = hitting.avg,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "안타",
                    data = hitting.hits.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "홈런",
                    data = hitting.homeRuns.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "출루율",
                    data = hitting.obp,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "장탸율",
                    data = hitting.slg,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "ops",
                    data = hitting.ops,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                FBStatDataItem(
                    category = "사구-[타자]",
                    data = hitting.hitByPitch.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "병살타",
                    data = hitting.groundIntoDoublePlay.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "땅볼아웃",
                    data = hitting.groundOuts.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "득점",
                    data = hitting.runs.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "타점",
                    data = hitting.rbi.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "삼진-[타자]",
                    data = hitting.strikeOuts.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                FBStatDataItem(
                    category = "피안타율",
                    data = pitching.avg,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "피안타",
                    data = pitching.hits.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "피홈런",
                    data = pitching.homeRuns.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "평균자책점",
                    data = pitching.era,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "세이브",
                    data = pitching.saves.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "블론세이브",
                    data = pitching.blownSaves.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.alpha(contentsAlpha)
            ) {

                FBStatDataItem(
                    category = "볼넷-[투수]",
                    data = pitching.baseOnBalls.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "보크",
                    data = pitching.balks.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "홀드",
                    data = pitching.holds.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "이닝당 출루허용률",
                    data = pitching.whip,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "도루성공",
                    data = hitting.stolenBases.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                FBStatDataItem(
                    category = "도루실패",
                    data = hitting.caughtStealing.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}