package dev.shounakmulay.devpulse.core.data.db

import androidx.paging.PagingSource
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.shounakmulay.devpulse.core.data.db.converter.GzipCompressor
import dev.shounakmulay.devpulse.core.data.db.converter.LocalCompressedTextTypeConverter
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssContentFeedPost
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeed
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssPostContentFts
import dev.shounakmulay.devpulse.core.data.db.paging.LocalRssPostWithFeedMetadataPagingDataProvider
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostFilter
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostQuery
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedPostSort
import java.nio.file.Files
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserStateDatabaseTest {
    @Test
    fun `Given missing parent When setting read status Then no orphan state is created`() = runBlocking {
        withDatabase { db ->
            seed(db)
            val states = db.getPostUserStateDao()

            states.setRead(postId = id("missing"), isRead = true)

            assertNull(states.getState(id("missing")))
        }
    }

    @Test
    fun `Given overlapping read and bookmark writes When completed Then latest read choice and bookmark survive`() = runBlocking {
        withDatabase { db ->
            seed(db)
            val states = db.getPostUserStateDao()
            coroutineScope {
                val read = async { states.setRead(postId = id("p4"), isRead = true) }
                val bookmark = async { states.setBookmarked(postId = id("p4"), bookmarked = true) }
                read.await()
                bookmark.await()
            }
            states.setRead(postId = id("p4"), isRead = false)
            states.setRead(postId = id("p4"), isRead = false)

            val stored = states.getState(id("p4"))!!
            assertFalse(stored.isRead)
            assertTrue(stored.bookmarked)
        }
    }

    @Test
    fun `Given absent state When querying Then defaults and negative filters include untouched articles`() = runBlocking {
        withDatabase { db ->
            seed(db)
            val post = db.getFeedContentDao().observePost(id("p4")).first()
            assertFalse(post.postState.isRead)
            assertFalse(post.postState.bookmarked)
            assertFalse(post.feedState.pinned)
            assertNull(db.getPostUserStateDao().getState(id("p4")))
            assertEquals(listOf("p4", "p3", "p2", "p1"), ids(db, LocalFeedPostFilter.ReadStatus(false)))
            assertEquals(listOf("p4", "p3", "p2", "p1"), ids(db, LocalFeedPostFilter.Bookmarked(false)))
            assertEquals(emptyList(), ids(db, LocalFeedPostFilter.ReadStatus(true)))
        }
    }

    @Test
    fun `Given user state When sync upserts content Then each narrow write preserves other fields`() = runBlocking {
        withDatabase { db ->
            seed(db)
            val state = db.getPostUserStateDao()
            state.setBookmarked(id("p3"), true)
            state.setRead(postId = id("p3"), isRead = true)
            db.getFeedUserStateDao().setPinned(id("feed"), true)
            db.getFeedUserStateDao().setLastOpenedAt(id("feed"), 99)
            db.getFeedContentDao().upsertPost(post("p3", "Updated Kotlin", 10))
            db.getFeedDao().upsertFeed(feed().copy(title = "Updated feed", updatedAt = 10))
            state.setBookmarked(id("p3"), false)
            state.setRead(postId = id("p3"), isRead = true)
            val stored = state.getState(id("p3"))!!
            assertTrue(stored.isRead)
            assertFalse(stored.bookmarked)
            val feedState = db.getFeedUserStateDao().getState(id("feed"))!!
            assertTrue(feedState.pinned)
            assertEquals(99L, feedState.lastOpenedAt)
            assertEquals("Updated Kotlin", db.getFeedContentDao().getPost(id("p3"))!!.title)
            state.setRead(postId = id("p3"), isRead = false)
            assertFalse(state.getState(id("p3"))!!.isRead)
        }
    }

    @Test
    fun `Given tied sort values When paging with state filters Then append prepend and removed anchor refresh retain order`() = runBlocking {
        withDatabase { db ->
            seed(db)
            for (sort in LocalFeedPostSort.entries) {
                val provider = provider(db, setOf(LocalFeedPostFilter.ReadStatus(false)), sort)
                val first = provider.getInitialPage(2)
                val second = provider.getPageAfter(provider.getId(first.last()), 2)
                val expected = when (sort) {
                    LocalFeedPostSort.PublishedNewest, LocalFeedPostSort.TitleZtoA -> listOf("p4", "p3", "p2", "p1")
                    LocalFeedPostSort.PublishedOldest -> listOf("p1", "p2", "p3", "p4")
                    LocalFeedPostSort.TitleAtoZ -> listOf("p2", "p1", "p4", "p3")
                }
                assertEquals(expected, (first + second).map { it.post.id.value })
                assertEquals(first.map { it.post.id }, provider.getPageBefore(provider.getId(second.first()), 2).map { it.post.id })
            }
            val provider = provider(db, setOf(LocalFeedPostFilter.ReadStatus(false)))
            val anchor = provider.getId(provider.getInitialPage(2).last())
            db.getPostUserStateDao().setRead(postId = id("p3"), isRead = true)
            assertEquals(listOf("p4", "p2", "p1"), provider.getRefreshPageAround(anchor, 4).map { it.post.id.value })
        }
    }

    @Test
    fun `Given FTS matches When combining user filters Then highlights and search membership survive state writes`() = runBlocking {
        withDatabase { db ->
            seed(db)
            db.getPostUserStateDao().setBookmarked(id("p3"), true)
            db.getFeedUserStateDao().setPinned(id("feed"), true)
            val filters = setOf(LocalFeedPostFilter.SearchText("Kotlin"), LocalFeedPostFilter.Bookmarked(true), LocalFeedPostFilter.PinnedFeed(true), LocalFeedPostFilter.ReadStatus(false))
            assertEquals(listOf("p3"), provider(db, filters).getInitialPage(10).map { it.post.id.value })
            db.getPostUserStateDao().setRead(postId = id("p3"), isRead = true)
            assertTrue(provider(db, filters).getInitialPage(10).isEmpty())
            assertEquals(listOf("p4", "p3", "p2", "p1"), ids(db, LocalFeedPostFilter.SearchText("Kotlin")))
            db.getPostContentDao().insertPostContentFts(LocalRssPostContentFts(id("p2"), "Rust extraction"))
            db.getPostUserStateDao().setBookmarked(id("p2"), true)
            val articleOnly = provider(db, setOf(LocalFeedPostFilter.SearchText("Rust"), LocalFeedPostFilter.Bookmarked(true))).getInitialPage(10)
            assertEquals(listOf("p2"), articleOnly.map { it.post.id.value })
            assertTrue(articleOnly.single().search!!.highlightedContent!!.contains("Rust"))
        }
    }

    @Test
    fun `Given feed pages and search When pinning Then projections and paging reflect feed user state`() = runBlocking {
        withDatabase { db ->
            seed(db)
            val source = db.getFeedDao().getFeedPagingSource()
            val invalidated = CompletableDeferred<Unit>()
            source.registerInvalidatedCallback { invalidated.complete(Unit) }
            val before = source.load(PagingSource.LoadParams.Refresh(null, 10, false)) as PagingSource.LoadResult.Page
            assertEquals(listOf("feed"), before.data.map { it.feed.id.value })
            assertFalse(before.data.single().state.pinned)
            db.getFeedUserStateDao().setPinned(id("feed"), true)
            withTimeout(5_000) { invalidated.await() }
            val pinned = db.getFeedDao().getPinnedFeedPagingSource().load(PagingSource.LoadParams.Refresh(null, 10, false)) as PagingSource.LoadResult.Page
            assertEquals(listOf("feed"), pinned.data.map { it.feed.id.value })
            assertTrue(db.getFeedDao().observeFeed(id("feed")).first().state.pinned)
            assertTrue(db.getFeedDao().getPinnedAndRecentFeeds(10).first().single().state.pinned)
            val search = db.getFeedDao().searchFeeds("\"Kotlin\"").load(PagingSource.LoadParams.Refresh(null, 10, false)) as PagingSource.LoadResult.Page
            assertEquals(listOf("feed"), search.data.map { it.id.value })
            assertTrue(search.data.single().pinned)
        }
    }

    @Test
    fun `Given active pages When article or feed state changes Then paging invalidates and observers see state`() = runBlocking {
        withDatabase { db ->
            seed(db)
            for (feedChange in listOf(false, true)) {
                val source = db.getFeedPostPagingSource(LocalFeedPostQuery())
                val invalidated = CompletableDeferred<Unit>()
                source.registerInvalidatedCallback { invalidated.complete(Unit) }
                val result = source.load(PagingSource.LoadParams.Refresh(null, 2, false))
                assertTrue(result is PagingSource.LoadResult.Page)
                if (feedChange) db.getFeedUserStateDao().setPinned(id("feed"), true)
                else db.getPostUserStateDao().setRead(postId = id("p4"), isRead = true)
                withTimeout(5_000) { invalidated.await() }
            }
            val observed = db.getFeedContentDao().observeRecentPosts(1).first().single()
            assertTrue(observed.postState.isRead)
            assertTrue(observed.feedState.pinned)
        }
    }

    @Test
    fun `Given a database with a retired version When opening baseline Then missing migration fails without deleting data`() = runBlocking {
        val file = Files.createTempFile("retired-db-version", ".db").toFile()
        try {
            withDatabase(file.absolutePath) { seed(it) }
            BundledSQLiteDriver().open(file.absolutePath).use { connection ->
                connection.prepare("PRAGMA user_version=3").use { it.step() }
            }
            val attempt = runCatching {
                withDatabase(file.absolutePath) { it.getFeedDao().getFeed("feed") }
            }
            assertTrue(attempt.isFailure)
            assertTrue(attempt.exceptionOrNull()!!.message.orEmpty().contains("migration", ignoreCase = true))
            BundledSQLiteDriver().open(file.absolutePath).use { connection ->
                connection.prepare("SELECT COUNT(*) FROM LocalRssContentFeedPost").use { assertTrue(it.step()); assertEquals(4L, it.getLong(0)) }
            }
        } finally {
            file.delete()
        }
    }

    @Test
    fun `Given a fresh baseline When reopening Then state persists at version one and cascades leave no orphans`() = runBlocking {
        val file = Files.createTempFile("user-state-v1", ".db").toFile()
        try {
            withDatabase(file.absolutePath) { db ->
                seed(db)
                db.getPostUserStateDao().setBookmarked(id("p4"), true)
                db.getPostUserStateDao().setRead(postId = id("p4"), isRead = true)
                db.getFeedUserStateDao().setPinned(id("feed"), true)
            }
            withDatabase(file.absolutePath) { db ->
                assertTrue(db.getPostUserStateDao().getState(id("p4"))!!.isRead)
                assertTrue(db.getPostUserStateDao().getState(id("p4"))!!.bookmarked)
                assertTrue(db.getFeedUserStateDao().getState(id("feed"))!!.pinned)
            }
            BundledSQLiteDriver().open(file.absolutePath).use { connection ->
                connection.prepare("PRAGMA table_info(LocalRssPostUserState)").use { statement ->
                    val columns = buildList { while (statement.step()) add(statement.getText(1)) }
                    assertEquals(listOf("postId", "isRead", "bookmarked"), columns)
                }
                connection.prepare("PRAGMA user_version").use { assertTrue(it.step()); assertEquals(1L, it.getLong(0)) }
                connection.prepare("PRAGMA foreign_keys=ON").use { it.step() }
                connection.prepare("DELETE FROM LocalRssFeed WHERE id='feed'").use { it.step() }
                for (table in listOf("LocalRssPostUserState", "LocalRssFeedUserState", "LocalRssContentFeedPost")) {
                    connection.prepare("SELECT COUNT(*) FROM $table").use { assertTrue(it.step()); assertEquals(0L, it.getLong(0)) }
                }
                connection.prepare("PRAGMA foreign_key_check").use { assertFalse(it.step()) }
                connection.prepare("PRAGMA integrity_check").use { assertTrue(it.step()); assertEquals("ok", it.getText(0)) }
            }
        } finally {
            file.delete()
        }
    }

    private fun id(value: String) = LocalUUID(value)

    private fun provider(db: DevPulseDatabase, filters: Set<LocalFeedPostFilter>, sort: LocalFeedPostSort = LocalFeedPostSort.PublishedNewest) =
        LocalRssPostWithFeedMetadataPagingDataProvider(db.getFeedContentDao(), LocalFeedPostQuery(filters = filters, sort = sort))

    private suspend fun ids(db: DevPulseDatabase, filter: LocalFeedPostFilter) = provider(db, setOf(filter)).getInitialPage(20).map { it.post.id.value }

    private suspend fun seed(db: DevPulseDatabase) {
        db.getFeedDao().upsertFeed(feed())
        db.getFeedContentDao().upsertPosts((1..4).map { post("p$it", if (it < 3) "Kotlin A" else "Kotlin B", (it + 1L) / 2) })
    }

    private fun feed() = LocalRssFeed(
        id = id("feed"), sourceUrl = "https://example.com/feed", title = "Kotlin", name = "Kotlin", link = null,
        description = null, image = null, lastBuildDate = null, updatePeriod = null, youtubeChannel = null, createdAt = 1, updatedAt = 1,
    )

    private fun post(value: String, title: String, published: Long) = LocalRssContentFeedPost(
        id = id(value), feedId = id("feed"), fingerprint = value, guid = value, title = title, author = "Author", link = null,
        pubDate = null, publishedAtEpochMillis = published, description = "Kotlin article", content = "Kotlin content", image = null,
        audio = null, video = null, sourceName = "Kotlin", sourceUrl = "https://example.com/feed", categories = "", commentsUrl = null,
        youtubeData = null, rawEnclosure = null, rawMedia = null, createdAt = 1, updatedAt = 1,
    )

    private suspend fun withDatabase(path: String? = null, block: suspend (DevPulseDatabase) -> Unit) {
        val builder = if (path == null) Room.inMemoryDatabaseBuilder<DevPulseDatabase>() else Room.databaseBuilder<DevPulseDatabase>(path)
        val db = builder.setDriver(BundledSQLiteDriver()).addColumnTypeConverter(LocalCompressedTextTypeConverter(GzipCompressor())).build()
        try { block(db) } finally { db.close() }
    }
}
