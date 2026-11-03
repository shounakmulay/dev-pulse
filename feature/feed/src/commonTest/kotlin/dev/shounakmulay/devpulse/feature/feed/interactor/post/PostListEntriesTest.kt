package dev.shounakmulay.devpulse.feature.feed.interactor.post

import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import dev.shounakmulay.devpulse.core.domain.models.common.UUID
import dev.shounakmulay.devpulse.core.domain.models.post.RssPostFilter
import dev.shounakmulay.devpulse.feature.feed.components.postFilterSort.PostListEntry
import dev.shounakmulay.devpulse.feature.feed.model.UIFeedPost
import dev.shounakmulay.devpulse.feature.feed.preview.FeedPreviewData
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PostListEntriesTest {
    @Test
    fun `Given empty results When search and filters change Then presented messages follow each result`() = runTest {
        val presenter = object : PagingDataPresenter<PostListEntry>(StandardTestDispatcher(testScheduler)) {
            override suspend fun presentPagingDataEvent(event: PagingDataEvent<PostListEntry>) = Unit
        }
        val scope = RssPostFilter.FeedIds(setOf(UUID("feed")))
        val cases = listOf(
            listOf(scope) to PostListEntry.Empty(search = false, readStatus = null, filtered = false),
            listOf(scope, RssPostFilter.ReadStatus(false)) to PostListEntry.Empty(search = false, readStatus = false, filtered = false),
            listOf(scope, RssPostFilter.Bookmarked(true)) to PostListEntry.Empty(search = false, readStatus = null, filtered = true),
            listOf(scope, RssPostFilter.SearchText("Kotlin")) to PostListEntry.Empty(search = true, readStatus = null, filtered = false),
        )
        for ((filters, expected) in cases) {
            presenter.collectFrom(PagingData.from<UIFeedPost>(emptyList()).withPostEmptyItem(filters, scopedToFeed = true))
            assertEquals(listOf(expected), presenter.snapshot().items)
        }
        presenter.collectFrom(PagingData.from(listOf(FeedPreviewData.post)).withPostEmptyItem(emptyList(), scopedToFeed = false))
        assertEquals(listOf(PostListEntry.Post(FeedPreviewData.post)), presenter.snapshot().items)
    }
}
