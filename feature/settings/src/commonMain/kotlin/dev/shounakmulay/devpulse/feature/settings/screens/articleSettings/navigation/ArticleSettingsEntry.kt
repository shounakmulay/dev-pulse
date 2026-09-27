package dev.shounakmulay.devpulse.feature.settings.screens.articleSettings.navigation

import androidx.navigation3.runtime.EntryProviderScope
import dev.shounakmulay.devpulse.core.navigation.Navigator
import dev.shounakmulay.devpulse.core.navigation.Screen
import dev.shounakmulay.devpulse.feature.settings.screens.articleSettings.ui.ArticleSettingsScreen
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<Screen>.articleSettingsEntry(navigator: Navigator) {
    entry<Screen.Settings.ArticleSettings> {
        ArticleSettingsScreen(viewModel = koinViewModel(), navigator = navigator)
    }
}