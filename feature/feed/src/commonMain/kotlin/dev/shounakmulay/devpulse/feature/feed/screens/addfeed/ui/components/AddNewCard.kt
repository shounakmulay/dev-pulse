package dev.shounakmulay.devpulse.feature.feed.screens.addfeed.ui.components

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope

fun LazyGridScope.addNewCard(onClick: () -> Unit) {
    item(key = "AddNewCard", span = { GridItemSpan(maxLineSpan) }) {
        AddNewCardButton(onClick)
    }
}