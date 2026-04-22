package dev.shounakmulay.feature.settings.screens.settings.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.shounakmulay.core.designsystem.components.DPTextView
import dev.shounakmulay.core.designsystem.components.DPTextViewVariant

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.collectAsState()
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        DPTextView(text = "Settings", variant = DPTextViewVariant.HeadingLarge)
    }
}
