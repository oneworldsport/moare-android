package com.moare.android.features.search.display.football.view

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.EnNameTranslationUtils
import com.moare.android.core.util.TranslationType
import com.moare.android.features.search.display.common.container.InfoViewContainer
import com.moare.android.features.search.display.common.container.MovingCapsuleItemContainer
import com.moare.android.features.search.display.components.FBStatDataItem
import com.moare.android.features.search.display.football.viewmodel.FBTeamInfoIntent
import com.moare.android.features.search.display.football.viewmodel.FBTeamInfoViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamInfoDisplayModel
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.util.optionalClickable
import com.moare.android.ui.util.screenWidthDp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun FBTeamInfoView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbTeamInfoViewModel: FBTeamInfoViewModel = hiltViewModel(),
    data: FBTeamInfoDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.FBTeamInfo) {
            fbTeamInfoViewModel.send(FBTeamInfoIntent.InitData(data))
        }
    }

    InfoViewContainer(itemCount = 6, measureContent = {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            FBTeamInfoFirstItem(
                containerModifier = Modifier.weight(1f)
            ) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            FBTeamInfoSecondItem(
                containerModifier = Modifier.weight(1f)
            ) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            FBTeamInfoThirdItem(
                containerModifier = Modifier.weight(1f)
            ) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        }

        FBTeamInfoFourthItem { index, coordinates ->
            updateItemPosition(index, coordinates)
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(top = 12.dp)
        ) {
            FBTeamInfoFifthItem(
                containerModifier = Modifier.weight(1f)
            ) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            FBTeamInfoSixthItem(
                containerModifier = Modifier.weight(1f)
            ) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        }
    }, displayContent = {
        FBTeamInfoFirstItem(
            isAniItem = true,
            itemSize = itemSizes[0],
            itemPosition = itemPositions[0],
            aniPosition = aniPositions,
            contentsAlpha = contentsAlpha
        )

        FBTeamInfoSecondItem(
            isAniItem = true,
            itemSize = itemSizes[1],
            itemPosition = itemPositions[1],
            aniPosition = aniPositions,
            contentsAlpha = contentsAlpha
        )

        FBTeamInfoThirdItem(
            isAniItem = true,
            itemSize = itemSizes[2],
            itemPosition = itemPositions[2],
            aniPosition = aniPositions,
            contentsAlpha = contentsAlpha
        )

        FBTeamInfoFourthItem(
            isAniItem = true,
            itemSize = itemSizes[3],
            itemPosition = itemPositions[3],
            aniPosition = aniPositions,
            contentsAlpha = contentsAlpha
        )

        FBTeamInfoFifthItem(
            isAniItem = true,
            itemSize = itemSizes[4],
            itemPosition = itemPositions[4],
            aniPosition = aniPositions,
            contentsAlpha = contentsAlpha
        )

        FBTeamInfoSixthItem(
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
    fbTeamInfoViewModel: FBTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by fbTeamInfoViewModel.displayModel.collectAsState()

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
                url = team.logo,
                modifier = Modifier.alpha(contentsAlpha)
            )

            Text(
                text = fbTeamInfoViewModel.teamNameDictionary["full_${team.id}"] ?: team.name,
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
}

// founded, city, country
@Composable
fun FBTeamInfoSecondItem(
    fbTeamInfoViewModel: FBTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by fbTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val team = it.team
        val venue = it.venue

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
}

// venue
@Composable
fun FBTeamInfoThirdItem(
    fbTeamInfoViewModel: FBTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by fbTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
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
                    text = fbTeamInfoViewModel.teamNameDictionary["venue_${displayModel?.team?.id}"] ?: venue.name,
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
        }
    }
}

// league stats
@Composable
fun FBTeamInfoFourthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbTeamInfoViewModel: FBTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by fbTeamInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val stats = it.stats
        val league = it.stats?.league

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

            league?.let {
                LeagueTitle(
                    url = league.logo,
                    leagueName = league.name,
                    leagueSeason = league.season,
                    modifier = Modifier.alpha(contentsAlpha)
                )
            }

            stats?.let {
                Row(
                    modifier = Modifier
                        .alpha(contentsAlpha)
                ) {
                    FBStatDataItem(
                        category = "승",
                        data = stats.fixtures.wins.total.toString()
                    )

                    FBStatDataItem(
                        category = "무",
                        data = stats.fixtures.draws.total.toString()
                    )

                    FBStatDataItem(
                        category = "패",
                        data = stats.fixtures.loses.total.toString()
                    )

                    FBStatDataItem(
                        category = "득점",
                        data = stats.goals.teamGoalsFor.total.total.toString()
                    )

                    FBStatDataItem(
                        category = "실점",
                        data = stats.goals.teamGoalsAgainst.total.total.toString()
                    )
                }
            }
        }
    }
}

// last game stats
@Composable
fun FBTeamInfoFifthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbTeamInfoViewModel: FBTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by fbTeamInfoViewModel.displayModel.collectAsState()

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .alpha(contentsAlpha)
                ) {
                    Text(
                        text = fbTeamInfoViewModel.teamNameDictionary["short_${lastGame.teams.home.id}"] ?: lastGame.teams.home.name,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = lastGame.goals.home.toString(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.3f),
                        color = if ((lastGame.goals.home) >= (lastGame.goals.away)) MaterialTheme.colors.primary else Color.Black
                    )

                    Text(
                        text = " vs ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.3f)
                    )

                    Text(
                        text = lastGame.goals.away.toString(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(0.3f),
                        color = if ((lastGame.goals.away) >= (lastGame.goals.home)) MaterialTheme.colors.primary else Color.Black
                    )

                    Text(
                        text = fbTeamInfoViewModel.teamNameDictionary["short_${lastGame.teams.away.id}"] ?: lastGame.teams.away.name,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = CalendarUtil.formatDate(lastGame.fixture.date),
                    fontSize = 15.sp,
                    modifier = Modifier.alpha(contentsAlpha)
                )
            }
        }
    }
}

// next game stats
@Composable
fun FBTeamInfoSixthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbTeamInfoViewModel: FBTeamInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by fbTeamInfoViewModel.displayModel.collectAsState()

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
                        text = fbTeamInfoViewModel.teamNameDictionary["short_${nextGame.teams.home.id}"] ?: nextGame.teams.home.name,
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
                        text = fbTeamInfoViewModel.teamNameDictionary["short_${nextGame.teams.away.id}"] ?: nextGame.teams.away.name,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = CalendarUtil.formatDate(nextGame.fixture.date),
                    fontSize = 15.sp,
                    modifier = Modifier.alpha(contentsAlpha)
                )
            }
        }
    }
}














