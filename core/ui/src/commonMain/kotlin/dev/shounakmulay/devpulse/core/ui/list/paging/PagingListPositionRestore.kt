package dev.shounakmulay.devpulse.core.ui.list.paging

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.ItemSnapshotList
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.math.abs

@Serializable
private data class ReturnAnchor(
    val key: String,
    val offset: Int,
    val indexDelta: Int,
    val queryKey: String,
)

private val AnchorSaver = Saver<ReturnAnchor?, String>(
    save = { Json.encodeToString(it) },
    restore = { Json.decodeFromString<ReturnAnchor?>(it) },
)

private data class VisibleItem(
    val index: Int,
    val offset: Int,
)

private interface PagingViewport {
    val interactionSource: InteractionSource
    val itemCount: Int

    fun findVisibleItem(key: String): VisibleItem?
    suspend fun scrollTo(index: Int, offset: Int)
}

private class GridViewport(
    private val state: LazyGridState,
) : PagingViewport {

    override val interactionSource
        get() = state.interactionSource

    override val itemCount
        get() = state.layoutInfo.totalItemsCount

    override fun findVisibleItem(key: String): VisibleItem? {
        val item = state.layoutInfo.visibleItemsInfo
            .firstOrNull { it.key == key }
            ?: return null

        return VisibleItem(
            index = item.index,
            offset = item.offset.y,
        )
    }

    override suspend fun scrollTo(index: Int, offset: Int) {
        state.scrollToItem(index, offset)
    }
}

private class ListViewport(
    private val state: LazyListState,
) : PagingViewport {

    override val interactionSource
        get() = state.interactionSource

    override val itemCount
        get() = state.layoutInfo.totalItemsCount

    override fun findVisibleItem(key: String): VisibleItem? {
        val item = state.layoutInfo.visibleItemsInfo
            .firstOrNull { it.key == key }
            ?: return null

        return VisibleItem(
            index = item.index,
            offset = item.offset,
        )
    }

    override suspend fun scrollTo(index: Int, offset: Int) {
        state.scrollToItem(index, offset)
    }
}

private fun <T : Any> LazyPagingItems<T>.findLoadedIndex(
    key: String,
    keyOf: (T) -> String,
): Int? {
    val snapshot = itemSnapshotList

    val index = snapshot.items.indexOfFirst {
        keyOf(it) == key
    }

    return index.takeIf { it >= 0 }?.let {
        snapshot.placeholdersBefore + it
    }
}

@Composable
private fun <T : Any> rememberPagingReturnPositionImpl(
    items: LazyPagingItems<T>,
    viewport: PagingViewport,
    keyOf: (T) -> String,
    queryKey: String?,
): PagingListPositionRestoreController {

    var anchor by rememberSaveable(stateSaver = AnchorSaver) {
        mutableStateOf<ReturnAnchor?>(null)
    }

    var capturedSnapshot by remember { mutableStateOf<ItemSnapshotList<T>?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateAsState()

    val currentQuery by rememberUpdatedState(queryKey)
    val currentKeyOf by rememberUpdatedState(keyOf)

    LaunchedEffect(lifecycleOwner, items, viewport, anchor) {
        val saved = anchor ?: return@LaunchedEffect
        launch {
            viewport.interactionSource.interactions
                .filterIsInstance<DragInteraction.Start>()
                .first()
            if (anchor == saved) anchor = null
        }
        snapshotFlow {
            currentQuery != saved.queryKey ||
                capturedSnapshot !== items.itemSnapshotList ||
                !lifecycleState.isAtLeast(Lifecycle.State.STARTED)
        }.first { it }

        lifecycleOwner.lifecycle.repeatOnLifecycle(
            Lifecycle.State.STARTED
        ) {
            val restoration = launch {
                val targetIndex = snapshotFlow {
                    when {
                        currentQuery == null -> null
                        currentQuery != saved.queryKey -> -1
                        items.loadState.refresh !is LoadState.NotLoading -> null
                        else -> {
                            val index = items.findLoadedIndex(
                                key = saved.key,
                                keyOf = currentKeyOf,
                            )?.plus(saved.indexDelta)
                            when {
                                index == null -> -1
                                viewport.itemCount <= index -> null
                                else -> index
                            }
                        }
                    }
                }.filterNotNull().first()

                if (targetIndex < 0) {
                    if (anchor == saved) anchor = null
                    return@launch
                }

                if (anchor != saved || currentQuery != saved.queryKey) return@launch

                val visible = viewport.findVisibleItem(saved.key)

                if (
                    visible == null ||
                    visible.index != targetIndex ||
                    abs(visible.offset - saved.offset) > 2
                ) {
                    viewport.scrollTo(
                        index = targetIndex,
                        offset = -saved.offset,
                    )
                }

                if (anchor == saved) {
                    anchor = null
                }
            }

            restoration.join()
        }
    }

    return PagingListPositionRestoreController { key ->
        val visible = viewport.findVisibleItem(key)
        val pagingIndex = items.findLoadedIndex(key, keyOf)

        capturedSnapshot = items.itemSnapshotList
        anchor = if (
            visible != null &&
            pagingIndex != null &&
            queryKey != null
        ) {
            ReturnAnchor(
                key = key,
                offset = visible.offset,
                indexDelta = visible.index - pagingIndex,
                queryKey = queryKey,
            )
        } else {
            null
        }
    }
}

data class PagingListPositionRestoreController(
    val setItemToRestore: (String) -> Unit,
)

@Composable
fun <T : Any> rememberPagingReturnPosition(
    items: LazyPagingItems<T>,
    gridState: LazyGridState,
    keyOf: (T) -> String,
    queryKey: String? = "",
): PagingListPositionRestoreController {
    val viewport = remember(gridState) {
        GridViewport(gridState)
    }

    return rememberPagingReturnPositionImpl(
        items = items,
        viewport = viewport,
        keyOf = keyOf,
        queryKey = queryKey,
    )
}

@Composable
fun <T : Any> rememberPagingReturnPosition(
    items: LazyPagingItems<T>,
    listState: LazyListState,
    keyOf: (T) -> String,
    queryKey: String? = "",
): PagingListPositionRestoreController {
    val viewport = remember(listState) {
        ListViewport(listState)
    }

    return rememberPagingReturnPositionImpl(
        items = items,
        viewport = viewport,
        keyOf = keyOf,
        queryKey = queryKey,
    )
}
