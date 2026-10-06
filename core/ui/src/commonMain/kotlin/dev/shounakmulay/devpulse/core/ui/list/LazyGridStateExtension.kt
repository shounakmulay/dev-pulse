package dev.shounakmulay.devpulse.core.ui.list

import androidx.compose.foundation.lazy.grid.LazyGridState

fun LazyGridState.isAtTop(): Boolean {
    return firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0
}