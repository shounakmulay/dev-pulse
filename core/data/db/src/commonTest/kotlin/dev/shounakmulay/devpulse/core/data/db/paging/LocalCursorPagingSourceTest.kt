package dev.shounakmulay.devpulse.core.data.db.paging

import androidx.paging.PagingSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
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
            invalidationEvents = invalidationEvents,
            invalidationScope = this
        )

        while (invalidationEvents.subscriptionCount.value == 0) {
            yield()
        }
        assertFalse(pagingSource.invalid)
        invalidationEvents.emit(Unit)
        yield()

        assertTrue(pagingSource.invalid)
    }

    @Test
    fun `Given source invalidates When invalidated Then invalidation collection stops`() = runBlocking {
        val invalidationEvents = MutableSharedFlow<Unit>()
        val pagingSource = createPagingSource(
            items = listOf(1, 2, 3),
            invalidationEvents = invalidationEvents,
            invalidationScope = this
        )

        while (invalidationEvents.subscriptionCount.value == 0) {
            yield()
        }
        pagingSource.invalidate()
        while (invalidationEvents.subscriptionCount.value > 0) {
            yield()
        }

        assertEquals(0, invalidationEvents.subscriptionCount.value)
    }

    private fun createPagingSource(
        items: List<Int> = emptyList(),
        error: Exception? = null,
        invalidationEvents: MutableSharedFlow<Unit> = MutableSharedFlow(),
        invalidationScope: CoroutineScope = CoroutineScope(kotlin.coroutines.EmptyCoroutineContext)
    ): LocalCursorPagingSource<Int, TestItem> {
        return LocalCursorPagingSource(
            dataProvider = FakeDataProvider(
                items = items.map(::TestItem),
                error = error
            ),
            invalidationEvents = invalidationEvents,
            invalidationScope = invalidationScope
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
