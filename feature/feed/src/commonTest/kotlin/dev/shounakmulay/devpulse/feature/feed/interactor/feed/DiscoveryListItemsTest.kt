package dev.shounakmulay.devpulse.feature.feed.interactor.feed

import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.feed.FeedDiscoveryQuery
import dev.shounakmulay.devpulse.feature.feed.components.discovery.DiscoveryListItem
import dev.shounakmulay.devpulse.feature.feed.model.UIDiscoveryFeed
import dev.shounakmulay.devpulse.feature.feed.preview.FeedPreviewData
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DiscoveryListItemsTest {
    @Test
    fun `Given discovery results When the presented query changes Then empty messages follow the result`() = runTest {
        val presenter = object : PagingDataPresenter<DiscoveryListItem>(StandardTestDispatcher(testScheduler)) {
            override suspend fun presentPagingDataEvent(event: PagingDataEvent<DiscoveryListItem>) = Unit
        }
        val collection = FeedDiscoveryQuery.Collection(UUID("collection"))
        val search = FeedDiscoveryQuery.Search("Kotlin")
        presenter.collectFrom(PagingData.from<UIDiscoveryFeed>(emptyList()).withDiscoveryEmpty(collection))
        assertEquals(listOf(DiscoveryListItem.Empty.Collection), presenter.snapshot().items)
        presenter.collectFrom(PagingData.from<UIDiscoveryFeed>(emptyList()).withDiscoveryEmpty(search))
        assertEquals(listOf(DiscoveryListItem.Empty.Search), presenter.snapshot().items)
        presenter.collectFrom(PagingData.from(listOf(FeedPreviewData.discoveryFeed)).withDiscoveryEmpty(search))
        assertEquals(listOf(DiscoveryListItem.Feed(FeedPreviewData.discoveryFeed)), presenter.snapshot().items)
    }
}
