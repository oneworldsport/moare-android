package com.moare.android.features.search.display.search.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.moare.android.core.util.Trie
import com.moare.android.features.search.display.search.viewmodel.SearchStore.SearchType
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.KeywordInfo
import com.moare.android.features.search.models.NoticeModel
import com.moare.android.features.search.models.SportDisplayType
import com.moare.android.features.search.models.TrendingKeywords
import com.moare.android.features.search.models.models.nba.NBAGame
import com.moare.android.features.search.networking.KeywordsClient
import com.moare.android.features.search.networking.SearchClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

sealed interface SearchAction {
    data object BarFirstOpen : SearchAction
    data class PerformSearch(val searchType: SearchType = SearchType.QUERY, val aniDuration: Long = 0) : SearchAction
    data class ToggleFocusState(val isFocused: Boolean) : SearchAction
    data class UpdateTextField(val newValue: TextFieldValue, val updateAutoCompleteList: Boolean = true) : SearchAction

    data object ToggleSearchBar : SearchAction
    data object ToggleAutoCompleteListVisibleState : SearchAction

    data class SelectNBATournamentRound(val gameList: List<NBAGame>) : SearchAction

    data class TestSearch(val viewForTest: SportDisplayType) : SearchAction
}

sealed interface SearchDelegate {
    data class Push(val model: SportDecodableModel) : SearchDelegate
}

class SearchStore @AssistedInject constructor(
    @ApplicationContext private val context: Context,
    private val searchClient: SearchClient,
    private val keywordsClient: KeywordsClient,
    private val trieDeferred: CompletableDeferred<Pair<Trie, List<KeywordInfo>>>,
    private val noticeDeferred: CompletableDeferred<List<NoticeModel>>,
    private val trendingKeywordsDeferred: CompletableDeferred<TrendingKeywords>,
    @Assisted val emitToParent: (SearchDelegate) -> Unit
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    
    private val _searchDataState = MutableStateFlow<ApiFetchState>(ApiFetchState.Idle)
    val searchDataState: StateFlow<ApiFetchState> = _searchDataState

    // auto complete
    private val _autoCompleteList = MutableStateFlow<List<String>>(emptyList())
    val autoCompleteList: StateFlow<List<String>> = _autoCompleteList

    private val _trendingKeywordList = MutableStateFlow<List<String>>(emptyList())
    val trendingKeywordList: StateFlow<List<String>> = _trendingKeywordList

    private val _noticeList = MutableStateFlow<List<NoticeModel>>(emptyList())
    val noticeList: StateFlow<List<NoticeModel>> = _noticeList

    private val _searchExample = MutableStateFlow("")
    val searchExample: StateFlow<String> = _searchExample

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

    /* ---------------------
       init
       --------------------- */
    init {
        scope.launch {
            // test
//            val inputStream = context.assets.open("football_player_standings.json")
//            val jsonContent = inputStream.bufferedReader().use { it.readText() }
//
//            val data = DataModel.fromJson(jsonContent).data as SportDecodableModel.FBPlayerStandings
//            _fbPlayerStandingsData.emit(data.displayModel)
//            delay(5000)
            val keywords = trendingKeywordsDeferred.await().keywords
            trendingKeywords = keywords.associateBy { it.keyword }
            _trendingKeywordList.emit(keywords.map { it.keyword })

            val noticeData = noticeDeferred.await()
            _searchExample.value = noticeData.find { it.title == "검색 예시" }?.content ?: ""
            _noticeList.value = noticeData.filter { it.title != "검색 예시" }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            emitToParent: (SearchDelegate) -> Unit
        ): SearchStore
    }

    enum class SearchType {
        QUERY, TRENDING_KEYWORD, AUTO_COMPLETE
    }

    fun send(action: SearchAction) {
        // TODO: 비동기를 여기서 실행할지, 각 implements에서 실행할지 고민필요
        scope.launch {
            when (action) {
                is SearchAction.BarFirstOpen -> barFirstOpen()
                is SearchAction.PerformSearch -> {
                    if (query.value.text.isBlank()) {
                        val firstTrendingKeyword = trendingKeywordList.value.firstOrNull()
                        if (!firstTrendingKeyword.isNullOrBlank()) {
                            updateTextField(TextFieldValue(firstTrendingKeyword), false)
                            performSearch(SearchType.TRENDING_KEYWORD, action.aniDuration)
                        }
                    } else {
                        performSearch(action.searchType, action.aniDuration)
                    }
                }
                is SearchAction.ToggleFocusState -> toggleFocusState(action.isFocused)
                is SearchAction.ToggleAutoCompleteListVisibleState -> toggleAutoCompleteListVisibleState()
                is SearchAction.UpdateTextField -> updateTextField(action.newValue, action.updateAutoCompleteList)
                is SearchAction.ToggleSearchBar -> toggleSearchBar()
                is SearchAction.SelectNBATournamentRound -> selectNBATournamentRound(action.gameList)

                is SearchAction.TestSearch -> testSearch(action.viewForTest)
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

            val dataFetchDeferred = scope.async {
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

            when (data?.data) {
                is SportDecodableModel.FBPlayerInfo, is SportDecodableModel.FBPlayerStandings, is SportDecodableModel.FBTeamInfo,
                is SportDecodableModel.FBTeamStats, is SportDecodableModel.FBTeamStandings, is SportDecodableModel.FBLeagueSchedule,
                is SportDecodableModel.FBGameStats, is SportDecodableModel.FBTournament, is SportDecodableModel.NBAPlayerInfo, is SportDecodableModel.NBAPlayerStats,
                is SportDecodableModel.NBAPlayerStandings, is SportDecodableModel.NBATeamInfo, is SportDecodableModel.NBATeamStats,
                is SportDecodableModel.NBATeamStandings, is SportDecodableModel.NBALeagueSchedule, is SportDecodableModel.NBAGameStats,
                is SportDecodableModel.NBATournament, is SportDecodableModel.KBOPlayerInfo, is SportDecodableModel.KBOPlayerStats,
                is SportDecodableModel.KBOPlayerStandings, is SportDecodableModel.KBOTeamInfo, is SportDecodableModel.KBOTeamStats,
                is SportDecodableModel.KBOTeamStandings, is SportDecodableModel.KBOLeagueSchedule, is SportDecodableModel.KBOGameStats, is SportDecodableModel.KBOTournament,
                is SportDecodableModel.MLBPlayerInfo, is SportDecodableModel.MLBPlayerStats, is SportDecodableModel.MLBPlayerStandings,
                is SportDecodableModel.MLBTeamInfo, is SportDecodableModel.MLBTeamStats, is SportDecodableModel.MLBTeamStandings,
                is SportDecodableModel.MLBLeagueSchedule, is SportDecodableModel.MLBGameStats, is SportDecodableModel.MLBTournament -> null
                else -> {
                    throw IllegalArgumentException("Unknown data type")
                }
            }

            _resultVisibleState.emit(true)

            emitToParent(SearchDelegate.Push(model = data.data))
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

    private suspend fun selectNBATournamentRound(gameList: List<NBAGame>) {
//        val modelConverter = ModelConverter()
//
//        val dataModel: SportDecodableModel
//
//        when (val lastView = viewStack.value.lastOrNull()) {
//            is SportDecodableModel.NBALeagueTournament-> {
//                val responseModel = NBAGameScheduleResponseModel(
//                    scheduleType = ScheduleType.TEAM_FLAT,
//                    scheduledMonths = emptyList(),
//                    schedule = modelConverter.nbaGameListToGameScheduleListConverter(gameList)
//                )
//                dataModel = SportDecodableModel.NBALeagueSchedule(
//                    responseModel = responseModel,
//                    displayModel = modelConverter.nbaLeagueScheduleConverter(responseModel)
//                )
//            }
//
//            else -> return // Make it do nothing
//        }
//
//        _resultVisibleState.emit(false)
//        delay(1000)
//
//        _resultVisibleState.emit(true)
    }

    // test code
    private suspend fun testSearch(viewForTest: SportDisplayType) {
        try {
            _searchState.emit(true)
            toggleFocusState(false)

            val result = searchClient.fetchFromJson(context, viewForTest)

            _resultVisibleState.emit(true)
        } catch (e: Exception) {
            _searchDataState.emit(ApiFetchState.Error("검색 결과가 없습니다."))
            Log.e("dsdf", e.localizedMessage ?: "data type error")
        }
    }
}















