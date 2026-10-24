package dev.shounakmulay.devpulse.core.data.db.paging

import androidx.paging.PagingSource
import dev.shounakmulay.devpulse.core.logging.log.DPLog
import dev.shounakmulay.devpulse.core.logging.log.DPLogConfigs
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LocalCursorPagingSourceTest {

    @Test
    fun `Given no key When refresh loads initial page Then returns first page with next cursor`() = runBlocking {
        val pagingSource = createPagingSource(items = listOf(1, 2, 3, 4))

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        val page = assertIs<PagingSource.LoadResult.Page<Int, TestItem>>(result)
        assertEquals(listOf(TestItem(1), TestItem(2)), page.data)
        assertNull(page.prevKey)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun `Given append key When append loads after cursor Then returns next items`() = runBlocking {
        val pagingSource = createPagingSource(items = listOf(1, 2, 3, 4))

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 2,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        val page = assertIs<PagingSource.LoadResult.Page<Int, TestItem>>(result)
        assertEquals(listOf(TestItem(3), TestItem(4)), page.data)
        assertEquals(4, page.nextKey)
    }

    @Test
    fun `Given short append page When load completes Then next key is null`() = runBlocking {
        val pagingSource = createPagingSource(items = listOf(1, 2, 3))

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 2,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        val page = assertIs<PagingSource.LoadResult.Page<Int, TestItem>>(result)
        assertEquals(listOf(TestItem(3)), page.data)
        assertNull(page.nextKey)
    }

    @Test
    fun `Given anchor key When refresh loads around anchor Then anchor item remains in page`() = runBlocking {
        val pagingSource = createPagingSource(items = listOf(1, 2, 3, 4))

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 3,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        val page = assertIs<PagingSource.LoadResult.Page<Int, TestItem>>(result)
        assertEquals(listOf(TestItem(3), TestItem(4)), page.data)
        assertEquals(4, page.nextKey)
    }

    @Test
    fun `Given provider error When load fails Then returns load error`() = runBlocking {
        DPLog.configure(DPLogConfigs.debug())
        val pagingSource = createPagingSource(error = IllegalStateException("failed"))

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 2,
                placeholdersEnabled = false
            )
        )

        val error = assertIs<PagingSource.LoadResult.Error<Int, TestItem>>(result)
        assertEquals("failed", error.throwable.message)
    }

    @Test
    fun `Given provider cancellation When load is canceled Then cancellation is rethrown`() = runBlocking {
        val pagingSource = createPagingSource(error = CancellationException("canceled"))

        val error = assertFailsWith<CancellationException> {
            pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        }
        assertEquals("canceled", error.message)
    }

    @Test
    fun `Given invalidation flow When event emits Then source invalidates`() = runBlocking {
        val invalidationEvents = MutableSharedFlow<Unit>()
        val pagingSource = createPagingSource(
            items = listOf(1, 2, 3),
            invalidationEvents = invalidationEvents
        )

        val invalidated = CompletableDeferred<Unit>()
        pagingSource.registerInvalidatedCallback { invalidated.complete(Unit) }
        try {
            withTimeout(5_000) {
                invalidationEvents.subscriptionCount.first { it > 0 }
                assertFalse(pagingSource.invalid)
                invalidationEvents.emit(Unit)
                invalidated.await()
            }
            assertTrue(pagingSource.invalid)
        } finally {
            pagingSource.invalidate()
        }
    }

    @Test
    fun `Given source invalidates When invalidated Then invalidation collection stops`() = runBlocking {
        val invalidationEvents = MutableSharedFlow<Unit>()
        val pagingSource = createPagingSource(
            items = listOf(1, 2, 3),
            invalidationEvents = invalidationEvents
        )

        try {
            withTimeout(5_000) {
                invalidationEvents.subscriptionCount.first { it > 0 }
                pagingSource.invalidate()
                invalidationEvents.subscriptionCount.first { it == 0 }
            }
            assertEquals(0, invalidationEvents.subscriptionCount.value)
        } finally {
            pagingSource.invalidate()
        }
    }

    private fun createPagingSource(
        items: List<Int> = emptyList(),
        error: Exception? = null,
        invalidationEvents: Flow<Unit> = emptyFlow()
    ): LocalCursorPagingSource<Int, TestItem> {
        return LocalCursorPagingSource(
            dataProvider = FakeDataProvider(
                items = items.map(::TestItem),
                error = error
            ),
            invalidationEvents = invalidationEvents
        )
    }

    data class TestItem(val id: Int)

    private class FakeDataProvider(
        private val items: List<TestItem>,
        private val error: Exception?
    ) : LocalCursorPagingSourceDataProvider<Int, TestItem> {

        override fun getTablesToTrack(): List<String> {
            return listOf("TestItem")
        }

        override suspend fun getInitialPage(loadSize: Int): List<TestItem> {
            error?.let { throw it }
            return items.take(loadSize)
        }

        override suspend fun getPageAfter(cursor: Int, loadSize: Int): List<TestItem> {
            error?.let { throw it }
            return items.filter { it.id > cursor }.take(loadSize)
        }

        override suspend fun getRefreshPageAround(anchorCursor: Int, loadSize: Int): List<TestItem> {
            error?.let { throw it }
            return items.filter { it.id >= anchorCursor }.take(loadSize)
        }

        override fun getId(item: TestItem): Int {
            return item.id
        }
    }
}
