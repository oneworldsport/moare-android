package com.moare.android.features.search.display.tennis.store

import android.util.Log
import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.DayInfo
import com.moare.android.features.search.display.common.store.BaseScheduleStore
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.KeywordInfo
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.tennis.TennisLeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.tennis.TennisGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import com.moare.android.features.search.data.networking.SearchClient
import com.moare.android.features.search.domain.repository.SearchRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface TennisLeagueScheduleAction {
    data object InitData : TennisLeagueScheduleAction
    data class SelectYearMonth(val yearMonth: String, val selectedIndex: Int) : TennisLeagueScheduleAction
    data class SelectDay(val day: DayInfo, val selectedIndex: Int) : TennisLeagueScheduleAction
    data object ToggleAllResult : TennisLeagueScheduleAction
    data class UpdateResultOpenedState(val itemKey: String, val isOpened: Boolean) : TennisLeagueScheduleAction
    data class SelectGame(val game: TennisGameForSchedule) : TennisLeagueScheduleAction
    data class SelectRelatedLeague(val index: Int) : TennisLeagueScheduleAction
    data object UpdateFilteredGames : TennisLeagueScheduleAction
    data object ShowTournament : TennisLeagueScheduleAction
    data object RefreshGames: TennisLeagueScheduleAction

    data class UpdateStateByRefreshGame(val model: SportDecodableModel.TennisGameStats) : TennisLeagueScheduleAction
}

sealed interface TennisLeagueScheduleDelegate {
    data class ShowGameStats(val model: SportDecodableModel.TennisGameStats) : TennisLeagueScheduleDelegate
    data class ShowTournament(val model: SportDecodableModel.TennisTournament) : TennisLeagueScheduleDelegate
}

class TennisLeagueScheduleStore @AssistedInject constructor(
    private val searchRepository: SearchRepository,
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: TennisLeagueScheduleDisplayModel,
    @Assisted val emitToParent: (TennisLeagueScheduleDelegate) -> Unit
) : BaseScheduleStore<TennisLeagueScheduleAction, TennisLeagueScheduleDisplayModel>(model, nameProvider) {
    private val _filteredGames = MutableStateFlow<Map<Int, List<TennisGameForSchedule>>>(emptyMap())
    val filteredGames: StateFlow<Map<Int, List<TennisGameForSchedule>>> = _filteredGames

    private val _gameResultOpenedStateList = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val gameResultOpenedStateList: StateFlow<Map<String, Boolean>> = _gameResultOpenedStateList

    @AssistedFactory
    interface Factory {
        fun create(
            model: TennisLeagueScheduleDisplayModel,
            emitToParent: (TennisLeagueScheduleDelegate) -> Unit
        ): TennisLeagueScheduleStore
    }

    override fun send(action: TennisLeagueScheduleAction) {
        when (action) {
            is TennisLeagueScheduleAction.InitData -> initData()
            is TennisLeagueScheduleAction.SelectYearMonth -> selectYearMonth(action.yearMonth, action.selectedIndex, false)
            is TennisLeagueScheduleAction.SelectDay -> selectDay(action.day, action.selectedIndex)
            is TennisLeagueScheduleAction.ToggleAllResult -> toggleAllResult()
            is TennisLeagueScheduleAction.UpdateResultOpenedState -> updateResultOpenedState(action.itemKey, action.isOpened)
            is TennisLeagueScheduleAction.SelectGame -> selectGame(action.game)
            is TennisLeagueScheduleAction.SelectRelatedLeague -> selectRelatedLeague(action.index)
            is TennisLeagueScheduleAction.UpdateFilteredGames -> updateFilteredGames()
            is TennisLeagueScheduleAction.UpdateStateByRefreshGame -> updateStateByRefreshGame(action.model)
            is TennisLeagueScheduleAction.ShowTournament -> showTournament()
            is TennisLeagueScheduleAction.RefreshGames -> refreshGames()
        }
    }

    override fun initData() {
        super.initData()

        // init with default value
        _filteredGames.value = emptyMap()
        _gameResultOpenedStateList.value = emptyMap()

        // init data
        _yearMonthList.value = displayModel.value.yearMonthList

        //
        val selectedIndex: Int? = displayModel.value.sortedRelatedLeagues?.indexOf(displayModel.value.leagueId)

        _selectedRelatedLeagueIndex.value = selectedIndex ?: 0

        when (displayModel.value.scheduleType) {
            ScheduleType.LEAGUE -> {
                displayModel.value.games.firstOrNull()?.date?.let {
                    setDefaultYearMonth(it)
                }
            }
            else -> {}
        }
    }

    override fun selectYearMonth(yearMonth: String, selectedIndex: Int, isInit: Boolean) {
        super.selectYearMonth(yearMonth, selectedIndex, isInit)

        if (isInit) {
            setDays(true)
        } else {
            when (displayModel.value.scheduleType) {
                ScheduleType.LEAGUE -> { fetchGames() }
                else -> {}
            }
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

            days = days.mapIndexedNotNull { index, day ->
                val games = displayModel.value.games.filter { game ->
                    CalendarUtil.isSameDate(game.date, selectedYearMonth.value, day.day)
                }

                isResultOpenedStateList.putAll(
                    (games).associate { (it.itemKey) to isAllResultOpened.value })

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
                    entityId = Constants.Ids.AUS_OPEN_M_SINGLE,
                    entityName = "호주오픈",
                    category = "tennis",
                    entityType = "league",
                    leagueId = Constants.Ids.AUS_OPEN_M_SINGLE
                )

                val result = searchRepository.fetchLeagueSchedule(entity, displayModel.value.season, yearMonth)

                if (result.data is SportDecodableModel.TennisLeagueSchedule) {
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

    private fun updateResultOpenedState(itemKey: String, isOpened: Boolean) {
        _gameResultOpenedStateList.update { currentMap ->
            currentMap.toMutableMap().apply {
                this[itemKey] = isOpened
            }
        }
    }

    private fun selectGame(game: TennisGameForSchedule) {
        scope.launch {
            val displayModel = displayModel.value

            val result = searchRepository.fetchById(
                season = displayModel.season,
                category = "tennis",
                date = game.date,
                dataType = "tennis_game_stats",
                leagueId = displayModel.leagueId,
                id = game.gameId
            )

            if (result.data is SportDecodableModel.TennisGameStats) {
                val updated = result.data
                updated.displayModel.leagueKrName = displayModel.relatedLeagueKrname.getOrNull(selectedRelatedLeagueIndex.value) ?: ""
                updated.displayModel.roundName = game.gameInfo?.roundInfo?.name ?: ""


                emitToParent(TennisLeagueScheduleDelegate.ShowGameStats(result.data))
                updateResultOpenedState(game.itemKey, true)
            }
        }
    }

    override fun selectRelatedLeague(index: Int) {
        super.selectRelatedLeague(index)

        scope.launch {
            val displayModel = displayModel.value
            displayModel.sortedRelatedLeagues?.getOrNull(index)?.let { leagueId ->
                _displayDataState.value = ApiFetchState.Fetching

                try {
                    val leagueName = StringConstants.Tennis.leagueNameStr(leagueId = leagueId)
                    val entity = EntityInfo(
                        entityId = leagueId,
                        entityName = leagueName,
                        category = "tennis",
                        entityType = "league",
                        leagueId = leagueId
                    )

                    val result = searchRepository.fetchLeagueSchedule(entity, displayModel.season, null)

                    if (result.data is SportDecodableModel.TennisLeagueSchedule) {
                        val data = result.data
                        _displayModel.value = data.displayModel
                        initData()
                    }
                } catch (e: Exception) {
                    _displayDataState.value = ApiFetchState.Error("데이터를 불러오는데 실패하였습니다.")
                    Log.e("dsdf", e.localizedMessage ?: "error")
                }
            }
        }
    }

    private fun showTournament() {
        scope.launch {
            val leagueId = displayModel.value.leagueId
            val leagueName = StringConstants.Tennis.leagueNameStr(leagueId)

            val keywordInfo = KeywordInfo(
                keyword = "$leagueName 대진표",
                weight = 100,
                keywords = listOf(Keyword(keyword = "대진표", id = "tournament", priority = 2)),
                entities = listOf(
                    EntityInfo(
                        entityId = leagueId,
                        entityName = leagueName,
                        category = "tennis",
                        entityType = "league",
                        leagueId = leagueId
                    )
                )
            )

            val result = searchRepository.fetchDataByKeyword(keywordInfo, displayModel.value.season)

            if (result.data is SportDecodableModel.TennisTournament) {
                emitToParent(TennisLeagueScheduleDelegate.ShowTournament(result.data))
            }
        }
    }

    private fun refreshGames() {
        val games = filteredGames.value[selectedDayIndex.value] ?: return

        scope.launch {
            _isRefreshing.value = true

            try {
                val splittedYearMonth = selectedYearMonth.value.split("/")
                val yearMonth = splittedYearMonth[0] + splittedYearMonth[1]

                val entity = displayModel.value.entityInfo.firstOrNull() ?: EntityInfo(
                    entityId = Constants.Ids.AUS_OPEN_M_SINGLE,
                    entityName = "호주오픈",
                    category = "tennis",
                    entityType = "league",
                    leagueId = Constants.Ids.AUS_OPEN_M_SINGLE
                )

                val hasLive = games.any { game ->
                    (game.gameStatus.toIntOrNull() ?: 0) in Constants.GameStatus.Tennis.LIVE_LIST
                }

                if (hasLive) {
                    val result = searchRepository.fetchLeagueSchedule(
                        entity,
                        displayModel.value.season,
                        yearMonth,
                        selectedDay.value?.day
                    )

                    if (result.data is SportDecodableModel.TennisLeagueSchedule) {
                        val newGames = result.data.displayModel.games
                        val gamesByItemKey = newGames.associateBy { it.itemKey }

                        _displayModel.update { current ->
                            current.copy(
                                games = current.games.map { gamesByItemKey[it.itemKey] ?: it }
                            )
                        }

                        updateFilteredGames()
                    }
                }
            } catch (e: Exception) {
                _displayDataState.value = ApiFetchState.Error("데이터를 불러오는데 실패하였습니다.")
                Log.e("dsdf", e.localizedMessage ?: "error")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun updateFilteredGames() {
        if (displayModel.value.scheduleType == ScheduleType.TEAM_FLAT) {
            _filteredGames.update { currentMap ->
                currentMap.toMutableMap().apply {
                    this[0] = displayModel.value.games
                }
            }
        } else {
            _filteredGames.update { currentMap ->
                currentMap.toMutableMap().apply {
                    this[selectedDayIndex.value] = displayModel.value.games.filter { game ->
                        CalendarUtil.isSameDate(game.date, selectedYearMonth.value, selectedDayIndex.value + 1)
                    }
                }
            }
        }
    }

    private fun updateStateByRefreshGame(model: SportDecodableModel.TennisGameStats) {
//        _displayModel.value = ModelConverter.tennisGameDisplayToLeagueScheduleDisplayConverter(
//            gameStatsDisplayModel = model.displayModel,
//            leagueScheduleDisplayModel = displayModel.value
//        )
    }
}