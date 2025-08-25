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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.util.EnNameTranslationUtils
import com.moare.android.core.util.KBOUtil
import com.moare.android.core.util.MLBUtil
import com.moare.android.core.util.TranslationType
import com.moare.android.features.search.display.common.components.EmptyStatDataItem
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.components.FBStatDataItem
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerStatsIntent
import com.moare.android.features.search.display.kbo.viewmodel.KBOPlayerStatsViewModel
import com.moare.android.features.search.display.mlb.viewmodel.MLBPlayerStatsIntent
import com.moare.android.features.search.display.mlb.viewmodel.MLBPlayerStatsViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBPlayerStatsDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOPlayerStats
import com.moare.android.features.search.models.models.mlb.MLBPlayerStats
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.HDivider
import com.moare.android.ui.common.components.StatsDivider
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.CenterRow

@Composable
fun MLBPlayerStatsView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    mlbPlayerStatsViewModel: MLBPlayerStatsViewModel = hiltViewModel(),
    data: MLBPlayerStatsDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val displayModel by mlbPlayerStatsViewModel.displayModel.collectAsState()
    val statsList = displayModel?.stats

    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.MLBPlayerStats) {
            mlbPlayerStatsViewModel.send(MLBPlayerStatsIntent.InitData(data))
        }
    }

    InfoViewContainer(
        itemCount = (statsList?.size ?: 0) + 1,
//        shouldShowMeasureContent = true,
        modifier = Modifier,
//            .verticalScroll(rememberScrollState()),
        measureContent = {
            MLBPlayerStatsPlayerInfoItem { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            MLBPlayerStatsList { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        },
        displayContent = {
            MLBPlayerStatsPlayerInfoItem(
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha,
                measureContentAlpha = measureContentAlpha
            )

            MLBPlayerStatsList(
                isAniItem = true,
                itemSizes = itemSizes,
                itemPositions = itemPositions,
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha,
                measureContentAlpha = measureContentAlpha
            )
        }
    )
}

// player info
@Composable
fun MLBPlayerStatsPlayerInfoItem(
    mlbPlayerStatsViewModel: MLBPlayerStatsViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    startPosition: Offset = Offset.Zero,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 1f,
    containerModifier: Modifier = Modifier,
    measureContentAlpha: Float = 0f,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbPlayerStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.player

        var nationalityKrName by remember { mutableStateOf("") }

        LaunchedEffect(displayModel) {
            nationalityKrName = EnNameTranslationUtils.translateByDic(TranslationType.COUNTRY, input = player.birthCountry)
        }

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
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .alpha(contentsAlpha)
            ) {
                URLImage(url = MLBUtil.playerPhotoUrl(player.id))

                // name
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(start = 6.dp, end = 8.dp)
                ) {
                    Text(
                        text = mlbPlayerStatsViewModel.playerNameDictionary["${player.id}"] ?: player.fullName,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = player.fullName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Light,
                        maxLines = 2
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "국적: ",
                            fontSize = 15.sp
                        )

                        Text(
                            text = nationalityKrName,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                URLImage(
                    url = MLBUtil.teamLogoUrl(it.teamId),
                    isSvg = true
                )

                // team, jersey, position
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Text(
                        text = mlbPlayerStatsViewModel.teamNameDictionary["full_${it.teamId}"] ?: "",
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "포지션: ",
                            fontSize = 15.sp
                        )

                        Text(
                            text = player.primaryPosition.name,
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
fun MLBPlayerStatsList(
    mlbPlayerStatsViewModel: MLBPlayerStatsViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSizes: Map<Int, DpSize>? = null,
    itemPositions: Map<Int, Offset>? = null,
    startPosition: Offset = Offset.Zero,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 1f,
    containerModifier: Modifier = Modifier,
    measureContentAlpha: Float = 0f,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by mlbPlayerStatsViewModel.displayModel.collectAsState()

    displayModel?.let {
        val statsList = it.stats

        /* ---------------------
           ui
           --------------------- */
        for ((index, value) in statsList.withIndex()) {
            MLBPlayerStatsListItem(
                index = index,
                data = value,
                isAniItem = isAniItem,
                itemSizes = itemSizes,
                itemPositions = itemPositions,
                startPosition = startPosition,
                aniPosition = aniPosition,
                contentsAlpha = contentsAlpha,
                containerModifier = containerModifier,
                measureContentAlpha = measureContentAlpha,
                updateItemPosition = updateItemPosition
            )
        }
    }
}

@Composable
fun MLBPlayerStatsListItem(
    index: Int,
    data: MLBPlayerStats,
    isAniItem: Boolean,
    itemSizes: Map<Int, DpSize>?,
    itemPositions: Map<Int, Offset>?,
    startPosition: Offset,
    aniPosition: Boolean,
    contentsAlpha: Float,
    containerModifier: Modifier = Modifier,
    measureContentAlpha: Float,
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
        MLBPlayerStatsItem(
            data = data,
            contentsAlpha = contentsAlpha,
            measureContentAlpha = measureContentAlpha
        )
    }
}

@Composable
fun MLBPlayerStatsItem(
    data: MLBPlayerStats,
    contentsAlpha: Float,
    measureContentAlpha: Float,
) {
    val season = data.fielding?.season
        ?: data.hitting?.season
        ?: data.pitching?.season
        ?: data.catching?.season

    /* ---------------------
       ui
       --------------------- */
    CenterColumn(
        modifier = Modifier.alpha(contentsAlpha)
    ) {
        data.hitting?.stat?.let {
            CenterRow {
                BaseballLeagueTitle(
                    url = MLBUtil.mlbLogoUrl,
                    leagueName = "MLB",
                    leagueSeason = season?.toIntOrNull()
                )
                Text(" - [타자]")
            }

            // stats
            CenterRow(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                FBStatDataItem(
                    category = "경기수",
                    data = it.gamesPlayed.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "타수",
                    data = it.atBats.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "타율",
                    data = it.avg,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "안타",
                    data = it.hits.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "2루타",
                    data = it.doubles.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "3루타",
                    data = it.triples.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            HDivider(
                modifier = Modifier.alpha(0.5f).padding(vertical = 4.dp),
                color = Color.Gray,
            )

            CenterRow {
                FBStatDataItem(
                    category = "홈런",
                    data = it.homeRuns.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "출루율",
                    data = it.obp,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "장타율",
                    data = it.slg,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "ops",
                    data = it.ops,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "득점",
                    data = it.runs.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "타점",
                    data = it.rbi.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            HDivider(
                modifier = Modifier.alpha(0.5f).padding(vertical = 4.dp),
                color = Color.Gray,
            )

            CenterRow {
                FBStatDataItem(
                    category = "볼넷",
                    data = it.baseOnBalls.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "삼진",
                    data = it.strikeOuts.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "사구",
                    data = it.hitByPitch.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "도루",
                    data = it.stolenBases.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "도루 실패",
                    data = it.caughtStealing.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "도루 성공률",
                    data = it.stolenBasePercentage,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            HDivider(
                modifier = Modifier.alpha(0.5f).padding(vertical = 4.dp),
                color = Color.Gray,
            )

            CenterRow {
                FBStatDataItem(
                    category = "희생번트",
                    data = it.sacBunts.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "희생플라이",
                    data = it.sacFlies.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "병살타",
                    data = it.groundIntoDoublePlay.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "땅볼아웃",
                    data = it.groundOuts.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "뜬공아웃",
                    data = it.airOuts.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider(modifier = Modifier.alpha(0f))
                EmptyStatDataItem(modifier = Modifier.weight(1f))
            }
        }

        data.pitching?.stat?.let {
            CenterRow {
                BaseballLeagueTitle(
                    url = MLBUtil.mlbLogoUrl,
                    leagueName = "MLB",
                    leagueSeason = season?.toIntOrNull()
                )
                Text(" - [투수]")
            }

            // stats
            CenterRow(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                FBStatDataItem(
                    category = "경기수",
                    data = it.gamesPitched.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "이닝",
                    data = it.inningsPitched,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "평균자책",
                    data = it.era,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "승",
                    data = it.wins.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "패",
                    data = it.losses.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "홀드",
                    data = it.holds.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            HDivider(
                modifier = Modifier.alpha(0.5f).padding(vertical = 4.dp),
                color = Color.Gray,
            )

            CenterRow {
                FBStatDataItem(
                    category = "세이브",
                    data = it.saves.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "삼진",
                    data = it.strikeOuts.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "볼넷",
                    data = it.baseOnBalls.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "피안타",
                    data = it.hits.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "피2루타",
                    data = it.doubles.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "피3루타",
                    data = it.triples.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            HDivider(
                modifier = Modifier.alpha(0.5f).padding(vertical = 4.dp),
                color = Color.Gray,
            )

            CenterRow {
                FBStatDataItem(
                    category = "피홈런",
                    data = it.homeRuns.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "피안타율",
                    data = it.avg,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "실점",
                    data = it.runs.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "자책점",
                    data = it.earnedRuns.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "블론세이브",
                    data = it.blownSaves.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "보크",
                    data = it.balks.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            HDivider(
                modifier = Modifier.alpha(0.5f).padding(vertical = 4.dp),
                color = Color.Gray,
            )

            CenterRow {
                FBStatDataItem(
                    category = "고의4구",
                    data = it.intentionalWalks.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "폭투",
                    data = it.wildPitches.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "완투",
                    data = it.completeGames.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "완봉",
                    data = it.shutouts.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "투구수",
                    data = it.numberOfPitches.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "이닝당 출루혀용률",
                    data = it.whip,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            HDivider(
                modifier = Modifier.alpha(0.5f).padding(vertical = 4.dp),
                color = Color.Gray,
            )

            CenterRow {
                FBStatDataItem(
                    category = "도루 허용",
                    data = it.stolenBases.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "도루 허용률",
                    data = it.stolenBasePercentage,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "승률",
                    data = it.winPercentage,
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "병살타",
                    data = it.groundIntoDoublePlay.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "뜬공아웃",
                    data = it.airOuts.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
                StatsDivider()
                FBStatDataItem(
                    category = "땅볼아웃",
                    data = it.groundOuts.toString(),
                    customCategoryFontSize = 11,
                    customCategoryHeight = 30.dp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}