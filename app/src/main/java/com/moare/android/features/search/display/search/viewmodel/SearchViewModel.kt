package com.moare.android.features.search.display.search.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.core.util.Trie
import com.moare.android.core.util.getChosung
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.ApiFetchState
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
import com.moare.android.features.search.models.displaymodels.nba.NBATeamScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAGameStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBAPlayerStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamInfoDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStatsDisplayModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamScheduleDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBALeagueScheduleDisplayModel
import com.moare.android.features.search.models.NoticeModel
import com.moare.android.features.search.models.TrendingKeywords
import com.moare.android.features.search.models.models.football.FBGame
import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.models.responsemodels.football.FBGameStatsResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBPlayerInfoResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamInfoResponseModel
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

    // football
    private val _fbPlayerInfoData = MutableStateFlow<FBPlayerInfoDisplayModel?>(null)
    val fbPlayerInfoData: StateFlow<FBPlayerInfoDisplayModel?> = _fbPlayerInfoData

    private val _fbPlayerStatsData = MutableStateFlow<FBPlayerStatsDisplayModel?>(null)
    val fbPlayerStatsData: StateFlow<FBPlayerStatsDisplayModel?> = _fbPlayerStatsData

    private val _fbPlayerStandingsData = MutableStateFlow<FBPlayerStandingsDisplayModel?>(null)
    val fbPlayerStandingsData: StateFlow<FBPlayerStandingsDisplayModel?> = _fbPlayerStandingsData

    private val _fbTeamInfoData = MutableStateFlow<FBTeamInfoDisplayModel?>(null)
    val fbTeamInfoData: StateFlow<FBTeamInfoDisplayModel?> = _fbTeamInfoData

    private val _fbTeamStatsData = MutableStateFlow<FBTeamStatsDisplayModel?>(null)
    val fbTeamStatsData: StateFlow<FBTeamStatsDisplayModel?> = _fbTeamStatsData

    private val _fbTeamStandingsData = MutableStateFlow<FBTeamStandingsDisplayModel?>(null)
    val fbTeamStandingsData: StateFlow<FBTeamStandingsDisplayModel?> = _fbTeamStandingsData

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

    private val _nbaTeamScheduleData = MutableStateFlow<NBATeamScheduleDisplayModel?>(null)
    val nbaTeamScheduleData: StateFlow<NBATeamScheduleDisplayModel?> = _nbaTeamScheduleData

    private val _nbaLeagueScheduleData = MutableStateFlow<NBALeagueScheduleDisplayModel?>(null)
    val nbaLeagueScheduleData: StateFlow<NBALeagueScheduleDisplayModel?> = _nbaLeagueScheduleData
    private var initialNBALeagueScheduleData: NBALeagueScheduleDisplayModel? = null

    private val _nbaGameStatsData = MutableStateFlow<NBAGameStatsDisplayModel?>(null)
    val nbaGameStatsData: StateFlow<NBAGameStatsDisplayModel?> = _nbaGameStatsData

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

        data class SelectFBGame(val game: FBGame, val leagueId: Int?) : Intent()
        data class SelectNBAGame(val game: NBAGame) : Intent()

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
            _fbPlayerInfoData.emit(null)
            _fbPlayerStatsData.emit(null)
            _fbPlayerStandingsData.emit(null)
            _fbTeamInfoData.emit(null)
            _fbTeamStatsData.emit(null)
            _fbTeamStandingsData.emit(null)
            _fbTeamScheduleData.emit(null)
            _fbLeagueScheduleData.emit(null)
            _fbGameStatsData.emit(null)

            _nbaPlayerInfoData.emit(null)
            _nbaPlayerStatsData.emit(null)
            _nbaPlayerStandingsData.emit(null)
            _nbaTeamInfoData.emit(null)
            _nbaTeamStatsData.emit(null)
            _nbaTeamStandingsData.emit(null)
            _nbaTeamScheduleData.emit(null)
            _nbaLeagueScheduleData.emit(null)
            _nbaGameStatsData.emit(null)

            when (val data = data?.data) {
                is SportDecodableModel.FBPlayerInfo -> {
                    _fbPlayerInfoData.emit(data.displayModel)
                }
                is SportDecodableModel.FBPlayerStats -> {
                    _fbPlayerStatsData.emit(data.displayModel)
                }
                is SportDecodableModel.FBPlayerStandings -> {
                    _fbPlayerStandingsData.emit(data.displayModel)
                }
                is SportDecodableModel.FBTeamInfo -> {
                    _fbTeamInfoData.emit(data.displayModel)
                }
                is SportDecodableModel.FBTeamStats -> {
                    _fbTeamStatsData.emit(data.displayModel)
                }
                is SportDecodableModel.FBTeamStandings -> {
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

                is SportDecodableModel.NBAPlayerInfo -> {
                    _nbaPlayerInfoData.emit(data.displayModel)
                }
                is SportDecodableModel.NBAPlayerStats -> {
                    _nbaPlayerStatsData.emit(data.displayModel)
                }
                is SportDecodableModel.NBAPlayerStandings -> {
                    _nbaPlayerStandingsData.emit(data.displayModel)
                }
                is SportDecodableModel.NBATeamInfo -> {
                    _nbaTeamInfoData.emit(data.displayModel)
                }
                is SportDecodableModel.NBATeamStats -> {
                    _nbaTeamStatsData.emit(data.displayModel)
                }
                is SportDecodableModel.NBATeamStandings -> {
                    _nbaTeamStandingsData.emit(data.displayModel)
                }
                is SportDecodableModel.NBATeamSchedule -> {
                    _nbaTeamScheduleData.emit(data.displayModel)
                }
                is SportDecodableModel.NBALeagueSchedule -> {
                    _nbaLeagueScheduleData.emit(data.displayModel)
                    initialNBALeagueScheduleData = data.displayModel
                }
                is SportDecodableModel.NBAGameStats -> {
                    _nbaGameStatsData.emit(data.displayModel)
                }

                else -> {
                    throw IllegalArgumentException("Unknown data type")
                }
            }

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

    private suspend fun selectFBGame(game: FBGame, leagueId: Int?) {
        val dataModel = SportDecodableModel.FBGameStats(
            responseModel = FBGameStatsResponseModel(game = game),
            displayModel = FBGameStatsDisplayModel(game = game, leagueId = leagueId)
        )

        // add stack before emiting _fbGameStatsData to ensure the last stack(SportDecodableModel.FBGameStats in this case) can be up to date after refreshing game data when opening FBGameStatsView
        val stack = viewStack.value.toMutableList()
        stack.add(dataModel)
        _viewStack.emit(stack)
        _poppedView.emit(null)

        _fbGameStatsData.emit(FBGameStatsDisplayModel(game = game, leagueId = leagueId))
    }

    private suspend fun selectNBAGame(game: NBAGame) {
        val dataModel = SportDecodableModel.NBAGameStats(
            responseModel = NBAGameStatsResponseModel(game = game),
            displayModel = NBAGameStatsDisplayModel(game = game)
        )

        val stack = viewStack.value.toMutableList()
        stack.add(dataModel)
        _viewStack.emit(stack)
        _poppedView.emit(null)

        _nbaGameStatsData.emit(NBAGameStatsDisplayModel(game = game))
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
                    _fbPlayerInfoData.emit(null)
                    _fbPlayerStatsData.emit(null)
                    _fbPlayerStandingsData.emit(null)
                    _fbTeamInfoData.emit(null)
                    _fbTeamStandingsData.emit(null)
                    _fbTeamScheduleData.emit(null)
                    _fbLeagueScheduleData.emit(null)
                    _fbGameStatsData.emit(null)
                    _fbTeamStatsData.emit(null)

                    _nbaPlayerInfoData.emit(null)
                    _nbaPlayerStatsData.emit(null)
                    _nbaPlayerStandingsData.emit(null)
                    _nbaTeamInfoData.emit(null)
                    _nbaTeamStatsData.emit(null)
                    _nbaTeamStandingsData.emit(null)
                    _nbaTeamScheduleData.emit(null)
                    _nbaLeagueScheduleData.emit(null)
                    _nbaGameStatsData.emit(null)

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

                        is SportDecodableModel.NBAPlayerInfo -> {
                            _nbaPlayerInfoData.emit(viewToShow.displayModel)
                        }
                        is SportDecodableModel.NBAPlayerStats -> {
                            _nbaPlayerStatsData.emit(viewToShow.displayModel)
                        }
                        is SportDecodableModel.NBAPlayerStandings -> {
                            _nbaPlayerStandingsData.emit(viewToShow.displayModel)
                        }
                        is SportDecodableModel.NBATeamInfo -> {
                            _nbaTeamInfoData.emit(viewToShow.displayModel)
                        }
                        is SportDecodableModel.NBATeamStats -> {
                            _nbaTeamStatsData.emit(viewToShow.displayModel)
                        }
                        is SportDecodableModel.NBATeamStandings -> {
                            _nbaTeamStandingsData.emit(viewToShow.displayModel)
                        }
                        is SportDecodableModel.NBATeamSchedule -> {
                            _nbaTeamScheduleData.emit(viewToShow.displayModel)
                        }
                        is SportDecodableModel.NBALeagueSchedule -> {
                            if (lastView is SportDecodableModel.NBAGameStats) {
                                _nbaLeagueScheduleData.emit(initialNBALeagueScheduleData)
                            } else {
                                _nbaLeagueScheduleData.emit(viewToShow.displayModel)
                            }
                        }
                        is SportDecodableModel.NBAGameStats -> {
                            _nbaGameStatsData.emit(viewToShow.displayModel)
                        }

                        else -> {}
                    }

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
                    val game = fbGameStatsData.value?.game
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
                    val gameSummary = nbaGameStatsData.value?.game?.gameSummary
                    val boxScoreTraditional = nbaGameStatsData.value?.game?.boxScoreTraditional
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
                val responseModel = NBAGameScheduleResponseModel(scheduledMonths = emptyList(), schedule = gameList)
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

    private suspend fun updateMainDisplayModel(data: SportDecodableModel, shouldReset: Boolean = true) {
        if (shouldReset) {
            _fbPlayerInfoData.emit(null)
            _fbPlayerStatsData.emit(null)
            _fbPlayerStandingsData.emit(null)
            _fbTeamInfoData.emit(null)
            _fbTeamStatsData.emit(null)
            _fbTeamStandingsData.emit(null)
            _fbTeamScheduleData.emit(null)
            _fbLeagueScheduleData.emit(null)
            _fbGameStatsData.emit(null)

            _nbaPlayerInfoData.emit(null)
            _nbaPlayerStatsData.emit(null)
            _nbaPlayerStandingsData.emit(null)
            _nbaTeamInfoData.emit(null)
            _nbaTeamStatsData.emit(null)
            _nbaTeamStandingsData.emit(null)
            _nbaTeamScheduleData.emit(null)
            _nbaLeagueScheduleData.emit(null)
            _nbaGameStatsData.emit(null)
        }

        when (data) {
            is SportDecodableModel.FBPlayerInfo -> {
                _fbPlayerInfoData.emit(data.displayModel)
            }
            is SportDecodableModel.FBPlayerStats -> {
                _fbPlayerStatsData.emit(data.displayModel)
            }
            is SportDecodableModel.FBPlayerStandings -> {
                _fbPlayerStandingsData.emit(data.displayModel)
            }
            is SportDecodableModel.FBTeamInfo -> {
                _fbTeamInfoData.emit(data.displayModel)
            }
            is SportDecodableModel.FBTeamStats -> {
                _fbTeamStatsData.emit(data.displayModel)
            }
            is SportDecodableModel.FBTeamStandings -> {
                _fbTeamStandingsData.emit(data.displayModel)
            }
            is SportDecodableModel.FBTeamSchedule -> {
                _fbTeamScheduleData.emit(data.displayModel)
            }
            is SportDecodableModel.FBLeagueSchedule -> {
                _fbLeagueScheduleData.emit(data.displayModel)
            }
            is SportDecodableModel.FBGameStats -> {
                _fbGameStatsData.emit(data.displayModel)
            }

            is SportDecodableModel.NBAPlayerInfo -> {
                _nbaPlayerInfoData.emit(data.displayModel)
            }
            is SportDecodableModel.NBAPlayerStats -> {
                _nbaPlayerStatsData.emit(data.displayModel)
            }
            is SportDecodableModel.NBAPlayerStandings -> {
                _nbaPlayerStandingsData.emit(data.displayModel)
            }
            is SportDecodableModel.NBATeamInfo -> {
                _nbaTeamInfoData.emit(data.displayModel)
            }
            is SportDecodableModel.NBATeamStats -> {
                _nbaTeamStatsData.emit(data.displayModel)
            }
            is SportDecodableModel.NBATeamStandings -> {
                _nbaTeamStandingsData.emit(data.displayModel)
            }
            is SportDecodableModel.NBATeamSchedule -> {
                _nbaTeamScheduleData.emit(data.displayModel)
            }
            is SportDecodableModel.NBALeagueSchedule -> {
                _nbaLeagueScheduleData.emit(data.displayModel)
            }
            is SportDecodableModel.NBAGameStats -> {
                _nbaGameStatsData.emit(data.displayModel)
            }

            else -> {}
        }
    }
}















