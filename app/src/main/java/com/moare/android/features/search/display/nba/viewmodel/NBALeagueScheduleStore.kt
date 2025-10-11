package com.moare.android.features.search.display.nba.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.DayInfo
import com.moare.android.features.search.display.common.viewmodel.BaseScheduleStore
import com.moare.android.features.search.display.football.viewmodel.FBTeamStandingsStore
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBALeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.nba.NBAGameForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import com.moare.android.features.search.networking.SearchClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed interface NBALeagueScheduleAction {
    data object InitData : NBALeagueScheduleAction
    data class SelectYearMonth(val yearMonth: String, val selectedIndex: Int, val updateViewStack: (SportDecodableModel.NBALeagueSchedule) -> Unit) : NBALeagueScheduleAction
    data class SelectDay(val day: DayInfo, val selectedIndex: Int) : NBALeagueScheduleAction
    data object ToggleAllResult : NBALeagueScheduleAction
    data class UpdateResultOpenedState(val gameId: String, val isOpened: Boolean) : NBALeagueScheduleAction
    data class UpdateGamesData(
        val nbaLeagueScheduleData: SportDecodableModel.NBALeagueSchedule,
        val nbaGameStatsData: SportDecodableModel.NBAGameStats,
        val updateViewStack: (SportDecodableModel.NBALeagueSchedule) -> Unit
    ) : NBALeagueScheduleAction
}

class NBALeagueScheduleStore @AssistedInject constructor(
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider,
    @Assisted val initial: NBALeagueScheduleDisplayModel
) : BaseScheduleStore<NBALeagueScheduleAction, NBALeagueScheduleDisplayModel>(initial, nameProvider) {
    private val _filteredGames = MutableStateFlow<Map<Int, List<NBAGameForSchedule>>>(emptyMap())
    val filteredGames: StateFlow<Map<Int, List<NBAGameForSchedule>>> = _filteredGames

    private val _gameResultOpenedStateList = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val gameResultOpenedStateList: StateFlow<Map<String, Boolean>> = _gameResultOpenedStateList

    @AssistedFactory
    interface Factory {
        fun create(displayModel: NBALeagueScheduleDisplayModel) : NBALeagueScheduleStore
    }

    override fun send(action: NBALeagueScheduleAction) {
        when (action) {
            is NBALeagueScheduleAction.InitData -> initData()
            is NBALeagueScheduleAction.SelectYearMonth -> selectYearMonth(action.yearMonth, action.selectedIndex, action.updateViewStack)
            is NBALeagueScheduleAction.SelectDay -> selectDay(action.day, action.selectedIndex)
            is NBALeagueScheduleAction.ToggleAllResult -> toggleAllResult()
            is NBALeagueScheduleAction.UpdateResultOpenedState -> updateResultOpenedState(action.gameId, action.isOpened)
            is NBALeagueScheduleAction.UpdateGamesData -> updateGamesData(action.nbaLeagueScheduleData, action.nbaGameStatsData, action.updateViewStack)
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
            ScheduleType.TEAM_FLAT -> {
                // In TEAM_FLAT type, selectedDayIndex is always 0
                _filteredGames.value = mapOf(0 to displayModel.value.games)

                val gameResultOpenedStateList = displayModel.value.games.associate { (it.gameId) to false }
                _gameResultOpenedStateList.value = gameResultOpenedStateList
            }
        }
    }

    /* ---------------------
       implements
       --------------------- */
    private fun selectYearMonth(yearMonth: String, selectedIndex: Int, updateViewStack: (SportDecodableModel.NBALeagueSchedule) -> Unit) {
        _selectedYearMonth.value = yearMonth
        _selectedYearMonthIndex.value = selectedIndex

        when (displayModel.value.scheduleType) {
            ScheduleType.LEAGUE -> { fetchGames(updateViewStack) }
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

            days = days.mapIndexedNotNull { index, day ->
                val games = displayModel.value.games.filter { game ->
                    CalendarUtil.isSameDate(game.date, selectedYearMonth.value, day.day)
                }

                isResultOpenedStateList.putAll(
                    (games).associate { (it.gameId) to isAllResultOpened.value })

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

    private fun fetchGames(updateViewStack: (SportDecodableModel.NBALeagueSchedule) -> Unit) {
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

                if (result.data is SportDecodableModel.NBALeagueSchedule) {
                    val data = result.data
                    _displayModel.value = data.displayModel
                    updateViewStack(data)
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

    private fun updateGamesData(
        nbaLeagueScheduleData: SportDecodableModel.NBALeagueSchedule,
        nbaGameStatsData: SportDecodableModel.NBAGameStats,
        updateViewStack: (SportDecodableModel.NBALeagueSchedule) -> Unit
    ) {
        val game = nbaGameStatsData.displayModel.game
        val newGames = nbaLeagueScheduleData.displayModel.games.map {
            if (it.gameId == game.gameSummary?.gameCode) ModelConverter.nbaGameToGameScheduleConverter(game) else it
        }

        val newData = nbaLeagueScheduleData
        newData.displayModel.games = newGames
        _displayModel.value = newData.displayModel

        val newFilteredGames = filteredGames.value.toMutableMap()
        newFilteredGames[selectedDayIndex.value] = newData.displayModel.games.filter { game ->
            CalendarUtil.isSameDate(game.date, selectedYearMonth.value, selectedDayIndex.value + 1)
        }

        _filteredGames.value = newFilteredGames

        updateViewStack(newData)
    }
}
































