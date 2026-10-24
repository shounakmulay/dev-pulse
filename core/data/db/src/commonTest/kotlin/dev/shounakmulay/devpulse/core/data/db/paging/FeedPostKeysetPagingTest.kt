package dev.shounakmulay.devpulse.core.data.db.paging

import androidx.paging.PagingSource
import androidx.room3.RoomRawQuery
import dev.shounakmulay.devpulse.core.data.db.dao.FeedPostDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedAndSearch
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostCursor
import dev.shounakmulay.devpulse.core.data.db.query.SqlBinding
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.paging.FeedPostPagingFixtures.row
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostQuery
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostSort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class FeedPostKeysetPagingTest {

    @Test
    fun `Given tied published sort values When initial page loads Then next cursor includes publication and stable id`() = runBlocking {
        val pagingSource = createPagingSource(
            pages = listOf(
                listOf(row("post-c"), row("post-b"))
            )
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        val page =
            assertIs<PagingSource.LoadResult.Page<FeedPostCursor, LocalRssPostWithFeedAndSearch>>(result)
        assertEquals(listOf("post-c", "post-b"), page.data.postIds())
        assertNull(page.prevKey)
        assertEquals(cursor("post-b"), page.nextKey)
    }

    @Test
    fun `Given tied published sort values When append loads after cursor Then cursor row is excluded without gaps`() = runBlocking {
        val pagingSource = createPagingSource(
            pages = listOf(
                listOf(row("post-a"))
            )
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = cursor("post-b"),
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        val page =
            assertIs<PagingSource.LoadResult.Page<FeedPostCursor, LocalRssPostWithFeedAndSearch>>(result)
        assertEquals(listOf("post-a"), page.data.postIds())
        assertNull(page.nextKey)
    }

    @Test
    fun `Given tied published sort values When refresh loads around anchor Then anchor remains stable in page`() = runBlocking {
        val pagingSource = createPagingSource(
            pages = listOf(
                emptyList(),
                listOf(row("post-b"), row("post-a"))
            )
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = cursor("post-b"),
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        val page =
            assertIs<PagingSource.LoadResult.Page<FeedPostCursor, LocalRssPostWithFeedAndSearch>>(result)
        assertEquals(listOf("post-b", "post-a"), page.data.postIds())
        assertEquals(cursor("post-a"), page.nextKey)
    }

    private fun createPagingSource(
        pages: List<List<LocalRssPostWithFeedAndSearch>>
    ): LocalCursorPagingSource<FeedPostCursor, LocalRssPostWithFeedAndSearch> {
        val dataProvider: LocalCursorPagingSourceDataProvider<FeedPostCursor, LocalRssPostWithFeedAndSearch> =
            LocalRssPostWithFeedMetadataPagingDataProvider(
                feedPostDao = FakeFeedPostDao(pages),
                query = LocalFeedPostQuery(sort = LocalFeedPostSort.PublishedNewest)
            )
        return LocalCursorPagingSource(
            dataProvider = dataProvider,
            invalidationEvents = emptyFlow()
        )
    }

    private fun cursor(id: String): FeedPostCursor = FeedPostCursor(
        id = LocalUUID(id),
        sort = LocalFeedPostSort.PublishedNewest,
        sortValue = SqlBinding.LongValue(PublishedAt),
    )

    private fun List<LocalRssPostWithFeedAndSearch>.postIds(): List<String> {
        return map { it.post.id.value }
    }

    private class FakeFeedPostDao(
        pages: List<List<LocalRssPostWithFeedAndSearch>>
    ) : FeedPostDao {

        private val remainingPages = pages.toMutableList()

        override suspend fun getPostPage(query: RoomRawQuery): List<LocalRssPostWithFeedAndSearch> {
            return remainingPages.removeAt(0)
        }

        override suspend fun upsertPost(post: LocalRssContentFeedPost) = unsupported()

        override suspend fun upsertPosts(posts: List<LocalRssContentFeedPost>) = unsupported()

        override suspend fun deletePosts(posts: List<LocalRssContentFeedPost>) = unsupported()

        override suspend fun getPost(id: LocalUUID): LocalRssContentFeedPost? = unsupported()

        override fun observePost(id: LocalUUID): Flow<LocalRssPostWithFeedAndSearch> = emptyFlow()

        override suspend fun getByFingerprints(
            fingerprints: Set<String>
        ): Map<String, LocalRssContentFeedPostIdentitySlice> = unsupported()

        override suspend fun getLatestPostPublishedTimeForFeed(fromUuid: LocalUUID): Long? = unsupported()

        override fun observeRecentPosts(limit: Int): Flow<List<LocalRssPostWithFeedAndSearch>> {
            return emptyFlow()
        }

        private fun unsupported(): Nothing {
            error("Fake DAO only supports feed post raw paging queries")
        }
    }

    private companion object {
        const val PublishedAt = 1_000L
    }
}
