package dev.shounakmulay.devpulse.feature.feed.interactor.feed

import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.shounakmulay.devpulse.core.domain.feed.feed.ExtractInitialsUseCase
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.RssFeedSearchResult
import dev.shounakmulay.devpulse.core.ui.text.AnnotatedStringTextResource
import dev.shounakmulay.devpulse.core.ui.text.SimpleTextResource
import dev.shounakmulay.devpulse.core.ui.text.TextResourceSpan
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model.FeedListItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FeedSearchInteractorTest {
    private val interactor = FeedSearchInteractor(ExtractInitialsUseCase())

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Given unpinned pages before a pinned match When searching pinned feeds Then paging reaches the match`() = runTest {
        val results = listOf(
            result(name = "First", pinned = false),
            result(name = "Second", pinned = false),
            result(name = "Pinned", pinned = true).copy(id = UUID("pinned")),
        )
        val pager = Pager(PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 1, enablePlaceholders = false)) {
            object : PagingSource<Int, RssFeedSearchResult>() {
                override fun getRefreshKey(state: PagingState<Int, RssFeedSearchResult>): Int? = null

                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RssFeedSearchResult> {
                    val index = params.key ?: 0
                    return LoadResult.Page(
                        data = listOf(results[index]),
                        prevKey = if (index > 0) index - 1 else null,
                        nextKey = (index + 1).takeIf { it < results.size },
                    )
                }
            }
        }
        val presenter = object : PagingDataPresenter<FeedListItem>(StandardTestDispatcher(testScheduler)) {
            override suspend fun presentPagingDataEvent(event: PagingDataEvent<FeedListItem>) = Unit
        }
        val collection = launch {
            interactor.getUIFeedSearchFlow(pager.flow, FeedListSource.Pinned).collect { pages ->
                presenter.collectFrom(pages.withEmptyItem(FeedListSource.Pinned, "Kotlin"))
            }
        }

        advanceUntilIdle()

        assertEquals(listOf(UUID("pinned")), presenter.snapshot().items.map { (it as FeedListItem.Feed).feed.id })
        collection.cancel()
    }

    @Test
    fun `Given matching pinned and unpinned feeds When searching each tab Then only pinned tab filters results`() = runTest {
        val pinned = result(name = "Pinned Kotlin", pinned = true).copy(id = UUID("pinned"))
        val unpinned = result(name = "Kotlin", pinned = false).copy(id = UUID("unpinned"))
        val presenter = object : PagingDataPresenter<FeedListItem>(StandardTestDispatcher(testScheduler)) {
            override suspend fun presentPagingDataEvent(event: PagingDataEvent<FeedListItem>) = Unit
        }

        for (source in listOf(FeedListSource.All, FeedListSource.Pinned)) {
            val pages = interactor.getUIFeedSearchFlow(flowOf(PagingData.from(listOf(pinned, unpinned))), source).first()
            presenter.collectFrom(pages.withEmptyItem(source, "Kotlin"))
            val ids = presenter.snapshot().items.map { (it as FeedListItem.Feed).feed.id }
            val expected = if (source == FeedListSource.All) listOf(UUID("pinned"), UUID("unpinned")) else listOf(UUID("pinned"))
            assertEquals(expected, ids)
        }
    }

    @Test
    fun `Given only an unpinned match When searching pinned feeds Then search is empty`() = runTest {
        val presenter = object : PagingDataPresenter<FeedListItem>(StandardTestDispatcher(testScheduler)) {
            override suspend fun presentPagingDataEvent(event: PagingDataEvent<FeedListItem>) = Unit
        }
        val pages = interactor.getUIFeedSearchFlow(
            flowOf(PagingData.from(listOf(result(name = "Kotlin", pinned = false)))),
            FeedListSource.Pinned,
        ).first()

        presenter.collectFrom(pages.withEmptyItem(FeedListSource.Pinned, "Kotlin"))

        assertEquals(listOf(FeedListItem.Empty.Search), presenter.snapshot().items)
    }

    @Test
    fun `Given named pinned search result When mapping Then feed keeps its display name and actions`() {
        val feed = interactor.toUIFeed(result(name = "My Kotlin", pinned = true))

        assertEquals("My Kotlin", feed.title)
        assertEquals("MK", feed.initials)
        assertEquals(true, feed.pinned)
        assertEquals(UUID("feed-id"), feed.id)
        assertEquals("https://example.com/rss", feed.sourceUrl)
        assertEquals(SimpleTextResource("My Kotlin"), feed.searchHighlights?.highlightedName)
        assertEquals(SimpleTextResource("A feed description"), feed.searchHighlights?.highlightedDescription)
    }

    @Test
    fun `Given unnamed search result When mapping Then title and its highlight remain visible`() {
        val feed = interactor.toUIFeed(result(name = "", pinned = false))

        assertEquals("Kotlin Weekly", feed.title)
        assertEquals("KW", feed.initials)
        assertNull(feed.searchHighlights?.highlightedName)
        assertEquals(SimpleTextResource("Kotlin Weekly"), feed.searchHighlights?.highlightedTitle)
    }

    @Test
    fun `Given marked search title When mapping Then visible title and bold range exclude markers`() {
        val feed = interactor.toUIFeed(
            result(name = "", pinned = false).copy(highlightedTitle = "\uE000Kotlin\uE001 Weekly")
        )

        assertEquals(
            AnnotatedStringTextResource(
                text = "Kotlin Weekly",
                spans = listOf(TextResourceSpan(start = 0, end = 6, fontWeight = 700)),
            ),
            feed.searchHighlights?.highlightedTitle,
        )
    }

    private fun result(name: String, pinned: Boolean) = RssFeedSearchResult(
        id = UUID("feed-id"),
        title = "Kotlin Weekly",
        name = name,
        image = null,
        link = "https://example.com",
        sourceUrl = "https://example.com/rss",
        pinned = pinned,
        highlightedTitle = "Kotlin Weekly",
        highlightedName = name,
        highlightedDescription = "A feed description",
    )
}
