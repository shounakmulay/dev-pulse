package dev.shounakmulay.devpulse.feature.settings.screens.articleSettings.ui

import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable

@Serializable
data class ArticleSettingsState(val isLoading: Boolean = false) : ScreenState
