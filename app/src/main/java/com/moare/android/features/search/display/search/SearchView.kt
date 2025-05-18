package com.moare.android.features.search.display.search

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.moare.android.features.search.display.kbo.view.KBOPlayerInfoView
import com.moare.android.features.search.display.kbo.view.KBOPlayerStatsView
import com.moare.android.features.search.display.kbo.view.KBOTeamInfoView
import com.moare.android.features.search.display.mlb.view.MLBPlayerInfoView
import com.moare.android.features.search.display.mlb.view.MLBPlayerStatsView
import com.moare.android.features.search.display.mlb.view.MLBTeamInfoView
import com.moare.android.features.search.display.nba.view.NBAGameStatsView
import com.moare.android.features.search.display.nba.view.NBALeagueScheduleView
import com.moare.android.features.search.display.nba.view.NBALeagueTournamentView
import com.moare.android.features.search.display.nba.view.NBAPlayerInfoView
import com.moare.android.features.search.display.nba.view.NBAPlayerStandingsView
import com.moare.android.features.search.display.nba.view.NBAPlayerStatsView
import com.moare.android.features.search.display.nba.view.NBATeamInfoView
import com.moare.android.features.search.display.nba.view.NBATeamScheduleView
import com.moare.android.features.search.display.nba.view.NBATeamStandingsView
import com.moare.android.features.search.display.nba.view.NBATeamStatsView
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.ui.common.components.ProgressIndicator
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.theme.MoareAndroidTheme
import com.moare.android.ui.util.rememberKeyboardVisibility
import kotlinx.coroutines.delay

@Composable
fun SearchView(
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    /* ---------------------
       constants
       --------------------- */
    val barHeight = 50.dp

    /* ---------------------
       ui state
       --------------------- */
    var isNoticeVisible by remember { mutableStateOf(false) }
    var isNoticeOpened by remember { mutableStateOf(false) }
    var isSearchBarOpened by remember { mutableStateOf(false) }

    /* ---------------------
       viewmodel state
       --------------------- */
    val searchDataState by searchViewModel.searchDataState.collectAsState()
    val showResult by searchViewModel.resultVisibleState.collectAsState()
    val searchState by searchViewModel.searchState.collectAsState()
    val barFirstOpened by searchViewModel.barFirstOpened.collectAsState()
    val focusState by searchViewModel.focusState.collectAsState()
    val notice by searchViewModel.noticeData.collectAsState()

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

    // nba
    val nbaPlayerInfoData by searchViewModel.nbaPlayerInfoData.collectAsState()
    val nbaPlayerStatsData by searchViewModel.nbaPlayerStatsData.collectAsState()
    val nbaPlayerStandingsData by searchViewModel.nbaPlayerStandingsData.collectAsState()
    val nbaTeamInfoData by searchViewModel.nbaTeamInfoData.collectAsState()
    val nbaTeamStatsData by searchViewModel.nbaTeamStatsData.collectAsState()
    val nbaTeamStandingsData by searchViewModel.nbaTeamStandingsData.collectAsState()
    val nbaTeamScheduleData by searchViewModel.nbaTeamScheduleData.collectAsState()
    val nbaLeagueScheduleData by searchViewModel.nbaLeagueScheduleData.collectAsState()
    val nbaGameStatsData by searchViewModel.nbaGameStatsData.collectAsState()
    val nbaLeagueTournamentData by searchViewModel.nbaLeagueTournamentData.collectAsState()

    // kbo
    val kboPlayerInfoData by searchViewModel.kboPlayerInfoData.collectAsState()
    val kboPlayerStatsData by searchViewModel.kboPlayerStatsData.collectAsState()
    val kboPlayerStandingsData by searchViewModel.kboPlayerStandingsData.collectAsState()
    val kboTeamInfoData by searchViewModel.kboTeamInfoData.collectAsState()
    val kboTeamStatsData by searchViewModel.kboTeamStatsData.collectAsState()
    val kboTeamStandingsData by searchViewModel.kboTeamStandingsData.collectAsState()
    val kboLeagueScheduleData by searchViewModel.kboLeagueScheduleData.collectAsState()
    val kboGameStatsData by searchViewModel.kboGameStatsData.collectAsState()

    // mlb
    val mlbPlayerInfoData by searchViewModel.mlbPlayerInfoData.collectAsState()
    val mlbPlayerStatsData by searchViewModel.mlbPlayerStatsData.collectAsState()
    val mlbPlayerStandingsData by searchViewModel.mlbPlayerStandingsData.collectAsState()
    val mlbTeamInfoData by searchViewModel.mlbTeamInfoData.collectAsState()
    val mlbTeamStatsData by searchViewModel.mlbTeamStatsData.collectAsState()
    val mlbTeamStandingsData by searchViewModel.mlbTeamStandingsData.collectAsState()
    val mlbLeagueScheduleData by searchViewModel.mlbLeagueScheduleData.collectAsState()
    val mlbGameStatsData by searchViewModel.mlbGameStatsData.collectAsState()

    val query by searchViewModel.query.collectAsState()
    val autoCompleteList by searchViewModel.autoCompleteList.collectAsState()
    val autoCompleteListVisibleState by searchViewModel.autoCompleteListVisibleState.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val dataContainerCenter = remember { mutableStateOf(Offset.Zero) }
    val noticeAlpha by animateFloatAsState(
        targetValue = if (isNoticeOpened) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )

    /* ---------------------
       etc
       --------------------- */
    val keyboardVisibleState by rememberKeyboardVisibility()
    val noRippleInteractionSource = remember { MutableInteractionSource() }
    val activity = LocalActivity.current

    /* ---------------------
       LaunchedEffect
       --------------------- */
    LaunchedEffect(searchState, autoCompleteList) {
        isNoticeVisible = if (searchState) {
            isNoticeOpened = false
            false
        } else {
            if (barFirstOpened) {
                if (autoCompleteList.isEmpty()) {
                    true
                } else {
                    isNoticeOpened = false
                    false
                }
            } else {
                false
            }
        }
    }

    LaunchedEffect(barFirstOpened) {
        if (barFirstOpened) {
            delay(1000)
            isSearchBarOpened = true
            isNoticeVisible = true
        }
    }

    LaunchedEffect(keyboardVisibleState) {
        // NOTE: barFirstOpened prevents executing at first launch
        if (barFirstOpened && !keyboardVisibleState && focusState) {
            searchViewModel.send(SearchViewModel.Intent.ToggleFocusState(false))
        }
    }

    BackHandler {
        searchViewModel.send(SearchViewModel.Intent.GoBack(activity))
    }

    /* ---------------------
       ui
       --------------------- */
    Box(
        contentAlignment = Alignment.Center
    ) {
        /* ---------------------
           back button
           --------------------- */
        Column(
            Modifier.zIndex(1f)
        ) {
            Row {
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier.size(width = 34.dp, height = 50.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_arrow_back_24),
                        contentDescription = null,
                        tint = Moare,
                        modifier = Modifier
                            .clickable {
                                searchViewModel.send(SearchViewModel.Intent.GoBack(activity))
                            }
                    )
                }

                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.weight(1f))
        }

        /* ---------------------
           notice
           - info about providing data
           --------------------- */
        AnimatedVisibility(
            visible = isNoticeVisible,
            modifier = Modifier
                .zIndex(1f)
                .offset(x = (-12).dp, y = (-113).dp), // y: 전체 박스 높이(100 + 20 + 4) / 2 + (검색창 높이(50) + 트렌딩 키워드 높이(40)) / 2 + 추가 패딩 6,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row {
                Spacer(Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    NoticeBox(
                        notice = notice,
                        modifier = Modifier.alpha(noticeAlpha)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_rounded_info_24),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier
                            .padding(top = UIConstants.Padding.DEFAULT_V_PADDING)
                            .size(20.dp)
                            .clickable {
                                isNoticeOpened = !isNoticeOpened
                            }
                    )
                }
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
                        if (isNoticeOpened) {
                            isNoticeOpened = false
                        } else {
                            searchViewModel.send(SearchViewModel.Intent.ToggleFocusState(false))
                        }
                    }
                )
            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatingSearchBar(
                modifier = Modifier
//                    .padding(top = 10.dp)
            )

            // trending keywords
            AnimatedVisibility(
                visible = if (searchState) {
                    false
                } else {
                    if (isSearchBarOpened) {
                        autoCompleteList.isEmpty()
                    } else {
                        false
                    }
                },
                exit = if (searchState) fadeOut(tween(1000)) + shrinkVertically(tween(durationMillis = 1000)) else fadeOut() + shrinkVertically()
            ) {
                TrendingKeywords { keyword ->
                    searchViewModel.send(SearchViewModel.Intent.UpdateTextField(TextFieldValue(keyword), false))
                    searchViewModel.send(SearchViewModel.Intent.PerformSearch(searchType = SearchViewModel.SearchType.TRENDING_KEYWORD, aniDuration = 1000))
                }
            }

            // NOTE: didn't wrap with box because of AnimatedVisibility
            // autoComplete list
            AnimatedVisibility(
                visible = autoCompleteListVisibleState,
                enter = fadeIn() + expandVertically(tween(durationMillis = 1000)),
                exit = fadeOut(tween(1000)) + shrinkVertically(tween(durationMillis = 1000))
            ) {
//            key(System.currentTimeMillis()) {
                key(autoCompleteList) { // redraw the composable with its initial state
                    AutoCompleteList(
                        onItemSelected = { query ->
                            searchViewModel.send(SearchViewModel.Intent.UpdateTextField(TextFieldValue(query), false))
                            searchViewModel.send(SearchViewModel.Intent.PerformSearch(searchType = SearchViewModel.SearchType.AUTO_COMPLETE, aniDuration = 2000))
                        }
                    )
                }
            }

            // loading
            AnimatedVisibility(
                visible = searchDataState == ApiFetchState.Fetching,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                ProgressIndicator()
            }

            // search result
            AnimatedVisibility(
                visible = showResult,
                enter = fadeIn(tween(durationMillis = 500)) + expandVertically(tween(durationMillis = 1000)),
                exit = fadeOut(tween(durationMillis = 500)) + shrinkVertically(tween(durationMillis = 1000))
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 20.dp)
                ) {
                    // football
                    fbPlayerInfoData?.let {
                        FBPlayerInfoView(data = it)
                    }
                    fbPlayerStatsData?.let {
                        FBPlayerStatsView(data = it)
                    }
                    fbPlayerStandingsData?.let {
                        FBPlayerStandingsView(data = it)
                    }
                    fbTeamInfoData?.let {
                        FBTeamInfoView(data = it)
                    }
                    fbTeamStatsData?.let {
                        FBTeamStatsView(data = it)
                    }
                    fbTeamStandingsData?.let {
                        FBTeamStandingsView(data = it)
                    }
                    fbTeamScheduleData?.let {
                        FBTeamScheduleView(data = it)
                    }
                    fbLeagueScheduleData?.let {
                        FBLeagueScheduleView(data = it)
                    }
                    fbGameStatsData?.let {
                        FBGameStatsView(data = it)
                    }

                    // nba
                    nbaPlayerInfoData?.let {
                        NBAPlayerInfoView(data = it )
                    }
                    nbaPlayerStatsData?.let {
                        NBAPlayerStatsView(data = it)
                    }
                    nbaPlayerStandingsData?.let {
                        NBAPlayerStandingsView(data = it)
                    }
                    nbaTeamInfoData?.let {
                        NBATeamInfoView(data = it)
                    }
                    nbaTeamStatsData?.let {
                        NBATeamStatsView(data = it)
                    }
                    nbaTeamStandingsData?.let {
                        NBATeamStandingsView(data = it)
                    }
                    nbaTeamScheduleData?.let {
                        NBATeamScheduleView(data = it)
                    }
                    nbaLeagueScheduleData?.let {
                        NBALeagueScheduleView(data = it)
                    }
                    nbaGameStatsData?.let {
                        NBAGameStatsView(data = it)
                    }
                    nbaLeagueTournamentData?.let {
                        NBALeagueTournamentView(data = it)
                    }

                    // kbo
                    kboPlayerInfoData?.let {
                        KBOPlayerInfoView(data = it)
                    }
                    kboPlayerStatsData?.let {
                        KBOPlayerStatsView(data = it)
                    }
//                    kboPlayerStandingsData?.let {
//                        KBOPlayerStandingsView(data = it)
//                    }
                    kboTeamInfoData?.let {
                        KBOTeamInfoView(data = it)
                    }
//                    kboTeamStatsData?.let {
//                        KBOTeamStatsView(data = it)
//                    }
//                    kboTeamStandingsData?.let {
//                        KBOTeamStandingsView(data = it)
//                    }
//                    kboLeagueScheduleData?.let {
//                        KBOLeagueScheduleView(data = it)
//                    }
//                    kboGameStatsData?.let {
//                        KBOGameStatsView(data = it)
//                    }

                    // mlb
                    mlbPlayerInfoData?.let {
                        MLBPlayerInfoView(data = it)
                    }
                    mlbPlayerStatsData?.let {
                        MLBPlayerStatsView(data = it)
                    }
//                    mlbPlayerStandingsData?.let {
//                        MLBPlayerStandingsView(data = it)
//                    }
                    mlbTeamInfoData?.let {
                        MLBTeamInfoView(data = it)
                    }
//                    mlbTeamStatsData?.let {
//                        MLBTeamStatsView(data = it)
//                    }
//                    mlbTeamStandingsData?.let {
//                        MLBTeamStandingsView(data = it)
//                    }
//                    mlbLeagueScheduleData?.let {
//                        MLBLeagueScheduleView(data = it)
//                    }
//                    mlbGameStatsData?.let {
//                        MLBGameStatsView(data = it)
//                    }
                }
            }

            // no result / error
            AnimatedVisibility(
                visible = searchDataState is ApiFetchState.Error,
                enter = fadeIn()
            ) {
                val error = searchDataState as? ApiFetchState.Error
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