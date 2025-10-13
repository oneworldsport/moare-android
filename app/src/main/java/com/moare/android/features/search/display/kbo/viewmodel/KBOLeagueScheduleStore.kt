package com.moare.android.features.search.display.kbo.viewmodel

import android.util.Log
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.DayInfo
import com.moare.android.features.search.display.common.store.BaseScheduleStore
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOLeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOGameForSchedule
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

sealed interface KBOLeagueScheduleAction {
    data object InitData : KBOLeagueScheduleAction
    data class SelectYearMonth(val yearMonth: String, val selectedIndex: Int) : KBOLeagueScheduleAction
    data class SelectDay(val day: DayInfo, val selectedIndex: Int) : KBOLeagueScheduleAction
    data object ToggleAllResult : KBOLeagueScheduleAction
    data class UpdateResultOpenedState(val itemKey: String, val isOpened: Boolean) : KBOLeagueScheduleAction // NOTE: 더블헤더가 있는 날에 취소된 경기가 있으면 gameId가 같은 경우가 있어 gameId 대신에 itemKey를 사용
    data class SelectGame(val game: KBOGameForSchedule) : KBOLeagueScheduleAction
    data object UpdateFilteredGames : KBOLeagueScheduleAction

    data class UpdateStateByRefreshGame(val model: SportDecodableModel.KBOGameStats) : KBOLeagueScheduleAction
}

sealed interface KBOLeagueScheduleDelegate {
    data class ShowGameStats(val model: SportDecodableModel.KBOGameStats) : KBOLeagueScheduleDelegate
}

class KBOLeagueScheduleStore @AssistedInject constructor(
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: KBOLeagueScheduleDisplayModel,
    @Assisted val emitToParent: (KBOLeagueScheduleDelegate) -> Unit
) : BaseScheduleStore<KBOLeagueScheduleAction, KBOLeagueScheduleDisplayModel>(initial, nameProvider) {
    private val _filteredGames = MutableStateFlow<Map<Int, List<KBOGameForSchedule>>>(emptyMap())
    val filteredGames: StateFlow<Map<Int, List<KBOGameForSchedule>>> = _filteredGames

    private val _gameResultOpenedStateList = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val gameResultOpenedStateList: StateFlow<Map<String, Boolean>> = _gameResultOpenedStateList

    @AssistedFactory
    interface Factory {
        fun create(
            model: KBOLeagueScheduleDisplayModel,
            emitToParent: (KBOLeagueScheduleDelegate) -> Unit
        ) : KBOLeagueScheduleStore
    }

    override fun send(action: KBOLeagueScheduleAction) {
        when (action) {
            is KBOLeagueScheduleAction.InitData -> initData()
            is KBOLeagueScheduleAction.SelectYearMonth -> selectYearMonth(action.yearMonth, action.selectedIndex)
            is KBOLeagueScheduleAction.SelectDay -> selectDay(action.day, action.selectedIndex)
            is KBOLeagueScheduleAction.ToggleAllResult -> toggleAllResult()
            is KBOLeagueScheduleAction.UpdateResultOpenedState -> updateResultOpenedState(action.itemKey, action.isOpened)
            is KBOLeagueScheduleAction.SelectGame -> selectGame(action.game)
            is KBOLeagueScheduleAction.UpdateFilteredGames -> updateFilteredGames()
            is KBOLeagueScheduleAction.UpdateStateByRefreshGame -> updateStateByRefreshGame(action.model)
        }
    }

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

    /* ---------------------
       implements
       --------------------- */
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
                    entityId = 90001,
                    entityName = "NBA",
                    category = "basketball",
                    entityType = "league",
                    leagueId = 90001
                )

                val result = searchClient.fetchLeagueSchedule(entity, displayModel.value.season, yearMonth)

                if (result.data is SportDecodableModel.KBOLeagueSchedule) {
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
        val newMap = gameResultOpenedStateList.value.toMutableMap()
        newMap[itemKey] = isOpened
        _gameResultOpenedStateList.value = newMap
    }

    private fun selectGame(game: KBOGameForSchedule) {
        scope.launch {
            val result = searchClient.fetchById(
                season = displayModel.value.season,
                category = "baseball",
                date = game.date,
                dataType = "baseball_game_stats",
                leagueId = displayModel.value.leagueId,
                id = game.gameId
            )

            if (result.data is SportDecodableModel.KBOGameStats) {
                emitToParent(KBOLeagueScheduleDelegate.ShowGameStats(result.data))
                updateResultOpenedState(game.itemKey, true)
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

    private fun updateStateByRefreshGame(model: SportDecodableModel.KBOGameStats) {
        _displayModel.value = ModelConverter.kboGameDisplayToLeagueScheduleDisplayConverter(
            gameStatsDisplayModel = model.displayModel,
            leagueScheduleDisplayModel = displayModel.value
        )
    }
}