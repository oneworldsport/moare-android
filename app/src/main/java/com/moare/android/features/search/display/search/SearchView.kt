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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.moare.android.R
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
import com.moare.android.features.search.display.football.view.FBTournamentView
import com.moare.android.features.search.display.kbo.view.KBOGameStatsView
import com.moare.android.features.search.display.kbo.view.KBOLeagueScheduleView
import com.moare.android.features.search.display.kbo.view.KBOPlayerInfoView
import com.moare.android.features.search.display.kbo.view.KBOPlayerStatsView
import com.moare.android.features.search.display.kbo.view.KBOTeamInfoView
import com.moare.android.features.search.display.kbo.view.KBOTeamStandingsView
import com.moare.android.features.search.display.kbo.view.KBOTeamStatsView
import com.moare.android.features.search.display.kbo.view.KBOTournamentView
import com.moare.android.features.search.display.mlb.view.MLBGameStatsView
import com.moare.android.features.search.display.mlb.view.MLBLeagueScheduleView
import com.moare.android.features.search.display.mlb.view.MLBPlayerInfoView
import com.moare.android.features.search.display.mlb.view.MLBPlayerStatsView
import com.moare.android.features.search.display.mlb.view.MLBTeamInfoView
import com.moare.android.features.search.display.mlb.view.MLBTeamStandingsView
import com.moare.android.features.search.display.mlb.view.MLBTeamStatsView
import com.moare.android.features.search.display.mlb.view.MLBTournamentView
import com.moare.android.features.search.display.nba.view.NBAGameStatsView
import com.moare.android.features.search.display.nba.view.NBALeagueScheduleView
import com.moare.android.features.search.display.nba.view.NBAPlayerInfoView
import com.moare.android.features.search.display.nba.view.NBAPlayerStandingsView
import com.moare.android.features.search.display.nba.view.NBAPlayerStatsView
import com.moare.android.features.search.display.nba.view.NBATeamInfoView
import com.moare.android.features.search.display.nba.view.NBATeamStandingsView
import com.moare.android.features.search.display.nba.view.NBATeamStatsView
import com.moare.android.features.search.display.nba.view.NBATournamentView
import com.moare.android.features.search.display.search.store.SearchAction
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.display.tennis.view.TennisGameStatsView
import com.moare.android.features.search.display.tennis.view.TennisLeagueScheduleView
import com.moare.android.features.search.display.tennis.view.TennisTournamentView
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.ui.common.components.ProgressIndicator
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.theme.MoareAndroidTheme
import com.moare.android.ui.util.rememberKeyboardVisibility
import kotlinx.coroutines.delay

@Composable
fun SearchView(
    viewModel: AppViewModel,
    searchStore: SearchStore,
    viewForTest: SportDisplayType? = null
) {
    val density = LocalDensity.current

    /* ---------------------
       ui state
       --------------------- */
    var isNoticeVisible by remember { mutableStateOf(false) }
    var isNoticeOpened by remember { mutableStateOf(false) }
    var isSearchExampleVisible by remember { mutableStateOf(false) }
    var isSearchExampleOpened by remember { mutableStateOf(false) }
    var noticeBoxHeight by remember { mutableStateOf(0.dp) }
    var searchExampleBoxHeight by remember { mutableStateOf(0.dp) }
    var isSearchBarOpened by remember { mutableStateOf(false) }
    var leagueKeywordsComponentHeight by remember { mutableStateOf(0.dp) }

    // notice 아이콘 y 위치
    // y: (전체 컨텐츠 높이(박스 높이(boxHeight) + 아이콘 높이(20) + padding(6))) / 2 + (검색창 높이(50) + 트렌딩 키워드 높이(40)) / 2 + 추가 패딩 8 + leagueKeywords컴포넌트 높이 / 2
    // searchExampleBoxHeight가 noticeBoxHeight보다 높은 경우는 전체 컨텐츠 높이를 계산할때 searchExampleBoxHeight를 기준으로 해야함
    // TODO: noticeBoxHeight가 다시 계산될때 위치가 조정되면서 깜빡이는 이슈가 있는데 해결 필요. 처음 화면이 열리고 noticeYOffset 설정되고 나면 바뀔 필요가 없음.
    val boxHeight = maxOf(searchExampleBoxHeight, noticeBoxHeight)
    val noticeYOffset = ((boxHeight + 20.dp + 6.dp) / 2) + ((50.dp + 40.dp) / 2) + 8.dp + (leagueKeywordsComponentHeight / 2)

    /* ---------------------
       viewmodel state
       --------------------- */
    val stack by viewModel.stack.collectAsState()
    val didPop by viewModel.didPop.collectAsState()
    val includesPreviousView by viewModel.includesPreviousView.collectAsState()

    val searchDataState by searchStore.searchDataState.collectAsState()
    val showResult by searchStore.resultVisibleState.collectAsState()
    val searchState by searchStore.searchState.collectAsState()
    val barFirstOpened by searchStore.barFirstOpened.collectAsState()
    val focusState by searchStore.focusState.collectAsState()
    val noticeList by searchStore.noticeList.collectAsState()
    val searchExample by searchStore.searchExample.collectAsState()
    val leagueKeywords by searchStore.leagueKeyowrds.collectAsState()

    val autoCompleteList by searchStore.autoCompleteList.collectAsState()
    val autoCompleteListVisibleState by searchStore.autoCompleteListVisibleState.collectAsState()

    /* ---------------------
       animation
       --------------------- */
    val noticeAlpha by animateFloatAsState(
        targetValue = if (isNoticeOpened) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        )
    )
    val searchExampleAlpha by animateFloatAsState(
        targetValue = if (isSearchExampleOpened) 1f else 0f,
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
            searchStore.send(SearchAction.TestSearch(viewForTest))
        }
    }

    LaunchedEffect(searchState, autoCompleteList) {
        if (searchState) {
            isNoticeOpened = false
            isNoticeVisible = false
            isSearchExampleOpened = false
            isSearchExampleVisible = false
        } else {
            if (barFirstOpened) {
                if (autoCompleteList.isEmpty()) {
                    isNoticeVisible = true
                    isSearchExampleVisible = true
                } else {
                    isNoticeOpened = false
                    isNoticeVisible = false
                    isSearchExampleOpened = false
                    isSearchExampleVisible = false
                }
            } else {
                isNoticeVisible = false
                isSearchExampleVisible = false
            }
        }
    }

    LaunchedEffect(barFirstOpened) {
        if (barFirstOpened) {
            delay(1000)
            isSearchBarOpened = true
            isNoticeVisible = true
            isSearchExampleVisible = true
        }
    }

    LaunchedEffect(keyboardVisibleState) {
        // NOTE: barFirstOpened prevents executing at first launch
        if (barFirstOpened && !keyboardVisibleState && focusState) {
            searchStore.send(SearchAction.ToggleFocusState(false))
        }
    }

    LaunchedEffect(Unit) {
        // LeagueKeywords 가져오기
        searchStore.send(SearchAction.GetLeagueKeywords)
    }

    BackHandler {
        viewModel.pop(activity)
    }

    /* ---------------------
       ui
       --------------------- */
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clickable(
                interactionSource = noRippleInteractionSource,
                indication = null,
                onClick = {
                    if (isNoticeOpened || isSearchExampleOpened) {
                        isNoticeOpened = false
                        isSearchExampleOpened = false
                    } else {
                        searchStore.send(SearchAction.ToggleFocusState(false))
                    }
                }
            )
            .imePadding() // 키보드 높이 반영
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
                                viewModel.pop(activity)
                            }
                    )
                }

                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.weight(1f))
        }

        /* ---------------------
           notice, search example
           --------------------- */
//        Row(
//            verticalAlignment = Alignment.Bottom,
//            modifier = Modifier
//                .zIndex(1f)
//                .padding(horizontal = 12.dp)
//                .offset(x = 0.dp, y = -(noticeYOffset))
//        ) {
//            AnimatedVisibility(
//                visible = isSearchExampleVisible,
//                enter = fadeIn(),
//                exit = fadeOut()
//            ) {
//                Column(
//                    horizontalAlignment = Alignment.Start
//                ) {
//                    SearchExampleBox(
//                        text = searchExample,
//                        modifier = Modifier.alpha(searchExampleAlpha)
//                    ) { height ->
//                        searchExampleBoxHeight = height
//                    }
//
//                    Box(
//                        contentAlignment = Alignment.BottomCenter,
//                        modifier = Modifier
//                            .padding(top = 6.dp)
//                            .height(20.dp)
//                            .alpha(0.7f)
//                            .clickable {
//                                isSearchExampleOpened = !isSearchExampleOpened
//                            }
//                    ) {
//                        Text(
//                            text = "검색 예시",
//                            fontSize = 13.sp,
//                            color = Color.Gray,
//                        )
//                    }
//                }
//            }
//
//            Spacer(Modifier.weight(1f))
//
//            AnimatedVisibility(
//                visible = isNoticeVisible,
//                enter = fadeIn(),
//                exit = fadeOut()
//            ) {
//                Column(
//                    horizontalAlignment = Alignment.End
//                ) {
//                    NoticeBox(
//                        noticeList = noticeList,
//                        height = noticeBoxHeight,
//                        modifier = Modifier.alpha(noticeAlpha),
//                        enabled = noticeAlpha > 0f  // 닫혀있을때 눌림 방지
//                    ) { height ->
//                        noticeBoxHeight = height
//                    }
//
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_rounded_info_24),
//                        contentDescription = null,
//                        tint = Color.Gray,
//                        modifier = Modifier
//                            .padding(top = 6.dp)
//                            .size(20.dp)
//                            .alpha(0.7f)
//                            .clickable {
//                                isNoticeOpened = !isNoticeOpened
//                            }
//                    )
//                }
//            }
//        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatingSearchBar(
                searchStore = searchStore,
                modifier = Modifier
//                    .padding(top = 10.dp)
            )

            // trending keywords
//            AnimatedVisibility(
//                visible = if (searchState) {
//                    false
//                } else {
//                    if (isSearchBarOpened) {
//                        autoCompleteList.isEmpty()
//                    } else {
//                        false
//                    }
//                },
//                exit = if (searchState) fadeOut(tween(1000)) + shrinkVertically(tween(durationMillis = 1000)) else fadeOut() + shrinkVertically()
//            ) {
//                TrendingKeywords(searchStore = searchStore) { keyword ->
//                    searchStore.send(SearchAction.UpdateTextField(TextFieldValue(keyword), false))
//                    searchStore.send(SearchAction.PerformSearch(searchType = SearchStore.SearchType.TrendingKeyword, aniDuration = 1000))
//                }
//            }

            // league keywords
//            AnimatedVisibility(
//                visible = if (searchState) {
//                    false
//                } else {
//                    if (isSearchBarOpened) {
//                        autoCompleteList.isEmpty()
//                    } else {
//                        false
//                    }
//                },
//                exit = if (searchState) fadeOut(tween(1000)) + shrinkVertically(tween(durationMillis = 1000)) else fadeOut() + shrinkVertically()
//            ) {
//                leagueKeywords?.let {
//                    LeagueKeywords(
//                        leagueKeywords = it,
//                        modifier = Modifier
//                            .padding(top = 16.dp)
//                            .onGloballyPositioned { layoutCoordinates ->
//                                with(density) {
//                                    leagueKeywordsComponentHeight = layoutCoordinates.size.height.toDp() + 16.dp
//                                }
//                            }
//                    ) { keywordInfo ->
//                        searchStore.send(SearchAction.UpdateTextField(TextFieldValue(keywordInfo.keyword), false))
//                        searchStore.send(SearchAction.PerformSearch(searchType = SearchStore.SearchType.LeagueKeyword(keywordInfo), aniDuration = 1000))
//                    }
//                }
//            }

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
                        searchStore = searchStore,
                        onItemSelected = { query ->
                            searchStore.send(SearchAction.UpdateTextField(TextFieldValue(query), false))
                            searchStore.send(SearchAction.PerformSearch(searchType = SearchStore.SearchType.AutoComplete, aniDuration = 2000))
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
                    if (includesPreviousView) {
                        stack.takeLast(2).firstOrNull()?.let { item ->
                            StackItemView(
                                searchStore = searchStore,
                                item = item,
                                didPop = true,
                                isCombinedView = true
                            )
                            // isCombinedView
                        }
                    }

                    stack.lastOrNull()?.let { item ->
                        StackItemView(
                            searchStore = searchStore,
                            item = item,
                            didPop = didPop,
                            isCombinedView = includesPreviousView
                        )
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

@Composable
fun StackItemView(
    searchStore: SearchStore,
    item: StackItem,
    didPop: Boolean,
    isCombinedView: Boolean
) {
    when (item) {
        is StackItem.FBPlayerInfo -> FBPlayerInfoView(searchStore, item.store)
        is StackItem.FBPlayerStats -> FBPlayerStatsView(searchStore, item.store)
        is StackItem.FBPlayerStandings -> FBPlayerStandingsView(searchStore, item.store)
        is StackItem.FBTeamInfo -> FBTeamInfoView(searchStore, item.store)
        is StackItem.FBTeamStats -> FBTeamStatsView(searchStore, item.store)
        is StackItem.FBTeamStandings -> FBTeamStandingsView(searchStore, item.store)
        is StackItem.FBLeagueSchedule -> FBLeagueScheduleView(searchStore, item.store, didPop, isCombinedView)
        is StackItem.FBGameStats -> FBGameStatsView(searchStore, item.store, isCombinedView)
        is StackItem.FBTournament -> FBTournamentView(searchStore, item.store)

        is StackItem.NBAPlayerInfo -> NBAPlayerInfoView(searchStore, item.store)
        is StackItem.NBAPlayerStats -> NBAPlayerStatsView(searchStore, item.store)
        is StackItem.NBAPlayerStandings -> NBAPlayerStandingsView(searchStore, item.store)
        is StackItem.NBATeamInfo -> NBATeamInfoView(searchStore, item.store)
        is StackItem.NBATeamStats -> NBATeamStatsView(searchStore, item.store)
        is StackItem.NBATeamStandings -> NBATeamStandingsView(searchStore, item.store)
        is StackItem.NBALeagueSchedule -> NBALeagueScheduleView(searchStore, item.store, didPop)
        is StackItem.NBAGameStats -> NBAGameStatsView(searchStore, item.store)
        is StackItem.NBATournament -> NBATournamentView(searchStore, item.store)

        is StackItem.MLBPlayerInfo -> MLBPlayerInfoView(searchStore, item.store)
        is StackItem.MLBPlayerStats -> MLBPlayerStatsView(searchStore, item.store)
//        displayModels[SportDisplayType.MLB_PLAYER_STANDINGS]?.let {
////                        MLBPlayerStandingsView(data = it)
//            CenterColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
//                Text(StringConstants.viewPreparingAdviseText("'MLB 선수 순위'"))
//            }
//        }
        is StackItem.MLBTeamInfo -> MLBTeamInfoView(searchStore, item.store)
        is StackItem.MLBTeamStats -> MLBTeamStatsView(searchStore, item.store)
        is StackItem.MLBTeamStandings -> MLBTeamStandingsView(searchStore, item.store)
        is StackItem.MLBLeagueSchedule -> MLBLeagueScheduleView(searchStore, item.store, didPop)
        is StackItem.MLBGameStats -> MLBGameStatsView(searchStore, item.store)
        is StackItem.MLBTournament -> MLBTournamentView(searchStore, item.store)

        is StackItem.KBOPlayerInfo -> KBOPlayerInfoView(searchStore, item.store)
        is StackItem.KBOPlayerStats -> KBOPlayerStatsView(searchStore, item.store)
//        displayModels[SportDisplayType.KBO_PLAYER_STANDINGS]?.let {
////                        KBOPlayerStandingsView(data = it)
//            CenterColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
//                Text(StringConstants.viewPreparingAdviseText("'KBO 선수 순위'"))
//            }
//        }
        is StackItem.KBOTeamInfo -> KBOTeamInfoView(searchStore, item.store)
        is StackItem.KBOTeamStats -> KBOTeamStatsView(searchStore, item.store)
        is StackItem.KBOTeamStandings -> KBOTeamStandingsView(searchStore, item.store)
        is StackItem.KBOLeagueSchedule -> KBOLeagueScheduleView(searchStore, item.store, didPop)
        is StackItem.KBOGameStats -> KBOGameStatsView(searchStore, item.store)
        is StackItem.KBOTournament -> KBOTournamentView(searchStore, item.store)

        is StackItem.TennisLeagueSchedule -> TennisLeagueScheduleView(searchStore, item.store, didPop)
        is StackItem.TennisGameStats -> TennisGameStatsView(searchStore, item.store)
        is StackItem.TennisTournament -> TennisTournamentView(searchStore, item.store)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MoareAndroidTheme {
//        SearchView()
    }
}