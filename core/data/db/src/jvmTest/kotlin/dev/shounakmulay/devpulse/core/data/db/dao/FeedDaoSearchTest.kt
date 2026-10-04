package dev.shounakmulay.devpulse.core.data.db.dao

import androidx.paging.PagingSource
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.shounakmulay.devpulse.core.common.text.HL_END
import dev.shounakmulay.devpulse.core.common.text.HL_START
import dev.shounakmulay.devpulse.core.data.db.DevPulseDatabase
import dev.shounakmulay.devpulse.core.data.db.converter.GzipCompressor
import dev.shounakmulay.devpulse.core.data.db.converter.LocalCompressedTextTypeConverter
import dev.shounakmulay.devpulse.core.data.db.model.core.LocalUUID
import dev.shounakmulay.devpulse.core.data.db.model.feed.projection.LocalRssFeedWithSearch
import dev.shounakmulay.devpulse.core.data.db.model.feed.tables.LocalRssFeed
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class FeedDaoSearchTest {
    @Test
    fun `Given feeds When browsing all and pinned Then normal order and absent highlights are preserved`() = withDatabase { dao ->
        dao.upsertFeeds(
            listOf(
                feed("c", name = "Kotlin"),
                feed("a", name = "Android", pinned = true),
                feed("b", pinned = true),
            )
        )

        val all = dao.getFeedPagingSource(pinnedOnly = false).firstPage()
        val pinned = dao.getFeedPagingSource(pinnedOnly = true).firstPage()

        assertEquals(listOf("b", "a", "c"), all.data.map { it.feed.id.value })
        assertEquals(listOf("b", "a"), pinned.data.map { it.feed.id.value })
        all.data.forEach {
            assertNull(it.highlightedTitle)
            assertNull(it.highlightedName)
            assertNull(it.highlightedDescription)
        }
    }

    @Test
    fun `Given matching feeds When searching Then highlights and pinned changes use same feed rows`() = withDatabase { dao ->
        dao.upsertFeeds(
            listOf(
                feed("a", name = "Kotlin Weekly", pinned = true),
                feed("b", title = "Kotlin news", pinned = true),
                feed("c", description = "Learn Kotlin"),
            )
        )

        val results = dao.searchFeeds(query = "\"Kotlin\"", pinnedOnly = false).firstPage().data
        assertEquals(setOf("a", "b", "c"), results.map { it.feed.id.value }.toSet())
        assertEquals(
            "${HL_START}Kotlin${HL_END} Weekly",
            results.single { it.feed.id.value == "a" }.highlightedName,
        )
        assertEquals(
            "${HL_START}Kotlin${HL_END} news",
            results.single { it.feed.id.value == "b" }.highlightedTitle,
        )
        assertEquals(
            "Learn ${HL_START}Kotlin${HL_END}",
            results.single { it.feed.id.value == "c" }.highlightedDescription,
        )

        dao.setFeedPinned(id = LocalUUID("a"), pinned = false)
        val pinned = dao.searchFeeds(query = "\"Kotlin\"", pinnedOnly = true).firstPage().data
        assertEquals(listOf("b"), pinned.map { it.feed.id.value })
        assertEquals(emptyList(), dao.searchFeeds(query = "\"missing\"", pinnedOnly = false).firstPage().data)
        dao.deleteFeeds(listOf(LocalUUID("b")))
        val remaining = dao.searchFeeds(query = "\"Kotlin\"", pinnedOnly = false).firstPage().data
        assertEquals(setOf("a", "c"), remaining.map { it.feed.id.value }.toSet())
    }

    @Test
    fun `Given more than one page of tied matches When appending Then every feed appears once in stable order`() = withDatabase { dao ->
        dao.upsertFeeds(
            (0 until 28).map {
                feed(id = "feed-${it.toString().padStart(2, '0')}", title = "Kotlin news")
            }
        )
        val source = dao.searchFeeds(query = "\"Kotlin\"", pinnedOnly = false)
        val first = source.firstPage(loadSize = 10)
        val second = assertIs<PagingSource.LoadResult.Page<Int, LocalRssFeedWithSearch>>(
            source.load(
                PagingSource.LoadParams.Append(
                    key = requireNotNull(first.nextKey),
                    loadSize = 10,
                    placeholdersEnabled = false,
                )
            )
        )
        val third = assertIs<PagingSource.LoadResult.Page<Int, LocalRssFeedWithSearch>>(
            source.load(
                PagingSource.LoadParams.Append(
                    key = requireNotNull(second.nextKey),
                    loadSize = 10,
                    placeholdersEnabled = false,
                )
            )
        )

        assertEquals(
            (0 until 28).map { "feed-${it.toString().padStart(2, '0')}" },
            (first.data + second.data + third.data).map { it.feed.id.value },
        )
        assertNull(third.nextKey)
    }

    private suspend fun PagingSource<Int, LocalRssFeedWithSearch>.firstPage(loadSize: Int = 30) =
        assertIs<PagingSource.LoadResult.Page<Int, LocalRssFeedWithSearch>>(
            load(PagingSource.LoadParams.Refresh(key = null, loadSize = loadSize, placeholdersEnabled = false))
        )

    private fun withDatabase(block: suspend (FeedDao) -> Unit) = runBlocking {
        val directory = Files.createTempDirectory("feed-search-test")
        val database = Room.databaseBuilder<DevPulseDatabase>(name = directory.resolve("test.db").toString())
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .addColumnTypeConverter(LocalCompressedTextTypeConverter(GzipCompressor()))
            .build()
        try {
            block(database.getFeedDao())
        } finally {
            database.close()
            directory.toFile().deleteRecursively()
        }
    }

    private fun feed(
        id: String,
        name: String = "",
        title: String = "",
        description: String = "",
        pinned: Boolean = false,
    ) = LocalRssFeed(
        id = LocalUUID(id),
        pinned = pinned,
        sourceUrl = "https://example.com/$id",
        title = title,
        name = name,
        description = description,
        link = null,
        image = null,
        lastBuildDate = null,
        updatePeriod = null,
        youtubeChannel = null,
        createdAt = 1,
        updatedAt = 1,
        lastOpenedAt = null,
    )
}
