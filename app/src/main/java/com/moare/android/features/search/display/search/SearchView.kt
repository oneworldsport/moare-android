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
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.constants.UIConstants
import com.moare.android.features.search.display.football.view.FBLeagueScheduleView
import com.moare.android.features.search.display.football.view.FBGameStatsView
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
import com.moare.android.features.search.display.nba.view.NBALeagueTournamentView
import com.moare.android.features.search.display.nba.view.NBAPlayerInfoView
import com.moare.android.features.search.display.nba.view.NBAPlayerStandingsView
import com.moare.android.features.search.display.nba.view.NBAPlayerStatsView
import com.moare.android.features.search.display.nba.view.NBATeamInfoView
import com.moare.android.features.search.display.nba.view.NBATeamStandingsView
import com.moare.android.features.search.display.nba.view.NBATeamStatsView
import com.moare.android.features.search.display.search.viewmodel.SearchViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBALeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATournamentDisplayModel
import com.moare.android.ui.common.components.ProgressIndicator
import com.moare.android.ui.theme.Moare
import com.moare.android.ui.theme.MoareAndroidTheme
import com.moare.android.ui.util.CenterColumn
import com.moare.android.ui.util.rememberKeyboardVisibility
import kotlinx.coroutines.delay

@Composable
fun SearchView(
    searchViewModel: SearchViewModel = hiltViewModel(),
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
    val displayModels by searchViewModel.displayModels.collectAsState()
    val searchDataState by searchViewModel.searchDataState.collectAsState()
    val showResult by searchViewModel.resultVisibleState.collectAsState()
    val searchState by searchViewModel.searchState.collectAsState()
    val barFirstOpened by searchViewModel.barFirstOpened.collectAsState()
    val focusState by searchViewModel.focusState.collectAsState()
    val notice by searchViewModel.noticeData.collectAsState()

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
    LaunchedEffect(viewForTest) {
        viewForTest?.let {
            searchViewModel.send(SearchViewModel.Intent.TestSearch(viewForTest))
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
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    // football
                    displayModels[SportDisplayType.FB_PLAYER_INFO]?.let {
                        FBPlayerInfoView(data = it as FBPlayerInfoDisplayModel)
                    }
                    displayModels[SportDisplayType.FB_PLAYER_STATS]?.let {
                        FBPlayerStatsView(data = it as FBPlayerStatsDisplayModel)
                    }
                    displayModels[SportDisplayType.FB_PLAYER_STANDINGS]?.let {
                        FBPlayerStandingsView(data = it as FBPlayerStandingsDisplayModel)
                    }
                    displayModels[SportDisplayType.FB_TEAM_INFO]?.let {
                        FBTeamInfoView(data = it as FBTeamInfoDisplayModel)
                    }
                    displayModels[SportDisplayType.FB_TEAM_STATS]?.let {
                        FBTeamStatsView(data = it as FBTeamStatsDisplayModel)
                    }
                    displayModels[SportDisplayType.FB_TEAM_STANDINGS]?.let {
                        FBTeamStandingsView(data = it as FBTeamStandingsDisplayModel)
                    }
                    displayModels[SportDisplayType.FB_LEAGUE_SCHEDULE]?.let {
                        FBLeagueScheduleView(data = it as FBLeagueScheduleDisplayModel)
                    }
                    displayModels[SportDisplayType.FB_GAME_STATS]?.let {
                        FBGameStatsView(data = it as FBGameStatsDisplayModel)
                    }
                    // nba
                    displayModels[SportDisplayType.NBA_PLAYER_INFO]?.let {
                        NBAPlayerInfoView(data = it as NBAPlayerInfoDisplayModel)
                    }
                    displayModels[SportDisplayType.NBA_PLAYER_STATS]?.let {
                        NBAPlayerStatsView(data = it as NBAPlayerStatsDisplayModel)
                    }
                    displayModels[SportDisplayType.NBA_PLAYER_STANDINGS]?.let {
                        NBAPlayerStandingsView(data = it as NBAPlayerStandingsDisplayModel)
                    }
                    displayModels[SportDisplayType.NBA_TEAM_INFO]?.let {
                        NBATeamInfoView(data = it as NBATeamInfoDisplayModel)
                    }
                    displayModels[SportDisplayType.NBA_TEAM_STATS]?.let {
                        NBATeamStatsView(data = it as NBATeamStatsDisplayModel)
                    }
                    displayModels[SportDisplayType.NBA_TEAM_STANDINGS]?.let {
                        NBATeamStandingsView(data = it as NBATeamStandingsDisplayModel)
                    }
                    displayModels[SportDisplayType.NBA_LEAGUE_SCHEDULE]?.let {
                        NBALeagueScheduleView(data = it as NBALeagueScheduleDisplayModel)
                    }
                    displayModels[SportDisplayType.NBA_GAME_STATS]?.let {
                        NBAGameStatsView(data = it as NBAGameStatsDisplayModel)
                    }
                    displayModels[SportDisplayType.NBA_LEAGUE_TOURNAMENT]?.let {
                        NBALeagueTournamentView(data = it as NBATournamentDisplayModel)
                    }
                    // kbo
                    displayModels[SportDisplayType.KBO_PLAYER_INFO]?.let {
                        KBOPlayerInfoView(data = it as KBOPlayerInfoDisplayModel)
                    }
                    displayModels[SportDisplayType.KBO_PLAYER_STATS]?.let {
                        KBOPlayerStatsView(data = it as KBOPlayerStatsDisplayModel)
                    }
                    displayModels[SportDisplayType.KBO_PLAYER_STANDINGS]?.let {
//                        KBOPlayerStandingsView(data = it)
                        CenterColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                            Text(StringConstants.viewPreparingAdviseText("'KBO 선수 순위'"))
                        }
                    }
                    displayModels[SportDisplayType.KBO_TEAM_INFO]?.let {
                        KBOTeamInfoView(data = it as KBOTeamInfoDisplayModel)
                    }
                    displayModels[SportDisplayType.KBO_TEAM_STATS]?.let {
                        KBOTeamStatsView(data = it as KBOTeamStatsDisplayModel)
                    }
                    displayModels[SportDisplayType.KBO_TEAM_STANDINGS]?.let {
                        KBOTeamStandingsView(data = it as KBOTeamStandingsDisplayModel)
                    }
                    displayModels[SportDisplayType.KBO_LEAGUE_SCHEDULE]?.let {
                        KBOLeagueScheduleView(data = it as KBOLeagueScheduleDisplayModel)
                    }
                    displayModels[SportDisplayType.KBO_GAME_STATS]?.let {
                        KBOGameStatsView(data = it as KBOGameStatsDisplayModel)
                    }
                    // mlb
                    displayModels[SportDisplayType.MLB_PLAYER_INFO]?.let {
                        MLBPlayerInfoView(data = it as MLBPlayerInfoDisplayModel)
                    }
                    displayModels[SportDisplayType.MLB_PLAYER_STATS]?.let {
                        MLBPlayerStatsView(data = it as MLBPlayerStatsDisplayModel)
                    }
                    displayModels[SportDisplayType.MLB_PLAYER_STANDINGS]?.let {
//                        MLBPlayerStandingsView(data = it)
                        CenterColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                            Text(StringConstants.viewPreparingAdviseText("'MLB 선수 순위'"))
                        }
                    }
                    displayModels[SportDisplayType.MLB_TEAM_INFO]?.let {
                        MLBTeamInfoView(data = it as MLBTeamInfoDisplayModel)
                    }
                    displayModels[SportDisplayType.MLB_TEAM_STATS]?.let {
                        MLBTeamStatsView(data = it as MLBTeamStatsDisplayModel)
                    }
                    displayModels[SportDisplayType.MLB_TEAM_STANDINGS]?.let {
                        MLBTeamStandingsView(data = it as MLBTeamStandingsDisplayModel)
                    }
                    displayModels[SportDisplayType.MLB_LEAGUE_SCHEDULE]?.let {
                        MLBLeagueScheduleView(data = it as MLBLeagueScheduleDisplayModel)
                    }
                    displayModels[SportDisplayType.MLB_GAME_STATS]?.let {
                        MLBGameStatsView(data = it as MLBGameStatsDisplayModel)
                    }
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