package dev.shounakmulay.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider

@Composable
fun NavDisplay(
    modifier: Modifier,
    navigationState: NavigationState,
    navigator: Navigator,
    entryProvider: EntryProviderScope<Screen>.() -> Unit
) {
    androidx.navigation3.ui.NavDisplay(
        modifier = modifier,
        entries = navigationState.toEntries(entryProvider = entryProvider {
           entryProvider()
        }),
        onBack = { navigator.navigateBack() }
    )
}