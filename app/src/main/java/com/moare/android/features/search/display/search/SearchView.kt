package com.moare.android.features.search.display.search

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.moare.android.R
import com.moare.android.core.constants.UIConstants
import com.moare.android.features.search.display.football.view.FBLeagueScheduleView
import com.moare.android.features.search.display.football.view.FBGameStatsView
import com.moare.android.features.search.display.football.view.FBPlayerInfoView
import com.moare.android.features.search.display.football.view.FBPlayerStandingsView
import com.moare.android.features.search.display.football.view.FBPlayerStatsView
import com.moare.android.features.search.display.football.view.FBTeamInfoView
import com.moare.android.features.search.display.football.view.FBTeamScheduleView
import com.moare.android.features.search.display.football.view.FBTeamStandingsView
import com.moare.android.features.search.display.football.view.FBTeamStatsView
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.SearchDataState
import com.moare.android.ui.theme.MoareAndroidTheme

@Composable
fun SearchView(
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    /* ---------------------
       ui state
       --------------------- */
    var isInfoVisible by remember { mutableStateOf(true) }
    var isInfoOpened by remember { mutableStateOf(false) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val searchDataState by searchViewModel.searchDataState.collectAsState()
    val showResult by searchViewModel.resultVisibleState.collectAsState()
    val searchState by searchViewModel.searchState.collectAsState()

    // football
    val fbPlayerInfoData by searchViewModel.fbPlayerInfoData.collectAsState()
    val fbPlayerStatsData by searchViewModel.fbPlayerStatsData.collectAsState()
    val fbPlayerStandingsData by searchViewModel.fbPlayerStandingsData.collectAsState()
    val fbTeamInfoData by searchViewModel.fbTeamInfoData.collectAsState()
    val fbTeamStatsData by searchViewModel.fbTeamStatsData.collectAsState()
    val fbTeamStandingsData by searchViewModel.fbTeamStandingsData.collectAsState()
    val fbTeamScheduleData by searchViewModel.fbTeamScheduleData.collectAsState()
    val fbLeagueScheduleData by searchViewModel.fbLeagueScheduleData.collectAsState()
    val fbGameStatsData by searchViewModel.fbGameStatsData.collectAsState()

    val query by searchViewModel.query.collectAsState()
    val autoCompleteList by searchViewModel.autoCompleteList.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val dataContainerCenter = remember { mutableStateOf(Offset.Zero) }

    /* ---------------------
       etc
       --------------------- */
    val focusManager = LocalFocusManager.current
    val noRippleInteractionSource = remember { MutableInteractionSource() }

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(searchState, autoCompleteList) {
        isInfoVisible = if (searchState) {
            false
        } else {
            autoCompleteList.isEmpty()
        }
    }

    BackHandler {
        searchViewModel.send(SearchViewModel.Intent.GoBack)
    }

    /* ---------------------
       ui
       --------------------- */
    Box {
        // info about currently providing data
        AnimatedVisibility(
            visible = isInfoVisible,
            modifier = Modifier
                .size(width = 250.dp, height = 140.dp)
                .align(Alignment.CenterEnd)
                .offset(x = (-20).dp, y = (-98).dp)
                .zIndex(1f),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                AnimatedVisibility(
                    visible = isInfoOpened,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .weight(1f)
                ) {
                    // wrapped with box because border's fadeOut animation is not applied if it is in AnimatedVisibility's modifier
                    Box(
                        Modifier
                            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(UIConstants.CornerRadius.small))
                    ) {
                        Column(
                            Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "현재 제공중인 스포츠 데이터:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Text(
                                text = "• 프리미어리그 24/25",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "\n제공 예정 스포츠 데이터:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Text(
                                text = "• 라리가 24/25" +
                                        "\n• 분데스리가 24/25" +
                                        "\n• 리그 1 24/25" +
                                        "\n• 챔피언스리그 24/25",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Icon(
                    painter = painterResource(id = R.drawable.ic_info_24),
                    contentDescription = "ic_info_24",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(top = UIConstants.Padding.defalutVPadding)
                        .clickable {
                            isInfoOpened = !isInfoOpened
                        }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize()
                .clickable(
                    interactionSource = noRippleInteractionSource,
                    indication = null,
                    onClick = {
                        focusManager.clearFocus()
                    }
                )
            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatingSearchBar(
                modifier = Modifier
                    .padding(top = 10.dp)
            )

            // NOTE: didn't wrap with box because of AnimatedVisibility
            // autoComplete list
            AnimatedVisibility(
                visible = autoCompleteList.isNotEmpty(),
                enter = fadeIn() + expandVertically(tween(durationMillis = 1000))
            ) {
//            key(System.currentTimeMillis()) {
                key(autoCompleteList) { // redraw the composable with its initial state
                    AutoCompleteList(
                        onItemSelected = { query ->
                            searchViewModel.send(SearchViewModel.Intent.UpdateTextField(TextFieldValue(query), false))
                            searchViewModel.send(SearchViewModel.Intent.PerformSearch(2000))
                        }
                    )
                }
            }

            // loading
            AnimatedVisibility(
                visible = searchDataState == SearchDataState.Fetching
            ) {
                CircularProgressIndicator()
            }

            // search result
            AnimatedVisibility(
                visible = showResult,
                enter = fadeIn(tween(durationMillis = 500)) + expandVertically(tween(durationMillis = 1000)),
                exit = fadeOut(tween(durationMillis = 500)) + shrinkVertically(tween(durationMillis = 1000))
            ) {
                Column {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .onGloballyPositioned { layoutCoordinates ->
                                        // Calculate the center of the Box
                                        dataContainerCenter.value = Offset(
                                            x = layoutCoordinates.size.width / 2f,
                                            y = layoutCoordinates.size.height / 2f
                                        )
                                    }
                            ) {
                                // football_player_info
                                fbPlayerInfoData?.let {
                                    FBPlayerInfoView(
                                        data = it,
                                        center = dataContainerCenter
                                    )
                                }

                                // football_player_stats
                                fbPlayerStatsData?.let {
                                    FBPlayerStatsView(
                                        data = it,
                                        center = dataContainerCenter
                                    )
                                }

                                // football_team_info
                                fbTeamInfoData?.let {
                                    FBTeamInfoView(
                                        data = it,
                                        center = dataContainerCenter
                                    )
                                }

                                // football_team_stats
                                fbTeamStatsData?.let {
                                    FBTeamStatsView(
                                        data = it,
                                        center = dataContainerCenter
                                    )
                                }
                            }

                            // football_player_standings
                            fbPlayerStandingsData?.let {
                                FBPlayerStandingsView(
                                    data = it
                                )
                            }

                            // football_team_standings
                            fbTeamStandingsData?.let {
                                FBTeamStandingsView(
                                    data = it
                                )
                            }

                            // football_team_schedule
                            fbTeamScheduleData?.let {
                                FBTeamScheduleView(
                                    data = it
                                )
                            }

                            // football_league_schedule
                            fbLeagueScheduleData?.let {
                                FBLeagueScheduleView(
                                    data = it
                                )
                            }

                            // football_game_stats
                            fbGameStatsData?.let {
                                FBGameStatsView(
                                    data = it
                                )
                            }
                        }
                    }
                }
            }

            // no result / error
            AnimatedVisibility(
                visible = searchDataState is SearchDataState.Error,
                enter = fadeIn()
            ) {
                val error = searchDataState as? SearchDataState.Error
                error?.let {
                    Text(error.message)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MoareAndroidTheme {
//        SearchView()
    }
}