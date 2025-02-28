package com.moare.android.features.search.display.search.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.core.util.Trie
import com.moare.android.core.util.getChosung
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SearchDataState
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.KeywordInfo
import com.moare.android.features.search.models.displaymodels.football.FBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAGameScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamScheduleDisplayModel
import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.models.responsemodels.football.FBGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBPlayerInfoResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBPlayerStandingsResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamInfoResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamStandingsResponseModel
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
    private val trieDeferred: CompletableDeferred<Pair<Trie, List<KeywordInfo>>>
) : MVIViewModel<SearchViewModel.Intent, Nothing>() {
    /* ---------------------
       data state
       --------------------- */
    private val _searchDataState = MutableStateFlow<SearchDataState>(SearchDataState.Idle)
    val searchDataState: StateFlow<SearchDataState> = _searchDataState

    // football
    private val _fbPlayerInfoData = MutableStateFlow<FBPlayerInfoDisplayModel?>(null)
    val fbPlayerInfoData: StateFlow<FBPlayerInfoDisplayModel?> = _fbPlayerInfoData
    private var fbPlayerInfoResponseModel: FBPlayerInfoResponseModel? = null

    private val _fbPlayerStatsData = MutableStateFlow<FBPlayerStatsDisplayModel?>(null)
    val fbPlayerStatsData: StateFlow<FBPlayerStatsDisplayModel?> = _fbPlayerStatsData

    private val _fbPlayerStandingsData = MutableStateFlow<FBPlayerStandingsDisplayModel?>(null)
    val fbPlayerStandingsData: StateFlow<FBPlayerStandingsDisplayModel?> = _fbPlayerStandingsData
    private var fbPlayerStandingsResponseModel: FBPlayerStandingsResponseModel? = null

    private val _fbTeamInfoData = MutableStateFlow<FBTeamInfoDisplayModel?>(null)
    val fbTeamInfoData: StateFlow<FBTeamInfoDisplayModel?> = _fbTeamInfoData
    private var fbTeamInfoResponseModel: FBTeamInfoResponseModel? = null

    private val _fbTeamStatsData = MutableStateFlow<FBTeamStatsDisplayModel?>(null)
    val fbTeamStatsData: StateFlow<FBTeamStatsDisplayModel?> = _fbTeamStatsData

    private val _fbTeamStandingsData = MutableStateFlow<FBTeamStandingsDisplayModel?>(null)
    val fbTeamStandingsData: StateFlow<FBTeamStandingsDisplayModel?> = _fbTeamStandingsData
    private var fbTeamStandingsResponseModel: FBTeamStandingsResponseModel? = null

    private val _fbTeamScheduleData = MutableStateFlow<FBTeamScheduleDisplayModel?>(null)
    val fbTeamScheduleData: StateFlow<FBTeamScheduleDisplayModel?> = _fbTeamScheduleData

    private val _fbLeagueScheduleData = MutableStateFlow<FBLeagueScheduleDisplayModel?>(null)
    val fbLeagueScheduleData: StateFlow<FBLeagueScheduleDisplayModel?> = _fbLeagueScheduleData
    private var initialFBLeagueScheduleData: FBLeagueScheduleDisplayModel? = null // NOTE: Used when go back from FBGameStatsView and reopen FBLeagueScheduleView. Has to think about structure.

    private val _fbGameStatsData = MutableStateFlow<FBGameStatsDisplayModel?>(null)
    val fbGameStatsData: StateFlow<FBGameStatsDisplayModel?> = _fbGameStatsData

    // nba
    private val _nbaPlayerInfoData = MutableStateFlow<NBAPlayerInfoDisplayModel?>(null)
    val nbaPlayerInfoData: StateFlow<NBAPlayerInfoDisplayModel?> = _nbaPlayerInfoData

    private val _nbaPlayerStatsData = MutableStateFlow<NBAPlayerStatsDisplayModel?>(null)
    val nbaPlayerStatsData: StateFlow<NBAPlayerStatsDisplayModel?> = _nbaPlayerStatsData

    private val _nbaPlayerStandingsData = MutableStateFlow<NBAPlayerStandingsDisplayModel?>(null)
    val nbaPlayerStandingsData: StateFlow<NBAPlayerStandingsDisplayModel?> = _nbaPlayerStandingsData

    private val _nbaTeamInfoData = MutableStateFlow<NBATeamInfoDisplayModel?>(null)
    val nbaTeamInfoData: StateFlow<NBATeamInfoDisplayModel?> = _nbaTeamInfoData

    private val _nbaTeamStatsData = MutableStateFlow<NBATeamStatsDisplayModel?>(null)
    val nbaTeamStatsData: StateFlow<NBATeamStatsDisplayModel?> = _nbaTeamStatsData

    private val _nbaTeamStandingsData = MutableStateFlow<NBATeamStandingsDisplayModel?>(null)
    val nbaTeamStandingsData: StateFlow<NBATeamStandingsDisplayModel?> = _nbaTeamStandingsData

    private val _nbaGameScheduleData = MutableStateFlow<NBAGameScheduleDisplayModel?>(null)
    val nbaGameScheduleData: StateFlow<NBAGameScheduleDisplayModel?> = _nbaGameScheduleData

    private val _nbaGameStatsData = MutableStateFlow<NBAGameStatsDisplayModel?>(null)
    val nbaGameStatsData: StateFlow<NBAGameStatsDisplayModel?> = _nbaGameStatsData

    // auto complete
    private val _autoCompleteList = MutableStateFlow<List<String>>(emptyList())
    val autoCompleteList: StateFlow<List<String>> = _autoCompleteList

    private val _trendingKeywordList = MutableStateFlow<List<String>>(emptyList())
    val trendingKeywordList: StateFlow<List<String>> = _trendingKeywordList

    /* ---------------------
       ui state
       --------------------- */
    private val _firstOpened = MutableStateFlow(false)
    val firstOpened: StateFlow<Boolean> = _firstOpened

    private val _focusRequester = MutableStateFlow(FocusRequester())
    val focusRequester: StateFlow<FocusRequester> = _focusRequester

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

    // NOTE: viewStack should always be up to date
    private val _viewStack = MutableStateFlow<List<SportDecodableModel>>(emptyList())
    val viewStack: StateFlow<List<SportDecodableModel>> = _viewStack

    private val _poppedView = MutableStateFlow<SportDecodableModel?>(null)
    val poppedView: StateFlow<SportDecodableModel?> = _poppedView

    private var trendingKeywords: Map<String, KeywordInfo> = emptyMap()

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
        }

        fetchTrendingKeywords()
    }

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data object FirstOpen : Intent()
        data class PerformSearch(val searchType: SearchType = SearchType.QUERY, val aniDuration: Long = 0) : Intent()
        data object ToggleFocusState : Intent()
        data class UpdateTextField(val newValue: TextFieldValue, val updateAutoCompleteList: Boolean = true) : Intent()

        data object ToggleSearchBar : Intent()
        data object ToggleAutoCompleteListVisibleState : Intent()

        data class SelectFBGame(val game: FBGame) : Intent()

        data object GoBack : Intent()

        data class ShowPlayerStats(val from: String, val playerId: Int = 0) : Intent()
        data class ShowTeamStats(val from: String, val teamId: Int = 0) : Intent()
        data class ShowGameStats(val from: String, val dd: String) : Intent()

        data object RefreshGame : Intent()
        data class UpdateLastViewStack(val data: SportDecodableModel) : Intent()
    }

    enum class SearchType {
        QUERY, TRENDING_KEYWORD, AUTO_COMPLETE
    }

    override fun send(intent: Intent) {
        // TODO: 비동기를 여기서 실행할지, 각 implements에서 실행할지 고민필요
        viewModelScope.launch {
            when (intent) {
                is Intent.FirstOpen -> firstOpen()
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
                is Intent.ToggleFocusState -> toggleFocusState()
                is Intent.ToggleAutoCompleteListVisibleState -> toggleAutoCompleteListVisibleState()
                is Intent.UpdateTextField -> updateTextField(intent.newValue, intent.updateAutoCompleteList)
                is Intent.ToggleSearchBar -> toggleSearchBar()
                is Intent.SelectFBGame -> selectFBGame(intent.game)
                is Intent.GoBack -> goBack()
                is Intent.ShowPlayerStats -> showPlayerStats(intent.from, intent.playerId)
                is Intent.ShowTeamStats -> showTeamStats(intent.from, intent.teamId)
                is Intent.ShowGameStats -> showGameStats(intent.from, intent.dd)
                is Intent.RefreshGame -> refreshGame()
                is Intent.UpdateLastViewStack -> updateLastViewStack(intent.data)
            }
        }
    }

    /* ---------------------
       implements
       --------------------- */
    private suspend fun firstOpen() {
        _firstOpened.emit(true)
    }

    private fun fetchTrendingKeywords() {
        viewModelScope.launch {
            try {
                trendingKeywords = keywordsClient.fetchTrendingKeywords().associateBy { it.keyword }
                _trendingKeywordList.emit(trendingKeywords.keys.toList())
            } catch (e: Exception) {
                Log.e("dsdf", e.localizedMessage ?: "trendingKeywords error")
            }
        }
    }

    private suspend fun performSearch(searchType: SearchType, aniDuration: Long) {
        // animation/search start time
        val startTime = System.currentTimeMillis()

        try {
            _searchState.emit(true)
            toggleFocusState()

            val dataFetchDeferred = viewModelScope.async {
//                delay(5000) // test for fetching delay
                when (searchType) {
                    SearchType.QUERY -> searchClient.fetchDataByQuery(context, query.value.text)
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
                _searchDataState.emit(SearchDataState.Fetching)
            }

            val data = dataFetchDeferred.await()

            // hide loading first before showing data
            _searchDataState.emit(SearchDataState.Success)

            _fbPlayerInfoData.emit(null)
            _fbPlayerStatsData.emit(null)
            _fbPlayerStandingsData.emit(null)
            _fbTeamInfoData.emit(null)
            _fbTeamStatsData.emit(null)
            _fbTeamStandingsData.emit(null)
            _fbTeamScheduleData.emit(null)
            _fbLeagueScheduleData.emit(null)
            _fbGameStatsData.emit(null)

            when (val data = data?.data) {
                is SportDecodableModel.FBPlayerInfo -> {
                    fbPlayerInfoResponseModel = data.responseModel

                    _fbPlayerInfoData.emit(data.displayModel)
                }
                is SportDecodableModel.FBPlayerStats -> {
                    _fbPlayerStatsData.emit(data.displayModel)
                }
                is SportDecodableModel.FBPlayerStandings -> {
                    fbPlayerStandingsResponseModel = data.responseModel

                    _fbPlayerStandingsData.emit(data.displayModel)
                }
                is SportDecodableModel.FBTeamInfo -> {
                    fbTeamInfoResponseModel = data.responseModel

                    _fbTeamInfoData.emit(data.displayModel)
                }
                is SportDecodableModel.FBTeamStats -> {
                    _fbTeamStatsData.emit(data.displayModel)
                }
                is SportDecodableModel.FBTeamStandings -> {
                    fbTeamStandingsResponseModel = data.responseModel

                    _fbTeamStandingsData.emit(data.displayModel)
                }
                is SportDecodableModel.FBTeamSchedule -> {
                    _fbTeamScheduleData.emit(data.displayModel)
                }
                is SportDecodableModel.FBLeagueSchedule -> {
                    _fbLeagueScheduleData.emit(data.displayModel)
                    initialFBLeagueScheduleData = data.displayModel
                }
                is SportDecodableModel.FBGameStats -> {
                    _fbGameStatsData.emit(data.displayModel)
                }
                else -> {
                    throw IllegalArgumentException("Unknown data type")
                }
            }

            // add viewStack
            val stack = viewStack.value.toMutableList()
            stack.add(data.data)
            _viewStack.emit(stack)

            _resultVisibleState.emit(true)
        } catch (e: Exception) {
            _searchDataState.emit(SearchDataState.Error("검색 결과가 없습니다."))
            Log.e("dsdf", e.localizedMessage ?: "data type error")
        }
    }

    private suspend fun toggleFocusState() {
        val currentFocusState = focusState.value

        if (currentFocusState) {
            _focusRequester.value.freeFocus()
            _focusState.emit(false)
        } else {
            // move textfield's cursor to the end of the query
            _query.emit(query.value.copy(
                selection = TextRange(query.value.text.length)
            ))
            _focusRequester.value.requestFocus()
            _focusState.emit(true)
        }
    }

    private suspend fun toggleAutoCompleteListVisibleState() {
        _autoCompleteListVisibleState.emit(!autoCompleteListVisibleState.value)
    }

    private suspend fun updateTextField(newValue: TextFieldValue, updateAutoCompleteList: Boolean = true) {
        _query.emit(newValue)

        // auto complete
        if (updateAutoCompleteList) {
            if (newValue.text.isBlank()) {
                _autoCompleteList.emit(emptyList())
                _autoCompleteListVisibleState.emit(false)
            } else {
//                val result = mutableSetOf<String>()
//                result.addAll(trie.search(newValue.text))
//                result.addAll(trie.search(getChosung(newValue.text)))
//                _autoCompleteList.emit(result.toList())

                val result = mutableListOf<String>()
                result.addAll(trie.search(getChosung(newValue.text)))

                val additionalResult = trie.search(newValue.text)

                for (word in additionalResult) {
                    if (!result.contains(word)) {
                        result.add(word)
                    }
                }

                _autoCompleteList.emit(result)
                _autoCompleteListVisibleState.emit(true)
            }
        }
    }

    private suspend fun toggleSearchBar() {
        val currentSearchState = searchState.value

        if (currentSearchState) {
            _searchState.emit(false)
            _resultVisibleState.emit(false)
            _searchDataState.emit(SearchDataState.Idle)
            updateTextField(query.value)
            delay(1000)
            toggleFocusState()
        } else {
            _searchState.emit(true)
        }
    }

    private suspend fun selectFBGame(game: FBGame) {
        val dataModel = SportDecodableModel.FBGameStats(
            responseModel = FBGameStatsResponseModel(game = game),
            displayModel = FBGameStatsDisplayModel(game = game)
        )

        // add stack before emiting _fbGameStatsData to ensure the last stack(SportDecodableModel.FBGameStats in this case) can be up to date after refreshing game data when opening FBGameStatsView
        val stack = viewStack.value.toMutableList()
        stack.add(dataModel)
        _viewStack.emit(stack)

        _fbGameStatsData.emit(FBGameStatsDisplayModel(game = game))
    }

    private suspend fun goBack() {
        val stack = viewStack.value.toMutableList()
        val lastView = stack.removeLastOrNull()
        _poppedView.emit(lastView)

        val viewToShow = stack.lastOrNull()

        lastView?.let { lastView ->
            _resultVisibleState.emit(false)

            if (viewToShow == null) {
                toggleSearchBar()
            } else {
                delay(1000)

                _fbPlayerInfoData.emit(null)
                _fbPlayerStatsData.emit(null)
                _fbPlayerStandingsData.emit(null)
                _fbTeamInfoData.emit(null)
                _fbTeamStandingsData.emit(null)
                _fbTeamScheduleData.emit(null)
                _fbLeagueScheduleData.emit(null)
                _fbGameStatsData.emit(null)
                _fbTeamStatsData.emit(null)

                when (viewToShow) {
                    is SportDecodableModel.FBPlayerInfo -> {
                        _fbPlayerInfoData.emit(viewToShow.displayModel)
                    }
                    is SportDecodableModel.FBPlayerStats -> {
                        _fbPlayerStatsData.emit(viewToShow.displayModel)
                    }
                    is SportDecodableModel.FBPlayerStandings -> {
                        _fbPlayerStandingsData.emit(viewToShow.displayModel)
                    }
                    is SportDecodableModel.FBTeamInfo -> {
                        _fbTeamInfoData.emit(viewToShow.displayModel)
                    }
                    is SportDecodableModel.FBTeamStats -> {
                        _fbTeamStatsData.emit(viewToShow.displayModel)
                    }
                    is SportDecodableModel.FBTeamStandings -> {
                        _fbTeamStandingsData.emit(viewToShow.displayModel)
                    }
                    is SportDecodableModel.FBTeamSchedule -> {
                        _fbTeamScheduleData.emit(viewToShow.displayModel)
                    }
                    is SportDecodableModel.FBLeagueSchedule -> {
                        if (lastView is SportDecodableModel.FBGameStats) {
                            _fbLeagueScheduleData.emit(initialFBLeagueScheduleData)
                        } else {
                            _fbLeagueScheduleData.emit(viewToShow.displayModel)
                        }
                    }
                    is SportDecodableModel.FBGameStats -> {
                        _fbGameStatsData.emit(viewToShow.displayModel)
                    }
                    else -> {}
                }

                _resultVisibleState.emit(true)
            }

            _viewStack.emit(stack)
        }
    }

    private suspend fun showPlayerStats(from: String, playerId: Int) {
        val modelConverter = ModelConverter()

        var playerInfoResponseModel: FBPlayerInfoResponseModel? = null
        var stats: FBPlayerStatsDisplayModel? = null

        if (from == "standings") {
            fbPlayerStandingsResponseModel?.let {
                val player = it.standings.find { player ->
                    player.player.id == playerId
                }

                playerInfoResponseModel = FBPlayerInfoResponseModel(info = player)

                stats = modelConverter.fbPlayerStatsConverter(playerInfoResponseModel!!)
            }
        } else {
            fbPlayerInfoResponseModel?.let {
                playerInfoResponseModel = it

                stats = modelConverter.fbPlayerStatsConverter(it)
            }
        }

        _resultVisibleState.emit(false)
        delay(1000)

        _fbPlayerInfoData.emit(null)
        _fbPlayerStandingsData.emit(null)
        _fbTeamInfoData.emit(null)
        _fbTeamStatsData.emit(null)
        _fbTeamStandingsData.emit(null)
        _fbTeamScheduleData.emit(null)
        _fbLeagueScheduleData.emit(null)
        _fbGameStatsData.emit(null)
        _fbPlayerStatsData.emit(stats)

        val dataModel = SportDecodableModel.FBPlayerStats(
            responseModel = playerInfoResponseModel!!,
            displayModel = stats!!
        )

        val stack = viewStack.value.toMutableList()
        stack.add(dataModel)
        _viewStack.emit(stack)

        _resultVisibleState.emit(true)
    }

    private suspend fun showTeamStats(from: String, teamId: Int) {
        val modelConverter = ModelConverter()

        var teamInfoResponseModel: FBTeamInfoResponseModel? = null
        var stats: FBTeamStatsDisplayModel? = null

        if (from == "standings") {
            fbTeamStandingsResponseModel?.let {
                val team = it.standings.find { team ->
                    team.team.id == teamId
                }

                teamInfoResponseModel = FBTeamInfoResponseModel(info = team)

                stats = modelConverter.fbTeamStatsConverter(teamInfoResponseModel!!)
            }
        } else {
            fbTeamInfoResponseModel?.let {
                teamInfoResponseModel = it

                stats = modelConverter.fbTeamStatsConverter(it)
            }
        }

        _resultVisibleState.emit(false)
        delay(1000)

        _fbPlayerInfoData.emit(null)
        _fbPlayerStatsData.emit(null)
        _fbPlayerStandingsData.emit(null)
        _fbTeamInfoData.emit(null)
        _fbTeamStandingsData.emit(null)
        _fbTeamScheduleData.emit(null)
        _fbLeagueScheduleData.emit(null)
        _fbGameStatsData.emit(null)
        _fbTeamStatsData.emit(stats)

        val dataModel = SportDecodableModel.FBTeamStats(
            responseModel = teamInfoResponseModel!!,
            displayModel = stats!!
        )

        val stack = viewStack.value.toMutableList()
        stack.add(dataModel)
        _viewStack.emit(stack)

        _resultVisibleState.emit(true)
    }

    private suspend fun showGameStats(from: String, dd: String) {
        val modelConverter = ModelConverter()

        var gameStatsReponseModel: FBGameStatsResponseModel? = null
        var stats: FBGameStatsDisplayModel? = null

        // TODO: from 대신에 stack으로 판단
        if (from == "player") {
            fbPlayerInfoResponseModel?.let {
                gameStatsReponseModel = if (dd == "previous") FBGameStatsResponseModel(it.lastGame) else FBGameStatsResponseModel(it.nextGame)
                stats = modelConverter.fbGameStatsConverter(gameStatsReponseModel!!)
            }
        } else {
            fbTeamInfoResponseModel?.let {
                gameStatsReponseModel = if (dd == "previous") FBGameStatsResponseModel(it.lastGame) else FBGameStatsResponseModel(it.nextGame)
                stats = modelConverter.fbGameStatsConverter(gameStatsReponseModel!!)
            }
        }

        _resultVisibleState.emit(false)
        delay(1000)

        _fbPlayerInfoData.emit(null)
        _fbPlayerStatsData.emit(null)
        _fbPlayerStandingsData.emit(null)
        _fbTeamInfoData.emit(null)
        _fbTeamStatsData.emit(null)
        _fbTeamStandingsData.emit(null)
        _fbTeamScheduleData.emit(null)
        _fbLeagueScheduleData.emit(null)
        _fbGameStatsData.emit(stats)

        val dataModel = SportDecodableModel.FBGameStats(
            responseModel = gameStatsReponseModel!!,
            displayModel = stats!!
        )

        val stack = viewStack.value.toMutableList()
        stack.add(dataModel)
        _viewStack.emit(stack)

        _resultVisibleState.emit(true)
    }

    private suspend fun refreshGame() {
        try {
            val game = fbGameStatsData.value?.game
            game?.let {
                val result = searchClient.fetchGameInfo("football", it.fixture.date, it.league.id, it.fixture.id)

                if (result.data is SportDecodableModel.FBGameStats) {
                    val data = result.data
                    _fbGameStatsData.emit(data.displayModel)

                    updateLastViewStack(data)
                }
            }
        } catch (e: Exception) {
            Log.e("dsdf", e.localizedMessage ?: "error")
        }
    }

    private suspend fun updateLastViewStack(data: SportDecodableModel) {
        val stack = viewStack.value.dropLast(1).toMutableList()
        stack.add(data)
        _viewStack.emit(stack)
    }
}















