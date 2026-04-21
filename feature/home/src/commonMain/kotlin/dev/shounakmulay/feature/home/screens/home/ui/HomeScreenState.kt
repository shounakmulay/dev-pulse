package dev.shounakmulay.feature.home.screens.home.ui

import dev.shounakmulay.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable

@Serializable
data class HomeScreenState(
    val isLoading: Boolean,
    val count: Int = 0,
) : ScreenState
