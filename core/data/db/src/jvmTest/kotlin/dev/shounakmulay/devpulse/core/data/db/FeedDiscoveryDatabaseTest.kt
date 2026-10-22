package dev.shounakmulay.devpulse.core.data.db

import androidx.paging.PagingSource
import androidx.room3.Room
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.shounakmulay.devpulse.core.common.text.HL_END
import dev.shounakmulay.devpulse.core.common.text.HL_START
import dev.shounakmulay.devpulse.core.data.db.converter.GzipCompressor
import dev.shounakmulay.devpulse.core.data.db.converter.LocalCompressedTextTypeConverter
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalFeedDiscoveryFeedPage
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.enums.LocalRssFeedQueueActionRequestor
import dev.shounakmulay.devpulse.core.data.db.model.feed.enums.LocalRssFeedQueueActionType
import dev.shounakmulay.devpulse.core.data.db.model.feed.enums.LocalRssFeedQueueStatus
import dev.shounakmulay.devpulse.core.data.db.model.feed.enums.LocalRssFeedType
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryCollection
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryCollectionToFeedMapping
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryFeedFormat
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryFeedMetadata
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalFeedDiscoveryMasterMetadata
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeed
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeedQueue
import dev.shounakmulay.devpulse.core.data.db.query.LocalFeedDiscoveryQuery
import dev.shounakmulay.devpulse.core.data.db.paging.LocalFeedDiscoveryPagingDataProvider
import java.nio.file.Files
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FeedDiscoveryDatabaseTest {
    @Test
    fun `Given populated baseline When reopening Then subscriptions and discovery search survive`() = runBlocking {
        val file = Files.createTempFile("discovery-baseline", ".db").toFile()
        try {
            withDatabase(file.absolutePath) { db ->
                seed(db)
                db.getFeedDao().upsertFeed(LocalRssFeed(
                    id = LocalUUID("subscribed"), sourceUrl = "https://example.com/1",
                    title = "Preserved title", name = "", link = null, description = null,
                    image = null, lastBuildDate = null, updatePeriod = null, youtubeChannel = null,
                    createdAt = 1, updatedAt = 1,
                ))
                db.getFeedUserStateDao().setPinned(LocalUUID("subscribed"), true)
                assertEquals(listOf("feed-1"), search(db, "\"Kotlin\"").getInitialPage(10).map { it.feed.metadata.id.value })
            }
            withDatabase(file.absolutePath) { db ->
                assertEquals("Preserved title", db.getFeedDao().getFeed("subscribed").title)
                assertEquals(true, db.getFeedUserStateDao().getState(LocalUUID("subscribed"))!!.pinned)
                assertEquals(listOf("feed-1"), search(db, "\"Kotlin\"").getInitialPage(10).map { it.feed.metadata.id.value })
                assertTrue(db.getFeedDiscoveryFeedMetadataDao().observeFeed(LocalUUID("feed-1")).first()!!.subscribed)
            }
            BundledSQLiteDriver().open(file.absolutePath).use { connection ->
                connection.prepare("PRAGMA user_version").use { statement ->
                    assertTrue(statement.step())
                    assertEquals(1L, statement.getLong(0))
                }
                connection.prepare("PRAGMA foreign_key_check").use { statement -> assertFalse(statement.step()) }
            }
        } finally {
            file.delete()
        }
    }

    @Test
    fun `Given tied positions and ranks When paging Then append prepend and refresh preserve stable order without duplicates`() = runBlocking {
        withDatabase { db ->
            seed(db, count = 6)
            db.getFeedDiscoveryCollectionToFeedMappingDao().upsert((1..6).map {
                LocalFeedDiscoveryCollectionToFeedMapping(LocalUUID("collection"), LocalUUID("feed-$it"), (it - 1) / 2)
            })
            val collection = LocalFeedDiscoveryPagingDataProvider(db.getFeedDiscoveryFeedMetadataDao(), LocalFeedDiscoveryQuery.Collection(LocalUUID("collection")))
            val first = collection.getInitialPage(2)
            assertEquals(listOf("feed-1", "feed-2"), first.map { it.feed.metadata.id.value })
            val second = collection.getPageAfter(collection.getId(first.last()), 2)
            assertEquals(listOf("feed-3", "feed-4"), second.map { it.feed.metadata.id.value })
            assertEquals(listOf("feed-1", "feed-2"), collection.getPageBefore(collection.getId(second.first()), 2).map { it.feed.metadata.id.value })
            assertEquals(listOf("feed-2", "feed-3", "feed-4", "feed-5"), collection.getRefreshPageAround(collection.getId(second.last()), 4).map { it.feed.metadata.id.value })
            assertTrue(collection.getPageBefore(collection.getId(first.first()), 2).isEmpty())
            val collectionEnd = collection.getPageAfter(collection.getId(second.last()), 2)
            assertTrue(collection.getPageAfter(collection.getId(collectionEnd.last()), 2).isEmpty())
            val collectionAnchor = collection.getId(second.last())
            db.getFeedDiscoveryCollectionToFeedMappingDao().upsert(
                LocalFeedDiscoveryCollectionToFeedMapping(LocalUUID("collection"), LocalUUID("feed-4"), 0)
            )
            assertEquals(listOf("feed-1", "feed-2", "feed-4", "feed-3"), collection.getRefreshPageAround(collectionAnchor, 4).map { it.feed.metadata.id.value })
            val search = search(db, "\"News\"")
            val all = search.getInitialPage(20)
            assertEquals(listOf("feed-1", "feed-2", "feed-3", "feed-4", "feed-5", "feed-6"), all.map { it.feed.metadata.id.value })
            val page = search.getInitialPage(2)
            val next = search.getPageAfter(search.getId(page.last()), 2)
            assertEquals(listOf("feed-3", "feed-4"), next.map { it.feed.metadata.id.value })
            assertEquals(listOf("feed-1", "feed-2"), search.getPageBefore(search.getId(next.first()), 2).map { it.feed.metadata.id.value })
            assertEquals(listOf("feed-2", "feed-3", "feed-4", "feed-5"), search.getRefreshPageAround(search.getId(next.last()), 4).map { it.feed.metadata.id.value })
            assertTrue(search.getPageBefore(search.getId(page.first()), 2).isEmpty())
            assertTrue(search.getPageAfter(search.getId(all.last()), 2).isEmpty())
            assertEquals("${HL_START}News${HL_END}", (page.first() as LocalFeedDiscoveryFeedPage.Search).search?.highlightedTitle)
            assertNull((page.first() as LocalFeedDiscoveryFeedPage.Search).search?.highlightedUrl)
            assertNull((page.first() as LocalFeedDiscoveryFeedPage.Search).search?.highlightedDescription)
            db.getFeedDiscoveryFeedMetadataDao().upsert(feed("feed-4", "News News News"))
            val reordered = search.getInitialPage(20)
            assertEquals(listOf("feed-4", "feed-1", "feed-2", "feed-3", "feed-5", "feed-6"), reordered.map { it.feed.metadata.id.value })
            assertEquals(2, reordered.map { (it as LocalFeedDiscoveryFeedPage.Search).rank }.distinct().size)
            val rankedFirst = search.getInitialPage(2)
            assertEquals(listOf("feed-2", "feed-3"), search.getPageAfter(search.getId(rankedFirst.last()), 2).map { it.feed.metadata.id.value })
            assertEquals(listOf("feed-4", "feed-1", "feed-2", "feed-3"), search.getRefreshPageAround(search.getId(next.last()), 4).map { it.feed.metadata.id.value })
        }
    }

    @Test
    fun `Given shared orphan and empty collection data When observing Then shelves preserve empties and search excludes orphans`() = runBlocking {
        withDatabase { db ->
            seed(db, count = 3)
            db.getFeedDiscoveryCollectionDao().upsert(collection("second", 1, 1))
            db.getFeedDiscoveryCollectionDao().upsert(collection("empty", 2))
            db.getFeedDiscoveryCollectionToFeedMappingDao().upsert(LocalFeedDiscoveryCollectionToFeedMapping(LocalUUID("second"), LocalUUID("feed-1"), 0))
            db.getFeedDiscoveryFeedMetadataDao().upsert(feed("orphan", "News"))
            assertEquals(listOf("feed-1", "feed-2", "feed-3"), search(db, "\"News\"").getInitialPage(10).map { it.feed.metadata.id.value })
            assertEquals(listOf("feed-1"), search(db, "\"News\"", "second").getInitialPage(10).map { it.feed.metadata.id.value })
            assertNull(db.getFeedDiscoveryFeedMetadataDao().observeFeed(LocalUUID("orphan")).first())
            val shelves = db.getFeedDiscoveryCollectionDao().observeCollectionShelves(1).first()
            assertEquals(listOf("collection", "second", "empty"), shelves.map { it.collection.id.value })
            assertEquals(listOf("feed-1", "feed-1", null), shelves.map { it.feed?.metadata?.id?.value })
            db.getFeedDiscoveryFeedMetadataDao().upsert(feed("feed-2", "Compose"))
            assertEquals(listOf("feed-1", "feed-3"), search(db, "\"News\"").getInitialPage(10).map { it.feed.metadata.id.value })
            assertEquals(listOf("feed-2"), search(db, "\"Compose\"").getInitialPage(10).map { it.feed.metadata.id.value })
        }
    }

    @Test
    fun `Given active discovery pages When import status or membership changes Then pages invalidate and projections update`() = runBlocking {
        withDatabase { db ->
            seed(db)
            val source = db.getFeedDiscoveryPagingSource(LocalFeedDiscoveryQuery.Search("\"Kotlin\""))
            val invalidated = CompletableDeferred<Unit>()
            source.registerInvalidatedCallback { invalidated.complete(Unit) }
            val initial = source.load(PagingSource.LoadParams.Refresh(null, 10, false))
            assertEquals(listOf("feed-1"), (initial as PagingSource.LoadResult.Page).data.map { it.feed.metadata.id.value })
            db.getFeedQueueDao().upsert(queue(LocalRssFeedQueueActionType.IMPORT, LocalRssFeedQueueStatus.QUEUED))
            withTimeout(5_000) { invalidated.await() }
            db.getFeedQueueDao().upsert(queue(LocalRssFeedQueueActionType.SYNC, LocalRssFeedQueueStatus.FAILED, id = 2))
            assertEquals(LocalRssFeedQueueStatus.QUEUED, db.getFeedDiscoveryFeedMetadataDao().observeFeed(LocalUUID("feed-1")).first()!!.importStatus)
            val subscriptionSource = db.getFeedDiscoveryPagingSource(LocalFeedDiscoveryQuery.Search("\"Kotlin\""))
            val subscribed = CompletableDeferred<Unit>()
            subscriptionSource.registerInvalidatedCallback { subscribed.complete(Unit) }
            subscriptionSource.load(PagingSource.LoadParams.Refresh(null, 10, false))
            db.getFeedDao().upsertFeed(LocalRssFeed(
                id = LocalUUID("subscribed"), sourceUrl = "https://example.com/1",
                title = "Subscribed feed", name = "", link = null, description = null, image = null,
                lastBuildDate = null, updatePeriod = null, youtubeChannel = null, createdAt = 1,
                updatedAt = 1,
            ))
            withTimeout(5_000) { subscribed.await() }
            assertTrue(db.getFeedDiscoveryFeedMetadataDao().observeFeed(LocalUUID("feed-1")).first()!!.subscribed)
            val collectionSource = db.getFeedDiscoveryPagingSource(LocalFeedDiscoveryQuery.Collection(LocalUUID("collection")))
            val removed = CompletableDeferred<Unit>()
            collectionSource.registerInvalidatedCallback { removed.complete(Unit) }
            assertEquals(1, (collectionSource.load(PagingSource.LoadParams.Refresh(null, 10, false)) as PagingSource.LoadResult.Page).data.size)
            db.getFeedDiscoveryCollectionToFeedMappingDao().delete(listOf("collection"))
            withTimeout(5_000) { removed.await() }
            assertNull(db.getFeedDiscoveryFeedMetadataDao().observeFeed(LocalUUID("feed-1")).first())
        }
    }

    private fun search(db: DevPulseDatabase, query: String, collectionId: String? = null) =
        LocalFeedDiscoveryPagingDataProvider(db.getFeedDiscoveryFeedMetadataDao(), LocalFeedDiscoveryQuery.Search(query, collectionId?.let(::LocalUUID)))

    private suspend fun withDatabase(path: String? = null, block: suspend (DevPulseDatabase) -> Unit) {
        val database = open(path)
        try {
            block(database)
        } finally {
            database.close()
        }
    }

    private fun open(path: String? = null): DevPulseDatabase {
        val builder = if (path == null) Room.inMemoryDatabaseBuilder<DevPulseDatabase>() else Room.databaseBuilder<DevPulseDatabase>(path)
        return builder.setDriver(BundledSQLiteDriver())
            .addColumnTypeConverter(LocalCompressedTextTypeConverter(GzipCompressor()))
            .build()
    }

    private suspend fun seed(db: DevPulseDatabase, count: Int = 1) {
        db.getFeedDiscoveryMasterMetadataDao().upsert(LocalFeedDiscoveryMasterMetadata(LocalUUID("catalog"), count, 1, 1, count))
        db.getFeedDiscoveryCollectionDao().upsert(collection("collection", 0, count))
        db.getFeedDiscoveryFeedMetadataDao().upsert((1..count).map { feed("feed-$it", if (count == 1) "Kotlin News" else "News") })
        db.getFeedDiscoveryCollectionToFeedMappingDao().upsert((1..count).map {
            LocalFeedDiscoveryCollectionToFeedMapping(LocalUUID("collection"), LocalUUID("feed-$it"), it - 1)
        })
    }

    private fun collection(id: String, position: Int, count: Int = 0) = LocalFeedDiscoveryCollection(
        id = LocalUUID(id), catalogId = LocalUUID("catalog"), path = "path", feedCount = count,
        sha256Hash = "hash", slug = id, title = id, position = position, description = "Description",
    )

    private fun feed(id: String, title: String) = LocalFeedDiscoveryFeedMetadata(
        id = LocalUUID(id), title = title, description = null, feedFormat = LocalFeedDiscoveryFeedFormat.RSS,
        url = "https://example.com/${id.removePrefix("feed-")}", iconUrl = null, languageTag = null, websiteUrl = null,
    )

    private fun queue(action: LocalRssFeedQueueActionType, status: LocalRssFeedQueueStatus, id: Int = 1) = LocalRssFeedQueue(
        id = id, url = "https://example.com/1", name = null, feedType = LocalRssFeedType.CONTENT,
        actionType = action, requestor = LocalRssFeedQueueActionRequestor.USER, status = status,
        fetchAttempt = 0, tagIds = null, folderIds = null, createAt = 1, updatedAt = 1,
    )

    private fun SQLiteConnection.execute(sql: String) {
        prepare(sql).use { it.step() }
    }
}
