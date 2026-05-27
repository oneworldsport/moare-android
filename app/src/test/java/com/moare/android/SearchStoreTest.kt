package com.moare.android

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import com.moare.android.features.search.display.search.store.SearchAction
import com.moare.android.features.search.display.search.store.SearchDelegate
import com.moare.android.features.search.display.search.store.SearchStore
import com.moare.android.features.search.domain.repository.AutoCompleteRepository
import com.moare.android.features.search.domain.repository.KeywordsRepository
import com.moare.android.features.search.domain.repository.SearchRepository
import com.moare.android.features.search.domain.repository.TrendingKeywordsRepository
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.DataModel
import com.moare.android.features.search.models.KeywordInfo
import com.moare.android.features.search.models.LeagueKeywords
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.TrendingKeywords
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame

@OptIn(ExperimentalCoroutinesApi::class)
class SearchStoreTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val context: Context = mockk(relaxed = true)
    private val searchRepository: SearchRepository = mockk(relaxed = true)
    private val keywordsRepository: KeywordsRepository = mockk(relaxed = true)
    private val autoCompleteRepository: AutoCompleteRepository = mockk(relaxed = true)
    private val trendingKeywordsRepository: TrendingKeywordsRepository = mockk(relaxed = true)

    @Test
    fun `barFirstOpen sets barFirstOpened true`() = runTest {
        val store = makeStore()

        store.send(SearchAction.BarFirstOpen)
        advanceUntilIdle()

        assertTrue(store.barFirstOpened.value)
    }

    @Test

    fun `updateTextField updates query`() = runTest {
        val store = makeStore()

        store.send(
            SearchAction.UpdateTextField(
                newValue = TextFieldValue("NBA"),
                updateAutoCompleteList = false
            )
        )
        advanceUntilIdle()

        assertEquals("NBA", store.query.value.text)
    }

    @Test
    fun `updateTextField with blank text clears autoCompleteList`() = runTest {
        val store = makeStore()

        coEvery {
            autoCompleteRepository.search("NBA")
        } returns listOf("NBA 일정", "NBA 순위")

        store.send(
            SearchAction.UpdateTextField(
                TextFieldValue("NBA"),
                true
            )
        )
        advanceUntilIdle()

        assertEquals("NBA", store.query.value.text)
        assertEquals(
            listOf("NBA 일정", "NBA 순위"),
            store.autoCompleteList.value
        )
        assertTrue(store.autoCompleteListVisibleState.value)

        store.send(
            SearchAction.UpdateTextField(
                TextFieldValue(""),
                true
            )
        )
        advanceUntilIdle()

        assertEquals("", store.query.value.text)
        assertEquals(emptyList<String>(), store.autoCompleteList.value)
        assertFalse(store.autoCompleteListVisibleState.value)
    }

    @Test
    fun `getLeagueKeywords success updates leagueKeywords after delay`() = runTest {
        val expected = LeagueKeywords(
            live = listOf(
                KeywordInfo(keyword = "NBA", keywords = null, entities = emptyList())
            ),
            recent = listOf(
                KeywordInfo(keyword = "MLB", keywords = null, entities = emptyList())
            )
        )

        coEvery {
            keywordsRepository.fetchLeagueKeywords()
        } returns expected

        val store = makeStore()

        store.send(SearchAction.GetLeagueKeywords)

        advanceTimeBy(2700)
        advanceUntilIdle()

        assertEquals(expected, store.leagueKeyowrds.value)
    }

    @Test
    fun `getLeagueKeywords failure keeps leagueKeywords unchanged`() = runTest {
        coEvery {
            keywordsRepository.fetchLeagueKeywords()
        } throws RuntimeException("failed")

        val store = makeStore()

        assertEquals(null, store.leagueKeyowrds.value)

        store.send(SearchAction.GetLeagueKeywords)
        advanceUntilIdle()

        assertEquals(null, store.leagueKeyowrds.value)
    }

    @Test
    fun `updateTextField updates autoCompleteList`() = runTest {
        coEvery {
            autoCompleteRepository.search("손")
        } returns listOf("손흥민", "손흥민 경기")

        val store = makeStore()

        store.send(
            SearchAction.UpdateTextField(
                TextFieldValue("손"),
                true
            )
        )
        advanceUntilIdle()

        assertEquals("손", store.query.value.text)
        assertEquals(
            listOf("손흥민", "손흥민 경기"),
            store.autoCompleteList.value
        )
        assertTrue(store.autoCompleteListVisibleState.value)

        coVerify(exactly = 1) {
            autoCompleteRepository.search("손")
        }
    }

    @Test
    fun `performSearch query failure updates searchDataState Error`() = runTest {
        coEvery {
            searchRepository.fetchDataByQuery("NBA")
        } throws RuntimeException("failed")

        val store = makeStore()

        store.send(
            SearchAction.UpdateTextField(
                newValue = TextFieldValue("NBA"),
                updateAutoCompleteList = false
            )
        )
        advanceUntilIdle()

        store.send(
            SearchAction.PerformSearch(
                searchType = SearchStore.SearchType.Query,
                aniDuration = 0
            )
        )
        advanceUntilIdle()

        assertTrue(store.searchState.value)
        assertEquals(
            ApiFetchState.Error("검색 결과가 없습니다."),
            store.searchDataState.value
        )

        coVerify(exactly = 1) {
            searchRepository.fetchDataByQuery("NBA")
        }
        coVerify(exactly = 0) {
            searchRepository.fetchDataByKeyword(any(), any())
        }
    }

    @Test
    fun `performSearch query success emits delegate push`() = runTest {
        val sportModel = mockk<SportDecodableModel.NBALeagueSchedule>(relaxed = true)
        val dataModel = mockk<DataModel>()

        every { dataModel.data } returns sportModel
        coEvery {
            searchRepository.fetchDataByQuery("NBA")
        } returns dataModel

        // NOTE: delegates가 List인 이유는 다음 내용들을 확인하기 위해
        // - delegate가 정확히 1번 발생했는지
        // - 여러 번 발생했다면 어떤 순서로 발생했는지
        // - Push 안의 model이 기대한 값인지
        val delegates = mutableListOf<SearchDelegate>()
        val store = makeStore(
            emitToParent = { delegates += it }
        )

        store.send(
            SearchAction.UpdateTextField(
                newValue = TextFieldValue("NBA"),
                updateAutoCompleteList = false
            )
        )
        advanceUntilIdle()

        store.send(
            SearchAction.PerformSearch(
                searchType = SearchStore.SearchType.Query,
                aniDuration = 0
            )
        )
        advanceUntilIdle()

        assertEquals(ApiFetchState.Success, store.searchDataState.value)
        assertTrue(store.resultVisibleState.value)

        assertEquals(1, delegates.size)

        val push = delegates.single() as SearchDelegate.Push
        assertSame(sportModel, push.model)

        coVerify(exactly = 1) {
            searchRepository.fetchDataByQuery("NBA")
        }
        coVerify(exactly = 0) {
            searchRepository.fetchDataByKeyword(any(), any())
        }
    }

    @Test
    fun `performSearch trendingKeywords success emits delegate push`() = runTest {
        val keywordInfo = KeywordInfo(
            keyword = "NBA 일정",
            weight = 100,
            keywords = emptyList(),
            entities = emptyList()
        )

        val sportModel = mockk<SportDecodableModel.NBALeagueSchedule>(relaxed = true)
        val dataModel = mockk<DataModel>()

        every { dataModel.data } returns sportModel
        coEvery {
            trendingKeywordsRepository.keywordInfo("NBA 일정")
        } returns keywordInfo
        coEvery {
            searchRepository.fetchDataByKeyword(keywordInfo,null)
        } returns dataModel

        val delegates = mutableListOf<SearchDelegate>()
        val store = makeStore(
            emitToParent = { delegates += it }
        )

        store.send(
            SearchAction.UpdateTextField(
                newValue = TextFieldValue("NBA 일정"),
                updateAutoCompleteList = false
            )
        )
        advanceUntilIdle()

        store.send(
            SearchAction.PerformSearch(
                searchType = SearchStore.SearchType.TrendingKeyword,
                aniDuration = 0
            )
        )
        advanceUntilIdle()

        assertEquals(ApiFetchState.Success, store.searchDataState.value)
        assertTrue(store.resultVisibleState.value)

        assertEquals(1, delegates.size)

        val push = delegates.single() as SearchDelegate.Push
        assertSame(sportModel, push.model)

        coVerify(exactly = 1) {
            trendingKeywordsRepository.keywordInfo("NBA 일정")
        }
        coVerify(exactly = 1) {
            searchRepository.fetchDataByKeyword(keywordInfo, null)
        }
        coVerify(exactly = 0) {
            searchRepository.fetchDataByQuery(any())
        }
    }

    @Test
    fun `performSearch autoComplete success emits delegate push`() = runTest {
        val keywordInfo = KeywordInfo(
            keyword = "NBA 일정",
            weight = 100,
            keywords = emptyList(),
            entities = emptyList()
        )

        val sportModel = mockk<SportDecodableModel.NBALeagueSchedule>(relaxed = true)
        val dataModel = mockk<DataModel>()

        every { dataModel.data } returns sportModel
        coEvery {
            autoCompleteRepository.keywordInfo("NBA 일정")
        } returns keywordInfo
        coEvery {
            searchRepository.fetchDataByKeyword(keywordInfo,null)
        } returns dataModel

        val delegates = mutableListOf<SearchDelegate>()
        val store = makeStore(
            emitToParent = { delegates += it }
        )

        store.send(
            SearchAction.UpdateTextField(
                newValue = TextFieldValue("NBA 일정"),
                updateAutoCompleteList = false
            )
        )
        advanceUntilIdle()

        store.send(
            SearchAction.PerformSearch(
                searchType = SearchStore.SearchType.AutoComplete,
                aniDuration = 0
            )
        )
        advanceUntilIdle()

        assertEquals(ApiFetchState.Success, store.searchDataState.value)
        assertTrue(store.resultVisibleState.value)

        assertEquals(1, delegates.size)

        val push = delegates.single() as SearchDelegate.Push
        assertSame(sportModel, push.model)

        // PerformSearch AutoComplete 검색 시 request body에서 weight를 제외하려고 null 처리한다.
        assertEquals(null, keywordInfo.weight)

        coVerify(exactly = 1) {
            autoCompleteRepository.keywordInfo("NBA 일정")
        }
        coVerify(exactly = 1) {
            searchRepository.fetchDataByKeyword(keywordInfo, null)
        }
        coVerify(exactly = 0) {
            searchRepository.fetchDataByQuery(any())
        }
    }

    private fun makeStore(
        emitToParent: (SearchDelegate) -> Unit = {}
    ): SearchStore {
        return SearchStore(
            context = context,
            searchRepository = searchRepository,
            keywordsRepository = keywordsRepository,
            autoCompleteRepository = autoCompleteRepository,
            trendingKeywordsRepository = trendingKeywordsRepository,
            noticeDeferred = CompletableDeferred(emptyList()),
            emitToParent = emitToParent
        )
    }
}