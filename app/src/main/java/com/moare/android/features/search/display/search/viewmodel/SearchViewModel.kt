package com.moare.android.features.search.display.search.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.Constants
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.core.util.Trie
import com.moare.android.core.util.getChosung
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.KeywordInfo
import com.moare.android.features.search.models.displaymodels.football.FBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBALeagueScheduleDisplayModel
import com.moare.android.features.search.models.NoticeModel
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.search.models.TrendingKeywords
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.models.models.football.FBGameFixture
import com.moare.android.features.search.models.models.football.FBGameForSchedule
import com.moare.android.features.search.models.models.kbo.KBOGame
import com.moare.android.features.search.models.models.kbo.KBOGameForSchedule
import com.moare.android.features.search.models.models.mlb.MLBGame
import com.moare.android.features.search.models.models.mlb.MLBGameForSchedule
import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.models.nba.NBAGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.FBGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBPlayerInfoResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamInfoResponseModel
import com.moare.android.features.search.models.responsemodels.kbo.KBOGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.mlb.MLBGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBAGameScheduleResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBAGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBAPlayerInfoResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBATeamInfoResponseModel
import com.moare.android.features.search.networking.KeywordsClient
import com.moare.android.features.search.networking.SearchClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val searchClient: SearchClient,
    private val keywordsClient: KeywordsClient,
    private val trieDeferred: CompletableDeferred<Pair<Trie, List<KeywordInfo>>>,
    private val noticeDeferred: CompletableDeferred<List<NoticeModel>>,
    private val trendingKeywordsDeferred: CompletableDeferred<TrendingKeywords>,
) : MVIViewModel<SearchViewModel.Intent, Nothing>() {
    /* ---------------------
       data state
       --------------------- */
    private val _searchDataState = MutableStateFlow<ApiFetchState>(ApiFetchState.Idle)
    val searchDataState: StateFlow<ApiFetchState> = _searchDataState

    val initialMap = SportDisplayType.values().associateWith { null }
    private val _displayModels = MutableStateFlow<Map<SportDisplayType, SportDisplayModel?>>(initialMap)
    val displayModels: StateFlow<Map<SportDisplayType, SportDisplayModel?>> = _displayModels

    private var initialFBLeagueScheduleData: FBLeagueScheduleDisplayModel? = null // NOTE: Used when go back from FBGameStatsView and reopen FBLeagueScheduleView. Has to think about structure.
    private var initialNBALeagueScheduleData: NBALeagueScheduleDisplayModel? = null
    private var initialKBOLeagueScheduleData: KBOLeagueScheduleDisplayModel? = null
    private var initialMLBLeagueScheduleData: MLBLeagueScheduleDisplayModel? = null

    // auto complete
    private val _autoCompleteList = MutableStateFlow<List<String>>(emptyList())
    val autoCompleteList: StateFlow<List<String>> = _autoCompleteList

    private val _trendingKeywordList = MutableStateFlow<List<String>>(emptyList())
    val trendingKeywordList: StateFlow<List<String>> = _trendingKeywordList

    private val _noticeData = MutableStateFlow<List<NoticeModel>>(emptyList())
    val noticeData: StateFlow<List<NoticeModel>> = _noticeData

    /* ---------------------
       ui state
       --------------------- */
    private val _barFirstOpened = MutableStateFlow(false)
    val barFirstOpened: StateFlow<Boolean> = _barFirstOpened

//    private val _focusRequester = MutableStateFlow(FocusRequester())
//    val focusRequester: StateFlow<FocusRequester> = _focusRequester

    private val _focusState = MutableStateFlow(false)
    val focusState: StateFlow<Boolean> = _focusState

    private val _query = MutableStateFlow(TextFieldValue(""))
    val query: StateFlow<TextFieldValue> = _query

    private val _searchState = MutableStateFlow(false)
    val searchState: StateFlow<Boolean> = _searchState

    private val _resultVisibleState = MutableStateFlow(false)
    val resultVisibleState: StateFlow<Boolean> = _resultVisibleState

    private val _autoCompleteListVisibleState = MutableStateFlow(false)
    val autoCompleteListVisibleState: StateFlow<Boolean> = _autoCompleteListVisibleState

    /* ---------------------
       etc
       --------------------- */
    private val trie: Trie by lazy {
        runBlocking { trieDeferred.await().first }
    }

    private val autoCompleteDataMap: Map<String, KeywordInfo> by lazy {
        runBlocking {
            trieDeferred.await().second.associateBy { it.keyword }
        }
    }

    private var trendingKeywords: Map<String, KeywordInfo> = emptyMap()

    // NOTE: viewStack should always be up to date
    private val _viewStack = MutableStateFlow<List<SportDecodableModel>>(emptyList())
    val viewStack: StateFlow<List<SportDecodableModel>> = _viewStack

    private val _poppedView = MutableStateFlow<SportDecodableModel?>(null)
    val poppedView: StateFlow<SportDecodableModel?> = _poppedView

    /* ---------------------
       init
       --------------------- */
    init {
        viewModelScope.launch {
            // test
//            val inputStream = context.assets.open("football_player_standings.json")
//            val jsonContent = inputStream.bufferedReader().use { it.readText() }
//
//            val data = DataModel.fromJson(jsonContent).data as SportDecodableModel.FBPlayerStandings
//            _fbPlayerStandingsData.emit(data.displayModel)
//            delay(5000)
            trendingKeywords = trendingKeywordsDeferred.await().keywords.associateBy { it.keyword }
            _trendingKeywordList.emit(trendingKeywords.keys.toList())

            _noticeData.emit(noticeDeferred.await())
        }
    }

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data object BarFirstOpen : Intent()
        data class PerformSearch(val searchType: SearchType = SearchType.QUERY, val aniDuration: Long = 0) : Intent()
        data class ToggleFocusState(val isFocused: Boolean) : Intent()
        data class UpdateTextField(val newValue: TextFieldValue, val updateAutoCompleteList: Boolean = true) : Intent()

        data object ToggleSearchBar : Intent()
        data object ToggleAutoCompleteListVisibleState : Intent()

        data class SelectFBGame(val game: FBGameForSchedule, val leagueId: Int) : Intent()
        data class SelectNBAGame(val game: NBAGameForSchedule) : Intent()
        data class SelectKBOGame(val game: KBOGameForSchedule) : Intent()
        data class SelectMLBGame(val game: MLBGameForSchedule) : Intent()

        data class GoBack(val activity: Activity?) : Intent()

        data class ShowPlayerStats(val category: String? = null, val playerId: Int) : Intent()
        data class ShowTeamStats(val teamId: Int) : Intent()
        data class ShowGameStats(val gameType: String) : Intent()
        data class RefreshGame(val category: String) : Intent()
        data class SelectNBATournamentRound(val gameList: List<NBAGame>) : Intent()

        data class UpdateLastViewStack(val data: SportDecodableModel) : Intent()
    }

    enum class SearchType {
        QUERY, TRENDING_KEYWORD, AUTO_COMPLETE
    }

    override fun send(intent: Intent) {
        // TODO: 비동기를 여기서 실행할지, 각 implements에서 실행할지 고민필요
        viewModelScope.launch {
            when (intent) {
                is Intent.BarFirstOpen -> barFirstOpen()
                is Intent.PerformSearch -> {
                    if (query.value.text.isBlank()) {
                        val firstTrendingKeyword = trendingKeywordList.value.firstOrNull()
                        if (!firstTrendingKeyword.isNullOrBlank()) {
                            updateTextField(TextFieldValue(firstTrendingKeyword), false)
                            performSearch(SearchType.TRENDING_KEYWORD, intent.aniDuration)
                        }
                    } else {
                        performSearch(intent.searchType, intent.aniDuration)
                    }
                }
                is Intent.ToggleFocusState -> toggleFocusState(intent.isFocused)
                is Intent.ToggleAutoCompleteListVisibleState -> toggleAutoCompleteListVisibleState()
                is Intent.UpdateTextField -> updateTextField(intent.newValue, intent.updateAutoCompleteList)
                is Intent.ToggleSearchBar -> toggleSearchBar()
                is Intent.SelectFBGame -> selectFBGame(intent.game, intent.leagueId)
                is Intent.SelectNBAGame -> selectNBAGame(intent.game)
                is Intent.SelectKBOGame -> selectKBOGame(intent.game)
                is Intent.SelectMLBGame -> selectMLBGame(intent.game)
                is Intent.GoBack -> goBack(intent.activity)
                is Intent.ShowPlayerStats -> showPlayerStats(intent.category, intent.playerId)
                is Intent.ShowTeamStats -> showTeamStats(intent.teamId)
                is Intent.ShowGameStats -> showGameStats(intent.gameType)
                is Intent.RefreshGame -> refreshGame(intent.category)
                is Intent.SelectNBATournamentRound -> selectNBATournamentRound(intent.gameList)
                is Intent.UpdateLastViewStack -> updateLastViewStack(intent.data)
            }
        }
    }

    /* ---------------------
       implements
       --------------------- */
    private suspend fun barFirstOpen() {
        _barFirstOpened.emit(true)
    }

    private suspend fun performSearch(searchType: SearchType, aniDuration: Long) {
        // animation/search start time
        val startTime = System.currentTimeMillis()

        try {
            _searchState.emit(true)
            toggleFocusState(false)

            val dataFetchDeferred = viewModelScope.async {
//                delay(5000) // test for fetching delay
                when (searchType) {
                    SearchType.QUERY -> searchClient.fetchDataByQuery(query.value.text)
                    SearchType.TRENDING_KEYWORD -> {
                        val keyword = trendingKeywords[query.value.text]
                        keyword?.let {
                            searchClient.fetchDataByKeyword(keyword)
                        }
                    }
                    SearchType.AUTO_COMPLETE -> {
                        val keywordInfo = autoCompleteDataMap[query.value.text]
                        keywordInfo?.let {
                            keywordInfo.weight = null // To exclude field "weight" in the request body
                            searchClient.fetchDataByKeyword(keywordInfo)
                        }
                    }
                }
            }

            // delay for animation duration in case the data fetched before animation ends
            delay(aniDuration)

            // reset autocomplete list
            _autoCompleteList.emit(emptyList())
            _autoCompleteListVisibleState.emit(false)

            // if data is still fetching after the animation duration, show loading
            if (!dataFetchDeferred.isCompleted) {
                _searchDataState.emit(ApiFetchState.Fetching)
            }

            val data = dataFetchDeferred.await()

            // hide loading first before showing data
            _searchDataState.emit(ApiFetchState.Success)

            // TODO: updateMainDisplayModel로 정리해야함
            _displayModels.value = initialMap

            val newDisplayModels = displayModels.value.toMutableMap()
            when (val data = data?.data) {
                // football
                is SportDecodableModel.FBPlayerInfo -> { newDisplayModels[SportDisplayType.FB_PLAYER_INFO] = data.displayModel }
                is SportDecodableModel.FBPlayerStats -> { newDisplayModels[SportDisplayType.FB_PLAYER_STATS] = data.displayModel }
                is SportDecodableModel.FBPlayerStandings -> { newDisplayModels[SportDisplayType.FB_PLAYER_STANDINGS] = data.displayModel }
                is SportDecodableModel.FBTeamInfo -> { newDisplayModels[SportDisplayType.FB_TEAM_INFO] = data.displayModel }
                is SportDecodableModel.FBTeamStats -> { newDisplayModels[SportDisplayType.FB_TEAM_STATS] = data.displayModel }
                is SportDecodableModel.FBTeamStandings -> { newDisplayModels[SportDisplayType.FB_TEAM_STANDINGS] = data.displayModel }
                is SportDecodableModel.FBTeamSchedule -> { newDisplayModels[SportDisplayType.FB_TEAM_SCHEDULE] = data.displayModel }
                is SportDecodableModel.FBLeagueSchedule -> {
                    newDisplayModels[SportDisplayType.FB_LEAGUE_SCHEDULE] = data.displayModel
                    initialFBLeagueScheduleData = data.displayModel
                }
                is SportDecodableModel.FBGameStats -> { newDisplayModels[SportDisplayType.FB_GAME_STATS] = data.displayModel }
                // nba
                is SportDecodableModel.NBAPlayerInfo -> { newDisplayModels[SportDisplayType.NBA_PLAYER_INFO] = data.displayModel }
                is SportDecodableModel.NBAPlayerStats -> { newDisplayModels[SportDisplayType.NBA_PLAYER_STATS] = data.displayModel }
                is SportDecodableModel.NBAPlayerStandings -> { newDisplayModels[SportDisplayType.NBA_PLAYER_STANDINGS] = data.displayModel }
                is SportDecodableModel.NBATeamInfo -> { newDisplayModels[SportDisplayType.NBA_TEAM_INFO] = data.displayModel }
                is SportDecodableModel.NBATeamStats -> { newDisplayModels[SportDisplayType.NBA_TEAM_STATS] = data.displayModel }
                is SportDecodableModel.NBATeamStandings -> { newDisplayModels[SportDisplayType.NBA_TEAM_STANDINGS] = data.displayModel }
                is SportDecodableModel.NBATeamSchedule -> { newDisplayModels[SportDisplayType.NBA_TEAM_SCHEDULE] = data.displayModel }
                is SportDecodableModel.NBALeagueSchedule -> {
                    newDisplayModels[SportDisplayType.NBA_LEAGUE_SCHEDULE] = data.displayModel
                    initialNBALeagueScheduleData = data.displayModel
                }
                is SportDecodableModel.NBAGameStats -> { newDisplayModels[SportDisplayType.NBA_GAME_STATS] = data.displayModel }
                is SportDecodableModel.NBALeagueTournament -> { newDisplayModels[SportDisplayType.NBA_LEAGUE_TOURNAMENT] = data.displayModel }
                // kbo
                is SportDecodableModel.KBOPlayerInfo -> { newDisplayModels[SportDisplayType.KBO_PLAYER_INFO] = data.displayModel }
                is SportDecodableModel.KBOPlayerStats -> { newDisplayModels[SportDisplayType.KBO_PLAYER_STATS] = data.displayModel }
                is SportDecodableModel.KBOPlayerStandings -> { newDisplayModels[SportDisplayType.KBO_PLAYER_STANDINGS] = data.displayModel }
                is SportDecodableModel.KBOTeamInfo -> { newDisplayModels[SportDisplayType.KBO_TEAM_INFO] = data.displayModel }
                is SportDecodableModel.KBOTeamStats -> { newDisplayModels[SportDisplayType.KBO_TEAM_STATS] = data.displayModel }
                is SportDecodableModel.KBOTeamStandings -> { newDisplayModels[SportDisplayType.KBO_TEAM_STANDINGS] = data.displayModel }
                is SportDecodableModel.KBOTeamSchedule -> { newDisplayModels[SportDisplayType.KBO_TEAM_SCHEDULE] = data.displayModel }
                is SportDecodableModel.KBOLeagueSchedule -> {
                    newDisplayModels[SportDisplayType.KBO_LEAGUE_SCHEDULE] = data.displayModel
                    initialKBOLeagueScheduleData = data.displayModel
                }
                is SportDecodableModel.KBOGameStats -> { newDisplayModels[SportDisplayType.KBO_GAME_STATS] = data.displayModel }
                // mlb
                is SportDecodableModel.MLBPlayerInfo -> { newDisplayModels[SportDisplayType.MLB_PLAYER_INFO] = data.displayModel }
                is SportDecodableModel.MLBPlayerStats -> { newDisplayModels[SportDisplayType.MLB_PLAYER_STATS] = data.displayModel }
                is SportDecodableModel.MLBPlayerStandings -> { newDisplayModels[SportDisplayType.MLB_PLAYER_STANDINGS] = data.displayModel }
                is SportDecodableModel.MLBTeamInfo -> { newDisplayModels[SportDisplayType.MLB_TEAM_INFO] = data.displayModel }
                is SportDecodableModel.MLBTeamStats -> { newDisplayModels[SportDisplayType.MLB_TEAM_STATS] = data.displayModel }
                is SportDecodableModel.MLBTeamStandings -> { newDisplayModels[SportDisplayType.MLB_TEAM_STANDINGS] = data.displayModel }
                is SportDecodableModel.MLBTeamSchedule -> { newDisplayModels[SportDisplayType.MLB_TEAM_SCHEDULE] = data.displayModel }
                is SportDecodableModel.MLBLeagueSchedule -> {
                    newDisplayModels[SportDisplayType.MLB_LEAGUE_SCHEDULE] = data.displayModel
                    initialMLBLeagueScheduleData = data.displayModel
                }
                is SportDecodableModel.MLBGameStats -> { newDisplayModels[SportDisplayType.MLB_GAME_STATS] = data.displayModel }

                else -> {
                    throw IllegalArgumentException("Unknown data type")
                }
            }
            _displayModels.value = newDisplayModels

            // add viewStack
            val stack = viewStack.value.toMutableList()
            stack.add(data.data)
            _viewStack.emit(stack)
            _poppedView.emit(null)

            _resultVisibleState.emit(true)
        } catch (e: Exception) {
            _searchDataState.emit(ApiFetchState.Error("검색 결과가 없습니다."))
            Log.e("dsdf", e.localizedMessage ?: "data type error")
        }
    }

    private suspend fun toggleFocusState(isFocused: Boolean) {
        if (isFocused) {
            // move textfield's cursor to the end of the query
            _query.emit(query.value.copy(
                selection = TextRange(query.value.text.length)
            ))
            _focusState.emit(true)
        } else {
            _focusState.emit(false)
        }
    }

    private suspend fun toggleAutoCompleteListVisibleState() {
        _autoCompleteListVisibleState.emit(!autoCompleteListVisibleState.value)
    }

    private fun updateTextField(newValue: TextFieldValue, updateAutoCompleteList: Boolean = true) {
        _query.value = newValue

        // auto complete
        if (updateAutoCompleteList) {
            if (newValue.text.isBlank()) {
                _autoCompleteList.value = emptyList()
                _autoCompleteListVisibleState.value = false
            } else {
                val result = trie.search(newValue.text)

                _autoCompleteList.value = result
                _autoCompleteListVisibleState.value = true
            }
        }
    }

    private suspend fun toggleSearchBar() {
        val currentSearchState = searchState.value

        if (currentSearchState) {
            _searchState.emit(false)
            _resultVisibleState.emit(false)
            _searchDataState.emit(ApiFetchState.Idle)
            updateTextField(query.value)
            delay(1000)
            toggleFocusState(true)
        } else {
            // TODO: toggleSearchBar를 openSearchBar로 바꾸고, 여기 액션은 goBack() 에서만 쓰이기 때문에 goBack()으로 옮기는게 나을듯?
            _searchState.emit(true)

            // reset autocomplete list
            _autoCompleteList.emit(emptyList())
            _autoCompleteListVisibleState.emit(false)

            _searchDataState.emit(ApiFetchState.Success)

            _resultVisibleState.emit(true)
        }
    }

    private suspend fun selectFBGame(game: FBGameForSchedule, leagueId: Int) {
        val result = searchClient.fetchById(
            category = "football",
            date = game.date,
            dataType = "football_game_stats",
            leagueId = leagueId,
            id = game.gameId
        )

        // add stack before emiting _fbGameStatsData to ensure the last stack(SportDecodableModel.FBGameStats in this case) can be up to date after refreshing game data when opening FBGameStatsView
        addViewStack(result.data)
        updateMainDisplayModel(result.data, false)
    }

    private suspend fun selectNBAGame(game: NBAGameForSchedule) {
        val result = searchClient.fetchById(
            category = "basketball",
            date = game.date,
            dataType = "basketball_game_stats",
            leagueId = Constants.Ids.NBA,
            id = game.gameId
        )

        addViewStack(result.data)
        updateMainDisplayModel(result.data)
    }

    private suspend fun selectKBOGame(game: KBOGameForSchedule) {
        val result = searchClient.fetchById(
            category = "baseball",
            date = game.date,
            dataType = "baseball_game_stats",
            leagueId = Constants.Ids.KBO,
            id = game.gameId
        )

        addViewStack(result.data)
        updateMainDisplayModel(result.data)
    }

    private suspend fun selectMLBGame(game: MLBGameForSchedule) {
        val result = searchClient.fetchById(
            category = "baseball",
            date = game.date,
            dataType = "baseball_game_stats",
            leagueId = Constants.Ids.MLB,
            id = game.gameId
        )

        addViewStack(result.data)
        updateMainDisplayModel(result.data)
    }

    private suspend fun goBack(activity: Activity?) {
        val stack = viewStack.value.toMutableList()

        if (!searchState.value) {
            val lastView = stack.lastOrNull()

            if (lastView != null) {
                toggleSearchBar()
            } else {
                // close app
                activity?.finishAffinity()
            }
        } else {
            val lastView = stack.removeLastOrNull()
            _poppedView.emit(lastView)

            val viewToShow = stack.lastOrNull()

            if (lastView == null) {
                // close app
                activity?.finishAffinity()
            } else {
                _resultVisibleState.emit(false)

                if (viewToShow == null) {
                    // doesn't have to initialize data states(putting all null) because its action is done in performSearch()
                    toggleSearchBar()
                } else {
                    delay(1000)

                    // TODO: updateMainDisplayModel로 정리해야함
                    _displayModels.value = initialMap

                    val newDisplayModels = displayModels.value.toMutableMap()
                    when (viewToShow) {
                        // football
                        is SportDecodableModel.FBPlayerInfo -> { newDisplayModels[SportDisplayType.FB_PLAYER_INFO] = viewToShow.displayModel }
                        is SportDecodableModel.FBPlayerStats -> { newDisplayModels[SportDisplayType.FB_PLAYER_STATS] = viewToShow.displayModel }
                        is SportDecodableModel.FBPlayerStandings -> { newDisplayModels[SportDisplayType.FB_PLAYER_STANDINGS] = viewToShow.displayModel }
                        is SportDecodableModel.FBTeamInfo -> { newDisplayModels[SportDisplayType.FB_TEAM_INFO] = viewToShow.displayModel }
                        is SportDecodableModel.FBTeamStats -> { newDisplayModels[SportDisplayType.FB_TEAM_STATS] = viewToShow.displayModel }
                        is SportDecodableModel.FBTeamStandings -> { newDisplayModels[SportDisplayType.FB_TEAM_STANDINGS] = viewToShow.displayModel }
                        is SportDecodableModel.FBTeamSchedule -> { newDisplayModels[SportDisplayType.FB_TEAM_SCHEDULE] = viewToShow.displayModel }
                        is SportDecodableModel.FBLeagueSchedule -> {
                            if (lastView is SportDecodableModel.FBGameStats) {
                                newDisplayModels[SportDisplayType.FB_LEAGUE_SCHEDULE] = initialFBLeagueScheduleData
                            } else {
                                newDisplayModels[SportDisplayType.FB_LEAGUE_SCHEDULE] = viewToShow.displayModel
                            }
                        }
                        is SportDecodableModel.FBGameStats -> { newDisplayModels[SportDisplayType.FB_GAME_STATS] = viewToShow.displayModel }
                        // nba
                        is SportDecodableModel.NBAPlayerInfo -> { newDisplayModels[SportDisplayType.NBA_PLAYER_INFO] = viewToShow.displayModel }
                        is SportDecodableModel.NBAPlayerStats -> { newDisplayModels[SportDisplayType.NBA_PLAYER_STATS] = viewToShow.displayModel }
                        is SportDecodableModel.NBAPlayerStandings -> { newDisplayModels[SportDisplayType.NBA_PLAYER_STANDINGS] = viewToShow.displayModel }
                        is SportDecodableModel.NBATeamInfo -> { newDisplayModels[SportDisplayType.NBA_TEAM_INFO] = viewToShow.displayModel }
                        is SportDecodableModel.NBATeamStats -> { newDisplayModels[SportDisplayType.NBA_TEAM_STATS] = viewToShow.displayModel }
                        is SportDecodableModel.NBATeamStandings -> { newDisplayModels[SportDisplayType.NBA_TEAM_STANDINGS] = viewToShow.displayModel }
                        is SportDecodableModel.NBATeamSchedule -> { newDisplayModels[SportDisplayType.NBA_TEAM_SCHEDULE] = viewToShow.displayModel }
                        is SportDecodableModel.NBALeagueSchedule -> {
                            if (lastView is SportDecodableModel.NBAGameStats) {
                                newDisplayModels[SportDisplayType.NBA_LEAGUE_SCHEDULE] = initialNBALeagueScheduleData
                            } else {
                                newDisplayModels[SportDisplayType.NBA_LEAGUE_SCHEDULE] = viewToShow.displayModel
                            }
                        }
                        is SportDecodableModel.NBAGameStats -> { newDisplayModels[SportDisplayType.NBA_GAME_STATS] = viewToShow.displayModel }
                        is SportDecodableModel.NBALeagueTournament -> { newDisplayModels[SportDisplayType.NBA_LEAGUE_TOURNAMENT] = viewToShow.displayModel }
                        // kbo
                        is SportDecodableModel.KBOPlayerInfo -> { newDisplayModels[SportDisplayType.KBO_PLAYER_INFO] = viewToShow.displayModel }
                        is SportDecodableModel.KBOPlayerStats -> { newDisplayModels[SportDisplayType.KBO_PLAYER_STATS] = viewToShow.displayModel }
                        is SportDecodableModel.KBOPlayerStandings -> { newDisplayModels[SportDisplayType.KBO_PLAYER_STANDINGS] = viewToShow.displayModel }
                        is SportDecodableModel.KBOTeamInfo -> { newDisplayModels[SportDisplayType.KBO_TEAM_INFO] = viewToShow.displayModel }
                        is SportDecodableModel.KBOTeamStats -> { newDisplayModels[SportDisplayType.KBO_TEAM_STATS] = viewToShow.displayModel }
                        is SportDecodableModel.KBOTeamStandings -> { newDisplayModels[SportDisplayType.KBO_TEAM_STANDINGS] = viewToShow.displayModel }
                        is SportDecodableModel.KBOTeamSchedule -> { newDisplayModels[SportDisplayType.KBO_TEAM_SCHEDULE] = viewToShow.displayModel }
                        is SportDecodableModel.KBOLeagueSchedule -> {
                            if (lastView is SportDecodableModel.KBOGameStats) {
                                newDisplayModels[SportDisplayType.KBO_LEAGUE_SCHEDULE] = initialKBOLeagueScheduleData
                            } else {
                                newDisplayModels[SportDisplayType.KBO_LEAGUE_SCHEDULE] = viewToShow.displayModel
                            }
                        }
                        is SportDecodableModel.KBOGameStats -> { newDisplayModels[SportDisplayType.KBO_GAME_STATS] = viewToShow.displayModel }
                        // mlb
                        is SportDecodableModel.MLBPlayerInfo -> { newDisplayModels[SportDisplayType.MLB_PLAYER_INFO] = viewToShow.displayModel }
                        is SportDecodableModel.MLBPlayerStats -> { newDisplayModels[SportDisplayType.MLB_PLAYER_STATS] = viewToShow.displayModel }
                        is SportDecodableModel.MLBPlayerStandings -> { newDisplayModels[SportDisplayType.MLB_PLAYER_STANDINGS] = viewToShow.displayModel }
                        is SportDecodableModel.MLBTeamInfo -> { newDisplayModels[SportDisplayType.MLB_TEAM_INFO] = viewToShow.displayModel }
                        is SportDecodableModel.MLBTeamStats -> { newDisplayModels[SportDisplayType.MLB_TEAM_STATS] = viewToShow.displayModel }
                        is SportDecodableModel.MLBTeamStandings -> { newDisplayModels[SportDisplayType.MLB_TEAM_STANDINGS] = viewToShow.displayModel }
                        is SportDecodableModel.MLBTeamSchedule -> { newDisplayModels[SportDisplayType.MLB_TEAM_SCHEDULE] = viewToShow.displayModel }
                        is SportDecodableModel.MLBLeagueSchedule -> {
                            if (lastView is SportDecodableModel.MLBGameStats) {
                                newDisplayModels[SportDisplayType.MLB_LEAGUE_SCHEDULE] = initialMLBLeagueScheduleData
                            } else {
                                newDisplayModels[SportDisplayType.MLB_LEAGUE_SCHEDULE] = viewToShow.displayModel
                            }
                        }
                        is SportDecodableModel.MLBGameStats -> { newDisplayModels[SportDisplayType.MLB_GAME_STATS] = viewToShow.displayModel }

                        else -> {}
                    }
                    _displayModels.value = newDisplayModels

                    _resultVisibleState.emit(true)
                }

                _viewStack.emit(stack)
            }
        }
    }

    private suspend fun showPlayerStats(category: String?, playerId: Int) {
        val modelConverter = ModelConverter()

        val dataModel: SportDecodableModel

        when (val lastView = viewStack.value.lastOrNull()) {
            is SportDecodableModel.FBPlayerStandings -> {
                if (category == null) {
                    val player = lastView.responseModel.standings.find { player ->
                        player.player.id == playerId
                    }

                    val responseModel = FBPlayerInfoResponseModel(info = player)
                    dataModel = SportDecodableModel.FBPlayerStats(
                        responseModel = responseModel,
                        displayModel = modelConverter.fbPlayerStatsConverter(responseModel)
                    )
                } else {
                    val leagueId = lastView.responseModel.standings.firstOrNull()?.statistics?.firstOrNull()?.league?.id ?: 39

                    // TODO: Has to add loading
                    val result = searchClient.fetchById(
                        category = category,
                        dataType = "${category}_player_stats",
                        leagueId = leagueId,
                        id = playerId.toString()
                    )

                    if (result.data is SportDecodableModel.FBPlayerStats) {
                        dataModel = result.data
                    } else {
                        return
                    }
                }
            }
            is SportDecodableModel.FBPlayerInfo -> {
                dataModel = SportDecodableModel.FBPlayerStats(
                    responseModel = lastView.responseModel,
                    displayModel = modelConverter.fbPlayerStatsConverter(lastView.responseModel)
                )
            }

            is SportDecodableModel.NBAPlayerStandings -> {
                // NOTE: nba player stats data in standings has all the stats for now, so doesn't has to fetchById like football above.
                val player = lastView.responseModel.standings.find { player ->
                    player.player.personId == playerId
                }

                val responseModel = NBAPlayerInfoResponseModel(info = player)
                dataModel = SportDecodableModel.NBAPlayerStats(
                    responseModel = responseModel,
                    displayModel = modelConverter.nbaPlayerStatsConverter(responseModel)
                )
            }
            is SportDecodableModel.NBAPlayerInfo -> {
                dataModel = SportDecodableModel.NBAPlayerStats(
                    responseModel = lastView.responseModel,
                    displayModel = modelConverter.nbaPlayerStatsConverter(lastView.responseModel)
                )
            }

            is SportDecodableModel.KBOPlayerInfo -> {
                dataModel = SportDecodableModel.KBOPlayerStats(
                    responseModel = lastView.responseModel,
                    displayModel = modelConverter.kboPlayerStatsConverter(lastView.responseModel)
                )
            }
            is SportDecodableModel.MLBPlayerInfo -> {
                dataModel = SportDecodableModel.MLBPlayerStats(
                    responseModel = lastView.responseModel,
                    displayModel = modelConverter.mlbPlayerStatsConverter(lastView.responseModel)
                )
            }

             else -> return // Make it do nothing
        }

        _resultVisibleState.emit(false)
        delay(1000)

        updateMainDisplayModel(dataModel)

        val stack = viewStack.value.toMutableList()
        stack.add(dataModel)
        _viewStack.emit(stack)
        _poppedView.emit(null)

        _resultVisibleState.emit(true)
    }

    private suspend fun showTeamStats(teamId: Int) {
        val modelConverter = ModelConverter()

        val dataModel: SportDecodableModel

        when (val lastView = viewStack.value.lastOrNull()) {
            is SportDecodableModel.FBTeamStandings -> {
                val team = lastView.responseModel.standings.find { team ->
                    team.team.id == teamId
                }

                val responseModel = FBTeamInfoResponseModel(info = team)
                dataModel = SportDecodableModel.FBTeamStats(
                    responseModel = responseModel,
                    displayModel = modelConverter.fbTeamStatsConverter(responseModel)
                )
            }
            is SportDecodableModel.FBTeamInfo -> {
                dataModel = SportDecodableModel.FBTeamStats(
                    responseModel = lastView.responseModel,
                    displayModel = modelConverter.fbTeamStatsConverter(lastView.responseModel)
                )
            }

            is SportDecodableModel.NBATeamStandings -> {
                val team = lastView.responseModel.standings.find { team ->
                    team.team.id == teamId
                }

                val responseModel = NBATeamInfoResponseModel(info = team)
                dataModel = SportDecodableModel.NBATeamStats(
                    responseModel = responseModel,
                    displayModel = modelConverter.nbaTeamStatsConverter(responseModel)
                )
            }
            is SportDecodableModel.NBATeamInfo -> {
                dataModel = SportDecodableModel.NBATeamStats(
                    responseModel = lastView.responseModel,
                    displayModel = modelConverter.nbaTeamStatsConverter(lastView.responseModel)
                )
            }

            is SportDecodableModel.KBOTeamInfo -> {
                dataModel = SportDecodableModel.KBOTeamStats(
                    responseModel = lastView.responseModel,
                    displayModel = modelConverter.kboTeamStatsConverter(lastView.responseModel)
                )
            }
            is SportDecodableModel.MLBTeamInfo -> {
                dataModel = SportDecodableModel.MLBTeamStats(
                    responseModel = lastView.responseModel,
                    displayModel = modelConverter.mlbTeamStatsConverter(lastView.responseModel)
                )
            }

            else -> return // Make it do nothing
        }

        _resultVisibleState.emit(false)
        delay(1000)

        updateMainDisplayModel(dataModel)

        val stack = viewStack.value.toMutableList()
        stack.add(dataModel)
        _viewStack.emit(stack)
        _poppedView.emit(null)

        _resultVisibleState.emit(true)
    }

    private suspend fun showGameStats(gameType: String) {
        val modelConverter = ModelConverter()

        val dataModel: SportDecodableModel

        when (val lastView = viewStack.value.lastOrNull()) {
            is SportDecodableModel.FBPlayerInfo,
            is SportDecodableModel.FBTeamInfo-> {
                val lastGame: FBGame?
                val nextGame: FBGame?
                if (lastView is SportDecodableModel.FBPlayerInfo) {
                    lastGame = lastView.responseModel.lastGame
                    nextGame = lastView.responseModel.nextGame
                } else {
                    lastGame = (lastView as SportDecodableModel.FBTeamInfo).responseModel.lastGame
                    nextGame = lastView.responseModel.nextGame
                }

                val responseModel = if (gameType == "previous") FBGameStatsResponseModel(lastGame) else FBGameStatsResponseModel(nextGame)
                dataModel = SportDecodableModel.FBGameStats(
                    responseModel = responseModel,
                    displayModel = modelConverter.fbGameStatsConverter(responseModel)
                )
            }

            is SportDecodableModel.NBAPlayerInfo,
            is SportDecodableModel.NBATeamInfo-> {
                val lastGame: NBAGame?
                val nextGame: NBAGame?
                if (lastView is SportDecodableModel.NBAPlayerInfo) {
                    lastGame = lastView.responseModel.lastGame
                    nextGame = lastView.responseModel.nextGame
                } else {
                    lastGame = (lastView as SportDecodableModel.NBATeamInfo).responseModel.lastGame
                    nextGame = lastView.responseModel.nextGame
                }

                val responseModel = if (gameType == "previous") NBAGameStatsResponseModel(lastGame) else NBAGameStatsResponseModel(nextGame)
                dataModel = SportDecodableModel.NBAGameStats(
                    responseModel = responseModel,
                    displayModel = modelConverter.nbaGameStatsConverter(responseModel)
                )
            }

            is SportDecodableModel.KBOPlayerInfo,
            is SportDecodableModel.KBOTeamInfo-> {
                val lastGame: KBOGame?
                val nextGame: KBOGame?
                if (lastView is SportDecodableModel.KBOPlayerInfo) {
                    lastGame = lastView.responseModel.lastGame
                    nextGame = lastView.responseModel.nextGame
                } else {
                    lastGame = (lastView as SportDecodableModel.KBOTeamInfo).responseModel.lastGame
                    nextGame = lastView.responseModel.nextGame
                }

                val responseModel = if (gameType == "previous") KBOGameStatsResponseModel(lastGame) else KBOGameStatsResponseModel(nextGame)
                dataModel = SportDecodableModel.KBOGameStats(
                    responseModel = responseModel,
                    displayModel = modelConverter.kboGameStatsConverter(responseModel)
                )
            }

            is SportDecodableModel.MLBPlayerInfo,
            is SportDecodableModel.MLBTeamInfo-> {
                val lastGame: MLBGame?
                val nextGame: MLBGame?
                if (lastView is SportDecodableModel.MLBPlayerInfo) {
                    lastGame = lastView.responseModel.lastGame
                    nextGame = lastView.responseModel.nextGame
                } else {
                    lastGame = (lastView as SportDecodableModel.MLBTeamInfo).responseModel.lastGame
                    nextGame = lastView.responseModel.nextGame
                }

                val responseModel = if (gameType == "previous") MLBGameStatsResponseModel(lastGame) else MLBGameStatsResponseModel(nextGame)
                dataModel = SportDecodableModel.MLBGameStats(
                    responseModel = responseModel,
                    displayModel = modelConverter.mlbGameStatsConverter(responseModel)
                )
            }

             else -> return // Make it do nothing
        }

        _resultVisibleState.emit(false)
        delay(1000)

        updateMainDisplayModel(dataModel)

        val stack = viewStack.value.toMutableList()
        stack.add(dataModel)
        _viewStack.emit(stack)
        _poppedView.emit(null)

        _resultVisibleState.emit(true)
    }

    private suspend fun refreshGame(category: String) {
        try {
            when (val lastView = viewStack.value.lastOrNull()) {
                is SportDecodableModel.FBGameStats -> {
                    val game = (displayModels.value[SportDisplayType.FB_GAME_STATS] as? FBGameStatsDisplayModel)?.game
                    game?.let {
                        // TODO: Has to add loading
                        val result = searchClient.fetchById(
                            category = category,
                            date = it.fixture.date,
                            dataType = "${category}_game_stats",
                            leagueId = it.league.id,
                            id = it.fixture.id.toString()
                        )

                        if (result.data is SportDecodableModel.FBGameStats) {
                            val data = result.data
//                    _fbGameStatsData.emit(data.displayModel)
                            updateMainDisplayModel(data = data, shouldReset = false)

                            updateLastViewStack(data)
                        }
                    }
                }

                is SportDecodableModel.NBAGameStats -> {
                    val game = (displayModels.value[SportDisplayType.NBA_GAME_STATS] as? NBAGameStatsDisplayModel)?.game
                    val gameSummary = game?.gameSummary
                    val boxScoreTraditional = game?.boxScoreTraditional
                    gameSummary?.let { gameSummary ->
                        boxScoreTraditional?.let { boxScoreTraditional->
                            // TODO: Has to add loading
                            val result = searchClient.fetchById(
                                category = category,
                                date = gameSummary.date,
                                dataType = "${category}_game_stats",
                                leagueId = 90001,
                                id = boxScoreTraditional.gameId
                            )

                            if (result.data is SportDecodableModel.NBAGameStats) {
                                val data = result.data
                                updateMainDisplayModel(data = data, shouldReset = false)

                                updateLastViewStack(data)
                            }
                        }
                    }
                }

                else -> return // do nothing
            }
        } catch (e: Exception) {
            Log.e("dsdf", e.localizedMessage ?: "error")
        }
    }

    private suspend fun selectNBATournamentRound(gameList: List<NBAGame>) {
        val modelConverter = ModelConverter()

        val dataModel: SportDecodableModel

        when (val lastView = viewStack.value.lastOrNull()) {
            is SportDecodableModel.NBALeagueTournament-> {
                val responseModel = NBAGameScheduleResponseModel(scheduledMonths = emptyList(), schedule = modelConverter.nbaGameListToGameScheduleListConverter(gameList))
                dataModel = SportDecodableModel.NBATeamSchedule(
                    responseModel = responseModel,
                    displayModel = modelConverter.nbaTeamScheduleConverter(responseModel)
                )
            }

            else -> return // Make it do nothing
        }

        _resultVisibleState.emit(false)
        delay(1000)

        updateMainDisplayModel(dataModel)

        val stack = viewStack.value.toMutableList()
        stack.add(dataModel)
        _viewStack.emit(stack)
        _poppedView.emit(null)

        _resultVisibleState.emit(true)
    }

    private suspend fun updateLastViewStack(data: SportDecodableModel) {
        val stack = viewStack.value.dropLast(1).toMutableList()
        stack.add(data)
        _viewStack.emit(stack)
    }

    private fun updateMainDisplayModel(data: SportDecodableModel, shouldReset: Boolean = true) {
        if (shouldReset) {
            _displayModels.value = initialMap
        }

        val newDisplayModels = displayModels.value.toMutableMap()
        when (data) {
            // football
            is SportDecodableModel.FBPlayerInfo -> { newDisplayModels[SportDisplayType.FB_PLAYER_INFO] = data.displayModel }
            is SportDecodableModel.FBPlayerStats -> { newDisplayModels[SportDisplayType.FB_PLAYER_STATS] = data.displayModel }
            is SportDecodableModel.FBPlayerStandings -> { newDisplayModels[SportDisplayType.FB_PLAYER_STANDINGS] = data.displayModel }
            is SportDecodableModel.FBTeamInfo -> { newDisplayModels[SportDisplayType.FB_TEAM_INFO] = data.displayModel }
            is SportDecodableModel.FBTeamStats -> { newDisplayModels[SportDisplayType.FB_TEAM_STATS] = data.displayModel }
            is SportDecodableModel.FBTeamStandings -> { newDisplayModels[SportDisplayType.FB_TEAM_STANDINGS] = data.displayModel }
            is SportDecodableModel.FBTeamSchedule -> { newDisplayModels[SportDisplayType.FB_TEAM_SCHEDULE] = data.displayModel }
            is SportDecodableModel.FBLeagueSchedule -> { newDisplayModels[SportDisplayType.FB_LEAGUE_SCHEDULE] = data.displayModel }
            is SportDecodableModel.FBGameStats -> { newDisplayModels[SportDisplayType.FB_GAME_STATS] = data.displayModel }
            // nba
            is SportDecodableModel.NBAPlayerInfo -> { newDisplayModels[SportDisplayType.NBA_PLAYER_INFO] = data.displayModel }
            is SportDecodableModel.NBAPlayerStats -> { newDisplayModels[SportDisplayType.NBA_PLAYER_STATS] = data.displayModel }
            is SportDecodableModel.NBAPlayerStandings -> { newDisplayModels[SportDisplayType.NBA_PLAYER_STANDINGS] = data.displayModel }
            is SportDecodableModel.NBATeamInfo -> { newDisplayModels[SportDisplayType.NBA_TEAM_INFO] = data.displayModel }
            is SportDecodableModel.NBATeamStats -> { newDisplayModels[SportDisplayType.NBA_TEAM_STATS] = data.displayModel }
            is SportDecodableModel.NBATeamStandings -> { newDisplayModels[SportDisplayType.NBA_TEAM_STANDINGS] = data.displayModel }
            is SportDecodableModel.NBATeamSchedule -> { newDisplayModels[SportDisplayType.NBA_TEAM_SCHEDULE] = data.displayModel }
            is SportDecodableModel.NBALeagueSchedule -> { newDisplayModels[SportDisplayType.NBA_LEAGUE_SCHEDULE] = data.displayModel }
            is SportDecodableModel.NBAGameStats -> { newDisplayModels[SportDisplayType.NBA_GAME_STATS] = data.displayModel }
            is SportDecodableModel.NBALeagueTournament -> { newDisplayModels[SportDisplayType.NBA_LEAGUE_TOURNAMENT] = data.displayModel }
            // kbo
            is SportDecodableModel.KBOPlayerInfo -> { newDisplayModels[SportDisplayType.KBO_PLAYER_INFO] = data.displayModel }
            is SportDecodableModel.KBOPlayerStats -> { newDisplayModels[SportDisplayType.KBO_PLAYER_STATS] = data.displayModel }
            is SportDecodableModel.KBOPlayerStandings -> { newDisplayModels[SportDisplayType.KBO_PLAYER_STANDINGS] = data.displayModel }
            is SportDecodableModel.KBOTeamInfo -> { newDisplayModels[SportDisplayType.KBO_TEAM_INFO] = data.displayModel }
            is SportDecodableModel.KBOTeamStats -> { newDisplayModels[SportDisplayType.KBO_TEAM_STATS] = data.displayModel }
            is SportDecodableModel.KBOTeamStandings -> { newDisplayModels[SportDisplayType.KBO_TEAM_STANDINGS] = data.displayModel }
            is SportDecodableModel.KBOTeamSchedule -> { newDisplayModels[SportDisplayType.KBO_TEAM_SCHEDULE] = data.displayModel }
            is SportDecodableModel.KBOLeagueSchedule -> { newDisplayModels[SportDisplayType.KBO_LEAGUE_SCHEDULE] = data.displayModel }
            is SportDecodableModel.KBOGameStats -> { newDisplayModels[SportDisplayType.KBO_GAME_STATS] = data.displayModel }
            // mlb
            is SportDecodableModel.MLBPlayerInfo -> { newDisplayModels[SportDisplayType.MLB_PLAYER_INFO] = data.displayModel }
            is SportDecodableModel.MLBPlayerStats -> { newDisplayModels[SportDisplayType.MLB_PLAYER_STATS] = data.displayModel }
            is SportDecodableModel.MLBPlayerStandings -> { newDisplayModels[SportDisplayType.MLB_PLAYER_STANDINGS] = data.displayModel }
            is SportDecodableModel.MLBTeamInfo -> { newDisplayModels[SportDisplayType.MLB_TEAM_INFO] = data.displayModel }
            is SportDecodableModel.MLBTeamStats -> { newDisplayModels[SportDisplayType.MLB_TEAM_STATS] = data.displayModel }
            is SportDecodableModel.MLBTeamStandings -> { newDisplayModels[SportDisplayType.MLB_TEAM_STANDINGS] = data.displayModel }
            is SportDecodableModel.MLBTeamSchedule -> { newDisplayModels[SportDisplayType.MLB_TEAM_SCHEDULE] = data.displayModel }
            is SportDecodableModel.MLBLeagueSchedule -> { newDisplayModels[SportDisplayType.MLB_LEAGUE_SCHEDULE] = data.displayModel }
            is SportDecodableModel.MLBGameStats -> { newDisplayModels[SportDisplayType.MLB_GAME_STATS] = data.displayModel }

            else -> {}
        }
        _displayModels.value = newDisplayModels
    }

    private fun addViewStack(data: SportDecodableModel) {
        val stack = viewStack.value.toMutableList()
        stack.add(data)
        _viewStack.value = stack
        _poppedView.value = null
    }
}















