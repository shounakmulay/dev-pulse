package dev.shounakmulay.devpulse.feature.settings.screens.articleSettings.ui

import dev.shounakmulay.devpulse.core.domain.models.contentSettings.BrowserOpeningMode
import dev.shounakmulay.devpulse.core.ui.event.ScreenEvent

sealed interface ArticleSettingsEvent : ScreenEvent {
    data class SetBrowserOpeningMode(val mode: BrowserOpeningMode) : ArticleSettingsEvent
    data class SetContentTextScale(val scale: Float) : ArticleSettingsEvent
    data class SetContentLineHeightScale(val scale: Float) : ArticleSettingsEvent
}
