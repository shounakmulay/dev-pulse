package dev.shounakmulay.devpulse.feature.settings.screens.settings.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class SettingsScreenState(
    val isLoading: Boolean = false,
) : ScreenState
