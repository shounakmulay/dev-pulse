package dev.shounakmulay.devpulse.feature.settings.screens.articleSettings.ui

import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningMode
import dev.shounakmulay.devpulse.core.ui.screen.ScreenState
import kotlinx.serialization.Serializable

@Serializable
data class ArticleSettingsState(
    val isLoading: Boolean = true,
    val browserOpeningMode: BrowserOpeningMode = BrowserOpeningMode.IN_APP_BROWSER,
    val isSavingBrowserOpeningMode: Boolean = false,
) : ScreenState
