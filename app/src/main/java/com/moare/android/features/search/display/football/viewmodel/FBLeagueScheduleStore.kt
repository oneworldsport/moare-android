package com.moare.android.features.search.display.football.viewmodel

import android.util.Log
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.DayInfo
import com.moare.android.features.search.display.common.viewmodel.BaseScheduleStore
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBLeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.football.FBGameForSchedule
import com.moare.android.features.search.models.models.football.FBLeague
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import com.moare.android.features.search.networking.SearchClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface FBLeagueScheduleAction {
    data object InitData : FBLeagueScheduleAction
    data class SelectYearMonth(val yearMonth: String, val selectedIndex: Int) : FBLeagueScheduleAction
    data class SelectDay(val day: DayInfo, val selectedIndex: Int) : FBLeagueScheduleAction
    data object ToggleAllResult : FBLeagueScheduleAction
    data class UpdateResultOpenedState(val gameId: String, val isOpened: Boolean) : FBLeagueScheduleAction
    data class SelectGame(val game: FBGameForSchedule) : FBLeagueScheduleAction
    data object UpdateSelectedGame : FBLeagueScheduleAction
    data object UpdateFilteredGames : FBLeagueScheduleAction
}

sealed interface FBLeagueScheduleDelegate {
    data class ShowGameStats(val model: SportDecodableModel.FBGameStats) : FBLeagueScheduleDelegate
}

class FBLeagueScheduleStore @AssistedInject constructor(
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: FBLeagueScheduleDisplayModel,
    @Assisted val emitToParent: (FBLeagueScheduleDelegate) -> Unit
) : BaseScheduleStore<FBLeagueScheduleAction, FBLeagueScheduleDisplayModel>(model, nameProvider) {
    private val _filteredGames = MutableStateFlow<Map<Int, List<FBGameForSchedule>>>(emptyMap())
    val filteredGames: StateFlow<Map<Int, List<FBGameForSchedule>>> = _filteredGames

    // FBGameStatsView에서 title 정보에 사용
    // FBGameStatsStore에서 DidRefreshGame을 실행하면, AppViewModel에서 설정됨.
    private val _league = MutableStateFlow<FBLeague?>(null)
    val league: StateFlow<FBLeague?> = _league

    private val _selectedGame = MutableStateFlow<FBGameForSchedule?>(null)
    val selectedGame: StateFlow<FBGameForSchedule?> = _selectedGame

    private val _gameResultOpenedStateList = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val gameResultOpenedStateList: StateFlow<Map<String, Boolean>> = _gameResultOpenedStateList

    @AssistedFactory
    interface Factory {
        fun create(
            model: FBLeagueScheduleDisplayModel,
            emitToParent: (FBLeagueScheduleDelegate) -> Unit
        ) : FBLeagueScheduleStore
    }

    override fun send(action: FBLeagueScheduleAction) {
        when (action) {
            is FBLeagueScheduleAction.InitData -> initData()
            is FBLeagueScheduleAction.SelectYearMonth -> selectYearMonth(action.yearMonth, action.selectedIndex)
            is FBLeagueScheduleAction.SelectDay -> selectDay(action.day, action.selectedIndex)
            is FBLeagueScheduleAction.ToggleAllResult -> toggleAllResult()
            is FBLeagueScheduleAction.UpdateResultOpenedState -> updateResultOpenedState(action.gameId, action.isOpened)
            is FBLeagueScheduleAction.SelectGame -> selectGame(action.game)
            is FBLeagueScheduleAction.UpdateSelectedGame -> updateSelectedGame()
            is FBLeagueScheduleAction.UpdateFilteredGames -> updateFilteredGames()
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData() {
        super.initData()

        // init with default value
        _filteredGames.value = emptyMap()
        _gameResultOpenedStateList.value = emptyMap()

        // init data
        _yearMonthList.value = displayModel.value.yearMonthList

        when (displayModel.value.scheduleType) {
            ScheduleType.LEAGUE -> {
                displayModel.value.games.firstOrNull()?.date?.let {
                    setDefaultYearMonth(it)
                }

                setDays(true)
            }
            ScheduleType.TEAM -> {
                val upcomingGame = displayModel.value.games.firstOrNull { game ->
                    CalendarUtil.isUpcomingDay(game.date)
                }

                if (upcomingGame != null) {
                    setDefaultYearMonth(upcomingGame.date)
                } else {
                    displayModel.value.games.lastOrNull()?.date?.let {
                        setDefaultYearMonth(it)
                    }
                }

                setDays(true)
            }
            else -> {}
        }
    }

    private fun selectYearMonth(yearMonth: String, selectedIndex: Int) {
        _selectedYearMonth.value = yearMonth
        _selectedYearMonthIndex.value = selectedIndex

        when (displayModel.value.scheduleType) {
            ScheduleType.LEAGUE -> { fetchGames() }
            ScheduleType.TEAM -> { setDays() }
            else -> {}
        }
    }

    override fun toggleAllResult() {
        val newState = !isAllResultOpened.value
        _isAllResultOpened.value = newState
        _gameResultOpenedStateList.value = gameResultOpenedStateList.value.mapValues { newState }
    }

    override fun setDays(isInit: Boolean) {
        // set filtered games to each day
        val yearMonth = selectedYearMonth.value.split("/")
        val year = ("20" + (yearMonth.firstOrNull() ?: "25")).toInt()
        val month = yearMonth.lastOrNull()?.toInt()

        month?.let {
            var days = CalendarUtil.getDaysInMonth(year, it)

            val isResultOpenedStateList = emptyMap<String, Boolean>().toMutableMap()
            val newFilteredGames = filteredGames.value.toMutableMap()

            days = days.mapIndexed { index, day ->
                val games = displayModel.value.games.filter { game ->
                    CalendarUtil.isSameDate(game.date, selectedYearMonth.value, day.day)
                }

                isResultOpenedStateList.putAll(
                    (games).associate { it.gameId to isAllResultOpened.value })

                newFilteredGames[index] = games

                if (games.isEmpty()) {
                    day.isDataEmpty = true
                }

                day
            }

            // ui operation order
            // 1. Set default 'isOpened' value as false to every games, before 'filteredGames' show.
            _gameResultOpenedStateList.value = isResultOpenedStateList

            // 2. Set days to days calendar.
            _days.value = days

            // 3. Move bar and scroll the days calendar.
            if (isInit) {
                // select default day
                val defaultDay = CalendarUtil.getDefaultDay(selectedYearMonth.value, days)
                defaultDay?.let {
                    _selectedDay.value = defaultDay.second
                    _selectedDayIndex.value = defaultDay.first
                    _dayCalendarScrollTrigger.value = UUID.randomUUID().toString()
                }
            } else {
                // select first day that has games
                for ((index, day) in days.withIndex()) {
                    if (!day.isDataEmpty) {
                        _selectedDay.value = day
                        _selectedDayIndex.value = index
                        _dayCalendarScrollTrigger.value = UUID.randomUUID().toString()
                        break
                    }
                }
            }

            // 4. Remove loading.
            _displayDataState.value = ApiFetchState.Success

            // 5. Show 'filteredGames'
            _filteredGames.value = newFilteredGames
        } // month?.let

        // added to prevent any gaps
        if (displayDataState.value != ApiFetchState.Success) {
            _displayDataState.value = ApiFetchState.Success
        }
    }

    private fun fetchGames() {
        _displayDataState.value = ApiFetchState.Fetching

        scope.launch {
            try {
                val selectedYearMonth = selectedYearMonth.value.split("/")
                val yearMonth = selectedYearMonth[0] + selectedYearMonth[1]

                val entity = displayModel.value.entityInfo.firstOrNull() ?: EntityInfo(
                    entityId = 39,
                    entityName = "프리미어리그",
                    category = "football",
                    entityType = "league",
                    leagueId = 39
                )

                val result = searchClient.fetchLeagueSchedule(entity, displayModel.value.season, yearMonth)

                if (result.data is SportDecodableModel.FBLeagueSchedule) {
                    val data = result.data
                    _displayModel.value = data.displayModel
                    setDays()
                }
            } catch (e: Exception) {
                _displayDataState.value = ApiFetchState.Error("데이터를 불러오는데 실패하였습니다.")
                Log.e("dsdf", e.localizedMessage ?: "error")
            }
        }
    }

    private fun updateResultOpenedState(gameId: String, isOpened: Boolean) {
        val newMap = gameResultOpenedStateList.value.toMutableMap()
        newMap[gameId] = isOpened
        _gameResultOpenedStateList.value = newMap
    }

    private fun selectGame(game: FBGameForSchedule) {
        _selectedGame.value = game
        _filteredGames.update { currentMap ->
            currentMap.toMutableMap().apply {
                this[selectedDayIndex.value] = listOf(game)
            }
        }

        scope.launch {
            val result = searchClient.fetchById(
                season = displayModel.value.season,
                category = "football",
                date = game.date,
                dataType = "football_game_stats",
                leagueId = displayModel.value.leagueId,
                id = game.gameId
            )

            if (result.data is SportDecodableModel.FBGameStats) {
                emitToParent(FBLeagueScheduleDelegate.ShowGameStats(result.data))
                updateResultOpenedState(game.gameId, true)
            }
        }
    }

    private fun updateSelectedGame() {
        // 바뀐 displayModel에 있는 (update된)game으로 기존 selectedGame을 update한다.
        val game = displayModel.value.games.find { it.gameId == selectedGame.value?.gameId }
        game?.let {
            _filteredGames.update { currentMap ->
                currentMap.toMutableMap().apply {
                    this[selectedDayIndex.value] = listOf(game)
                }
            }
        }
    }

    private fun updateFilteredGames() {
        _filteredGames.update { currentMap ->
            currentMap.toMutableMap().apply {
                this[selectedDayIndex.value] = displayModel.value.games.filter { game ->
                    CalendarUtil.isSameDate(game.date, selectedYearMonth.value, selectedDayIndex.value + 1)
                }
            }
        }

        // 해당 액션은 뒤로왔을때 실행되므로 선택된 게임은 없앤다.
        _selectedGame.value = null
    }

    private fun updateGamesData(
        fbLeagueScheduleData: SportDecodableModel.FBLeagueSchedule,
        fbGameStatsData: SportDecodableModel.FBGameStats,
        updateViewStack: (SportDecodableModel.FBLeagueSchedule) -> Unit
    ) {
//        val game = fbGameStatsData.displayModel.game
//        val newGames = fbLeagueScheduleData.displayModel.games.map {
//            if (it.gameId == fbGameStatsData.displayModel.game.fixture.id.toString()) {
//                ModelConverter.fbGameToGameScheduleConverter(game)
//            } else it
//        }
//
//        val newData = fbLeagueScheduleData
//        newData.displayModel.games = newGames
//        _displayModel.value = newData.displayModel
//
//        val newFilteredGames = filteredGames.value.toMutableMap()
//        newFilteredGames[selectedDayIndex.value] = newData.displayModel.games.filter { game ->
//            CalendarUtil.isSameDate(game.date, selectedYearMonth.value, selectedDayIndex.value + 1)
//        }
//
//        _filteredGames.value = newFilteredGames
//
//        updateViewStack(newData)
    }
}






















