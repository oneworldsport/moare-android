package com.moare.android

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.CalendarUtil
import com.moare.android.core.util.OutputTimeFormatType
import com.moare.android.features.search.display.nba.store.NBALeagueScheduleAction
import com.moare.android.features.search.display.nba.store.NBALeagueScheduleDelegate
import com.moare.android.features.search.display.nba.store.NBALeagueScheduleStore
import com.moare.android.features.search.domain.repository.SearchRepository
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.DataModel
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBALeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.nba.NBAGameForSchedule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NBALeagueScheduleStoreTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val searchRepository: SearchRepository = mockk(relaxed = true)
    private val nameProvider: TranslatedNameProvider = mockk(relaxed = true)

    @Test
    fun `initData league type sets default yearMonth from first game date`() = runTest {
        val displayModel = makeNBALeagueScheduleDisplayModel()
        val firstGame = displayModel.games.first()

        val defaultYearMonth = CalendarUtil.formatDate(
            firstGame.date,
            outputFormatType = OutputTimeFormatType.YEAR_MONTH
        )

        val defaultYearMonthIndex = displayModel.yearMonthList.indexOf(defaultYearMonth)

        val store = makeStore(displayModel = displayModel)

        store.send(NBALeagueScheduleAction.InitData)
        advanceUntilIdle()

//        assertEquals(emptyMap<Int, List<NBAGameForSchedule>>(), store.filteredGames.value)
//        assertEquals(emptyMap<String, Boolean>(), store.gameResultOpenedStateList.value)

        assertEquals(displayModel.yearMonthList, store.yearMonthList.value)
        assertEquals(defaultYearMonth, store.selectedYearMonth.value)
        assertEquals(defaultYearMonthIndex, store.selectedYearMonthIndex.value)
        assertEquals(
            defaultYearMonth.split("/").last().toInt(),
            store.selectedMonth.value
        )
    }

    @Test
    fun `setDays sets days filteredGames and selected day for selected month`() = runTest {
        val displayModel = makeNBALeagueScheduleDisplayModel()
        val firstGame = displayModel.games.first()

        val selectedYearMonth = CalendarUtil.formatDate(
            firstGame.date,
            outputFormatType = OutputTimeFormatType.YEAR_MONTH
        )

        val selectedYearMonthIndex = displayModel.yearMonthList.indexOf(selectedYearMonth)

        val store = makeStore(displayModel = displayModel)

        store.selectYearMonth(
            yearMonth = selectedYearMonth,
            selectedIndex = selectedYearMonthIndex,
            isInit = true // store.setDays() 호출
        )
        advanceUntilIdle()

        val split = selectedYearMonth.split("/")
        val year = ("20" + split[0]).toInt()
        val month = split[1].toInt()

        val expectedDays = CalendarUtil.getDaysInMonth(year, month).toMutableList()
        val expectedFilteredGames = mutableMapOf<Int, List<NBAGameForSchedule>>()
        val expectedGameResultOpenedStateList = mutableMapOf<String, Boolean>()

        expectedDays.forEachIndexed { index, day ->
            val games = displayModel.games.filter { game ->
                CalendarUtil.isSameDate(
                    game.date,
                    selectedYearMonth,
                    day.day
                )
            }

            expectedFilteredGames[index] = games

            if (games.isEmpty()) {
                day.isDataEmpty = true
            }

            games.forEach { game ->
                expectedGameResultOpenedStateList[game.itemKey] = false
            }
        }

        val defaultDay = CalendarUtil.getDefaultDay(
            yearMonth = selectedYearMonth,
            dayList = expectedDays
        ) ?: error("default day가 없음")

        assertEquals(expectedGameResultOpenedStateList, store.gameResultOpenedStateList.value)
        assertEquals(expectedDays, store.days.value)
        assertEquals(defaultDay.second, store.selectedDay.value)
        assertEquals(defaultDay.first, store.selectedDayIndex.value)
        assertEquals(expectedFilteredGames, store.filteredGames.value)
        assertEquals(ApiFetchState.Success, store.displayDataState.value)
    }

    @Test
    fun `updateResultOpenedState updates opened state for game itemKey`() = runTest {
        val displayModel = makeNBALeagueScheduleDisplayModel()
        val game = displayModel.games.first()

        val store = makeStore(displayModel = displayModel)

        store.send(
            NBALeagueScheduleAction.UpdateResultOpenedState(
                itemKey = game.itemKey,
                isOpened = true
            )
        )
        advanceUntilIdle()

        assertEquals(
            mapOf(game.itemKey to true),
            store.gameResultOpenedStateList.value
        )
    }

    @Test
    fun `toggleAllResult toggles all game result opened states`() = runTest {
        val displayModel = makeNBALeagueScheduleDisplayModel()
        val games = displayModel.games.take(2)

        val store = makeStore(displayModel = displayModel)

        games.forEach { game ->
            store.send(
                NBALeagueScheduleAction.UpdateResultOpenedState(
                    itemKey = game.itemKey,
                    isOpened = false
                )
            )
        }
        advanceUntilIdle()

        store.send(NBALeagueScheduleAction.ToggleAllResult)
        advanceUntilIdle()

        val expected = games.associate { game ->
            game.itemKey to true
        }

        assertEquals(true, store.isAllResultOpened.value)
        assertEquals(expected, store.gameResultOpenedStateList.value)
    }

    @Test
    fun `selectGame success emits gameStats delegate and sets opened state true`() = runTest {
        val displayModel = makeNBALeagueScheduleDisplayModel()
        val game = displayModel.games.first()

        val gameStatsModel = mockk<SportDecodableModel.NBAGameStats>(relaxed = true)
        val dataModel = mockk<DataModel>()

        every { dataModel.data } returns gameStatsModel
        coEvery {
            searchRepository.fetchById(
                season = displayModel.season,
                category = "basketball",
                date = game.date,
                dataType = "basketball_game_stats",
                leagueId = displayModel.leagueId,
                id = game.gameId
            )
        } returns dataModel

        val delegates = mutableListOf<NBALeagueScheduleDelegate>()
        val store = makeStore(
            displayModel = displayModel,
            emitToParent = { delegates += it }
        )

        store.send(
            NBALeagueScheduleAction.SelectGame(game)
        )
        advanceUntilIdle()

        assertEquals(1, delegates.size)

        val delegate = delegates.single() as NBALeagueScheduleDelegate.ShowGameStats
        assertSame(gameStatsModel, delegate.model)

        assertEquals(
            true,
            store.gameResultOpenedStateList.value[game.itemKey]
        )

        coVerify(exactly = 1) {
            searchRepository.fetchById(
                season = displayModel.season,
                category = "basketball",
                date = game.date,
                dataType = "basketball_game_stats",
                leagueId = displayModel.leagueId,
                id = game.gameId
            )
        }
    }

    private fun makeStore(
        displayModel: NBALeagueScheduleDisplayModel,
        emitToParent: (NBALeagueScheduleDelegate) -> Unit = {}
    ): NBALeagueScheduleStore {
        every {
            nameProvider.getDictionary(any())
        } returns emptyMap()

        return NBALeagueScheduleStore(
            searchRepository = searchRepository,
            nameProvider = nameProvider,
            model = displayModel,
            emitToParent = emitToParent
        )
    }

    private fun makeNBALeagueScheduleDisplayModel(
        fileName: String = "nba_league_schedule.json"
    ): NBALeagueScheduleDisplayModel {
//        val inputStream = context.assets.open(filePath)
//        val jsonContent = inputStream.bufferedReader().use { it.readText() }
//
//        return DataModel.fromJson(jsonContent)

        val json = javaClass.classLoader!! // 여기 확인. 테스트 파일쪽으로 옮기는거
            .getResourceAsStream(fileName)
            .bufferedReader()
            .use { it.readText() }

        val dataModel = DataModel.fromJson(json)

        val model = dataModel.data as? SportDecodableModel.NBALeagueSchedule
            ?: error("mock json이 NBALeagueSchedule 타입이 아님")

        return model.displayModel
    }
}