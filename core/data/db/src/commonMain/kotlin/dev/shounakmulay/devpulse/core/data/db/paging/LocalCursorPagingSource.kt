package dev.shounakmulay.devpulse.core.data.db.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room3.RoomDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class LocalCursorPagingSource<KEY : Any, VALUE : Any> internal constructor(
    private val dataProvider: LocalCursorPagingSourceDataProvider<KEY, VALUE>,
    invalidationEvents: Flow<Unit>,
    invalidationScope: CoroutineScope
) : PagingSource<KEY, VALUE>() {

    constructor(
        database: RoomDatabase,
        dataProvider: LocalCursorPagingSourceDataProvider<KEY, VALUE>
    ) : this(
        dataProvider = dataProvider,
        invalidationEvents = database.createInvalidationFlow(dataProvider),
        invalidationScope = database.getCoroutineScope()
    )

    private val invalidationJob: Job = invalidationEvents
        .onEach { invalidate() }
        .launchIn(invalidationScope)

    init {
        registerInvalidatedCallback {
            invalidationJob.cancel()
        }
    }

    override suspend fun load(params: LoadParams<KEY>): LoadResult<KEY, VALUE> {
        return try {
            if (invalid) {
                return LoadResult.Invalid()
            }

            val items = when (params) {
                is LoadParams.Refresh -> {
                    val refreshCursor = params.key
                    if (refreshCursor == null) {
                        dataProvider.getInitialPage(params.loadSize)
                    } else {
                        dataProvider.getRefreshPageAround(refreshCursor, params.loadSize)
                    }
                }
                is LoadParams.Append -> dataProvider.getPageAfter(params.key, params.loadSize)
                is LoadParams.Prepend -> emptyList()
            }

            if (invalid) {
                return LoadResult.Invalid()
            }

            LoadResult.Page(
                data = items,
                prevKey = null,
                nextKey = when {
                    params is LoadParams.Prepend -> null
                    items.size < params.loadSize -> null
                    else -> items.lastOrNull()?.let { dataProvider.getId(it) }
                }
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<KEY, VALUE>): KEY? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.let { dataProvider.getId(it) }
        }
    }
}

private fun <KEY, VALUE> RoomDatabase.createInvalidationFlow(
    dataProvider: LocalCursorPagingSourceDataProvider<KEY, VALUE>
): Flow<Unit> {
    val tables = dataProvider.getTablesToTrack()
    require(tables.isNotEmpty()) {
        "LocalCursorPagingSource requires at least one table to track"
    }
    return invalidationTracker
        .createFlow(*tables.toTypedArray(), emitInitialState = false)
        .map { Unit }
}
