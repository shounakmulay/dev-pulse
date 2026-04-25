package dev.shounakmulay.devpulse.feature.home.screens.home.ui

import androidx.compose.runtime.Immutable
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class HomeScreenState(
    val isLoading: Boolean,
    val count: Int = 0,
) : ScreenState
