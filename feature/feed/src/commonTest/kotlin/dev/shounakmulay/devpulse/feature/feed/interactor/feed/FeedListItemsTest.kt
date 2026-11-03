package dev.shounakmulay.devpulse.feature.feed.interactor.feed

import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import dev.shounakmulay.devpulse.feature.feed.model.UIFeed
import dev.shounakmulay.devpulse.feature.feed.preview.FeedPreviewData
import dev.shounakmulay.devpulse.feature.feed.screens.feedlist.ui.model.FeedListItem
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FeedListItemsTest {
    @Test
    fun `Given empty results When the source or query changes Then each presented result has its own empty message`() = runTest {
        val presenter = presenter(StandardTestDispatcher(testScheduler))
        val empty = PagingData.from<UIFeed>(emptyList())

        presenter.collectFrom(empty.withEmptyItem(FeedListSource.All, ""))
        assertEquals(listOf(FeedListItem.Empty.All), presenter.snapshot().items)

        presenter.collectFrom(PagingData.from<UIFeed>(emptyList()).withEmptyItem(FeedListSource.Pinned, ""))
        assertEquals(listOf(FeedListItem.Empty.Pinned), presenter.snapshot().items)

        presenter.collectFrom(PagingData.from<UIFeed>(emptyList()).withEmptyItem(FeedListSource.Pinned, "Kotlin"))
        assertEquals(listOf(FeedListItem.Empty.Search), presenter.snapshot().items)
    }

    @Test
    fun `Given a feed When the results are presented Then no empty message is inserted`() = runTest {
        val presenter = presenter(StandardTestDispatcher(testScheduler))

        presenter.collectFrom(PagingData.from(listOf(FeedPreviewData.feed)).withEmptyItem(FeedListSource.All, ""))

        assertEquals(listOf(FeedListItem.Feed(FeedPreviewData.feed)), presenter.snapshot().items)
    }

    private fun presenter(dispatcher: kotlin.coroutines.CoroutineContext) = object : PagingDataPresenter<FeedListItem>(mainContext = dispatcher) {
        override suspend fun presentPagingDataEvent(event: PagingDataEvent<FeedListItem>) = Unit
    }
}
