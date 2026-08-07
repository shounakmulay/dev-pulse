package dev.shounakmulay.devpulse.core.data.db.paging

import androidx.paging.PagingSource
import androidx.room3.RoomRawQuery
import dev.shounakmulay.devpulse.core.data.db.dao.FeedPostDao
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostCategory
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssPostWithFeedMetadataProjection
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssContentFeedPostIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.model.feed.slices.LocalRssFeedIdentitySlice
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostCursor
import dev.shounakmulay.devpulse.core.data.db.query.FeedPostCursorValue
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostQuery
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostSort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class FeedPostKeysetPagingTest {

    @Test
    fun `Given tied published sort values When initial page loads Then next cursor includes full named sort key`() = runBlocking {
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
            assertIs<PagingSource.LoadResult.Page<FeedPostCursor, LocalRssPostWithFeedMetadataProjection>>(result)
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
            assertIs<PagingSource.LoadResult.Page<FeedPostCursor, LocalRssPostWithFeedMetadataProjection>>(result)
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
            assertIs<PagingSource.LoadResult.Page<FeedPostCursor, LocalRssPostWithFeedMetadataProjection>>(result)
        assertEquals(listOf("post-b", "post-a"), page.data.postIds())
        assertEquals(cursor("post-a"), page.nextKey)
    }

    private fun createPagingSource(
        pages: List<List<LocalRssPostWithFeedMetadataProjection>>
    ): LocalCursorPagingSource<FeedPostCursor, LocalRssPostWithFeedMetadataProjection> {
        val dataProvider: LocalCursorPagingSourceDataProvider<FeedPostCursor, LocalRssPostWithFeedMetadataProjection> =
            LocalRssPostWithFeedMetadataPagingDataProvider(
                feedPostDao = FakeFeedPostDao(pages),
                query = LocalFeedPostQuery(sort = LocalFeedPostSort.PublishedNewest)
            )
        return LocalCursorPagingSource(
            dataProvider = dataProvider,
            invalidationEvents = MutableSharedFlow(),
            invalidationScope = CoroutineScope(EmptyCoroutineContext)
        )
    }

    private fun cursor(id: String): FeedPostCursor {
        return FeedPostCursor(
            sort = LocalFeedPostSort.PublishedNewest,
            values = listOf(
                FeedPostCursorValue.LongValue(PublishedAt),
                FeedPostCursorValue.LongValue(UpdatedAt),
                FeedPostCursorValue.TextValue(id)
            )
        )
    }

    private fun row(id: String): LocalRssPostWithFeedMetadataProjection {
        return LocalRssPostWithFeedMetadataProjection(
            post = LocalRssContentFeedPost(
                id = id,
                feedId = FeedId,
                fingerprint = "$id-fingerprint",
                guid = "$id-guid",
                title = "Tied title",
                author = "Tied author",
                link = "https://example.com/posts/$id",
                pubDate = null,
                publishedAtEpochMillis = PublishedAt,
                description = "Description $id",
                content = "Content $id",
                image = null,
                audio = null,
                video = null,
                sourceName = FeedName,
                sourceUrl = "https://example.com/feed.xml",
                categories = "",
                commentsUrl = null,
                bookmarked = false,
                youtubeData = null,
                rawEnclosure = null,
                rawMedia = null,
                createdAt = CreatedAt,
                updatedAt = UpdatedAt
            ),
            feed = LocalRssFeedIdentitySlice(
                id = FeedId,
                title = FeedName,
                name = FeedName,
                pinned = false,
                sourceUrl = "https://example.com/feed.xml",
                link = "https://example.com",
                createdAt = CreatedAt,
                updatedAt = UpdatedAt
            )
        )
    }

    private fun List<LocalRssPostWithFeedMetadataProjection>.postIds(): List<String> {
        return map { it.post.id }
    }

    private class FakeFeedPostDao(
        pages: List<List<LocalRssPostWithFeedMetadataProjection>>
    ) : FeedPostDao {

        private val remainingPages = pages.toMutableList()

        override suspend fun getPostPage(query: RoomRawQuery): List<LocalRssPostWithFeedMetadataProjection> {
            return remainingPages.removeAt(0)
        }

        override suspend fun upsertPost(post: LocalRssContentFeedPost) = unsupported()

        override suspend fun upsertPosts(posts: List<LocalRssContentFeedPost>) = unsupported()

        override suspend fun upsertPostCategories(categories: List<LocalRssPostCategory>) = unsupported()

        override suspend fun deletePostCategories(postId: String) = unsupported()

        override suspend fun deletePostCategories(postIds: Set<String>) = unsupported()

        override suspend fun deletePosts(posts: List<LocalRssContentFeedPost>) = unsupported()

        override suspend fun getPost(id: String): LocalRssContentFeedPost = unsupported()

        override suspend fun updateBookmarkStatus(id: String, isBookmarked: Boolean) = unsupported()

        override suspend fun getByFingerprints(
            fingerprints: Set<String>
        ): Map<String, LocalRssContentFeedPostIdentitySlice> = unsupported()

        override suspend fun getPostsForFeed(feedId: String): List<LocalRssContentFeedPost> = unsupported()

        override fun observeRecentPosts(limit: Int): Flow<List<LocalRssPostWithFeedMetadataProjection>> {
            return emptyFlow()
        }

        override suspend fun getInitialPage(limit: Int): List<LocalRssContentFeedPost> = unsupported()

        override suspend fun getPageAfter(
            id: String,
            limit: Int
        ): List<LocalRssContentFeedPost> = unsupported()

        override suspend fun getRefreshPageAround(
            id: String,
            limit: Int
        ): List<LocalRssContentFeedPost> = unsupported()

        override suspend fun getPageBeforeQuery(
            id: String,
            limit: Int
        ): List<LocalRssContentFeedPost> = unsupported()

        private fun unsupported(): Nothing {
            error("Fake DAO only supports feed post raw paging queries")
        }
    }

    private companion object {
        const val FeedId = "feed-a"
        const val FeedName = "Feed A"
        const val PublishedAt = 1_000L
        const val UpdatedAt = 500L
        const val CreatedAt = 100L
    }
}
