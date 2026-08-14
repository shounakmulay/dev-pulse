package dev.shounakmulay.devpulse.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import dev.shounakmulay.devpulse.core.ui.content.LocalContentTextSettings
import dev.shounakmulay.devpulse.feature.settings.controllers.contentText.ContentTextSettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DevPulseContextProviders(content: @Composable () -> Unit) {
    val contentTextSettingsViewModel = koinViewModel<ContentTextSettingsViewModel>()
    CompositionLocalProvider(
        LocalContentTextSettings provides contentTextSettingsViewModel.state
    ) {
        content()
    }
}