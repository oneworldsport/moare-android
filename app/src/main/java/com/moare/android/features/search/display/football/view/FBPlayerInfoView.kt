package com.moare.android.features.search.display.football.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.EnNameTranslationUtils
import com.moare.android.core.util.TranslationType
import com.moare.android.features.search.display.common.container.view.InfoViewContainer
import com.moare.android.features.search.display.common.container.component.MovingCapsuleItemContainer
import com.moare.android.features.search.display.components.FBStatDataItem
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoIntent
import com.moare.android.features.search.display.football.viewmodel.FBPlayerInfoViewModel
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.ui.common.components.HCapsuleBar
import com.moare.android.ui.common.components.LeagueTitle
import com.moare.android.ui.common.components.URLImage
import com.moare.android.ui.common.components.URLImageSize

@Composable
fun FBPlayerInfoView(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbPlayerInfoViewModel: FBPlayerInfoViewModel = hiltViewModel(),
    data: FBPlayerInfoDisplayModel
) {
    /* ---------------------
       viewmodel state
       --------------------- */
    val poppedView by searchViewModel.poppedView.collectAsState()

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(data) {
        if (poppedView == null || poppedView is SportDecodableModel.FBPlayerInfo) {
            fbPlayerInfoViewModel.send(FBPlayerInfoIntent.InitData(data))
        }
    }

    InfoViewContainer(itemCount = 6, measureContent = {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            FBPlayerInfoFirstItem(
                containerModifier = Modifier.weight(1f)
            ) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            FBPlayerInfoSecondItem(
                containerModifier = Modifier.weight(1f)
            ) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }

            FBPlayerInfoThirdItem(
                containerModifier = Modifier.weight(1f)
            ) { index, coordinates ->
                updateItemPosition(index, coordinates)
            }
        }

        FBPlayerInfoFourthItem { index, coordinates ->
            updateItemPosition(index, coordinates)
        }

        FBPlayerInfoFifthItem { index, coordinates ->
            updateItemPosition(index, coordinates)
        }

        FBPlayerInfoSixthItem { index, coordinates ->
            updateItemPosition(index, coordinates)
        }
    }, displayContent = {
        FBPlayerInfoFirstItem(
            isAniItem = true,
            itemSize = itemSizes[0],
            itemPosition = itemPositions[0],
            aniPosition = aniPositions,
            contentsAlpha = contentsAlpha
        )

        FBPlayerInfoSecondItem(
            isAniItem = true,
            itemSize = itemSizes[1],
            itemPosition = itemPositions[1],
            aniPosition = aniPositions,
            contentsAlpha = contentsAlpha
        )

        FBPlayerInfoThirdItem(
            isAniItem = true,
            itemSize = itemSizes[2],
            itemPosition = itemPositions[2],
            aniPosition = aniPositions,
            contentsAlpha = contentsAlpha
        )

        FBPlayerInfoFourthItem(
            isAniItem = true,
            itemSize = itemSizes[3],
            itemPosition = itemPositions[3],
            aniPosition = aniPositions,
            contentsAlpha = contentsAlpha
        )

        FBPlayerInfoFifthItem(
            isAniItem = true,
            itemSize = itemSizes[4],
            itemPosition = itemPositions[4],
            aniPosition = aniPositions,
            contentsAlpha = contentsAlpha
        )

        FBPlayerInfoSixthItem(
            isAniItem = true,
            itemSize = itemSizes[5],
            itemPosition = itemPositions[5],
            aniPosition = aniPositions,
            contentsAlpha = contentsAlpha
        )
    })
}


// photo, name
@Composable
fun FBPlayerInfoFirstItem(
    fbPlayerInfoViewModel: FBPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by fbPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.info

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
                url = player.photo,
                modifier = Modifier.alpha(contentsAlpha)
            )

            Text(
                text = fbPlayerInfoViewModel.playerNameDictionary["${player.id}"] ?: player.name,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(contentsAlpha)
            )

            Text(
                text = player.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                maxLines = 2,
                modifier = Modifier.alpha(contentsAlpha)
            )
        }
    }
}

// age, birth, nationality
@Composable
fun FBPlayerInfoSecondItem(
    fbPlayerInfoViewModel: FBPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by fbPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.info

        var nationalityKrName by remember { mutableStateOf("") }

        LaunchedEffect(player) {
            nationalityKrName = EnNameTranslationUtils.translateByDic(TranslationType.COUNTRY, input = player.nationality)
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
                    text = "국적: ",
                    fontSize = 15.sp
                )

                Text(
                    text = nationalityKrName,
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
                    text = player.birth.date,
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
                    text = "${CalendarUtil.calculateAge(player.birth.date)}",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// weight, height
@Composable
fun FBPlayerInfoThirdItem(
    fbPlayerInfoViewModel: FBPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    containerModifier: Modifier = Modifier,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by fbPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val player = it.info

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
                    text = "키: ",
                    fontSize = 15.sp
                )

                Text(
                    text = player.height,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(contentsAlpha)
            ) {
                Text(
                    text = "몸무게: ",
                    fontSize = 15.sp
                )

                Text(
                    text = player.weight,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// league stats
@Composable
fun FBPlayerInfoFourthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbPlayerInfoViewModel: FBPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by fbPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let { displayModel ->
        val stats = displayModel.stats
        val team = stats?.team
        val league = stats?.league

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
                searchViewModel.send(SearchViewModel.Intent.ShowPlayerStats(playerId = displayModel.info.id))
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

            Row(
                modifier = Modifier
                    .alpha(contentsAlpha)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(
                        text = "소속팀",
                        fontSize = 15.sp
                    )

                    team?.let {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(fbPlayerInfoViewModel.itemHeight)
                        ) {
                            URLImage(
                                url = team.logo,
                                modifier = Modifier.padding(end = 4.dp),
                                size = URLImageSize.SMALL
                            )

                            Text(
                                text = fbPlayerInfoViewModel.teamNameDictionary["full_${team.id}"] ?: team.name,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                stats?.let {
                    FBStatDataItem(
                        category = "경기수",
                        data = stats.games.appearences.toString()
                    )

                    FBStatDataItem(
                        category = "골",
                        data = stats.goals.total.toString()
                    )

                    FBStatDataItem(
                        category = "도움",
                        data = stats.goals.assists.toString()
                    )
                }
            }
        }
    }
}

// last game stats
@Composable
fun FBPlayerInfoFifthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbPlayerInfoViewModel: FBPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by fbPlayerInfoViewModel.displayModel.collectAsState()

    displayModel?.let {
        val lastGame = it.lastGame
        val lastGamePlayerStats = it.lastGamePlayerStats

        MovingCapsuleItemContainer(
            isAniItem = isAniItem,
            itemSize = itemSize,
            itemPosition = itemPosition,
            aniPosition = aniPosition,
            updateItemPosition = { coordinates ->
                updateItemPosition?.let { it(4, coordinates) }
            },
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(top = if (isAniItem) 0.dp else 12.dp),
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .alpha(contentsAlpha)
            ) {
                lastGame?.let {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = fbPlayerInfoViewModel.teamNameDictionary["short_${lastGame.teams.home.id}"] ?: lastGame.teams.home.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Light,
                                maxLines = 1
                            )

                            Text(
                                text = lastGame.goals.home.toString(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = if ((lastGame.goals.home) >= (lastGame.goals.away)) MaterialTheme.colors.primary else Color.Black
                            )

                            Text(
                                text = " vs ",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = lastGame.goals.away.toString(),
                                fontWeight = FontWeight.Medium,
                                color = if ((lastGame.goals.away) >= (lastGame.goals.home)) MaterialTheme.colors.primary else Color.Black
                            )

                            Text(
                                text = fbPlayerInfoViewModel.teamNameDictionary["short_${lastGame.teams.away.id}"] ?: lastGame.teams.away.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Light,
                                maxLines = 1
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.height(fbPlayerInfoViewModel.itemHeight)
                        ) {
                            Text(
                                text = CalendarUtil.formatDate(lastGame.fixture.date),
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                lastGamePlayerStats?.let {
                    FBStatDataItem(
                        category = "출전시간",
                        data = "${
                            if (lastGamePlayerStats.games.substitute) {
                                "후보"
                            } else {
                                "선발"
                            }
                        } / ${lastGamePlayerStats.games.minutes}분",
                        customWidth = 80.dp
                    )

                    FBStatDataItem(
                        category = "골",
                        data = lastGamePlayerStats.goals.total.toString()
                    )

                    FBStatDataItem(
                        category = "도움",
                        data = lastGamePlayerStats.goals.assists.toString()
                    )
                }
            }
        }
    }
}

// next game stats
@Composable
fun FBPlayerInfoSixthItem(
    searchViewModel: SearchViewModel = hiltViewModel(),
    fbPlayerInfoViewModel: FBPlayerInfoViewModel = hiltViewModel(),
    isAniItem: Boolean = false,
    itemSize: DpSize? = null,
    itemPosition: Offset? = null,
    aniPosition: Boolean = true,
    contentsAlpha: Float = 0f,
    updateItemPosition: ((Int, LayoutCoordinates) -> Unit)? = null
) {
    val displayModel by fbPlayerInfoViewModel.displayModel.collectAsState()

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
            modifier = Modifier
                .padding(top = if (isAniItem) 0.dp else 12.dp),
            onClick = {
                searchViewModel.send(SearchViewModel.Intent.ShowGameStats(gameType = "next"))
            }
        ) {
            HCapsuleBar()

            Text(
                text = "다음경기",
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
                        text = fbPlayerInfoViewModel.teamNameDictionary["short_${nextGame.teams.home.id}"] ?: nextGame.teams.home.name,
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
                        text = fbPlayerInfoViewModel.teamNameDictionary["short_${nextGame.teams.away.id}"] ?: nextGame.teams.away.name,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start,
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