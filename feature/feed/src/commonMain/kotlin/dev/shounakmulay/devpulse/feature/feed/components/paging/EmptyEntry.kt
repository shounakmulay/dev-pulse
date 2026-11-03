package dev.shounakmulay.devpulse.feature.feed.components.paging

import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.insertSeparators
import androidx.paging.map

internal fun <T : Any, R : Any> PagingData<T>.withEmptyEntry(wrap: (T) -> R, empty: R): PagingData<R> = map(wrap).insertSeparators { before, after -> if (before == null && after == null) empty else null }

internal val LazyPagingItems<*>.showEmptyEntry: Boolean
    get() = loadState.refresh is LoadState.NotLoading
