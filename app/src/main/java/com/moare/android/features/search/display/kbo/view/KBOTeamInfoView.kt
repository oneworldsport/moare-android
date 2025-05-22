package com.moare.android.features.search.display.kbo.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextOverflow
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
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamInfoIntent
import com.moare.android.features.search.display.kbo.viewmodel.KBOTeamInfoViewModel
import com.moare.android.features.search.display.nba.viewmodel.NBATeamInfoIntent
import com.moare.android.features.search.display.nba.viewmodel.NBATeamInfoViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamInfoDisplayModel
import com.moare.android.ui.common.components.BaseballLeagueTitle
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.NBATitle
import com.moare.android.ui.common.components.URLImage

@Composable
fun KBOTeamInfoView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboTeamInfoViewModel: KBOTeamInfoViewModel = hiltViewModel(),
    data: KBOTeamInfoDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.KBOTeamInfo) {
            kboTeamInfoViewModel.send(KBOTeamInfoIntent.InitData(data))
        }
    }

    InfoViewContainer(
        itemCount = 6,
        measureContent = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                KBOTeamInfoFirstItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                KBOTeamInfoSecondItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                KBOTeamInfoThirdItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }
            }

            KBOTeamInfoFourthItem { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            ) {
                KBOTeamInfoFifthItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }

                KBOTeamInfoSixthItem(
                    containerModifier = Modifier.weight(1f)
                ) { index, coordinates ->
                    updateItemPosition(index, coordinates)
                }
            }
        },
        displayContent = {
            KBOTeamInfoFirstItem(
                isAniItem = true,
                itemSize = itemSizes[0],
                itemPosition = itemPositions[0],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOTeamInfoSecondItem(
                isAniItem = true,
                itemSize = itemSizes[1],
                itemPosition = itemPositions[1],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOTeamInfoThirdItem(
                isAniItem = true,
                itemSize = itemSizes[2],
                itemPosition = itemPositions[2],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOTeamInfoFourthItem(
                isAniItem = true,
                itemSize = itemSizes[3],
                itemPosition = itemPositions[3],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOTeamInfoFifthItem(
                isAniItem = true,
                itemSize = itemSizes[4],
                itemPosition = itemPositions[4],
                aniPosition = aniPositions,
                contentsAlpha = contentsAlpha
            )

            KBOTeamInfoSixthItem(
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
    kboTeamInfoViewModel: KBOTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by kboTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val team = it.team

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
                url = KBOUtil.teamLogoUrl(team.id),
                modifier = Modifier.alpha(contentsAlpha),
                isSvg = true
            )

            Text(
                text = kboTeamInfoViewModel.teamNameDictionary["full_${team.id}"] ?: team.teamName,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}

// founded, city, coach
@Composable
fun KBOTeamInfoSecondItem(
    kboTeamInfoViewModel: KBOTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by kboTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val team = it.team

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
                    text = "창단연도: ",
                    fontSize = 15.sp
                )

                Text(
                    text = team.yearFounded.toString(),
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "연고지: ",
                    fontSize = 15.sp
                )

                Text(
                    text = team.city,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "감독: ",
                    fontSize = 15.sp
                )

                Text(
                    text = team.coach,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// venue
@Composable
fun KBOTeamInfoThirdItem(
    kboTeamInfoViewModel: KBOTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by kboTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val team = it.team
        val venue = it.venue

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
                    text = "홈구장: ",
                    fontSize = 15.sp
                )

                Text(
                    text = kboTeamInfoViewModel.teamNameDictionary["venue_${team.id}"] ?: venue.name,
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
}

// league stats
@Composable
fun KBOTeamInfoFourthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboTeamInfoViewModel: KBOTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by kboTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val team = it.team
        val stats = it.stats

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
                searchViewModel.send(SearchViewModel.Intent.ShowTeamStats(teamId = it.team.id))
            }
        ) {
            HCapsuleBar()

            BaseballLeagueTitle(
                url = KBOUtil.kboLogoUrl,
                leagueName = "KBO",
                leagueSeason = stats?.season ?: 2025,
                modifier = Modifier.alpha(contentsAlpha)
            )

            stats?.let {
                Row(
                    modifier = Modifier
                        .alpha(contentsAlpha)
                ) {
                    FBStatDataItem(
                        category = "순위",
                        data = it.rankData.rank,
                        customCategoryFontSize = 13,
                        modifier = Modifier.weight(1f)
                    )

                    FBStatDataItem(
                        category = "승",
                        data = it.rankData.wins,
                        modifier = Modifier.weight(1f)
                    )

                    FBStatDataItem(
                        category = "패",
                        data = it.rankData.losses,
                        modifier = Modifier.weight(1f)
                    )

                    FBStatDataItem(
                        category = "무",
                        data = it.rankData.draws,
                        modifier = Modifier.weight(1f)
                    )

                    FBStatDataItem(
                        category = "타율",
                        data = it.hitterData.avg,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// last game stats
@Composable
fun KBOTeamInfoFifthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboTeamInfoViewModel: KBOTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by kboTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val lastGame = it.lastGame

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
                val homeTeamScore = it.lineScore.home.r
                val awayTeamScore = it.lineScore.away.r

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .alpha(contentsAlpha)
                ) {
                    Text(
                        text = kboTeamInfoViewModel.teamNameDictionary["short_${it.gameInfo?.homeTeamId}"] ?: "",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = homeTeamScore,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.4f),
                        color = if ((homeTeamScore) >= (awayTeamScore)) MaterialTheme.colors.primary else Color.Black
                    )

                    Text(
                        text = " vs ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.3f)
                    )

                    Text(
                        text = awayTeamScore,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.4f),
                        color = if ((awayTeamScore) >= (homeTeamScore)) MaterialTheme.colors.primary else Color.Black
                    )

                    Text(
                        text = kboTeamInfoViewModel.teamNameDictionary["short_${it.gameInfo?.awayTeamId}"] ?: "",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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

// next game stats
@Composable
fun KBOTeamInfoSixthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    kboTeamInfoViewModel: KBOTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by kboTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val nextGame = it.nextGame

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
                searchViewModel.send(SearchViewModel.Intent.ShowGameStats(gameType = "next"))
            }
        ) {
            HCapsuleBar()

            Text(
                text = "다음경기",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(contentsAlpha)
            )

            nextGame?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .alpha(contentsAlpha)
                ) {
                    Text(
                        text = kboTeamInfoViewModel.teamNameDictionary["short_${it.gameInfo?.homeTeamId}"] ?: "",
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
                        text = kboTeamInfoViewModel.teamNameDictionary["short_${it.gameInfo?.awayTeamId}"] ?: "",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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