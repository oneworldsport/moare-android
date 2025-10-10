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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.moare.android.R
import com.moare.android.core.constants.UIConstants
import com.moare.android.core.mvi.AppViewModel
import com.moare.android.core.mvi.StackItem
import com.moare.android.features.search.display.football.view.FBGameStatsView
import com.moare.android.features.search.display.football.view.FBLeagueScheduleView
import com.moare.android.features.search.display.football.view.FBPlayerInfoView
import com.moare.android.features.search.display.football.view.FBPlayerStandingsView
import com.moare.android.features.search.display.football.view.FBPlayerStatsView
import com.moare.android.features.search.display.football.view.FBTeamInfoView
import com.moare.android.features.search.display.football.view.FBTeamStandingsView
import com.moare.android.features.search.display.football.view.FBTeamStatsView
import com.moare.android.features.search.display.kbo.view.KBOGameStatsView
import com.moare.android.features.search.display.kbo.view.KBOLeagueScheduleView
import com.moare.android.features.search.display.kbo.view.KBOPlayerInfoView
import com.moare.android.features.search.display.kbo.view.KBOPlayerStatsView
import com.moare.android.features.search.display.kbo.view.KBOTeamInfoView
import com.moare.android.features.search.display.kbo.view.KBOTeamStandingsView
import com.moare.android.features.search.display.kbo.view.KBOTeamStatsView
import com.moare.android.features.search.display.mlb.view.MLBGameStatsView
import com.moare.android.features.search.display.mlb.view.MLBLeagueScheduleView
import com.moare.android.features.search.display.mlb.view.MLBPlayerInfoView
import com.moare.android.features.search.display.mlb.view.MLBPlayerStatsView
import com.moare.android.features.search.display.mlb.view.MLBTeamInfoView
import com.moare.android.features.search.display.mlb.view.MLBTeamStandingsView
import com.moare.android.features.search.display.mlb.view.MLBTeamStatsView
import com.moare.android.features.search.display.nba.view.NBAGameStatsView
import com.moare.android.features.search.display.nba.view.NBALeagueScheduleView
import com.moare.android.features.search.display.nba.view.NBAPlayerInfoView
import com.moare.android.features.search.display.nba.view.NBAPlayerStandingsView
import com.moare.android.features.search.display.nba.view.NBAPlayerStatsView
import com.moare.android.features.search.display.nba.view.NBATeamInfoView
import com.moare.android.features.search.display.nba.view.NBATeamStandingsView
import com.moare.android.features.search.display.nba.view.NBATeamStatsView
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.ui.common.components.ProgressIndicator
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.theme.MoareAndroidTheme
import com.moare.android.ui.util.rememberKeyboardVisibility
import kotlinx.coroutines.delay
import java.util.Stack

@Composable
fun SearchView(
    viewModel: AppViewModel,
    searchStore: SearchViewModel,
    viewForTest: SportDisplayType? = null
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
    val stack by viewModel.stack.collectAsState()

    val displayModels by searchStore.displayModels.collectAsState()
    val searchDataState by searchStore.searchDataState.collectAsState()
    val showResult by searchStore.resultVisibleState.collectAsState()
    val searchState by searchStore.searchState.collectAsState()
    val barFirstOpened by searchStore.barFirstOpened.collectAsState()
    val focusState by searchStore.focusState.collectAsState()
    val notice by searchStore.noticeData.collectAsState()

    val query by searchStore.query.collectAsState()
    val autoCompleteList by searchStore.autoCompleteList.collectAsState()
    val autoCompleteListVisibleState by searchStore.autoCompleteListVisibleState.collectAsState()

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
    LaunchedEffect(viewForTest) {
        viewForTest?.let {
            searchStore.send(SearchViewModel.Intent.TestSearch(viewForTest))
        }
    }

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
            searchStore.send(SearchViewModel.Intent.ToggleFocusState(false))
        }
    }

    BackHandler {
//        searchViewModel.send(SearchViewModel.Intent.GoBack(activity))
        viewModel.pop()
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
//                                searchViewModel.send(SearchViewModel.Intent.GoBack(activity))
                                viewModel.pop()
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
                            searchStore.send(SearchViewModel.Intent.ToggleFocusState(false))
                        }
                    }
                )
            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatingSearchBar(
                searchViewModel = searchStore,
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
                TrendingKeywords(searchViewModel = searchStore) { keyword ->
                    searchStore.send(SearchViewModel.Intent.UpdateTextField(TextFieldValue(keyword), false))
                    searchStore.send(SearchViewModel.Intent.PerformSearch(searchType = SearchViewModel.SearchType.TRENDING_KEYWORD, aniDuration = 1000))
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
                        searchViewModel = searchStore,
                        onItemSelected = { query ->
                            searchStore.send(SearchViewModel.Intent.UpdateTextField(TextFieldValue(query), false))
                            searchStore.send(SearchViewModel.Intent.PerformSearch(searchType = SearchViewModel.SearchType.AUTO_COMPLETE, aniDuration = 2000))
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
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    when (val top = stack.lastOrNull()) {
                        is StackItem.FBPlayerInfo -> FBPlayerInfoView(searchStore, top.store)
                        is StackItem.FBPlayerStats -> FBPlayerStatsView(searchStore, top.store)
                        is StackItem.FBPlayerStandings -> FBPlayerStandingsView(searchStore, top.store)
                        is StackItem.FBTeamInfo -> FBTeamInfoView(searchStore, top.store)
                        is StackItem.FBTeamStats -> FBTeamStatsView(searchStore, top.store)
                        is StackItem.FBTeamStandings -> FBTeamStandingsView(searchStore, top.store)
                        is StackItem.FBLeagueSchedule -> FBLeagueScheduleView(searchStore, top.store)
                        is StackItem.FBGameStats -> FBGameStatsView(searchStore, top.store)

                        is StackItem.NBAPlayerInfo -> NBAPlayerInfoView(searchStore, top.store)
                        is StackItem.NBAPlayerStats -> NBAPlayerStatsView(searchStore, top.store)
                        is StackItem.NBAPlayerStandings -> NBAPlayerStandingsView(searchStore, top.store)
                        is StackItem.NBATeamInfo -> NBATeamInfoView(searchStore, top.store)
                        is StackItem.NBATeamStats -> NBATeamStatsView(searchStore, top.store)
                        is StackItem.NBATeamStandings -> NBATeamStandingsView(searchStore, top.store)
                        is StackItem.NBALeagueSchedule -> NBALeagueScheduleView(searchStore, top.store)
                        is StackItem.NBAGameStats -> NBAGameStatsView(searchStore, top.store)

                        is StackItem.MLBPlayerInfo -> MLBPlayerInfoView(searchStore, top.store)
                        is StackItem.MLBPlayerStats -> MLBPlayerStatsView(searchStore, top.store)
//                        displayModels[SportDisplayType.MLB_PLAYER_STANDINGS]?.let {
////                        MLBPlayerStandingsView(data = it)
//                            CenterColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
//                                Text(StringConstants.viewPreparingAdviseText("'MLB 선수 순위'"))
//                            }
//                        }
                        is StackItem.MLBTeamInfo -> MLBTeamInfoView(searchStore, top.store)
                        is StackItem.MLBTeamStats -> MLBTeamStatsView(searchStore, top.store)
                        is StackItem.MLBTeamStandings -> MLBTeamStandingsView(searchStore, top.store)
                        is StackItem.MLBLeagueSchedule -> MLBLeagueScheduleView(searchStore, top.store)
                        is StackItem.MLBGameStats -> MLBGameStatsView(searchStore, top.store)

                        is StackItem.KBOPlayerInfo -> KBOPlayerInfoView(searchStore, top.store)
                        is StackItem.KBOPlayerStats -> KBOPlayerStatsView(searchStore, top.store)
//                        displayModels[SportDisplayType.KBO_PLAYER_STANDINGS]?.let {
////                        KBOPlayerStandingsView(data = it)
//                            CenterColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
//                                Text(StringConstants.viewPreparingAdviseText("'KBO 선수 순위'"))
//                            }
//                        }
                        is StackItem.KBOTeamInfo -> KBOTeamInfoView(searchStore, top.store)
                        is StackItem.KBOTeamStats -> KBOTeamStatsView(searchStore, top.store)
                        is StackItem.KBOTeamStandings -> KBOTeamStandingsView(searchStore, top.store)
                        is StackItem.KBOLeagueSchedule -> KBOLeagueScheduleView(searchStore, top.store)
                        is StackItem.KBOGameStats -> KBOGameStatsView(searchStore, top.store)
                        else -> Unit
                    }

//                    displayModels[SportDisplayType.NBA_LEAGUE_TOURNAMENT]?.let {
//                        NBALeagueTournamentView(searchViewModel = searchViewModel,data = it as NBATournamentDisplayModel)
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